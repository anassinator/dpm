/* 
 * Lab5.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

import lejos.nxt.*;

public class Lab5 {
    private static double path[][] = {{1,2}, {0,0}, {1,4}, {2,4}, {0,5}, {2,5}, {0,6}, {2,6}, {0,6.5}, {2,6.5}};
    private static Odometer odometer = new Odometer();
    private static Display display = new Display(odometer);
    private static Robot robot = new Robot();
    private static Navigation nav = new Navigation(robot, path);
    // private static Localizer localizer = new Localizer(robot, nav);
    // private static ObstacleDetection obstacleManager = new ObstacleDetection(robot, nav);

    public static void main(String[] args) {
        // SET VOLUME TO MAX
        Sound.setVolume(100);

        LCD.drawString("WELCOME", 5, 4);

        Button.waitForAnyPress();

        // CLEAR DISPLAY
        LCD.clear();

        robot.odometer = odometer;
        odometer.robot = robot;
        odometer.start();
        display.start();

        // localizer.doLocalization();
        // obstacleManager.start();
        nav.start();
    }

}
