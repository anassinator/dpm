/* 
 * Navigation.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.*;

public class Navigation {
	// put your navigation code here 
	
	private Odometer odometer;
	private final int FORWARD_SPEED = 50;
	public final double leftRadius = 2.16;              // RADIUS OF LEFT WHEEL
    public final double rightRadius = 2.14;             // RADIUS OF RIGHT WHEEL
    public final double width = 16.1;                   // DISTANCE BETWEEN WHEELS
    private final int degToRad = 1745;                  // CONVERSION RATIO FROM DEGREES TO RADIANS
                                                        // SCALED UP BY 100 000 TO PRESERVE ACCURACY
    private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C;
    public double tempAngle = 0;
	
	public Navigation(Odometer odometer) {
		this.odometer = odometer;
	}

	public void goForward(double distance) {
		rightMotor.rotate(convertDistance(rightRadius, distance), true);
		leftMotor.rotate(convertDistance(rightRadius, distance), false);
	}
	
	public void travelTo(double xDestination, double yDestination) {
        // calculate desired location from current location
        // by measuring the difference
        double x = xDestination - odometer.getX();
        double y = yDestination - odometer.getY();
    
        // measure desired orientation by trigonometry
        // atan2 deals with correct signs for us
        double desiredOrientation = Math.atan2(y, x);

        // spin to desired angle
        turnTo(desiredOrientation);

        // measure desired distance by pythagorus
        double desiredDistance = Math.sqrt(x * x + y * y);

        // set motor speeds
        leftMotor.setSpeed(FORWARD_SPEED);
        rightMotor.setSpeed(FORWARD_SPEED);

        // move forward desired distance and return immediately
        leftMotor.rotate(convertDistance(leftRadius, desiredDistance), true);
        rightMotor.rotate(convertDistance(rightRadius, desiredDistance), false);
        
        // stop motors
        leftMotor.stop();
        rightMotor.stop();
    }

    public void turnTo(double theta) {
        // calculate angle to rotate realtive to current angle
        double currentOrientation = odometer.getTheta() * degToRad / 100000;
        double angle = theta - currentOrientation;

        // correct angle to remain within -180 and 180 degrees
        // to minimize angle to spin
        if (angle < -3.14)
            angle += 6.28;
        else if (angle > 3.14)
            angle -= 6.28;

        // rotate said angle and wait until done
        this.tempAngle = angle;
        leftMotor.rotate(convertAngle(leftRadius, width, angle), true);
        rightMotor.rotate(-convertAngle(rightRadius, width, angle), false);

		leftMotor.stop();
		rightMotor.stop();
    }

	private int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private int convertAngle(double radius, double width, double angle) {
        // fixed to work in radians instead of degrees
        return convertDistance(radius, width * angle / 2.0);
    }
}
