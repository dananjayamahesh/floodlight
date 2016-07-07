package net.floodlightcontroller.wifioffload;

import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.IFloodlightService;


public interface IWifiOffloadService extends IFloodlightService {

    /**
     * Enables/disables the wifi-offload.
     * @param enable Whether to enable or disable the wifi-offload.
     */
    public void enableWifiOffload(boolean enable);

    /**
     * Returns operational status of the wifi-offload
     * @return boolean enabled;
     */
    public boolean isEnabled();
 
    /**
     * Returns all of the wifi-offload rules
     * @return List of all rules
     */
    public List<WifiOffloadUserEntry> getUserEntries();
    
    /**
     * Returns the user ID
     * @return user ID
     */
    public String getUserId();
    
    /**
     * Sets the user Id
     * @param newMask The new user ID
     */
    public void setUserId(long userId);

    /**
     * Returns all of the wifi-offload entries in storage
     * for debugging and unit-testing purposes
     * @return List of all rules in storage
     */
    public String getConName();
    public void setConName(String conName);
    
    public List<Map<String, Object>> getStorageUserEntries();

    /**
     * Adds a new wifi-offload user entry
     */
    public void addUserEntry(WifiOffloadUserEntry entry);

    /**
     * Deletes a wifi-offload user entry
     */
    public void deleteUserEntry(long userId);
	
	
}
