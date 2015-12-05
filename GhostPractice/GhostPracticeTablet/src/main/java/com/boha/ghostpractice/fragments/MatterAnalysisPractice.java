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

import com.boha.ghostpractice.reports.data.MatterAnalysisByOwnerReport;
import com.boha.ghostpractice.tablet.R;
import com.boha.ghostpractice.tablet.interfaces.ReportInterface;
import com.boha.ghostpractice.util.NumberFormatter;

public class MatterAnalysisPractice extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	MatterAnalysisByOwnerReport matterReport;

	public MatterAnalysisPractice(MatterAnalysisByOwnerReport matterReport) {
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
		view = inflater.inflate(R.layout.matter_analysis_practice, null);
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Matter Analysis (Practice)");
		setInvoiceTotal();
		setActivity();
		setMatterBalances();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	void setInvoiceTotal() {
		TextView tx = (TextView) view.findViewById(R.id.MAPR_invoicedTotal);
		setAmountText(tx, matterReport.getPracticeTotals().getInvoicedMTDTotal());
	}

	void setActivity() {
		LayoutInflater inflator = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) view
				.findViewById(R.id.MAPR_layout_matter_activity);
		LinearLayout matterActivityLayout = (LinearLayout) inflator.inflate(
				R.layout.matter_activity, null);

		TextView t1 = (TextView) matterActivityLayout.findViewById(R.id.MAC_active);
		setNumberText(t1, matterReport.getPracticeTotals().getMatterActivity().getActive());

		TextView t2 = (TextView) matterActivityLayout.findViewById(R.id.MAC_deactivated);
		setNumberText(t2, matterReport.getPracticeTotals().getMatterActivity().getDeactivated());

		TextView t3 = (TextView) matterActivityLayout.findViewById(R.id.MAC_newWork);
		setNumberText(t3, matterReport.getPracticeTotals().getMatterActivity().getNewWork());

		TextView t4 = (TextView) matterActivityLayout.findViewById(R.id.MAC_noActivity);
		setNumberText(t4, matterReport.getPracticeTotals().getMatterActivity().getNoActivity());

		//
		TextView t5 = (TextView) matterActivityLayout.findViewById(R.id.MAC_noActivityDuration);
		t5.setText(matterReport.getPracticeTotals().getMatterActivity().getNoActivityDuration());
		//
		TextView t6 = (TextView) matterActivityLayout.findViewById(R.id.MAC_workedOn);
		t6.setText("" + matterReport.getPracticeTotals().getMatterActivity().getWorkedOn());

		v.addView(matterActivityLayout);

	}

	void setMatterBalances() {
		LayoutInflater inflator = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) view
				.findViewById(R.id.MAPR_layout_matter_balances);
		LinearLayout lay = (LinearLayout) inflator.inflate(
				R.layout.matter_balances, null);

		TextView t1 = (TextView) lay.findViewById(R.id.MAB_business);
		setAmountText(t1, matterReport.getPracticeTotals().getMatterBalances()
				.getBusiness());

		TextView t2 = (TextView) lay.findViewById(R.id.MAB_trustBal);
		setAmountText(t2, matterReport.getPracticeTotals().getMatterBalances()
				.getTrust());

		TextView t3 = (TextView) lay.findViewById(R.id.MAB_invest);
		setAmountText(t3, matterReport.getPracticeTotals().getMatterBalances()
				.getInvestment());

		TextView t4 = (TextView) lay.findViewById(R.id.MAB_unbilled);
		setAmountText(t4, matterReport.getPracticeTotals().getMatterBalances()
				.getUnbilled());

		TextView t5 = (TextView) lay.findViewById(R.id.MAB_pending);
		setAmountText(t5, matterReport.getPracticeTotals().getMatterBalances()
				.getPendingDisbursements());

		v.addView(lay);
	}

	void setAmountText(TextView txt, double amt) {
		NumberFormatter.setAmountText(txt, amt);
		
	}
	void setNumberText(TextView txt, int amt) {
		NumberFormatter.setNumberText(txt, amt);
		
	}

	//NumberFormat nf = NumberFormat.getInstance();
	//@Override
	public void getName() {
		// TODO Auto-generated method stub

	}
}
