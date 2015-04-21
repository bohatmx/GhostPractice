package com.boha.ghostpractice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.boha.ghostpractice.fragments.BranchFinancialStatus;
import com.boha.ghostpractice.fragments.FeeTargetBranch;
import com.boha.ghostpractice.fragments.FeeTargetOwner;
import com.boha.ghostpractice.fragments.FeeTargetPractice;
import com.boha.ghostpractice.fragments.MatterAnalysisBranch;
import com.boha.ghostpractice.fragments.MatterAnalysisOwner;
import com.boha.ghostpractice.fragments.MatterAnalysisPractice;
import com.boha.ghostpractice.fragments.ReportInterface;
import com.boha.ghostpractice.reports.data.Branch;
import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.reports.data.FinancialStatusReport;
import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;
import com.boha.ghostpractice.util.SharedUtil;
import com.boha.ghostpractice.util.Util;
import com.boha.ghostpracticephone.R;

import java.util.ArrayList;
import java.util.List;

public class ReportPager extends ActionBarActivity {
	static int numberOfPages;
	MyAdapter mAdapter;

	ViewPager mPager;
	static FinancialStatusReport financeReport;
	static FeeTargetProgressReport feeTargetReport;
	static MatterAnalysisByOwnerReport matterReport;
	static boolean isAllReportsRequested;
	static List<ReportInterface> reportList;

	//TextView txtPageNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_pager);
		//txtPageNumber = (TextView) findViewById(R.id.PAGER_pageNumber);
		getReportData();
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(getApplicationContext(), bar,
					SharedUtil.getPracticeName(getApplicationContext()));
		}
		mAdapter = new MyAdapter(getSupportFragmentManager(), numberOfPages,
				this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		switch (reportType) {
		case ReportControllerActivity.FINANCIAL_STATUS:
			mPager.setCurrentItem(0, true);
			//txtPageNumber.setText("1");
			break;
		case ReportControllerActivity.FEE_TARGET:
			if (financeReport != null) {
				mPager.setCurrentItem(2, true);
				//txtPageNumber.setText("3");
			} else {
				mPager.setCurrentItem(0, true);
				//txtPageNumber.setText("1");
			}
			
			break;
		case ReportControllerActivity.MATTER_ANALYSIS:
			if (financeReport != null) {
				if (feeTargetReport != null) {
					mPager.setCurrentItem(5, true);
					//txtPageNumber.setText("6");
				} else {
					mPager.setCurrentItem(2, true);
					//txtPageNumber.setText("3");
				}
			} else {
				if (feeTargetReport != null) {
					mPager.setCurrentItem(3, true);
					//txtPageNumber.setText("4");
				} else {
					mPager.setCurrentItem(0, true);
					//txtPageNumber.setText("1");
				}
			}
			break;

		default:
			break;
		}

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				//txtPageNumber.setText("" + (arg0 + 1));
				isReadyToFinish = false;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int curr = mPager.getCurrentItem();
				if (curr > 0)
					return;

				if (arg0 == 0 && arg1 == 0.0 && arg2 == 0) {
					if (isReadyToFinish) {
						isReadyToFinish = false;
						finish();
					} else {
						isReadyToFinish = true;
					}
				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// Log.i("RP", "### onPageScrollStateChanged " + arg0);

			}
		});

	}

	int reportType;
	boolean isReadyToFinish;

	void getReportData() {
		Bundle b = getIntent().getBundleExtra("data");
		financeReport = (FinancialStatusReport) b
				.getSerializable("financialStatus");
		feeTargetReport = (FeeTargetProgressReport) b
				.getSerializable("feeTarget");
		matterReport = (MatterAnalysisByOwnerReport) b
				.getSerializable("matterAnalysis");
		isAllReportsRequested = b.getBoolean("isAllReports");
		reportType = b.getInt("reportType");
				
		reportList = new ArrayList<ReportInterface>();
		numberOfPages = 0;
		// Financial Status Report
		if (financeReport != null) {
			numberOfPages += financeReport.getBranches().size();			
			for (Branch br : financeReport.getBranches()) {
				BranchFinancialStatus bfs = new BranchFinancialStatus();
                bfs.setBranch(br);
				reportList.add(bfs);
			}
		}
		// Fee Target Progress Report
		if (feeTargetReport != null) {
			numberOfPages += 3;
			
			FeeTargetPractice ftp = new FeeTargetPractice();
			FeeTargetBranch ftb = new FeeTargetBranch();
			FeeTargetOwner fto = new FeeTargetOwner();
            ftp.setFeeTargetReport(feeTargetReport);
            ftb.setFeeTargetReport(feeTargetReport);
            fto.setFeeTargetReport(feeTargetReport);
			reportList.add(ftp);
			reportList.add(ftb);
			reportList.add(fto);
		}

		// Matter Analysis Report
		if (matterReport != null) {
			numberOfPages += 3;
			
			MatterAnalysisPractice ftp = new MatterAnalysisPractice();
			MatterAnalysisBranch ftb = new MatterAnalysisBranch();
			MatterAnalysisOwner fto = new MatterAnalysisOwner();

            ftp.setMatterReport(matterReport);
            ftb.setMatterReport(matterReport);
            fto.setMatterReport(matterReport);
			reportList.add(ftp);
			reportList.add(ftb);
			reportList.add(fto);
		}

	}

	//
	public static class MyAdapter extends FragmentPagerAdapter {

		ReportPager pager;

		public MyAdapter(FragmentManager fm, int pages, ReportPager pager) {
			super(fm);
			numberOfPages = pages;
			this.pager = pager;

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


    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
}