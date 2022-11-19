package com.ldv.financesx.model;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ldv.financesx.CsvFileHelper;
import com.ldv.financesx.FileMgtInterface;
import com.ldv.financesx.LogManager;
import com.ldv.financesx.OperationsResults;

import com.ldv.financesx.DialogMessage;
import com.ldv.financesx.ErrorCodeAndMessage;

@Component
public class OperationsBook implements FileMgtInterface {

	// file location
	// updates paths
	@Value("${associationFile.Path}")
	private String associationFileName;

	@Value("${categories.Path}")
	private String categoriesFileName;

	// contains all the operation
	@Value("${financeFile.Path}")
	private String csvFileName;

	@Value("${backup.Path}")
	private String backupPath;

	// store the list of operation extracted from CVS file
	ArrayList<String[]> aString = new ArrayList<String[]>();
	// store the list of association extracted from CVS file
	ArrayList<String[]> bString = new ArrayList<String[]>();
	// store the list of categories extracted from CVS file
	ArrayList<String[]> categoriesString = new ArrayList<String[]>();
	ArrayList<Operation> opBookData = new ArrayList<Operation>();
	
	// store the list of operation with errors (missing element in the operation)
	ArrayList<String[]> operationsWithError = new ArrayList<String[]>();
	
	// list of operations without association
	ArrayList<Operation> opBookDataWithoutAssociation = new ArrayList<Operation>();
	ArrayList<OperationCategory> categoriesList = new ArrayList<OperationCategory>();
	int countWithoutAssociation = 0;
	double sumValueCredit = 0;
	double sumValueDebit = 0;

	// used to backup and update fiance.csv file
	File source;
	File dest;

	// operation Book stats data
	OperationBookStats operationBookStats = new OperationBookStats();
	
	@PostConstruct
	public void OperationsBookSetup() throws Exception {

		// compute execution time:
		Instant start = Instant.now();

		LogManager.LOGGER.log(Level.INFO, "Chargement des opérations dans le livre de compte: START => " + start);
		LogManager.LOGGER.log(Level.FINE, "Lecture du fichier des opérations");

		// livre de compte (base de donnée interne des opérations)
		opBookData = new ArrayList<Operation>();

		// 0uverture du fichier CSV (finance) des opérations à importer
		// final File file = CsvFileHelper.getResource(csvFileName);
		final File file = CsvFileHelper.getResourceAbs(csvFileName);
		if (!file.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + csvFileName);
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			DialogMessage.displayMessage("Finance file does not exists in : " + "\"" + csvFileName + "\""
					+ System.getProperty("line.separator") + "Exiting program");
			System.exit(1);
		}

		// extraction des données du fichier CSV (finance)
		aString = CsvFileHelper.extractRawOperationData(file);

		// transformation des données CSV en livre de compte
		fillOperationBookData(aString, opBookData, operationBookStats);

		//System.out.println("****************************>>>>>  " + operationBookStats.getStartOperationDate());
		//System.out.println("****************************>>>>>  " + operationBookStats.getEndOperationDate());

