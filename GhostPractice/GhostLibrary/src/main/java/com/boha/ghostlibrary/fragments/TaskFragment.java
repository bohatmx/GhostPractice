package com.boha.ghostlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.boha.ghostlibrary.R;
import com.boha.ghostpractice.data.GhostRequestDTO;
import com.boha.ghostpractice.data.MobileUser;
import com.boha.ghostpractice.data.TaskDTO;
import com.boha.ghostpractice.data.WebServiceResponse;
import com.boha.ghostpractice.util.CommsUtil;
import com.boha.ghostpractice.util.NetworkUnavailableException;
import com.boha.ghostpractice.util.Statics;
import com.boha.ghostpractice.util.ToastUtil;
import com.boha.ghostpractice.util.bean.CommsException;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by aubreyM on 2014/07/25.
 */
public class TaskFragment extends Fragment {

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
        view = inflater.inflate(R.layout.task, container,
                false);

        setFields();
        Bundle b = getArguments();
        if (b != null) {
            mobileUser = (MobileUser) b.getSerializable("mobileUser");
            if (mobileUser != null) {
                txtName.setText(mobileUser.getFirstNames()
                        + " " + mobileUser.getLastName());
            }
        }
        return view;
    }

    private SharedPreferences sp;
    WebServiceResponse resp;
    TaskDTO task;
    Gson gson = new Gson();

    class PostTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (sp == null) {
                sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            }
            GhostRequestDTO req = new GhostRequestDTO();
            req.setRequestType(GhostRequestDTO.ASSIGN_TASK);
            req.setTask(task);
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
                ToastUtil.errorToast(ctx, resp.getResponseMessage());
                return;
            }


        }

    }

    private void setFields() {
        txtName = (TextView) view.findViewById(R.id.TASK_name);
        txtTitle = (TextView) view.findViewById(R.id.HEADER_title);
        btnDate = (Button) view.findViewById(R.id.TASK_btnDate);
        btnPost = (Button) view.findViewById(R.id.TASK_btnPost);
        txtTitle.setText("Assign Task");
        aSwitch = (Switch) view.findViewById(R.id.TASK_switch);
        editDesc = (EditText) view.findViewById(R.id.TASK_desc);
        date = new Date().getTime();
        btnDate.setText(sdf.format(new Date(date)));
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editDesc.getText().toString().isEmpty()) {
                    ToastUtil.toast(ctx, "Please enter task description");
                    return;
                }
                task = new TaskDTO();
                task.setDueDate(date);
                task.setTaskDescription(editDesc.getText().toString());
                task.setUserID(mobileUser.getUserID());
                task.setMatterID(matterID);
                if (aSwitch.isChecked()) {
                    task.setNotifyWhenComplete(true);
                }
                new PostTask().execute();
            }
        });

    }

    public void setMobileUser(MobileUser mobileUser, String matterID) {
        this.mobileUser = mobileUser;
        txtName.setText(mobileUser.getFirstNames()
                + " " + mobileUser.getLastName());
    }

    private void showDateDialog() {

        if (mYear == 0) {
            Calendar cal = GregorianCalendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dp = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                date = cal.getTimeInMillis();
                btnDate.setText(sdf.format(cal.getTime()));
            }
        }, mYear, mMonth, mDay);
        dp.setYearRange(2013, 2037);
        dp.show(getFragmentManager(), "datP");

    }

    private static final Locale loc = Locale.getDefault();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", loc);
    private int mYear, mMonth, mDay, mHour, mMinute;
    long date;
    MobileUser mobileUser;
    View view;
    Switch aSwitch;
    TextView txtName, txtTitle;
    Button btnDate, btnPost;
    EditText editDesc;
    String matterID;
    static final String LOG = "TaskFragment";
    Context ctx;
}
