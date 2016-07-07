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


public class WifiOffloadUserEntryResource extends ServerResource {
	protected static Logger log = LoggerFactory.getLogger(WifiOffloadUserEntryResource.class);

	@Get("json")
	public List<WifiOffloadUserEntry> retrieve() {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		return wifioffload.getUserEntries();
	}
	
	@Post
	public String store(String fmJson) {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		WifiOffloadUserEntry entry = jsonToUserEntry(fmJson);
		if (entry == null) {
			return "{\"status\" : \"Error! Could not parse wifi-offloading user entry, see log for details.\"}";
		}
		String status = null;
		if (checkUserEntryExists(entry, wifioffload.getUserEntries())) {
			status = "Error! A similar firewall rule already exists.";
			log.error(status);
			return ("{\"status\" : \"" + status + "\"}");
		} else {
			// add rule to wifi-offload
			wifioffload.addUserEntry(entry);
			status = "user entry added";
			return ("{\"status\" : \"" + status + "\", \"userid\" : \""+ Long.toString(entry.userId) + "\"}");
		}
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

	public static WifiOffloadUserEntry jsonToUserEntry(String fmJson) {
		WifiOffloadUserEntry entry = new WifiOffloadUserEntry();
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;
		try {
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

				// This is currently only applicable for remove().  In store(), ruleid takes a random number
				if (n.equalsIgnoreCase("userid")) {
					try {
						entry.userId = Long.parseLong(jp.getText());
					} catch (IllegalArgumentException e) {
						log.error("Unable to parse user ID: {}", jp.getText());
					}
				}

				// This assumes user having dpid info for involved switches
				else if (n.equalsIgnoreCase("dpid")) {
					entry.anyDpId = false;
					try {
						entry.dpId = DatapathId.of(jp.getText());
					} catch (NumberFormatException e) {
						log.error("Unable to parse switch DPID: {}", jp.getText());
						//TODO should return some error message via HTTP message
					}
				}

				else if (n.equalsIgnoreCase("portin")) {
					entry.anyPortIn = false;
					try {
						entry.portIn = OFPort.of(Integer.parseInt(jp.getText()));
					} catch (NumberFormatException e) {
						log.error("Unable to parse ingress port: {}", jp.getText());
						//TODO should return some error message via HTTP message
					}
				}

				else if (n.equalsIgnoreCase("usermacaddress")) {
					if (!jp.getText().equalsIgnoreCase("ANY")) {
						entry.anyUserMacAddress = false;
						try {
							entry.userMacAddress = MacAddress.of(jp.getText());
						} catch (IllegalArgumentException e) {
							log.error("Unable to parse user MAC: {}", jp.getText());
							//TODO should return some error message via HTTP message
						}
					}
				}

				else if (n.equalsIgnoreCase("useripaddress")) {
					if (!jp.getText().equalsIgnoreCase("ANY")) {
						entry.anyUserIpAddress = false;
						try {
							entry.userIpAddress = IPv4Address.of(jp.getText());
						} catch (IllegalArgumentException e) {
							log.error("Unable to parse user IP: {}", jp.getText());
							//TODO should return some error message via HTTP message
						}
					}
				}

				else if (n.equalsIgnoreCase("areaid")) {
					entry.anyAreaId = false;
					try {
						entry.areaId = Integer.parseInt(jp.getText());
					} catch (NumberFormatException e) {
						log.error("Unable to parse area ID: {}", jp.getText());
						//TODO should return some error message via HTTP message
					}
				}

				else if (n.equalsIgnoreCase("conid")) {
					entry.anySdnConId = false;
					try {
						entry.sdnConId = Integer.parseInt(jp.getText());
					} catch (NumberFormatException e) {
						log.error("Unable to parse SDN controller ID: {}", jp.getText());
						//TODO should return some error message via HTTP message
					}
				}				
			}
		} catch (IOException e) {
			log.error("Unable to parse JSON string: {}", e);
		}

		return entry;
	}

	public static boolean checkUserEntryExists(WifiOffloadUserEntry entry, List<WifiOffloadUserEntry> entries) {
		Iterator<WifiOffloadUserEntry> iter = entries.iterator();
		while (iter.hasNext()) {
			WifiOffloadUserEntry r = iter.next();

			// check if we find a similar rule
			if (entry.isSameAs(r)) {
				return true;
			}
		}

		// no rule matched, so it doesn't exist in the rules
		return false;
	}
}


