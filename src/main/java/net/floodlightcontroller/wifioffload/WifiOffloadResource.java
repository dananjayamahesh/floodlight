package net.floodlightcontroller.wifioffload;


import net.floodlightcontroller.storage.IStorageSourceService;

import java.io.IOException;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiOffloadResource extends ServerResource{
	
	protected static Logger log = LoggerFactory.getLogger(WifiOffloadResource.class);
	
	@Post
	public String store(String fmJson){
		IStorageSourceService storageSource=
				(IStorageSourceService)getContext().getAttributes().get(IStorageSourceService.class.getCanonicalName());
		Map <String,Object>rowValues;
		log.info("MAHESH-"+fmJson);
		try{
			rowValues = WifiOffloadEntries.jsonToStorageEntry(fmJson);
			return "MAHESH-STORE";
		}
		 catch (IOException e) {
				log.error("Error parsing push flow mod request: " + fmJson, e);
				return "{\"status\" : \"Error! Could not parse flow mod, see log for details.\"}";
		} 
		
	}

}
