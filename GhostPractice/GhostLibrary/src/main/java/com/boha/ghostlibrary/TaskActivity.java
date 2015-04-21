package com.boha.ghostlibrary;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.boha.ghostlibrary.fragments.TaskFragment;
import com.boha.ghostpractice.data.MobileUser;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class TaskActivity extends FragmentActivity {


    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_task);
        taskFragment = (TaskFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);

        mobileUser = (MobileUser) getIntent().getSerializableExtra("mobileUser");
        matterID = getIntent().getStringExtra("matterID");
        taskFragment.setMobileUser(mobileUser,matterID);

    }
    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    TaskFragment taskFragment;
    MobileUser mobileUser;
    String matterID;
}
