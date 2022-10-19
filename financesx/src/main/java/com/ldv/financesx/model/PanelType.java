package com.ldv.financesx.model;

// liste pas choices in the combobox menu - add here to have a new entry
public enum PanelType {

	GLOBAL ("Statistiques globales"),
	HISTORY ("Statistiques historiques"),
	GAINLOSS ("Gain & perte"),
	BUDGET ("Budget"),
	BUDGETVSREIMBURSEMENT ("Budget vs remboursements"),
	CURRENT ("Mois"),
	NOASSOCIATION ("Opérations sans associations"),
	AMAZON ("Amazon"),
	STATSPERCAT("Statistiques par catégorie"),
	STATSPERCATVSINCOME("Statistiques par catégorie vs Revenu"),
	STATSPERCATANDHISTORY("Statistiques cummulatives et historiques par catégories"),
	STATSPERCATANDHISTORYANDCONSTRAINT("Statistiques cummulatives et historiques pour les catégories contraintes");
	 
	private String txtType ="";
	
	PanelType (String txtType) {
		this.txtType = txtType;
	}
	
	public String getTxtType () {
		return txtType;
	}
	
	// return the type text as an array to be injected in the cmd combox
	public static String[] getTypeTextArray() {
		String[] tstString = new String[PanelType.values().length];
		int i = 0; 
		
		for (PanelType ptype : PanelType.values()) {
			tstString[i] = ptype.getTxtType();
			i++;
		}
		
		return tstString;
	}
	
	public static int getIndex(String str) {
		
		int rValue = 0;
		int i = 0; 
		
		for (PanelType ptype : PanelType.values()) {
			if (ptype.getTxtType().equals(str)) {
				rValue = i;
			}
			i++;
		}
		
		return rValue;
	}
	
}
