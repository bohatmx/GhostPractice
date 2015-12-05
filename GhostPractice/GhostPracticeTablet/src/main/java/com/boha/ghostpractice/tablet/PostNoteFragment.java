package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.ghostpractice.data.FeeDTO;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterNoteDTO;
import com.boha.ghostpractice.data.MobileTariffCodeDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.tablet.adapter.StringAdapter;
import com.boha.ghostpractice.tablet.interfaces.PostListener;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class PostNoteFragment extends Fragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Context ctx;
	private View view;
	private PostListener listener;
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private WebServiceResponse resp = null;
	boolean ignoreCalcButton;

	public PostNoteFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.post_note, null);

		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	class TariffTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableTariffSpinner();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(LOG, "@@@@ PostNoteFragment - getting note tariff codes...");
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

			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
				if (resp.getResponseCode() == 0) {
					codeList = resp.getMobileTariffCodeList();
					return 0;
				} else {
					return resp.getResponseCode();
				}
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
			vb.vibrate(50);
			if (ret > 0) {
				codeList = new ArrayList<MobileTariffCodeDTO>();
				if (ret == 999) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp != null) {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(ctx,
										"Get Tariff Codes failed. Please contact GhostPractice support");
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
		StringAdapter dataAdapter = new StringAdapter(ctx,
				R.layout.string_tariff_item, tarList);
		dataAdapter.setDropDownViewResource(R.layout.drop_down);
		tariffSpinner.setAdapter(dataAdapter);
		tariffSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					// @Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						btnSend.setEnabled(true);
						btnSend.setVisibility(View.VISIBLE);
						if (arg2 == 0) {
							selectedTariff = null;
							//editNarration.setText("");
							return;
						} else {
							selectedTariff = codeList.get(arg2 - 1);
							editNarration.setText(selectedTariff.getNarration());
						}
					}

					// @Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
	}

	class PostTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			btnSend.setEnabled(false);
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(LOG, "@@@@ PostNoteFragment - posting note ...");
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
				resp = CommsUtil.getData(Statics.URL + json, ctx);
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				return resp.getResponseCode();

			} catch (CommsException e) {
				Log.e(LOG, "Error posting note", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			btnSend.setEnabled(true);
			vb.vibrate(50);
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
			editNarration.setText("");
			setTariffSpinner();
			ElapsedTimeUtil.showElapsed(start, end, ctx);			
			ToastUtil.toast(ctx, "Note Posting Successful");

		}

	}

	void postNote() {

		if (editNarration.getText().toString().trim().equalsIgnoreCase("")) {
			ToastUtil.errorToast(ctx, "Please enter text of note");
			return;
		}
		note = new MatterNoteDTO();
		int day = datePicker.getDayOfMonth();
		int mth = datePicker.getMonth();
		int yr = datePicker.getYear();
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, mth);
		cal.set(Calendar.YEAR, yr);
		// cal.set(Calendar.HOUR_OF_DAY, 0);
		// cal.set(Calendar.MINUTE, 0);
		// cal.set(Calendar.SECOND, 0);

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
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		//
		bar = (ProgressBar) view.findViewById(R.id.HEADER_progress);
		TextView header = (TextView) view.findViewById(R.id.HEADER_title);
		header.setText("Post Note - " + matter.getMatterName());

		tariffSpinner = (Spinner) view.findViewById(R.id.PN_tariffSpinner);
		disableTariffSpinner();
		btnSend = (Button) view.findViewById(R.id.PN_btnPost);
		btnSend.setVisibility(View.GONE);
		datePicker = (DatePicker) view.findViewById(R.id.PN_datePicker);
		editNarration = (EditText) view.findViewById(R.id.PN_narration);
		btnSend.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				postNote();

			}
		});
	}

	public MatterDTO getMatter() {
		return matter;
	}

	public void setMatter(MatterDTO matter) {
		this.matter = matter;
		setFields();
		new TariffTask().execute();
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
			Log.d(LOG, "@@@@ PostNoteFragment - sending elapsed time...");
			ElapsedData data = params[0];
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

	private MatterNoteDTO note;
	private List<MobileTariffCodeDTO> codeList;
	private DatePicker datePicker;
	private Vibrator vb;

	private long start, end;
	private static final String LOG = PostNoteFragment.class.getCanonicalName();
	private List<MobileTariffCodeDTO> cachedTariffList, cachedTimeTariffList;
	private FeeDTO fee;
	private double calculatedAmt;
	private Button btnSend, btnCalculate;
	private MobileTariffCodeDTO selectedTariff;
	private boolean isTimeBased = true;
	private int duration = 0;
	private Gson gson = new Gson();
	private List<MobileTariffCodeDTO> tariffList;
	private List<String> hourList, minuteList;
	private EditText editAmount, editNarration;
	private Spinner tariffSpinner, hourSpinner, minuteSpinner;
	private CheckBox timeToggle;
	private SharedPreferences sp;
	private MatterDTO matter;
	private ProgressBar bar;
	TextView txtSpinMin, txtSpinHr;
	boolean isFee;
	public static final int TARIFF_CODE_TYPE_FEES = 0;
	public static final int TARIFF_CODE_TYPE_NOTES = 1;
}
