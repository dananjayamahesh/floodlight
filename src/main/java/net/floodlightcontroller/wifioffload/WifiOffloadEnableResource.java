package net.floodlightcontroller.wifioffload;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiOffloadEnableResource extends WifiOffloadResourceBase {
    private static final Logger log = LoggerFactory.getLogger(WifiOffloadEnableResource.class);

    @Get("json")
    public Object handleRequest() {
        log.warn("call to WifiOffloadDisableResource with method GET is not allowed. Use PUT: ");
        
        setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	return "{\"status\" : \"failure\", \"details\" : \"Use PUT to enable wifi-offload\"}";
    }

    @Put("json")
    public Object handlePut() {
        IWifiOffloadService firewall = getWifiOffloadService();

	firewall.enableWifiOffload(true);

        setStatus(Status.SUCCESS_OK);

	return "{\"status\" : \"success\", \"details\" : \"wifi-offload running\"}";
    }

}
