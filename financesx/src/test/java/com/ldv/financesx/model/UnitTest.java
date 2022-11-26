package com.ldv.financesx.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;

import com.ldv.financesx.CsvFileHelper;
import com.ldv.financesx.DialogMessage;
import com.ldv.financesx.LogManager;
import com.ldv.financesx.model.AssociationMode;
import com.ldv.financesx.model.CategoryType;
import com.ldv.financesx.model.Operation;
import com.ldv.financesx.model.OperationsBook;

@DisplayName ("Operation class unit testing")
public class UnitTest {

	// store the list of operation extracted from CVS file
	ArrayList<String[]> aString = new ArrayList<String[]>();
	ArrayList<String[]> categoriesStringCompare = new ArrayList<String[]>();
	ArrayList<String[]> categoriesStringFromFile = new ArrayList<String[]>();
	
	// basic operations place holders
	Operation op;
	Operation op1;
	
	// String for comparison
	String EndOperationDate;
	String StartOperationDate;
	
	// Number of operation
	int numberOfOperationWithoutOperation;
	int numberOfOperationAmazon;
	
	// for verification of categories data from file
	String categoriesFileName = "./src/test/java/com/ldv/financesx/model/categories.csv";	
	final File categoriesFile = CsvFileHelper.getResourceAbs(categoriesFileName);
	
	
	@BeforeEach
	void beforeEachMethod() throws Exception {
		
		/*
		 * Variable to be used during the tests
		 */
		
		// Date read from CSV files (finance.csv)
		String[] a = {"Date opération","Date valeur","libellé","Débit","Crédit","Association","Mode Association"};
		String[] b = {"14/11/2022","14/11/2022","VIR C.P.A.M. TOULOUSE","0.0","19.6","CPAM + Mutuelle","Automatic"};
		String[] c = {"25/10/2021","25/10/2021","CARTE 11/11 CASTORAMA PORTET S1446/","-55.85","0.0","Bricolage et Jardin","Automatic"};
		String[] d = {"12/02/2021","12/02/2021","CARTE 10/02 AMAZON EU SARL PAYLI2090401/","-6.5","0.0","Amazon","Automatic"};	
		String[] e = {"02/11/2020","02/11/2020","CHQ 7424516","-10.0","0.0","Aucune","Pas d'association"};
				
		aString.add(a);
		aString.add(b);
		aString.add(c);
		aString.add(d);
		aString.add(e);
		
		//date for comparison
		EndOperationDate = "2022-11-14";
		StartOperationDate = "2020-11-02";
		
		// Number of operation without association
		numberOfOperationWithoutOperation = 1;
		numberOfOperationAmazon = 1;
		
		// basic Operation with valid content
		op = new Operation ("10/11/2022",  "10/11/2022", "PRLV Bouygues Telecom", -14.99, 0.0, "Internet / téléphonie", AssociationMode.AUTOMATIC);

		// basic Operation with first date ("11/11/2022")  different from first date in op ("10/11/2022")
		op1 = new Operation ("10/11/2022",  "10/11/2022", "PRLV Bouygues Telecom - Not equal", -14.99, 0.0, "Internet / téléphonie", AssociationMode.AUTOMATIC);

		// categories list from "categories.csv"
		String[] c1 = {"Rubrique","Opération","Contrainte"};
		String[] c2 = {"Alimentation et Hygiène","Débit","Oui"};
		String[] c3 = {"Santé","Débit","Non"};
		String[] c4 = {"Salaire","Crédit","NA"};
		
		
		// for comparison related to categories	
		categoriesStringCompare.add(c1);
		categoriesStringCompare.add(c2);
		categoriesStringCompare.add(c3);
		categoriesStringCompare.add(c4);
		
		if (!categoriesFile.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + categoriesFileName);
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			System.out.println("Categories file does not exists in : " + "\"" + categoriesFileName + "\""
					+ System.getProperty("line.separator") + "Exiting program");
			fail();
		}		
	}
	
	@DisplayName("Category type evaluation")
	@Test
	void Test_Operation_Class_GetCategorytype_ReturnDebit() {
		
		// Arrange
		
		// Act
		CategoryType catType = op.getCategoryType();
		
		// Assert
		assertEquals(CategoryType.DEBIT, catType, "Category type for the operation is not corectly set at initialisation");
	
	}
	
	
	@DisplayName("Comparison of identical objects ")
	@Test
	void Test_Operation_Class_equalLibDebCred_ReturnEqual() {
		
		// Arrange // Given
		
		// Act // When
		boolean cmpOps = op.equalLibDebCred(op);
		
		// Assert Then
		assertTrue(cmpOps, "operation comparaison method - equalLibDebCred- failed");
	}
	
	
	@DisplayName("Comparison of different objects ")
	@Test
	void Test_Operation_Class_equalLibDebCred_ReturnNotEqual() {
		
		// arrange
		
		
		// Act
		boolean cmpOps = op.equalLibDebCred(op1);
		
		// Assert
		assertFalse(cmpOps, "operation comparaison method - equalLibDebCred- failed");
	}
	
	@DisplayName("Verification of ToSTring method output")
	@Test
	void Test_Operation_Class_toString_ReturnEqual() {
		
		// Given
		
		
		// When
		boolean bl = op.toString().contains("10/11/2022	10/11/2022	PRLV Bouygues Telecom		-14.99	0.0	Internet / téléphonie	Automatic");
		
				
		// Then
		assertTrue(bl, "Error in the toString function format not correct");
	}
	
		
	@DisplayName("Calculation of start and end date on a list of Operations")
	@Test
	void UnitTest_OperationBook_Class_fillOperationBookData_operationBookStats_ReturnEqual() throws ParseException {
		
		// Given
		OperationsBook opBook = new OperationsBook();
		
		
		// When
		// transformation des données CSV en livre de compte
		opBook.fillOperationBookData(aString, opBook.getOpBookData(), opBook.getOperationBookStats());
		
		
		// Then
		assertEquals(opBook.getOperationBookStats().getEndOperationDate().toString(), EndOperationDate);
		assertEquals(opBook.getOperationBookStats().getStartOperationDate().toString(), StartOperationDate);		
	}
	
	
	
	@DisplayName("Get the number of operations without association")
	@Test
	void UnitTest_OperationBook_Class_opWithoutAssociation_NumberOfNoAssociation_ReturnEqual() throws ParseException {
		
		// Given
		OperationsBook opBook = new OperationsBook();
		opBook.fillOperationBookData(aString, opBook.getOpBookData(), opBook.getOperationBookStats());
		
		// When
		opBook.getOpBookDataAmazon();
			
		// Then
		assertEquals(opBook.getOpBookDataAmazon().size(), numberOfOperationWithoutOperation);
	
	}
	
	
	@DisplayName("Get the number of Amazon operations")
	@Test
	void UnitTest_OperationBook_Class_opAmazon_NumberOfNoAssociation_ReturnEqual() throws ParseException {
		
		// Given
		OperationsBook opBook = new OperationsBook();
		opBook.fillOperationBookData(aString, opBook.getOpBookData(), opBook.getOperationBookStats());
		
		// When
		opBook.opWithoutAssociation();
			
		// Then
		assertEquals(opBook.getOpBookDataWithoutAssociation().size(), numberOfOperationAmazon);
	
	}
	

	
	@DisplayName("Get the list of categories from categories.csv file")
	@Test
	void UnitTest_OperationBook_Class_CategoriesString_List_ReturnEqual() throws Exception {
		
		// Given
		// category file (categories.csv)
		// ouverture du fichier des categories
		
		if (!categoriesFile.exists()) {
			LogManager.LOGGER.log(Level.SEVERE, "File does not exists : " + categoriesFileName);
			LogManager.LOGGER.log(Level.SEVERE, "Please provide the missing file");
			LogManager.LOGGER.log(Level.SEVERE, "End of program");
			System.out.println("Categories file does not exists in : " + "\"" + categoriesFileName + "\""
					+ System.getProperty("line.separator") + "Exiting program");
			//System.exit(1);
		}	
		
		// When
		// extraction des données du fichier des association
		categoriesStringFromFile = CsvFileHelper.extractRawOperationData(categoriesFile);
			
		// Then
		for (int line = 0; line < categoriesStringFromFile.size(); line++) {
			for (int word = 0; word < categoriesStringFromFile.get(line).length ; word++) {
				assertEquals(categoriesStringFromFile.get(line)[word], categoriesStringCompare.get(line)[word]);
			}
		}
	}
	
	
	
	@DisplayName("Get the list of categories from categories.csv file and compare to invalid data")
	@Test
	void UnitTest_OperationBook_Class_CategoriesString_List_ReturnNotEqual() throws Exception {
		
		// Given
		// category file (categories.csv)
		// ouverture du fichier des categories
		
		
		// When
		// extraction des données du fichier des association
		categoriesStringFromFile = CsvFileHelper.extractRawOperationData(categoriesFile);
			
		// Then
		for (int line = 0; line < categoriesStringFromFile.size(); line++) {
			for (int word = 0; word < categoriesStringFromFile.get(line).length ; word++) {
				assertNotEquals(categoriesStringFromFile.get(line)[word], "Not Equal");
			}
		}
	}
		
	
	
	@DisplayName("Build the list of categories")
	@Test
	void UnitTest_OperationBook_Class_Categories_List_ReturnEqual() throws Exception {
		
		// Given
		OperationsBook opBook = new OperationsBook();
		categoriesStringFromFile = CsvFileHelper.extractRawOperationData(categoriesFile);
		
		// When
		opBook.setCategoriesString(categoriesStringFromFile);
		opBook.generateCategoriesList();
		ArrayList<OperationCategory> categoriesList = opBook.getCategoriesList();
			
		// Then
		for (int opCatNb = 0; opCatNb < categoriesList.size(); opCatNb++) {	
			// verify the category name
			assertEquals(categoriesList.get(opCatNb).getName(), categoriesStringCompare.get(opCatNb+1)[0]);
			
			// verify the category type
			assertEquals(categoriesList.get(opCatNb).getType().getTxtType(), categoriesStringCompare.get(opCatNb+1)[1]);
			
			// verify the constraint
			boolean constraintCompare = (categoriesStringCompare.get(opCatNb+1)[2] == "Oui") ? true : false;
			assertEquals(categoriesList.get(opCatNb).isConstraint(), constraintCompare);
			
		}
		
		
	}	
	
}
