package com.ldv.financesx.rest;

import java.util.ArrayList;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ldv.financesx.LogManager;
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.GlobalStatsDataType;
import com.ldv.financesx.model.OperationCategory;
import com.ldv.financesx.service.OpStatsService;

@RestController
//@Controller
@RequestMapping(path = "/api/contact")
public class HomeRestController {
	
	private final OpStatsService opStatsService;
	
	@Autowired
	public HomeRestController(OpStatsService opStatsService) {
		this.opStatsService = opStatsService;
	}
		
	
	/**
    * Return the status of the server.
    * @param None.
    * @return list of data per categories.
    */
	@GetMapping("/getServerStatus")
	//@ResponseBody
	public String getServerStatus(@RequestParam(name = "status") String status) {
		return status + " : " + "Server Status OK";
	}
	
	
	/**
    * Return the global statistics.
    * @param None.
    * @return Global statistics.
    */
	@GetMapping("/displayGlobaStats")
	//@ResponseBody
	public ArrayList<GlobalStatsDataType> displayGlobaStats() {
		
		ArrayList<GlobalStatsDataType> globalStatsDataList = opStatsService.getGlobalStatsSumDebit(false);
		
		return globalStatsDataList;
	}
	
	
	
	/**
    * Return the history statistics.
    * @param category .
    * @return history statistics.
    */
	@GetMapping("/displayStatsHistory")
	//@ResponseBody
	public ArrayList<GlobalStatsDataType> displayStatsHistory(@RequestBody OperationCategory operationCategory) {
		
		
		//Return the history corresponding to the category provided in the request;	
		ArrayList<GlobalStatsDataType> categoryHistory = opStatsService.getCategoryHistory(operationCategory.getName()).getCategoryHistory();	

		
		return categoryHistory;
	}
	

}
