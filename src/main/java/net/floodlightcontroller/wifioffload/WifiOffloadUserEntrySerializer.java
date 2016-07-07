package net.floodlightcontroller.wifioffload;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.floodlightcontroller.wifioffload.WifiOffloadUserEntry;

public class WifiOffloadUserEntrySerializer extends JsonSerializer<WifiOffloadUserEntry>{

	@Override
	public void serialize(WifiOffloadUserEntry entry, JsonGenerator jGen, SerializerProvider serializer)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
jGen.writeStartObject();
        
        jGen.writeNumberField("userid", entry.userId);
        jGen.writeNumberField("dpid", entry.dpId.getLong());
        jGen.writeNumberField("portin", entry.portIn.getPortNumber());
        jGen.writeNumberField("usermacaddr", entry.userMacAddress.getLong());
        jGen.writeNumberField("useripaddress", entry.userIpAddress.getInt());
        jGen.writeNumberField("areaid", entry.areaId);
        jGen.writeNumberField("conid", entry.sdnConId);
        jGen.writeEndObject();
	}

	
}
