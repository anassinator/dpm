/* 
 * Display.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.robotics.Color;
import lejos.util.*;

public class Display extends Thread {
    private static final long DISPLAY_PERIOD = 20;
    private Odometer odometer;

    // constructor
    public Display(Odometer odometer) {
        this.odometer = odometer;
    }

    // run method (required for Thread)
    public void run() {
        long displayStart, displayEnd;
        double[] position = new double[3];

        while (true) {
            displayStart = System.currentTimeMillis();

            // clear the lines for displaying odometry information
            LCD.drawString("X: ", 0, 3);
            LCD.drawString("Y: ", 0, 4);
            LCD.drawString("T: ", 0, 5);

            // get the odometry information
            odometer.getPosition(position, new boolean[] { true, true, true });

            // display odometry information
            for (int i = 3; i < 6; i++) {
                LCD.drawString(String.valueOf(position[i - 3]), 3, i);
            }
            
            // throttle the OdometryDisplay
            displayEnd = System.currentTimeMillis();
            if (displayEnd - displayStart < DISPLAY_PERIOD) {
                try {
                    Thread.sleep(DISPLAY_PERIOD - (displayEnd - displayStart));
                } catch (InterruptedException e) {
                    // there is nothing to be done here because it is not
                    // expected that OdometryDisplay will be interrupted
                    // by another thread
                }
            }
        }
    }

}
