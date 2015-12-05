package com.boha.ghostpractice.tablet.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.ghostpractice.tablet.R;

public class StringAdapter extends ArrayAdapter<String> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<String> mList;

	public StringAdapter(Context context, int textViewResourceId,
			List<String> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if (convertView == null) {
			view = mInflater.inflate(mLayoutRes, parent, false);
		} else {
			view = convertView;
		}

		final String item = mList.get(position);

		TextView name = (TextView) view.findViewById(R.id.STRING_owner);
		

		name.setText(item);
		

		return (view);
	}

}
