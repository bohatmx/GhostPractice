package com.boha.ghostpractice.tablet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.fragments.PostNoteDialogFragment;
import com.boha.ghostpractice.tablet.interfaces.Constants;
import com.boha.ghostpractice.tablet.interfaces.MatterSelectedListener;
import com.boha.ghostpractice.tablet.interfaces.PostButtonListener;
import com.boha.ghostpractice.util.ToastUtil;

import java.io.Serializable;
import java.util.List;

public class MatterListActivity extends FragmentActivity implements
		MatterSelectedListener, PostButtonListener {

	private boolean mTwoPane;
	private MatterListFragment matterListFragment;
	private MatterDetailFragment matterDetailFragment;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matter_twopane);

		mTwoPane = true;
		matterListFragment = (MatterListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.matter_list);
		matterDetailFragment = (MatterDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.matter_detail);
		matterDetailFragment.setListener(this);
		matterListFragment.setActivateOnItemClick(true);
		matterListFragment.setListener(this);
		if (savedInstanceState != null) {
			Log.d(LOG, "###### savedInstanceState is not null");
			MatterSearchResultDTO x = (MatterSearchResultDTO) savedInstanceState
					.getSerializable(Constants.SEARCH_RESULT);
			MatterDTO matter = (MatterDTO) savedInstanceState
					.getSerializable(Constants.MATTER);
			matterDetailFragment.setMatterDetail(matter, x);
			matterListFragment
					.setMatterList((List<MatterSearchResultDTO>) savedInstanceState
							.getSerializable(Constants.MATTER_LIST));
		}

	}

	private void showPostNoteDialog() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		DialogFragment newFragment = new PostNoteDialogFragment();
		newFragment.show(ft, "postNoteDialog");
	}

	public static final int POST_FEE = 1;
	public static final int POST_UNBILLABLE = 2;
	public static final int POST_NOTE = 3;
	public static final String MSG = "Please select matter or wait for details to arrive";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ML_reports:
			Log.i(LOG, "##### reports selected..kick off reportlist activity");
			Intent i = new Intent(getApplicationContext(),
					ReportControllerActivity.class);
			startActivity(i);
			return true;
		case R.id.ML_postFee:
			if (matterListFragment.getMatterSelected() == null
					|| matterDetailFragment.getMatterDetail() == null) {
				Log.i(LOG, "##### postFee selected..matter is NULL ");
				ToastUtil.errorToast(getApplicationContext(), MSG);
			} else {
				Intent i2 = new Intent(getApplicationContext(),
						PostActivity.class);
				// set matter in post activity
				Bundle b = new Bundle();
				b.putSerializable(Constants.SEARCH_RESULT,
						matterListFragment.matterSelected);
				b.putSerializable(Constants.MATTER,
						matterDetailFragment.getMatterDetail());
				b.putBoolean(Constants.IS_FEE, true);
				i2.putExtra(Constants.DATA_BUNDLE, b);
				startActivity(i2);
			}
			return true;
		case R.id.ML_postUnbillable:
			if (matterListFragment.getMatterSelected() == null
					|| matterDetailFragment.getMatterDetail() == null) {
				Log.i(LOG, "##### postUnbillable selected..matter is NULL ");
				ToastUtil.errorToast(getApplicationContext(), MSG);

			} else {
				Intent i2 = new Intent(getApplicationContext(),
						PostActivity.class);
				// set matter in post activity
				Bundle b = new Bundle();
				b.putSerializable(Constants.SEARCH_RESULT,
						matterListFragment.matterSelected);
				b.putSerializable(Constants.MATTER,
						matterDetailFragment.getMatterDetail());
				b.putBoolean(Constants.IS_FEE, false);

				i2.putExtra(Constants.DATA_BUNDLE, b);
				startActivity(i2);
			}

			return true;
		case R.id.ML_postNote:
			if (matterListFragment.getMatterSelected() == null
					|| matterDetailFragment.getMatterDetail() == null) {
				Log.i(LOG, "##### postNote selected..matter is NULL ");
				ToastUtil.errorToast(getApplicationContext(), MSG);

			} else {
				Intent i2 = new Intent(getApplicationContext(),
						PostActivity.class);
				// set matter in post activity
				Bundle b = new Bundle();
				b.putSerializable(Constants.SEARCH_RESULT,
						matterListFragment.matterSelected);
				b.putSerializable(Constants.MATTER,
						matterDetailFragment.getMatterDetail());
				b.putBoolean(Constants.IS_FEE, false);
				b.putBoolean(Constants.IS_NOTE, true);
				i2.putExtra(Constants.DATA_BUNDLE, b);
				startActivity(i2);
			}

			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.matter_list, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG, "###### onSaveInstanceState...saving state");
		outState.putSerializable(Constants.SEARCH_RESULT,
				matterDetailFragment.getSearchResult());
		outState.putSerializable(Constants.MATTER_LIST,
				(Serializable) matterListFragment.getMatterList());
		outState.putSerializable(Constants.MATTER,
				matterDetailFragment.getMatterDetail());
	}

	public void onMatterSelected(MatterSearchResultDTO matter) {

		Log.d(LOG, "onMatterSelected matter: " + matter.getMatterName());

		if (mTwoPane) {
			matterDetailFragment = (MatterDetailFragment) getSupportFragmentManager()
					.findFragmentById(R.id.matter_detail);
			matterDetailFragment.setSearchResult(matter);

		} else {
			Intent detailIntent = new Intent(this, MatterDetailActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("matter", matter);
			detailIntent.putExtra(Constants.DATA_BUNDLE, b);
			startActivity(detailIntent);
		}

	}

	public void onMatterSearchError(String message) {
		// TODO Auto-generated method stub

	}

	static final String LOG = MatterListActivity.class.getCanonicalName();

	public void onPostFeeRequested() {

		Intent i = new Intent(getApplicationContext(), PostActivity.class);
		Bundle b = new Bundle();
		b.putSerializable(Constants.MATTER,
				matterDetailFragment.getMatterDetail());
		b.putSerializable(Constants.SEARCH_RESULT,
				matterDetailFragment.getSearchResult());
		b.putBoolean(Constants.IS_FEE, true);
		i.putExtra(Constants.DATA_BUNDLE, b);
		startActivity(i);

	}

	public void onPostNoteRequested() {
		// TODO Auto-generated method stub

	}

	public void onUnbillableRequested() {
		// TODO Auto-generated method stub

	}
}
