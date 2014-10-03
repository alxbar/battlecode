package TestPlayer;

import battlecode.common.*;

public class HQBehavior {
	
	private enum HQState{
		INIT,DEFAULT;
	}
	
	public static MapLocation[] assemblyPositions;
	public static HQState state = HQState.INIT;
	
	public static void hqBehavior(RobotController rc) throws Exception{
	
		if (!rc.isActive()) {
			return;
		}
		switch (state) {
		case INIT:
			MapMaker.makeMap(rc);
			MapMaker.broadcastMap(rc);
			rc.broadcast(10000, 1);
			assemblyPositions = MapMaker.buildImportanceMap(4,rc);
//			MapMaker.displayMap();
			sendBuildPastrCommand(assemblyPositions[0],11000,rc);
			sendBuildPastrCommand(assemblyPositions[1],11001,rc);
			sendBuildPastrCommand(assemblyPositions[2],11002,rc);
			sendBuildPastrCommand(assemblyPositions[3],11003,rc);
			state = HQState.DEFAULT;
			break;
		case DEFAULT:
			int count = rc.senseRobotCount();
			if (count < 20) {
				Direction toEnemy = rc.getLocation().directionTo(
						rc.senseEnemyHQLocation());
				if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {
					rc.spawn(toEnemy);
				}
			}
			break;
		}
	}
	
	public static void sendBuildPastrCommand(MapLocation loc, int channelID, RobotController rc) throws Exception{
		int broadcast = StaticFunctions.locToInt(loc) + 10000;
		rc.broadcast(channelID, broadcast);
	}
}
