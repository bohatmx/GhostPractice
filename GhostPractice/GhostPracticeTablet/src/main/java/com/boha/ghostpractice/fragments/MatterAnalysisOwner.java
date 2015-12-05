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
import com.boha.ghostpractice.reports.data.Owner;
import com.boha.ghostpractice.tablet.R;
import com.boha.ghostpractice.tablet.interfaces.ReportInterface;
import com.boha.ghostpractice.util.NumberFormatter;

public class MatterAnalysisOwner extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	MatterAnalysisByOwnerReport matterReport;
	LinearLayout bucket;

	public MatterAnalysisOwner(MatterAnalysisByOwnerReport matterReport) {
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
		view = inflater.inflate(R.layout.matter_analysis_owner, null);
		inf = inflater;
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Matter Analysis (Owner)");
		bucket = (LinearLayout) view.findViewById(R.id.MABO_layout);
		setBranches();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	void setBranches() {
		for (Branch branch : matterReport.getBranches()) {
			TextView t = (TextView) inf.inflate(R.layout.text, null);
			t.setText(branch.getName());
			bucket.addView(t);
			for (Owner owner : branch.getOwners()) {
				TextView tn = (TextView) inf.inflate(R.layout.text, null);
				tn.setText(owner.getName());
				bucket.addView(tn);
				//
				TextView th = (TextView) inf.inflate(R.layout.text_subheader, null);
				th.setText("Matter Activity");
				bucket.addView(th);
				//
				LinearLayout lay = (LinearLayout) inf.inflate(
						R.layout.matter_activity, null);
				TextView t1 = (TextView) lay.findViewById(R.id.MAC_active);
				setNumberText(t1, owner.getMatterActivity().getActive());

				TextView t2 = (TextView) lay.findViewById(R.id.MAC_deactivated);
				setNumberText(t2, owner.getMatterActivity().getDeactivated());

				TextView t3 = (TextView) lay.findViewById(R.id.MAC_newWork);
				setNumberText(t3, owner.getMatterActivity().getNewWork());

				TextView t4 = (TextView) lay.findViewById(R.id.MAC_noActivity);
				setNumberText(t4, owner.getMatterActivity().getNoActivity());

				TextView t5 = (TextView) lay.findViewById(R.id.MAC_noActivityDuration);
				t5.setText(owner.getMatterActivity().getNoActivityDuration());
				//
				TextView t6 = (TextView) lay.findViewById(R.id.MAC_workedOn);
				t6.setText("" + owner.getMatterActivity().getWorkedOn());

				bucket.addView(lay);
				//
				TextView tha = (TextView) inf.inflate(R.layout.text_subheader, null);
				tha.setText("Matter Balances");
				bucket.addView(tha);
				//
				LinearLayout lay1 = (LinearLayout) inf.inflate(
						R.layout.matter_balances, null);

				TextView t11 = (TextView) lay1.findViewById(R.id.MAB_business);
				setAmountText(t11, owner.getMatterBalances()
						.getBusiness());

				TextView t21 = (TextView) lay1.findViewById(R.id.MAB_trustBal);
				setAmountText(t21, owner.getMatterBalances()
						.getTrust());

				TextView t31 = (TextView) lay1.findViewById(R.id.MAB_invest);
				setAmountText(t31, owner.getMatterBalances()
						.getInvestment());

				TextView t41 = (TextView) lay1.findViewById(R.id.MAB_unbilled);
				setAmountText(t41, owner.getMatterBalances()
						.getUnbilled());

				TextView t51 = (TextView) lay1.findViewById(R.id.MAB_pending);
				setAmountText(t51, owner.getMatterBalances()
						.getPendingDisbursements());

				bucket.addView(lay1);
			}

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
