package com.ldv.financesx;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.Collections;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ldv.financesx.model.AssociationMode;
import com.ldv.financesx.model.CategoriesList;
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.model.OperationsBook;
import com.ldv.financesx.model.OperatorStats;
import com.ldv.financesx.model.StatDataHistory;
import com.ldv.financesx.model.StatType1;

@Component
public class OperationStatistics implements OpStatInterface {

	@Autowired
	OperationsBook opBook;
	
	private ArrayList<Operation> opBookData;
	private ArrayList<OperationCategory> categoriesList;
	private int numberOfOpWithoutAssociation = 0;
	private double sumValueDebit = 0;
	private double sumValueCredit = 0;
	
	// tableau des statistiques
	// Contains the list of all categories and for each categories all its data
	private ArrayList<StatType1> lStat;
	private ArrayList<StatType1> lStatGlobal;
	
	@PostConstruct
	public void OperationStatisticsSetups () {	
		this.opBookData = opBook.getOpBookData();
		this.categoriesList = opBook.getCategoriesList();
		
		LogManager.LOGGER.log(Level.INFO,"Calcul des statistiques sur les opérations : START");
		
		//compute execution time:
	 	Instant start = Instant.now();
		
		// update all statistics
		updateStats();
		
		//compute execution time:
	 	Instant end = Instant.now();
	 	Duration res = Duration.between(start, end);
		LogManager.LOGGER.log(Level.INFO,"Calcul des statistiques sur les opérations : COMPLETED => " + res);
	
	}
	
	
	// pre-Calculation of data for later usage
	public void calculateAll () {
		
		LocalDate currentHistorydate = LocalDate.now();
		
		
		LogManager.LOGGER.log(Level.FINE,"***********************************");
		LogManager.LOGGER.log(Level.FINE,"| Rubrique | somme des opérations |");
		LogManager.LOGGER.log(Level.FINE,"***********************************");
		
		// generate data type for pre-generated global statistics
		lStatGlobal.add(new StatType1("GlobalGainLossHistory", 0, CategoryType.TOUS, false));
		lStatGlobal.add(new StatType1("GlobalDebitHistory", 0, CategoryType.DEBIT, false));
		lStatGlobal.add(new StatType1("GlobalCreditHistory", 0, CategoryType.CREDIT, false));
		lStatGlobal.add(new StatType1("GlobalReimbursement", 0, CategoryType.CREDIT, false));		
		
		// Objective : for each category add to its history the operation that belongs to it
		// Parameters: "CategoriesList" contains the list of Categories that have been previously built from the association file (csv)
		
		// loop on each Category
		for (OperationCategory lCategory : categoriesList) {
			// create the data type that will hold all the data for this category including the data history
			StatType1 lStatOp = new StatType1(lCategory.getName(), 0, lCategory.getType(), lCategory.isConstraint());		
			// add it to the list of all categories
			lStat.add(lStatOp);
			
			
			// prepare category history by adding placeholder for all the months in between the first date of operation up to the last date  
			// Loop on the month, by starting with the first, than loop adding 1 month up to latest date of operation
			//initiate loop date
			LocalDate loopDate = LocalDate.of(opBook.getLatestDate().getYear(), 
					opBook.getLatestDate().getMonth(), 
					1);
			
			while (loopDate.isAfter(opBook.getOldestDate())) {
				//System.out.println("=>>>>>>>>>>>>>>>>>>>>>>>>>>: " + loopDate);
				
				// create datahistory and add it to the catégory
				StatDataHistory dataHistory = new StatDataHistory(loopDate);
				lStatOp.getDataHistory().add(dataHistory);
				
				// move to next month
				loopDate = loopDate.minus(1,ChronoUnit.MONTHS);
			}
				
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + lCategory.getName());
			//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + CategoriesList.ALIMENTATION_HYGIENE.getTxtType());
			
			// For category/budget "Alimentation et Hygiène" create the list of operators
			// check if the category is "Alimentation et Hygiène"
			if (lCategory.getName().equals(CategoriesList.ALIMENTATION_HYGIENE.getTxtType())) {
				// loop on the association list
				for (String[] associationData : opBook.getbString()) {
					// check if the ossociation item is of category "Alimentation et Hygiène"
					if (associationData[0].equals(CategoriesList.ALIMENTATION_HYGIENE.getTxtType())) {
						// if yes create creat a new operator stat and add it to the list
						OperatorStats localOperatorStats = new OperatorStats(associationData[1], (double)0);
						lStat.get(lStat.size()-1).getOperatorStats().add(localOperatorStats);
						//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + associationData[1]);
					}
					
				}
			}
			
					
			// loop on all operation to check if they belong to the category, and if yes, add them to the category history 
			for (Operation op : opBookData) {
				// check if, for this operation, an association with a category already exist (if not there is nothing to do)
				if (op.getaMode() != AssociationMode.NONE) {														
					// check if, for this operation, the association correspond to the category
					if (op.getAssociation().equals(lCategory.getName())) {
						// increment the total value for the category (= sum of the values of the operations that belong to the category)
						lStatOp.incOpValue(op.getOpValue());
						
						//loop on dates on the history  and add value when date are similar
						for (StatDataHistory dataHistoryLoop : lStatOp.getDataHistory()) {
							if ((dataHistoryLoop.getMonthAndYear().getYear() == op.getDateOp().getYear()) &&
									(dataHistoryLoop.getMonthAndYear().getMonth() == op.getDateOp().getMonth())) {
								dataHistoryLoop.incDataValue(op.getOpValue());
							}
						}
						LogManager.LOGGER.log(Level.FINE,"Categorie : " + lCategory.getName() + ", Date : " 
								+ lStatOp.getDataHistory().get(lStatOp.getDataHistory().size()-1).getMonthAndYear() + ", Valeur : "  
								+ lStatOp.getDataHistory().get(lStatOp.getDataHistory().size()-1).getValue() + ", Libellé : " + op.getLibelle());
								
					}
				}
				
				
				// For category/budget "Alimentation et Hygiène" sum the value for each of operator
				// check the category
				if (lCategory.getName().equals(CategoriesList.ALIMENTATION_HYGIENE.getTxtType())) {
					// check if operation is of type "Alimentation et Hygiène"
					if (op.getAssociation().equals(CategoriesList.ALIMENTATION_HYGIENE.getTxtType())) {
						// loop on operators
						for (OperatorStats localOperatorStats: lStat.get(0).getOperatorStats()) {
							//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> operator- :" + localOperatorStats.getName());
							//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> operation :" + op.getLibelle());
							// check if operator name corresponds to operation operator name, if yes, add value
							if (op.getLibelle().contains(localOperatorStats.getName())) {
								localOperatorStats.addToSum(op.getDebit());
								//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ajout :  " + op.getLibelle());
								//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ajout :  " + localOperatorStats.getName());
								//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ajout :  " + op.getDebit());
							}						
						}
						//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> somme : " + lStat.get(0).getOperatorStats().get(1).getConsolidatedSum());
					}
				}
				
				
			}
			
			// moyenne mensuelle par catégorie
			lStatOp.setOpMoyenneMensuelle(opBook.getOldestDate(), opBook.getLatestDate());	
			LogManager.LOGGER.log(Level.FINE,"Categorie : " + lStatOp.getOpCategory() + ", Somme : " + lStatOp.getOpValue()
			+ ", type : " + lStatOp.getcType().getTxtType() + ", moyenne :" + lStatOp.getOpMoyenneMensuelle());				
			
		}
		
		
		// reverse the data history si that is starts will the oldest date (usefull for later display)
		for (StatType1 lStatOp1 : lStat) {
			Collections.reverse(lStatOp1.getDataHistory());
		}
		
