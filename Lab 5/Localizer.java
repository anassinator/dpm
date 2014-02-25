/* 
 * Localizer.java
 * @authors     Anass Al-Wohoush 		260575013
 *              Malcolm William Watt 	260585950
 * @team        42
 */

import lejos.nxt.*;

public class Localizer {

    private double firstAngle, secondAngle, deltaTheta;

	private Robot robot;
	private Navigation nav;

	private int counterLarge = 0, counterSmall = 0, distance = 0;
	public int tempDistance = 0;
	
	public Localizer(Robot robot, Navigation nav) {
		this.robot = robot;
		this.nav = nav;

		// switch off the ultrasonic sensor
		robot.sonic.off();
	}
	
	public void doLocalization() {
		robot.localizing = true;

		nav.setRotateSpeed(200);

		double[] pos = new double [3];

		LCD.drawString("LOCATING...", 0, 0);
		
		// rotate the robot until it sees no wall
		while (getFilteredData() < 50) {
            robot.leftMotor.forward();
            robot.rightMotor.backward();
        }

        // play sound
        Sound.playTone(3000,100);

		// keep rotating until the robot sees a wall, then latch the angle
		while (getFilteredData() > 30);
        firstAngle = Math.toDegrees(robot.odometer.getTheta());

        // play lower frequency sound
        Sound.playTone(2000,100);

        // stop motors
		nav.stop();

		// switch direction and wait until it sees no wall
		while (getFilteredData() < 50) {
            robot.leftMotor.backward();
            robot.rightMotor.forward();
        }

        // play higher frequency sound
        Sound.playTone(3000,100);

		// keep rotating until the robot sees a wall, then latch the angle
		while (getFilteredData() > 30);
        secondAngle = Math.toDegrees(robot.odometer.getTheta());

        // play lower frequency sound
        Sound.playTone(2000,100);

        // stop motors
		nav.stop();

		// measure orientation
		deltaTheta = (secondAngle + firstAngle) / 2 - 50;

		// update the odometer position
		robot.odometer.setTheta(Math.toRadians(secondAngle - deltaTheta));

		robot.localizing = false;

		nav.turnTo(Math.PI / 2);
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		robot.sonic.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = robot.sonic.getDistance();

		// filter out incorrect values that are over 50 or under 30
		if (distance >= 50 && ++counterLarge > 10) {
			this.distance = distance;
			counterSmall = 0;
		} else if (distance < 50 && ++counterSmall > 10) {
			this.distance = distance;
			counterLarge = 0;
		}

		this.tempDistance = distance;
		
		return this.distance;
	}
}
