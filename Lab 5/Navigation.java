/*
 * Navigation.java
 */

import lejos.nxt.*;
import lejos.util.*;

public class Navigation extends Thread {
    private int FORWARD_SPEED = 250;
    private int ROTATE_SPEED = 50;
    private boolean turning = false, navigating = false, done = false; // setup needed flags
    private Odometer odometer;
    private double path[][]; // store desired path
    private NXTRegulatedMotor claw = Motor.A, leftMotor = Motor.B, rightMotor = Motor.C; // setup motors
    private double leftRadius = 2.16, rightRadius = 2.14, width = 16.1; // vehicle properties
    private UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S2); // setup ultrasonic sensor
    private int threshold = 15; // threshold for ultrasonic sensor
    public boolean paused = false, found = false, stop = false;
    private ObstacleDetection obstacleManager;

    public Navigation(Odometer odometer, ObstacleDetection obstacleManager, double[][] path) {
        // store odometer and path
        this.odometer = odometer;
        this.path = path;
        this.obstacleManager = obstacleManager;

        // stop motors
        leftMotor.stop();
        rightMotor.stop();

        // set motors acceleration
        leftMotor.setAcceleration(3000);
        rightMotor.setAcceleration(3000);
    }

    public void run() {
        // travel path specified
        for (int i = 0; i < path.length; i++)
            if (!found)
                travelTo(30.48 * path[i][0], 30.48 * path[i][1]);

        goForward(-7);
        turn(Math.PI);
        goForward(-5);
        grab();

        travelTo(60.96, 182.88);
        goForward(30);

        turnTo(5 * Math.PI / 4);
        letGo();
    }

    public void goForward() {
        leftMotor.forward();
        rightMotor.forward();
    }

    public void goBackward() {
        leftMotor.backward();
        rightMotor.backward();
    }

    public void goForward(double distance) {
        rightMotor.rotate(convertDistance(rightRadius, distance), true);
        leftMotor.rotate(convertDistance(leftRadius, distance), false);
    }

    public void travelTo(double xDestination, double yDestination) {
        // set navigation and done flags
        navigating = true;
        done = false;

        // keep trying to go to desired location until done
        while (!done) {

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
            rightMotor.rotate(convertDistance(rightRadius, desiredDistance), true);

            // while the motors are moving, keep measuring sensor data to avoid obstacles
            while (!found && (leftMotor.isMoving() || rightMotor.isMoving())) {
                // if obstacle detected
                if (obstacleManager.search() == 1)
                    if(obstacleManager.detect() == 1) {
                        found = true;
                        stop();
                        break;
                    } else {
                        goForward(-5);
                        turn(Math.PI / 2);
                        goForward(30);
                    }

            }
            
            // set done and return from function if and only if we are within +/- (1,1) of the desired destination
            if (Math.abs(xDestination - odometer.getX()) <= 1 && Math.abs(yDestination - odometer.getY()) <= 1)
                done = true;
        }
        
        // stop motors
        leftMotor.stop();
        rightMotor.stop();
        
        // reset navigating flag
        navigating = false;
    }

    public void turn(double theta) {
        setRotateSpeed(ROTATE_SPEED);

        // rotate said angle and wait until done
        leftMotor.rotate(-convertAngle(leftRadius, width, theta), true);
        rightMotor.rotate(convertAngle(rightRadius, width, theta), false);

        stop();

        setForwardSpeed(FORWARD_SPEED);
    }

    public void turnTo(double theta) {
        // calculate angle to rotate realtive to current angle
        boolean done = false;

        while (!done) {
            double currentOrientation = odometer.getTheta();
            double angle = theta - currentOrientation;

            // correct angle to remain within -180 and 180 degrees
            // to minimize angle to spin
            if (angle < -3.14)
                angle += 6.28;
            else if (angle > 3.14)
                angle -= 6.28;

            // set turning flag
            turning = true; 

            // rotate said angle and wait until done
            leftMotor.rotate(-convertAngle(leftRadius, width, angle), true);
            rightMotor.rotate(convertAngle(rightRadius, width, angle), false);

            if (Math.abs(odometer.getTheta() - theta) <= 0.02)
                done = true;
        }

        // reset turning flag
        turning = false;
    }

    public void stop() {
        leftMotor.stop();
        rightMotor.stop();
    }

    public void setForwardSpeed(int speed) {
        FORWARD_SPEED = speed;

        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
    }

    public void setRotateSpeed(int speed) {
        ROTATE_SPEED = speed;

        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
    }

    public void grab() {
        claw.rotate(180);
    }

    public void letGo() {
        claw.rotate(-180);
    }

    private static int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private static int convertAngle(double radius, double width, double angle) {
        // fixed to work in radians instead of degrees
        return convertDistance(radius, width * angle / 2.0);
    }
}