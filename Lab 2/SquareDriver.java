/*
 * SquareDriver.java
 */

import lejos.nxt.*;

public class SquareDriver {
    private static final int FORWARD_SPEED = 250;
    private static final int ROTATE_SPEED = 150;
    public static boolean turning = false;          // STATE OF ROBOT WHETHER TURNING OR NOT

    public static void drive(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
            double leftRadius, double rightRadius, double width) {
        // reset the motors
        for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
            motor.stop();
            motor.setAcceleration(3000);
        }

        // wait 5 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // there is nothing to be done here because it is not expected that
            // the odometer will be interrupted by another thread
        }

        for (int i = 0; i < 4; i++) {
            // drive forward two tiles
            leftMotor.setSpeed(FORWARD_SPEED);
            rightMotor.setSpeed(FORWARD_SPEED);

            leftMotor.rotate(convertDistance(leftRadius, 60.96), true);
            rightMotor.rotate(convertDistance(rightRadius, 60.96), false);

            // turn 90 degrees clockwise
            leftMotor.setSpeed(ROTATE_SPEED);
            rightMotor.setSpeed(ROTATE_SPEED);

            turning = true; // CHANGE ROTATION STATE
            leftMotor.rotate(convertAngle(leftRadius, width, 90.0), true);
            rightMotor.rotate(-convertAngle(rightRadius, width, 90.0), false);
            turning = false; // CHANGE ROTATION STATE
        }
    }

    private static int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private static int convertAngle(double radius, double width, double angle) {
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
}