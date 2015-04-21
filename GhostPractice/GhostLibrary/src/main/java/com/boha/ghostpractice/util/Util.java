package com.boha.ghostpractice.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.ghostlibrary.R;

/**
 * Created by aubreyM on 15/04/21.
 */
public class Util {
    public interface UtilAnimationListener {
         void onAnimationEnded();
    }
    public static void setGPActionBar(final Context ctx, ActionBar actionBar, String text) {
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater)
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_logo, null);
        TextView txt = (TextView) v.findViewById(R.id.ACTION_BAR_text);
        final ImageView logo = (ImageView) v.findViewById(R.id.ACTION_BAR_logo);
        txt.setText(text);
        //
        logo.setImageDrawable(ctx.getResources().getDrawable(R.drawable.logo2_29));
        actionBar.setCustomView(v);
        actionBar.setTitle("");


    }
    public static void setCustomActionBar(final Context ctx, ActionBar actionBar, String text,
                                          Drawable image, final int logoInt) {
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater)
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_logo, null);
        TextView txt = (TextView) v.findViewById(R.id.ACTION_BAR_text);
        final ImageView logo = (ImageView) v.findViewById(R.id.ACTION_BAR_logo);
        txt.setText(text);
        //
        logo.setImageDrawable(image);
        actionBar.setCustomView(v);
        actionBar.setTitle("");


    }

    public static void flashOnce(View view, long duration, final UtilAnimationListener listener) {
        try {
            ObjectAnimator an = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
            an.setRepeatMode(ObjectAnimator.REVERSE);
            an.setDuration(duration);
            an.setInterpolator(new AccelerateDecelerateInterpolator());
            an.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null)
                        listener.onAnimationEnded();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            an.start();
        } catch (Exception e) {
            if (listener != null) {
                listener.onAnimationEnded();
            }
        }

    }



}
