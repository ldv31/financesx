package com.ldv.financesx.model;

public class GlobalStatsDataType {

	private String index = "";
	private double opValue = 0;
	
	
	public GlobalStatsDataType(String index, double opValue) {
		this.index = index;
		this.opValue = opValue;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public double getOpValue() {
		return opValue;
	}
	public void setOpValue(double opValue) {
		this.opValue = opValue;
	} 
		
}
