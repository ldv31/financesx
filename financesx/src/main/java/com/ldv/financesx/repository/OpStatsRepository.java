package com.ldv.financesx.repository;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ldv.financesx.LogManager;
import com.ldv.financesx.OperationStatistics;
import com.ldv.financesx.OperationsResults;
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.GlobalInfo;
import com.ldv.financesx.model.GlobalStatsDataType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.model.OperatorStats;
import com.ldv.financesx.model.StatType1;
import com.ldv.financesx.model.StatsDataSeriesType2;

import com.ldv.financesx.model.StatDataType;

import com.ldv.financesx.model.CategoriesList;

import com.ldv.financesx.model.StatDataHistory;


@Component
public class OpStatsRepository {

	private final OperationStatistics opStats;

    @Autowired
    public OpStatsRepository(OperationStatistics opStats) {
        this.opStats = opStats;
    }
    
    
    public String testString() {   	
  	
    	StatType1 lOpStat = opStats.getlStat().get(0);
    	return lOpStat.getOpCategory();
    	
    	//return "TEST STRING";
    
	}
    
    /* functions to extract data from database*/
   
    
    
    /**
     * Return the list sum for each category.
     * @param isConstraint (booelan), if true return only the list for the constraint category
     * @return list of Amazon operation.
     */	   
    public ArrayList<GlobalStatsDataType> getGlobalStatsSumDebit (boolean isConstraintFilter) {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = new ArrayList<GlobalStatsDataType>();
    	
    	for (StatType1 lOpStat : opStats.getlStat()) {
    		if (lOpStat.getcType() == CategoryType.DEBIT) {
    			
    				// check if the function was call on all categories or on constaint categories only
    				// 1. Return all categories
    				if (!isConstraintFilter) {
	    				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpValue());	    					
	    				globalStatsDataList.add(globalStatsDataType);
    				}
    				// 2. return on constraint categories
    				else if (lOpStat.isConstraint()) {
    					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpValue());	    					
	    				globalStatsDataList.add(globalStatsDataType);
    				}
    				// Do not return any information (isConstraintFilter == true and lOpStat.isConstraint() == false)
    				else {}		
    			}
    		}
    	  	        
    	return globalStatsDataList;	
    }
    
    
	public ArrayList<GlobalStatsDataType> getGlobalStatsSumCredit () {
	    	
	    	ArrayList<GlobalStatsDataType> globalStatsDataList = new ArrayList<GlobalStatsDataType>();
	    	
	    	for (StatType1 lOpStat : opStats.getlStat()) {
	    		if (lOpStat.getcType() == CategoryType.CREDIT) {
	    			
	    				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpValue());
	    					
	    				globalStatsDataList.add(globalStatsDataType);
	    			}
	    		}
	    	  	        
	    	return globalStatsDataList;	
	    }
   
    