		// instantiate data history array for global stats (first for all operation, second for debit operation only, third for Reimbursement)	
		CreateDataHistoryWithDates(lStatGlobal.get(0).getDataHistory());
		CreateDataHistoryWithDates(lStatGlobal.get(1).getDataHistory());
		CreateDataHistoryWithDates(lStatGlobal.get(3).getDataHistory());
		
		// fill global stats
		// lStat is an array. Each element in the array contain a category will all its stats (including the history data)
		// lsGlobalstat is used to  summ the history of all categories = sum per month of all catégories
		// loop on all the categories on by one
		for (StatType1 loopStat : lStat) {
			// sum category history
			// get the history array for a category 
			// loop an the history for one category
			for (StatDataHistory lDataHistory : loopStat.getDataHistory()) {
				
				//Loop on the history array of the global stat (all operations) where the sum will be made (oe item is a date = year/month where the sum will be made)
				for (StatDataHistory tmpStatDataHistory : lStatGlobal.get(0).getDataHistory()) {
					// if the global history date is the same as the current history of the category then add the value to the sum
					if (tmpStatDataHistory.CompareYearAndMonth(lDataHistory) == true)
					{
						// do not add in case of "epargne" as it is not an expense nor a revenue
						if (!loopStat.getOpCategory().equals("Epargne")) {
							tmpStatDataHistory.setValue(tmpStatDataHistory.getValue() + lDataHistory.getValue());
						}
					LogManager.LOGGER.log(Level.FINE,"Global stat, Category : " + lStatGlobal.get(0).getOpCategory().toString() + " " + lDataHistory.getMonthAndYear().toString() + " Valeure cummulée : " 
						+ tmpStatDataHistory.getValue() + "  => " + loopStat.getOpCategory());
					}
				}
				
				
				// Loop on the history array of the global stat, débit only + reimbursement (to get the effective amount spent) where the sum will be made (oe item is a date = year/moth where the sum will be made)
				for (StatDataHistory tmpStatDataHistory : lStatGlobal.get(1).getDataHistory()) {
					// if the global history date is the same as the one in the history of the category then add the value to the sum
					if (tmpStatDataHistory.CompareYearAndMonth(lDataHistory) == true)
					{
						// add only if débit (without "epargne")
						if (loopStat.getcType() == CategoryType.DEBIT) {
							// do not add in case of "épargne" as it is not an expense nor a revenue
							if (!loopStat.getOpCategory().equals("Epargne")) {
								tmpStatDataHistory.setValue(tmpStatDataHistory.getValue() + lDataHistory.getValue());
							}
						}
						
						// add credit only if reimbursement
						if (loopStat.getcType() == CategoryType.CREDIT) {
							if (loopStat.getOpCategory().equals("CPAM + Mutuelle") || loopStat.getOpCategory().equals("Remboursement") || loopStat.getOpCategory().equals("Amazon Credit") ) {
								tmpStatDataHistory.setValue(tmpStatDataHistory.getValue() + lDataHistory.getValue());
							}
						}
						
						
					LogManager.LOGGER.log(Level.FINE,"Global stat, Category : " + lStatGlobal.get(1).getOpCategory().toString() + " " + lDataHistory.getMonthAndYear().toString() + " Valeure cumulée : " 
						+ tmpStatDataHistory.getValue() + "  => " + loopStat.getOpCategory());
					}
				}
			}
			
			if ((loopStat.getOpCategory().equals("Remboursement")) || (loopStat.getOpCategory().equals("CPAM + Mutuelle")) || (loopStat.getOpCategory().equals("Amazon Crédit")) ) {
				for (StatDataHistory lDataHistory : loopStat.getDataHistory()) {
					//Loop on the history array of the reimbursement stat (all operations) where the sum will be made (oe item is a date = year/month where the sum will be made)
					for (StatDataHistory tmpStatDataHistory : lStatGlobal.get(3).getDataHistory()) {
						// if the global history date is the same as the current history of the category then add the value to the sum
						if (tmpStatDataHistory.CompareYearAndMonth(lDataHistory) == true)
						{
							tmpStatDataHistory.setValue(tmpStatDataHistory.getValue() + lDataHistory.getValue());
							
							LogManager.LOGGER.log(Level.FINE,"Reimbursement stat, Category : " + lStatGlobal.get(3).getOpCategory().toString() + " " + lDataHistory.getMonthAndYear().toString() + " Valeure cummulée : " 
							+ tmpStatDataHistory.getValue() + "  => " + loopStat.getOpCategory());
						}
					}
				}
			}
		}
	}


	private void CreateDataHistoryWithDates (ArrayList<StatDataHistory> dataHistory) {
		
		// Comporator to reorder data history
		DataHistoryComparator dataHistoryComparator = new DataHistoryComparator();
		
		
		// build the history array list
		// lstats Contains the list of of all categoriesa and for each categories all its data
		for (StatType1 lStatItem : lStat) {
			for (StatDataHistory loopStatDataHistory : lStatItem.getDataHistory()) {
				boolean dateFoundinCategoryHistory = false;
				
				// créer la première entrée dans l'historique si vide
				if (dataHistory.isEmpty()) {
					LocalDate tmpDate = LocalDate.of(loopStatDataHistory.getMonthAndYear().getYear(), loopStatDataHistory.getMonthAndYear().getMonth(), 1);
					// Les historiques des catégories sont identifiées par les années et mois uniquement
					StatDataHistory tmpDataHistory = new StatDataHistory(tmpDate);	
					tmpDataHistory.setValue((double)0);
					dataHistory.add(tmpDataHistory);
				}
				else {
					
					for (StatDataHistory pLoopDataHistory: dataHistory) {
						
						//if (pLoopDataHistory.getMonthAndYear() == loopStatDataHistory.getMonthAndYear())
						if (pLoopDataHistory.CompareYearAndMonth(loopStatDataHistory) == true)
							dateFoundinCategoryHistory = true;				
					}
					
					if (dateFoundinCategoryHistory == false) {				
						LocalDate tmpDate = LocalDate.of(loopStatDataHistory.getMonthAndYear().getYear(), loopStatDataHistory.getMonthAndYear().getMonth(), 1);					
						StatDataHistory tmpDataHistory = new StatDataHistory(tmpDate);
						tmpDataHistory.setValue((double)0);
						dataHistory.add(tmpDataHistory);
					}							
				}
			}
		}
		
		// reorder dataHistory
		Collections.sort(dataHistory, dataHistoryComparator);
		
	}
		
	
	public int getHistoryDaylength() {
		return (int)ChronoUnit.DAYS.between(opBook.getOldestDate(), opBook.getLatestDate());
	}
	
	public float getHistoryMonthlength() {
		return ChronoUnit.MONTHS.between(opBook.getOldestDate(), opBook.getLatestDate());
	}
	
	// total des revenus : salaire + CAF 
	
	public int getTotalIncomeSum() {
		int sum=0;
		
		for (StatType1 lOpStat : getlStat()) {
			if ((lOpStat.getOpCategory().equals(CategoriesList.SALAIRE.getTxtType())) || (lOpStat.getOpCategory().equals(CategoriesList.CAF.getTxtType())))   {
				sum += Math.abs(lOpStat.getOpValue());
			}
		}
		
		return sum;
	}
	
	// total des dépenses
	public int getTotalDebitSum() {
		int sum=0;
		
		for (StatType1 lOpStat : getlStat()) {
			if (lOpStat.getcType() == CategoryType.DEBIT) {
				if (!lOpStat.getOpCategory().equals(CategoriesList.EPARGNE.getTxtType())) {
					sum += Math.abs(lOpStat.getOpValue());
				}
			}
		}
		
		return sum;
	}
	
	
	/**
	 * @return the opBook
	 */
	public OperationsBook getOpBook() {
		return opBook;
	}


	/**
	 * @param opBook the opBook to set
	 */
	public void setOpBook(OperationsBook opBook) {
		this.opBook = opBook;
	}


	/**
	 * @return the opBookData
	 */
	public ArrayList<Operation> getOpBookData() {
		return opBookData;
	}


	/**
	 * @param opBookData the opBookData to set
	 */
	public void setOpBookData(ArrayList<Operation> opBookData) {
		this.opBookData = opBookData;
	}


	/**
	 * @return the categoriesList
	 */
	public ArrayList<OperationCategory> getCategoriesList() {
		return categoriesList;
	}


	/**
	 * @param categoriesList the categoriesList to set
	 */
	public void setCategoriesList(ArrayList<OperationCategory> categoriesList) {
		this.categoriesList = categoriesList;
	}


	/**
	 * @return the numberOfOpWithoutAssociation
	 */
	public int getNumberOfOpWithoutAssociation() {
		return numberOfOpWithoutAssociation;
	}


	/**
	 * @param numberOfOpWithoutAssociation the numberOfOpWithoutAssociation to set
	 */
	public void setNumberOfOpWithoutAssociation(int numberOfOpWithoutAssociation) {
		this.numberOfOpWithoutAssociation = numberOfOpWithoutAssociation;
	}


	/**
	 * @return the lStat
	 */
	public ArrayList<StatType1> getlStat() {
		return lStat;
	}


	/**
	 * @param lStat the lStat to set
	 */
	public void setlStat(ArrayList<StatType1> lStat) {
		this.lStat = lStat;
	}


	/**
	 * @return the lStatGlobal
	 */
	public final ArrayList<StatType1> getlStatGlobal() {
		return lStatGlobal;
	}


	/**
	 * @param lStatGlobal the lStatGlobal to set
	 */
	public final void setlStatGlobal(ArrayList<StatType1> lStatGlobal) {
		this.lStatGlobal = lStatGlobal;
	}
	
	public double getSumValueDebit() {
		return sumValueDebit;
	}
	
	public double getSumValueCredit() {
		return sumValueCredit;
	}


	@Override
	public void updateStats() {
		// update all statitics
		lStat = new ArrayList<StatType1>();
		lStatGlobal = new ArrayList<StatType1>();
		
		calculateAll ();
		numberOfOpWithoutAssociation = opBook.getCountWithoutAssociation();
		sumValueCredit = opBook.getSumValueCredit();
		sumValueDebit = opBook.getSumValueDebit();
		
	}	
}
