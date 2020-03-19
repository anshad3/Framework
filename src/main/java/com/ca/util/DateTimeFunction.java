package com.ca.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

/**
 * 
 * @author Mukesh Jha
 * @Version 1.0
 * @since 12-29-2015
 */
public class DateTimeFunction 
{
	/**
	 * This function return current date
	 * Example date() can return values  like "05-01-2016"  , "10-12-2014", "23-02-2001" 
	 * @return Currentdate in dd-MM-yyyy
	 */
	public String currentDate()
	{
		 Date date = new Date();
	     SimpleDateFormat obDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	     String Curdate= obDateFormat.format(date.getTime());
		
		return Curdate;
		
	}
	
	/**
	 * This function return current month.
	 * Eample today() can return day of month like(January).
	 * @return today in number
	 */
	public int today()
	{
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_MONTH); 
		
		return today;
		
	}
	
	/**
	 * This function return day of week
	 * Example todaysday() can return day of week like(TUESDAY)
	 * @return todaysday in string
	 */
	public String todaysday()
	{
		String todayday=null;
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK); 
		
		switch (day) {
	    case Calendar.SUNDAY:
	    	todayday="SUNDAY";
	        break;
        case Calendar.MONDAY:
        	todayday="MONDAY";
        	break;
        case Calendar.TUESDAY:
        	todayday="TUESDAY";
        	break;
        case Calendar.WEDNESDAY:
        	todayday="WEDNESDAY";
        	break;
        case Calendar.THURSDAY:
        	todayday="THURSDAY";
        	break;
        case Calendar.FRIDAY:
        	todayday="FRIDAY";
        	break;
        case Calendar.SATURDAY:
        	todayday="SATURDAY";
        	break;
	       
	}
		return todayday;
		
	}
	
	/**
	 * This function accept number between 1 and 7 and returns day
	 * Example todaysdayNum() can return day of week like(if Tuesday=3, if Wednesday=4...if Monday=2, if Sunday=1, if Saturday=7) 
	 * @return todaysday in number
	 */
	public int todaysdayNum()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK); 
		
		switch (day) {
	    case Calendar.SUNDAY:
	        break;
        case Calendar.MONDAY:
        	break;
        case Calendar.TUESDAY:
        	break;
        case Calendar.WEDNESDAY:
        	break;
        case Calendar.THURSDAY:
        	break;
        case Calendar.FRIDAY:
        	break;
        case Calendar.SATURDAY:
        	break;
	       
	}
		return day;
		
	}
	
	/**
	 * This function returns todaysmonth
	 * Example todaysmonth() can return calendar month number like if Jan(1),Feb(2)....Dec(12).
	 * @return todaysmonth in number
	 */
	public int todaysmonth()
	{
		
		int todaysmonth=Calendar.getInstance().get(Calendar.MONTH)+1;
		return todaysmonth;
	}

	/**
	 * This function returns current year
	 * Example todaysyear() can return today calendar year like "2016".
	 * @return returns todaysyear in number
	 */
	public int todaysyear()
	{
		int todaysyear=Calendar.getInstance().get(Calendar.YEAR);
		
		return todaysyear;
	}
	
	/**
	 * This function accept number between 1 and 7 and returns day
	 * which is current day minus number passed. 
	 * Example current day is 2 (Monday) and input is 1 then it returns 1 (Sunday).
	 * @param todayValue
	 * @return returns today - todayValue 
	 * @throws Exception 
	 */
	public int todayMinus(int todayValue) throws Exception
	{
		int todayMinus = 0;
		Calendar calendar = Calendar.getInstance();
		if(todayValue<today())
		{
		calendar.add(Calendar.DAY_OF_MONTH, -todayValue);
		todayMinus=calendar.get(Calendar.DAY_OF_MONTH);
		}
		else
		{
			throw new Exception("Please give input  dayvalue:"+todayValue+" is less then today :"+today());
		}
		
		return todayMinus;
		
	}
	
	/**
	 * This function accept number between 1 and 7 and returns day
	 * which is current day plus number passed. 
	 * Example current day is 2 (Monday) and input is 2 then it returns 4 (Wednesday).
	 * @param todayvalue
	 * @return returns today + todayvalue
	 */
	public int todayPlus(int todayvalue)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, +todayvalue);
		
		int todayPlus=calendar.get(Calendar.DAY_OF_MONTH);
		
		return todayPlus;
	}
	
	/**
	 * This function accept number between 1 and 12 and returns month
	 * which is current month plus number passed. 
	 * Example current month is 4 (April) and input is 5 then it returns 9 (September).
	 * @param monthValue
	 * @return returns currentMonth + monthValue
	 * example:  currentMonthPlus(5)  can return "6" if the current month is Januray 
	 */
	public int currentMonthPlus(int monthValue)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,+monthValue);
		int currentMonthPlus=calendar.get(Calendar.MONTH)+1;
		
		
		return currentMonthPlus;
	}
	
	 /**
	  * This function is used to get year Plus 
	  * Example yearPlus(10) then it returns 2016.
	  * @param yearValue
	  * @return yearPlus
	  */
	public int yearPlus(int yearValue)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR,+yearValue);
		int yearPlus=calendar.get(Calendar.YEAR);
		
		
		return yearPlus;
	}
	
	/**
	 * This function is used to get year Plus 
	 * Example yearPlus(10) then it returns 2006.
	 * @param yearValue
	 * @return yearMinus
	 */
	public int yearMinus(int yearValue)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR,-yearValue);
		int yearMinus=calendar.get(Calendar.YEAR);
		
		return yearMinus;
	}
	
	/**
	 * This function return current time
	 * Example currenttime() can return current time like "2:52:38"
	 * @return returns currenTime in HH:mm:ss
	 *  example:  currentTime()  can return like "35" and "38" 
	 */
	public String currenttime()
	{
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currenTime =sdf.format(cal.getTime()) ;
		
		return currenTime;
		
	}
	
	/**
	 * This function return current second
	 * Example currentsec() can return current second like "37", "39,"47"
	 * @return returns currentsec
	 * example:  currentsec()  can return like "35" and "38" 
	 */
	public int currentsec()
	{
		Calendar calendar = Calendar.getInstance();
		int currentsec = calendar.get(Calendar.SECOND);
		return currentsec;
		
	}
	
	/**
	 * This function can return current minute
	 * Example currentminute() can return current minute like "23","55"
	 * @return returns currentminute
	 * example:  currentminute()  can return like "35" and "38" 
	 */
	public int currentminute()
	{
		Calendar calendar = Calendar.getInstance();
		int currentminute = calendar.get(Calendar.MINUTE);
		return currentminute;
	}
	/**
	 * This function return current hour.
	 * Example currenthour() can return current hour like "2","4","11"
	 * @return returns currenthour
	 * example: currenthour() can return like "12" and "1"
	 */
	public int currenthour()
	{
		Calendar calendar = Calendar.getInstance();
		int currenthour = calendar.get(Calendar.HOUR);
		return currenthour;
	}
	
	public String getAddDaysToCurrentDate(String noOfDays){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, Integer.parseInt(noOfDays)); 
		String output = sdf.format(c.getTime());
		return output;
	} 
	public static void main(String[] args) throws Exception {
		DateTimeFunction d=new DateTimeFunction();
		System.out.println(d.currentminute());
		System.out.println(d.currenthour());
		System.out.println(d.currentsec());
		System.out.println(d.currentMonthPlus(4));
		System.out.println(d.todaysmonth());
		System.out.println(d.todaysyear());
		System.out.println("YearPlus:"+d.yearPlus(10));
		System.out.println("YearMinus:"+d.yearMinus(10));
		//System.out.println("CurrentMonthMinus:"+currentMonthMinus(10));
		
		System.out.println(d.currentDate());
		System.out.println("given date format:---->"+d.currentDateAndTime("yyyyMMddHHmmss"));
		System.out.println("given date format:---->"+d.currentDateAndTime());
	}
	
	public String currentDateAndTime(String dateFormat) {
		String curDateTime = null;
		if (StringUtils.hasText(dateFormat)) {
			TimeZone timezone = TimeZone.getTimeZone("GMT");
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat(dateFormat);
			df.setTimeZone(timezone);
			curDateTime = df.format(calendar.getTime());
		} else {
			curDateTime = currentDateAndTime();
		}
		
		return curDateTime;

	}

	public String currentDateAndTime()
	{
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		df.setTimeZone(timezone);
		String curDateTime = df.format(calendar.getTime());
		return curDateTime;
		
	}
	
	public String curDateAndTimeMinusMinutes(int duration)
	{
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		df.setTimeZone(timezone);
		calendar.add(Calendar.MINUTE, -duration);
		String updatedDuration = df.format(calendar.getTime());
		return updatedDuration;
		
	}

}//End of class
