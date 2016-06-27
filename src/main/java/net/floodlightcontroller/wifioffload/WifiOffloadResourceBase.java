package net.floodlightcontroller.wifioffload;
import org.restlet.resource.ServerResource;

import net.floodlightcontroller.wifioffload.IWifiOffloadService;

public class WifiOffloadResourceBase extends ServerResource {

	 IWifiOffloadService getWifiOffloadService() {
			return (IWifiOffloadService)getContext().getAttributes().
			        get(IWifiOffloadService.class.getCanonicalName());
		    }
}
