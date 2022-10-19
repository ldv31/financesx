package com.ldv.financesx;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

import com.opencsv.CSVWriter;

import java.io.FileWriter;


public class CsvFileHelper {

	public final static char SEPARATOR = ';';
	
	public static String getResourcePath(String fileName) {
	       final File f = new File("");
	       final String dossierPath = f.getAbsolutePath() + File.separator + fileName;
	       return dossierPath;
	   }

	public static File getResource(String fileName) {
	       final String completeFileName = getResourcePath(fileName);
	       LogManager.LOGGER.log(Level.FINE,"Ouverture du fichier : " + completeFileName);
	       File file = new File(completeFileName);
	       return file;
	   }
	
	public static File getResourceAbs(String fileName) {
	       LogManager.LOGGER.log(Level.FINE,"Ouverture du fichier : " + fileName);
	       File file = new File(fileName);
	       return file;
	   }
	   
	  
	public static boolean writeToCsv(String fileName, ArrayList<String[]> content){
        boolean rValue = true;
		
		//String csv = fileName;
        File csv = new File(fileName);
        try{
          
        	// Use "Cp1252" to support french Caracters
        	OutputStreamWriter o = new OutputStreamWriter(new FileOutputStream(csv), "Cp1252");
        	
        	FileWriter outputfile = new FileWriter(csv);
        	
        	CSVWriter writer = new CSVWriter(o, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        	
            writer.writeAll(content);

            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
            LogManager.LOGGER.log(Level.SEVERE,"File Save failed : \n" + e.getMessage());
            rValue = false;
        }
        
        return rValue;
	}
	
	public static ArrayList<String> readFile(File file) throws Exception {

	        ArrayList<String> result = new ArrayList<String>();

	        InputStreamReader fr;
			try {
	 
				fr = new InputStreamReader(new FileInputStream(file), "Cp1252");
				
				BufferedReader br = new BufferedReader(fr);
				
		        // Declaring a string variable
		        String st;
		        // Condition holds true till
		        // there is character in a string
		        // Refactoring LD : besoin de mettre un try/Catch pour evite l'utilisation d'un throw sur toutes les fonctions appelantes
		        while ((st = br.readLine()) != null) {
		 
		            // Print the string
		        	result.add(st);
		            //System.out.println(st);
		        }
		            
				/*
				BufferedReader br
	            = new BufferedReader(new FileReader(file));
	 
		        // Declaring a string variable
		        String st;
		        // Condition holds true till
		        // there is character in a string
		        while ((st = br.readLine()) != null) {
		 
		            // Print the string
		        	result.add(st);
		            System.out.println(st);
		        }
		        */
		        
				
			 		        
				/*
				// Use "Cp1252" to support french Caracters
				fr = new InputStreamReader(new FileInputStream(file), "Cp1252");
				
				BufferedReader br = new BufferedReader(fr);
				
				
				String line;
				boolean loopOK = true;
								
				do {
					try {
						line = br.readLine();
					
						if (line != null) {
							result.add(line);
						}
						else
							loopOK = false;
					
					
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						loopOK = false;
					}
				} while (loopOK == true);
				

		        try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        */

				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				LogManager.LOGGER.log(Level.SEVERE,"Le fichier demandé n'existe pas");
				System.exit(1);
			}
		
			return result;
	    }
	   
	
	public static  ArrayList<String[]> extractRawOperationData (File file) throws Exception {
		ArrayList<String> lines = CsvFileHelper.readFile(file);

		ArrayList<String[] > data = new ArrayList<String[] >(lines.size());
        String sep = new Character(SEPARATOR).toString();
        for (String line : lines) {
            String[] oneData = line.split(sep);
            data.add(oneData);             
        }
        
        return data;
    }
	
	public static void printRawOperationData (ArrayList<String[]> array) {
		String forPrint;
		
		LogManager.LOGGER.log(Level.FINE,"\n***********************************************************");
		LogManager.LOGGER.log(Level.FINE,"******************** CVS File Content *********************");
		LogManager.LOGGER.log(Level.FINE,"***********************************************************\n");
		
		for (String [] arrayStringElement: array) {
			forPrint = "";
			for (String string: arrayStringElement) {
				forPrint += " | " + string;
			}
			forPrint += " | ";
			LogManager.LOGGER.log(Level.FINE,forPrint);
		}
	}
	
}
