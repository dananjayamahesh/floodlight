package net.floodlightcontroller.wifioffload;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.firewall.FirewallDisableResource;
import net.floodlightcontroller.firewall.FirewallEnableResource;
import net.floodlightcontroller.firewall.FirewallRulesResource;
import net.floodlightcontroller.firewall.FirewallStatusResource;
import net.floodlightcontroller.firewall.FirewallStorageRulesResource;
import net.floodlightcontroller.firewall.FirewallSubnetMaskResource;
import net.floodlightcontroller.restserver.RestletRoutable;
import net.floodlightcontroller.staticflowentry.web.StaticFlowEntryPusherResource;

public class WifiOffloadWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		// TODO Auto-generated method stub
		Router router= new Router(context);
		router.attach("/json", WifiOffloadResource.class);
		router.attach("/json/store", WifiOffloadResource.class);
		router.attach("/module/status/json",       WifiOffloadStatusResource.class);
        router.attach("/module/enable/json",       WifiOffloadEnableResource.class);
        router.attach("/module/disable/json",      WifiOffloadDisableResource.class);
        router.attach("/module/userid/json",       WifiOffloadUserIdResource.class);
        router.attach("/module/controller/json",       WifiOffloadControllerResource.class);
        router.attach("/module/storageRules/json", WifiOffloadStorageRulesResource.class);

        router.attach("/entries/json",              WifiOffloadUserEntryResource.class);
        router.attach("/user/json",                 WifiOffloadUserResource.class);
		return router;
	}

	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/oulu/wifioffload";
	}

}
