package com.boha.ghostpractice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boha.ghostpractice.reports.data.Branch;
import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;
import com.boha.ghostpractice.tablet.R;
import com.boha.ghostpractice.tablet.interfaces.ReportInterface;
import com.boha.ghostpractice.util.NumberFormatter;

public class MatterAnalysisBranch extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	LinearLayout bucket;

	MatterAnalysisByOwnerReport matterReport;

	public MatterAnalysisBranch(MatterAnalysisByOwnerReport matterReport) {
		this.matterReport = matterReport;
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.matter_analysis_branch, null);
		inf = inflater;
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Matter Analysis (Branch)");
		//
		bucket = (LinearLayout)view.findViewById(R.id.MABR_layout);
		setBranches();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	void setBranches() {
		for (Branch branch : matterReport.getBranches()) {
			TextView t = (TextView)inf.inflate(R.layout.text, null);
			t.setText(branch.getName());
			bucket.addView(t);
			//
			TextView th = (TextView)inf.inflate(R.layout.text_subheader, null);
			th.setText("Matter Activity");
			bucket.addView(th);
			//
			LinearLayout matterActivityLayout = (LinearLayout) inf.inflate(
					R.layout.matter_activity, null);

			TextView t1 = (TextView) matterActivityLayout.findViewById(R.id.MAC_active);
			setNumberText(t1, branch.getBranchTotals().getMatterActivity().getActive());

			TextView t2 = (TextView) matterActivityLayout.findViewById(R.id.MAC_deactivated);
			setNumberText(t2, branch.getBranchTotals().getMatterActivity().getDeactivated());

			TextView t3 = (TextView) matterActivityLayout.findViewById(R.id.MAC_newWork);
			setNumberText(t3, branch.getBranchTotals().getMatterActivity().getNewWork());

			TextView t4 = (TextView) matterActivityLayout.findViewById(R.id.MAC_noActivity);			
			setNumberText(t4, branch.getBranchTotals().getMatterActivity().getNoActivity());

			TextView t5 = (TextView) matterActivityLayout.findViewById(R.id.MAC_noActivityDuration);
			t5.setText(branch.getBranchTotals().getMatterActivity()
					.getNoActivityDuration());
			TextView t6 = (TextView) matterActivityLayout.findViewById(R.id.MAC_workedOn);
			t6.setText("" + branch.getBranchTotals().getMatterActivity().getWorkedOn());
			
			

			bucket.addView(matterActivityLayout);
			//
			TextView thb = (TextView)inf.inflate(R.layout.text_subheader, null);
			thb.setText("Matter Balances");
			bucket.addView(thb);
			//
			LinearLayout lay1 = (LinearLayout) inf.inflate(
					R.layout.matter_balances, null);

			TextView t11 = (TextView) lay1.findViewById(R.id.MAB_business);
			setAmountText(t11, branch.getBranchTotals().getMatterBalances()
					.getBusiness());

			TextView t21 = (TextView) lay1.findViewById(R.id.MAB_trustBal);
			setAmountText(t21, branch.getBranchTotals().getMatterBalances()
					.getTrust());

			TextView t31 = (TextView) lay1.findViewById(R.id.MAB_invest);
			setAmountText(t31, branch.getBranchTotals().getMatterBalances()
					.getInvestment());

			TextView t41 = (TextView) lay1.findViewById(R.id.MAB_unbilled);
			setAmountText(t41, branch.getBranchTotals().getMatterBalances()
					.getUnbilled());

			TextView t51 = (TextView) lay1.findViewById(R.id.MAB_pending);
			setAmountText(t51, branch.getBranchTotals().getMatterBalances()
					.getPendingDisbursements());

			bucket.addView(lay1);
			
		}
	}
	void setAmountText(TextView txt, double amt) {
		NumberFormatter.setAmountText(txt, amt);
		
	}
	void setNumberText(TextView txt, int amt) {
		NumberFormatter.setNumberText(txt, amt);
		
	}

	//NumberFormat nf = NumberFormat.getInstance();
	LayoutInflater inf;
	
	//@Override
	public void getName() {
		// TODO Auto-generated method stub

	}
}
