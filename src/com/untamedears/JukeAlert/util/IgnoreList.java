package com.untamedears.JukeAlert.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IgnoreList {
	
	
	private IgnoreList() { }

	private static final Object lockObject_ = new Object();
	private static Map<String, Set<String>> ignoresByPlayer_ = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> ignoresByGroup_ = new HashMap<String, Set<String>>();
	private static Map<String, Boolean> playerIgnoreAlls_ = new HashMap<String, Boolean>();

	// Flip the ignore/unignore bit for a player-group combination.
	// Requires quick access with lookup by player.
	// Returns true if it adds to ignore list.
	public static Boolean toggleIgnore(String playerName, String groupName) {
		
		Boolean returnValue = false;
		playerName = playerName.toLowerCase();
		groupName = groupName.toLowerCase();
		
		synchronized(lockObject_) {

			// If the player has no ignores, this is obviously an add and will have
			//	an ignore after this call is over.  Add a list for us to append to.
			if (!ignoresByPlayer_.containsKey(playerName)) {
				ignoresByPlayer_.put(playerName, new HashSet<String>());
			}
			
			// If the group has no ignores, this is obviously an add and will have
			//	an ignore after this call is over.  Add a list for us to append to.
			if (!ignoresByGroup_.containsKey(groupName)) {
				ignoresByGroup_.put(groupName, new HashSet<String>());
			}
			
			Set<String> playerIgnores = ignoresByPlayer_.get(playerName);
			
			// If the player has this group name ignored:
			//	Remove it from the player reference.
			// 	Remove the player from the group reference.
			//	If possible, remove the group from the group reference.
			//	If possible, remove the player from the player reference.
			if (playerIgnores.contains(groupName)) {
				
				// Remove from player reference.
				playerIgnores.remove(groupName);
				
				// Remove the player from the group reference.
				ignoresByGroup_.get(groupName).remove(playerName);
				
				// Remove group if we can.
				if (ignoresByGroup_.get(groupName).size() == 0) {
					ignoresByGroup_.remove(groupName);
				}
				
				// Remove player if we can.
				if (playerIgnores.size() == 0) {
					ignoresByPlayer_.remove(playerName);
				}
				
			} 
			// Else if it does not contain the group name, lets add it in.
			else {
				playerIgnores.add(groupName);
				ignoresByGroup_.get(groupName).add(playerName);
				returnValue = true;
			}
			
		}
		
		return returnValue;
	}
	
	// Obtain list of all player ignores for a given group.
	// Requires quick accesss by group name.
	// !!IMPORTANT!!:  Returns null if there exists no player ignores for a given group.
	// !!IMPORTANT!!:  This will return a reference value for a set for this group for speed reasons.
	//	Folks accessing this method would be well advised to keep this in mind.	
	public static Set<String> GetPlayerIgnoreListByGroup(String groupName) {
		
		groupName = groupName.toLowerCase();
		Set<String> returnValue = null;
		
		synchronized(lockObject_) {
			if(ignoresByGroup_.containsKey(groupName)) {
				returnValue = ignoresByGroup_.get(groupName);
			}
		}
		
		return returnValue;
	}
	
	// Obtain list of all group ignores for a playername.
	// Requires quick accesss by player name
	// !!IMPORTANT!!:  Returns null if there exists no group ignores for a given player.
	// !!IMPORTANT!!:  This will return a reference value for a set for this group for speed reasons.
	//	Folks accessing this method would be well advised to keep this in mind.	
	public static Set<String> GetGroupIgnoreListByPlayer(String playerName) {
		
		playerName = playerName.toLowerCase();
		
		Set<String> returnValue = null;
		
		synchronized(lockObject_) {
			if(ignoresByPlayer_.containsKey(playerName)) {
				returnValue = ignoresByPlayer_.get(playerName);
			}
		}
		
		return returnValue;
	}

    // Toggle the global ignore flag for a specific player. If the player isn't
    // noted in the map, enable the ignore bit.
    public static boolean toggleIgnoreAll(String playerName) {
        boolean newState;
        playerName = playerName.toLowerCase();
        synchronized(playerIgnoreAlls_) {
            if (!playerIgnoreAlls_.containsKey(playerName)) {
                newState = true;
            } else {
                newState = !playerIgnoreAlls_.get(playerName);
            }
            playerIgnoreAlls_.put(playerName, newState);
        }
        return newState;
    }

    public static boolean doesPlayerIgnoreAll(String playerName) {
        playerName = playerName.toLowerCase();
        synchronized(playerIgnoreAlls_) {
            if (!playerIgnoreAlls_.containsKey(playerName)) {
                return false;
            }
            return playerIgnoreAlls_.get(playerName);
        }
    }
}
