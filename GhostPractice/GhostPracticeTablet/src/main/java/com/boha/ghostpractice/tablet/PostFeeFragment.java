package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.ghostpractice.data.FeeDTO;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MobileTariffCodeDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.tablet.adapter.StringAdapter;
import com.boha.ghostpractice.tablet.interfaces.PostListener;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class PostFeeFragment extends Fragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Context ctx;
	private Vibrator vb;
	private View view;
	private PostListener listener;
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private WebServiceResponse resp = null;
	private static final int DURATION_JUST_FOR_QUERY = 100;
	boolean ignoreCalcButton;

	public PostFeeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.d(LOG, "################# PostFeeFragment --- onCreateView()");
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.post_fee, null);

		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	void finishPosting() {
		Intent data = new Intent();
		Bundle b = new Bundle();
		b.putSerializable("matterDetail", matter);
		data.putExtra("data", b);

	}

	void disableButtons() {
		btnSend.setEnabled(false);
	}

	void enableButtons() {
		btnSend.setEnabled(true);
	}

	class TariffTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableTariffSpinner();
			start = System.currentTimeMillis();
			// timeToggle.setVisibility(View.GONE);
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

			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
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
				return 99;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			vb.vibrate(50);
			end = System.currentTimeMillis();
			if (ret > 0) {
				tariffList = new ArrayList<MobileTariffCodeDTO>();
				if (ret == 99) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp == null) {
					ToastUtil.errorToast(ctx, "Tariff Codes not found");
					} else {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					}
				}
			}

			ElapsedTimeUtil.showElapsed(start, end, ctx);
			enableTariffSpinner();
			setTariffSpinner();

			// save tariff codes in cache
			createTariffCache();
			timeToggle.setEnabled(true);
			// timeToggle.setVisibility(View.VISIBLE);
		}

	}

	class FeeCalcTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.CALCULATE_FEE);
			req.setTariffCodeID(Integer.parseInt(selectedTariff.getId()));
			Log.d(LOG, "#### ----> Calculating Fee, duration: " + duration + " tariffCode: " + req.getTariffCodeID());
			
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

			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				if (resp.getResponseCode() == 0) {
					Log.i(LOG, "#### ----> Fee returned from calculation: " + resp.getFee());
					calculatedAmt = resp.getFee();
					return 0;
				} else {
					return resp.getResponseCode();
				}
			} catch (CommsException e) {
				Log.e(LOG, "Error posting fee", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			vb.vibrate(50);
			enableButtons();
			end = System.currentTimeMillis();
			ElapsedTimeUtil.showElapsed(start, end, ctx);
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp != null) {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(ctx,
										"Fee Calculation failed. Please contact GhostPractice support");
					}
				}
				return;
			}
			if (calculatedAmt == 0) {
				ToastUtil.toast(ctx,
						"Fee Calculation web service returned 0.00 fee");
			}
			NumberFormatter.setAmountText(editAmount, calculatedAmt);
			//animateButtonIn(btnSend);
			
		}
	}

	class PostTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableButtons();
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
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

			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
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
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			end = System.currentTimeMillis();
			vb.vibrate(100);
			enableButtons();
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp != null) {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(ctx,
										"Posting failed. Please contact GhostPractice support");
					}
				}
				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, ctx);
			
			//animateButtonOut(btnSend);
			if (resp.getMatter() != null) {
				matter = resp.getMatter();
			}
			refreshFields();
			String message = "Fee Posting Successful";
			listener.onPostComplete(message);
			ToastUtil.toast(ctx, message);

		}

	}

	void animateButtonOut(final Button btn) {
		Animation a = AnimationUtils.makeOutAnimation(ctx, true);
		a.setAnimationListener(new AnimationListener() {

			// @Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			// @Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			// @Override
			public void onAnimationEnd(Animation animation) {
				btn.setVisibility(View.GONE);

			}
		});
		btn.startAnimation(a);
	}

	void animateButtonIn(final Button btn) {
		btn.setVisibility(View.VISIBLE);
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		a.setAnimationListener(new AnimationListener() {

			// @Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			// @Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			// @Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		btn.startAnimation(a);
	}

	void setTariffSpinner() {
		Log.i(LOG,
				"################# PostFeeFragment --- setting Tariff spinner ...");
		ArrayList<String> tarList = new ArrayList<String>();
		tarList.add("Please select tariff");
		for (MobileTariffCodeDTO code : tariffList) {
			tarList.add(code.getName());
		}

		StringAdapter dataAdapter = new StringAdapter(ctx,
				R.layout.string_tariff_item, tarList);
		dataAdapter.setDropDownViewResource(R.layout.drop_down);
		tariffSpinner.setAdapter(dataAdapter);
		tariffSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					// @Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//editAmount.setText("");
						if (arg2 == 0) {
							selectedTariff = null;
							editNarration.setText("");
							disableButtons();
							return;
						}
						selectedTariff = tariffList.get(arg2 - 1);
						editNarration.setText(selectedTariff.getNarration());
						enableButtons();
					}

					// @Override
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

		StringAdapter dataAdapter = new StringAdapter(ctx,
				R.layout.string_item, hourList);
		dataAdapter.setDropDownViewResource(R.layout.drop_down);
		hourSpinner.setAdapter(dataAdapter);
		hourSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					// @Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//editAmount.setText("");
						//animateButtonIn(btnSend);
						calculateTimeDuration();
					}

					// @Override
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
		Log.d(LOG, "#### ----> Duration calculated: " + duration);
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

		StringAdapter dataAdapter = new StringAdapter(ctx,
				R.layout.string_item, minuteList);
		dataAdapter.setDropDownViewResource(R.layout.drop_down);
		minuteSpinner.setAdapter(dataAdapter);
		minuteSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					// @Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						//editAmount.setText("");
						//animateButtonIn(btnSend);
						calculateTimeDuration();
					}

					// @Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	void calculateFee() {
		if (selectedTariff == null) {
			ToastUtil.errorToast(ctx,
					"Please select tariff code before requesting Calculation");
			return;
		}
		if (isTimeBased) {
			if (duration == 0) {
				ToastUtil
						.errorToast(ctx,
								"Please enter time duration before requesting Calculation");
				return;
			}
		}
		new FeeCalcTask().execute();

	}

	void postFee() {

		if (editAmount.getText().toString().equalsIgnoreCase("")) {
			ToastUtil.errorToast(ctx,
					"Please enter proper amount or re-calculate");
			return;
		}
		if (selectedTariff == null) {
			ToastUtil.errorToast(ctx,
					"Please select tariff");
			return;
		}
		if (isTimeBased && duration == 0) {
			ToastUtil.errorToast(ctx,
					"Please select time units");
			return;
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
					.errorToast(ctx,
							"Please calculate the fee or enter the proper amount manually");
			return;
		}

		if (amt == 0) {
			ToastUtil
					.errorToast(ctx,
							"Please calculate the fee or enter the proper amount manually");
			return;
		}

		fee.setAmount(amt);
		fee.setDate(new Date().getTime());
		if (isTimeBased) {
			fee.setDuration(duration);
		}
		fee.setMatterID(matter.getMatterID());
		fee.setNarration(editNarration.getText().toString());
		fee.setTariffCodeID(selectedTariff.getId());

		new PostTask().execute();
	}

	private TextView header;

	public void setHeader() {

		if (isFee) {
			header.setText("Post Fee - " + matter.getMatterName());
		} else {
			header.setText("Post Unbillable Fee - " + matter.getMatterName());
		}
	}
	private void refreshFields() {
		editAmount.setText("");
		editAmount.setHint("0.00");
		editNarration.setText("");
		setTariffSpinner();
		setMinuteSpinner();
		setHourSpinner();
		selectedTariff = null;
	}
	private void setFields() {
		if (matter == null) {
			Log.e(LOG, "### matter is null....Bam!");
			return;
		}
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		//
		header = (TextView) view.findViewById(R.id.HEADER_title);
		if (isFee) {
			header.setText("Post Fee - " + matter.getMatterName());
		} else {
			header.setText("Post Unbillable Fee - " + matter.getMatterName());
		}
		// TextView name = (TextView) view.findViewById(R.id.PF_matterName);
		// name.setText(matter.getMatterName());
		txtSpinHr = (TextView) view.findViewById(R.id.PF_spinTextHours);
		txtSpinMin = (TextView) view.findViewById(R.id.PF_spinTextMinutes);
		bar = (ProgressBar) view.findViewById(R.id.HEADER_progress);
		editAmount = (EditText) view.findViewById(R.id.PF_amount);
		editAmount.setHint("0.00");
		editAmount.setOnKeyListener(new View.OnKeyListener() {

			// @Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					if (!editAmount.getText().toString().equalsIgnoreCase("")) {
						//animateButtonIn(btnSend);
						enableButtons();
					}
				}
				return false;
			}
		});
		editAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			// @Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (!editAmount.getText().toString().equalsIgnoreCase("")) {
						//animateButtonIn(btnSend);
						enableButtons();
					}
				}

			}
		});

		editNarration = (EditText) view.findViewById(R.id.PF_narration);
		timeToggle = (CheckBox) view.findViewById(R.id.PF_toggle);
		timeToggle.setChecked(true);
		timeToggle
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					// @Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// disableButtons();
						if (isChecked) {
							isTimeBased = true;
							hourSpinner.setVisibility(View.VISIBLE);
							minuteSpinner.setVisibility(View.VISIBLE);
							txtSpinHr.setVisibility(View.VISIBLE);
							txtSpinMin.setVisibility(View.VISIBLE);
						} else {
							isTimeBased = false;
							editAmount.setText("");
							disableButtons();
							hourSpinner.setVisibility(View.GONE);
							minuteSpinner.setVisibility(View.GONE);
							txtSpinHr.setVisibility(View.GONE);
							txtSpinMin.setVisibility(View.GONE);
						}
						if (!isTariffCacheFilled()) {
							new TariffTask().execute();
						} else {
							refreshFromCache();
						}
					}
				});

		hourSpinner = (Spinner) view.findViewById(R.id.PF_hourSpinner);
		minuteSpinner = (Spinner) view.findViewById(R.id.PF_minuteSpinner);
		tariffSpinner = (Spinner) view.findViewById(R.id.PF_tariffSpinner);
		setHourSpinner();
		setMinuteSpinner();
		disableTariffSpinner();
		btnSend = (Button) view.findViewById(R.id.PF_btnPost);
		btnCalculate = (Button) view.findViewById(R.id.PF_btnCalculate);
		btnCalculate.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				calculateFee();
			}
		});
		btnSend.setEnabled(false);

		btnSend.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				postFee();
			}
		});

	}

	public MatterDTO getMatter() {
		return matter;
	}

	public void setMatter(MatterDTO matter) {
		this.matter = matter;
		setFields();

	}

	public void getTariffCodes() {
		new TariffTask().execute();
	}

	public boolean isFee() {
		return isFee;
	}

	public void setFee(boolean isFee) {
		this.isFee = isFee;
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
		Log.i(LOG,
				"################# PostFeeFragment --- refreshing tariff spinner from cache");
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
			Log.d(LOG,
					"################# PostFeeFragment --- time based - createTariffCache() isFee = "
							+ isFee);
			cachedTimeTariffList = new ArrayList<MobileTariffCodeDTO>();
			for (MobileTariffCodeDTO t : tariffList) {
				cachedTimeTariffList.add(setCode(t));
			}
		} else {
			Log.d(LOG,
					"################# PostFeeFragment --- NON time based - createTariffCache() isFee = "
							+ isFee);
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
			Log.d(LOG,
					"################# PostFeeFragment --- sending elapsed time back to server");
			try {
				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
						ctx);
			} catch (CommsException e) {
				Log.e(LOG, "Problem posting elapsed time");
			} catch (NetworkUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG, "onCreate....");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(LOG, "onViewCreated....");

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(LOG, "onAttach....");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(LOG, "onDetach....");
		// mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG, "onSaveInstanceState....");
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public PostListener getListener() {
		return listener;
	}

	public void setListener(PostListener listener) {
		this.listener = listener;
	}

	long start, end;
	static final String LOG = PostFeeFragment.class.getCanonicalName();
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
	CheckBox timeToggle;
	SharedPreferences sp;
	MatterDTO matter;
	ProgressBar bar;
	TextView txtSpinMin, txtSpinHr;
	boolean isFee;
	public static final int TARIFF_CODE_TYPE_FEES = 0;
	public static final int TARIFF_CODE_TYPE_NOTES = 1;
}
