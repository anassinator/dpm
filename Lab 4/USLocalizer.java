/* 
 * USLocalizer.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.*;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static float ROTATE_SPEED = 50;
	public final double leftRadius = 2.16;              // RADIUS OF LEFT WHEEL
    public final double rightRadius = 2.14;             // RADIUS OF RIGHT WHEEL
    public final double width = 16.1;                   // DISTANCE BETWEEN WHEELS
    private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C;
    private double firstAngle, secondAngle, deltaTheta;

	public Odometer odometer;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private int counterLarge = 0, counterSmall = 0, distance = 0;
	public int tempDistance = 0;
	
	public USLocalizer(Odometer odometer, UltrasonicSensor us, LocalizationType locType) {
		this.odometer = odometer;
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double[] pos = new double [3];
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// set flag
			Lab4.usRunning = true;

			// rotate the robot until it sees no wall
			while (getFilteredData() < 50) {
				leftMotor.setSpeed(ROTATE_SPEED);
	            rightMotor.setSpeed(ROTATE_SPEED);
	            leftMotor.forward();
	            rightMotor.backward();
	        }

	        // play sound
            Sound.playTone(3000,100);

			// keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() > 30);
	        firstAngle = odometer.getTheta();

	        // play lower frequency sound
            Sound.playTone(2000,100);

            // stop motors
	        leftMotor.stop();
	        rightMotor.stop();

			// switch direction and wait until it sees no wall
			while (getFilteredData() < 50) {
				leftMotor.setSpeed(ROTATE_SPEED);
	            rightMotor.setSpeed(ROTATE_SPEED);
	            leftMotor.backward();
	            rightMotor.forward();
	        }

	        // play higher frequency sound
            Sound.playTone(3000,100);

			// keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() > 30);
	        secondAngle = odometer.getTheta();

	        // play lower frequency sound
            Sound.playTone(2000,100);

            // stop motors
	        leftMotor.stop();
	        rightMotor.stop();

	        // reset flag
			Lab4.usRunning = false;

			// measure orientation
			// no if statement is necessary due to usRunning flag
			// which stops the odometer from fixing the angle between
			// 0 and 2 pi
			deltaTheta = (secondAngle + firstAngle) / 2 - 32;

			// update the odometer position
			odometer.setTheta(secondAngle - deltaTheta);
		} else {
			// set flag
			Lab4.usRunning = true;

			// rotate the robot until it sees no wall
			leftMotor.setSpeed(ROTATE_SPEED);
            rightMotor.setSpeed(ROTATE_SPEED);
            leftMotor.forward();
            rightMotor.backward();

			// rotate the robot until it sees a wall
			while (getFilteredData() > 30);

			// play sound
            Sound.playTone(3000,100);

			// keep rotating until the robot sees no wall, then latch the angle
			while (getFilteredData() < 50);
	        firstAngle = odometer.getTheta();

	        // play lower frequency sound
            Sound.playTone(2000,100);

            // stop motors
	        leftMotor.stop();
	        rightMotor.stop();

			// switch direction and wait until it sees a wall
			while (getFilteredData() > 30) {
				leftMotor.setSpeed(ROTATE_SPEED);
	            rightMotor.setSpeed(ROTATE_SPEED);
	            leftMotor.backward();
	            rightMotor.forward();
	        }

	        // play higher frequency sound
            Sound.playTone(3000,100);

			// keep rotating until the robot sees no wall, then latch the angle
			while (getFilteredData() < 50);
	        secondAngle = odometer.getTheta();

 	        // play lower frequency sound
            Sound.playTone(2000,100);

            // stop motors
	        leftMotor.stop();
	        rightMotor.stop();

	        // reset flag
			Lab4.usRunning = false;

			// measure orientation
			// no if statement is necessary due to usRunning flag
			// which stops the odometer from fixing the angle between
			// 0 and 2 pi
			deltaTheta = (secondAngle + firstAngle) / 2 - 32 + 170;

			// update the odometer position (example to follow:)
			odometer.setTheta(secondAngle - deltaTheta);
		}
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();

		// filter out incorrect values that are over 50 or under 30
		if (distance >= 50 && ++counterLarge > 30) {
			this.distance = distance;
			counterSmall = 0;
		} else if (distance < 50 && ++counterSmall > 30) {
			this.distance = distance;
			counterLarge = 0;
		}

		this.tempDistance = distance;
		
		return this.distance;
	}
}
