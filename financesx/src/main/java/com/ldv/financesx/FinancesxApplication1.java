package com.ldv.financesx;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import com.ldv.financesx.config.ProjectConfig;
import com.ldv.financesx.OperationStatistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ldv.financesx.model.OperationsBook;
import com.ldv.financesx.config.ProjectConfig;

@SpringBootApplication
public class FinancesxApplication1 {

	public static void main(String[] args) {
		
		// program start log
		System.out.println("Lancement du programme FinanceSX");
		System.out.println("Initialisation du context d'application");

		
		//SpringApplication.run(FinancesxApplication1.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(FinancesxApplication1.class,args);
			
		// Retrieve the bean for logging management
		LogManager logManager = context.getBean("logManager", LogManager.class);
		
		// Log: "start of the program"
		logManager.LOGGER.log(Level.INFO,"Execution du programme FinanceSX : STARTED");
	    
	    // Afficher la class path
		logManager.LOGGER.log(Level.INFO,"ClassPath : " + System.getProperty("java.classpath"));
		
		
		
	}

}
