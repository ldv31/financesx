package com.ldv.financesx.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ldv.financesx.ErrorCodeAndMessage;
import com.ldv.financesx.LogManager;
import com.ldv.financesx.OperationsResults;
import com.ldv.financesx.model.AssociationMode;
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.GlobalStatsDataType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.service.OpStatsService;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.model.OperatorStats;
import com.ldv.financesx.model.StatDataHistory;
import com.ldv.financesx.model.StatType1;

@Controller
public class HomeController {

	private final int averageWindows = 12;
	
	private final OpStatsService opStatsService;
	
	@Autowired
	public HomeController(OpStatsService opStatsService) {
		this.opStatsService = opStatsService;
	}
	
	@RequestMapping(value ={"","/","/Accueil.html"})
	public String displayHomePage(Model model) {
		model.addAttribute("username", "Bill");
		return "Accueil.html";
	}
	
	
	@RequestMapping(value ={"/displayGlobalstats.html"})
	public ModelAndView displayGlobaStats(Model model) {
	
		// create model and view
		ModelAndView modelAndView = new ModelAndView("globalstats.html");
		
		// A. add the global stats for sum of expenses per categories
		// parameter in function getGlobalStatsSumDebit is set to flase to get in formation on all categories
		// parameter to true is for constraint categories only
		ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsService.getGlobalStatsSumDebit(false);
        modelAndView.addObject("globalStatsDataList", globalStatsDataList);
        
        //Intermediate datastore
        Map<String, Double> data = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : globalStatsDataList) {
        	// convert to positive value for display
    		data.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
    	}
        
        modelAndView.addObject("keySet", data.keySet());
        modelAndView.addObject("values", data.values());
       
        
        // B. add the global stats for sum of incomes per categories
        ArrayList<GlobalStatsDataType> globalStatsDataListSumCredit = opStatsService.getGlobalStatsSumCredit();
        modelAndView.addObject("globalStatsDataListSumCredit", globalStatsDataListSumCredit);
        
        //Intermediate datastore
        Map<String, Double> dataSumIncome = new LinkedHashMap<String, Double>();
        
        for (GlobalStatsDataType stat : globalStatsDataListSumCredit) {
        	dataSumIncome.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
    	}
        
        modelAndView.addObject("keySetSumCredit", dataSumIncome.keySet());
        modelAndView.addObject("valuesSumCredit", dataSumIncome.values());
        
        
		
      
        // C. add the global stats for average of expenses per categories
 		ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebit = opStatsService.getGlobalStatsMoyDebit();
        modelAndView.addObject("globalStatsDataListMoyDebit", globalStatsDataListMoyDebit);
         
        //Intermediate datastore
        Map<String, Double> dataMoyDebit = new LinkedHashMap<String, Double>();
         
         
        for (GlobalStatsDataType stat : globalStatsDataListMoyDebit) {
     		dataMoyDebit.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
     	}
	    modelAndView.addObject("keySetMoyDebit", dataMoyDebit.keySet());
	    modelAndView.addObject("valuesMoyDebit", dataMoyDebit.values());
        
        
        // D. add the global stats for average of income per categories
  		ArrayList<GlobalStatsDataType> globalStatsDataListMoyCredit = opStatsService.getGlobalStatsMoyCredit();
	    modelAndView.addObject("globalStatsDataListMoyDCredit", globalStatsDataListMoyCredit);
	      
	    //Intermediate datastore
	    Map<String, Double> dataMoyCredit = new LinkedHashMap<String, Double>();
	      
	      
	    for (GlobalStatsDataType stat : globalStatsDataListMoyCredit) {
	  		dataMoyCredit.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
      	}
        modelAndView.addObject("keySetMoyCredit", dataMoyCredit.keySet());
        modelAndView.addObject("valuesMoyCredit", dataMoyCredit.values());
        
        // for exception error page test only
        //ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebitTest = new ArrayList<GlobalStatsDataType>();
        //System.out.println("error for test" + globalStatsDataListMoyDebitTest.get(10));
        
