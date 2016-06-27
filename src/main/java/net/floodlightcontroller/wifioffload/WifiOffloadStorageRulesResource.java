package net.floodlightcontroller.wifioffload;

import org.restlet.resource.Get;


public class WifiOffloadStorageRulesResource extends WifiOffloadResourceBase {
	// REST API for retrieving rules from storage

	@Get("json")
	public Object handleRequest() {
		IWifiOffloadService wifioffload = getWifiOffloadService();
		return wifioffload.getStorageRules();
	}
}
