package TestPlayer;

import java.util.Random;

import battlecode.common.*;

public class SoldierBehavior {
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	static int[][] map;
	static int width,height;
	static MapLocation target;
	private enum SoldierState{
		SPAWN,WAITING_FOR_COMMAND,BUILD_PASTR;
	}
	
	public static SoldierState state = SoldierState.SPAWN;
	public static int iD;
	
	public static void run(RobotController rc) throws Exception{
		if(!rc.isActive()){
			return;
		}
		
		if(state == SoldierState.SPAWN){
			moveRandomly(rc);	
			if(rc.readBroadcast(10000)==1){
				updateInternalMap(rc);
				iD = rc.getRobot().getID();
				state = SoldierState.WAITING_FOR_COMMAND;
			}
		}else if(state == SoldierState.WAITING_FOR_COMMAND){
			moveRandomly(rc);
			int command = rc.readBroadcast(11000+(iD%4));
			if(command / 10000 == 1){
				target = StaticFunctions.intToLoc(command);
//				System.out.println("moveTo: " + target);
				state = SoldierState.BUILD_PASTR;
			}
		}else if(state == SoldierState.BUILD_PASTR){
			tryToShoot(rc);
			if(rc.getLocation().equals(target) && rc.isActive()){
				rc.construct(RobotType.PASTR);
			}
			if(!moveToLoc(target,rc)){
				moveRandomly(rc);
			}
		}
	}
	
	public static void moveRandomly(RobotController rc) throws Exception{
		Direction moveDirection = directions[(int) (Math.random()*8)];
		if (rc.isActive() && rc.canMove(moveDirection)) {
			rc.sneak(moveDirection);
		}
	}
	public static void moveToEnemyHQ(RobotController rc) throws Exception{
		Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		if (rc.isActive() && rc.canMove(toEnemy)) {
			rc.sneak(toEnemy);
		}
	}
	public static boolean moveToLoc(MapLocation loc, RobotController rc) throws Exception{
		Direction toDest = rc.getLocation().directionTo(loc);
		if (rc.isActive() && rc.canMove(toDest)) {
			rc.sneak(toDest);
			return true;
		}else{
			return false;
		}
	}
	public static void updateInternalMap(RobotController rc) throws Exception{
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		map = new int[width][height];
		int index = 0;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				map[x][y] = rc.readBroadcast(index);
				index++;
			}
		}
	}
	private static void tryToShoot(RobotController rc) throws Exception {
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam().opponent());
		if(enemyRobots.length > 0){
			for(int i = 0; i < enemyRobots.length; i ++){
				Robot current = enemyRobots[i];
				RobotInfo anEnemyInfo;
				anEnemyInfo = rc.senseRobotInfo(current);
				if(anEnemyInfo.type != RobotType.HQ && anEnemyInfo.location.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){
					if(rc.isActive()){
						rc.attackSquare(anEnemyInfo.location);
					}
				}
			}
		}
	}
}
