package com.ldv.financesx.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ldv.financesx.OperationsResults;
import com.ldv.financesx.model.GlobalInfo;
import com.ldv.financesx.model.GlobalStatsDataType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.model.OperatorStats;
import com.ldv.financesx.model.StatType1;
import com.ldv.financesx.model.StatsDataSeriesType2;
import com.ldv.financesx.repository.OpStatsRepository;

@Service
public class OpStatsService {

	@Autowired
    private OpStatsRepository opStatsRepository;
	
	public String repoTestString() {
		String rString = opStatsRepository.testString();
		return rString;
	}
	
	public ArrayList<GlobalStatsDataType> getGlobalStatsSumDebit (boolean isConstraintFilter) {
	    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGlobalStatsSumDebit(isConstraintFilter);
    	   	  	        
    	return globalStatsDataList;	
	} 
	
	public ArrayList<GlobalStatsDataType> getGlobalStatsSumCredit () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGlobalStatsSumCredit();
    	   	  	        
    	return globalStatsDataList;	
	} 
	
	
	public ArrayList<GlobalStatsDataType> getGlobalStatsMoyDebit () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGlobalStatsMoyDebit();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getGlobalStatsMoyCredit () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGlobalStatsMoyCredit();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public StatsDataSeriesType2 getCategoryHistory (String  opCategorieUserChoice) {
    	
		StatsDataSeriesType2 globalStatsDataList = opStatsRepository.getCategoryHistory(opCategorieUserChoice);
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public StatsDataSeriesType2 getCategoryHistoryAverage (String  opCategorieUserChoice, int averageWindows) {
    	
		StatsDataSeriesType2 globalStatsDataList = opStatsRepository.getCategoryHistoryAverage(opCategorieUserChoice, averageWindows);
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getGainsandlosses () {
	    	
	    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGainsandlosses();
	    	   	  	        
	    	return globalStatsDataList;	
	}
	
	
	/**
    * Return cummulative Gains and losses data from database.
    * @param None.
    * @return cummulative Gains and losses per month.
    */	
	public ArrayList<GlobalStatsDataType> getGainsandlossesSum () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getGainsandlossesSum();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getBudget () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getBudget();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	public ArrayList<GlobalStatsDataType> getAverageBudget () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getAverageBudget();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getAverageBudgetConstraint () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getAverageBudgetConstraint();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getReimbursement () {
	    	
	    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getReimbursement();
	    	   	  	        
	    	return globalStatsDataList;	
		}
	
	
	public ArrayList<GlobalStatsDataType> getAverageBudgetWithReimbursements () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getAverageBudgetWithReimbursements();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	public ArrayList<GlobalStatsDataType> getExpensesPerCategoryforPieChart (boolean isConstraintFilter) {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getExpensesPerCategoryforPieChart(isConstraintFilter);
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	/**
     * Get expenses per category vs income chart from database for pie chart display (service level)
     * @return list of expenses per category vs income chart
     */	
	public ArrayList<GlobalStatsDataType> getExpensesPerCategoryVsIncomeForPieChart () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsRepository.getExpensesPerCategoryVsIncomeForPieChart();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	
	
	
	// return the list of categories
	public ArrayList<OperationCategory> getCategoriesList () {
    	
    	ArrayList<OperationCategory> globalStatsDataList = opStatsRepository.getCategoriesList();
    	   	  	        
    	return globalStatsDataList;	
	}
	
	/**
     * return global information
     * @return A GlobalInfo class
     */	

	public GlobalInfo getGlobalInfo () {
						
		return opStatsRepository.getGlobalInfo();	
	}
	
	
	/**
     * trig the upload of new data
     * @input: string that contains the absolute path to the file that contain the new operation data 
     * @return 
	 * @throws Exception 
     */
	public OperationsResults uploadNewData(String uploadfilepath) throws Exception {
		return (opStatsRepository.uploadNewData(uploadfilepath));
	}
	
	
	/**
     * Save new data to csv file (finance.csv)
     * @input:  
     * @return: true/false according to update file result
     */
	public String saveFinance() {
	
	// sauvegarde des nouvelles données dans le fichier des opérations
	return opStatsRepository.saveFinance();

	}
	
	
	/**
     * Update the stats 
     * @input:  
     * @return: 
     */
	public void updateStats() {
	
	// sauvegarde des nouvelles données dans le fichier des opérations
	opStatsRepository.updateStats();

	}
	
	
	// list of operations without association
	public ArrayList<Operation> getOpWithoutAssociation() {			
		return opStatsRepository.getOpWithoutAssociation();
		
	}
	
	
	// total food expense per operator
	public ArrayList<OperatorStats> getFoodSumPerOperator() {	
		return opStatsRepository.getFoodSumPerOperator();
	}
	
	/**
    * Return the statistic for a specific month (all expenses per category).
    * @param month(LocalDate) to get the stats from.
    * @return List of expenses per categories for the month.
    */
	public Map<String, Double> getMonthStats(LocalDate selectedDate) {
		return opStatsRepository.getMonthStats(selectedDate);
	}
	
	
	/**
    * Return the list of Amazon operation (still not associated to the right category).
    * @param None.
    * @return list of Amazon operation.
    */		
	public ArrayList<Operation> getOpBookDataAmazon() {			
		return opStatsRepository.getOpBookDataAmazon();		
	}
	
	
	/**
    * Return a table that contains all the stats of categories.
    * @param none.
    * @return List of stats per categories.
    */
	public ArrayList<StatType1> getCategoriesStatsConstraint() {
		return opStatsRepository.getCategoriesStatsConstraint();
	}
	
		
	/**
     * Get budget data from database (sum of mandatory expenses without "Remboursements" and without "Epargne" )
     * @input: None
     * @return : list of budget expenses per month 
     */
	public ArrayList<GlobalStatsDataType> getBudgetConstraint () {
		return opStatsRepository.getBudgetConstraint();
	}
	
	/**
    * Return the list of operation with errors during reading of CSV file.
    * @param None.
    * @return list of Amazon operation.
    */		
	public ArrayList<String[]> getOperationsWithErrors() {			
			return opStatsRepository.getOperationsWithErrors();		
	}
	
	
	
	
}
