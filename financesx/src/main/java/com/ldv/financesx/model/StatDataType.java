package com.ldv.financesx.model;

// Liste of data types, add here a new entry for a new data data type to be processed
public enum StatDataType {

	CATEGORY_SUM ("Somme"),
	CATEGORY_AVERAGE ("Moyenne Mensuelle"),
	CATEGORY_HISTORY ("Historique"),
	CATEGORY_GAINLOSS ("Gain & Perte"),
	CATEGORY_GAINLOSS_AVERAGE ("Gain & Perte - Moyenne Historique"),
	CATEGORY_BUDGET ("Budget - sans les remboursements"),
	CATEGORY_BUDGETVSREIMBURSEMENT ("Budget sans remboursements vs remboursements"),
	CATEGORY_BUDGET_AVERAGE ("Budget - moyenne - avec les remboursements"),
	CATEGORY_CURRENT ("Mois"),
	CATEGORY_NOASSOCIATION ("Operations sans association"),
	CATEGORY_AMAZON ("Amazon"),
	CATEGORY_STATSPERCAT ("Statistiques par catégorie"),
	CATEGORY_STATSPERCATVSINCOME ("Statistiques par catégorie vs Revenu"),
	CATEGORY_STATSPERCATANDHISTORY("Statistiques cummulatives et historiques par catégories - sans les remboursements"),
	CATEGORY_STATSPERCATANDHISTORYANDCONSTRAINT("Statistiques cummulatives et historiques pour les catégories contraintes - sans les remboursements");
	
	
	private String txtType ="";
	
	StatDataType (String txtType) {
		this.txtType = txtType;
	}
	
	public String getTxtType () {
		return txtType;
	}
	
}
