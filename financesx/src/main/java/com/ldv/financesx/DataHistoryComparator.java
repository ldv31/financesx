package com.ldv.financesx;

import java.util.Comparator;
import com.ldv.financesx.model.StatDataHistory;

public class DataHistoryComparator implements Comparator<StatDataHistory> {

	@Override
	public int compare(StatDataHistory p, StatDataHistory q) {
		
		if (p.getMonthAndYear().isBefore(q.getMonthAndYear())) {
            return -1;
        } else if (p.getMonthAndYear().isAfter(q.getMonthAndYear())) {
            return 1;
        } else {
            return 0;
        }     
	}

}
