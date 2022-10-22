package com.ldv.financesx.repository;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    
    /* functions to extract date from database*/
    
    public ArrayList<GlobalStatsDataType> getGlobalStatsSumDebit () {
    	
    	ArrayList<GlobalStatsDataType> globalStatsDataList = new ArrayList<GlobalStatsDataType>();
    	
    	for (StatType1 lOpStat : opStats.getlStat()) {
    		if (lOpStat.getcType() == CategoryType.DEBIT) {
    			
    				GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(lOpStat.getOpCategory(), lOpStat.getOpValue());
    					
    				globalStatsDataList.add(globalStatsDataType);
   				
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
			
			
			// Idenfification de la catégorie selon le choix de l'utilisateur
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
		public StatsDataSeriesType2 getCategoryHistoryAverage (String  opCategorieUserChoice) {
				
				// to fill the series
				ArrayList<GlobalStatsDataType> categoryHistory = new ArrayList<GlobalStatsDataType>();
				
				// return array
				StatsDataSeriesType2 statsDataSeriesType2 = null;
				
				// average calculation
				double averageSumC = 0;
				double averageValueC = 0;
				int indexC = 1;
				
				
				// Idenfification de la catégorie selon le choix de l'utilisateur
				for (StatType1 lOpStat : opStats.getlStat()) {
					if (lOpStat.getOpCategory().equals(opCategorieUserChoice)) {
									
						for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
							
							averageSumC += sDataHistory.getValue();
							averageValueC = averageSumC / indexC;
							
							GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(sDataHistory.getMonthAndYearShort(), Math.abs(averageValueC));
							
							indexC++;
							
							categoryHistory.add(globalStatsDataType);
											
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
	
	
	// Get budget data from database
	public ArrayList<GlobalStatsDataType> getBudget () {
		
		ArrayList<GlobalStatsDataType> budget = new ArrayList<GlobalStatsDataType>();
		
		// ....
		StatType1 lOpStat = opStats.getlStatGlobal().get(1);
							
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
				// add the value for a spécific date
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
	
	
	// Get expenses per category for pie chart from database
	public ArrayList<GlobalStatsDataType> getExpensesPerCategoryforPieChart () {
		
		ArrayList<GlobalStatsDataType> expensesPerCategoryforPieChart = new ArrayList<GlobalStatsDataType>();
		
		for (StatType1 lOpStat : opStats.getlStat()) {
			if (lOpStat.getcType() == CategoryType.DEBIT) {
				if (!lOpStat.getOpCategory().equals(CategoriesList.EPARGNE.getTxtType())) {
					//String label = lOpStat.getOpCategory() + String.format( ": %.0f€,  %.2f %%", Math.abs(lOpStat.getOpValue()), 100*(Math.abs(lOpStat.getOpValue())/Math.abs(opStats.getTotalDebitSum())));
					String label = lOpStat.getOpCategory() + String.format( " - %.0f€ ", Math.abs(lOpStat.getOpValue()));
					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(label, Math.abs(lOpStat.getOpValue()));
					
					expensesPerCategoryforPieChart.add(globalStatsDataType);	
					
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
					String label = lOpStat.getOpCategory() + String.format( ": %.0f€,  %.2f %%", Math.abs(lOpStat.getOpValue()), 100*(Math.abs(lOpStat.getOpValue())/Math.abs(opStats.getTotalIncomeSum())));
					
					//decrease income saved
					incomeSaved -= Math.abs(lOpStat.getOpValue());
					
					//String label = lOpStat.getOpCategory() + String.format( " - %.0f€ ", Math.abs(lOpStat.getOpValue()));
					GlobalStatsDataType globalStatsDataType = new GlobalStatsDataType(label, 100*(Math.abs(lOpStat.getOpValue())/Math.abs(opStats.getTotalIncomeSum())));
					
					expensesPerCategoryforPieChart.add(globalStatsDataType);	
					
				}
			}
		}
		// add the amount of revenue saved (it is assumed here that the value is >= 0)
		String label1 = "Non dépensé" + String.format( ": %.0f€,  %.2f %%", incomeSaved, 100*(Math.abs(incomeSaved)/Math.abs(opStats.getTotalIncomeSum())));
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
		
		/*
		// Affichage des statistiques : période d'analyse, nombre d'opérations sans association, valeur des opérations sans associations 
		DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate StartDate = opStats.getOpBook().getOperationBookStats().getStartOperationDate();
		LocalDate EndDate = opStats.getOpBook().getOperationBookStats().getEndOperationDate();
		
		String rValue = " INFORMATION GLOBALES => " 
				+ " Date de début: " + StartDate.format(datePattern) 
				+ " / Date de fin:  " + EndDate.format(datePattern)
				+ " => Durée d'analyse : " + Integer.toString(opStats.getHistoryDaylength()) 
				+ " jours" + " ( " + String.format("%.1f", opStats.getHistoryMonthlength()/12) + " années)    " + " => Opérations sans association : " + opStats.getNumberOfOpWithoutAssociation()
				+ "  => Débit sans association : " + (int)opStats.getSumValueDebit()
				+ "€"
				+ "  => Crédit sans association : " + (int)opStats.getSumValueCredit()
				+ "€";
		*/
							
	}
	
	
	/**
     * trig the upload of new data
     * @input: string that contains the absolute path to the file that contain the new operation data 
     * @return 
	 * @throws Exception 
     */
	public OperationsResults uploadNewData(String uploadfilepath) throws Exception {
		
		OperationsResults operationsResults;
		
		//integration dans le data book des nouvelles données
		operationsResults = opStats.getOpBook().loadNewFinanceData(uploadfilepath);
		
		// update stats with new data
		opStats.updateStats();
		
		// return added opérations
		return operationsResults;
		
	}
	
	
	/**
     * Save new data to csv file (finance.csv)
     * @input:  
     * @return: true/false according to update file result
     */
	public String saveFinance() {
	
	boolean saveResult;
		
	// sauvegarde des nouvelles données dans le fichier des opérations
		
	saveResult = opStats.getOpBook().saveFinance();
	
	return saveResult == true ? "Succés" : "Echec";

	}
	
	
	/**
     * Update the stats 
     * @input:  
     * @return: 
     */
	public void updateStats() {
	
	// sauvegarde des nouvelles données dans le fichier des opérations
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
	public Map<String, Double> getMonthStats() {
		
		// return data : categories and corresponding sums for expenses 
		Map<String, Double> monthExpenses = new LinkedHashMap<String, Double>();
           	
		for (StatType1 lOpStat : opStats.getlStat()) {
			// si la catégorie est de type Débit
			if (lOpStat.getcType() == CategoryType.DEBIT) {	
				// verifie si l'historique de la catégorie est non vide
				if (!lOpStat.getDataHistory().isEmpty()) {
					// ajouter si une valeur historique de la catégorie est dans le mois/année selectionné
					// récupérer la date selectionnée (dans date picker du pannel "Mois courant")
					
					// Todo : recupérer la date du date picker (dans la page HTML) et l'utiliser ici (au lieu d'utiliser la date courante)
					LocalDate currentDate = LocalDate.now();
									
					// cherche dans l'historique de la catégorie s'il y a couple mois/année correspondant
					for (StatDataHistory sDataHistory : lOpStat.getDataHistory()) {
						if ((currentDate.getMonth() == sDataHistory.getMonthAndYear().getMonth()) &&
								currentDate.getYear() == sDataHistory.getMonthAndYear().getYear()) {
							//Ajouter la valeur du dernier mois						
							monthExpenses.put(lOpStat.getOpCategory(), -sDataHistory.getValue());

						}		
					}		
				} else {
					// historique de la catégorie vide, la valeur de cette catégorie pour le month selectionné est mise à 0
					monthExpenses.put(lOpStat.getOpCategory(), (double)0);
				}			
			}	
		}		
		return monthExpenses;
	}
	
	
	
	/**
    * Return the list of Amazon operation (still not associated to the right category).
    * @param None.
    * @return list of Amazon operation.
    */		
	public ArrayList<Operation> getOpBookDataAmazon() {			
			return opStats.getOpBook().getOpBookDataAmazon();		
	}
	
}

