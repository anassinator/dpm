/* 
 * OdometryCorrection.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.util.*;

public class OdometryCorrection extends Thread {
    private static final long CORRECTION_PERIOD = 10;
    private Odometer odometer;
    private ColorSensor sensor = new ColorSensor(SensorPort.S1, 7);
    public int lineCount = 0, turnCount = 0;
    private double prevPosition = 0.0;
    private final double DISTANCE_BETWEEN_LINES = 30.48;
    private final double DISTANCE_BETWEEN_FIRST_LINE_AND_CENTER = 15.24;
    private final double DISTANCE_BETWEEN_SENSOR_AND_CENTER = 7.00;
    private final int UPPER_THRESHOLD = 420, LOWER_THRESHOLD = 300;

    // constructor
    public OdometryCorrection(Odometer odometer) {
        this.odometer = odometer;
        Sound.setVolume(100);
        sensor.setFloodlight(true);
    }

    // run method (required for Thread)
    public void run() {
        long correctionStart, correctionEnd;

        while (true) {
            correctionStart = System.currentTimeMillis();

            // GET COLOR INTENSITY AND DRAW ON SCREEN
            int colorIntensity = sensor.getNormalizedLightValue();
            LCD.drawString("Color: " + String.valueOf(colorIntensity), 0, 6);

            // DETECT LINE ONLY WHILE NOT TURNING
            // UPPER THRESHOLD IS TO DISTINGUISH LINE FROM WOOD
            // LOWER THRESHOLD IS TO IGNORE INTENSITY READINGS WHEN ROBOT IS PICKED UP
            if (!SquareDriver.turning && colorIntensity < UPPER_THRESHOLD && colorIntensity > LOWER_THRESHOLD) {
                // BEEP AT LINE DETECTION
                Sound.playTone(3000,100);

                // INCREMENT AND PRINT LINE COUNT
                lineCount++;
                LCD.drawString("Lines: " + String.valueOf(lineCount), 0, 4);

                /* CHECK NUMBER OF TURNS TO KNOW WHETHER TO CHANGE X OR Y */

                // CHANGE X ON FIRST AND THIRD TURN
                if (turnCount == 1 || turnCount == 3) {
                    // IF FIRST LINE AFTER FIRST TURN THEN SET THE X VALUE TO PROJECTION OF ITS POSITION
                    // ON THE X AXIS WITH ORIGIN SET TO THE CENTER OF THE FIRST TILE
                    if (lineCount == 1 && turnCount == 1)
                        odometer.setX(DISTANCE_BETWEEN_FIRST_LINE_AND_CENTER - DISTANCE_BETWEEN_SENSOR_AND_CENTER);

                    /*
                     * FOLLOWING OPTIMIZED TO IMPROVE ACCURACY OF 3 X 3 SQUARE TRAJECTORY
                     * TURNCOUNT AND LINECOUNTS CONDITIONS SHOULD BE ALTERED SLIGHTLY FOR
                     * DIFFERENT SHAPES. IT COULD ALSO SIMPLY BE REMOVED
                     */

                    // STORE X POSITION ONCE REACHED FIRST LINE
                    if (lineCount == 1)
                        prevPosition = odometer.getX();
                    
                    // CORRECT X POSITION ONCE REACHED SECOND LINE BY KNOWN DISTANCE BETWEEN LINES
                    else if (lineCount == 2) {
                        // IF FIRST TURN THEN SUBTRACT KNOWN DISTANCE
                        if (turnCount == 1)
                            odometer.setX(prevPosition + DISTANCE_BETWEEN_LINES);
                        // OTHERWISE ADD
                        else
                            odometer.setX(prevPosition - DISTANCE_BETWEEN_LINES);
                    }
                }

                // CHANGE Y POSITION ON ZEROETH AND SECOND TURN
                else {
                    // IF FIRST LINE BEFORE ANY TURN THEN SET THE Y VALUE TO PROJECTION OF ITS POSITION
                    // ON THE Y AXIS WITH ORIGIN SET TO THE CENTER OF THE FIRST TILE
                    if (lineCount == 1 && turnCount == 0)
                        odometer.setY(DISTANCE_BETWEEN_FIRST_LINE_AND_CENTER - DISTANCE_BETWEEN_SENSOR_AND_CENTER);

                    /*
                     * FOLLOWING OPTIMIZED TO IMPROVE ACCURACY OF 3 X 3 SQUARE TRAJECTORY
                     * TURNCOUNT AND LINECOUNTS CONDITIONS SHOULD BE ALTERED SLIGHTLY FOR
                     * DIFFERENT SHAPES. IT COULD ALSO SIMPLY BE REMOVED
                     */
                    
                    // STORE Y POSITION ON CE REACHED FIRST LINE
                    if (lineCount == 1)
                        prevPosition = odometer.getY();

                    // CORRECT Y POSITION ONCE REACHED SECOND LINE BY KNOWN DISTANCE BETWEEN LINES
                    else if (lineCount == 2) {
                        // IF SECOND TURN THEN SUBTRACT KNOWN DISTANCE
                        if (turnCount == 2)
                            odometer.setY(prevPosition - DISTANCE_BETWEEN_LINES);
                        // OTHERWISE ADD
                        else
                            odometer.setY(prevPosition + DISTANCE_BETWEEN_LINES);
                    }
                }

                // DELAY TO AVOID COUNTING SAME LINE SEVERAL TIMES
                // 1000 MILLISECONDS OBTAINED EXPERIMENTALLY
                Delay.msDelay(1000);
            }
            // KEEP TRACK OF TURNS
            else if (SquareDriver.turning) {
                // RESET LINE COUNTER
                lineCount = 0;

                // INCREMENT AND PRINT TURN COUNT
                turnCount++;
                LCD.drawString("Turns: " + String.valueOf(turnCount), 0, 5);

                // DELAY TO AVOID COUNTING SAME TURN SEVERAL TIMES
                // 2500 MILLISECONDS OBTAINED EXPERIMENTALLY
                Delay.msDelay(2500);
            }

            // this ensure the odometry correction occurs only once every period
            correctionEnd = System.currentTimeMillis();
            if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
                try {
                    Thread.sleep(CORRECTION_PERIOD
                            - (correctionEnd - correctionStart));
                } catch (InterruptedException e) {
                    // there is nothing to be done here because it is not
                    // expected that the odometry correction will be
                    // interrupted by another thread
                }
            }
        }
    }
}