		// ouverture du fichier des associations
		// final File associationFile = CsvFileHelper.getResource(associationFileName);
		final File associationFile = CsvFileHelper.getResourceAbs(associationFileName);
		if (!associationFile.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + associationFileName);
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			DialogMessage.displayMessage("Association file does not exists in : " + "\"" + associationFileName + "\""
					+ System.getProperty("line.separator") + "Exiting program");
			System.exit(1);
		}

		// extraction des données du fichier des association
		bString = CsvFileHelper.extractRawOperationData(associationFile);
		CsvFileHelper.printRawOperationData(bString);

		// ouverture du fichier des categories
		final File categoriesFile = CsvFileHelper.getResourceAbs(categoriesFileName);
		if (!categoriesFile.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + categoriesFileName);
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			DialogMessage.displayMessage("Categories file does not exists in : " + "\"" + categoriesFileName + "\""
					+ System.getProperty("line.separator") + "Exiting program");
			System.exit(1);
		}

		// extraction des données du fichier des association
		categoriesString = CsvFileHelper.extractRawOperationData(categoriesFile);
		CsvFileHelper.printRawOperationData(bString);

		// Mise a jour des association dans le livre de compte
		fillAssociationBookData();

		// display operations without associations and set the corresponding values
		opWithoutAssociation();

		// generation de la liste des catégories
		generateCategoriesList();

		// Sauvegarde du fichier des operations après l'ajout des associations
		saveFinance();

		// backup du fichier d'entrée finance.csv et du fichier des associations
		backupFiles();

		// affichage du livre de compte.
		LogManager.LOGGER.log(Level.FINE, toString());

		// livre de compte chargé
		// compute execution time:
		Instant end = Instant.now();
		Duration res = Duration.between(start, end);
		LogManager.LOGGER.log(Level.INFO, "Chargement des opérations dans le livre de compte: COMPLETED => " + res);
	}

	// Sauvegarde le fichier des associations
	public boolean saveAssociation() {

		ArrayList<String[]> associationString = new ArrayList<String[]>();

		// add the header for association file
		// String [] lAssoHeader = {"Rubrique","Libellé","Opération"};
		// associationString.add(lAssoHeader);

		// add the associations in association file
		for (String[] assoString : bString) {
			associationString.add(assoString);
		}

		// Ecriture du fichier des associations
		return (CsvFileHelper.writeToCsv(associationFileName, associationString));

	}

	// sauvegarde l'operation book dans le réperttoire courant (par exemple suite à
	// la modification d'une association)
	public boolean saveFinance() {

		// create the table of string for CSV writer : finance file and association
		// *****************************************
		ArrayList<String[]> financeString = new ArrayList<String[]>();

		// add the header for finance file
		String[] lheader = { "Date opération", "Date valeur", "libellé", "Débit", "Crédit", "Association",
				"Mode Association" };
		financeString.add(lheader);

		// add the operations in finance file
		for (Operation opLocal : opBookData) {
			financeString.add(opLocal.toStringArray());
		}

		// Ecriture du fichier d'export
		return (CsvFileHelper.writeToCsv(csvFileName, financeString));
	}

	
	/* Objectif : Mets à jours la lsite des opérations 
	 * return : liste des opérations ajoutées + resultat de l'opération
	 */
	public OperationsResults loadNewFinanceData(String uploadfilepath) throws Exception {

		File loadFile = new File(uploadfilepath);
		OperationsResults operationsResults = new OperationsResults();
		ArrayList<String[]> newDataOpString;
		ArrayList<Operation> opBookNewData = new ArrayList<Operation>();
		ArrayList<Operation> opBookAddedData = new ArrayList<Operation>();
		
		// file deos not exist
		if (!loadFile.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + loadFile.getAbsolutePath());
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			operationsResults.setResult(ErrorCodeAndMessage.FILE_DOES_NOT_EXIST);
			//System.exit(1);
		} // file exist
		else {
			// extraction des données du fichier CSV (finance)
			try {
				newDataOpString = CsvFileHelper.extractRawOperationData(loadFile);
	
				// transformation des données CSV en livre de compte
				try {
					fillOperationBookData(newDataOpString, opBookNewData, operationBookStats);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			LogManager.LOGGER.log(Level.FINE, "size of new data array :" + opBookNewData.size());
	
			// index of insertion
			int insertIndex = 0;
	
			// get the date of the last operation in the main operation book
			LocalDate OPBookLastOpDate = opBookData.get(0).getDateOp();

			// get the last operation from the main operation book
			Operation opLastMain = opBookData.get(0);
	
			// last main operation reached => stop adding operation from new operation to
			// the main operation
			boolean lastMainOperatioReached = false;
	
			// add new data operation only if the date is not already in the main operation
			// book
			// Date of new operation is >= to the latest operation in the main databook
			// if Date of new operation = to the latest operation in the main databook then
			// compare the opération and if different add them
			for (Operation newOpCandidate : opBookNewData) {
				LocalDate newOpDate = newOpCandidate.getDateOp();
	
				if (newOpDate.isAfter(OPBookLastOpDate)) {
					// new operation to be added (date after the one of last operation in the
					// dataOperation book)
					opBookData.add(insertIndex, newOpCandidate);
					opBookAddedData.add(insertIndex, newOpCandidate);
					System.out.println("Adding new op date > , index :" + insertIndex);
					insertIndex++;
				}
	
				if (newOpDate.isEqual(OPBookLastOpDate)) {
					if (!newOpCandidate.equalLibDebCred(opLastMain) && (lastMainOperatioReached == false)) {
						opBookData.add(insertIndex, newOpCandidate);
						opBookAddedData.add(insertIndex, newOpCandidate);
						System.out.println("Adding new op date = , index :" + insertIndex);
						insertIndex++;
					} else {
						lastMainOperatioReached = true;
					}
				}
			}
	
			// print to user the number of operation added
			LogManager.LOGGER.log(Level.INFO, "Number of operation added:  " + insertIndex);
	
			// Mise a jour des association dans le livre de compte
			fillAssociationBookData();
			LogManager.LOGGER.log(Level.INFO, "fillAssociationBookData updated");	
			
			// return the list of operation to be printed
			operationsResults.setResult(ErrorCodeAndMessage.OPERATION_SUCCES);
			operationsResults.setOpBookOperationData(opBookAddedData);
		
		}		
		// Code to be done : replace the return statement by operationsResults an propagate on repository and service up to error.html 
		return operationsResults;
	}

	// backup des fichiers finance et association pour la protection des données
	public void backupFiles() {

		// backup du fichier d'entrée finance.csv
		LocalDate todaysDate = LocalDate.now();
		source = new File(csvFileName);
		dest = new File(backupPath + "finance.csv" + "." + todaysDate);

		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			LogManager.LOGGER.log(Level.SEVERE, "Finance.csv backup failed");
			e.printStackTrace();
		}

		// Sauvegarde du fichier d'association
		source = new File(associationFileName);
		dest = new File(backupPath + "association.csv" + "." + todaysDate);

		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			LogManager.LOGGER.log(Level.SEVERE, "Association.csv backup failed");
			e.printStackTrace();
		}
	}
 
	/**
	 * Retrieve the list of categories from the csv file
	 * @input: None 
	 * @return: ArrayList<OperationCategory> 
	 */
	public void opWithoutAssociation() {
		
		// reset operation without association list
		opBookDataWithoutAssociation = new ArrayList<Operation>();
		
		LogManager.LOGGER.log(Level.FINE, "********************************************************");
		LogManager.LOGGER.log(Level.FINE, "******** Liste des opérations sans associations ********");
		LogManager.LOGGER.log(Level.FINE, "********************************************************");
	
		for (Operation op : opBookData) {
			if (op.getaMode() == AssociationMode.NONE) {
				countWithoutAssociation++;
				sumValueCredit += op.getCredit();
				sumValueDebit += op.getDebit();
				LogManager.LOGGER.log(Level.FINE, op.toString());
				opBookDataWithoutAssociation.add(op);
			}
		}
		LogManager.LOGGER.log(Level.FINE, "********************************************************\n");
		LogManager.LOGGER.log(Level.FINE, "Total crédit sans association : " + (int) sumValueCredit);
		LogManager.LOGGER.log(Level.FINE, "Total débit sans association : " + (int) sumValueDebit);
	}

	
	public ArrayList<Operation> getOpBookDataWithoutAssociation() {
		return opBookDataWithoutAssociation;
	}
	
	
	/**
    * Return the list of Amazon operations (still not associated to the right category).
    * @param None.
    * @return list of Amazon operations.
    */	
	public ArrayList<Operation> getOpBookDataAmazon() {
		
		ArrayList<Operation> opAmazon = new ArrayList<Operation>();
		
		for (Operation opLocal : opBookData) {
			if (opLocal.getAssociation().equals(CategoriesList.AMAZON.getTxtType()) ) {
				opAmazon.add(opLocal);
			}					   
		}	
		return opAmazon;
	}
	

	// inputString est le tableau des opérations extraites du fichier "finance.csv"
	// et inputBookData est le livre de compte dans lequel les opération seront
	// formalisées
	public void fillOperationBookData(ArrayList<String[]> inputString, ArrayList<Operation> inputBookData,
			OperationBookStats operationBookStats) throws ParseException {
		boolean csvHeader = true;
		double lCredit = 0;
		double lDebit = 0;
		AssociationMode localMode = AssociationMode.NONE;
		// String lassociation = "Aucune";
		String lassociation = CategoriesList.AUCUNE.toString();

		// count the lines in the file, used to identify the lines with error
		int lineCounter = 1;
		
		for (String[] lString : inputString) {
			// Ignorer le header présent dans le fichier CSV
			if (csvHeader == false) {

				// Le fichier CSV contient les élémnent suivants:
				// 1 - Date opération
				// 2 - Date valeure
				// 3 - Libellé
				// 4 - Débit
				// 5 - Crédit
				// 6 - association (optionnel)
				// 7 - mode d'association (optionnel)

				
				
				// Process only if : 
				//    - lString.length >= 4 (at least  dates + libelle + débit are provided) => CVS file or update
				//			- 1 to 3 do not provide enought information no credit nor debit
				//			- 4 is for update with debit
				//			- 5 is for update with credit
				//			- 6 is not possible (error case), see below
				//			- 7 is for finance CVS file 
				// 		- and lString.length != 6. This is an error in between update and CVS finance files
				// if not, add the line to the error table an move to the next one
				// the operation with error can by displayed in the IHM
				if ((lString.length >= 4) && (lString.length != 6)){
									
					// case of reading of CVS file a line does  have 7  
					if (lString.length == 7){
						// Renseigner le Mode d'association
						if (AssociationMode.fromString(lString[6]) != null)
							localMode = AssociationMode.fromString(lString[6]);
						else
							localMode = AssociationMode.NONE;
						
						// Renseigner l"association
						lassociation = lString[5];	
						
						// update the credit value
						lCredit = lString[4].isEmpty() ? 0 : Double.parseDouble(lString[4].replaceAll(",", "."));					
						
					}
						
					// case of operation from update files for credit 
					if (lString.length == 5){
						// set default values for for localMode and lassociation
						localMode = AssociationMode.NONE;
						lassociation = CategoriesList.AUCUNE.toString();
						
						// update the credit value
						lCredit = lString[4].isEmpty() ? 0 : Double.parseDouble(lString[4].replaceAll(",", "."));
					}
					
					// case of operation from update files for debit
					if (lString.length == 4){
						// set default values for for localMode and lassociation
						localMode = AssociationMode.NONE;
						lassociation = CategoriesList.AUCUNE.toString();
						
						// update the credit value to 0 (this is a débit line)
						lCredit = 0;
					}
					
														
					// get the expense value applicable to CSV file and update
					lDebit = lString[3].isEmpty() ? 0 : Double.parseDouble(lString[3].replaceAll(",", "."));
														
					Operation op = new Operation(lString[0], lString[1], lString[2], lDebit, lCredit,
							lassociation, localMode);
					inputBookData.add(op);
	
					// identifier les dates mini et maxi des opération.
					// date mini
					LocalDate dateOp = LocalDate.parse(lString[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	
					if (operationBookStats.getStartOperationDate() == null) {
						operationBookStats.setStartOperationDate(dateOp);
					}
	
					int result = operationBookStats.getStartOperationDate().compareTo(dateOp);
	
					if (result == 0) {
						// nothing to be done dates are equals
					} else if (result > 0) {
						// startOperationDate is after operation date
						operationBookStats.setStartOperationDate(dateOp);
						// System.out.println("startOperationDate =============> " +
						// operationBookStats.getStartOperationDate());
					} else if (result < 0) {
						// startOperationDate is before operation date nothing to do
					} else {
						System.out.println("How to get here?");
					}
	
					// date maxi
					LocalDate dateOpMax = LocalDate.parse(lString[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	
					if (operationBookStats.getEndOperationDate() == null) {
						operationBookStats.setEndOperationDate(dateOpMax);
					}
	
					int resultMax = operationBookStats.getEndOperationDate().compareTo(dateOpMax);
	
					if (resultMax == 0) {
						// nothing to be done dates are equals
					} else if (resultMax > 0) {
						// startOperationDate is after operation date nothing to do
					} else if (resultMax < 0) {
						// startOperationDate is before operation date nothing to do
						operationBookStats.setEndOperationDate(dateOpMax);
						// System.out.println("startOperationDate =============> " +
						// operationBookStats.getEndOperationDate());
					} else {
						System.out.println("How to get here?");
					}
				} else {
					
					// add the line in error for potential later display
					operationsWithError.add(lString);
					
					// log the error with line number and content
					String delimiter = ";";
					LogManager.LOGGER.log(Level.SEVERE, "Error Reading CVS file in line:  " 
					+ lineCounter + " with content : " +  String.join(delimiter, lString));
				}
				
			} else
				csvHeader = false;
			
			// increase the line counter (used to identify the lines with errors)
			lineCounter++;
		}
	}

	private void fillAssociationBookData() {
		boolean associationNotFound = true;
		int i = 1;
		String[] lbString;

		for (Operation opLocal : opBookData) {
			// check if an association already exist
			// if (opLocal.getAssociation().equals("Aucune")) {
			if (opLocal.getAssociation().equals(CategoriesList.AUCUNE.toString())) {
				while ((associationNotFound == true) && (i < bString.size())) {
					lbString = bString.get(i);
					// check if:
					// - the libelle of the operation match the libelle of a category
					// - the libelle of the category is not empty
					// - the operation type is the same
					if (opLocal.getLibelle().contains(lbString[1]) && (!lbString[1].equals(""))
							&& (opLocal.getCategoryType().getTxtType().equals(lbString[2]))) {
						associationNotFound = false;
						opLocal.setAssociation(lbString[0]);
						opLocal.setaMode(AssociationMode.AUTOMATIC);
					}
					i++;
				}
				associationNotFound = true;
				i = 1;
			}
		}
	}

	void generateCategoriesList() {
		boolean csvHeader = true;
		boolean categoryIsNew = true;
		CategoryType opType;
		boolean constraint;

		for (String[] lString : categoriesString) {

			// Ignorer le header présent dans le fichier CSV
			if (csvHeader == false) {

				for (OperationCategory opCat : categoriesList) {
					if (lString[0].equals(opCat.getName()))
						categoryIsNew = false;
				}

				if (categoryIsNew) {
					opType = lString[1].equals(CategoryType.CREDIT.getTxtType()) ? CategoryType.CREDIT
							: CategoryType.DEBIT;
					constraint = lString[2].equals("Oui") ? true : false;

					OperationCategory opNew = new OperationCategory(lString[0], opType, constraint);
					categoriesList.add(opNew);
				}
				categoryIsNew = true;
			} else
				csvHeader = false;
		}

		// affichage temporaire
		for (OperationCategory opCat : categoriesList) {
			LogManager.LOGGER.log(Level.FINE, "Catégorie : " + opCat.getName());
		}
	}

	public String toString() {

		LogManager.LOGGER.log(Level.FINE, "***********************************************************");
		LogManager.LOGGER.log(Level.FINE, "*************** Table des opérations (Data) ***************");
		LogManager.LOGGER.log(Level.FINE, "***********************************************************");

		String rString = "";
		for (Operation op : opBookData)
			rString += op.toString() + "\n";
		return rString;
	}

	public LocalDate getOldestDate() {
		return opBookData.get(opBookData.size() - 1).getDateOp();
	}

	public LocalDate getLatestDate() {
		return opBookData.get(0).getDateOp();
	}

	public ArrayList<String[]> getaString() {
		return aString;
	}

	public int getCountWithoutAssociation() {
		return countWithoutAssociation;
	}

	public double getSumValueCredit() {
		return sumValueCredit;
	}

	public double getSumValueDebit() {
		return sumValueDebit;
	}

	public void setaString(ArrayList<String[]> aString) {
		this.aString = aString;
	}

	public ArrayList<String[]> getbString() {
		return bString;
	}

	public void setbString(ArrayList<String[]> bString) {
		this.bString = bString;
	}

	public ArrayList<Operation> getOpBookData() {
		return opBookData;
	}

	public void setOpBookData(ArrayList<Operation> opBookData) {
		this.opBookData = opBookData;
	}

	public ArrayList<OperationCategory> getCategoriesList() {
		return categoriesList;
	}

	public void setCategoriesList(ArrayList<OperationCategory> categoriesList) {
		this.categoriesList = categoriesList;
	}

	public OperationBookStats getOperationBookStats() {
		return operationBookStats;
	}

	public void setOperationBookStats(OperationBookStats operationBookStats) {
		this.operationBookStats = operationBookStats;
	}

	
	public ArrayList<String[]> getOperationsWithError() {
		return operationsWithError;
	}

	public void setOperationsWithError(ArrayList<String[]> operationsWithError) {
		this.operationsWithError = operationsWithError;
	}


	public ArrayList<String[]> getCategoriesString() {
		return categoriesString;
	}

	public void setCategoriesString(ArrayList<String[]> categoriesString) {
		this.categoriesString = categoriesString;
	}

	@Override
	public void loadNewFinanceData() {
		// TODO Auto-generated method stub
		
	}

}
