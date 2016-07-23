package net.floodlightcontroller.wifioffload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiFiOffloadPerformanceMonitor {

	public static PrintWriter printer;
	protected static Logger logger = LoggerFactory.getLogger(WifiOffloadSDNControllers.class);
	
	public static long timeStamp;
	public static IPv4Address ipAddress;
	public static MacAddress userMacAddress;
	public static long numMobileUsers;
	public static double userSearchTime;
	public static double userLocalSearchTime;
	public static double userNetworkSearchTime;
	public static double userControllerSyncTime;
	public static double userCheckRemoteUserTime;
	public static double userGetRemoteUserTime;
	public static double userAddingTime;
	
	public static boolean userLocalSearchTimeEn;
	public static boolean userNetworkSearchTimeEn;
	public static boolean userControllerSyncTimeEn;
	public static boolean userCheckRemoteUserTimeEn;
	public static boolean userGetRemoteUserTimeEn;
	public static boolean userAddingTimeEn;
    

	public WiFiOffloadPerformanceMonitor(String filePath){
		
	}
	
	public static boolean initPrinter(String filePath){
		
		try{
			File file=new File(filePath);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			printer = pw;
			
			printer.println("TIMESTAMP\tIP\tMAC_ADDRESS\tUSERS\tLOCAL_SEARCH\tNET_SEARCH\tSYNC_EN\tSYNC_TIME\tCHECK_EN\tCHECK_TIME\tGET_EN\tGET_TIME\tADD_EN\tADD_TIME");
			printer.flush();
			return true;
		}
		catch(Exception e){
			return false;
		}
		
		
		
	}
	
	public static boolean closePrinter(){
		WiFiOffloadPerformanceMonitor.printer.close();
		return true;
	}
	
	public static boolean flushRecord(){
		 timeStamp =0;;
		 ipAddress= IPv4Address.of("0.0.0.0");
		userMacAddress = MacAddress.of("00:00:00:00:00:00");
		 numMobileUsers = 0;
		 userSearchTime = 0;
		 userLocalSearchTime =0;
		 userNetworkSearchTime =0;
		 userControllerSyncTime =0;
		 userCheckRemoteUserTime =0;
		 userGetRemoteUserTime =0;
		 userAddingTime =0;
		
		 userLocalSearchTimeEn = false;
		 userNetworkSearchTimeEn = false;
		 userControllerSyncTimeEn = false;
		 userCheckRemoteUserTimeEn =false;
		 userGetRemoteUserTimeEn = false;
		 userAddingTimeEn = false;
		
		return true;
	}
	
	public static boolean printRecord(){
		
		String record = "";
		
		Date data = new Date(timeStamp);
		record = record+timeStamp;		
		record += "\t"+ ipAddress.toString();
		record += "\t"+ userMacAddress.toString();
		record += "\t"+ numMobileUsers;
		record += "\t"+ userLocalSearchTime;
		record += "\t"+ userNetworkSearchTime;	
		
		
		if(userControllerSyncTimeEn){
			record += "\t"+"true";
			record += "\t"+ userControllerSyncTime;
		}else{
			record += "\t"+"false";
			record += "\t"+ userControllerSyncTime;
		}
		
		if(userCheckRemoteUserTimeEn){
			record += "\t"+"true";
			record += "\t"+ userCheckRemoteUserTime;
		}
		else{
			record += "\t"+"false";
			record += "\t"+ userCheckRemoteUserTime;
		}
		
		if(userGetRemoteUserTimeEn){
			record += "\t"+"true";
			record += "\t"+ userGetRemoteUserTime;
		}
		else{
			record += "\t"+"false";
			record += "\t"+ userGetRemoteUserTime;
		}
		
		
		if(userAddingTimeEn){
			record += "\t"+"true";
			record += "\t"+ userAddingTime;
		}
		else{
			record += "\t"+"false";
			record += "\t"+ userAddingTime;
		}
		
		printer.println(record);
		printer.flush();
		
		return true;
	}
	
}
