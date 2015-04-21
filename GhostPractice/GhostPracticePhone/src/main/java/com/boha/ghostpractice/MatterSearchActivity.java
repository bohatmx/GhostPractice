package com.boha.ghostpractice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boha.ghostpractice.adapter.MatterSearchAdapter;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.SharedUtil;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.Util;
import com.boha.ghostpractice.util.bean.CommsException;
import com.boha.ghostpracticephone.R;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MatterSearchActivity extends ActionBarActivity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = getApplicationContext();
		setContentView(R.layout.matter_search);
		setFields();

		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			Util.setGPActionBar(ctx, bar, SharedUtil.getPracticeName(ctx));
		}
	}

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
	}

	long start, end;
	WebServiceResponse resp;

	class SearchTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			//bar.setVisibility(View.VISIBLE);
			splash.setVisibility(View.VISIBLE);
			hideKeyboard();
			start = System.currentTimeMillis();
			btnSearch.setVisibility(View.GONE);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			resp = null;
			try {
				GhostRequestDTO req = new GhostRequestDTO();
				req.setRequestType(GhostRequestDTO.FIND_MATTER);
				req.setAppID(sp.getInt("appID", 0));
				req.setPlatformID(sp.getInt("platformID", 0));
				req.setUserID(sp.getInt("userID", 0));
				req.setCompanyID(sp.getInt("companyID", 0));
				req.setSearchString(searchString);
				req.setDeviceID(sp.getString("deviceID", null));

				String json = URLEncoder.encode(gson.toJson(req));
				resp = CommsUtil.getData(Statics.URL + json,
						getApplicationContext());
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = resp.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start,
						end);
				new ElapsedTask().execute(data);
				matterList = resp.getMatterSearchList();
				return resp.getResponseCode();
			} catch (CommsException e) {
				Log.e(LOG, "Error searching matters", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 999;
			}

		}

		@Override
		protected void onPostExecute(Integer ret) {
			btnSearch.setVisibility(View.VISIBLE);
			if (ret > 0) {
				if (ret == 999) {
					ToastUtil.noNetworkToast(getApplicationContext());
				} else {
					if (resp != null) {
						ToastUtil.errorToast(getApplicationContext(),
								resp.getResponseMessage());
					} else {
						ToastUtil
								.errorToast(getApplicationContext(),
										"Matter Search failed. Please contact GhostPractice support");
					}
				}
				return;
			}
			listView.setVisibility(View.VISIBLE);
			splash.setVisibility(View.GONE);
			setList();
		}

	}

    private void setList() {
        if (matterList != null) {
            //txtCount.setText("" + matterList.size());
        } else {
            matterList = new ArrayList<MatterSearchResultDTO>();
        }
        if (matterList.size() == 0) {
            ToastUtil.toast(getApplicationContext(),
                    "No matters found for search");
            splash.setVisibility(View.VISIBLE);
        } else {
            splash.setVisibility(View.GONE);
        }
        ElapsedTimeUtil.showElapsed(start, end, getApplicationContext());
        MatterSearchAdapter a = new MatterSearchAdapter(
                getApplicationContext(), R.layout.matter_search_item,
                matterList);
        listView.setAdapter(a);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        matterSelected = matterList.get(arg2);
                        Intent i = new Intent(getApplicationContext(),
                                MatterDetailsActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("matter", matterSelected);
                        i.putExtra("data", b);
                        startActivity(i);
                    }
                }
        );
        Animation an = AnimationUtils.makeInAnimation(
                getApplicationContext(), true);
        listView.startAnimation(an);
    }
	class ElapsedData {
		public int activityID;
		public double elapsedSeconds;
	}

	class ElapsedTask extends AsyncTask<ElapsedData, Void, Void> {

		@Override
		protected Void doInBackground(ElapsedData... params) {
			ElapsedData data = params[0];
//			try {
//				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds,
//						getApplicationContext());
//			} catch (CommsException e) {
//				Log.e("MatterSearch", "Problem posting elapsed time");
//			} catch (NetworkUnavailableException e) {
//				e.printStackTrace();
//			}

			return null;
		}

	}

	Gson gson = new Gson();

	void setFields() {
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		bar = (ProgressBar) findViewById(R.id.HEADER_progress);
		editSearch = (EditText) findViewById(R.id.MS_editSearch);
        listView = (ListView)findViewById(R.id.MS_list);
		splash = (ImageView) findViewById(R.id.splash);
		splash.setScaleType(ScaleType.FIT_XY);
		btnSearch = (ImageView) findViewById(R.id.MS_btnSearch);


		// change scaling of splash
		editSearch.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				splash.setVisibility(View.GONE);
				return false;
			}
		});
		editSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (event.getAction() == KeyEvent.ACTION_UP) {
						Log.d(LOG, "#### Enter key has been pressed. ACTION_UP");
						searchString = editSearch.getText().toString().trim();
						if (!searchString.equalsIgnoreCase("")) {
							new SearchTask().execute();
						} else {
							ToastUtil.errorToast(getApplicationContext(),
									"Please enter search text");
							return false;
						}
					}
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						Log.d(LOG,
								"Enter key has been pressed. ACTION_DOWN");
					}
				}
				return false;
			}
		});
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.flashOnce(btnSearch, 300, new Util.UtilAnimationListener() {
					@Override
					public void onAnimationEnded() {
						hideKeyboard();
						String s = editSearch.getText().toString().trim();
						if (s.equalsIgnoreCase("")) {
							editSearch.setText("");
							if (matterList != null) {
								if (matterList.size() == 0) {
									splash.setVisibility(View.VISIBLE);
								}
							} else {
								splash.setVisibility(View.VISIBLE);
							}
							ToastUtil.errorToast(getApplicationContext(),
									"Please enter search text");
							return;
						}
						searchString = editSearch.getText().toString();
						SharedUtil.saveSeachString(searchString, getApplicationContext());
						listView.setVisibility(View.GONE);
						splash.setVisibility(View.VISIBLE);
						new SearchTask().execute();
					}
				});
			}
		});


	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.matter_search, menu);
        mMenu = menu;
        searchString = SharedUtil.getSearchString(getApplicationContext());
        if (searchString != null) {
            editSearch.setText(searchString);
            new SearchTask().execute();
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.reports);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.reports:
                Intent i = new Intent(getApplicationContext(),
                        ReportControllerActivity.class);
                startActivity(i);
                return true;

            case R.id.about:
                getUserData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUserData() {

        AlertDialog.Builder diag = new AlertDialog.Builder(this);
        diag.setMessage(SharedUtil.getUserInfo(getApplicationContext()))
                .setTitle("User Information")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    Menu mMenu;
    ImageView splash;
	MatterSearchResultDTO matterSelected;
	List<MatterSearchResultDTO> matterList;
	ImageView btnSearch;
	EditText editSearch;
	TextView txtHeader, txtCount;
	ProgressBar bar;
    ListView listView;
	SharedPreferences sp;
	String searchString;
	static final String LOG = "MatterSearch";

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
}
