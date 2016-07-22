package net.floodlightcontroller.wifioffload;

import java.util.Scanner;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using=WifiOffloadControllerSerializer.class)
public class WifiOffloadSDNController implements Comparable<WifiOffloadSDNController> {

	 public long id;
	 public String name;
	 public String description;
	 public long areaId;
	 public MacAddress macAddress;
	 public IPv4Address ipAddress;
	 public long numMobileUsers;
	 public long maxNumMobileUsers;
	 public boolean enabled;
	 public int conType=0;
	 	 
	 public int tcpPort;	 
	 public int costFactor;
	 public int priority;
	 public int congestionFactor;
	 public int qosFactor;
	 public int userFactor;
	 
	 public static int CONTYPE_WIFI = 0;
	 public static int CONTYPE_LTE  = 1;
	 public static int CONTYPE_3G 	= 2;
	 
	 
	 
	 //Controller Type
	 private CONTYPE controllerType = CONTYPE.WIFI;
	 
	 public enum CONTYPE {WIFI(0),LTE(1),C3G(2),C2G(3),C1G(4);
	     private int conType;
	     
	     CONTYPE(int conType){
	    	 this.conType = conType;
	     }
	     
	     public String getType(){
	    	 return "Controller Type :"+this.conType;
	     }
	     
	 }
	 
	public  WifiOffloadSDNController(long id, String name,String description,long areaId,MacAddress macAddress, IPv4Address ipAddress, int tcpPort, long numMobileUsers, long maxNumMobileUsers, boolean enabled, int conType ){
		 this.id = id;
		 this.name = name;
		 this.description = description;
		 this.areaId = areaId;
		 this.macAddress = macAddress;
		 this.ipAddress  = ipAddress;
		 this.numMobileUsers = numMobileUsers;
		 this.maxNumMobileUsers = maxNumMobileUsers;
		 this.enabled = enabled;
		 this.tcpPort = tcpPort;
		 this.costFactor =0;
		 this.qosFactor =0;
		 this.congestionFactor = 0;
		 this.priority =0;
		 this.userFactor =0;
		 //Controller Type For Subscriber Density
		 this.conType = conType;
	 }
	
	public WifiOffloadSDNController(){
		 this.id = 0;
		 this.name = "";
		 this.description = "";
		 this.areaId = 0;
		 this.macAddress = MacAddress.of("00:00:00:00:00:00");
		 this.ipAddress  = IPv4Address.of("0.0.0.0");
		 this.numMobileUsers = 0;
		 this.maxNumMobileUsers = 0;
		 this.enabled = false;
		 this.tcpPort = 0;
		 this.costFactor =0;
		 this.qosFactor =0;
		 this.congestionFactor = 0;
		 this.priority =0;
		 this.userFactor =0;
		 //Controller Type For Subscriber Density
		 this.conType = 0; //default Wi-FI

	}
	
	
	
	public void setConType(int conType){
		this.conType = conType;
	}
	
	public int getConType(){
		return this.conType;
	}
	
	public long genID(){
		return 0;
	}
	
	public void setId(long id){
		this.id = id;
	}	
	
	public long getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}	
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}	
	
	public String getName(){
		return this.name;
	}
	
	public void setArea(long areaId){
		this.areaId = areaId;
	}
	
	public long getAreadId(){
		return this.areaId;
	} 
	
	public void setMacAddress(MacAddress macAddress){
		this.macAddress = macAddress;
	}
	
	public MacAddress getMacAddress(){
		return this.macAddress;
	}
	
	public void setNumMobileUsers(long numMobileUsers){
		this.numMobileUsers = numMobileUsers;
	}
	
	public long getNumMobileUsers(){
		return this.numMobileUsers;
	}
	
	public void setIpAddress(IPv4Address ipAddress){
		this.ipAddress = ipAddress;
	}
	
	public IPv4Address getIpAddress(){
		return this.ipAddress;
	}
	public void setMaxNumMobileUsers(long maxNumMobileUsers){
		this.maxNumMobileUsers = maxNumMobileUsers;
	}
	
	public long getMaxNumMobileUsers(){
		return this.maxNumMobileUsers;
	}
	
	public void enabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public void setTcpPort(int tcpPort ){
		this.tcpPort = tcpPort;
	}
	
	public int getTcpPort(){
		return this.tcpPort;
	}
	
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	public int getPriority(){
		return this.priority;
	}
	
	public void setQosFactor(int qosFactor){
		this.qosFactor = qosFactor;
	}
	
	public int getQosFacotr(){
		return this.qosFactor;
	}

	public void setUserFactor(int userFactor){
		this.userFactor = userFactor;
	}
	
	public int getUserFacotr(){
		return this.userFactor;
	}

	public void setCostFactor(int costFactor){
		this.costFactor = costFactor;
	}
	
	public int getCostFacotr(){
		return this.costFactor;
	}

	public void setCongestionFactor(int congestionFactor){
		this.congestionFactor = congestionFactor;
	}
	
	public int getCongestionFacotr(){
		return this.congestionFactor;
	}

	@Override
	public int compareTo(WifiOffloadSDNController o) {
		// TODO Auto-generated method stub
		return this.priority - o.priority;
		
	}
	
	public String toString(){
		return "id = "+this.id+"\narea-id = "+this.areaId+"\nname = "+this.name+"\nipaddress = "+this.ipAddress.toString()+"\nmac-address = "+this.macAddress.toString()+"\nnum-users = "+this.numMobileUsers+"\nmax-num-users = "+this.maxNumMobileUsers+"\nenabled = "+this.enabled+"\ntype = "+this.conType;
	}
}
