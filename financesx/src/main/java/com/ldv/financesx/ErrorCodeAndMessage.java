package com.ldv.financesx;

import com.ldv.financesx.model.PanelType;


// general error code to be used across the application
public enum ErrorCodeAndMessage {
	
	OPERATION_SUCCES ("00 - Operation Success"),
	FILE_DOES_NOT_EXIST ("01 - File does not exist"),
	END_OF_LIST("end of list");
	 
	private String txtType ="";
	
	ErrorCodeAndMessage (String txtType) {
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
