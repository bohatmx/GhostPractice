package com.boha.ghostlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.ghostlibrary.R;
import com.boha.ghostlibrary.TaskActivity;
import com.boha.ghostlibrary.adapters.FeeEarnerAdapter;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MobileUser;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class FeeEarnerListFragment extends Fragment {

    @Override
    public void onAttach(Activity a) {

        super.onAttach(a);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG, "-- onCreateView ............");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_fee_earner_list, container,
                false);

        setFields();
        new GetFeeEarnersTask().execute();

        return view;
    }

    private SharedPreferences sp;
    WebServiceResponse resp;
    Gson gson = new Gson();
    class GetFeeEarnersTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (sp == null) {
                sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            }
            GhostRequestDTO req = new GhostRequestDTO();
            req.setRequestType(GhostRequestDTO.GET_FEE_EARNERS);
            req.setAppID(sp.getInt("appID", 0));
            req.setPlatformID(sp.getInt("platformID", 0));
            req.setUserID(sp.getInt("userID", 0));
            req.setCompanyID(sp.getInt("companyID", 0));
            req.setDeviceID(sp.getString("deviceID", null));
            ;
            try {
                String json = URLEncoder.encode(gson.toJson(req), "UTF-8");
                resp = CommsUtil.getData(Statics.URL + json,
                        ctx);
                mobileUsers = resp.getMobileUsers();
                return resp.getResponseCode();

            } catch (CommsException e) {
                Log.e(LOG, "Error getting matter detail", e);
                return 1;
            } catch (NetworkUnavailableException e) {
                return 999;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer ret) {
            if (ret > 0) {
                ToastUtil.errorToast(ctx, "Error getting list");
                return;
            }
            setList();

        }

    }

    private void setList() {
        if (mobileUsers == null) {
            mobileUsers = new ArrayList<MobileUser>();
        }
        adapter = new FeeEarnerAdapter(ctx, R.layout.fee_earner_item, mobileUsers);
        listView.setAdapter(adapter);
        txtCount.setText("" + mobileUsers.size());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent w = new Intent(ctx, TaskActivity.class);
                w.putExtra("mobileUser", mobileUsers.get(i));
                w.putExtra("matterID", matterID);
                startActivity(w);
            }
        });
    }
    private void setFields() {
        listView = (ListView) view.findViewById(R.id.FEL_list);
        txtCount = (TextView) view.findViewById(R.id.FEL_txtCount);
    }

    public void setMatterID(String matterID) {
        this.matterID = matterID;
    }

    FeeEarnerAdapter adapter;
    List<MobileUser> mobileUsers;
    View view;
    String matterID;
    ListView listView;
    TextView txtCount;
    static final String LOG = "FeeEarnerListFragment";
    Context ctx;
}
