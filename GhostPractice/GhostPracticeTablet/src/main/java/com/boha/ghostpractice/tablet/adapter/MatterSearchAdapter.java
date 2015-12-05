package com.boha.ghostpractice.tablet.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.ghostpractice.data.MatterSearchResultDTO;
import com.boha.ghostpractice.tablet.R;

public class MatterSearchAdapter extends ArrayAdapter<MatterSearchResultDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<MatterSearchResultDTO> mList;

	public MatterSearchAdapter(Context context, int textViewResourceId,
			List<MatterSearchResultDTO> list) {
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

		final MatterSearchResultDTO item = mList.get(position);

		TextView name = (TextView) view.findViewById(R.id.MSI_title);
		TextView desc = (TextView) view.findViewById(R.id.MSI_detail);
		TextView owner = (TextView) view.findViewById(R.id.MSI_owner);

		name.setText(item.getMatterName());
		desc.setText(item.getClientName());
		owner.setText(item.getCurrentOwner());

//		int x = position % 2;
//		if (x > 0) {
//			view.setBackgroundColor(getContext().getResources().getColor(
//					R.color.grey_light));
//		} else {
//			view.setBackgroundColor(getContext().getResources().getColor(
//					R.color.white));
//		}

		return (view);
	}

}
