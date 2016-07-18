package net.floodlightcontroller.wifioffload;

import java.io.IOException;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class WifiOffloadJsonExtract {
	protected static Logger log = LoggerFactory.getLogger(WifiOffloadUserEntryResource.class);

	public static String jsonExtractStatus(String fmJson) throws IOException {
		String status = "";
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;

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
		     //log.info("Current Name:"+n);
			jp.nextToken();
			if (jp.getText().equals(""))
				continue;

			if (n == "status") {
				status = jp.getText();
				break;
			}
		}

		return status;
	}
	
	
	
	public static String jsonExtractUserIdStatus(String fmJson) throws IOException {
		String status = "";
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;

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
		     //log.info("Current Name:"+n);
			jp.nextToken();
			if (jp.getText().equals(""))
				continue;

			if (n == "userid") {
				status = jp.getText();
				break;
			}
		}

		return status;
	}
	
	
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



}
