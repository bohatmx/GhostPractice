package com.boha.ghostpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.boha.ghostpractice.data.FeeDTO;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MobileTariffCodeDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpractice.util.SharedUtil;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.Util;
import com.boha.ghostpractice.util.bean.CommsException;
import com.boha.ghostpracticephone.R;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostFeeActivity extends ActionBarActivity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_fee);
		setFields();

		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(getApplicationContext(), bar,
					SharedUtil.getPracticeName(getApplicationContext()));
		}
		new TariffTask().execute();
	}

	static final int DURATION_JUST_FOR_QUERY = 100;

	class TariffTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableTariffSpinner();
			start = System.currentTimeMillis();
			disableButtons();
			timeToggle.setEnabled(false);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			 resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.GET_TARIFF_CODES);
			req.setTarrifCodeType(TARIFF_CODE_TYPE_FEES);

			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			if (isTimeBased) {
				req.setDuration(DURATION_JUST_FOR_QUERY);
			} else {
				req.setDuration(0);
			}
			req.setMatterID("" + matter.getMatterID());
			req.setDeviceID(sp.getString("deviceID", null));
			String json = URLEncoder.encode(gson.toJson(req));
			try {
				resp = CommsUtil.getData(Statics.URL + json,
						getApplicationContext());
				
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				if (resp.getResponseCode() == 0) {
					tariffList = resp.getMobileTariffCodeList();
					return 0;
				} else {
					return resp.getResponseCode();
				}
			} catch (CommsException e) {
				Log.e(LOG, "Error posting fee", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			enableButtons();
			end = System.currentTimeMillis();
			if (ret > 0) {
				tariffList = new ArrayList<MobileTariffCodeDTO>();
				if (ret == 999) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp == null) {
					ToastUtil.errorToast(getApplicationContext(),
							"Get Tariff Codes failed. Please contact GhostPractice support");
					} else {
						ToastUtil.errorToast(getApplicationContext(), resp.getResponseMessage());
					}
				}
			}

			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			enableTariffSpinner();
			setTariffSpinner();

			// save tariff codes in cache
			createTariffCache();
			timeToggle.setEnabled(true);
			// timeToggle.setVisibility(View.VISIBLE);
		}

	}
	private void refreshChoices() {
		setHourSpinner();
		setMinuteSpinner();
		editNarration.setText("");
		editAmount.setText("");
		duration = 0;
		Log.d(LOG, "### Screen refreshed");
	}
	class FeeCalcTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			start = System.currentTimeMillis();
			disableButtons();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			WebServiceResponse resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.CALCULATE_FEE);
			req.setTariffCodeID(Integer.parseInt(selectedTariff.getId()));
			
			req.setMatterID(matter.getMatterID());
			//
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			if (!isTimeBased) {
				req.setDuration(DURATION_JUST_FOR_QUERY);
			} else {
				req.setDuration(duration);
			}
			req.setMatterID("" + matter.getMatterID());
			req.setDeviceID(sp.getString("deviceID", null));
			String json = URLEncoder.encode(gson.toJson(req));
			try {
				resp = CommsUtil.getData(Statics.URL + json,
						getApplicationContext());
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				if (resp.getResponseCode() == 0) {
					calculatedAmt = resp.getFee();
					return 0;
				} else {
					return resp.getResponseCode();
				}
			} catch (CommsException e) {
				Log.e(LOG, "Error calculating fee", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;

			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			enableButtons();
			end = System.currentTimeMillis();
			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp != null) {
					ToastUtil.errorToast(getApplicationContext(),
							resp.getResponseMessage());
					} else {
						ToastUtil.errorToast(getApplicationContext(), "Fee Calculation failed. Please try again or contact GhostPractice support");
					}
				}
				return;
			}

			if (calculatedAmt == 0) {
				ToastUtil.toast(getApplicationContext(),
						"Fee Calculation web service returned 0.00 fee");
			}
			NumberFormatter.setAmountText(editAmount, calculatedAmt);
			animateButtonIn(btnSend);
			
		}
	}

	boolean ignoreCalcButton;

	void setFields() {
		Bundle b = getIntent().getBundleExtra("data");
		matter = (MatterDTO) b.getSerializable("matter");
		isFee = b.getBoolean("isFee");
		//
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		//
		TextView header = (TextView) findViewById(R.id.HEADER_title);
		if (isFee) {
			header.setText("Post Fee");
		} else {
			header.setText("Post Unbillable Fee");
		}
		TextView name = (TextView) findViewById(R.id.PF_matterName);
		name.setText(matter.getMatterName());
		bar = (ProgressBar) findViewById(R.id.HEADER_progress);
		editAmount = (EditText) findViewById(R.id.PF_amount);
		editAmount.setHint("0.00");
		editAmount.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					if (!editAmount.getText().toString().equalsIgnoreCase("")) {
						animateButtonIn(btnSend);
						enableButtons();
					}
				}
				return false;
			}
		});
		editAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (!editAmount.getText().toString().equalsIgnoreCase("")) {
						animateButtonIn(btnSend);
						enableButtons();
					}
				}

			}
		});

		editNarration = (EditText) findViewById(R.id.PF_narration);
		timeToggle = (ToggleButton) findViewById(R.id.PF_toggle);
		timeToggle.setChecked(true);
		timeToggle
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// disableButtons();
						if (isChecked) {
							isTimeBased = true;
							hourSpinner.setVisibility(View.VISIBLE);
							minuteSpinner.setVisibility(View.VISIBLE);
						} else {
							isTimeBased = false;
							duration = 0;
							hourSpinner.setVisibility(View.GONE);
							minuteSpinner.setVisibility(View.GONE);
						}
						if (!isTariffCacheFilled()) {
							new TariffTask().execute();
						} else {
							refreshFromCache();
						}
					}
				});

		hourSpinner = (Spinner) findViewById(R.id.PF_hourSpinner);
		minuteSpinner = (Spinner) findViewById(R.id.PF_minuteSpinner);
		tariffSpinner = (Spinner) findViewById(R.id.PF_tariffSpinner);
		setHourSpinner();
		setMinuteSpinner();
		disableTariffSpinner();
		btnSend = (Button) findViewById(R.id.PF_btnPost);
		btnCalculate = (Button) findViewById(R.id.PF_btnCalculate);
		btnCalculate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				calculateFee();
			}
		});
		btnSend.setEnabled(false);

		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				postFee();
			}
		});

	}

	void finishPosting() {
		Intent data = new Intent();
		Bundle b = new Bundle();
		b.putSerializable("matterDetail", matter);
		data.putExtra("data", b);
		setResult(RESULT_OK, data);
		finish();
	}

	void disableButtons() {
		btnSend.setEnabled(false);
	}

	void enableButtons() {
		btnSend.setEnabled(true);
	}

	long start, end;
	WebServiceResponse resp = null;

	class PostTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableButtons();
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			GhostRequestDTO req = new GhostRequestDTO();
			if (isFee) {
				req.setRequestType(GhostRequestDTO.POST_FEE);
			} else {
				req.setRequestType(GhostRequestDTO.POST_UNBILLABLE_FEE);
			}

			req.setFee(fee);
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			req.setDeviceID(sp.getString("deviceID", null));
			String json = null;
			try {
				json = URLEncoder.encode(gson.toJson(req), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				resp = CommsUtil.getData(Statics.URL + json,
						getApplicationContext());
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				return resp.getResponseCode();

			} catch (CommsException e) {
				Log.e(LOG, "Error getting matter detail", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			end = System.currentTimeMillis();
			enableButtons();
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp != null) {
						ToastUtil.errorToast(getApplicationContext(),
								resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(getApplicationContext(),
										"Posting failed. Please try again or contact GhostPractice support");
					}
				}
				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			
			//animateButtonOut(btnSend);
			if (resp.getMatter() != null) {
				matter = resp.getMatter();
			}
			refreshChoices();
			ToastUtil.toast(getApplicationContext(), "Fee Posting Successful");
            finish();

		}

	}

	void animateButtonOut(final Button btn) {
		Animation a = AnimationUtils.makeOutAnimation(getApplicationContext(),
				true);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				btn.setVisibility(View.GONE);

			}
		});
		btn.startAnimation(a);
	}

	void animateButtonIn(final Button btn) {
		btn.setVisibility(View.VISIBLE);
		Animation a = AnimationUtils.makeInAnimation(getApplicationContext(),
				true);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		btn.startAnimation(a);
	}

	void setTariffSpinner() {
		ArrayList<String> tarList = new ArrayList<String>();
		tarList.add("Please select tariff");
		for (MobileTariffCodeDTO code : tariffList) {
			tarList.add(code.getName());
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, tarList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tariffSpinner.setAdapter(dataAdapter);
		tariffSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						editAmount.setText("");
						if (arg2 == 0) {
							selectedTariff = null;
							editNarration.setText("");
							//animateButtonOut(btnSend);
							return;
						}
						selectedTariff = tariffList.get(arg2 - 1);
						editNarration.setText(selectedTariff.getNarration());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	void setHourSpinner() {
		hourList = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			if (i == 0) {
				hourList.add("0 hours");
				continue;
			}
			if (i == 1) {
				hourList.add("1 hour");
				continue;
			}

			hourList.add("" + i + " hours");
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.xxsimple_spinner_item, hourList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		hourSpinner.setAdapter(dataAdapter);
		hourSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						editAmount.setText("");
						//animateButtonIn(btnSend);
						calculateTimeDuration();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		
	}

	void calculateTimeDuration() {
		int hours = hourSpinner.getSelectedItemPosition();
		int mins = minuteSpinner.getSelectedItemPosition();

		if (hours == 0) {
			duration = mins;
			return;
		}
		int hourMinutes = hours * 60;
		duration = hourMinutes + mins;
		Log.d(LOG, "#### Duration calculated: " + duration);
	}

	void setMinuteSpinner() {
		minuteList = new ArrayList<String>();
		for (int i = 0; i < 60; i++) {
			if (i == 0) {
				minuteList.add("0 minutes");
				continue;
			}
			if (i == 1) {
				minuteList.add("1 minute");
				continue;
			}

			minuteList.add("" + i + " minutes");
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.xxsimple_spinner_item, minuteList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		minuteSpinner.setAdapter(dataAdapter);
		minuteSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						editAmount.setText("");
						//animateButtonIn(btnSend);
						calculateTimeDuration();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	void calculateFee() {
		if (selectedTariff == null) {
			ToastUtil.errorToast(getApplicationContext(),
					"Please select tariff code before requesting Calculation");
			return;
		}
		if (isTimeBased) {
			if (duration == 0) {
				ToastUtil
						.errorToast(getApplicationContext(),
								"Please enter time duration before requesting Calculation");
				return;
			}
		}
		new FeeCalcTask().execute();

	}

	void postFee() {

		if (editAmount.getText().toString().equalsIgnoreCase("")) {
			ToastUtil.errorToast(getApplicationContext(),
					"Please enter proper amount or re-calculate");
			return;
		}
		if (isTimeBased) {
			if (duration == 0) {
				ToastUtil
						.errorToast(getApplicationContext(),
								"Please enter time duration before posting Fee");
				return;
			}
		}
		fee = new FeeDTO();
		double amt = 0.00;

		Pattern patt = Pattern.compile(",");
		Matcher m = patt.matcher(editAmount.getText().toString());
		String s = m.replaceAll("");
		//
		try {
			amt = Double.parseDouble(s);
			Log.i(LOG, "## Fee amount to be posted: " + amt);
		} catch (Exception e) {
			ToastUtil
					.errorToast(getApplicationContext(),
							"Please calculate the fee or enter the proper amount manually");
			return;
		}

		if (amt == 0) {
			ToastUtil
					.errorToast(getApplicationContext(),
							"Please calculate the fee or enter the proper amount manually");
			return;
		}

		fee.setAmount(amt);
		fee.setDate(new Date().getTime());
		fee.setDuration(duration);
		fee.setMatterID(matter.getMatterID());
		fee.setNarration(editNarration.getText().toString());
		fee.setTariffCodeID(selectedTariff.getId());

		new PostTask().execute();
	}

	void enableTariffSpinner() {
		tariffSpinner.setVisibility(View.VISIBLE);
	}

	void disableTariffSpinner() {
		tariffSpinner.setVisibility(View.GONE);
	}

	class ElapsedData {
		public int activityID;
		public double elapsedSeconds;
	}

	private boolean isTariffCacheFilled() {
		if (cachedTimeTariffList == null || cachedTimeTariffList.size() == 0) {
			return false;
		}

		if (cachedTariffList == null || cachedTariffList.size() == 0) {
			return false;
		}

		return true;
	}

	private void refreshFromCache() {
		tariffList = new ArrayList<MobileTariffCodeDTO>();
		if (isTimeBased) {
			for (MobileTariffCodeDTO t : cachedTimeTariffList) {
				tariffList.add(t);
			}
		} else {
			for (MobileTariffCodeDTO t : cachedTariffList) {
				tariffList.add(t);
			}
		}
		setTariffSpinner();
		Log.e(LOG, "Tariffs Refreshed from cache, isTimeBased: " + isTimeBased
				+ " size:" + tariffList.size());
	}

	private void createTariffCache() {
		if (isTimeBased) {
			cachedTimeTariffList = new ArrayList<MobileTariffCodeDTO>();
			for (MobileTariffCodeDTO t : tariffList) {
				cachedTimeTariffList.add(setCode(t));
			}
		} else {
			cachedTariffList = new ArrayList<MobileTariffCodeDTO>();
			for (MobileTariffCodeDTO t : tariffList) {
				cachedTariffList.add(setCode(t));
			}

		}

	}

	private MobileTariffCodeDTO setCode(MobileTariffCodeDTO t) {
		MobileTariffCodeDTO mtc = new MobileTariffCodeDTO();
		mtc.setAmount(t.getAmount());
		mtc.setId(t.getId());

		mtc.setName(t.getName());
		mtc.setNarration(t.getNarration());
		mtc.setSurchargeApplies(t.isSurchargeApplies());
		mtc.setTariffType(t.getTariffType());
		mtc.setTimeBasedCode(t.isTimeBasedCode());
		mtc.setUnits(t.getUnits());

		return mtc;
	}

	class ElapsedTask extends AsyncTask<ElapsedData, Void, Void> {

		@Override
		protected Void doInBackground(ElapsedData... params) {
			ElapsedData data = params[0];
//			try {
//				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
//						getApplicationContext());
//			} catch (CommsException e) {
//				Log.e(LOG, "Problem posting elapsed time");
//			} catch (NetworkUnavailableException e) {
//				Log.e(LOG, "Network Error", e);
//			}

			return null;
		}

	}

	List<MobileTariffCodeDTO> cachedTariffList, cachedTimeTariffList;
	FeeDTO fee;
	double calculatedAmt;
	Button btnSend, btnCalculate;
	MobileTariffCodeDTO selectedTariff;
	boolean isTimeBased = true;
	int duration = 0;
	Gson gson = new Gson();
	List<MobileTariffCodeDTO> tariffList;
	List<String> hourList, minuteList;
	EditText editAmount, editNarration;
	Spinner tariffSpinner, hourSpinner, minuteSpinner;
	ToggleButton timeToggle;
	SharedPreferences sp;
	MatterDTO matter;
	ProgressBar bar;
	boolean isFee;
	static final String LOG = "PostFee";
	public static final int TARIFF_CODE_TYPE_FEES = 0;
	public static final int TARIFF_CODE_TYPE_NOTES = 1;

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
}
