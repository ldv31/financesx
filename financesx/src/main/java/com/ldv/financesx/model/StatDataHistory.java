package com.ldv.financesx.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatDataHistory {

	private LocalDate monthAndYear;
	private Double value = 0.0;
		
	public StatDataHistory(LocalDate localDate) {
		this.monthAndYear = localDate;
	}

	public void incDataValue (double incValue) {
		value += incValue;
	}

	public LocalDate getMonthAndYear() {
		return monthAndYear;
	}
	
	public String getMonthAndYearShort() {
		DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");	
	    return monthAndYear.format(datePattern);	
	}

	public void setMonthAndYear(LocalDate monthAndYear) {
		this.monthAndYear = monthAndYear;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	
	public boolean CompareYearAndMonth (StatDataHistory compareDate) {
		if ((this.monthAndYear.getYear() == compareDate.monthAndYear.getYear()) &&
				(this.monthAndYear.getMonth() == compareDate.monthAndYear.getMonth()))
			return true;
		else
			return false;
	}
	
		
	
}
