package com.ldv.financesx.model;


public class OperationCategory {

		private String name;
		CategoryType type;
		boolean constraint;
		// define the windows size for calculating average values 
		String averageWindowsSize;
	
	
	public OperationCategory() {
	}


	public OperationCategory(String name, CategoryType type, boolean constraint) {
		this.name = name;
		this.type = type;
		this.constraint = constraint;

	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public CategoryType getType() {
		return type;
	}


	public void setType(CategoryType type) {
		this.type = type;
	}


	public boolean isConstraint() {
		return constraint;
	}


	public String getAverageWindowsSize() {
		return averageWindowsSize;
	}


	public void setAverageWindowsSize(String averageWindowsSize) {
		this.averageWindowsSize = averageWindowsSize;
	}
	
	
	
}
