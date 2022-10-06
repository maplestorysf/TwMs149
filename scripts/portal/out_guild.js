function enter(pi) {
	var returnMap = pi.getSavedLocation("GUILD"); 
 	if (returnMap < 0) { 
		returnMap = 100000000; // to fix people who entered the fm trough an unconventional way 
 	}
 	//pi.playPortalSE(); 
 	pi.clearSavedLocation("GUILD"); 
 	pi.warp(returnMap, 0); 
}