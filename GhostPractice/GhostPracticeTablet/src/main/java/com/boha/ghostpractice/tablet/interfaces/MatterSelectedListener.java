package com.boha.ghostpractice.tablet.interfaces;

import com.boha.ghostpractice.data.MatterSearchResultDTO;

public interface MatterSelectedListener {

	public void onMatterSelected(MatterSearchResultDTO matter);
	public void onMatterSearchError(String message);
}
