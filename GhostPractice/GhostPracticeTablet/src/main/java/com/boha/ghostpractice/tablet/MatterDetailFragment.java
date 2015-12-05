package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.tablet.interfaces.PostButtonListener;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class MatterDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	public MatterDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (getArguments().containsKey(ARG_ITEM_ID)) {
		// mItem =
		// DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
		// }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = getActivity().getApplicationContext();
		rootView = inflater.inflate(R.layout.fragment_matter_detail, container,
				false);
		setFields();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		rootView.startAnimation(a);
		return rootView;
	}

	public void refreshMatterDetail() {
		new MatterDetailTask().execute();
	}

	WebServiceResponse resp;

	class MatterDetailTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			start = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(Void... arg0) {
			resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.GET_MATTER_DETAIL);
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
			req.setMatterID("" + searchResult.getMatterID());
			req.setDeviceID(sp.getString("deviceID", null));

			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				resp = CommsUtil.getData(Statics.URL + json, ctx);
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				Log.e("MatterDetailsAct",
						"matterDetails roundTrip elapsed: "
								+ data.elapsedSeconds + " webService elapsed: "
								+ resp.getElapsedSeconds());

				new ElapsedTask().execute(data);
				if (resp.getResponseCode() == 0) {
					matterDetail = resp.getMatter();
					return 0;
				} else {
					return resp.getResponseCode();
				}
			} catch (NetworkUnavailableException e) {
				return 999;
			} catch (CommsException e) {
				Log.e(LOG, "Error getting matter detail", e);
				return 1;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(ctx);
				} else {
					if (resp != null) {
						ToastUtil.errorToast(ctx, resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(ctx,
										"Matter Details failed. Please contact GhostPractice support");
					}
				}
				return;
			}
			if (matterDetail == null) {
				ToastUtil.errorToast(ctx, "Matter details not found");
				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, ctx);
			// enableButtons();
			updateDetails();

		}

	}

	void updateDetails() {
		setAmountText(businessBal, matterDetail.getBusinessBalance());
		setAmountText(currentBal, matterDetail.getCurrentBalance());
		setAmountText(trustBal, matterDetail.getTrustBalance());
		setAmountText(unBilled, matterDetail.getUnbilledBalance());
		setAmountText(reserveTrust, matterDetail.getReserveTrust());
		setAmountText(pending, matterDetail.getPendingDisbursementBalance());
		setAmountText(investTrust, matterDetail.getInvestmentTrustBalance());

		//
		// matterName.setText(searchResult.getMatterName());
		ownerName.setText(searchResult.getCurrentOwner());
		matterID.setText("Matter ID: " + searchResult.getMatterID());
		legacy.setText(matterDetail.getLegacyAccount());
		clientName.setText(searchResult.getClientName());
		topLayout.setVisibility(View.VISIBLE);
	}

	void setAmountText(TextView txt, double amt) {
		NumberFormatter.setAmountText(txt, amt);

	}

	private View rootView;
	private MatterSearchResultDTO searchResult;

	public void setSearchResult(MatterSearchResultDTO searchResult) {
		this.searchResult = searchResult;
		header.setText("Matter Details - " + searchResult.getMatterName());
		new MatterDetailTask().execute();

	}

	public MatterSearchResultDTO getSearchResult() {
		return searchResult;
	}

	public MatterDTO getMatterDetail() {
		return matterDetail;
	}

	public void setPostButtonOff() {
		btnPostAll.setVisibility(View.GONE);
	}

	public void setMatterDetail(MatterDTO matterDetail,
			MatterSearchResultDTO searchResult) {
		this.matterDetail = matterDetail;
		this.searchResult = searchResult;
		header.setText("Matter Details - " + searchResult.getMatterName());
		updateDetails();
		enableButtons();
	}

	TextView header;

	void setFields() {
		// Bundle b = getIntent().getBundleExtra("data");
		// matter = (MatterSearchResultDTO) b.getSerializable("matter");
		//
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		//
		header = (TextView) rootView.findViewById(R.id.HEADER_title);
		header.setText("Matter Details");

		bar = (ProgressBar) rootView.findViewById(R.id.HEADER_progress);
		businessBal = (TextView) rootView.findViewById(R.id.MD_businessBalance);
		currentBal = (TextView) rootView.findViewById(R.id.MD_currentBalance);
		trustBal = (TextView) rootView.findViewById(R.id.MD_trustBalance);
		unBilled = (TextView) rootView.findViewById(R.id.MD_unbilled);
		reserveTrust = (TextView) rootView.findViewById(R.id.MD_reserveTrust);
		pending = (TextView) rootView.findViewById(R.id.MD_pendingDisb);
		topLayout = (LinearLayout) rootView.findViewById(R.id.MD_topLayout);
		investTrust = (TextView) rootView.findViewById(R.id.MD_investTrust);
		// matterName = (TextView) rootView.findViewById(R.id.MD_matterName);
		ownerName = (TextView) rootView.findViewById(R.id.MD_ownerName);
		matterID = (TextView) rootView.findViewById(R.id.MD_matterID);
		legacy = (TextView) rootView.findViewById(R.id.MD_legacy);
		clientName = (TextView) rootView.findViewById(R.id.MD_clientName);

		btnPostAll = (Button) rootView.findViewById(R.id.MD_btnPostAll);
		btnPostNote = (Button) rootView.findViewById(R.id.MD_btnPostNote);
		btnPostUnbilled = (Button) rootView
				.findViewById(R.id.MD_btnPostUnbillable);

		btnContainer = (LinearLayout) rootView
				.findViewById(R.id.MD_btnContainer);
		disableButtons();

		btnPostAll.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				vb.vibrate(50);
				startPosting(true);

			}
		});
		btnPostUnbilled.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				vb.vibrate(50);
				startPosting(false);

			}
		});
		btnPostNote.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				vb.vibrate(50);
				startNote();
			}
		});
	}

	private void startNote() {

	}

	private void startPosting(boolean isFee) {

		listener.onPostFeeRequested();

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
				if (data.elapsedSeconds == 0) {
					Log.w("MatterDetailsAct",
							"### Roundtrip elapsed is zero, should not be");
				}
				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
						ctx);
			} catch (CommsException e) {
				Log.e(LOG, "Problem posting elapsed time");
			} catch (NetworkUnavailableException e) {

			}

			return null;
		}

	}

	void enableButtons() {
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		a.setDuration(750);
		btnContainer.setVisibility(View.VISIBLE);
		btnContainer.startAnimation(a);
	}

	void disableButtons() {
		Animation a = AnimationUtils.makeOutAnimation(ctx, true);
		a.setDuration(500);
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
				btnContainer.setVisibility(View.GONE);
			}
		});
		// btnContainer.startAnimation(a);
		btnContainer.setVisibility(View.GONE);
	}

	PostButtonListener listener;

	public PostButtonListener getListener() {
		return listener;
	}

	public void setListener(PostButtonListener listener) {
		this.listener = listener;
	}

	private static final String LOG = "MatterDetailFragment";
	private Gson gson = new Gson();
	private SharedPreferences sp;
	private Vibrator vb;
	private MatterDTO matterDetail;

	private ProgressBar bar;
	private LinearLayout topLayout;
	// MatterSearchResultDTO matter;
	private TextView businessBal, currentBal, trustBal, unBilled, reserveTrust,
			pending, reference, investTrust, matterName, ownerName, matterID,
			legacy, clientName;
	private Button btnPostAll, btnPostFee, btnPostUnbilled, btnPostNote;
	private LinearLayout btnContainer;
	private Context ctx;
	private long start, end;
	public static final int POST_FEE = 1;
}
