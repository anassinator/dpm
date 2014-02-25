/* 
 * ObstacleDetection.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.util.*;
import lejos.robotics.*;
import java.util.*;

public class ObstacleDetection extends Thread {
    private static final int WOOD = 0;
    private static final int SMURF = 1;

    private static Robot robot;
    private static Navigation nav;

    private static final int DISTANCE_THRESHOLD = 15;
    private static final double COLOR_THRESHOLD = 0.05;
    private static int counter = 0, filterCount = 0, blueCount = 0, redCount = 0;
    private static int distance, id;

    private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

    public ObstacleDetection(Robot robot, Navigation nav) {
        this.robot = robot;
        this.nav = nav;
    }

    public void run() {
        robot.sonic.continuous();

        // FOREVER
        while (true) {
            LCD.drawString("SEARCHING...", 0, 0);

            // CLEAR COUNTERS
            blueCount = 0;
            redCount = 0;
            filterCount = 0;

            // MEASURE DISTANCE FROM OBJECT
            // AND MAKE SURE WITHIN 15 CM
            // OTHERWISE DRIVE FORWARD UNTIL
            // OBJECT IS DETECTED
            while (filterCount <= 10) {
                if (robot.sonic.getDistance() < DISTANCE_THRESHOLD) {                    
                    filterCount++;
                }
                else {
                    filterCount = 0;
                }
            }

            nav.togglePause(true);

            // NOTIFY OF OBJECT
            Sound.systemSound(true, 2);
            LCD.drawString("OBJECT AHEAD", 0, 0);

            distance = robot.sonic.getDistance();

            // COMPARE RED AND BLUE COLOR VALUES
            // FROM COLOR SENSOR TO DISTINGUISH
            // BETWEEN STYROFOAM BLOCK AND WOODEN
            // BLOCK
            // WOODEN BLOCK IS SIGNIFICANTLY
            // MORE RED
            while (blueCount <= 100 && redCount <= 100) {
                Color cl = robot.color.getRawColor();
                if (Math.abs(cl.getBlue() - cl.getRed())/(double)cl.getRed() < COLOR_THRESHOLD) {
                    blueCount++;
                    redCount = 0;
                    id = SMURF;
                } else {
                    redCount++;
                    blueCount = 0;
                    id = WOOD;             
                }

                if (counter++ > 200) {
                    nav.goForward(1);
                    distance = robot.sonic.getDistance();
                    counter = 0;
                }
            }

            obstacles.add(new Obstacle(id, distance, robot.odometer));

            // NOTIFY OF OBJECT'S NATURE
            Sound.systemSound(false, 3);
            LCD.drawString(id == SMURF ? "SMURF" : "WOOD", 0, 1);

            nav.togglePause(false);
            
            if (id == SMURF) {
                nav.goForward(-5);
                nav.turn(Math.PI);
                robot.grab();
                nav.found = true;
                break;
            } else {
                nav.goForward(-5);
                if (robot.odometer.getX() > 30.94)
                    nav.turn(Math.PI / 2);
                else
                    nav.turn(-Math.PI / 2);
                nav.goForward(30);
                LCD.drawString("    ", 0, 1);
            }
        }

        nav.travelTo(60.96, 182.88);
        nav.turnTo(-Math.PI / 4);
        robot.letGo();
    }
}