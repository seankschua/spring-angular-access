package com.exp.adwords;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
	
	public static int endMonth(int in) throws ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(Integer.toString(in));
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		String out = sdf.format(c.getTime());
		
		return Integer.parseInt(out);
		
	}

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
		System.out.println(endMonth(20150101));
		
		
	}

}
