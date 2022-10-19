package com.ldv.financesx.model;

import java.util.ArrayList;

// use in the following diagram : stats per month for one single category
public class StatsDataSeriesType2 {

	// data series
	ArrayList<GlobalStatsDataType> categoryHistory = new ArrayList<GlobalStatsDataType>();
	
	// category
	String opCategory;
	
	public StatsDataSeriesType2(ArrayList<GlobalStatsDataType> categoryHistory, String opCategory) {
		this.categoryHistory = categoryHistory;
		this.opCategory = opCategory;
	}

	public ArrayList<GlobalStatsDataType> getCategoryHistory() {
		return categoryHistory;
	}

	public void setCategoryHistory(ArrayList<GlobalStatsDataType> categoryHistory) {
		this.categoryHistory = categoryHistory;
	}

	public String getOpCategory() {
		return opCategory;
	}

	public void setOpCategory(String opCategory) {
		this.opCategory = opCategory;
	}
	
	
	
}
