package com.ldv.financesx.model;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import com.ldv.financesx.LogManager;

/* Description : stockage des informations relative à une opération bancaire 
 * 
 */
public class Operation {
	
	// unique operation identifier, generated when the operation is created
	private static int opIdCounter = 0;
	private int opId;
	
	// Dates
	//private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private LocalDate dateOp ;
	private LocalDate dateVal ;
	
	// Libellé
	private String libelle;
	
	// valeurs
	private double credit = 0;
	private double debit = 0;
	
	// type d'opération
	private CategoryType lCategoryType;
	
	// association
	//private String association = "Aucune";
	private String association = CategoriesList.AUCUNE.toString();
	
	// mode association;
	private AssociationMode aMode =  AssociationMode.NONE;
	
	
	// Eléments de l'opération
	public Operation (String dateOp, String dateVal, String libelle, double debit, double credit, String pAssociation, AssociationMode lmode) {
		
		// set the Id of the operation
		this.opId = opIdCounter++;
		
		setDateOp (dateOp);
		setDateVal (dateVal);
		this.libelle = libelle;
		this.debit = debit;
		this.credit = credit;
		this.aMode = lmode;
		this.association = pAssociation;
		
		if (credit != 0)
			lCategoryType  = CategoryType.CREDIT;
		else
			lCategoryType  = CategoryType.DEBIT;
		
		
		LogManager.LOGGER.log(Level.FINE,"Category object after select: " + this);
	}
	
	
	public boolean equalLibDebCred(Operation op1) {
		boolean rValue = false;
		
		if ( (libelle.equals(op1.getLibelle())) && (debit == op1.getDebit()) &&  (credit == op1.getCredit()) ) {
			rValue = true;
		}
		
		return rValue;
	}
	
	private void setDateOp (String dateOp) {
		this.dateOp =  LocalDate.parse(dateOp, df);
	}
		
	private void setDateVal (String dateVal) {
		this.dateVal = LocalDate.parse(dateVal, df);
	}
	
	public String toString () {
	
		return opId + "\t" + dateOp.format(df) + "\t" + dateVal.format(df) 
		+ "\t" + libelle +"\t\t"+ debit + "\t" + credit + "\t" + association + "\t" + aMode.getTxtMode();
		
	}
	
	public String [] toStringArray() {
	
		String [] rStringArray = {dateOp.format(df), dateVal.format(df), libelle, Double.toString(debit), Double.toString(credit), association, aMode.getTxtMode()};
		return rStringArray;
		
	}

	
	public double getOpValue () {
		if (credit != 0)
			return credit;
		else
			return debit;
					
	}
	
	public CategoryType getCategoryType () {
		return lCategoryType;			
	}
	

	public DateTimeFormatter  getDf() {
		return df;
	}


	public void setDf(DateTimeFormatter  df) {
		this.df = df;
	}


	public LocalDate getDateOp() {
		return dateOp;
	}


	public void setDateOp(LocalDate dateOp) {
		this.dateOp = dateOp;
	}


	public LocalDate getDateVal() {
		return dateVal;
	}


	public void setDateVal(LocalDate dateVal) {
		this.dateVal = dateVal;
	}


	public String getLibelle() {
		return libelle;
	}


	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}


	public double getCredit() {
		return credit;
	}


	public void setCredit(double credit) {
		this.credit = credit;
	}


	public double getDebit() {
		return debit;
	}


	public void setDebit(double debit) {
		this.debit = debit;
	}


	public String getAssociation() {
		return association;
	}


	public void setAssociation(String association) {
		this.association = association;
	}


	public AssociationMode getaMode() {
		return aMode;
	}


	public void setaMode(AssociationMode aMode) {
		this.aMode = aMode;
	}


	public int getOpId() {
		return opId;
	}


	public void setOpId(int opId) {
		this.opId = opId;
	}
	
}
