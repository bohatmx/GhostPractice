package com.boha.ghostpractice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.util.NumberFormatter;
import com.boha.ghostpracticephone.R;

public class FeeTargetPractice extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	private Bundle state = null;
	FeeTargetProgressReport feeTargetReport;

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

		this.state = state;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity().getApplicationContext();
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		view = inflater.inflate(R.layout.fee_target_practice, null);
		
		TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
		hdr.setText("Fee Target Progress (Practice)");
		setInvoiceTotal();
		setMTD();
		setYTD();
		return view;
	}

	void setInvoiceTotal() {
		TextView tx = (TextView)view.findViewById(R.id.FTP_invoicedTotal);
		setAmountText(tx, feeTargetReport.getPracticeTotals().getInvoicedMTDTotal());
	}
	void setMTD() {
		LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) view.findViewById(R.id.FTP_recordedMTD);
		LinearLayout lay = (LinearLayout)inflator.inflate(R.layout.mtd, null);
		TextView t1 = (TextView) lay.findViewById(R.id.MTD_achieved);
		setAmountText(t1, feeTargetReport.getPracticeTotals().getRecordedMTD().getAchieved());
		
		TextView t2 = (TextView) lay.findViewById(R.id.MTD_estimated);
		setAmountText(t2, feeTargetReport.getPracticeTotals().getRecordedMTD().getEstimatedTarget());
		
		TextView t3 = (TextView) lay.findViewById(R.id.MTD_invoiced);
		setAmountText(t3, feeTargetReport.getPracticeTotals().getRecordedMTD().getInvoicedDebits());
		
		TextView t4 = (TextView) lay.findViewById(R.id.MTD_unbilled);
		setAmountText(t4, feeTargetReport.getPracticeTotals().getRecordedMTD().getUnbilled());
		
		TextView t5 = (TextView) lay.findViewById(R.id.MTD_total);
		setAmountText(t5, feeTargetReport.getPracticeTotals().getRecordedMTD().getTotal());
		//setAmountText(t5, 12345678901.99);
		
		v.addView(lay);		
	}
	void setYTD() {
		LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout v = (LinearLayout) view.findViewById(R.id.FTP_recordedYTD);
		LinearLayout lay = (LinearLayout)inflator.inflate(R.layout.mtd, null);
		
		TextView t1 = (TextView) lay.findViewById(R.id.MTD_achieved);
		setAmountText(t1, feeTargetReport.getPracticeTotals().getRecordedYTD().getAchieved());
		
		TextView t2 = (TextView) lay.findViewById(R.id.MTD_estimated);
		setAmountText(t2, feeTargetReport.getPracticeTotals().getRecordedYTD().getEstimatedTarget());
		
		TextView t3 = (TextView) lay.findViewById(R.id.MTD_invoiced);
		setAmountText(t3, feeTargetReport.getPracticeTotals().getRecordedYTD().getInvoicedDebits());
		
		TextView t4 = (TextView) lay.findViewById(R.id.MTD_unbilled);
		setAmountText(t4, feeTargetReport.getPracticeTotals().getRecordedYTD().getUnbilled());
		
		TextView t5 = (TextView) lay.findViewById(R.id.MTD_total);
		setAmountText(t5, feeTargetReport.getPracticeTotals().getRecordedYTD().getTotal());
		v.addView(lay);
	}

    public void setFeeTargetReport(FeeTargetProgressReport feeTargetReport) {
        this.feeTargetReport = feeTargetReport;
    }

    void setAmountText(TextView txt, double amt ) {
		NumberFormatter.setAmountText(txt, amt);
	
	}
	//NumberFormat nf = NumberFormat.getInstance();
	@Override
	public void getName() {
		// TODO Auto-generated method stub

	}
}