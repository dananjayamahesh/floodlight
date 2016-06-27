package net.floodlightcontroller.wifioffload;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.wifioffload.WifiOffloadUserEntrySerializer;


@JsonSerialize(using=WifiOffloadUserEntrySerializer.class)
public class WifiOffloadUserEntry implements Comparable<WifiOffloadUserEntry> {
 
	public int userId;
	public DatapathId dpId;
	public OFPort portIn;
	public MacAddress userMacAddress;
	public IPv4Address userIpAddress;
	public int areaId;
	public int sdnConId;
	
	public int priority = 0;
	  
	public WifiOffloadUserEntry(){
		
	}
	
	@Override
	public boolean equals(Object obj){
		
		return this.userMacAddress.equals((MacAddress)obj);
	}
	
	public int hashCode(){
		return (int)userMacAddress.getLong();
	}
	
	@Override
	public int compareTo(WifiOffloadUserEntry entry) {
		// TODO Auto-generated method stub
		return this.priority - entry.priority;
	}
	
	
	  public int genID() {
	        int uid = this.hashCode();
	        if (uid < 0) {
	            uid = Math.abs(uid);
	            uid = uid * 15551;
	        }
	        return uid;
	    }

}
