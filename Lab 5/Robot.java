/* 
 * Robot.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.util.*;

public class Robot {
    public Odometer odometer;
    public final double leftRadius = 2.16;              // RADIUS OF LEFT WHEEL
    public final double rightRadius = 2.13;             // RADIUS OF RIGHT WHEEL
    public final double width = 16.1;                   // DISTANCE BETWEEN WHEELS
    
    // STORE MOTORS AND ULTRASONIC SENSOR
    public NXTRegulatedMotor claw = Motor.A, leftMotor = Motor.B, rightMotor = Motor.C;
    public static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S3);

    // STORE WHETHER TRYING TO LOCALIZE OR NOT
    public boolean localizing = false;
}
