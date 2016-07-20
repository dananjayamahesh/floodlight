package net.floodlightcontroller.wifioffload;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.projectfloodlight.openflow.types.MacAddress;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;


public class WifiOffloadUserIdResource extends WifiOffloadResourceBase {
	// REST API to get or set local subnet mask -- this only makes sense for one subnet
	// will remove later

	private static final Logger log = LoggerFactory.getLogger(WifiOffloadUserIdResource.class);

	@Get("json")
	public Object handleRequest(String userIdStr) {
		IWifiOffloadService wifioffload = getWifiOffloadService();
		
		log.info("SEARCHING: "+userIdStr);
		MacAddress userMacAddress = MacAddress.of(userIdStr);
		long userId= userMacAddress.getLong();
		boolean foundUserId=checkUserIdExists(userId,wifioffload.getUserEntries());
		return "{\"userid\":\"" +userId+":"+ ((foundUserId)?"YES":"NO") + "\"}";
	}	

	@Post
	public String handlePost(String fmJson) {
		IWifiOffloadService wifioffload = getWifiOffloadService();

		String userIdStr;
		try {
			//log.info("fmJason:"+fmJson);
			userIdStr = jsonExtractUserId(fmJson);
		} catch (IOException e) {
			log.error("Error parsing new user id: " + fmJson, e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return "{\"status\" : \"Error! Could not parse new user id, see log for details.\"}";
		}
          
				
		MacAddress userMacAddress = MacAddress.of(userIdStr);
		long userId= userMacAddress.getLong();
		wifioffload.setUserId(userId);
		boolean foundUserId=checkUserIdExists(userId,wifioffload.getUserEntries());
		setStatus(Status.SUCCESS_OK);
		if(foundUserId){
			return ("{\"status\":\"exist\"}");
		}
		else {
			return ("{\"status\":\"notexist\"}");
		}
		
	}

	/**
	 * Extracts subnet mask from a JSON string
	 * @param fmJson The JSON formatted string
	 * @return The subnet mask
	 * @throws IOException If there was an error parsing the JSON
	 */
	public static String jsonExtractUserId(String fmJson) throws IOException {
		String userId = "";
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
				userId = jp.getText();
				break;
			}
		}

		return userId;
	}
	
	public static boolean checkUserIdExists(long userId, List<WifiOffloadUserEntry> entries) {
		Iterator<WifiOffloadUserEntry> iter = entries.iterator();
		while (iter.hasNext()) {
			WifiOffloadUserEntry r = iter.next();

			// check if we find a similar rule
			if (userId == r.userId) {
				return true;
			}
		}

		// no rule matched, so it doesn't exist in the rules
		return false;
	}
	
	
}


