package com.boha.ghostlibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.ghostlibrary.R;
import com.boha.ghostpractice.data.MobileUser;

import java.util.List;

public class FeeEarnerAdapter extends ArrayAdapter<MobileUser> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<MobileUser> mList;
    private Context ctx;

	public FeeEarnerAdapter(Context context, int textViewResourceId,
                            List<MobileUser> list) {
		super(context, textViewResourceId, list);
        ctx = context;
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

		final MobileUser item = mList.get(position);

		TextView name = (TextView) view.findViewById(R.id.FEI_txtName);
		TextView number = (TextView) view.findViewById(R.id.FEI_txtNumber);

		name.setText(item.getFirstNames() + " " + item.getLastName());
		number.setText("" + (position + 1));

		int x = position % 2;
		if (x > 0) {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.grey_light));
		} else {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.white));
		}

        animate(view);
		return (view);
	}
    private void animate(View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        view.startAnimation(a);
    }

}