public ArrayList<GlobalStatsDataType> getGlobalStatsMoyDebit () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebit = new ArrayList<GlobalStatsDataType>();
    	
    	for (StatType1 lOpStat : opStats.getlStat()) {
    		if (lOpStat.getcType() == CategoryType.DEBIT) {
    			
    				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpMoyenneMensuelle());
    					
    				globalStatsDataListMoyDebit.add(globalStatsDataType);
    						
    			}
    		}
    	  	        
    	return globalStatsDataListMoyDebit;	
    }
	

	public ArrayList<GlobalStatsDataType> getGlobalStatsMoyCredit () {
		
		ArrayList<GlobalStatsDataType> globalStatsDataListMoyCredit = new ArrayList<GlobalStatsDataType>();
		
		for (StatType1 lOpStat : opStats.getlStat()) {
			if (lOpStat.getcType() == CategoryType.CREDIT) {
				
					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpMoyenneMensuelle());
						
					globalStatsDataListMoyCredit.add(globalStatsDataType);
							
				}
			}
		  	        
		return globalStatsDataListMoyCredit;	
	}
	
	// get monthly data history for a single category 
	public StatsDataSeriesType2 getCategoryHistory (String  opCategorieUserChoice) {
			
			// to fill the series
			ArrayList<GlobalStatsDataType> categoryHistory = new ArrayList<GlobalStatsDataType>();
			
			// return array
			StatsDataSeriesType2 statsDataSeriesType2 = null;
			
			
			// Idenfification de la cat??gorie selon le choix de l'utilisateur
			for (StatType1 lOpStat : opStats.getlStat()) {
				if (lOpStat.getOpCategory().equals(opCategorieUserChoice)) {
								
					for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
										
						GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), Math.abs(sDataHistory.getValue()));
										
						categoryHistory.add(globalStatsDataType);								
					}
					
					// add the category information and fill the return parameter
					// return structure
					statsDataSeriesType2 = new StatsDataSeriesType2(categoryHistory, lOpStat.getOpCategory());
				}
			}
					
			return statsDataSeriesType2;	
		}
	
	
		
	// get monthly data history (average) for a single category 
	// averageWindows = numbers of items on which the average is calculated)
	public StatsDataSeriesType2 getCategoryHistoryAverage (String opCategorieUserChoice, int averageWindow) {
				
				// to fill the series
				ArrayList<GlobalStatsDataType> categoryHistory = new ArrayList<GlobalStatsDataType>();
				
				// return array
				StatsDataSeriesType2 statsDataSeriesType2 = null;
				
				
						
				// average calculation
				double averageSumC = 0;
				double averageValueC = 0;
				ArrayList<Double> averageList = new ArrayList<Double>();
				
						
				// Identification de la cat??gorie selon le choix de l'utilisateur
				for (StatType1 lOpStat : opStats.getlStat()) {
					if (lOpStat.getOpCategory().equals(opCategorieUserChoice)) {
						// avarage calculation			
						for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {													
							
							// average with windows calculation
							if (averageList.size() < averageWindow) {
								averageList.add(sDataHistory.getValue());
							}
							else if (averageList.size() == averageWindow) {
								// remove the oldest entry
								averageList.remove(0);
								// add the newest entry
								averageList.add(sDataHistory.getValue());							
							}
							
							// sum the value in the windows
							for (Double value : averageList) {
								averageSumC += value;
							}
							
							// calculate the average
							averageValueC = averageSumC / averageList.size();
							
							
							GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), Math.abs(averageValueC));
							
							categoryHistory.add(globalStatsDataType);
							
							// reset sum for next loop
							averageSumC = 0;
											
						}
						
						// add the category information and fill the return parameter
						// return structure
						statsDataSeriesType2 = new StatsDataSeriesType2(categoryHistory, lOpStat.getOpCategory());
					}
				}
						
				return statsDataSeriesType2;	
			}
	
	
	
	// Get Gains and losses data from database
	public ArrayList<GlobalStatsDataType> getGainsandlosses () {
		
		ArrayList<GlobalStatsDataType> gainsandlosses = new ArrayList<GlobalStatsDataType>();
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(0);
							
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
							
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), sDataHistory.getValue());
							
			gainsandlosses.add(globalStatsDataType);								
		}
		
		return gainsandlosses;	
	}
	
	

	/**
    * Return cummulative Gains and losses data from database.
    * @param None.
    * @return cummulative Gains and losses per month.
    */	
	public ArrayList<GlobalStatsDataType> getGainsandlossesSum () {
		
		ArrayList<GlobalStatsDataType> gainsandlosses = new ArrayList<GlobalStatsDataType>();
		
		Double cummulativeValue = 0.0;
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(0);
							
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
			
			cummulativeValue+=sDataHistory.getValue();
			
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), cummulativeValue);
							
			gainsandlosses.add(globalStatsDataType);								
		}
		
		return gainsandlosses;	
	}
	
	
	
	// Get budget data from database
	/**
     * Get budget data from database (sum of expenses without "Remboursements" and without "Epargne" )
     * @input: None
     * @return : list of budget expenses per month 
     */
	public ArrayList<GlobalStatsDataType> getBudget () {
		
		ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
		
		// retrieve the list of expenses per month
		StatType1 lOpStat = opStats.getlStatGlobal().get(1);
		
			
		// reformat data for future display in HTML page
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
							
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), sDataHistory.getValue());
							
			budget.add(globalStatsDataType);								
		}
		
	
		return budget;	
	}
	

	
	// Get budget average without reimbursementsdata from database
	public ArrayList<GlobalStatsDataType> getAverageBudget () {
		
		ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(1);
		
		double averageSumC = 0;
		double averageValueC = 0;
		int indexC = 1;		
		
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
			averageSumC += sDataHistory.getValue();
			averageValueC = averageSumC / indexC;
			
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), averageValueC);
			
			indexC++;
			
			budget.add(globalStatsDataType);								
		}
		
		return budget;	
	}
	
	
	
	// Get constraint expenses budget average data (without reimbursements)  from database
	public ArrayList<GlobalStatsDataType> getAverageBudgetConstraint () {
		
		ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(4);
		
		double averageSumC = 0;
		double averageValueC = 0;
		int indexC = 1;		
		
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
			averageSumC += sDataHistory.getValue();
			averageValueC = averageSumC / indexC;
			
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), averageValueC);
			
			indexC++;
			
			budget.add(globalStatsDataType);								
		}
		
		return budget;	
	}
	
	
	
	
	// Get budget average with reimbursements data from database
		public ArrayList<GlobalStatsDataType> getAverageBudgetWithReimbursements () {
			
			ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
			
			// ....
			StatType1 lOpStat = opStats.getlStatGlobal().get(1);
			StatType1 lOpStatReimbursementB = opStats.getlStatGlobal().get(3);
			
			double averageSumC = 0;
			double averageValueC = 0;
			int indexC = 1;
			
			for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
				// add the value for a sp??cific date
				averageSumC += sDataHistory.getValue();
				
				
				//check if for his date there is a reimbursement, if yes add it
				for (StatDataHistory sDataHistoryB : lOpStatReimbursementB.getDataHistory()) {
					if (sDataHistoryB.getMonthAndYearShort().equals(sDataHistory.getMonthAndYearShort())) {
						averageSumC += sDataHistoryB.getValue();
					}
				}
				
				// compute the average
				averageValueC = averageSumC / indexC;
				
				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), averageValueC);
				
				indexC++;
				
				budget.add(globalStatsDataType);								
			}
			
			return budget;	
		}
	
	
	
	
	// Get Reimbursement data from database
	public ArrayList<GlobalStatsDataType> getReimbursement () {
		
		ArrayList<GlobalStatsDataType> reimbursement = new ArrayList<GlobalStatsDataType>();
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(3);
							
		for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
							
			GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), sDataHistory.getValue());
							
			reimbursement.add(globalStatsDataType);								
		}
	
		return reimbursement;	
	}
	
	
	/**
     * Get expenses per category for pie chart display
     * @Param isConstraintFilter is set to true return only the constraint categories
     * @return list of expenses per category vs income chart
     */	
	public ArrayList<GlobalStatsDataType> getExpensesPerCategoryforPieChart (boolean isConstraintFilter) {
		
		ArrayList<GlobalStatsDataType> expensesPerCategoryforPieChart = new ArrayList<GlobalStatsDataType>();
		
		for (StatType1 lOpStat : opStats.getlStat()) {
			if (lOpStat.getcType() == CategoryType.DEBIT) {
				if (!lOpStat.getOpCategory().equals(CategoriesList.EPARGNE.getTxtType())) {
									
					// check if the function was call on all categories or on constaint categories only
    				// 1. Return all categories
    				if (!isConstraintFilter) {
    					String label = lOpStat.getOpCategory() + String.format( " - %.0f??? ", Math.abs(lOpStat.getOpValue()));
    					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(label, Math.abs(lOpStat.getOpValue()));
    					
    					expensesPerCategoryforPieChart.add(globalStatsDataType);	
    				}
    				// 2. return on constraint categories
    				else if (lOpStat.isConstraint()) {
    					String label = lOpStat.getOpCategory() + String.format( " - %.0f??? ", Math.abs(lOpStat.getOpValue()));
    					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(label, Math.abs(lOpStat.getOpValue()));
    					
    					expensesPerCategoryforPieChart.add(globalStatsDataType);	
    				}
    				// Do not return any information (isConstraintFilter == true and lOpStat.isConstraint() == false)
    				else {}						
				}
			}
		}
		return expensesPerCategoryforPieChart;	
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
    /**
     * Get expenses per category vs income chart from database for pie chart display
     * @return list of expenses per category vs income chart
     */	
	public ArrayList<GlobalStatsDataType> getExpensesPerCategoryVsIncomeForPieChart () {
		
		double incomeSaved = opStats.getTotalIncomeSum();
		
		ArrayList<GlobalStatsDataType> expensesPerCategoryforPieChart = new ArrayList<GlobalStatsDataType>();		
	
		for (StatType1 lOpStat : opStats.getlStat()) {
			if (lOpStat.getcType() == CategoryType.DEBIT) {
				if (!lOpStat.getOpCategory().equals(CategoriesList.EPARGNE.getTxtType())) {
					String label = lOpStat.getOpCategory() + String.format( ": %.0f???,  %.2f %%", Math.abs(lOpStat.getOpValue()), 100*(Math.abs(lOpStat.getOpValue())/Math.abs(opStats.getTotalIncomeSum())));
					
					//decrease income saved
					incomeSaved -= Math.abs(lOpStat.getOpValue());
					
					//String label = lOpStat.getOpCategory() + String.format( " - %.0f??? ", Math.abs(lOpStat.getOpValue()));
					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(label, 100*(Math.abs(lOpStat.getOpValue())/Math.abs(opStats.getTotalIncomeSum())));
					
					expensesPerCategoryforPieChart.add(globalStatsDataType);	
					
				}
			}
		}
		// add the amount of revenue saved (it is assumed here that the value is >= 0)
		String label1 = "Non d??pens??" + String.format( ": %.0f???,  %.2f %%", incomeSaved, 100*(Math.abs(incomeSaved)/Math.abs(opStats.getTotalIncomeSum())));
		expensesPerCategoryforPieChart.add(new GlobalStatsDataType(label1,100*(Math.abs(incomeSaved)/Math.abs(opStats.getTotalIncomeSum()))));
		return expensesPerCategoryforPieChart;	
	}
	
	
	// return the list of categories
	public ArrayList<OperationCategory> getCategoriesList () {
    	
    	ArrayList<OperationCategory> globalStatsDataList = new ArrayList<>();
    	globalStatsDataList.addAll(opStats.getCategoriesList());
    	   	  	        
    	return globalStatsDataList;	
	}	
	
	
	
	/**
     * return global information
     * @return A GlobalInfo class
     */	
	public GlobalInfo getGlobalInfo () {
		
		GlobalInfo globalInfo = new GlobalInfo(opStats);
				
		return globalInfo;							
	}
	
	
	/**
     * trig the upload of new data
     * @input: string that contains the absolute path to the file that contain the new operation data 
     * @return 
	 * @throws Exception 
     */
	public OperationsResults uploadNewData(String uploadfilepath) throws Exception {
		
		OperationsResults operationsResults;
		
		//integration dans le data book des nouvelles donn??es
		operationsResults = opStats.getOpBook().loadNewFinanceData(uploadfilepath);
		
		// update stats with new data
		opStats.updateStats();
		
		// return added op??rations
		return operationsResults;
		
	}
	
	
	/**
     * Save new data to csv file (finance.csv)
     * @input:  
     * @return: true/false according to update file result
     */
	public String saveFinance() {
	
	boolean saveResult;
		
	// sauvegarde des nouvelles donn??es dans le fichier des op??rations
		
	saveResult = opStats.getOpBook().saveFinance();
	
	return saveResult == true ? "Succ??s" : "Echec";

	}
	
	
	/**
     * Update the stats 
     * @input:  
     * @return: 
     */
	public void updateStats() {
	
	// sauvegarde des nouvelles donn??es dans le fichier des op??rations
	opStats.updateStats();

	}
	
	
	// list of operations without association
	public ArrayList<Operation> getOpWithoutAssociation() {
		
		return opStats.getOpBook().getOpBookDataWithoutAssociation();
	}
	
	
	// total food expense per operator
	public ArrayList<OperatorStats> getFoodSumPerOperator() {
		
		return opStats.getlStat().get(0).getOperatorStats();
	}
	
	
	
    /**
    * Return the statistic for a specific month (all expenses per category).
    * @param month(LocalDate) to get the stats from.
    * @return List of expenses per categories for the month.
    */
	public Map<String, Double> getMonthStats(LocalDate selectedDate) {
		
		// return data : categories and corresponding sums for expenses 
		Map<String, Double> monthExpenses = new LinkedHashMap<String, Double>();
           	
		for (StatType1 lOpStat : opStats.getlStat()) {
			// si la cat??gorie est de type D??bit
			if (lOpStat.getcType() == CategoryType.DEBIT) {	
				// verifie si l'historique de la cat??gorie est non vide
				if (!lOpStat.getDataHistory().isEmpty()) {
					// ajouter si une valeur historique de la cat??gorie est dans le mois/ann??e selectionn??
					// r??cup??rer la date selectionn??e (dans date picker du pannel "Mois courant")
					
					// Todo : recup??rer la date du date picker (dans la page HTML) et l'utiliser ici (au lieu d'utiliser la date courante)
					LocalDate currentDate = selectedDate;
					
					// control log
					LogManager.LOGGER.log(Level.FINE,"Date selectionn?? pour l'affichage du mois: " + currentDate);
					
					
					// cherche dans l'historique de la cat??gorie s'il y a couple mois/ann??e correspondant
					for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
						if ((currentDate.getMonth() == sDataHistory.getMonthAndYear().getMonth()) &&
								currentDate.getYear() == sDataHistory.getMonthAndYear().getYear()) {
							//Ajouter la valeur du dernier mois						
							monthExpenses.put(lOpStat.getOpCategory(), -Precision.round(sDataHistory.getValue(),1));

						}		
					}		
				} else {
					// historique de la cat??gorie vide, la valeur de cette cat??gorie pour le month selectionn?? est mise ?? 0
					monthExpenses.put(lOpStat.getOpCategory(), (double)0);
				}			
			}	
		}		
		return monthExpenses;
	}
	
	
	
	
	/**
	    * Return a table that contains all the stats of categories of type "debit" minus "Epargne".
	    * @param none.
	    * @return List of stats per categories of type "debit" minus "Epargne".
	    */
		public ArrayList<StatType1> getCategoriesStatsConstraint() {
			
			ArrayList<StatType1> returnStats = new ArrayList<StatType1>();
			
			for (StatType1 lOpStat : opStats.getlStat()) {
				if (lOpStat.getcType() == CategoryType.DEBIT) {
					if (lOpStat.isConstraint()) {
						if (!lOpStat.getOpCategory().equals(CategoriesList.EPARGNE.getTxtType())) {
							returnStats.add(lOpStat);
						}
					}
				}
			}
			
			return returnStats;
		}	
	
	
		
		/**
	     * Get budget data from database (sum of mandatory expenses without "Remboursements" and without "Epargne" )
	     * @input: None
	     * @return : list of budget expenses per month 
	     */
		public ArrayList<GlobalStatsDataType> getBudgetConstraint () {
			
			ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
			
			// retrieve the list of expenses per month
			StatType1 lOpStat = opStats.getlStatGlobal().get(4);

			// reformat data for future display in HTML page
			for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
								
				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), sDataHistory.getValue());
								
				budget.add(globalStatsDataType);								
			}

			return budget;	
		}
			
				
	
	/**
    * Return the list of Amazon operations (still not associated to the right category).
    * @param None.
    * @return list of Amazon operation.
    */		
	public ArrayList<Operation> getOpBookDataAmazon() {			
			return opStats.getOpBook().getOpBookDataAmazon();		
	}
	
	
	/**
    * Return the list of operation with errors during reading of CSV file.
    * @param None.
    * @return list of Amazon operation.
    */		
	public ArrayList<String[]> getOperationsWithErrors() {			
			return opStats.getOpBook().getOperationsWithError();		
	}
	
	
}

