/* 
 * Lab4.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.*;

public class Lab4 {
	public static Navigation nav;
	public static USLocalizer usl;
	public static boolean usRunning = false; // flag of whether ultrasonic sensor is running or not
	public static ColorSensor sensor = new ColorSensor(SensorPort.S3, 7);

	public static void main(String[] args) {
		// set volume to max
		Sound.setVolume(100);

		int buttonChoice;
        
        // clear the display
        LCD.clear();

        // ask the user whether the motors should drive in a square or float
        LCD.drawString("    <   |   >   ", 0, 0);
        LCD.drawString("        |       ", 0, 1);
        LCD.drawString("   FALL | RISE  ", 0, 2);
        LCD.drawString("   EDGE | EDGE  ", 0, 3);
        LCD.drawString("        |       ", 0, 4);

        // wait for button press
        buttonChoice = Button.waitForAnyPress();
        while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

		Odometer odometer = new Odometer();
		LCDInfo lcd = new LCDInfo(odometer);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		nav = new Navigation(odometer);

		// start odometer
		odometer.start();

        // choose localization method based on button
        if (buttonChoice == Button.ID_LEFT)
			usl = new USLocalizer(odometer, us, USLocalizer.LocalizationType.FALLING_EDGE);
        else
			usl = new USLocalizer(odometer, us, USLocalizer.LocalizationType.RISING_EDGE);

		// perform the ultrasonic localization
		usl.doLocalization();
		
		// perform the light sensor localization
		LightLocalizer csl = new LightLocalizer(odometer, sensor);
		csl.doLocalization();			
		
		Button.waitForAnyPress();
	}

}
