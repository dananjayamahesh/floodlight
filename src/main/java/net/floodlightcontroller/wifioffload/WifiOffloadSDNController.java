package net.floodlightcontroller.wifioffload;

import java.util.Scanner;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;

public class WifiOffloadSDNController implements Comparable<WifiOffloadSDNController> {

	 private long id;
	 private String name;
	 private String description;
	 private long areaId;
	 private MacAddress macAddress;
	 private IPv4Address ipAddress;
	 private long numMobileUsers;
	 private long maxNumMobileUsers;
	 private boolean enabled;
	 private int tcpPort;	 
	 private int costFactor;
	 private int priority;
	 private int congestionFactor;
	 private int qosFactor;
	 private int userFactor;
	 
	public  WifiOffloadSDNController(long id, String name,String description,long areaId,MacAddress macAddress, IPv4Address ipAddress, int tcpPort, long numMobileUsers, long maxNumMobileUsers, boolean enabled ){
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
	 }
	
	public WifiOffloadSDNController createController(){
		Scanner scn = new Scanner (System.in);
		System.out.println("Enter ID, NAME, DESCRIPTION, AREAID, MAC_ADDRESS, IP_ADDRESS, TCP_PORT, NUMBER_OF_USERS, MAX_NUMBER_OF_USERS, ENABLED");
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
		
		return new WifiOffloadSDNController(id, name, description, areaId, macAddress, ipAddress, tcpPort, numMobileUsers, maxNumMobileUsers, enabled);
		
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
		return "ID:"+id+" ,IPAddress:"+this.ipAddress.toString();
	}
}
