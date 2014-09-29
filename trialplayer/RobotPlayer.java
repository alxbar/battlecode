package trialplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static Random rand;

	public static void run(RobotController rc) {
		rand = new Random();
		Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

		while(true) {
			if (rc.getType() == RobotType.HQ) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.isActive() && rc.senseRobotCount() <= 25) {
						Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {
							rc.spawn(toEnemy);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
				}
			}

			if (rc.getType() == RobotType.SOLDIER) {
				
				
				if(Math.random() < 0.05){
				try {
					rc.construct(RobotType.PASTR);
				} catch (Exception e) {
					System.out.println("Soldier Exception");
				}
				} else {
					Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
					Direction toHome = rc.getLocation().directionTo(rc.senseHQLocation());
					Direction there;
					if (toHome.compareTo(toEnemy) < 0){
						if (Math.random() < 0.25){
							there = Direction.EAST;
						} else if (Math.random() > 0.75) {
							there = Direction.WEST;
						} else {
							there = toHome;
						}
						
					}
					else if(toHome.compareTo(toEnemy) > -15 && toHome.compareTo(toEnemy) <= 0){
						if (Math.random() < 0.5){
							there = Direction.EAST;
						} else {
							there = Direction.WEST;
						}
						
					}
					else {
						there = toEnemy;
					}
					
					if (rc.canMove(there)) {
						try {
							rc.move(there);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						if (Math.random() < 0.5){
							there = directions[rand.nextInt(8)];
						} else {
							there = directions[rand.nextInt(8)];
						}
						try {
							rc.move(there);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			rc.yield();
		}
	}
}