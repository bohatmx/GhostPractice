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
import com.boha.ghostpractice.reports.data.FeeTargetProgressReport;
import com.boha.ghostpractice.reports.data.Owner;
import com.boha.ghostpractice.tablet.R;
import com.boha.ghostpractice.tablet.interfaces.ReportInterface;
import com.boha.ghostpractice.util.NumberFormatter;

public class FeeTargetOwner extends Fragment implements ReportInterface {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	private Context ctx;
	Vibrator vb;
	View view;
	LinearLayout bucket;
	private Bundle state=null;
	
	FeeTargetProgressReport feeTargetReport;
	
	public FeeTargetOwner(FeeTargetProgressReport feeTargetReport) {
		this.feeTargetReport = feeTargetReport;
	}
	  
	  @Override
	  public void onActivityCreated(Bundle state) {
	    super.onActivityCreated(state);
	    
	    this.state=state;
	  }
	  
	  @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle saved) {
			ctx = getActivity().getApplicationContext();
			vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
			view = inflater.inflate(R.layout.fee_target_owner, null);
			inf = inflater;
			
			TextView hdr = (TextView) view.findViewById(R.id.HEADER_title);
			hdr.setText("Fee Target Progress (Owner)");
			bucket = (LinearLayout)view.findViewById(R.id.FTO_layout);
			setOwners();
			Animation a = AnimationUtils.makeInAnimation(ctx, true);
			view.startAnimation(a);
			return view;
		}

	  void setOwners() {
		  for (Branch branch : feeTargetReport.getBranches()) {
			//get branch name
			TextView t = (TextView)inf.inflate(R.layout.text, null);
			t.setText(branch.getName());
			bucket.addView(t);
			for (Owner owner : branch.getOwners()) {
				LinearLayout branchLayout = (LinearLayout)inf.inflate(R.layout.fee_branch_template, null);
				TextView txt = (TextView)branchLayout.findViewById(R.id.TEMP_branchName);
				txt.setText(owner.getName());
				
				TextView txt1 = (TextView)branchLayout.findViewById(R.id.TEMP_invoicedTotal);
				setAmountText(txt1, owner.getInvoicedMTD());
				
				LinearLayout mtdLayout = (LinearLayout)branchLayout.findViewById(R.id.TEMP_recordedMTD);
				LinearLayout mtdTemplate = (LinearLayout)inf.inflate(R.layout.mtd, null);
				TextView t1 = (TextView) mtdTemplate.findViewById(R.id.MTD_achieved);
				setAmountText(t1, owner.getRecordedMTD().getAchieved());
				
				TextView t2 = (TextView) mtdTemplate.findViewById(R.id.MTD_estimated);
				setAmountText(t2, owner.getRecordedMTD().getEstimatedTarget());
				
				TextView t3 = (TextView) mtdTemplate.findViewById(R.id.MTD_invoiced);
				setAmountText(t3, owner.getRecordedMTD().getInvoicedDebits());
				
				TextView t4 = (TextView) mtdTemplate.findViewById(R.id.MTD_unbilled);
				setAmountText(t4, owner.getRecordedMTD().getUnbilled());
				
				TextView t5 = (TextView) mtdTemplate.findViewById(R.id.MTD_total);
				setAmountText(t5, owner.getRecordedMTD().getTotal());
				
				mtdLayout.addView(mtdTemplate);
				//
				LinearLayout mtdLayout1 = (LinearLayout)branchLayout.findViewById(R.id.TEMP_recordedYTD);
				LinearLayout mtdTemplate1 = (LinearLayout)inf.inflate(R.layout.mtd, null);
				TextView t11 = (TextView) mtdTemplate1.findViewById(R.id.MTD_achieved);
				setAmountText(t11, owner.getRecordedYTD().getAchieved());
				
				TextView t21 = (TextView) mtdTemplate1.findViewById(R.id.MTD_estimated);
				setAmountText(t21, owner.getRecordedYTD().getEstimatedTarget());
				
				TextView t31 = (TextView) mtdTemplate1.findViewById(R.id.MTD_invoiced);
				setAmountText(t31, owner.getRecordedYTD().getInvoicedDebits());
				
				TextView t41 = (TextView) mtdTemplate1.findViewById(R.id.MTD_unbilled);
				setAmountText(t41, owner.getRecordedYTD().getUnbilled());
				
				TextView t51 = (TextView) mtdTemplate1.findViewById(R.id.MTD_total);
				setAmountText(t51, owner.getRecordedYTD().getTotal());
				
				mtdLayout1.addView(mtdTemplate1);
				
				
				bucket.addView(branchLayout);
			}
		}
	  }
	  LayoutInflater inf;
	  void setAmountText(TextView txt, double amt ) {
			NumberFormatter.setAmountText(txt, amt);		
		}
	  
	//@Override
	public void getName() {
		// TODO Auto-generated method stub
		
	}
}
