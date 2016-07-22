package net.floodlightcontroller.wifioffload;

import org.restlet.resource.Get;


import net.floodlightcontroller.wifioffload.IWifiOffloadService;

public class WifiOffloadStatusResource extends WifiOffloadResourceBase {
    @Get("json")
    public Object handleRequest() {
        IWifiOffloadService wifioffload = this.getWifiOffloadService();
       
	if (wifioffload.isEnabled())
		
	    return "{\"status\":\"enabled\",\"id\":\""+wifioffload.getLocalController().getIpAddress().toString()+"\"}";
	else
	    return "{\"status\":\"disabled\",\"id\":\""+wifioffload.getLocalController().getIpAddress().toString()+"\"}";
    }
}
