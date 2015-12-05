package com.boha.ghostpractice.tablet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.tablet.interfaces.Constants;
import com.boha.ghostpractice.tablet.interfaces.PostListener;

public class PostActivity extends FragmentActivity implements PostListener {

	private boolean mTwoPane;
	private PostFeeFragment postFeeFragment;
	private MatterDetailFragment matterDetailFragment;
	private PostNoteFragment noteFragment;
	private MatterDTO matter;
	private MatterSearchResultDTO searchResult;
	private boolean isFee;
	private Context ctx;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_twopane);
		ctx = getApplicationContext();

		matterDetailFragment = (MatterDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.matter_detail);
		matterDetailFragment.setPostButtonOff();
		noteFragment = (PostNoteFragment) getSupportFragmentManager()
				.findFragmentById(R.id.post_note);
		postFeeFragment = (PostFeeFragment) getSupportFragmentManager()
				.findFragmentById(R.id.post_fee);

		if (savedInstanceState != null) {
			Log.d(LOG,
					"###### savedInstanceState is not null... restoring state");
			searchResult = (MatterSearchResultDTO) savedInstanceState
					.getSerializable(Constants.SEARCH_RESULT);
			matter = (MatterDTO) savedInstanceState
					.getSerializable(Constants.MATTER);
			matterDetailFragment.setMatterDetail(matter, searchResult);
			if (postFeeFragment != null) {
				postFeeFragment.setFee(savedInstanceState
						.getBoolean(Constants.IS_FEE));
				postFeeFragment.setMatter(matter);
			}
			if (noteFragment != null) {
				noteFragment.setMatter(matter);
			}
		}

	}

	boolean isNote;

	@Override
	protected void onResume() {
		Log.d(LOG, "$$$$$$ onResume ..........");
		Bundle b = getIntent().getBundleExtra(Constants.DATA_BUNDLE);
		if (b != null) {
			matter = (MatterDTO) b.getSerializable(Constants.MATTER);
			searchResult = (MatterSearchResultDTO) b
					.getSerializable(Constants.SEARCH_RESULT);
			isFee = b.getBoolean(Constants.IS_FEE);
			isNote = b.getBoolean(Constants.IS_NOTE);

			matterDetailFragment.setMatterDetail(matter, searchResult);
			postFeeFragment.setFee(isFee);
			postFeeFragment.setMatter(matter);
			postFeeFragment.setListener(this);
			//
			noteFragment.setMatter(matter);
			noteFragment.setListener(this);

			//
			if (isNote) {			
				if (postFeeFragment != null) {
					FragmentTransaction tx = getSupportFragmentManager()
							.beginTransaction();
					tx.hide(postFeeFragment);
					tx.commit();
				}
			} else {
				postFeeFragment.getTariffCodes();
				if (noteFragment != null) {
					FragmentTransaction tx = getSupportFragmentManager()
							.beginTransaction();
					tx.hide(noteFragment);
					tx.commit();
				}
			}

		}
		super.onResume();
	}

	private void setMenuOptionsOnNote() {
		menu.getItem(0).setVisible(true);
		menu.getItem(1).setVisible(true);
		menu.getItem(2).setVisible(false);
	}

	private void setMenuOptionsOnFee() {
		menu.getItem(0).setVisible(false);
		menu.getItem(1).setVisible(true);
		menu.getItem(2).setVisible(true);
	}

	private void setMenuOptionsOnUnbillable() {
		menu.getItem(0).setVisible(true);
		menu.getItem(1).setVisible(false);
		menu.getItem(2).setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.POST_MENU_reports:
			Intent i = new Intent(getApplicationContext(),
					ReportControllerActivity.class);
			startActivity(i);
			return true;

		case R.id.POST_MENU_postFee:

			FragmentTransaction tx2a = getSupportFragmentManager()
					.beginTransaction();
			tx2a.hide(noteFragment);
			setMenuOptionsOnFee();
			tx2a.commit();
			//
			FragmentTransaction txa = getSupportFragmentManager()
					.beginTransaction();
			postFeeFragment.setFee(true);
			postFeeFragment.setHeader();
			postFeeFragment.setMatter(matter);
			postFeeFragment.getTariffCodes();
			txa.show(postFeeFragment);
			item.setVisible(false);
			txa.commit();
			return true;

		case R.id.POST_MENU_postUnbillable:
			FragmentTransaction tx2aa = getSupportFragmentManager()
					.beginTransaction();
			tx2aa.hide(noteFragment);
			setMenuOptionsOnUnbillable();
			tx2aa.commit();
			//
			FragmentTransaction txaa = getSupportFragmentManager()
					.beginTransaction();
			postFeeFragment.setFee(false);
			postFeeFragment.setHeader();
			postFeeFragment.setMatter(matter);
			postFeeFragment.getTariffCodes();
			txaa.show(postFeeFragment);
			item.setVisible(false);
			txaa.commit();
			return true;

		case R.id.POST_MENU_postNote:
			FragmentTransaction tx = getSupportFragmentManager()
					.beginTransaction();
			tx.hide(postFeeFragment);
			tx.commit();
			setMenuOptionsOnNote();
			FragmentTransaction tx2 = getSupportFragmentManager()
					.beginTransaction();
			noteFragment.setMatter(matter);
			tx2.show(noteFragment);
			item.setVisible(false);
			tx2.commit();
			return true;
		case R.id.POST_MENU_search:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private Menu menu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(LOG, "$$$$$$ creating menu: posting_menu.xml .......");
		getMenuInflater().inflate(R.menu.posting_menu, menu);
		this.menu = menu;
		if (isNote) {
			setMenuOptionsOnNote();
			return true;
		}

		if (isFee) {
			setMenuOptionsOnFee();
		} else {
			setMenuOptionsOnUnbillable();
		}

		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(LOG, "###### onSaveInstanceState...saving state");
		outState.putSerializable("searchResult",
				matterDetailFragment.getSearchResult());
		outState.putSerializable("matter",
				matterDetailFragment.getMatterDetail());
		outState.putSerializable(Constants.IS_FEE, isFee);
	}

	public void onPostComplete(String message) {
		matterDetailFragment.refreshMatterDetail();

	}

	static final String LOG = PostActivity.class.getCanonicalName();

}