        // Main return statement 
        return modelAndView;
         
	}
	
	
	// page to display when selected in the main page
	@RequestMapping(value ={"/displayStatsHistory.html"}, method = RequestMethod.GET)
	public ModelAndView displayStatsHistory(@ModelAttribute("opCategoryObject") OperationCategory opCategoryObject, Model model) {
	
		// size of the windows for average values (set to default)
		int localWindows = averageWindows;
			
		// identify the max value for the display in between sums and average across all categories
		double maxValueForChartHistory = 0;
		
		// get average windows string
		String averageWindowAlldisplays = opCategoryObject.getAverageWindowsSize();
		
		// get the windows value from the user;
		if (averageWindowAlldisplays != null) {
			if (averageWindowAlldisplays.length() > 0) {
				try {
					localWindows = Integer.parseInt(averageWindowAlldisplays) > 0 ? Integer.parseInt(averageWindowAlldisplays) : averageWindows;
				}
				// in case a non numerical value is entered
				catch (NumberFormatException ex) {
					LogManager.LOGGER.log(Level.FINE,"User did not enter a numerical value as expected");
				}
			}
		}
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("historystats.html");
				
		// Add the global stats for sum of expenses per categories
		ArrayList<GlobalStatsDataType> categoryHistory = opStatsService.getCategoryHistory(opCategoryObject.getName()).getCategoryHistory();	
		ArrayList<GlobalStatsDataType> categoryHistoryAverage = opStatsService.getCategoryHistoryAverage(opCategoryObject.getName(), localWindows).getCategoryHistory();
		modelAndView.addObject("categoryHistory", categoryHistory);
		modelAndView.addObject("categoryHistoryAverage", categoryHistoryAverage);
        
        //Intermediate datastore
        Map<String, Double> dataCategoryHistory = new LinkedHashMap<String, Double>();
        Map<String, Double> dataCategoryHistoryAverage = new LinkedHashMap<String, Double>();
        
        for (GlobalStatsDataType stat : categoryHistory) {
    		dataCategoryHistory.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
        	maxValueForChartHistory = (Precision.round(stat.getOpValue(),1) > maxValueForChartHistory) ? Precision.round(stat.getOpValue(),1) : maxValueForChartHistory;
    	}
        
        
        for (GlobalStatsDataType stat : categoryHistoryAverage) {
    		dataCategoryHistoryAverage.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
    	}
        
        modelAndView.addObject("keySetCategoryHistory", dataCategoryHistory.keySet());
        modelAndView.addObject("valuesCategoryHistory", dataCategoryHistory.values());

        modelAndView.addObject("keySetCategoryHistoryAverage", dataCategoryHistoryAverage.keySet());
        modelAndView.addObject("valuesCategoryHistoryAverage", dataCategoryHistoryAverage.values()); 
        
        modelAndView.addObject("CategoryNameDefault", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory());
        modelAndView.addObject("CategoryName", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory() + ": valeures");
        modelAndView.addObject("CategoryNameAverage", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory() + ": Moyenne" );
        
        // add to model the max value for the chart
	    modelAndView.addObject("maxValueForChartHistory", maxValueForChartHistory);
        
        // Main return statement 
        return modelAndView;
        
	}
	
	
	@RequestMapping(value ={"/displaygainsandlosses.html"})
	public ModelAndView displaygainsandlosses(Model model) {
	
		// create model and view
		ModelAndView modelAndView = new ModelAndView("gainsandlosses.html");
		
		
		// Add the gains / losses  stats
		ArrayList<GlobalStatsDataType> gainsandlosses = opStatsService.getGainsandlosses();
        
        // Add the gains / losses  stats
 		ArrayList<GlobalStatsDataType> gainsandlossesSum = opStatsService.getGainsandlossesSum();
        
        //Intermediate datastore
        Map<String, Double> dataGainsandlosses = new LinkedHashMap<String, Double>();
        
        //Intermediate datastore
        Map<String, Double> dataGainsandlossesSum = new LinkedHashMap<String, Double>();
        
        for (GlobalStatsDataType stat : gainsandlosses) {
        	dataGainsandlosses.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
    	}
        modelAndView.addObject("keySetGainsandlosses", dataGainsandlosses.keySet());
        modelAndView.addObject("valuesGainsandlosses", dataGainsandlosses.values());
	
        for (GlobalStatsDataType stat : gainsandlossesSum) {
        	dataGainsandlossesSum.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
    	}
        modelAndView.addObject("keySetGainsandlossesSum", dataGainsandlossesSum.keySet());
        modelAndView.addObject("valuesGainsandlossesSum", dataGainsandlossesSum.values());
        
        
        // Main return statement 
        return modelAndView;
        
	}
	
	
	@RequestMapping(value ={"/displaybudget.html"})
	public ModelAndView displaybudget(Model model) {
	
		// create model and view
		ModelAndView modelAndView = new ModelAndView("budget.html");
			
		// Add the budget stats
		ArrayList<GlobalStatsDataType> budget = opStatsService.getBudget();
        modelAndView.addObject("budget", budget);
        
        //Intermediate datastore
        Map<String, Double> dataBudget = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : budget) {
        	dataBudget.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
    	}
        modelAndView.addObject("keyBudget", dataBudget.keySet());
        modelAndView.addObject("valuesBudget", dataBudget.values());
	
        // Add the average budget stats
 		ArrayList<GlobalStatsDataType> budgetAverage = opStatsService.getAverageBudget();
         modelAndView.addObject("budgetAverage", budgetAverage);
         
         //Intermediate datastore
         Map<String, Double> dataAverageBudget = new LinkedHashMap<String, Double>();
         
         
         for (GlobalStatsDataType stat : budgetAverage) {
         	dataAverageBudget.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
     	}
         modelAndView.addObject("keyBudgetAverage", dataAverageBudget.keySet());
         modelAndView.addObject("valuesBudgetAverage", dataAverageBudget.values());
        
        // Main return statement 
        return modelAndView;
        
	}
	
	
	@RequestMapping(value ={"/displayreimbursement.html"})
	public ModelAndView displayReimbursement(Model model) {
	
		// create model and view
		ModelAndView modelAndView = new ModelAndView("reimbursement.html");
		
		
		// Add the reimbursements stats
		ArrayList<GlobalStatsDataType> reimbursement = opStatsService.getReimbursement();
        modelAndView.addObject("reimbursement", reimbursement);
        
        //Intermediate datastore
        Map<String, Double> dataReimbursement = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : reimbursement) {
        	dataReimbursement.put(stat.getIndex(), Precision.round(stat.getOpValue(),1));
    	}
        modelAndView.addObject("keyReimbursement", dataReimbursement.keySet());
        modelAndView.addObject("valuesReimbursement", dataReimbursement.values());
	
        // Add the average budget including reimbursements stats
 		ArrayList<GlobalStatsDataType> averageBudgetWithReimbursements = opStatsService.getAverageBudgetWithReimbursements();
         modelAndView.addObject("averageBudgetWithReimbursements", averageBudgetWithReimbursements);
         
         //Intermediate datastore
         Map<String, Double> dataAverageBudgetWithReimbursements = new LinkedHashMap<String, Double>();
         
         
         for (GlobalStatsDataType stat : averageBudgetWithReimbursements) {
         	dataAverageBudgetWithReimbursements.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
     	}
         modelAndView.addObject("keyAverageBudgetWithReimbursements", dataAverageBudgetWithReimbursements.keySet());
         modelAndView.addObject("valuesAverageBudgetWithReimbursements", dataAverageBudgetWithReimbursements.values());
        
        // Main return statement 
        return modelAndView;
        
	}
	
	
	@RequestMapping(value ={"/displayexpensescatpie.html"})
	public ModelAndView displayexpensescatpie(Model model) {
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("expensescatpie.html");
		
		// Add the stats
		ArrayList<GlobalStatsDataType> expensescatpie = opStatsService.getExpensesPerCategoryforPieChart(false);
		ArrayList<GlobalStatsDataType> expensesvsincomecatpie = opStatsService.getExpensesPerCategoryVsIncomeForPieChart();
        
        modelAndView.addObject("expensescatpie", expensescatpie);
        modelAndView.addObject("expensesvsincomecatpie", expensesvsincomecatpie);
        	
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// chargement de nouvelles op??rations
	@RequestMapping(value ={"/displayuploadoperations.html"})
	public ModelAndView displayuploadoperations(Model model) {
		
		// operation results
		int uploadResultCode = -1;
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("upload.html");
		
		// add the current operation result. "-1" indicated at this step we only want to display the invite page
		modelAndView.addObject("uploadResultCode", uploadResultCode);
		
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// R??sultat du chargement des nouvelles op??rations (statut, statistiques et sauvegarde si OK)
	@RequestMapping(value ={"/displayuploadresults.html"})
	public ModelAndView displayuploadresults(@RequestParam("uploadfilepath") String uploadfilepath, Model model) throws Exception {
		
		String saveFileResult;
		
		// operation results
		int uploadResultCode = -1;
		
		ModelAndView modelAndView;
		
		// structure hosting the results of the upload operation
		OperationsResults OperationsResults;
		
		// array to store data added from the upload file 
		ArrayList<Operation> opBookAddedData;
		
			
		LogManager.LOGGER.log(Level.FINE,"Upload file path: " + uploadfilepath);
		
		String uploadFilePathFrompModel = (String)model.getAttribute("uploadfilepath");
		
		
		// execution de la mise ?? jour des donn??es
		OperationsResults = opStatsService.uploadNewData(uploadfilepath);
		
		// r??cup??rer le r??sultats de l'op??ration d'upload
		opBookAddedData = OperationsResults.getOpBookOperationData();
		
		// upload operation success
		if (OperationsResults.getResult() == ErrorCodeAndMessage.OPERATION_SUCCES) {
			// create model and view
			modelAndView = new ModelAndView("uploadresults.html");
			
			// ajouter dans le model le chemin vers le fichier pour affichage dans la page HTML
			modelAndView.addObject("uploadfilepath", uploadfilepath);
			
			// ajouter dans le model le nombre d'op??rations ajout??es pour affichage dans la page HTML
			modelAndView.addObject("uploadoperationsize", opBookAddedData.size());
			
			LogManager.LOGGER.log(Level.FINE, "HomeController => size of new data array :" + opBookAddedData.size());
			
			// sauvegarde du fichier des op??rations avec les nouvelles mises ?? jour
			saveFileResult = opStatsService.saveFinance();
			
			// ajouter dans le model le nombre d'op??rations ajout??es pour affichage dans la page HTML
			modelAndView.addObject("saveFileResult", saveFileResult);
			
			// mettre ?? jour les statistiques
			opStatsService.updateStats();	
		}
		else {
			// create model and view
			modelAndView = new ModelAndView("upload.html");
			
			// set to 1 to indicate an error
			uploadResultCode = 1;
			
			// add the current operation result.
			modelAndView.addObject("uploadResultCode", uploadResultCode);
			
			// ajouter dans le model l'erreur pour affichage dans la page HTML
			modelAndView.addObject("errorMessage", OperationsResults.getResult().getTxtType());
	
		}
					
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// affichage des op??rations sans associations
	@RequestMapping(value ={"/displayopnoassociation.html"})
	public ModelAndView displayopnoassociation(Model model) {
			
		// list of operations without association
		ArrayList<Operation> opBookDataWithoutAssociation;
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayupopwithoutassociations.html");
		
			
		// Init the  category to display
		//String opCategorieUserChoice = new String("EDF");
		
		// will be used for the value selected in the drop-down
		
		OperationCategory opCategoryObjectNoAsso = new OperationCategory();
		opCategoryObjectNoAsso.setName("EDF");
		opCategoryObjectNoAsso.setType(CategoryType.TOUS);
		model.addAttribute("opCategoryObjectNoAsso", opCategoryObjectNoAsso);
		
	
	
		LogManager.LOGGER.log(Level.FINE,"Category object: " + opCategoryObjectNoAsso + "  Category : " + opCategoryObjectNoAsso.getName());
		
		
		LogManager.LOGGER.log(Level.FINE,"Category object from model: " + model.getAttribute("opCategoryObject") + "  Category : " + opCategoryObjectNoAsso.getName());
		
					
		opBookDataWithoutAssociation = opStatsService.getOpWithoutAssociation();
		
		// ajouter dans le mod??le les op??rations sans associations
		modelAndView.addObject("opBookDataWithoutAssociation", opBookDataWithoutAssociation);
		
        // Main return statement 
        return modelAndView;
        
	}
	

	/* Display the list of operation without association after selecting a new category for an operation.
    * @param opCategoryObjectNoAsso the new category to apply to the operation.
    * @param opIdTxt the unique identifier of the operation.
    * @return Page to display along with corresponding date in the model.
    */
	@RequestMapping(value ={"/opnoassociationselect.html"}, method = RequestMethod.POST)
	public ModelAndView displayopnoassociationSelect(@ModelAttribute("opCategoryObjectNoAsso") OperationCategory opCategoryObjectNoAsso, 
			@RequestParam("opIdTxt") String opIdTxt, Model model) {
		
		// operation results message 
		String resultMessage;
		
		// list of operations without association
		ArrayList<Operation> opBookDataWithoutAssociation;
		
		// operation id;
		int opId = -1;
		
		// operation to update (if found)
		Operation opUpdate = null;
		
		LogManager.LOGGER.log(Level.FINE,"Category object after select: " + opCategoryObjectNoAsso);
		
		OperationCategory opCategoryObjectFrompModel = (OperationCategory)model.getAttribute("opCategoryObjectNoAsso");
		LogManager.LOGGER.log(Level.FINE,"Category object after select, from model: " + opCategoryObjectFrompModel + "  Category : " + opCategoryObjectFrompModel.getName());
		
		LogManager.LOGGER.log(Level.FINE,"execution of /opnoassociationselect.html + category name : " + opCategoryObjectNoAsso.getName());

		
		LogManager.LOGGER.log(Level.FINE,"execution of /opnoassociationselect.html + opeeration ID : " + opIdTxt);
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("opnoassociationselect.html");
			
		
		//retrieve the list of operation and display the one whith change category	
		opBookDataWithoutAssociation = opStatsService.getOpWithoutAssociation();
		
		// verify if the input string for ID is empty
		if (!opIdTxt.isEmpty()) {
			
			opId = Integer.parseInt(opIdTxt);
			
			// search for the operation corresponding to the operation id provided by the user
			for (Operation op : opBookDataWithoutAssociation) {
				if (op.getOpId() == opId) {
					LogManager.LOGGER.log(Level.FINE,"execution of /opnoassociationselect.html + Libell?? op??ration  : " + op);
					opUpdate = op;			
				}
			}
		}
		
		// update operation and save or return error
		if (opUpdate != null) {
			LogManager.LOGGER.log(Level.FINE,"execution of /opnoassociationselect.html => changing association mode from  "
					+ opUpdate.getAssociation() + " to " + opCategoryObjectNoAsso.getName());
			
			// update operation
			opUpdate.setAssociation(opCategoryObjectNoAsso.getName());
			opUpdate.setaMode(AssociationMode.MANUAL);
			
			// recalculate stats and save operation
			// sauvegarde du fichier des op??rations avec les nouvelles mises ?? jour
			//saveFileResult = opStatsService.saveFinance();
			opStatsService.saveFinance();
			
			// ajouter dans le model le nombre d'op??rations ajout??es pour affichage dans la page HTML
			//modelAndView.addObject("saveFileResult", saveFileResult);
			
			// mettre ?? jour les statistiques
			opStatsService.updateStats();
			
			resultMessage = "Mise ?? jour de la cat??gorie : " + opUpdate.getAssociation() + " r??ussie pour l' op??ration : " + opUpdate;
			
		} else  { // error
			LogManager.LOGGER.log(Level.FINE,"execution of /opnoassociationselect.html => error => opid not found : " + opId);
			resultMessage = "Identifiant invalid ou non pr??cis??";
		}
		
		
		// get the update list of operation without category 	
		opBookDataWithoutAssociation = opStatsService.getOpWithoutAssociation();
	
		// ajouter dans le mod??le les op??rations sans associations
		modelAndView.addObject("opBookDataWithoutAssociation", opBookDataWithoutAssociation);
		
		// ajouter le message de retour
		modelAndView.addObject("resultMessage", resultMessage);
		
 
        // Main return statement 
        return modelAndView;
        
	}
	

	
	// affichage specifique pour alimentation et hygi??ne
	@RequestMapping(value ={"/displayalimspecific.html"})
	public ModelAndView displayalimspecific(Model model) {
				
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayalimspecific.html");
		
		// lisr of value per operator form alimentation/hygi??ne
		ArrayList<OperatorStats> foodSumPerOperator = opStatsService.getFoodSumPerOperator();
					
		//Intermediate datastore
        Map<String, Double> foodSumPerOperatorMap = new LinkedHashMap<String, Double>();
        
        
        for (OperatorStats stat : foodSumPerOperator) {
        	foodSumPerOperatorMap.put(stat.getName(), -Precision.round(stat.getConsolidatedSum(),1));
    	}
        modelAndView.addObject("keyFoodSumPerOperator", foodSumPerOperatorMap.keySet());
        modelAndView.addObject("valuesFoodSumPerOperator", foodSumPerOperatorMap.values());
		
		
        // Main return statement 
        return modelAndView;
	        
	}
	
	/* display of the expenses per categories for a specific month .
    * @param month(LocalDate) to get the stats from.
    * @return List of expenses per categories for the month.
    */
	@RequestMapping(value ={"/displaymonthstats.html"})
	public ModelAndView displaymonthstats(Model model) {
		
		double sum = 0;
		
		// identify the max value for the display in between sums and average across all categories
		double maxValueForChart = 0;
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displaymonthstats.html");
		
		// 1. Add to the model the sum for each category on the month selected
		//Intermediate datastore
        Map<String, Double> monthsStats = opStatsService.getMonthStats(LocalDate.now());
        
        modelAndView.addObject("keyMonthsStats", monthsStats.keySet());
        modelAndView.addObject("valuesMonthsStats", monthsStats.values());
		
        // identify the max value to be passed to the graph Y-axis for display
        for (Double sumCat: monthsStats.values()) {
        	sum+=sumCat;
        	maxValueForChart = (sumCat > maxValueForChart) ? sumCat : maxValueForChart;
        }
        
        modelAndView.addObject("sum", (int)sum);
        modelAndView.addObject("selecteddate", LocalDate.now());
		
        
        // 2. Add to the model the data about the average value
        ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebit = opStatsService.getGlobalStatsMoyDebit();
        modelAndView.addObject("globalStatsDataListMoyDebit", globalStatsDataListMoyDebit);
         
        //Intermediate datastore
        Map<String, Double> dataMoyDebit = new LinkedHashMap<String, Double>();
         
         
        for (GlobalStatsDataType stat : globalStatsDataListMoyDebit) {
     		dataMoyDebit.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
     		maxValueForChart = (-stat.getOpValue() > maxValueForChart) ? -stat.getOpValue() : maxValueForChart;
     	}
	    modelAndView.addObject("keySetMoyDebit", dataMoyDebit.keySet());
	    modelAndView.addObject("valuesMoyDebit", dataMoyDebit.values());
        
        
	    // add to model the max value for the chart
	    modelAndView.addObject("maxValueForChart", maxValueForChart);
	    
        // Main return statement 
        return modelAndView;			
	}
	

	
	/* display of the expenses per categories for a specific month after selecting a new date .
	    * @param month(LocalDate) to get the stats from.
	    * @return List of expenses per categories for the month.
	    */
		@RequestMapping(value ={"/displaymonthstatsupdate.html"})
		public ModelAndView displaymonthstatsupdate(@RequestParam("startDate") String selectedDateString, Model model) {
			
			double sum = 0;
			
			// identify the max value for the display in between sums and average across all categories
			double maxValueForChart = 0;
			
			LocalDate selectedDate;
			
			// control log
			LogManager.LOGGER.log(Level.FINE,"Date from datepicker (string): " + selectedDateString);
			LogManager.LOGGER.log(Level.FINE,"Date from datepicker (LocalDate): " + selectedDateString);
			
			// create model and view
			ModelAndView modelAndView = new ModelAndView("displaymonthstatsupdate.html");

			
			if (!selectedDateString.isEmpty()) {
				
				// convert selected date string to localdate
				selectedDate = LocalDate.parse(selectedDateString);
				
				// 1. Add to the model the sum for each category on the month selected
				//Intermediate datastore
		        Map<String, Double> monthsStats = opStatsService.getMonthStats(selectedDate);
		        
		        modelAndView.addObject("keyMonthsStats", monthsStats.keySet());
		        modelAndView.addObject("valuesMonthsStats", monthsStats.values());
				
		        for (Double sumCat: monthsStats.values()) {
		        	sum+=sumCat;
		        	maxValueForChart = (sumCat > maxValueForChart) ? sumCat : maxValueForChart;
		        }
		        
		        modelAndView.addObject("sum", (int)sum);
		        modelAndView.addObject("selecteddate", selectedDateString);
		        
		        // 2. Add to the model the data about the average value	        
		        ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebit = opStatsService.getGlobalStatsMoyDebit();
		        modelAndView.addObject("globalStatsDataListMoyDebit", globalStatsDataListMoyDebit);
		         
		        //Intermediate datastore
		        Map<String, Double> dataMoyDebit = new LinkedHashMap<String, Double>();
		         
		         
		        for (GlobalStatsDataType stat : globalStatsDataListMoyDebit) {
		     		dataMoyDebit.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
		     		maxValueForChart = (-stat.getOpValue() > maxValueForChart) ? -stat.getOpValue() : maxValueForChart;
		     	}
			    modelAndView.addObject("keySetMoyDebit", dataMoyDebit.keySet());
			    modelAndView.addObject("valuesMoyDebit", dataMoyDebit.values());        
				
			}
			else {
				modelAndView.addObject("sum", (int)0);
		        modelAndView.addObject("selecteddate", "pas de date selectionn??e");
			}
	
			// add to model the max value for the chart
		    modelAndView.addObject("maxValueForChart", maxValueForChart);
			
	        // Main return statement 
	        return modelAndView;			
		}
	
	
	
	/* display the global information .
    * @param none.
    * @return Global information object.
    */
	@RequestMapping(value ={"/inputdataanalysis.html"})
	public ModelAndView inputdataanalysis(Model model) {
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("inputdataanalysis.html");						
		
		modelAndView.addObject("globalinfo", opStatsService.getGlobalInfo());	
		
        // Main return statement 
        return modelAndView;			
		}
	
	
	/**
    * Display list of Amazon operations (still not associated to the right category).
    * @param None.
    * @return list of Amazon operations.
    */
	@RequestMapping(value ={"/displayopamazon.html"})
	public ModelAndView displayopamazon(Model model) {
			
		// list of operations without association
		ArrayList<Operation> opBookDataAmazon;
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayopamazon.html");
		
		// Init the category to display
		//String opCategorieUserChoice = new String("EDF");
		
		// will be used for the value selected in the drop-down
		
		OperationCategory opCategoryObjectAmazon = new OperationCategory();
		opCategoryObjectAmazon.setName("EDF");
		opCategoryObjectAmazon.setType(CategoryType.TOUS);
		model.addAttribute("opCategoryObjectAmazon", opCategoryObjectAmazon);
		
	
	
		LogManager.LOGGER.log(Level.FINE,"Category object: " + opCategoryObjectAmazon + "  Category : " + opCategoryObjectAmazon.getName());
		
		
		LogManager.LOGGER.log(Level.FINE,"Category object from model: " + model.getAttribute("opCategoryObject") + "  Category : " + opCategoryObjectAmazon.getName());
		
		
		
		
		opBookDataAmazon = opStatsService.getOpBookDataAmazon();
		
		// ajouter dans le mod??le les op??rations sans associations
		modelAndView.addObject("opBookDataAmazon", opBookDataAmazon);
		
        // Main return statement 
        return modelAndView;
        
	}
	
	
	

	/* Display the list of operation from Amazon after selecting a new category for an operation.
    * @param opCategoryObjectAmazon the new category to apply to the operation.
    * @param opIdTxt the unique identifier of the operation.
    * @return Page to display along with corresponding date in the model.
    */
	@RequestMapping(value ={"/displayopamazonselect.html"}, method = RequestMethod.POST)
	public ModelAndView displayopamazonselect(@ModelAttribute("opCategoryObjectAmazon") OperationCategory opCategoryObjectAmazon, 
			@RequestParam("opIdTxt") String opIdTxt, Model model) {
			
		// operation results message 
		String resultMessage;
				
		// list of operations without association
		ArrayList<Operation> opBookDataAmazon;		
				
		// operation id;
		int opId = -1;
				
		// operation to update (if found)
		Operation opUpdate = null;
				
			LogManager.LOGGER.log(Level.FINE,"Category object after select: " + opCategoryObjectAmazon);
			
			OperationCategory opCategoryObjectFrompModel = (OperationCategory)model.getAttribute("opCategoryObjectAmazon");
			LogManager.LOGGER.log(Level.FINE,"Category object after select, from model: " + opCategoryObjectFrompModel + "  Category : " + opCategoryObjectFrompModel.getName());
			
			LogManager.LOGGER.log(Level.FINE,"execution of /displayopamazonselect.html + category name : " + opCategoryObjectAmazon.getName());

			
			LogManager.LOGGER.log(Level.FINE,"execution of /displayopamazonselect.html + opeeration ID : " + opIdTxt);
			
			// create model and view
			ModelAndView modelAndView = new ModelAndView("displayopamazonselect.html");
				
			
			//retrieve the list of operation and display the one with changed category	
			opBookDataAmazon = opStatsService.getOpBookDataAmazon();
			
			// verify if the input string for ID is empty
			if (!opIdTxt.isEmpty()) {
				
				opId = Integer.parseInt(opIdTxt);
				
				// search for the operation corresponding to the operation id provided by the user
				for (Operation op : opBookDataAmazon) {
					if (op.getOpId() == opId) {
						LogManager.LOGGER.log(Level.FINE,"execution of /displayopamazonselect.html + Libell?? op??ration  : " + op);
						opUpdate = op;			
					}
				}
			}
			
			// update operation and save or return error
			if (opUpdate != null) {
				LogManager.LOGGER.log(Level.FINE,"execution of /displayopamazonselect.html => changing association mode from  "
						+ opUpdate.getAssociation() + " to " + opCategoryObjectAmazon.getName());
				
				// update operation
				opUpdate.setAssociation(opCategoryObjectAmazon.getName());
				opUpdate.setaMode(AssociationMode.MANUAL);
				
				// recalculate stats and save operation
				// sauvegarde du fichier des op??rations avec les nouvelles mises ?? jour
				//saveFileResult = opStatsService.saveFinance();
				opStatsService.saveFinance();
				
				// ajouter dans le model le nombre d'op??rations ajout??es pour affichage dans la page HTML
				//modelAndView.addObject("saveFileResult", saveFileResult);
				
				// mettre ?? jour les statistiques
				opStatsService.updateStats();
				
				resultMessage = "Mise ?? jour de la cat??gorie : " + opUpdate.getAssociation() + " r??ussie pour l' op??ration : " + opUpdate;
				
			} else  { // error
				LogManager.LOGGER.log(Level.FINE,"execution of /displayopamazonselect.html => error => opid not found : " + opId);
				resultMessage = "Identifiant invalid ou non pr??cis??";
			}
			
			
			// get the update list of operation without category 	
			opBookDataAmazon = opStatsService.getOpBookDataAmazon();
		
			// ajouter dans le mod??le les op??rations sans associations
			modelAndView.addObject("opBookDataAmazon", opBookDataAmazon);
			
			// ajouter le message de retour
			modelAndView.addObject("resultMessage", resultMessage);
				
        // Main return statement 
        return modelAndView;
        
	}
	
		
	/**
    * Display list budget constraints data.
    * @param None.
    * @return list of data per categories.
    */
	@RequestMapping(value ={"/displayconstraintbudget.html"})
	public ModelAndView displayconstraintbudget(Model model) {	
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayconstraintbudget.html");
		
		// identify the max value for the display in between sums and average across all categories
		double maxValueForChart = 0; 
		
		
		// This section if for the first graph with expesnes per months and per category
		// categories data
		ArrayList<StatType1> lStats = opStatsService.getCategoriesStatsConstraint();
		
		// get the list of months
		ArrayList<StatDataHistory> categoryHistory = lStats.get(0).getDataHistory();
		
		//Intermediate datastore
        Map<String, Double> dataCategoryHistory = new LinkedHashMap<String, Double>();
       
        for (StatDataHistory stat : categoryHistory) {
    		dataCategoryHistory.put(stat.getMonthAndYear().toString(), (double)0);
    	}
		
		// add data to model
	    modelAndView.addObject("lStats", lStats);
	    modelAndView.addObject("valuesMonthsStats", dataCategoryHistory.keySet()); 
		
		// 2.this section is for the 2nd graph, consolidate expenses constraint budget
	    // 2.1 Add the constraint budget stats
		ArrayList<GlobalStatsDataType> budgetConstraint = opStatsService.getBudgetConstraint();
        modelAndView.addObject("budgetConstraint", budgetConstraint);
        
        //Intermediate datastore
        Map<String, Double> dataBudgetConstraint = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : budgetConstraint) {
        	dataBudgetConstraint.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
        	maxValueForChart = (-stat.getOpValue() > maxValueForChart) ? -stat.getOpValue() : maxValueForChart;
    	}
        modelAndView.addObject("keyBudgetConstraint", dataBudgetConstraint.keySet());
        modelAndView.addObject("valuesBudgetConstraint", dataBudgetConstraint.values());
	
        // 2.2 Add the average budget stats
 		ArrayList<GlobalStatsDataType> budgetAverageConstraint = opStatsService.getAverageBudgetConstraint();
        modelAndView.addObject("budgetAverage", budgetAverageConstraint);
         
        //Intermediate datastore
        Map<String, Double> dataAverageBudgetConstraint = new LinkedHashMap<String, Double>();
         
         
         for (GlobalStatsDataType stat : budgetAverageConstraint) {
        	 dataAverageBudgetConstraint.put(stat.getIndex(), -Precision.round(stat.getOpValue(),1));
        	 maxValueForChart = (-stat.getOpValue() > maxValueForChart) ? -stat.getOpValue() : maxValueForChart;
     	}
         modelAndView.addObject("keyBudgetAverageConstraint", dataAverageBudgetConstraint.keySet());
         modelAndView.addObject("valuesBudgetAverageConstraint", dataAverageBudgetConstraint.values());
	    
	    
        // 3. This secton is for the thirs grap (pie) 	
 		// Add the stats
 		ArrayList<GlobalStatsDataType> expensescatpie = opStatsService.getExpensesPerCategoryforPieChart(true);
         
        modelAndView.addObject("expensescatpie", expensescatpie);
       
        // add to model the max value for the chart
	    modelAndView.addObject("maxValueForChart", maxValueForChart);
        
	    // Main return statement 
	    return modelAndView;    
	    
	}
		
			
	// direct injection of attributes for list of categories
	@ModelAttribute("categorieslist")
	public List<OperationCategory> getCategoriesList() {
		List<OperationCategory> list = new ArrayList<>();
		list.addAll(opStatsService.getCategoriesList());
		return list;
	}
	
	
	/*
	// direct injection or error status in the header 
	@ModelAttribute("errorStatus")
	public List<Boolean> getErrorStatus() {
		List<Boolean> listError = new ArrayList<>();
		listError.add(true);
		return listError;
	}
	*/
	
	@ModelAttribute("errorStatus")
	public boolean getErrorStatus() {
		ArrayList<String[]> listArray = opStatsService.getOperationsWithErrors();
		boolean listError;
		if (listArray.size() > 0) {
			listError = true;
		} else {
			listError = false;
		}
		return listError;
	}
	
	
	//initial injection of object to manage history category selection 	
	@ModelAttribute("opCategoryObject")
	// will be used for the value selected in the drop-down
	public OperationCategory getOperationCategory() {
		OperationCategory opCategoryObject = new OperationCategory();
		opCategoryObject.setName("Eau");
		opCategoryObject.setType(CategoryType.TOUS);
		opCategoryObject.setAverageWindowsSize(Integer.toString(averageWindows));
		return opCategoryObject;
	}
	
}
