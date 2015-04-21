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

import com.boha.ghostpractice.reports.data.Bank;
import com.boha.ghostpractice.reports.data.Branch;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpracticephone.R;

import java.util.Locale;

public class BranchFinancialStatus extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	Branch branch;



	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.financial_status_branch, null);

		// configure header
		LinearLayout lay = (LinearLayout) view.findViewById(R.id.HEADER_layout);
		lay.setBackgroundColor(getResources().getColor(R.color.purple));
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Financial Status (Branch)");
		setBusinessStatus();
		setTrustStatus();
		Animation a = AnimationUtils.makeInAnimation(ctx, true);
		view.startAnimation(a);
		return view;
	}

	void setBusinessStatus() {

		TextView t1 = (TextView) view.findViewById(R.id.FSB_branchName);
		t1.setText(branch.getName());
		TextView t2 = (TextView) view.findViewById(R.id.FSB_banksTotal);
		setAmountText(t2, branch.getBusinessStatus().getBanksTotal());

		TextView t3 = (TextView) view.findViewById(R.id.FSB_businessCreditors);
		setAmountText(t3, branch.getBusinessStatus().getBusinessCreditors());

		TextView t4 = (TextView) view.findViewById(R.id.FSB_businessDebtors);
		setAmountText(t4, branch.getBusinessStatus().getBusinessDebtors());

		TextView t5 = (TextView) view.findViewById(R.id.FSB_unbilled);
		setAmountText(t5, branch.getBusinessStatus().getUnbilled());
		TextView t6 = (TextView) view.findViewById(R.id.FSB_pending);
		setAmountText(t6, branch.getBusinessStatus().getPendingDisbursements());

		TextView t7 = (TextView) view.findViewById(R.id.FSB_VAT);
		setAmountText(t7, branch.getBusinessStatus().getVat());
		TextView t8 = (TextView) view.findViewById(R.id.FSB_avTransfer);
		setAmountText(t8, branch.getBusinessStatus().getAvailableForTransfer());

		LayoutInflater inflator = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layStatus = (LinearLayout) view
				.findViewById(R.id.FSB_BusinessStatus_banks);

		for (Bank bank : branch.getBusinessStatus().getBanks()) {
			LinearLayout lay = (LinearLayout) inflator.inflate(R.layout.bank,
					null);
			TextView tx1 = (TextView) lay.findViewById(R.id.BANK_balance);			
			setAmountText(tx1, bank.getBalance());
			TextView tx2 = (TextView) lay.findViewById(R.id.BANK_receipts);
			setAmountText(tx2, bank.getReceiptsForPeriod());
			TextView tx3 = (TextView) lay.findViewById(R.id.BANK_reconciledAmt);
			setAmountText(tx3, bank.getReconciledAmount());
			TextView tx4 = (TextView) lay
					.findViewById(R.id.BANK_dateReconciled);
			tx4.setText(bank.getDateReconciled());

			TextView txn = (TextView) lay.findViewById(R.id.BANK_name);
			txn.setText(bank.getName());
			layStatus.addView(lay);
		}

	}

	void setTrustStatus() {
		TextView t2 = (TextView) view.findViewById(R.id.FSBt_banksTotal);
		setAmountText(t2, branch.getTrustStatus().getBanksTotal());
		TextView t3 = (TextView) view.findViewById(R.id.FSBt_trustCreditors);
		setAmountText(t3, branch.getTrustStatus().getTrustCreditors());

		TextView t4 = (TextView) view.findViewById(R.id.FSBt_investments);
		setAmountText(t4, branch.getTrustStatus().getInvestments());

		LayoutInflater inflator = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layStatus = (LinearLayout) view
				.findViewById(R.id.FSB_TrustStatus_banks);

		for (Bank bank : branch.getTrustStatus().getBanks()) {
			LinearLayout lay = (LinearLayout) inflator.inflate(R.layout.bank,
					null);
			TextView tx1 = (TextView) lay.findViewById(R.id.BANK_balance);
			setAmountText(tx1, bank.getBalance());
			TextView tx2 = (TextView) lay.findViewById(R.id.BANK_receipts);
			setAmountText(tx2, bank.getReceiptsForPeriod());

			TextView tx3 = (TextView) lay.findViewById(R.id.BANK_reconciledAmt);
			setAmountText(tx3, bank.getReconciledAmount());

			TextView tx4 = (TextView) lay
					.findViewById(R.id.BANK_dateReconciled);
			tx4.setText(bank.getDateReconciled());

			TextView txn = (TextView) lay.findViewById(R.id.BANK_name);
			txn.setText(bank.getName());

			layStatus.addView(lay);
		}
	}

	void setAmountText(TextView txt, double amt) {
		NumberFormatter.setAmountText(txt, amt);

	}

	Locale deviceLocale;
	//NumberFormat nf = NumberFormat.getInstance();

	@Override
	public void getName() {
		// TODO Auto-generated method stub

	}

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
