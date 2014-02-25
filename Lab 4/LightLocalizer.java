/* 
 * LightLocalizer.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.util.*;

public class LightLocalizer {
	private Odometer odometer;
	private ColorSensor sensor;
	public static float ROTATE_SPEED = 50, DISTANCE = 12.1f; // speed of rotation and distance between sensor and center
	private final int UPPER_THRESHOLD = 420, LOWER_THRESHOLD = 300; // thresholds for line detection
	private int lineCount = 0; // number of lines encountered
	private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C;
	private double[] angles = new double[4]; // array to store orientations
	
	public LightLocalizer(Odometer odometer, ColorSensor sensor) {
		this.odometer = odometer;
		this.sensor = sensor;
		
		// turn on the light
		sensor.setFloodlight(true);
	}
	
	public void doLocalization() {
		// turn 45 degrees and move 15 cm forward
		Lab4.nav.turnTo(0.7854);
		Lab4.nav.goForward(15);

		// set new origin
		odometer.setX(0);
		odometer.setY(0);
		
		// start rotating clockwise
		leftMotor.setSpeed(ROTATE_SPEED);
        rightMotor.setSpeed(ROTATE_SPEED);
	    leftMotor.forward();
        rightMotor.backward();

        // count lines crossed and store the orientation at each line
        // using the colorsensor
		while (lineCount < 4) {
			int colorIntensity = sensor.getNormalizedLightValue();
			if (colorIntensity < UPPER_THRESHOLD && colorIntensity > LOWER_THRESHOLD) {
				angles[lineCount++] = odometer.getTheta();

				// play sound and avoid counting same line several times
            	Sound.playTone(2000,100);
                Delay.msDelay(2000);
			}
		}

		// stop motors
		leftMotor.stop();
        rightMotor.stop();

        // triangulate correct x, y and theta differences
        // using formulas from the tutorial slides 
        double negativeYTheta = angles[3];
        double deltaYTheta = angles[3] - angles[1];
        double deltaXTheta = angles[2] - angles[0];

        double deltaX = -DISTANCE * Math.cos(Math.toRadians(deltaYTheta) / 2);
        double deltaY = -DISTANCE * Math.cos(Math.toRadians(deltaXTheta) / 2);
        double deltaTheta = 270 - negativeYTheta + deltaYTheta / 2;

        // correct coordinates and orientation
        odometer.setTheta(odometer.getTheta() + deltaTheta);
        odometer.setX(odometer.getX() + deltaX);
        odometer.setY(odometer.getY() + deltaY);

        // travel to real origin and face north
        Lab4.nav.travelTo(0,0);
        Lab4.nav.turnTo(0);
	}

}
