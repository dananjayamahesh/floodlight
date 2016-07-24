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
	public static double userRemovingTime;
	
	public static boolean userLocalSearchTimeEn;
	public static boolean userNetworkSearchTimeEn;
	public static boolean userControllerSyncTimeEn;
	public static boolean userCheckRemoteUserTimeEn;
	public static boolean userGetRemoteUserTimeEn;
	public static boolean userAddingTimeEn;
	
	public static boolean userRemovingTimeEn;
	
	public static boolean isRestOperation;
	public static boolean isCoreOperation;
    

	public WiFiOffloadPerformanceMonitor(String filePath){
		
	}
	
	public static boolean initPrinter(String filePath){
		
		try{
			File file=new File(filePath);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			printer = pw;
			
			printer.println("TIMESTAMP,TYPE,IP,MAC_ADDRESS,USERS,LOCAL_SEARCH,NET_SEARCH,SYNC_EN,SYNC_TIME,CHECK_EN,CHECK_TIME,GET_EN,GET_TIME,ADD_EN,ADD_TIME,REMOVE_EN,REMOVE_TIME");
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
		 userRemovingTime =0;
		
		 userLocalSearchTimeEn = false;
		 userNetworkSearchTimeEn = false;
		 userControllerSyncTimeEn = false;
		 userCheckRemoteUserTimeEn =false;
		 userGetRemoteUserTimeEn = false;
		 userAddingTimeEn = false;
		 userRemovingTimeEn = false;
		 
		 isCoreOperation = false;
		 isRestOperation = false;
		
		return true;
	}
	
	public static boolean printRecord(){
		
		String record = "";
		
		Date data = new Date(timeStamp);
		record = record+timeStamp;	
		
		if(isCoreOperation){
			record += ","+"CORE";
		}else if(isRestOperation){
			record += ","+"REST";
		}else{
			record += ","+"UNDF";
		}
		
		record += ","+ ipAddress.toString();
		record += ","+ userMacAddress.toString();
		record += ","+ numMobileUsers;
		record += ","+ userLocalSearchTime;
		
		if(userNetworkSearchTimeEn){
			record += ","+"true";
		    record += ","+ userNetworkSearchTime;
		}else{
			record += ","+"false";
			record += ","+ userNetworkSearchTime;
		}
		
		
		if(userControllerSyncTimeEn){
			record += ","+"true";
			record += ","+ userControllerSyncTime;
		}else{
			record += ","+"false";
			record += ","+ userControllerSyncTime;
		}
		
		if(userCheckRemoteUserTimeEn){
			record += ","+"true";
			record += ","+ userCheckRemoteUserTime;
		}
		else{
			record += ","+"false";
			record += ","+ userCheckRemoteUserTime;
		}
		
		if(userGetRemoteUserTimeEn){
			record += ","+"true";
			record += ","+ userGetRemoteUserTime;
		}
		else{
			record += ","+"false";
			record += ","+ userGetRemoteUserTime;
		}
		
		
		if(userAddingTimeEn){
			record += ","+"true";
			record += ","+ userAddingTime;
		}
		else{
			record += ","+"false";
			record += ","+ userAddingTime;
		}
		
		if(userRemovingTimeEn){
			record += ","+"true";
			record += ","+ userRemovingTime;
		}
		else{
			record += ","+"false";
			record += ","+ userRemovingTime;
		}
		
		printer.println(record);
		printer.flush();
		
		return true;
	}
	
}
