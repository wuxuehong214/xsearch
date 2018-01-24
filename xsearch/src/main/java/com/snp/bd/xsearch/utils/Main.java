package com.snp.bd.xsearch.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "430421199609165679|9223372035371608591|9223372035371583865|105386471|4301220124|192.168.8.78,4301220124,430421199609165679,192.168.8.78,2016-12-31 14:53:36,10020,1512330519,~原H点~,105386471,二班伙伴嗨嗨嗨,2016-12-31 21:45:42,2016-12-31 21:46:05,3,650,/WJ_RESOURCE/NET/CHAT/2017/1/1/0/55/0504_20170101005418_W34301220124_C749066F86F91B56DE1D97857E59B72D_CF639D29773E0CB72E594394EA7FD796_192.168.8.78.GCHAT";
		String s =  "tan ten ttn tcn d.xlsx aa.docx";
		String regex = "\\.(xlsx|docx)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		
		System.out.println(m.matches());
		System.out.println(m.groupCount());
		while(m.find()) {
//	        System.out.println("Match " + m.group() +" at positions " + m.start() + "-" + (m.end() - 1));
			System.out.println(m.group());
			
//			int i = 0;
//			while(m.find(i)) {
//			      System.out.println(m.group(i+1));
//			      i++;
//			 }
	      }
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		System.out.println(sdf.format(new Date()));
		
		
	}

}
