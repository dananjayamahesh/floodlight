package net.floodlightcontroller.wifioffload;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class WifiOffloadControllerSerializer extends JsonSerializer<WifiOffloadSDNController>{

	@Override
	public void serialize(WifiOffloadSDNController controller, JsonGenerator jGen, SerializerProvider serializer)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
jGen.writeStartObject();

		jGen.writeBooleanField("enabled", controller.enabled);
        jGen.writeNumberField("id", controller.id);
        jGen.writeStringField("name", controller.name);
        jGen.writeStringField("description", controller.description);
        jGen.writeNumberField("areaid", controller.areaId);
        jGen.writeNumberField("macaddress", controller.macAddress.getLong());
        jGen.writeNumberField("ipaddress", controller.ipAddress.getInt());
        jGen.writeNumberField("users", controller.numMobileUsers);
        jGen.writeNumberField("maxusers", controller.maxNumMobileUsers);
        jGen.writeNumberField("contype", controller.conType);

        jGen.writeEndObject();
	}

	
}

