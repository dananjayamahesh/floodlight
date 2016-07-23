package net.floodlightcontroller.wifioffload;



import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.firewall.IFirewallService;


public class WifiOffloadUserResource extends ServerResource {

	protected static Logger log = LoggerFactory.getLogger(WifiOffloadUserEntryResource.class);

	@Get("json")
	public List<WifiOffloadUserEntry> retrieve() {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		return wifioffload.getUserEntries();
	}
	
	@Post
	public WifiOffloadUserEntry search(String fmJson) {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		WifiOffloadUserEntry entry = jsonToUserEntry(fmJson);
		if (entry == null) {
			log.info("{\"status\" : \"Error! Could not parse wifi-offloading user entry, see log for details.\"}");
		}
		String status = null;
		WifiOffloadUserEntry n = null;
		n = getUserEntryById(entry,wifioffload.getUserEntries());
		log.info("Removing User "+entry.userMacAddress.toString()+" From This Controller");
		this.remove(fmJson);
		log.info("User Removed From The DataBase");
		return n;
	}

	/**
	 * Takes a Firewall Rule string in JSON format and parses it into
	 * our firewall rule data structure, then deletes it from the firewall.
	 * @param fmJson The Firewall rule entry in JSON format.
	 * @return A string status message
	 */

	@Delete
	public String remove(String fmJson) {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		WifiOffloadUserEntry entry = jsonToUserEntry(fmJson);
		if (entry == null) {
			//TODO compose the error with a json formatter
			return "{\"status\" : \"Error! Could not parse wifi-offload user entry, see log for details.\"}";
		}
		
		String status = null;
		boolean exists = false;
		Iterator<WifiOffloadUserEntry> iter = wifioffload.getUserEntries().iterator();
		while (iter.hasNext()) {
			WifiOffloadUserEntry r = iter.next();
			if (r.userId == entry.userId) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			status = "Error! Can't delete, a rule with this ID doesn't exist.";
			log.error(status);
		} else {
			// delete rule from firewall
			wifioffload.deleteUserEntry(entry.userId);
			status = "Rule deleted";
		}
		return ("{\"status\" : \"" + status + "\"}");
	}

	/**
	 * Turns a JSON formatted Firewall Rule string into a FirewallRule instance
	 * @param fmJson The JSON formatted static firewall rule
	 * @return The FirewallRule instance
	 * @throws IOException If there was an error parsing the JSON
	 */

	public static WifiOffloadUserEntry jsonToUserEntry(String fmJson) 
	{
		
		WifiOffloadUserEntry entry = new WifiOffloadUserEntry();
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;
		String userId ="";
		try{
			try {
				jp = f.createParser(fmJson);
			} catch (JsonParseException e) {
				throw new IOException(e);
			}

			jp.nextToken();
			if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
				throw new IOException("Expected START_OBJECT");
			}

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
					throw new IOException("Expected FIELD_NAME");
				}

				String n = jp.getCurrentName();
				jp.nextToken();
				if (jp.getText().equals("")) {
					continue;
				}
				
				if (n == "userid") {
					userId = jp.getText();
					break;
				}
			}
			
		}
		catch(IOException e){
		
		}			
				entry.userMacAddress = MacAddress.of(userId);
				entry.userId 		= entry.genID();
				return entry;	
	}
	
	public static WifiOffloadUserEntry getUserEntryById(WifiOffloadUserEntry entry, List<WifiOffloadUserEntry> entries) {
		Iterator<WifiOffloadUserEntry> iter = entries.iterator();
		while (iter.hasNext()) {
			WifiOffloadUserEntry r = iter.next();

			// check if we find a similar rule
			if (entry.isSameAs(r)) {
				return r;
			}
		}

		// no rule matched, so it doesn't exist in the rules
		return null;
	}

	
}
