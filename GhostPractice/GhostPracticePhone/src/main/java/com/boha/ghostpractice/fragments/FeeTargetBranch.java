package com.boha.ghostpractice.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boha.ghostpractice.reports.data.Branch;
import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpracticephone.R;

@SuppressLint("ValidFragment")
public class FeeTargetBranch extends Fragment implements ReportInterface {
	
	private Context ctx;
	Vibrator vb;
	View view;
	LinearLayout bucket;
	private FeeTargetProgressReport feeTargetReport;


	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.fee_target_branch, null);

		// configure header
		//LinearLayout lay = (LinearLayout) view.findViewById(R.id.HEADER_layout);
		//lay.setBackgroundColor(getResources().getColor(R.color.black));
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Fee Target Progress (Branch)");
		bucket = (LinearLayout)view.findViewById(R.id.FTB_layout);
		setBranches();
		return view;
	}

	void setBranches() {
		LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (Branch branch : feeTargetReport.getBranches()) {
			LinearLayout branchLayout = (LinearLayout)inf.inflate(R.layout.fee_branch_template, null);
			TextView txt = (TextView)branchLayout.findViewById(R.id.TEMP_branchName);
			txt.setText(branch.getName());
			
			TextView txt1 = (TextView)branchLayout.findViewById(R.id.TEMP_invoicedTotal);
			setAmountText(txt1, branch.getBranchTotals().getInvoicedMTD());
			
			LinearLayout mtdLayout = (LinearLayout)branchLayout.findViewById(R.id.TEMP_recordedMTD);
			LinearLayout mtdTemplate = (LinearLayout)inf.inflate(R.layout.mtd, null);
			TextView t1 = (TextView) mtdTemplate.findViewById(R.id.MTD_achieved);
			setAmountText(t1, branch.getBranchTotals().getRecordedMTD().getAchieved());
			
			TextView t2 = (TextView) mtdTemplate.findViewById(R.id.MTD_estimated);
			setAmountText(t2, branch.getBranchTotals().getRecordedMTD().getEstimatedTarget());
			
			TextView t3 = (TextView) mtdTemplate.findViewById(R.id.MTD_invoiced);
			setAmountText(t3, branch.getBranchTotals().getRecordedMTD().getInvoicedDebits());
			
			TextView t4 = (TextView) mtdTemplate.findViewById(R.id.MTD_unbilled);
			setAmountText(t4, branch.getBranchTotals().getRecordedMTD().getUnbilled());
			
			TextView t5 = (TextView) mtdTemplate.findViewById(R.id.MTD_total);
			setAmountText(t5, branch.getBranchTotals().getRecordedMTD().getTotal());
			
			mtdLayout.addView(mtdTemplate);
			//
			LinearLayout mtdLayout1 = (LinearLayout)branchLayout.findViewById(R.id.TEMP_recordedYTD);
			LinearLayout mtdTemplate1 = (LinearLayout)inf.inflate(R.layout.mtd, null);
			TextView t11 = (TextView) mtdTemplate1.findViewById(R.id.MTD_achieved);
			setAmountText(t11, branch.getBranchTotals().getRecordedYTD().getAchieved());
			
			TextView t21 = (TextView) mtdTemplate1.findViewById(R.id.MTD_estimated);
			setAmountText(t21, branch.getBranchTotals().getRecordedYTD().getEstimatedTarget());
			
			TextView t31 = (TextView) mtdTemplate1.findViewById(R.id.MTD_invoiced);
			setAmountText(t31, branch.getBranchTotals().getRecordedYTD().getInvoicedDebits());
			
			TextView t41 = (TextView) mtdTemplate1.findViewById(R.id.MTD_unbilled);
			setAmountText(t41, branch.getBranchTotals().getRecordedYTD().getUnbilled());
			
			TextView t51 = (TextView) mtdTemplate1.findViewById(R.id.MTD_total);
			setAmountText(t51, branch.getBranchTotals().getRecordedYTD().getTotal());
			
			mtdLayout1.addView(mtdTemplate1);
			
			
			bucket.addView(branchLayout);
		}
	}
	void setAmountText(TextView txt, double amt ) {
		NumberFormatter.setAmountText(txt, amt);
		
	}
	
	//NumberFormat nf = NumberFormat.getInstance();
	
	@Override
	public void getName() {
		// TODO Auto-generated method stub

	}

	public FeeTargetProgressReport getFeeTargetReport() {
		return feeTargetReport;
	}

	public void setFeeTargetReport(FeeTargetProgressReport feeTargetReport) {
		this.feeTargetReport = feeTargetReport;
	}
}
