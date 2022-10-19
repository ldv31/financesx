package com.ldv.financesx;

import java.util.ArrayList;

import com.ldv.financesx.model.Operation;

/* Objective: this call is a place holder for results of calculation of list of opération
 * 
 */
public class OperationsResults {
	
	// result of the opération
	private ErrorCodeAndMessage result;
	
	// list of operations resulting from the caluclation
	private ArrayList<Operation> opBookOperationData;

	public OperationsResults(ErrorCodeAndMessage result, ArrayList<Operation> opBookOperationData) {
		this.result = result;
		this.opBookOperationData = opBookOperationData;
	}
	
	public OperationsResults(ErrorCodeAndMessage result) {
		this.result = result;
	}
	
	public OperationsResults() {
	}

	public ErrorCodeAndMessage getResult() {
		return result;
	}

	public void setResult(ErrorCodeAndMessage result) {
		this.result = result;
	}

	public ArrayList<Operation> getOpBookOperationData() {
		return opBookOperationData;
	}

	public void setOpBookOperationData(ArrayList<Operation> opBookOperationData) {
		this.opBookOperationData = opBookOperationData;
	}
	
	
	
}
