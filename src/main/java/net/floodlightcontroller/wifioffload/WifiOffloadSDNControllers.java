package net.floodlightcontroller.wifioffload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiOffloadSDNControllers {
 
	public TreeSet<WifiOffloadSDNController> controllers = new TreeSet<WifiOffloadSDNController>();
    public WifiOffloadSDNController localController=null;;
	public int numControllers =0;
	protected static Logger logger = LoggerFactory.getLogger(WifiOffloadSDNControllers.class);
	public boolean isSubDenBaseOffloadEn;
	public boolean isCostBaseOffloadEn;
	
	public WifiOffloadSDNControllers(){
		this.numControllers =0;
		logger = LoggerFactory.getLogger(WifiOffloadSDNControllers.class);
		localController = null;
		this.isSubDenBaseOffloadEn = false;
		this.isCostBaseOffloadEn  = false;
	}
	
	public long genId(WifiOffloadSDNController controller){
		MacAddress mac= controller.getMacAddress();
		return mac.getLong();
	}
	
	public boolean addController(WifiOffloadSDNController controller){
		//controller.setId(genId(controller));
		this.controllers.add(controller);
		this.numControllers++;
		return true;
	}
	
	public boolean setLocalController(WifiOffloadSDNController controller){
		this.localController = controller;
		return true;
	}
	
	public WifiOffloadSDNController getLocalController(){
		return this.localController;
	}
	
	public Iterator<WifiOffloadSDNController> getIterator(){
		return this.controllers.iterator();
	}
	
	public List<WifiOffloadSDNController> getControllersList(){
		return new ArrayList<WifiOffloadSDNController>(this.controllers);
	}
	
	public boolean isSubDenBaseOffloadEnable(){
		return this.isSubDenBaseOffloadEn;
	}
	public void setSubDenBaseOffloadEnable(boolean isSubDenBaseOffloadEn){
		this.isSubDenBaseOffloadEn = isSubDenBaseOffloadEn;
	}
	public boolean isCostBaseOffloadEnable(){
		return this.isCostBaseOffloadEn;
	}
	public void setCostBaseOffloadEnable(boolean isCostBaseOffloadEn){
		this.isCostBaseOffloadEn = isCostBaseOffloadEn;
	}	
	
	public WifiOffloadSDNController createController(){
		Scanner scn = new Scanner (System.in);
		logger.info("Enter ID, NAME, DESCRIPTION, AREAID, MAC_ADDRESS, IP_ADDRESS, TCP_PORT, NUMBER_OF_USERS, MAX_NUMBER_OF_USERS, ENABLED");
		long id= scn.nextLong();
		String name = scn.next();
		String description=scn.next();
		long areaId= scn.nextLong();
		MacAddress macAddress = MacAddress.of(scn.next());
		IPv4Address ipAddress = IPv4Address.of(scn.next());
		int tcpPort = scn.nextInt();
		long numMobileUsers = scn.nextLong();
		long maxNumMobileUsers = scn.nextLong();
		boolean enabled = scn.nextBoolean();	
		int conType = scn.nextInt();
		return new WifiOffloadSDNController(id, name, description, areaId, macAddress, ipAddress, tcpPort, numMobileUsers, maxNumMobileUsers, enabled,conType);
		
	}
	
	public boolean overrideControllerParameters(WifiOffloadSDNController con1, WifiOffloadSDNController con2){
		con1.id = con2.id;
		con1.name = con2.name;
		con1.description = con2.description;
		con1.areaId = con2.areaId;
		con1.macAddress = con2.macAddress;
		con1.numMobileUsers = con2.numMobileUsers;
		con1.maxNumMobileUsers = con2.maxNumMobileUsers;
		con1.enabled = con2.enabled;
		con1.conType = con2.conType;
		return true;
	}
	
	public WifiOffloadUserEntry searchUserInNetwork(WifiOffloadSDNControllers controllers , WifiOffloadUserEntry entry){
	  
		logger.info("Starting Searching The Network for user: "+entry.userMacAddress.toString());
		Iterator<WifiOffloadSDNController>iter = controllers.getIterator();
		WifiOffloadUserEntry remoteEntry=null;
		while(iter.hasNext()){
			WifiOffloadSDNController controller = iter.next();
			logger.info("Controller "+controller.getIpAddress().toString()+" processing....");
			boolean isEnabled = false;
			
			WifiOffloadSDNController con=null;
			long startTime=0;
			long endTime=0;
			double timeDiff=0;
			
			if(controller.isEnabled()){
				
				
				logger.info("Controller "+controller.getIpAddress().toString()+" is already Enabled");
			//	isEnabled = true;
				
				con=WifiOffloadSDNControllers.sendControllerRequest(controller);
				
				
				 isEnabled = con.isEnabled();
				 controller.setEnabled(isEnabled);	
				 //controller = con;
			}
			else{
				logger.info("Send A Enable Request To Controller: "+controller.getIpAddress().toString());				
				   
				   con=WifiOffloadSDNControllers.sendControllerRequest(controller);			    				 
				 
				 isEnabled = con.isEnabled();
				 controller.setEnabled(isEnabled);
				 //controller = con;
				 
			}
			
			//controller = con is not good
			overrideControllerParameters(controller, con);
			
			
			if(isEnabled){
				if(WifiOffloadSDNControllers.checkForUserInOtherControllers(controller,entry)){
					
					logger.info("User "+entry.userMacAddress.toString()+" Found in Controller: "+controller.getIpAddress().toString());
					
					//c is the native one;
					WifiOffloadSDNController localController = controllers.localController;
					//Offloading Scenario
					
					if(controller.areaId == localController.areaId ){
						
						//Typical Offloading with Subscriber Density
						  //Possible chances for offloading
						
						if(controllers.isCostBaseOffloadEn){
							logger.info("Start Wifi Offloading In The Area : "+controller.areaId +" Based on Controller Cost");
								// Based Offloading
								//Controller Ty
							if(controller.conType > localController.conType){
								//Should Check For Local Subscriber Density/Capacity Based Offloding
							  logger.info("Processing Controller Type , This Controller "+localController.conType + "And Remote Controller "+controller.conType);
							  remoteEntry=WifiOffloadSDNControllers.getUserFromRemoteController(controller,entry);
							  return remoteEntry;
							}else{
								
								//Subscriber Density Based Offloading
								if(controllers.isSubDenBaseOffloadEn){
									logger.info("Start Wifi Offloading In The Area : "+controller.areaId +" Based on Controller Subscriber Density");
									
									 if(controller.numMobileUsers > controller.maxNumMobileUsers){
										  logger.info("Checking For Remote Controller Subscriber Density : "+controller.numMobileUsers +" With Maximum Density of "+controller.maxNumMobileUsers);
										  remoteEntry=WifiOffloadSDNControllers.getUserFromRemoteController(controller,entry);
										  return remoteEntry;
									 }
									 else{
										 remoteEntry = null;
										 WifiOffloadUserEntry.userBlocked = true;
									     continue;
									 }
								}
								else{
									remoteEntry = null;
									continue;
								}
							  
							}
						}
						else{
							remoteEntry = null;
							  continue;
						}
						  
						  //SUbscriber Density Based Offloading
					}
					else{
						//checkForOffloadingScenario();
						logger.info("Start Mobility Management - Mobile User");
					 	remoteEntry=WifiOffloadSDNControllers.getUserFromRemoteController(controller,entry);
					 	return remoteEntry;
					}
									
					
				}else{
					logger.info("User "+entry.userMacAddress.toString()+" Not Found in Controller: "+controller.getIpAddress().toString());
					remoteEntry= null;
					continue;
				}
			}
			else{
				logger.info("Controller "+controller.getIpAddress().toString()+" is not enabled");
				remoteEntry= null;
				continue;
			}
		}
		
		return remoteEntry;
	}
	
	
	public static WifiOffloadUserEntry getUserFromRemoteController(WifiOffloadSDNController controller,WifiOffloadUserEntry entry){
		//Check Whether the user exist in that SDN Controller
		
		logger.info("Send Request For Getting User "+entry.userMacAddress.toString()+" From Controller "+controller.ipAddress.toString());
		long startTime = System.nanoTime();
				String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/user/json";
				String [] paramName = {"userid"};
				String userId = entry.userMacAddress.toString();
				String [] paramVal = {userId};
				WifiOffloadUserEntry userEntry=entry;
				try{
					
					String httpResponse=WifiOffloadRestClient.httpPost1(urlStr, paramName, paramVal);
					logger.info("Rest Operation Completed");
					userEntry = WifiOffloadJsonExtract.jsonToUserEntry(httpResponse);
					logger.info("User Entry :"+userEntry.toString());
					logger.info("REST HTTP POST RESPONSE: "+httpResponse+":UserId:"+userEntry.userMacAddress.toString());
				}
				catch(Exception e){
					logger.info(e.getMessage());
				}		
				
				long endTime = System.nanoTime();
		        double timeDiff= ((double)(endTime-startTime))/1000000000;
		        WiFiOffloadPerformanceMonitor.userGetRemoteUserTimeEn = true;
		        WiFiOffloadPerformanceMonitor.userGetRemoteUserTime = timeDiff;
			   logger.info("Time Taken for Check Conroller Enable: "+(((double)(endTime-startTime))/1000000000));
		
				
		return userEntry;
	}
	
	
	public static boolean checkForControllerEnable(WifiOffloadSDNController controller){
		long startTime = System.nanoTime();

		//String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/status/json";
		String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/controller/json";

		String status = "disable";
		//Check whether the SDN Controller is Running
		try{
			String httpResponse=WifiOffloadRestClient.httpGet(urlStr);
			// status = WifiOffloadJsonExtract.jsonExtractStatus(httpResponse);
			WifiOffloadSDNController c=WifiOffloadJsonExtract.jsonToController(httpResponse);
			logger.info(c.toString());
			if(c.isEnabled()){
				status = "enabled";
			}else{
				status = "disabled";
			}
			logger.info("REST HTTP GET RESPONSE: "+httpResponse+":Status:"+status);
			
		}catch(Exception e){
			logger.info(e.toString());
		}
	   long endTime = System.nanoTime();
	   logger.info("Time Taken for Check Conroller Enable: "+(((double)(endTime-startTime))/1000000000));
     
	      
		if(status.equals("enabled"))
		{
			return true;
		}else if (status.equals("disabled")) {
			return false;
		}else{
			return false;
		}
	}
	
	
	
	public static WifiOffloadSDNController sendControllerRequest(WifiOffloadSDNController controller){
		long startTime = System.nanoTime();

		//String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/status/json";
		String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/controller/json";

		String status = "disable";
		WifiOffloadSDNController c = new WifiOffloadSDNController();
		//Check whether the SDN Controller is Running
		try{
			String httpResponse=WifiOffloadRestClient.httpGet(urlStr);
			// status = WifiOffloadJsonExtract.jsonExtractStatus(httpResponse);
			c=WifiOffloadJsonExtract.jsonToController(httpResponse);
			logger.info(c.toString());
			logger.info("REST HTTP GET RESPONSE: "+httpResponse+":Status:"+status);
			
		}catch(Exception e){
			logger.info(e.toString());
		}
		
		long endTime = System.nanoTime();
        double timeDiff= ((double)(endTime-startTime))/1000000000;
        WiFiOffloadPerformanceMonitor.userControllerSyncTimeEn = true;
        WiFiOffloadPerformanceMonitor.userControllerSyncTime = timeDiff;
	   logger.info("Time Taken for Check Conroller Enable: "+(((double)(endTime-startTime))/1000000000));
     
	    return c;
	}
	
	
	
	
	public static boolean checkForUserInOtherControllers(WifiOffloadSDNController controller,WifiOffloadUserEntry entry){
		
		//Check for Enabling
		logger.info("CheckForUserInMulticast"+entry.userMacAddress.toString());
		long startTime = System.nanoTime();
		//Check Whether the user exist in that SDN Controller
		String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/userid/json";
		String [] paramName = {"userid"};
		String userId = entry.userMacAddress.toString();
		String [] paramVal = {userId};
		boolean isExist=false;
		try{
			
			String httpResponse=WifiOffloadRestClient.httpPost1(urlStr, paramName, paramVal);
			String status = WifiOffloadJsonExtract.jsonExtractStatus(httpResponse);
			logger.info("REST HTTP POST RESPONSE: "+httpResponse+":UserId:"+status);
			isExist = (status.equals("exist"))?true:false;
		}
		catch(Exception e){
			logger.info(e.getMessage());
		}
		
		long endTime = System.nanoTime();
        double timeDiff= ((double)(endTime-startTime))/1000000000;
        WiFiOffloadPerformanceMonitor.userCheckRemoteUserTimeEn = true;
        WiFiOffloadPerformanceMonitor.userCheckRemoteUserTime = timeDiff;
	   logger.info("Time Taken for Check Conroller Enable: "+(((double)(endTime-startTime))/1000000000));
 
		
		return isExist;
	}
	

	
	
}
