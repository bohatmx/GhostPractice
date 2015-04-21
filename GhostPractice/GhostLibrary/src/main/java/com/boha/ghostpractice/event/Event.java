package com.boha.ghostpractice.event;

public class Event {
	//
	public static final String REPORT_PAGE_REQUESTED = "com.boha.REPORT_PAGE_REQUESTED";

	private int pageNumber;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public EventDispatcher getTarget() {
		return target;
	}

	public void setTarget(EventDispatcher target) {
		this.target = target;
	}

	public String type;
	public EventDispatcher target;

	public Event(String type) {
		this.type = type;
	}
}