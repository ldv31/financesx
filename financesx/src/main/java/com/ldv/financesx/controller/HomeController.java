package com.ldv.financesx.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.GlobalStatsDataType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.service.OpStatsService;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.model.OperatorStats;

@Controller
public class HomeController {

	
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
		ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsService.getGlobalStatsSumDebit();
        modelAndView.addObject("globalStatsDataList", globalStatsDataList);
        
        //Intermediate datastore
        Map<String, Double> data = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : globalStatsDataList) {
        	// convert to positive value for display
    		data.put(stat.getIndex(), -stat.getOpValue());
    	}
        
        modelAndView.addObject("keySet", data.keySet());
        modelAndView.addObject("values", data.values());
       
        
        // B. add the global stats for sum of incomes per categories
        ArrayList<GlobalStatsDataType> globalStatsDataListSumCredit = opStatsService.getGlobalStatsSumCredit();
        modelAndView.addObject("globalStatsDataListSumCredit", globalStatsDataListSumCredit);
        
        //Intermediate datastore
        Map<String, Double> dataSumIncome = new LinkedHashMap<String, Double>();
        
        for (GlobalStatsDataType stat : globalStatsDataListSumCredit) {
        	dataSumIncome.put(stat.getIndex(), stat.getOpValue());
    	}
        
        modelAndView.addObject("keySetSumCredit", dataSumIncome.keySet());
        modelAndView.addObject("valuesSumCredit", dataSumIncome.values());
        
        
		
      
        // C. add the global stats for average of expenses per categories
 		ArrayList<GlobalStatsDataType> globalStatsDataListMoyDebit = opStatsService.getGlobalStatsMoyDebit();
        modelAndView.addObject("globalStatsDataListMoyDebit", globalStatsDataListMoyDebit);
         
        //Intermediate datastore
        Map<String, Double> dataMoyDebit = new LinkedHashMap<String, Double>();
         
         
        for (GlobalStatsDataType stat : globalStatsDataListMoyDebit) {
     		dataMoyDebit.put(stat.getIndex(), -stat.getOpValue());
     	}
	    modelAndView.addObject("keySetMoyDebit", dataMoyDebit.keySet());
	    modelAndView.addObject("valuesMoyDebit", dataMoyDebit.values());
        
        
        // D. add the global stats for average of income per categories
  		ArrayList<GlobalStatsDataType> globalStatsDataListMoyCredit = opStatsService.getGlobalStatsMoyCredit();
	    modelAndView.addObject("globalStatsDataListMoyDCredit", globalStatsDataListMoyCredit);
	      
	    //Intermediate datastore
	    Map<String, Double> dataMoyCredit = new LinkedHashMap<String, Double>();
	      
	      
	    for (GlobalStatsDataType stat : globalStatsDataListMoyCredit) {
	  		dataMoyCredit.put(stat.getIndex(), stat.getOpValue());
      	}
        modelAndView.addObject("keySetMoyCredit", dataMoyCredit.keySet());
        modelAndView.addObject("valuesMoyCredit", dataMoyCredit.values());
         
        // Main return statement 
        return modelAndView;
         
	}
	
	
	// page to display when seleted in the main page
	@RequestMapping(value ={"/displayStatsHistory.html"}, method = RequestMethod.GET)
	public ModelAndView displayStatsHistory(Model model) {
	
		// for debug 
		LogManager.LOGGER.log(Level.INFO,"execution of /displayStatsHistory.html ");

		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("historystats.html");
		
		// Initla category to display
		String opCategorieUserChoice = new String("EDF");
		
		// will be used for the value selected in the drop-down
		OperationCategory opCategoryObject = new OperationCategory();
		opCategoryObject.setName("EDF");
		opCategoryObject.setType(CategoryType.TOUS);
		model.addAttribute("opCategoryObject", opCategoryObject);
		//modelAndView.addObject("opCategoryObject", opCategoryObject);
		LogManager.LOGGER.log(Level.INFO,"Category object: " + opCategoryObject + "  Category : " + opCategoryObject.getName());
		LogManager.LOGGER.log(Level.INFO,"Category object from model: " + model.getAttribute("opCategoryObject") + "  Category : " + opCategoryObject.getName());
		
		// Add the global stats for sum of expenses per categories
		ArrayList<GlobalStatsDataType> categoryHistory = opStatsService.getCategoryHistory(opCategorieUserChoice).getCategoryHistory();	
		ArrayList<GlobalStatsDataType> categoryHistoryAverage = opStatsService.getCategoryHistoryAverage(opCategoryObject.getName()).getCategoryHistory();
		modelAndView.addObject("categoryHistory", categoryHistory);
		modelAndView.addObject("categoryHistoryAverage", categoryHistoryAverage);
        
        //Intermediate datastore
        Map<String, Double> dataCategoryHistory = new LinkedHashMap<String, Double>();
        Map<String, Double> dataCategoryHistoryAverage = new LinkedHashMap<String, Double>();
        
        for (GlobalStatsDataType stat : categoryHistory) {
    		dataCategoryHistory.put(stat.getIndex(), stat.getOpValue());
    	}
        
        
        for (GlobalStatsDataType stat : categoryHistoryAverage) {
    		dataCategoryHistoryAverage.put(stat.getIndex(), stat.getOpValue());
    	}
        
        modelAndView.addObject("keySetCategoryHistory", dataCategoryHistory.keySet());
        modelAndView.addObject("valuesCategoryHistory", dataCategoryHistory.values());

        modelAndView.addObject("keySetCategoryHistoryAverage", dataCategoryHistoryAverage.keySet());
        modelAndView.addObject("valuesCategoryHistoryAverage", dataCategoryHistoryAverage.values()); 
        
        modelAndView.addObject("CategoryNameDefault", opStatsService.getCategoryHistory(opCategorieUserChoice).getOpCategory());
        modelAndView.addObject("CategoryName", opStatsService.getCategoryHistory(opCategorieUserChoice).getOpCategory() + ": valeures");
        modelAndView.addObject("CategoryNameAverage", opStatsService.getCategoryHistory(opCategorieUserChoice).getOpCategory() + ": Moyenne" );
        
        // Main return statement 
        return modelAndView;
        
	}
	
	// page to display after selecting a category in the drop-down
	@RequestMapping(value ={"/historystatsselect.html"}, method = RequestMethod.POST)
	public ModelAndView displayStatsHistorySelect(@ModelAttribute("opCategoryObject") OperationCategory opCategoryObject, Model model) {
			
		LogManager.LOGGER.log(Level.INFO,"Category object after select: " + opCategoryObject);
		
		OperationCategory opCategoryObjectFrompModel = (OperationCategory)model.getAttribute("opCategoryObject");
		LogManager.LOGGER.log(Level.INFO,"Category object after select, from model: " + opCategoryObjectFrompModel + "  Category : " + opCategoryObjectFrompModel.getName());
		
		LogManager.LOGGER.log(Level.INFO,"execution of /historystatsselect.html + category name : " + opCategoryObject.getName());

		// create model and view
		ModelAndView modelAndView = new ModelAndView("historystatsselect.html");
			
		
		// Add the global stats for sum of expenses per categories + average
		ArrayList<GlobalStatsDataType> categoryHistory = opStatsService.getCategoryHistory(opCategoryObject.getName()).getCategoryHistory();
		ArrayList<GlobalStatsDataType> categoryHistoryAverage = opStatsService.getCategoryHistoryAverage(opCategoryObject.getName()).getCategoryHistory();
		modelAndView.addObject("categoryHistory", categoryHistory);
		modelAndView.addObject("categoryHistoryAverage", categoryHistoryAverage);
        
        //Intermediate datastore
        Map<String, Double> dataCategoryHistory = new LinkedHashMap<String, Double>();
        Map<String, Double> dataCategoryHistoryAverage = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : categoryHistory) {
    		dataCategoryHistory.put(stat.getIndex(), stat.getOpValue());
    	}
        
        for (GlobalStatsDataType stat : categoryHistoryAverage) {
    		dataCategoryHistoryAverage.put(stat.getIndex(), stat.getOpValue());
    	}
        
        modelAndView.addObject("keySetCategoryHistory", dataCategoryHistory.keySet());
        modelAndView.addObject("valuesCategoryHistory", dataCategoryHistory.values());
        
        modelAndView.addObject("keySetCategoryHistoryAverage", dataCategoryHistoryAverage.keySet());
        modelAndView.addObject("valuesCategoryHistoryAverage", dataCategoryHistoryAverage.values());
        
        modelAndView.addObject("CategoryNameDefault", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory());     
        modelAndView.addObject("CategoryName", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory() + ": valeures");
        modelAndView.addObject("CategoryNameAverage", opStatsService.getCategoryHistory(opCategoryObject.getName()).getOpCategory() + ": moyenne");
	
        // Main return statement 
        return modelAndView;
        
	}
	
	
	@RequestMapping(value ={"/displaygainsandlosses.html"})
	public ModelAndView displaygainsandlosses(Model model) {
	
		// create model and view
		ModelAndView modelAndView = new ModelAndView("gainsandlosses.html");
		
		
		// Add the gains / losses  stats
		ArrayList<GlobalStatsDataType> gainsandlosses = opStatsService.getGainsandlosses();
        modelAndView.addObject("gainsandlosses", gainsandlosses);
        
        //Intermediate datastore
        Map<String, Double> dataGainsandlosses = new LinkedHashMap<String, Double>();
        
        
        for (GlobalStatsDataType stat : gainsandlosses) {
        	dataGainsandlosses.put(stat.getIndex(), stat.getOpValue());
    	}
        modelAndView.addObject("keySetGainsandlosses", dataGainsandlosses.keySet());
        modelAndView.addObject("valuesGainsandlosses", dataGainsandlosses.values());
	
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
        	dataBudget.put(stat.getIndex(), -stat.getOpValue());
    	}
        modelAndView.addObject("keyBudget", dataBudget.keySet());
        modelAndView.addObject("valuesBudget", dataBudget.values());
	
        // Add the average budget stats
 		ArrayList<GlobalStatsDataType> budgetAverage = opStatsService.getAverageBudget();
         modelAndView.addObject("budgetAverage", budgetAverage);
         
         //Intermediate datastore
         Map<String, Double> dataAverageBudget = new LinkedHashMap<String, Double>();
         
         
         for (GlobalStatsDataType stat : budgetAverage) {
         	dataAverageBudget.put(stat.getIndex(), -stat.getOpValue());
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
        	dataReimbursement.put(stat.getIndex(), stat.getOpValue());
    	}
        modelAndView.addObject("keyReimbursement", dataReimbursement.keySet());
        modelAndView.addObject("valuesReimbursement", dataReimbursement.values());
	
        // Add the average budget including reimbursements stats
 		ArrayList<GlobalStatsDataType> averageBudgetWithReimbursements = opStatsService.getAverageBudgetWithReimbursements();
         modelAndView.addObject("averageBudgetWithReimbursements", averageBudgetWithReimbursements);
         
         //Intermediate datastore
         Map<String, Double> dataAverageBudgetWithReimbursements = new LinkedHashMap<String, Double>();
         
         
         for (GlobalStatsDataType stat : averageBudgetWithReimbursements) {
         	dataAverageBudgetWithReimbursements.put(stat.getIndex(), -stat.getOpValue());
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
		ArrayList<GlobalStatsDataType> expensescatpie = opStatsService.getExpensesPerCategoryforPieChart();
		ArrayList<GlobalStatsDataType> expensesvsincomecatpie = opStatsService.getExpensesPerCategoryVsIncomeForPieChart();
        
        modelAndView.addObject("expensescatpie", expensescatpie);
        modelAndView.addObject("expensesvsincomecatpie", expensesvsincomecatpie);
        	
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// chargement de nouvelles opérations
	@RequestMapping(value ={"/displayuploadoperations.html"})
	public ModelAndView displayuploadoperations(Model model) {
			
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("upload.html");
		
		
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// Résultat du chargement des nouvelles opérations (statut, statistiques et sauvegarde si OK)
	@RequestMapping(value ={"/displayuploadresults.html"})
	public ModelAndView displayuploadresults(@RequestParam("uploadfilepath") String uploadfilepath, Model model) throws Exception {
		
		String saveFileResult;
		
		ModelAndView modelAndView;
		
		// structure hosting the results of the upload operation
		OperationsResults OperationsResults;
		
		// array to store data added from the upload file 
		ArrayList<Operation> opBookAddedData;
		
			
		LogManager.LOGGER.log(Level.INFO,"Upload file path: " + uploadfilepath);
		
		String uploadFilePathFrompModel = (String)model.getAttribute("uploadfilepath");
		
		
		// execution de la mise à jour des données
		OperationsResults = opStatsService.uploadNewData(uploadfilepath);
		
		// récupérer le résultats de l'opération d'upload
		opBookAddedData = OperationsResults.getOpBookOperationData();
		
		// upload operation success
		if (OperationsResults.getResult() == ErrorCodeAndMessage.OPERATION_SUCCES) {
			// create model and view
			modelAndView = new ModelAndView("uploadresults.html");
			
			// ajouter dans le model le chemin vers le fichier pour affichage dans la page HTML
			modelAndView.addObject("uploadfilepath", uploadfilepath);
			
			// ajouter dans le model le nombre d'opérations ajoutées pour affichage dans la page HTML
			modelAndView.addObject("uploadoperationsize", opBookAddedData.size());
			
			LogManager.LOGGER.log(Level.INFO, "HomeController => size of new data array :" + opBookAddedData.size());
			
			// sauvegarde du fichier des opérations avec les nouvelles mises à jour
			saveFileResult = opStatsService.saveFinance();
			
			// ajouter dans le model le nombre d'opérations ajoutées pour affichage dans la page HTML
			modelAndView.addObject("saveFileResult", saveFileResult);
			
			// mettre à jours les statistiques
			opStatsService.updateStats();	
		}
		else {
			// create model and view
			modelAndView = new ModelAndView("error.html");
			
			// ajouter dans le model l'erreur pour affichage dans la page HTML
			modelAndView.addObject("errorMessage", OperationsResults.getResult().getTxtType());
	
		}
					
        // Main return statement 
        return modelAndView;
        
	}
	
	
	// affichage des opérations sans associations
	@RequestMapping(value ={"/displayopnoassociation.html"})
	public ModelAndView displayopnoassociation(Model model) {
			
		// list of operations without association
		ArrayList<Operation> opBookDataWithoutAssociation;
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayupopwithoutassociations.html");
		
		opBookDataWithoutAssociation = opStatsService.getOpWithoutAssociation();
		
		// ajouter dans le modèle les opérations sans associations
		modelAndView.addObject("opBookDataWithoutAssociation", opBookDataWithoutAssociation);
		
        // Main return statement 
        return modelAndView;
        
	}
	
	
	
	// affichage specifique pour alimentation et hygiène
	@RequestMapping(value ={"/displayalimspecific.html"})
	public ModelAndView displayalimspecific(Model model) {
				
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displayalimspecific.html");
		
		// lisr of value per operator form alimentation/hygiène
		ArrayList<OperatorStats> foodSumPerOperator = opStatsService.getFoodSumPerOperator();
					
		//Intermediate datastore
        Map<String, Double> foodSumPerOperatorMap = new LinkedHashMap<String, Double>();
        
        
        for (OperatorStats stat : foodSumPerOperator) {
        	foodSumPerOperatorMap.put(stat.getName(), -stat.getConsolidatedSum());
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
		
		// create model and view
		ModelAndView modelAndView = new ModelAndView("displaymonthstats.html");
					
		//Intermediate datastore
        Map<String, Double> monthsStats = opStatsService.getMonthStats();
        
        modelAndView.addObject("keyMonthsStats", monthsStats.keySet());
        modelAndView.addObject("valuesMonthsStats", monthsStats.values());
		
        for (Double sumCat: monthsStats.values()) {
        	sum+=sumCat;
        }
        
        modelAndView.addObject("sum", (int)sum);
        
		
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
		
		opBookDataAmazon = opStatsService.getOpBookDataAmazon();
		
		// ajouter dans le modèle les opérations sans associations
		modelAndView.addObject("opBookDataAmazon", opBookDataAmazon);
		
        // Main return statement 
        return modelAndView;
        
	}
	
	
	
	
	
	
	
	// direct injection of attributes for list of catégories
	@ModelAttribute("categorieslist")
	public List<OperationCategory> getCategoriesList() {
		List<OperationCategory> list = new ArrayList<>();
		list.addAll(opStatsService.getCategoriesList());
		return list;
	}
	
	
	
}
