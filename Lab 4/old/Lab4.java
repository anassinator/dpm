/*
 * Lab4.java
 */

import lejos.nxt.*;

public class Lab4 {
    public static Odometer odometer;
    private static Navigation navigation;
    public static boolean done = false;
    
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
        LCD.drawString("   Dumb | Smart  ", 0, 2);
        LCD.drawString("    Way | Way    ", 0, 3);
        LCD.drawString("        |        ", 0, 4);

        buttonChoice = Button.waitForAnyPress();

        while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

        // start the odometer and the odometry display
        odometer.start();
        odometryDisplay.start();

        // choose path based on button
        if (buttonChoice == Button.ID_LEFT)
            navigation = new Navigation(odometer, true);
        else
            navigation = new Navigation(odometer, false);

        // start navigation
        navigation.start();
        
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);
    }
}