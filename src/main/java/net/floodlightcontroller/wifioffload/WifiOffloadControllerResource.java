package net.floodlightcontroller.wifioffload;

import java.io.IOException;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class WifiOffloadControllerResource extends WifiOffloadResourceBase {
	// REST API to get or set local subnet mask -- this only makes sense for one subnet
	// will remove later

	private static final Logger log = LoggerFactory.getLogger(WifiOffloadUserIdResource.class);

	@Get("json")
	public WifiOffloadSDNController handleRequest() {
		IWifiOffloadService wifioffload = getWifiOffloadService();
		
		//return "{\"conname\":\"" + wifioffload.getConName() + "\"}";
		log.info("Controller Stat: "+wifioffload.toString());
		return wifioffload.getLocalController();
	}


	@Post
	public String handlePost(String fmJson) {
		IWifiOffloadService wifioffload = getWifiOffloadService();

		String conName;
		try {
			conName = jsonExtractConName(fmJson);
		} catch (IOException e) {
			log.error("Error parsing new subnet mask: " + fmJson, e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return "{\"status\" : \"Error! Could not parse new subnet mask, see log for details.\"}";
		}

		wifioffload.setConName(conName);

		setStatus(Status.SUCCESS_OK);
		return ("{\"status\" : \"subnet mask set\"}");
	}

	/**
	 * Extracts subnet mask from a JSON string
	 * @param fmJson The JSON formatted string
	 * @return The subnet mask
	 * @throws IOException If there was an error parsing the JSON
	 */
	public static String jsonExtractConName(String fmJson) throws IOException {
		String conName = "";
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
			jp.nextToken();
			if (jp.getText().equals(""))
				continue;

			if (n == "conname") {
				conName = jp.getText();
				break;
			}
		}

		return conName;
	}
	
	
}


