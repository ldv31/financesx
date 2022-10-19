package com.ldv.financesx.model;

public enum CategoriesList {
	
	
	AUCUNE ("Aucune"),
	ALIMENTATION_HYGIENE ("Alimentation et Hygiène"),
	SANTE ("Santé"),
	COIFFEUR ("Coiffeur"),
	EDF ("EDF"),
	ESSENCE ("Essence"),
	EPARGNE ("Epargne"),
	VETEMENTS ("Vétements"),
	LOISIR ("Loisir"),
	TRANSPORT ("Transport"),
	AMAZON ("Amazon"),
	INTERNET_TELEPHONE ("Internet / téléphonie"),
	BRICO_JARDIN ("Bricolage et Jardin"),
	EQUIP_MAISON ("Equipement maison"),
	RETRAIT_DAB ("Retrait DAB"),
	CADEAU_DEBIT ("Cadeau débit"),
	DEPL_PRO ("Déplacement pro"),
	ECOLE_DEBIT ("Ecole Debit"),
	ASSURANCE_DEBIT ("Assurance"),
	IMPOTS_DEBIT ("Impots Debit"),
	CADEAU_CREDIT ("Cadeau crédit"),
	TRANSF_BANK ("Transfert entre banques"),
	FORMATION ("Formation"),
	SALAIRE ("Salaire"),
	CAF ("Caf"),
	CPAM_MUTUELLE ("CPAM + Mutuelle"),
	REMBOURSEMENT ("Remboursement"),
	AMAZON_CREDIT ("Amazon Crédit");
	
	private String txtType ="";
	
	CategoriesList (String txtType) {
		this.txtType = txtType;
	}
	
	public String getTxtType () {
		return txtType;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return txtType;
	}
}
