package com.boha.ghostpractice.util;

import android.util.Log;

import com.boha.ghostpractice.data.MatterDTO;
import com.boha.ghostpractice.data.MobileTariffCodeDTO;

public class CalculateFee {

	public static double getFee(MatterDTO matter, MobileTariffCodeDTO code,
			int duration) {
		try {
		Log.i("CalculateFee","Calculate Fee - amount: " + code.getAmount()
				+ " duration: " + duration + " units: " + code.getUnits()
				+ " surcharge: " + matter.getSurchargeFactor());
		} catch (Exception e) {
			//ignore
			return 0;
		}
		double fee = 0;
		if (duration > 0) {
			int elapsed = DivRoundUp(duration, (int) code.getUnits());
			if (code.getUnits() > 0) {
				fee = (code.getAmount() / 60) * code.getUnits() * elapsed;
			} else {
				fee = (code.getAmount() / 60) * elapsed;
			}

		} else {
			if (code.getUnits() > 0) {
				fee = code.getAmount() * code.getUnits();
			} else {
				fee = code.getAmount();
			}

		}

		if (code.isSurchargeApplies()) {
			double surcharge = fee * matter.getSurchargeFactor();
			fee = surcharge;
		}
		Log.i("CalculateFee", "calculated fee is: " + fee);
		return fee;
	}

	private static int DivRoundUp(int dividend, int divisor) {
		if (divisor == 0)
			return dividend;
		if (divisor == -1 && dividend == 0)
			return 0;
		int roundedTowardsZeroQuotient = dividend / divisor;
		boolean dividedEvenly = (dividend % divisor) == 0;
		if (dividedEvenly)
			return roundedTowardsZeroQuotient;

		boolean wasRoundedDown = ((divisor > 0) == (dividend > 0));
		if (wasRoundedDown)
			return roundedTowardsZeroQuotient + 1;
		else
			return roundedTowardsZeroQuotient;
	}
}
