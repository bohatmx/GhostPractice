package com.boha.ghostlibrary;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.boha.ghostlibrary.fragments.FeeEarnerListFragment;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class FeeEarnerListActivity extends FragmentActivity {


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_fee_earner_list);
        feeEarnerListFragment = (FeeEarnerListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        String matterID = getIntent().getStringExtra("matterID");
        feeEarnerListFragment.setMatterID(matterID);

    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    FeeEarnerListFragment feeEarnerListFragment;
}
