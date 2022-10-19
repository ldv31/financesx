package com.ldv.financesx.model;

public enum AssociationMode {

	//Modes d'association
	AUTOMATIC ("Automatic"),
	MANUAL ("Manuel"),
	NONE ("Pas d'association");
	
	String txtMode ="";
	
	AssociationMode (String txtMode) {
		this.txtMode = txtMode;
	}
	
	public String getTxtMode () {
		return txtMode;
	}
	
	
	public static AssociationMode fromString(String text) {
	    for (AssociationMode b : AssociationMode.values()) {
	      if (b.txtMode.equalsIgnoreCase(text)) {
	        return b;
	      }
	    }
	    return null;
	  }
}
