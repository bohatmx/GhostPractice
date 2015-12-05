package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.fragments.BranchFinancialStatus;
import com.boha.ghostpractice.fragments.FeeTargetBranch;
import com.boha.ghostpractice.fragments.FeeTargetOwner;
import com.boha.ghostpractice.fragments.FeeTargetPractice;
import com.boha.ghostpractice.fragments.MatterAnalysisBranch;
import com.boha.ghostpractice.fragments.MatterAnalysisOwner;
import com.boha.ghostpractice.fragments.MatterAnalysisPractice;
import com.boha.ghostpractice.reports.data.Branch;
import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.reports.data.FinancialStatusReport;
import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;
import com.boha.ghostpractice.tablet.interfaces.ReportInterface;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class ReportControllerActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_controller);
		setFields();
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
				result.responseCode = 999;
				return result;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.responseCode = 97;
				return result;
			}
		}

		@Override
		protected void onPostExecute(Result result) {
			bar.setVisibility(View.GONE);
			vb.vibrate(50);
			enableToggles();

			if (result.responseCode > 0) {
				if (result.responseCode == 999) {
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
			// ignoreMe = true;

			reportType = result.reportType;
			startReportPager(result.reportType);
		}

	}

	int reportType;

	void startReportPager(int reportType) {
		this.reportType = reportType;
		txtPageNumber = (TextView) findViewById(R.id.PAGER_pageNumber);
		getReportData();
		mAdapter = new MyAdapter(getSupportFragmentManager(), numberOfPages);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		switch (reportType) {
		case ReportControllerActivity.FINANCIAL_STATUS:
			mPager.setCurrentItem(0, true);
			txtPageNumber.setText("1 of " + numberOfPages);
			break;
		case ReportControllerActivity.FEE_TARGET:
			if (financeReport != null) {
				mPager.setCurrentItem(2, true);
				txtPageNumber.setText("3 of " + numberOfPages);
			} else {
				mPager.setCurrentItem(0, true);
				txtPageNumber.setText("1");
			}

			break;
		case ReportControllerActivity.MATTER_ANALYSIS:
			if (financeReport != null) {
				if (feeTargetReport != null) {
					mPager.setCurrentItem(5, true);
					txtPageNumber.setText("6 of " + numberOfPages);
				} else {
					mPager.setCurrentItem(2, true);
					txtPageNumber.setText("3 of " + numberOfPages);
				}
			} else {
				if (feeTargetReport != null) {
					mPager.setCurrentItem(3, true);
					txtPageNumber.setText("4 of " + numberOfPages);
				} else {
					mPager.setCurrentItem(0, true);
					txtPageNumber.setText("1 of " + numberOfPages);
				}
			}
			break;

		default:
			break;
		}

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			// @Override
			public void onPageSelected(int arg0) {
				txtPageNumber.setText("" + (arg0 + 1) + " of " + numberOfPages);
				isReadyToFinish = false;
			}

			// @Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int curr = mPager.getCurrentItem();
				if (curr > 0)
					return;

				/*
				 * if (arg0 == 0 && arg1 == 0.0 && arg2 == 0) { if
				 * (isReadyToFinish) { isReadyToFinish = false; finish(); } else
				 * { isReadyToFinish = true; } }
				 */

			}

			// @Override
			public void onPageScrollStateChanged(int arg0) {
				// Log.i("RP", "### onPageScrollStateChanged " + arg0);

			}
		});

	}

	boolean isReadyToFinish;

	void getReportData() {

		reportList = new ArrayList<ReportInterface>();
		numberOfPages = 0;

		// Financial Status Report
		if (financeReport != null) {
			numberOfPages += financeReport.getBranches().size();
			for (Branch br : financeReport.getBranches()) {
				BranchFinancialStatus bfs = new BranchFinancialStatus(br);
				reportList.add(bfs);
			}
		}
		// Fee Target Progress Report
		if (feeTargetReport != null) {
			numberOfPages += 3;

			FeeTargetPractice ftp = new FeeTargetPractice(feeTargetReport);
			FeeTargetBranch ftb = new FeeTargetBranch(feeTargetReport);
			FeeTargetOwner fto = new FeeTargetOwner(feeTargetReport);
			reportList.add(ftp);
			reportList.add(ftb);
			reportList.add(fto);
		}

		// Matter Analysis Report
		if (matterReport != null) {
			numberOfPages += 3;

			MatterAnalysisPractice ftp = new MatterAnalysisPractice(
					matterReport);
			MatterAnalysisBranch ftb = new MatterAnalysisBranch(matterReport);
			MatterAnalysisOwner fto = new MatterAnalysisOwner(matterReport);
			reportList.add(ftp);
			reportList.add(ftb);
			reportList.add(fto);
		}

	}

	void disableToggles() {
		togFinancial.setEnabled(false);
		togFeeTarget.setEnabled(false);
		togMatter.setEnabled(false);
		togAll.setEnabled(false);
	}

	void enableToggles() {
		togFinancial.setEnabled(true);
		togFeeTarget.setEnabled(true);
		togMatter.setEnabled(true);
		togAll.setEnabled(true);
	}

	boolean ignoreMe;

	void setFields() {
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//
		// TextView header = (TextView) findViewById(R.id.HEADER_title);
		// header.setText("Practice Reports");

		// TextView txt = (TextView) findViewById(R.id.RC_userName);
		// txt.setText(sp.getString("userName",
		// sp.getString("userName", "UserName")));
		bar = (ProgressBar) findViewById(R.id.RC_progress);
		togFinancial = (Button) findViewById(R.id.RC_toggleFinance);
		togFeeTarget = (Button) findViewById(R.id.RC_toggleFeeTarget);
		togMatter = (Button) findViewById(R.id.RC_toggleMatter);
		togAll = (Button) findViewById(R.id.RC_toggleAll);
		togAll.setVisibility(View.GONE);
		togFinancial.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new ReportTask().execute(FINANCIAL_STATUS);

			}
		});
		togFeeTarget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new ReportTask().execute(FEE_TARGET);

			}
		});
		togMatter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new ReportTask().execute(MATTER_ANALYSIS);

			}
		});
		//
		new ReportTask().execute(FINANCIAL_STATUS);

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
						getApplicationContext());
			} catch (CommsException e) {
				Log.e("ReportController", "Problem posting elapsed time");
			} catch (NetworkUnavailableException e) {
				//
			}

			return null;
		}

	}

	//
	Button togFinancial, togFeeTarget, togMatter, togAll;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.REPORT_MENU_search:
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_report_controller, menu);
		return true;
	}

	//
	public class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm, int pages) {
			super(fm);
			numberOfPages = pages;

		}

		@Override
		public int getCount() {
			return numberOfPages;
		}

		@Override
		public Fragment getItem(int position) {
			return (Fragment) reportList.get(position);

		}

	}

	TextView txtPageNumber;
	static int numberOfPages;
	MyAdapter mAdapter;
	List<ReportInterface> reportList;
	boolean isAllReportsRequested;
	ViewPager mPager;
	private static String LOG = ReportControllerActivity.class
			.getCanonicalName();
}
