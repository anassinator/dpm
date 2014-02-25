/* 
 * Robot.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;
import lejos.util.*;

public class Robot {
    private int FORWARD_SPEED = 180;
    private int ROTATE_SPEED = 50;

    public Odometer odometer;
    public final double leftRadius = 2.16;              // RADIUS OF LEFT WHEEL
    public final double rightRadius = 2.14;             // RADIUS OF RIGHT WHEEL
    public final double width = 16.1;                   // DISTANCE BETWEEN WHEELS
    
    public NXTRegulatedMotor claw = Motor.A, leftMotor = Motor.B, rightMotor = Motor.C;
    public static ColorSensor color = new ColorSensor(SensorPort. S2);
    public static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S3);

    public boolean localizing = false;

    public void grab() {
        claw.rotate(180);
    }

    public void letGo() {
        claw.rotate(-180);
    }
}