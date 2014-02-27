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

public class ObstacleDetection {
    private final int WOOD = 0;
    private final int SMURF = 1;

    private Robot robot;
    public Navigation nav;

    private final int DISTANCE_THRESHOLD = 15;
    private final double COLOR_THRESHOLD = 0.05;
    private int counter = 0, filterCount = 0, blueCount = 0, redCount = 0;
    private int distance, id;
    public ColorSensor color = new ColorSensor(SensorPort. S2);

    public ObstacleDetection(Robot robot) {
        this.robot = robot;
    }

    public int search() {
        robot.sonic.continuous();
            
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
                return 0;
            }
        }

        // NOTIFY OF OBJECT
        Sound.systemSound(true, 2);
        LCD.drawString("OBJECT AHEAD", 0, 0);

        return 1;

    }

    public int detect() {

        // COMPARE RED AND BLUE COLOR VALUES
        // FROM COLOR SENSOR TO DISTINGUISH
        // BETWEEN STYROFOAM BLOCK AND WOODEN
        // BLOCK
        // WOODEN BLOCK IS SIGNIFICANTLY
        // MORE RED
        
        // robot.color.setFloodlight(true); 
        
        while (blueCount <= 100 && redCount <= 100) {
            Color cl =  color.getRawColor();
            if (Math.abs(cl.getBlue() - cl.getRed())/(double)cl.getRed() < COLOR_THRESHOLD) {
                blueCount++;
                redCount = 0;
                id = SMURF;
            } else {
                redCount++;
                blueCount = 0;
                id = WOOD;             
            }
        }

        // robot.color.setFloodlight(false);

        // NOTIFY OF OBJECT'S NATURE
        Sound.systemSound(false, 3);
        LCD.drawString(id == SMURF ? "SMURF" : "WOOD", 0, 1);

        return id;
        
        // if (id == SMURF) {
        //     nav.goForward(-7);
        //     nav.turn(Math.PI);
        //     nav.goForward(-5);
        //     robot.grab();
        //     nav.found = true;
        //     break;
        // } else {
        //     nav.goForward(-5);
        //     nav.turn(Math.PI / 2);

        //     nav.goForward(30);
        //     LCD.drawString("    ", 0, 1);
        // }

        // nav.travelTo(60.96, 182.88);
        // nav.goForward(30);

        // nav.turnTo(5 * Math.PI / 4);
        // robot.letGo();
    }
}