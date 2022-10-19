package com.ldv.financesx.model;

public enum CategoryType {

	DEBIT ("Débit"),
	CREDIT ("Crédit"),
	TOUS ("toute opération");
	
	private String txtType ="";
	
	CategoryType (String txtType) {
		this.txtType = txtType;
	}
	
	public String getTxtType () {
		return txtType;
	}
}
