package net.floodlightcontroller.wifioffload;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.firewall.IFirewallService;
import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.storage.IResultSet;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.storage.StorageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WifiOffload implements IWifiOffloadService,IOFMessageListener, IFloodlightModule {
    
	protected IFloodlightProviderService floodlightProvider;
	
	protected static Logger logger;
	protected IRestApiService restApiService;
	protected IStorageSourceService storageSource;	
	protected WifiOffloadRestClient restClient;	
	protected List<WifiOffloadUserEntry> entries; // protected by synchronized
	protected boolean enabled;
	
	public WifiOffloadSDNController controller;
	public WifiOffloadSDNControllers controllers;
	
	
	public static final String TABLE_NAME = "controller_wifioffload_userentries";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_PORTIN = "portin";
	public static final String COLUMN_DPID = "dpid";
	public static final String COLUMN_USERMACADDR = "usermacaddr";
	public static final String COLUMN_USERIPADDR = "useripaddr";
	public static final String COLUMN_AREAID = "areaid";
	public static final String COLUMN_CONID = "conid";
	
	public String conName="default-name";
	public static String ColumnNames[] = { COLUMN_USERID,COLUMN_DPID,COLUMN_PORTIN,COLUMN_USERMACADDR,COLUMN_USERIPADDR,COLUMN_AREAID, COLUMN_CONID };
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return WifiOffload.class.getSimpleName();
		
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IWifiOffloadService.class);
		return l;
		
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		
		
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		// We are the class that implements the service
		m.put(IWifiOffloadService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	
		Collection<Class<? extends IFloodlightService>> l =
		        new ArrayList<Class<? extends IFloodlightService>>();
		    l.add(IFloodlightProviderService.class);
			l.add(IStorageSourceService.class);
		    l.add(IRestApiService.class);
		    return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	   
	    logger = LoggerFactory.getLogger(WifiOffload.class);
	    restApiService = context.getServiceImpl(IRestApiService.class);
		storageSource = context.getServiceImpl(IStorageSourceService.class);
		entries = new ArrayList<WifiOffloadUserEntry>();
		enabled = false;
		restClient = new WifiOffloadRestClient();
		
		//Starting The SDN Controllers Stack
		controllers= new WifiOffloadSDNControllers();
		
		Scanner scn = new Scanner(System.in);
		logger.info("Enter Local Controller IP Address : \n");
		String ipStr= scn.next();
		logger.info("Enter Peer  Controller IP Address : \n");
		String peerIpStr = scn.next();
		logger.info("Enter Area ID: \n");
		int areaId = scn.nextInt();	
		logger.info("Enter Controller Type ID: \n");
		int conType = scn.nextInt();
		logger.info("Enter Local Controller MAXIMUM Subscribers: \n");
		int maxNumMobileUsers = scn.nextInt();
		logger.info("Enter Enable for Cost Based Wifi-Offloading: \n");
		boolean isCostBaseOffloadEn = scn.nextBoolean();
		logger.info("Enter Enable for Density Based Wifi-Offloading: \n");
		boolean isSubDenBaseOffloadEn = scn.nextBoolean();

		scn.close();
	controller = new WifiOffloadSDNController(0, "Master-Controller", "Main SDN Controller in The Network", 0, MacAddress.of("00:00:00:00:00:33"), IPv4Address.of(ipStr), 8080, 0,50, false,0);
	controller.setArea(areaId);
	controller.setConType(conType);
	controller.setMaxNumMobileUsers(maxNumMobileUsers);
	
	   //logger.info("Create Local SDN Controller");
	   //controller = controllers.createController();
      //logger.info("Create Peer SDN Controller");

	WifiOffloadSDNController peerController = new WifiOffloadSDNController(0, "Master-Controller", "Main SDN Controller in The Network", 0, MacAddress.of("00:00:00:00:00:44"), IPv4Address.of(peerIpStr), 8080, 0,50, false,0);
		//WifiOffloadSDNController peerController = controllers.createController();
	   controllers.addController(peerController);
		controllers.setLocalController(controller);
		controllers.setCostBaseOffloadEnable(isCostBaseOffloadEn);
		controllers.setSubDenBaseOffloadEnable(isSubDenBaseOffloadEn);
		
		logger.info("Initializing Wifi-Offload Core Module...");
		
		logger.info("OpenFile: "+WiFiOffloadPerformanceMonitor.initPrinter("/home/mahesh/FLOODLIGHT/wifioffload-output/mobility.csv")+"");
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		logger.info("Starting-Up Wifi-Offload Core Module...");
		logger.info("Starting SDN Controller "+controller.getIpAddress().toString());
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApiService.addRestletRoutable(new WifiOffloadWebRoutable());
		storageSource.createTable(TABLE_NAME, null);
		storageSource.setTablePrimaryKeyName(TABLE_NAME, COLUMN_USERID);
		//new Thread(restClient).start();
		this.entries = readRulesFromStorage();
		
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
	  
		if (!this.controller.isEnabled()) {
			return Command.CONTINUE;
		}

		switch (msg.getType()) {
		case PACKET_IN:
			logger.info("RECEIVE-PACKET-IN");
			IRoutingDecision decision = null;
			if (cntx != null) {
				decision = IRoutingDecision.rtStore.get(cntx, IRoutingDecision.CONTEXT_DECISION);
				return this.processPacketInMessage(sw, (OFPacketIn) msg, decision, cntx);
			}
			break;
		default:
			break;
		}

		//return Command.CONTINUE;
		// TODO Auto-generated method stub
		
        return Command.CONTINUE;
	}

	
	protected ArrayList<WifiOffloadUserEntry> readRulesFromStorage() {
		ArrayList<WifiOffloadUserEntry> l = new ArrayList<WifiOffloadUserEntry>();

		try {
			Map<String, Object> row;

			// (..., null, null) for no predicate, no ordering
			IResultSet resultSet = storageSource.executeQuery(TABLE_NAME, ColumnNames, null, null);

			// put retrieved rows into FirewallRules
			for (Iterator<IResultSet> it = resultSet.iterator(); it.hasNext();) {
				row = it.next().getRow();
				// now, parse row
				WifiOffloadUserEntry r = new WifiOffloadUserEntry();
				if (!row.containsKey(COLUMN_USERID) || !row.containsKey(COLUMN_DPID)) {
					logger.error( "skipping entry with missing required 'ruleid' or 'switchid' entry: {}", row);
					return l;
				}
				try {
					r.userId = Long.parseLong((String) row.get(COLUMN_USERID));
					r.dpId = DatapathId.of((String) row.get(COLUMN_DPID));

					for (String key : row.keySet()) {
						if (row.get(key) == null) {
							continue;
						}
						if (key.equals(COLUMN_USERID) || key.equals(COLUMN_DPID) || key.equals("id")) {
							continue; // already handled
						} else if (key.equals(COLUMN_PORTIN)) {
							r.portIn = OFPort.of(Integer.parseInt((String) row.get(COLUMN_PORTIN)));
						} else if (key.equals(COLUMN_USERMACADDR)) {
							r.userMacAddress = MacAddress.of(Long.parseLong((String) row.get(COLUMN_USERMACADDR)));
						}  else if (key.equals(COLUMN_USERIPADDR)) {
							r.userIpAddress = IPv4Address.of(Integer.parseInt((String) row.get(COLUMN_USERIPADDR)));
						} else if (key.equals(COLUMN_AREAID)) {
							r.areaId = Long.parseLong((String) row.get(COLUMN_AREAID));
						} else if (key.equals(COLUMN_CONID)) {
							r.sdnConId =Long.parseLong((String) row.get(COLUMN_CONID));
						}
					}
						
				} catch (ClassCastException e) {
					logger.error("skipping rule {} with bad data : " + e.getMessage(), r.userId);
				}
				
			}
		} catch (StorageException e) {
			logger.error("failed to access storage: {}", e.getMessage());
			// if the table doesn't exist, then wait to populate later via
			// setStorageSource()
		}

		// now, sort the list based on priorities
		Collections.sort(l);

		return l;
	}
	
	//IWifiOffloadServices
	@Override
	public void enableWifiOffload(boolean enabled) {
		logger.info("Setting wifi-offload to {}", enabled);
		this.controller.setEnabled(enabled);
		//this.enabled = enabled;
		
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		//return enabled;
		return this.controller.isEnabled();
	}

	@Override
	public List<WifiOffloadUserEntry> getUserEntries() {
		return this.entries;
	}

	@Override
	public String getUserId() {
		return "MAHESH";
	}

	@Override
	public void setUserId(long userId) {
		
		
	}
	
	 public String getConName(){
		 return conName;
	}
	   
	 public void setConName(String conName){
	    this.conName=conName;
	}

	@Override
	public List<Map<String, Object>> getStorageUserEntries() {
		ArrayList<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		try {
			// null1=no predicate, null2=no ordering
			IResultSet resultSet = storageSource.executeQuery(TABLE_NAME, ColumnNames, null, null);
			for (Iterator<IResultSet> it = resultSet.iterator(); it.hasNext();) {
				l.add(it.next().getRow());
			}
		} catch (StorageException e) {
			logger.error("failed to access storage: {}", e.getMessage());
			// if the table doesn't exist, then wait to populate later via
			// setStorageSource()
		}
		return l;
	}

	@Override
	public void addUserEntry(WifiOffloadUserEntry entry) {
         logger.info("ADD USER ENTRY");
         //Increase Number Of Mobile Users
         this.controller.numMobileUsers++;
		// generate random userId for each newly created rule
		// may want to return to caller if useful
		// may want to check conflict
		entry.userId = entry.genID();

		int i = 0;
		// locate the position of the new rule in the sorted arraylist
		for (i = 0; i < this.entries.size(); i++) {
			if (this.entries.get(i).priority >= entry.priority)
				break;
		}
		// now, add rule to the list
		if (i <= this.entries.size()) {
			this.entries.add(i, entry);
		} else {
			this.entries.add(entry);
		}
		// add rule to database
		Map<String, Object> entryNew = new HashMap<String, Object>();
		entryNew.put(COLUMN_USERID, Long.toString(entry.userId));
		entryNew.put(COLUMN_DPID, Long.toString(entry.dpId.getLong()));
		entryNew.put(COLUMN_PORTIN, Integer.toString(entry.portIn.getPortNumber()));
		entryNew.put(COLUMN_USERMACADDR, Long.toString(entry.userMacAddress.getLong()));
		entryNew.put(COLUMN_USERIPADDR, Integer.toString(entry.userIpAddress.getInt()));
		entryNew.put(COLUMN_AREAID, Long.toString(entry.areaId));
		entryNew.put(COLUMN_CONID, Long.toString(entry.sdnConId));
		storageSource.insertRow(TABLE_NAME, entryNew);
	}

	@Override
	public void deleteUserEntry(long userId) {
		
		WiFiOffloadPerformanceMonitor.flushRecord();
		WiFiOffloadPerformanceMonitor.timeStamp = System.currentTimeMillis();
		WiFiOffloadPerformanceMonitor.isRestOperation = true;
		
		
		long startTime= System.nanoTime();
		Iterator<WifiOffloadUserEntry> iter = this.entries.iterator();
		while (iter.hasNext()) {
			WifiOffloadUserEntry r = iter.next();
			if (r.userId == userId) {
				// found the rule, now remove it
				iter.remove();
				
				//Decrease Number Of Mobile Users
				this.controller.numMobileUsers--;
				break;
			}
		}
		// delete from database
		storageSource.deleteRow(TABLE_NAME, Long.toString(userId));
		
		long endTime = System.nanoTime();
        double timeDiff= ((double)(endTime-startTime))/1000000000;
        WiFiOffloadPerformanceMonitor.userRemovingTimeEn = true;
        WiFiOffloadPerformanceMonitor.userRemovingTime = timeDiff;
        WiFiOffloadPerformanceMonitor.numMobileUsers = controller.numMobileUsers;
        WiFiOffloadPerformanceMonitor.userMacAddress = MacAddress.of(userId);
        WiFiOffloadPerformanceMonitor.printRecord();
		
	}
	
	
	public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision, FloodlightContext cntx) {
		logger.info("PROCESS-PACKET-IN");
		
		WiFiOffloadPerformanceMonitor.flushRecord();
		WiFiOffloadPerformanceMonitor.timeStamp = System.currentTimeMillis();
		WiFiOffloadPerformanceMonitor.isCoreOperation = true;
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		OFPort portIn = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		
		MacAddress userMacAddress = eth.getSourceMACAddress();
		
		WifiOffloadUserEntry entry= new WifiOffloadUserEntry();
		entry.userId = 0;
		
		entry.portIn =portIn;
		entry.userMacAddress = userMacAddress;		
		entry.dpId = sw.getId();
		entry.userIpAddress = IPv4Address.of("0.0.0.0");
		entry.areaId = 0;
		entry.sdnConId = 0;
		
		logger.info("PACKET-In REASON: "+pi.getReason().toString());
		
       if(eth.getEtherType() == EthType.IPv4){
        	IPv4 ipv4 = (IPv4) eth.getPayload();
        	entry.userIpAddress = ipv4.getSourceAddress();
        }
        else if (eth.getEtherType() == EthType.ARP){
        	ARP arp = (ARP)eth.getPayload();        	
        }
        else{
        	logger.info("Packet is neither ARP nor IPV4");
        }	
        
        entry.userId = entry.genID();
        
        WiFiOffloadPerformanceMonitor.ipAddress = controllers.getLocalController().ipAddress;
        WiFiOffloadPerformanceMonitor.userMacAddress = entry.userMacAddress;
        
        long startTime = System.nanoTime();
        boolean isUserExist= checkUserEntryExists(entry, entries);
        long endTime = System.nanoTime();
        double timeDiff= ((double)(endTime-startTime))/1000000000;
        logger.info("Time Taken For Search User "+entry.userMacAddress.toString()+" is "+timeDiff);
        WiFiOffloadPerformanceMonitor.userLocalSearchTimeEn = true;
        WiFiOffloadPerformanceMonitor.userLocalSearchTime = timeDiff;
        
        if(isUserExist){
        	//Check User within this SDN Controller
        	logger.info("User "+entry.userMacAddress.toString()+" is already exist in this controller");
        }else{
        	
        	logger.info("Searching User in Other SDN Controller");
        	
        	 startTime = System.nanoTime();
        	 WifiOffloadUserEntry.userBlocked = false;
        	 WifiOffloadUserEntry remoteEntry = controllers.searchUserInNetwork(controllers, entry);        	
        	//Timing
        	 endTime = System.nanoTime();
             timeDiff= ((double)(endTime-startTime))/1000000000;
             WiFiOffloadPerformanceMonitor.userNetworkSearchTimeEn = true;
             WiFiOffloadPerformanceMonitor.userNetworkSearchTime = timeDiff;
        	
        	if(remoteEntry != null){
        		remoteEntry.sdnConId = this.controller.getId();
        		remoteEntry.areaId   = this.controller.getAreadId();
        		
        		startTime = System.nanoTime();
        		addUserEntry(remoteEntry);
        		endTime = System.nanoTime();
                timeDiff= ((double)(endTime-startTime))/1000000000;
                WiFiOffloadPerformanceMonitor.userAddingTimeEn = true;
                WiFiOffloadPerformanceMonitor.userAddingTime = timeDiff;
        		
        	}
        	/*if(WifiOffloadSDNControllers.checkForControllerEnable()){
        		
        		if(WifiOffloadSDNControllers.checkForUserInOtherControllers(entry)){
        			   
        			addUserEntry(WifiOffloadSDNControllers.getUserFromRemoteController(entry));
        			logger.info("User Found In The remote controller : Adding User Entry: "+entry.userMacAddress.toString());

        			
        		}else{
        			addUserEntry(entry);
        			logger.info("User Cannot Be Found In The remote controller : Adding User Entry: "+entry.userMacAddress.toString());
        		}
        	}*/
        	else{
        		
        		if(!WifiOffloadUserEntry.userBlocked){
    		     	addUserEntry(entry);
        		}
        		//Future Update with / may be want to store for better mobility
    			logger.info("User Entry is neither exist nor added to data base : reason may be user exist in a controller in the same area with high priority");
        	}
        }
        
        WiFiOffloadPerformanceMonitor.numMobileUsers = controller.numMobileUsers;
        WiFiOffloadPerformanceMonitor.printRecord();
		return Command.CONTINUE;
	}
	
		public static boolean checkUserEntryExists(WifiOffloadUserEntry entry, List<WifiOffloadUserEntry> entries) {
		Iterator<WifiOffloadUserEntry> iter = entries.iterator();
		while (iter.hasNext()) {
		
			WifiOffloadUserEntry r = iter.next();
			//logger.info("MAC-ADDRESS: "+ r.userMacAddress.toString());
			// check if we find a similar rule
			if (entry.isSameAs(r)) {
				return true;
			}
		}

		// no rule matched, so it doesn't exist in the rules
		return false;
	}

		@Override
		public WifiOffloadSDNController getLocalController() {
			// TODO Auto-generated method stub
			return this.controller;
		}

		@Override
		public void setLocalController(WifiOffloadSDNController controller) {
			// TODO Auto-generated method stub
			this.controller = controller;
			
		}

		@Override
		public void addController(WifiOffloadSDNController controller) {
			// TODO Auto-generated method stub
			this.controllers.addController(controller);
		}

		@Override
		public List<WifiOffloadSDNController> getControllers() {
			// TODO Auto-generated method stub
			return this.controllers.getControllersList();
		}	
	

}
