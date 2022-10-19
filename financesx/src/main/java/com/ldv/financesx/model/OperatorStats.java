package com.ldv.financesx.model;

// Contains information about one operator (sales, institution, job, ) 
// that contributes to one category (expense or income) 
public class OperatorStats {

	// Name of the operator
	private String name;
	private double consolidatedSum;
	
	public OperatorStats(String name, Double consolidatedSum) {
		this.name = name;
		this.consolidatedSum = consolidatedSum;
	}

	// increment sum value
	public void addToSum(double value) {
		consolidatedSum += value;
	}
	
	
	//Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getConsolidatedSum() {
		return consolidatedSum;
	}

	public void setConsolidatedSum(double consolidatedSum) {
		this.consolidatedSum = consolidatedSum;
	}
	
	
	
}


