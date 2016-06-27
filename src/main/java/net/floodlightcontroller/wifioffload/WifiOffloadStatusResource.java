package net.floodlightcontroller.wifioffload;

import org.restlet.resource.Get;


import net.floodlightcontroller.wifioffload.IWifiOffloadService;

public class WifiOffloadStatusResource extends WifiOffloadResourceBase {
    @Get("json")
    public Object handleRequest() {
        IWifiOffloadService wifioffload = this.getWifiOffloadService();

	if (wifioffload.isEnabled())
	    return "{\"result\" : \"wifi-offload enabled\"}";
	else
	    return "{\"result\" : \"wifi-offload disabled\"}";
    }
}
