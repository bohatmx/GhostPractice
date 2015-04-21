package com.boha.ghostpractice.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class Statics {

	//public static final String URL = "http://10.0.0.239:8080/GhostPractice-war/ghost?json=";
	//
	public static final String URL = "http://gpmobile.ghostpractice.com:7180/GhostPractice-war/ghost?json=";

	//public const string CONSOLE_URL = "http://gpmobile.ghostpractice.com:7180/GhostPractice-war/ghost?json=";
	public static final String HEADER_FONT = "BordeauxBlack Regular.ttf";
	public static final String DROID_FONT = "DroidSans.ttf";
	public static final String DROID_FONT_BOLD = "DroidSans-Bold.ttf";
	//http://69.89.1.149:7148/

	public static void setRomanFontLight(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Neuton-Light.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoFontBoldCondensed(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-BoldCondensed.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoFontRegular(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Regular.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoFontLight(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Light.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoFontBold(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Bold.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoItalic(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Italic.ttf");
		txt.setTypeface(font);
	}

	public static void setRobotoRegular(Context ctx, TextView txt) {
		Typeface font = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Regular.ttf");
		txt.setTypeface(font);
	}
	

}
