package net.floodlightcontroller.wifioffload;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WifiOffloadUserEntryResource extends ServerResource {
	protected static Logger log = LoggerFactory.getLogger(WifiOffloadUserEntryResource.class);

	@Get("json")
	public List<WifiOffloadUserEntry> retrieve() {
		IWifiOffloadService wifioffload =
				(IWifiOffloadService)getContext().getAttributes().
				get(IWifiOffloadService.class.getCanonicalName());

		return wifioffload.getRules();
	}

}
