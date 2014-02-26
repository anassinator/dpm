/* 
 * Navigation.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;

public class Navigation {
    private int FORWARD_SPEED = 240;
    private int ROTATE_SPEED = 50;

    private double path[][]; // store desired path

    private Robot robot;

    public boolean found = false;

    public Navigation(Robot robot, double[][] path) {
        this.robot = robot;
        this.path = path;
    }

    public void run() {
        // travel path specified
        for (int i = 0; i < path.length; i++) {
            if (!found) {
                travelTo(30.48 * path[i][0],30.48 * path[i][1]);
                Sound.beep();
            }
        }
    }

    public void goForward() {
        robot.leftMotor.forward();
        robot.rightMotor.forward();
    }

    public void goBackward() {
        robot.leftMotor.backward();
        robot.rightMotor.backward();
    }


    public void goForward(double distance) {
        robot.rightMotor.rotate(convertDistance(robot.rightRadius, distance), true);
        robot.leftMotor.rotate(convertDistance(robot.leftRadius, distance), false);
    }
    
    public void travelTo(double xDestination, double yDestination) {
        boolean done = false;

        while (!done) {
            // calculate desired location from current location
            // by measuring the difference
            double x = xDestination - robot.odometer.getX();
            double y = yDestination - robot.odometer.getY();
        
            // measure desired orientation by trigonometry
            // atan2 deals with correct signs for us
            double desiredOrientation = Math.atan2(y, x);

            // spin to desired angle
            turnTo(desiredOrientation);
            // measure desired distance by pythagorus
            double desiredDistance = Math.sqrt(x * x + y * y);

            // set motor speeds
            setForwardSpeed(FORWARD_SPEED);

            // move forward desired distance and return immediately
            robot.leftMotor.rotate(convertDistance(robot.leftRadius, desiredDistance), true);
            robot.rightMotor.rotate(convertDistance(robot.rightRadius, desiredDistance), true);

            while (robot.leftMotor.isMoving() || robot.rightMotor.isMoving());

            if (Math.abs(robot.odometer.getX() - xDestination) <= 2.00 || Math.abs(robot.odometer.getY() - yDestination) <= 2.00)
                done = true;
        }

        // stop motors
        stop();
    }

    public void turnTo(double theta) {
        setRotateSpeed(ROTATE_SPEED);
        boolean done = false;

        while (!done) {
            // calculate angle to rotate realtive to current angle
            double currentOrientation = robot.odometer.getTheta();
            double angle = theta - currentOrientation;

            // correct angle to remain within -180 and 180 degrees
            // to minimize angle to spin
            if (angle < -3.14)
                angle += 6.28;
            else if (angle > 3.14)
                angle -= 6.28;

            // rotate said angle and wait until done
            robot.leftMotor.rotate(-convertAngle(robot.leftRadius, robot.width, angle), true);
            robot.rightMotor.rotate(convertAngle(robot.rightRadius, robot.width, angle), false);

            stop();

            if (Math.abs(robot.odometer.getTheta() - theta) <= 0.017)
                done = true;
        }

        stop();
        
        setForwardSpeed(FORWARD_SPEED);
    }

    public void turn(double theta) {
        setRotateSpeed(ROTATE_SPEED);

        // rotate said angle and wait until done
        robot.leftMotor.rotate(-convertAngle(robot.leftRadius, robot.width, theta), true);
        robot.rightMotor.rotate(convertAngle(robot.rightRadius, robot.width, theta), false);

        stop();

        setForwardSpeed(FORWARD_SPEED);
    }

    public void setForwardSpeed(int speed) {
        FORWARD_SPEED = speed;

        robot.leftMotor.setSpeed(speed);
        robot.rightMotor.setSpeed(speed);
    }

    public void setRotateSpeed(int speed) {
        ROTATE_SPEED = speed;

        robot.leftMotor.setSpeed(speed);
        robot.rightMotor.setSpeed(speed);
    }

    public void stop() {
        robot.leftMotor.stop();
        robot.rightMotor.stop();
    }

    private int convertDistance(double radius, double distance) {
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private int convertAngle(double radius, double width, double angle) {
        // fixed to work in radians instead of degrees
        return convertDistance(radius, robot.width * angle / 2.0);
    }
}
