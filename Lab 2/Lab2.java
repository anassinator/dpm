/*
 * Lab2.java
 */

import lejos.nxt.*;

public class Lab2 {
    public static void main(String[] args) {
        int buttonChoice;

        // some objects that need to be instantiated
        Odometer odometer = new Odometer();
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
        
        // clear the display
        LCD.clear();

        // ask the user whether the motors should drive in a square or float
        LCD.drawString("< Left | Right >", 0, 0);
        LCD.drawString("       |        ", 0, 1);
        LCD.drawString(" Float | Drive  ", 0, 2);
        LCD.drawString("motors | in a   ", 0, 3);
        LCD.drawString("       | square ", 0, 4);

        buttonChoice = Button.waitForAnyPress();

        while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

        if (buttonChoice == Button.ID_LEFT) {
            for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
                motor.forward();
                motor.flt();
            }

            // start only the odometer and the odometry display
            odometer.start();
            odometryDisplay.start();
        } else {
            // start the odometer, the odometry display and (possibly) the
            // odometry correction
            odometer.start();
            odometryDisplay.start();
            odometryCorrection.start();

            // spawn a new Thread to avoid SquareDriver.drive() from blocking
            (new Thread() {
                public void run() {
                    double leftRadius = 2.16;  // RADIUS OF LEFT WHEEL
                    double rightRadius = 2.14; // RADIUS OF RIGHT WHEEL
                    double width = 16.1;       // DISTANCE BETWEEN WHEELS
                    SquareDriver.drive(Motor.B, Motor.C, leftRadius, rightRadius, width);
                }
            }).start();
        }
        
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);
    }
}