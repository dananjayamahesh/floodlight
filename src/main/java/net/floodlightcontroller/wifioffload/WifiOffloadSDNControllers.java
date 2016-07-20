package net.floodlightcontroller.wifioffload;

import java.util.Iterator;
import java.util.TreeSet;

import org.projectfloodlight.openflow.types.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiOffloadSDNControllers {
 
	private TreeSet<WifiOffloadSDNController> controllers = new TreeSet<WifiOffloadSDNController>();
	private WifiOffloadSDNController localController;
	private int numControllers;
	protected static Logger logger;
	
	public WifiOffloadSDNControllers(){
		this.numControllers =0;
		logger = LoggerFactory.getLogger(WifiOffloadSDNControllers.class);
		localController = null;
	}
	
	public long genId(WifiOffloadSDNController controller){
		MacAddress mac= controller.getMacAddress();
		return mac.getLong();
	}
	
	public boolean addController(WifiOffloadSDNController controller){
		controller.setId(genId(controller));
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
	
	public Iterator<WifiOffloadSDNController> getControllers(){
		return this.controllers.iterator();
	}
	
	public static WifiOffloadUserEntry searchUserInNetwork(WifiOffloadSDNControllers controllers,WifiOffloadUserEntry entry){
		
		Iterator<WifiOffloadSDNController>iter = controllers.getControllers();
		WifiOffloadUserEntry remoteEntry=null;
		while(iter.hasNext()){
			WifiOffloadSDNController controller = iter.next();
			
			boolean isEnabled = false;
			if(controller.isEnabled()){
				isEnabled = true;
			}
			else{
				isEnabled =WifiOffloadSDNControllers.checkForControllerEnable(controller);
				controller.setEnabled(isEnabled);
			}
			
			if(isEnabled){
				if(WifiOffloadSDNControllers.checkForUserInOtherControllers(controller,entry)){
					remoteEntry=WifiOffloadSDNControllers.getUserFromRemoteController(controller,entry);
					return remoteEntry;
				}else{
					remoteEntry= null;
					continue;
				}
			}
			else{
				remoteEntry= null;
				continue;
			}
		}
		
		return remoteEntry;
	}
	
	
	public static WifiOffloadUserEntry getUserFromRemoteController(WifiOffloadSDNController controller,WifiOffloadUserEntry entry){
		//Check Whether the user exist in that SDN Controller
				String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/user/json";
				String [] paramName = {"userid"};
				String userId = entry.userMacAddress.toString();
				String [] paramVal = {userId};
				WifiOffloadUserEntry userEntry=entry;
				try{
					
					String httpResponse=WifiOffloadRestClient.httpPost1(urlStr, paramName, paramVal);
					userEntry = WifiOffloadJsonExtract.jsonToUserEntry(httpResponse);
					logger.info("REST HTTP POST RESPONSE: "+httpResponse+":UserId:"+userEntry.userMacAddress.toString());
				}
				catch(Exception e){
					logger.info(e.getMessage());
				}		
				
			
				
		return userEntry;
	}
	
	
	public static boolean checkForControllerEnable(WifiOffloadSDNController controller){
		String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/status/json";
		String status = "disable";
		//Check whether the SDN Controller is Running
		try{
			String httpResponse=WifiOffloadRestClient.httpGet(urlStr);
			 status = WifiOffloadJsonExtract.jsonExtractStatus(httpResponse);
			logger.info("REST HTTP GET RESPONSE: "+httpResponse+":Status:"+status);
			
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		
		if(status.equals("enabled"))
		{
			return true;
		}else if (status.equals("disabled")) {
			return false;
		}else{
			return false;
		}
	}
	
	public static boolean checkForUserInOtherControllers(WifiOffloadSDNController controller,WifiOffloadUserEntry entry){
		
		//Check for Enabling
		logger.info("CheckForUserInMulticast"+entry.userMacAddress.toString());
		
		//Check Whether the user exist in that SDN Controller
		String urlStr = "http://"+controller.getIpAddress().toString()+":8080/oulu/wifioffload/module/userid/json";
		String [] paramName = {"userid"};
		String userId = entry.userMacAddress.toString();
		String [] paramVal = {userId};
		try{
			
			String httpResponse=WifiOffloadRestClient.httpPost1(urlStr, paramName, paramVal);
			String status = WifiOffloadJsonExtract.jsonExtractStatus(httpResponse);
			logger.info("REST HTTP POST RESPONSE: "+httpResponse+":UserId:"+status);
		}
		catch(Exception e){
			logger.info(e.getMessage());
		}
		
		return false;
	}
	

	
	
}
