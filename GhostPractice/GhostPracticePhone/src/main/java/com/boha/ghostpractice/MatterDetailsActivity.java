package com.boha.ghostpractice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostlibrary.FeeEarnerListActivity;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
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

import java.net.URLEncoder;

public class MatterDetailsActivity extends ActionBarActivity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	LinearLayout btnContainer;
	long start, end;
	Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG, "onCreate()......entered");
		ctx = getApplicationContext();
		setContentView(R.layout.matter_detail);

		setFields();

		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(ctx, bar, SharedUtil.getPracticeName(ctx));
		}
		new MatterDetailTask().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG, "onResume()......entered");
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
			}
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
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
										"Matter Details failed. Please contact GhostPractice support");
					}
				}
				return;
			}
			if (matterDetail == null) {
				ToastUtil.errorToast(getApplicationContext(),
						"Matter details not found");
				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			enableButtons();
			updateDetails();

		}

	}

	void setAmountText(TextView txt, double amt) {
		NumberFormatter.setAmountText(txt, amt);

	}

	void startFee(boolean isFee) {
		Intent i = new Intent(getApplicationContext(), PostFeeActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("matter", matterDetail);
		b.putBoolean("isFee", isFee);
		i.putExtra("data", b);
		startActivityForResult(i, POST_FEE);
	}

    void startFeeEarnerList() {
        Intent i = new Intent(getApplicationContext(), FeeEarnerListActivity.class);
        i.putExtra("matterID", ""+matter.getMatterID());
        startActivity(i);
    }
	void startNote() {
		Intent i = new Intent(getApplicationContext(), PostNoteActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("matter", matterDetail);
		i.putExtra("data", b);
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == POST_FEE) {
			if (resultCode == RESULT_OK) {
				// update matter details....
				Bundle b = data.getBundleExtra("data");
				if (b != null) {
					matterDetail = (MatterDTO) b
							.getSerializable("matterDetail");
					updateDetails();
				}
			} else {
				new MatterDetailTask().execute();
				Log.d(LOG, "Posting cancelled...by back button");
			}
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
		matterName.setText(matter.getMatterName());
		ownerName.setText(matter.getCurrentOwner());
		matterID.setText("Matter ID: " + matter.getMatterID());
		legacy.setText(matterDetail.getLegacyAccount());
		clientName.setText(matter.getClientName());
		topLayout.setVisibility(View.VISIBLE);
	}

	void enableButtons() {
		Animation a = AnimationUtils.makeInAnimation(getApplicationContext(),
				true);
		a.setDuration(750);
		btnContainer.setVisibility(View.VISIBLE);
		btnContainer.startAnimation(a);
	}

	void disableButtons() {
		Animation a = AnimationUtils.makeOutAnimation(getApplicationContext(),
				true);
		a.setDuration(500);
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
				btnContainer.setVisibility(View.GONE);
			}
		});
		// btnContainer.startAnimation(a);
		btnContainer.setVisibility(View.GONE);
	}

	void setFields() {
		Bundle b = getIntent().getBundleExtra("data");
		matter = (MatterSearchResultDTO) b.getSerializable("matter");
		//
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		//
		TextView header = (TextView) findViewById(R.id.HEADER_title);
		header.setText("Matter Details");

		bar = (ProgressBar) findViewById(R.id.HEADER_progress);
		businessBal = (TextView) findViewById(R.id.MD_businessBalance);
		currentBal = (TextView) findViewById(R.id.MD_currentBalance);
		trustBal = (TextView) findViewById(R.id.MD_trustBalance);
		unBilled = (TextView) findViewById(R.id.MD_unbilled);
		reserveTrust = (TextView) findViewById(R.id.MD_reserveTrust);
		pending = (TextView) findViewById(R.id.MD_pendingDisb);
		topLayout = (LinearLayout) findViewById(R.id.MD_topLayout);
		investTrust = (TextView) findViewById(R.id.MD_investTrust);
		matterName = (TextView) findViewById(R.id.MD_matterName);
		ownerName = (TextView) findViewById(R.id.MD_ownerName);
		matterID = (TextView) findViewById(R.id.MD_matterID);
		legacy = (TextView) findViewById(R.id.MD_legacy);
		clientName = (TextView) findViewById(R.id.MD_clientName);

		btnPostFee = (Button) findViewById(R.id.MD_btnPostFee);
		btnPostNote = (Button) findViewById(R.id.MD_btnPostNote);
        btnTask = (Button) findViewById(R.id.MD_btnTask);
		btnPostUnbilled = (Button) findViewById(R.id.MD_btnPostUnbillable);

		btnContainer = (LinearLayout) findViewById(R.id.MD_btnContainer);
		disableButtons();

		btnPostFee.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.flashOnce(btnPostNote, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						startFee(true);
					}
				});

			}
		});
		btnPostUnbilled.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.flashOnce(btnPostNote, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						startFee(false);
					}
				});

			}
		});
		btnPostNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.flashOnce(btnPostNote, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						startNote();
					}
				});

			}
		});
        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeeEarnerList();
            }
        });
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
//				if (data.elapsedSeconds == 0) {
//					Log.w("MatterDetailsAct",
//							"### Roundtrip elapsed is zero, should not be");
//				}
//				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
//						getApplicationContext());
//			} catch (CommsException e) {
//				Log.e(LOG, "Problem posting elapsed time");
//			} catch (NetworkUnavailableException e) {
//
//			}

			return null;
		}

	}

	static final String LOG = "MatterDetails";
	Gson gson = new Gson();
	SharedPreferences sp;
	MatterDTO matterDetail;
	ProgressBar bar;
	LinearLayout topLayout;
	MatterSearchResultDTO matter;
	TextView businessBal, currentBal, trustBal, unBilled, reserveTrust,
			pending, reference, investTrust, matterName, ownerName, matterID,
			legacy, clientName;
	Button btnPostFee, btnPostUnbilled, btnPostNote, btnTask;
	static final int POST_FEE = 1;

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
}
