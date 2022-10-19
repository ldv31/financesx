package com.ldv.financesx.model;

import java.time.LocalDate;

import com.ldv.financesx.OperationStatistics;

/**
* Contains the global information relative to the input data (cvsfiles)
*
* @author  Ludovic Duval
* @version 1.0
* @since   2022-10-16 
*/

public class GlobalInfo {

	// contains infos from which global info are extracted
	OperationStatistics opStats;
	
	// text for reporting info
	private final String startDateTxt = "Date de début";  
	private final String endDateTxt = "Date de fin";
	private final String analysisDurationTxt = "Durée d'analyse (jours)";
	private final String debitWithoutAssociationTxt = "Débit sans association (€)";
	private final String creditWithoutAssociationTxt = "Crédit sans association (€)";
	private final String operationsNumberTxt = "Nombre d'opérations";

	// data for reporting info
	private LocalDate startDate;  
	private LocalDate endDate;
	private int analysisDuration;
	private int debitWithoutAssociation;
	private int creditWithoutAssociation;
	private int operationsNumber;
	
	
	// this constructor call the function that fills global info data
	public GlobalInfo(OperationStatistics opStats) {
		this.opStats = opStats;
		
		// update the global info
		updateGlobalInfo();
	}


	/**
     * fills global info data.
     * @param none 
     * @return none.
     */
	public void updateGlobalInfo() {		
		startDate = opStats.getOpBook().getOperationBookStats().getStartOperationDate();  
		endDate = opStats.getOpBook().getOperationBookStats().getEndOperationDate();
		
		analysisDuration = opStats.getHistoryDaylength();
		debitWithoutAssociation = (int)opStats.getSumValueDebit();
		creditWithoutAssociation = (int)opStats.getSumValueCredit();
		operationsNumber = opStats.getOpBook().getaString().size();
	}


	public OperationStatistics getOpStats() {
		return opStats;
	}


	public void setOpStats(OperationStatistics opStats) {
		this.opStats = opStats;
	}


	public LocalDate getStartDate() {
		return startDate;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public LocalDate getEndDate() {
		return endDate;
	}


	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public int getAnalysisDuration() {
		return analysisDuration;
	}


	public void setAnalysisDuration(int analysisDuration) {
		this.analysisDuration = analysisDuration;
	}


	public int getDebitWithoutAssociation() {
		return debitWithoutAssociation;
	}


	public void setDebitWithoutAssociation(int debitWithoutAssociation) {
		this.debitWithoutAssociation = debitWithoutAssociation;
	}


	public int getCreditWithoutAssociation() {
		return creditWithoutAssociation;
	}


	public void setCreditWithoutAssociation(int creditWithoutAssociation) {
		this.creditWithoutAssociation = creditWithoutAssociation;
	}


	public String getStartDateTxt() {
		return startDateTxt;
	}


	public String getEndDateTxt() {
		return endDateTxt;
	}


	public String getAnalysisDurationTxt() {
		return analysisDurationTxt;
	}


	public String getDebitWithoutAssociationTxt() {
		return debitWithoutAssociationTxt;
	}


	public String getCreditWithoutAssociationTxt() {
		return creditWithoutAssociationTxt;
	}


	public int getOperationsNumber() {
		return operationsNumber;
	}


	public void setOperationsNumber(int operationsNumber) {
		this.operationsNumber = operationsNumber;
	}


	public String getOperationsNumberTxt() {
		return operationsNumberTxt;
	}	

}
