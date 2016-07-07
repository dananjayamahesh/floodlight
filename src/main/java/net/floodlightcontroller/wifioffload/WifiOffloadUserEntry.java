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
 
	public long userId;
	public DatapathId dpId;
	public OFPort portIn;
	public MacAddress userMacAddress;
	public IPv4Address userIpAddress;
	public int areaId;
	public int sdnConId;
	
	public boolean anyDpId;
	public boolean anyPortIn;
	public boolean anyUserMacAddress;
	public boolean anyUserIpAddress;
	public boolean anyAreaId;
	public boolean anySdnConId;
	
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
	
	
	  public long genID() {
	      
	        return this.userMacAddress.getLong();
	    }
	  
	    public boolean isSameAs(WifiOffloadUserEntry r) {
	       
	    	if (this.userId != r.userId) {
	    		
	            return false;
	        }
	        return true;
	    }

}
