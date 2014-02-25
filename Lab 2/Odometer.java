/*
 * Odometer.java
 */

import lejos.nxt.*;

public class Odometer extends Thread {
    // robot position
    private double x, y, theta;
    public final double leftRadius = 2.16;              // RADIUS OF LEFT WHEEL
    public final double rightRadius = 2.14;             // RADIUS OF RIGHT WHEEL
    public final double width = 16.1;                   // DISTANCE BETWEEN WHEELS
    private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C;
    private final int degToRad = 1745;                  // CONVERSION RATIO FROM DEGREES TO RADIANS
                                                        // SCALED UP BY 100 000 TO PRESERVE ACCURACY
    private final int radToDeg = 572958;                // CONVERSION RATIO FROM RADIANS TO DEGREES
                                                        // SCALED UP BY 10 000 TO PRESERVE ACCURACY
    private int prevTacoRight = 0, prevTacoLeft = 0;    // COUNTER TO STORE PREVIOUS TACHOMETER COUNT

    // odometer update period, in ms
    private static final long ODOMETER_PERIOD = 25;

    // lock object for mutual exclusion
    private Object lock;

    // default constructor
    public Odometer() {
        x = 0.0;
        y = 0.0;
        theta = 1.57; // START AT 90 DEGREES
        lock = new Object();
    }

    // run method (required for Thread)
    public void run() {
        long updateStart, updateEnd;

        while (true) {
            updateStart = System.currentTimeMillis();
            // put (some of) your odometer code here
            
            // NO...
            
            synchronized (lock) {
                // CALCULATE THE DISTANCE TRAVELED BY EACH WHEEL BY MEASURING THE TACHOMETER DIFFERENCE
                // d = r * delta(theta in radians)
                double distanceRight = rightRadius * ((rightMotor.getTachoCount() - prevTacoRight) * degToRad) / 100000;
                double distanceLeft = leftRadius * ((leftMotor.getTachoCount() - prevTacoLeft) * degToRad) / 100000;

                // STORE CURRENT TACHOCOUNTER TO REUSE AS PREVIOUS LATER            
                prevTacoLeft = leftMotor.getTachoCount();
                prevTacoRight = rightMotor.getTachoCount();

                // AVERAGING DISTANCE TRAVELLED BY EACH WHEEL TO GET DISTANCE OF ENTIRE ROBOT
                double distance = (distanceLeft + distanceRight) / 2;

                // TRIGONOMETRIZE EVERYTHING BECAUSE TRIANGLE
                // X = COS(THETA) AND Y = SIN(THETA)
                // WHERE THE POSITIVE Y AXIS POINTS TOWARDS THE FRONT OF THE ROBOT
                // AND THE POSITIVE X AXIS POINTS TOWARDS ITS RIGHT
                // BY THE RIGHT HAND RULE
                this.x += distance * Math.cos(theta);
                this.y += distance * Math.sin(theta);

                // SET THETA WHERE COUNTERCLOCKWISE ROTATION IS POSITIVE
                // AND CLOCKWISE IS NEGATIVE
                // THETA DIFFERENCE IS CALCULATED RELATIVE TO CENTER OF
                // ROTATION: BETWEEN BOTH WHEELS
                this.theta -= (distanceLeft - distanceRight) / width;

                // CORRECT RANGE OF THETA TO REMAIN BETWEEN 0 AND 2 PI
                if (theta < 0)
                    theta += 6.28;
                else if (theta > 6.28)
                    theta -= 6.28;
            }

            // this ensures that the odometer only runs once every period
            updateEnd = System.currentTimeMillis();
            if (updateEnd - updateStart < ODOMETER_PERIOD) {
                try {
                    Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
                } catch (InterruptedException e) {
                    // there is nothing to be done here because it is not
                    // expected that the odometer will be interrupted by
                    // another thread
                }
            }
        }
    }

    // accessors
    public void getPosition(double[] position, boolean[] update) {
        // ensure that the values don't change while the odometer is running
        synchronized (lock) {
            if (update[0])
                position[0] = x;
            if (update[1])
                position[1] = y;
            if (update[2])
                // CONVERT THETA TO DEGREES TO MAKE SENSE OF THINGS ON THE DISPLAY
                position[2] = theta * radToDeg / 10000;
        }
    }

    public double getX() {
        double result;

        synchronized (lock) {
            result = x;
        }

        return result;
    }

    public double getY() {
        double result;

        synchronized (lock) {
            result = y;
        }

        return result;
    }

    public double getTheta() {
        double result;

        synchronized (lock) {
            result = theta;
        }

        return result;
    }

    // mutators
    public void setPosition(double[] position, boolean[] update) {
        // ensure that the values don't change while the odometer is running
        synchronized (lock) {
            if (update[0])
                x = position[0];
            if (update[1])
                y = position[1];
            if (update[2])
                theta = position[2];
        }
    }

    public void setX(double x) {
        synchronized (lock) {
            this.x = x;
        }
    }

    public void setY(double y) {
        synchronized (lock) {
            this.y = y;
        }
    }

    public void setTheta(double theta) {
        synchronized (lock) {
            this.theta = theta;
        }
    }
}