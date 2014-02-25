/* 
 * LCDInfo.java
 * @authors     Anass Al-Wohoush, 260575013
 *              Malcolm William Watt, 260585950
 * @team        42
 */

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odometer;
	private Timer lcdTimer;
	
	// arrays for displaying data
	private double[] pos;
	
	public LCDInfo(Odometer odometer) {
		this.odometer = odometer;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// initialise the arrays for displaying data
		pos = new double[3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odometer.getPosition(pos, new boolean[] {true, true, true});
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("D: ", 0, 3);
		LCD.drawString(String.valueOf(pos[0]), 3, 0);
		LCD.drawString(String.valueOf(pos[1]), 3, 1);
		LCD.drawString(String.valueOf(pos[2]), 3, 2);
		LCD.drawInt(Lab4.usl.tempDistance, 3, 3); // print distance measured by US
	}
}
