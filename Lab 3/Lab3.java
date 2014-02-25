/*
 * Lab3.java
 */

import lejos.nxt.*;

public class Lab3 {
    public static Odometer odometer;
    private static Navigation navigation;
    private static double pathOne[][] = {{60,30}, {30,30}, {30,60}, {60,0}}; // array of coordinates for first path
    private static double pathTwo[][] = {{0,60}, {60,0}}; // array of coordinates for second path
    public static boolean done = false;
    private static NXTRegulatedMotor sensorMotor = Motor.A;
    public static void main(String[] args) {
        int buttonChoice;

        // some objects that need to be instantiated
        odometer = new Odometer();
        OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
        
        // clear the display
        LCD.clear();

        // ask the user whether the motors should drive in a square or float
        LCD.drawString(" < Left | Right >", 0, 0);
        LCD.drawString("        |        ", 0, 1);
        LCD.drawString("   Path | Path   ", 0, 2);
        LCD.drawString("    One | Two    ", 0, 3);
        LCD.drawString("        |        ", 0, 4);

        buttonChoice = Button.waitForAnyPress();

        while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

        // start the odometer and the odometry display
        odometer.start();
        odometryDisplay.start();

        // choose path based on button
        if (buttonChoice == Button.ID_LEFT)
            navigation = new Navigation(odometer, pathOne);
        else
            navigation = new Navigation(odometer, pathTwo);

        // start navigation
        navigation.start();

        // rotate sensor to check for obstacles
        while (!done) {
            sensorMotor.rotateTo(-45);
            sensorMotor.rotateTo(0);
        }
        
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);
    }
}