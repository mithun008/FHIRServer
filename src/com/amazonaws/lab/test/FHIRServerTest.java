package com.amazonaws.lab.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.LocalDate;

import com.amazonaws.lab.resources.PatientResource;

public class FHIRServerTest {
	
	public static void main(String[] args)throws IOException {
		PatientResource pat = new PatientResource();
		//pat.testBundle();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println("The date :"+sdf.format(new Date()));
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -30);
		
		LocalDate now = LocalDate.now();
		LocalDate thirty = now.minusDays( 30 );
		LocalDate sixty = now.minusDays( 60 );
		LocalDate ninety = now.minusDays( 90 );
		
		System.out.println( "now: " + now );
		System.out.println( "thirty: " + thirty );
		System.out.println( "sixty: " + sixty );
		System.out.println( "ninety: " + ninety );
				
	}
	
	

}
