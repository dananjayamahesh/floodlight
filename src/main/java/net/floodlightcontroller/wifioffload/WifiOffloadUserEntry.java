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
	public long areaId;
	public long sdnConId;
	
	public int conType;
	public long timestamp;
	public int opeartion;
	
	public boolean anyDpId;
	public boolean anyPortIn;
	public boolean anyUserMacAddress;
	public boolean anyUserIpAddress;
	public boolean anyAreaId;
	public boolean anySdnConId;
	
	public int priority = 0;
	
	public static boolean userBlocked;
	
	public WifiOffloadUserEntry(){
	    
	}
	
	@Override
	public boolean equals(Object obj){
		
		return this.userMacAddress.equals((MacAddress)obj);
	}
	
	public int hashCode(){
		return (int)userMacAddress.getLong();
	}
	
	public String toString(){
		String entryStr = "";
		
		entryStr  += this.timestamp+",";
		entryStr  += ((this.opeartion==0)?"ADD,":((this.opeartion==1)?"REMOVE,":"OTHER,"));	
		entryStr  += this.userId+",";
		entryStr  += this.dpId.getLong()+",";
		entryStr  += this.portIn.getPortNumber()+",";
		entryStr  += this.userMacAddress.toString()+",";
		entryStr  += this.userIpAddress.toString()+",";
		entryStr  += this.sdnConId+",";
		entryStr  += this.areaId+",";
		entryStr  += (this.conType==0)?"WIFI":((this.conType==1)?"LTE":"OTHER");

		return entryStr;
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
