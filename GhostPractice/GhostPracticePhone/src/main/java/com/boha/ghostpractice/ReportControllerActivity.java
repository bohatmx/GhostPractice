package com.boha.ghostpractice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.reports.data.FinancialStatusReport;
import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.SharedUtil;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.Util;
import com.boha.ghostpractice.util.bean.CommsException;
import com.boha.ghostpracticephone.R;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.Date;

public class ReportControllerActivity extends ActionBarActivity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_controller);
		setFields();
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(getApplicationContext(), bar,
					SharedUtil.getPracticeName(getApplicationContext()));
		}
	}


    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("RP", "##### onResume");
		ignoreMe = false;
	}

	class Result {
		public int responseCode;
		public int reportType;
	}

	long start, end;
	static final long FIVE_MINUTES = 1000 * 60 * 5;
	WebServiceResponse resp;

	class ReportTask extends AsyncTask<Integer, Void, Result> {

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			disableToggles();
			start = System.currentTimeMillis();
		}

		@Override
		protected Result doInBackground(Integer... arg0) {
			int type = arg0[0];
			Result result = new Result();
			result.reportType = type;
			long now = new Date().getTime();
			switch (type) {

			case FINANCIAL_STATUS:
				if (lastFinanceReportTime > 0) {
					if (now - lastFinanceReportTime < FIVE_MINUTES) {
						result.responseCode = 0;
						return result;
					}
				}
				break;

			case FEE_TARGET:
				if (lastFeeTargetReportTime > 0) {
					if (now - lastFeeTargetReportTime < FIVE_MINUTES) {
						result.responseCode = 0;
						return result;
					}
				}
				break;

			case MATTER_ANALYSIS:
				if (lastMatterReportTime > 0) {
					if (now - lastMatterReportTime < FIVE_MINUTES) {
						result.responseCode = 0;
						return result;
					}
				}
				break;
			}

			resp = null;
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.GET_REPORT);
			req.setReportType(type);
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			req.setUserID(sp.getInt("userID", 0));
			req.setCompanyID(sp.getInt("companyID", 0));
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
					switch (type) {
					case FINANCIAL_STATUS:
						financeReport = resp.getFinancialStatusReport();
						lastFinanceReportTime = new Date().getTime();
						break;
					case FEE_TARGET:
						feeTargetReport = resp.getFeeTargetProgressReport();
						lastFeeTargetReportTime = new Date().getTime();
						break;
					case MATTER_ANALYSIS:
						matterReport = resp.getMatterAnalysisByOwnerReport();
						lastMatterReportTime = new Date().getTime();
						break;
					}
					result.responseCode = 0;
					return result;
				} else {
					result.responseCode = resp.getResponseCode();
					return result;
				}
			} catch (CommsException e) {
				Log.e(LOG, "Error getting report", e);
				result.responseCode = 90;
				return result;
			} catch (NetworkUnavailableException e) {
				result.responseCode = 99;
				return result;
			}
		}

		@Override
		protected void onPostExecute(Result result) {
			bar.setVisibility(View.GONE);
			vb.vibrate(50);
			enableToggles();

			if (result.responseCode > 0) {
				if (result.responseCode == 99) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp == null) {
						ToastUtil
								.errorToast(getApplicationContext(),
										"Report failed. Please try again or contact GhostPractice support");
					} else {
						ToastUtil.errorToast(getApplicationContext(),
								resp.getResponseMessage());
					}
				}

				return;
			}
			ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
			ignoreMe = true;

			reportType = result.reportType;
			startReportPager(result.reportType);
		}

	}

	int reportType;

	void startReportPager(int reportType) {
		Intent i = new Intent(getApplicationContext(), ReportPager.class);
		Bundle b = new Bundle();
		b.putInt("reportType", reportType);
		if (financeReport != null) {
			b.putSerializable("financialStatus", financeReport);
		}
		if (feeTargetReport != null) {
			b.putSerializable("feeTarget", feeTargetReport);
		}
		if (matterReport != null) {
			b.putSerializable("matterAnalysis", matterReport);
		}
		i.putExtra("data", b);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(i);
	}

	void disableToggles() {
		togFinancial.setEnabled(false);
		togFeeTarget.setEnabled(false);
		togMatter.setEnabled(false);
	}

	void enableToggles() {
		togFinancial.setEnabled(true);
		togFeeTarget.setEnabled(true);
		togMatter.setEnabled(true);
	}

	boolean ignoreMe;

	void setFields() {
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//
		TextView header = (TextView) findViewById(R.id.HEADER_title);
		header.setText("Practice Reports");

		TextView txt = (TextView) findViewById(R.id.RC_userName);
		txt.setText(sp.getString("userName",
				sp.getString("userName", "UserName")));
		bar = (ProgressBar) findViewById(R.id.HEADER_progress);
		togFinancial = (Button) findViewById(R.id.RC_toggleFinance);
		togFeeTarget = (Button) findViewById(R.id.RC_toggleFeeTarget);
		togMatter = (Button) findViewById(R.id.RC_toggleMatter);

		togFinancial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.flashOnce(togFinancial, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						if (ignoreMe) {
							ignoreMe = false;
							startReportPager(FINANCIAL_STATUS);
						} else {
							new ReportTask().execute(FINANCIAL_STATUS);
						}
					}
				});
			}
		});
		togFeeTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.flashOnce(togFeeTarget, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						if (ignoreMe) {
							ignoreMe = false;
							startReportPager(FEE_TARGET);

						} else {
							new ReportTask().execute(FEE_TARGET);
						}
					}
				});
			}
		});
		togMatter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.flashOnce(togMatter, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						if (ignoreMe) {
							ignoreMe = false;
							startReportPager(MATTER_ANALYSIS);
						} else {
							new ReportTask().execute(MATTER_ANALYSIS);
						}
					}
				});
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
//				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
//						getApplicationContext());
//			} catch (CommsException e) {
//				Log.e("ReportController", "Problem posting elapsed time");
//			} catch (NetworkUnavailableException e) {
//				//
//			}

			return null;
		}

	}

	//
	Button togFinancial, togFeeTarget, togMatter;
	Vibrator vb;
	SharedPreferences sp;
	ProgressBar bar;

	FinancialStatusReport financeReport;
	FeeTargetProgressReport feeTargetReport;
	MatterAnalysisByOwnerReport matterReport;
	long lastFinanceReportTime, lastFeeTargetReportTime, lastMatterReportTime;
	//
	Gson gson = new Gson();
	public static final int FINANCIAL_STATUS = 1;
	public static final int FEE_TARGET = 2;
	public static final int MATTER_ANALYSIS = 3;
	public static final int ALL_REPORTS = 4;
	static final String LOG = "ReportControler";

}
