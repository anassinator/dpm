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
    private final double COLOR_THRESHOLD = 15;
    private int counter = 0, filterCount = 0, blueCount = 0, redCount = 0;
    private int distance, id;

    public ColorSensor color = new ColorSensor(SensorPort.S2);

    public ObstacleDetection(Robot robot) {
        this.robot = robot;
    }

    public int search() {
        // set up ultra sonic sensor
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
        while (filterCount <= 3) {
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

        // return obstacle detected
        return 1;
    }

    public int detect() {

        // COMPARE RED AND BLUE COLOR VALUES
        // FROM COLOR SENSOR TO DISTINGUISH
        // BETWEEN STYROFOAM BLOCK AND WOODEN
        // BLOCK
        // WOODEN BLOCK IS SIGNIFICANTLY
        // MORE RED
        
        // prepare color sensor
        color.setFloodlight(false); 
        
        while (blueCount <= 2 && redCount <= 2) {
            Color cl = color.getRawColor();
            if (cl.getRed() - cl.getBlue() > COLOR_THRESHOLD) {
                // check if wood
                redCount++;
                blueCount = 0;
                id = WOOD;
            } else {
                // check if sturofoam
                blueCount++;
                redCount = 0;
                id = SMURF;             
            }
        }

        // NOTIFY OF OBJECT'S NATURE
        Sound.systemSound(false, 3);
        LCD.drawString(id == SMURF ? "SMURF" : "WOOD", 0, 1);

        // return obstacle id
        return id;
    }
}