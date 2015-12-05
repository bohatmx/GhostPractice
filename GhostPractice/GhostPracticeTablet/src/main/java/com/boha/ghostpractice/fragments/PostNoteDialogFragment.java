package com.boha.ghostpractice.fragments;

import com.boha.ghostpractice.tablet.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public  class PostNoteDialogFragment extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static PostNoteDialogFragment newInstance(int num) {
    	PostNoteDialogFragment f = new PostNoteDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mNum = getArguments().getInt("num");

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_note, container, false);
        /*View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
                + getNameForNum(mNum));

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.show);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                ((FragmentDialog)getActivity()).showDialog();
            }
        });*/

        return v;
    }
}