package com.boha.ghostpractice;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterNoteDTO;
import com.boha.ghostpractice.data.MobileTariffCodeDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.SharedUtil;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.Util;
import com.boha.ghostpractice.util.bean.CommsException;
import com.boha.ghostpracticephone.R;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class PostNoteActivity extends ActionBarActivity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_note);
		setFields();
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(getApplicationContext(), bar,
					SharedUtil.getPracticeName(getApplicationContext()));
		}
		new TariffTask().execute();
	}

	WebServiceResponse resp;

	class TariffTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableTariffSpinner();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.GET_TARIFF_CODES);
			req.setTarrifCodeType(TARIFF_CODE_TYPE_NOTES);

			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			req.setMatterID("" + matter.getMatterID());
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
				if (resp.getResponseCode() == 0) {
					codeList = resp.getMobileTariffCodeList();
					return 0;
				} else {
					return resp.getResponseCode();
				}
			} catch (CommsException e) {
				Log.e(LOG, "Error getting tariff codes", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 99;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			if (ret > 0) {
				codeList = new ArrayList<MobileTariffCodeDTO>();
				if (ret == 99) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp == null) {
						ToastUtil
								.errorToast(getApplicationContext(),
										"Get tariff codes failed. Please try again or contact GhostPractice support");
					} else {
						ToastUtil.errorToast(getApplicationContext(),
								resp.getResponseMessage());
					}
				}
				return;
			}
			enableTariffSpinner();
			setTariffSpinner();
		}

	}

	void setTariffSpinner() {
		ArrayList<String> tarList = new ArrayList<String>();
		tarList.add("None");
		for (MobileTariffCodeDTO code : codeList) {
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
						btnSend.setEnabled(true);
						btnSend.setVisibility(View.VISIBLE);
						if (arg2 == 0) {
							selectedTariff = null;
							editNarration.setText("");
							return;
						} else {
							selectedTariff = codeList.get(arg2 - 1);
							editNarration.setText(selectedTariff.getNarration());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	long start, end;

	class PostTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			btnSend.setEnabled(false);
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.POST_NOTE);

			req.setNote(note);
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			req.setDeviceID(sp.getString("deviceID", null));

			try {
                String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
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
				return 99;
			} catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return 0;
        }

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			btnSend.setEnabled(true);
			if (ret > 0) {
				if (ret == 99) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp == null) {
						ToastUtil.errorToast(getApplicationContext(),
								"Note Posting failed, please try again");
					} else {
						ToastUtil.errorToast(getApplicationContext(),
								resp.getResponseMessage());
					}
				}
				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			
			ToastUtil.toast(getApplicationContext(), "Note Posting Successful");
			selectedTariff = null;
            finish();
		}

	}

	void postNote() {

		if (editNarration.getText().toString().equalsIgnoreCase("")
				&& selectedTariff == null) {
			ToastUtil.errorToast(getApplicationContext(),
					"Please enter text of note");
			return;
		}
		note = new MatterNoteDTO();
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, mDay);
		cal.set(Calendar.MONTH, mMonth);
		cal.set(Calendar.YEAR, mYear);
		Log.d(LOG, "## Time of Note Posting: " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

		note.setDate(cal.getTimeInMillis());
		note.setMatterID(matter.getMatterID());
		note.setNarration(editNarration.getText().toString());
		if (selectedTariff == null) {
			note.setTariffCodeID("0");
		} else {
			note.setTariffCodeID(selectedTariff.getId());
		}
		new PostTask().execute();
	}

	void setFields() {
		Bundle b = getIntent().getBundleExtra("data");
		matter = (MatterDTO) b.getSerializable("matter");
		//
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		//
		bar = (ProgressBar) findViewById(R.id.HEADER_progress);
		TextView header = (TextView) findViewById(R.id.HEADER_title);
		header.setText("Post Note");
		TextView name = (TextView) findViewById(R.id.PN_matterName);
		name.setText(matter.getMatterName());
		tariffSpinner = (Spinner) findViewById(R.id.PN_tariffSpinner);
		disableTariffSpinner();
		btnSend = (Button) findViewById(R.id.PN_btnPost);
		btnSend.setVisibility(View.GONE);
		btnDate = (Button) findViewById(R.id.PN_btnDate);
        btnDate.setText(sdf.format(new Date()));
		editNarration = (EditText) findViewById(R.id.PN_narration);
		editNarration.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				selectedTariff = null;
				return false;
			}
		});
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				postNote();

			}
		});
		btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
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
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			return null;
		}

	}

	MatterNoteDTO note;
	Gson gson = new Gson();
	ProgressBar bar;
	MobileTariffCodeDTO selectedTariff;
	List<MobileTariffCodeDTO> codeList;
	EditText editNarration;
	Spinner tariffSpinner;
	Button btnSend, btnDate;
	DatePickerDialog datePickerDialog;
    private long date;
	SharedPreferences sp;
	MatterDTO matter;
    private int round, mYear, mMonth, mDay, mHour, mMinute;
	public static final int TARIFF_CODE_TYPE_NOTES = 1;
	static final String LOG = "PostNote";

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    private void showDateDialog() {

        if (mYear == 0) {
            Calendar cal = GregorianCalendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dp = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                date = cal.getTimeInMillis();
                btnDate.setText(sdf.format(cal.getTime()));
            }
        },mYear, mMonth, mDay);
        dp.setYearRange(2013, 2037);
        dp.show(getSupportFragmentManager(), "datP");

    }

    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", loc);


}
