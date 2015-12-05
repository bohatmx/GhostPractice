package com.boha.ghostpractice.tablet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.ElapsedTimeUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

public class RegistrationActivity extends Activity {

	String activationCode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setFields();
        ///saveTestPreferences();
    }
    @Override
	protected void onResume() {
		super.onResume();
		if (isUserProvisioned()) {
			startSearch();
			finish();
		}
	}
    private void setFields() {
    	
    	sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		bar = (ProgressBar) findViewById(R.id.MAIN_bar);
		editCode = (EditText) findViewById(R.id.MAIN_activation);
		btnSubmit = (Button) findViewById(R.id.MAIN_submit);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			//@Override
			public void onClick(View v) {
				if (editCode.getText().toString().equalsIgnoreCase("")) {
					ToastUtil.errorToast(getApplicationContext(),
							"Please enter activation code");
					return;
				}
				btnSubmit.setEnabled(false);
				activationCode = editCode.getText().toString();
				int appID = sp.getInt("appID", 0);
				if (appID == 0) {
					new AppPlatformTask().execute();
				} else {
					new ProvisionTask().execute();
				}

			}
		});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_registration, menu);
        return true;
    }

    class AppPlatformTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.GET_APP_PLATFORM_IDS);
			req.setAppName("GhostPractice Mobile");
			req.setPlatformName("Android Tablet");
			
			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				response = CommsUtil.getData(Statics.URL + json, getApplicationContext());
			} catch (CommsException e) {
				Log.e(LOG, "Problem provisioning tablet", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 99;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
			return 0;
		}

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Integer ret) {
			bar.setVisibility(View.GONE);
			if (ret == 99) {
				ToastUtil.noNetworkToast(getApplicationContext());
				return;
			}
			if (ret == 0 && response.getResponseCode() == 0) {
				saveAppPreferences();
				new ProvisionTask().execute();

			} else {

				if (response != null) {
					ToastUtil.errorToast(getApplicationContext(),
							response.getResponseMessage());
				} else {
					ToastUtil.errorToast(getApplicationContext(),
							"Device Provisioning Failed");
				}
			}
		}
	}
	long start, end;
	class ProvisionTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			GhostRequestDTO req = new GhostRequestDTO();
			req.setRequestType(GhostRequestDTO.PROVISION_NEW_USER);
			req.setActivationCode(activationCode);
			req.setAppID(sp.getInt("appID", 0));
			req.setPlatformID(sp.getInt("platformID", 0));
			
			try {
				String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
				response = CommsUtil.getData(Statics.URL + json, getApplicationContext());
				end = System.currentTimeMillis();
				ElapsedData data = new ElapsedData();
				data.activityID = response.getActivityID();
				data.elapsedSeconds = ElapsedTimeUtil.getElapsedSeconds(start, end);
				new ElapsedTask().execute(data);
				
			} catch (CommsException e) {
				Log.e(LOG, "Problem provisioning device", e);
				return 1;
			} catch (NetworkUnavailableException e) {
				return 99;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 2;
			}
			return 0;
		}

		@Override
		protected void onPreExecute() {
			bar.setVisibility(View.VISIBLE);
			start = System.currentTimeMillis();
		}

		@Override
		protected void onPostExecute(Integer ret) {
			vb.vibrate(50);
			bar.setVisibility(View.GONE);
			if (ret == 99) {
				ToastUtil.noNetworkToast(getApplicationContext());
				return;
			}
			if (ret == 0 && response.getResponseCode() == 0) {
				Log.i(LOG, "#### Backend responded, Authentication success! ");
				saveUserPreferences();
				ToastUtil.toast(getApplicationContext(), "Device Provisioning Successful. Welcome to GhostPractice Mobile!");
				startSearch();
			} else {
				
				btnSubmit.setEnabled(true);
				if (response != null) {
					Log.e(LOG, "@@@@ Backend responded, Authentication failed: " + response.getResponseMessage());
					ToastUtil.errorToast(getApplicationContext(),
							response.getResponseMessage());
				} else {
					Log.e(LOG, "@@@@ Backend responded, Authentication failed, response is NULL! ");
					ToastUtil.errorToast(getApplicationContext(),
							"Device Provisioning Failed, please try again");
					
				}
			}
		}
	}
	void startSearch() {
		Intent i = new Intent(getApplicationContext(),
				MatterListActivity.class);
		startActivity(i);
	}

	boolean isUserProvisioned() {
		boolean yes = false;

		int userID = sp.getInt("userID", 0);
		if (userID > 0) {
			yes = true;
		}

		return yes;
	}

	void saveUserPreferences() {
		Editor ed = sp.edit();
		ed.putInt("userID", response.getUser().getUserID());
		ed.putString("userName", response.getUser().getUserName());
		ed.putString("deviceID", response.getDeviceID());
		if (response.getUser().getCellphone() != null) {
			ed.putString("cellphone", response.getUser().getCellphone());
		}
		if (response.getUser().getEmail() != null) {
			ed.putString("email", response.getUser().getEmail());
		}
		if (response.getUser().getCompany() != null) {
			ed.putInt("companyID", response.getUser().getCompany().getCompanyID());
		}
		ed.commit();
		Log.i(LOG, "#### GP User preferences have been stored. userName: " +
				response.getUser().getUserName());
	}

	void saveAppPreferences() {
		Editor ed = sp.edit();
		ed.putInt("appID", response.getApp().getAppID());
		ed.putInt("platformID", response.getPlatform().getPlatformID());
		ed.commit();
		Log.i(LOG, "#### GP App preferences have been stored. appID: " + response.getApp().getAppID());
	}
	
	//TODO - test code. Remove when done
	private void saveTestPreferences() {
		Editor ed = sp.edit();
		ed.putInt("userID", 336);
		ed.putString("userName", "Test Admin User");
		ed.putString("deviceID", "e5579a77-cd69-4f0f-a6a2-c9db1c05b98e");
		ed.putString("cellphone", "0828013722");
		ed.putString("email", "malengadev@gmail.com");		
		ed.putInt("companyID", 17);
		ed.putInt("appID", 1);
		ed.putInt("platformID", 4);
		
		ed.commit();
	}
	class ElapsedData {
		public int activityID;
		public double elapsedSeconds;
	}
	class ElapsedTask extends AsyncTask<ElapsedData, Void, Void> {

		@Override
		protected Void doInBackground(ElapsedData... params) {
			ElapsedData data = params[0];
			try {
				CommsUtil.postElapsedTime(data.activityID, data.elapsedSeconds, getApplicationContext());
			} catch (CommsException e) {
				Log.e("Provisioner", "Problem posting elapsed time");
			} catch (NetworkUnavailableException e) {
				
			}
			
			return null;
		}
		
	}
	Gson gson = new Gson();
	WebServiceResponse response;
    EditText editCode;
    ProgressBar bar;
    Button btnSubmit;
    Vibrator vb; 
    SharedPreferences sp;
    static final String LOG = RegistrationActivity.class.getName();
}
