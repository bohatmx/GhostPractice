package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.tablet.adapter.MatterSearchAdapter;
import com.boha.ghostpractice.tablet.interfaces.MatterSelectedListener;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class MatterListFragment extends ListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Context ctx;
	private Vibrator vb;
	private View view;
	private MatterSelectedListener listener;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public MatterListFragment() {
	}

	public MatterSelectedListener getListener() {
		return listener;
	}

	public void setListener(MatterSelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.matter_search, null);

		// configure header
		setFields();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
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
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}

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
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		Log.d(LOG, "onListItemClick position: " + position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG, "onSaveInstanceState....");
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		Log.d(LOG, "setActivatedOnItemClick....");
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		Log.d(LOG, "setActivatedPosition, position: " + position);
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	long start, end;
	WebServiceResponse resp;

	class SearchTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			splash.setVisibility(View.VISIBLE);
			hideKeyboard();
			start = System.currentTimeMillis();
			btnSearch.setVisibility(View.GONE);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
			try {
				GhostRequestDTO req = new GhostRequestDTO();
				req.setRequestType(GhostRequestDTO.FIND_MATTER);
				req.setAppID(sp.getInt("appID", 0));
				req.setPlatformID(sp.getInt("platformID", 0));
				req.setUserID(sp.getInt("userID", 0));
				req.setCompanyID(sp.getInt("companyID", 0));
				req.setSearchString(searchString);
				req.setDeviceID(sp.getString("deviceID", null));

				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				matterList = resp.getMatterSearchList();
				return resp.getResponseCode();

			} catch (CommsException e) {
				Log.e(LOG, "Error searching matters", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 99;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}

		}

		@Override
		protected void onPostExecute(Integer ret) {
			vb.vibrate(50);
			bar.setVisibility(View.GONE);
			btnSearch.setVisibility(View.VISIBLE);
			if (ret > 0) {
				if (ret == 99) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp == null) {
						ToastUtil
								.errorToast(ctx,
										"Error searching. Please try again or contact GhostPractice support");
					} else {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					}
				}
				return;
			}
			if (matterList != null) {
				txtCount.setText("" + matterList.size());
			} else {
				matterList = new ArrayList<MatterSearchResultDTO>();
			}
			if (matterList.size() == 0) {
				ToastUtil.toast(ctx, "No matters found for search");
				splash.setVisibility(View.VISIBLE);
			} else {
				splash.setVisibility(View.GONE);
			}
			ElapsedTimeUtil.showElapsed(start, end, ctx);
			setList();
		}

	}

	private void setList() {
		MatterSearchAdapter a = new MatterSearchAdapter(ctx,
				R.layout.matter_search_item, matterList);
		setListAdapter(a);
		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					// @Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						matterSelected = matterList.get(arg2);
						// fragment ...
						listener.onMatterSelected(matterSelected);
					}
				});
		Animation an = AnimationUtils.makeInAnimation(ctx, true);
		getListView().startAnimation(an);
	}

	public List<MatterSearchResultDTO> getMatterList() {
		return matterList;
	}

	public void setMatterList(List<MatterSearchResultDTO> matterList) {
		this.matterList = matterList;
		setList();
	}

	class ElapsedData {
		public int activityID;
		public double elapsedSeconds;
	}

	class ElapsedTask extends AsyncTask<ElapsedData, Void, Void> {

		@Override
		protected Void doInBackground(ElapsedData... params) {
			ElapsedData data = params[0];
			try {
				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
						ctx);
			} catch (CommsException e) {
				Log.e("MatterSearch", "Problem posting elapsed time");
			} catch (NetworkUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	Gson gson = new Gson();

	void setFields() {
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		bar = (ProgressBar) view.findViewById(R.id.HEADER_progress);
		editSearch = (EditText) view.findViewById(R.id.MS_editSearch);
		splash = (ImageView) view.findViewById(R.id.splash);
		splash.setScaleType(ScaleType.FIT_XY);
		btnSearch = (Button) view.findViewById(R.id.MS_btnSearch);
		btnReports = (Button) view.findViewById(R.id.MS_btnReports);
		btnReports.setVisibility(View.VISIBLE);
		txtCount = (TextView) view.findViewById(R.id.HEADER_count);
		txtCount.setVisibility(View.VISIBLE);
		txtHeader = (TextView) view.findViewById(R.id.HEADER_title);
		txtHeader.setText("Matters Found");

		/// change scaling of splash
		editSearch.setOnTouchListener(new View.OnTouchListener() {

			// @Override
			public boolean onTouch(View v, MotionEvent event) {
				splash.setVisibility(View.GONE);
				return false;
			}
		});

		editSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (event.getAction() == KeyEvent.ACTION_UP) {
						Log.d(LOG, "#### Enter key has been pressed. ACTION_UP");
						searchString = editSearch.getText().toString().trim();
						if (!searchString.equalsIgnoreCase("")) {
							new SearchTask().execute();
						} else {
							ToastUtil.errorToast(ctx,
									"Please enter search text");
							return false;
						}
					}
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						Log.d(LOG,
								"#### Enter key has been pressed. ACTION_DOWN");
					}
				}
				return false;
			}
		});
		btnSearch.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				hideKeyboard();
				String s = editSearch.getText().toString().trim();
				if (s.equalsIgnoreCase("")) {
					if (matterList != null) {
						if (matterList.size() == 0) {
							splash.setVisibility(View.VISIBLE);
						}
					} else {
						splash.setVisibility(View.VISIBLE);
					}
					ToastUtil.errorToast(ctx, "Please enter search text");
					return;
				}
				searchString = editSearch.getText().toString().trim();
				new SearchTask().execute();
			}
		});
		btnReports.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, ReportControllerActivity.class);
				startActivity(i);
			}
		});
		btnReports.setVisibility(View.GONE);
	}

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
	}

	public MatterSearchResultDTO getMatterSelected() {
		return matterSelected;
	}

	ImageView splash;
	MatterSearchResultDTO matterSelected;
	List<MatterSearchResultDTO> matterList;
	Button btnSearch, btnReports;
	EditText editSearch;
	TextView txtHeader, txtCount;
	ProgressBar bar;
	SharedPreferences sp;
	String searchString;
	static final String LOG = MatterListFragment.class.getCanonicalName();
}
