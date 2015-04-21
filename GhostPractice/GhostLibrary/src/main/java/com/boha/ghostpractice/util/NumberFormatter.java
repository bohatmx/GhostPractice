package com.boha.ghostpractice.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.widget.EditText;
import android.widget.TextView;

public class NumberFormatter {
	static Locale deviceLocale;
	static NumberFormat nf = NumberFormat.getInstance();
	static NumberFormat nf2 = NumberFormat.getInstance();
	
	public static void setAmountText(TextView txt, double amt) {
		if (deviceLocale == null) {
			Locale[] deviceLocaleList = Locale.getAvailableLocales();
			for (int i = 0; i < deviceLocaleList.length; i++) {
				if (deviceLocaleList[i].getCountry().equalsIgnoreCase("GB")) {
					deviceLocale = deviceLocaleList[i];
					nf = NumberFormat.getInstance(deviceLocale);
					if (nf instanceof DecimalFormat) {
						((DecimalFormat) nf)
								.setDecimalSeparatorAlwaysShown(true);
						((DecimalFormat) nf).setMaximumFractionDigits(2);
						((DecimalFormat) nf).setMinimumFractionDigits(2);
					}
					break;
				}
			}
		}

		txt.setText(nf.format(amt));
	}
	public static void setAmountText(EditText txt, double amt) {
		if (deviceLocale == null) {
			Locale[] deviceLocaleList = Locale.getAvailableLocales();
			for (int i = 0; i < deviceLocaleList.length; i++) {
				if (deviceLocaleList[i].getCountry().equalsIgnoreCase("GB")) {
					deviceLocale = deviceLocaleList[i];
					nf = NumberFormat.getInstance(deviceLocale);
					if (nf instanceof DecimalFormat) {
						((DecimalFormat) nf)
								.setDecimalSeparatorAlwaysShown(true);
						((DecimalFormat) nf).setMaximumFractionDigits(2);
						((DecimalFormat) nf).setMinimumFractionDigits(2);
					}
					break;
				}
			}
		}

		txt.setText(nf.format(amt));
	}
	
	public static void setNumberText(TextView txt, int amt) {
		txt.setText(df.format(amt));
	}
	static DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###");
}



