package com.ldv.financesx.model;

import java.time.LocalDate;

/* Objective : collect some basic statistics on ooperation list
 * - date of first operation
 * - date of last operation
 * - number of operations
 */


public class OperationBookStats {
	
	// operation first and last dates
	// operation Book stats data 
 	private LocalDate startOperationDate;
 	private LocalDate endOperationDate;
		
 	 	 		
	public OperationBookStats(LocalDate startOperationDate, LocalDate endOperationDate) {
		this.startOperationDate = startOperationDate;
		this.endOperationDate = endOperationDate;
	}
	
	
	public OperationBookStats() {
	}
	
	public LocalDate getStartOperationDate() {
		return startOperationDate;
	}
	public void setStartOperationDate(LocalDate startOperationDate) {
		this.startOperationDate = startOperationDate;
	}
	public LocalDate getEndOperationDate() {
		return endOperationDate;
	}
	public void setEndOperationDate(LocalDate endOperationDate) {
		this.endOperationDate = endOperationDate;
	}

 	
 	
}
