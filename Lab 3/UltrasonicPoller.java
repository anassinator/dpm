import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread {
	private UltrasonicSensor us;
	private UltrasonicController cont;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
	}
	
	public void run() {
		while (true) {
			// PROCESS COLLECTED DATA
			// AND PRESCALE IT WITH COS(45) TO ACCOMMODATE FOR
			// THE 45 DEGREE ANGLE OF THE SENSOR
			// COS(45) = SIN(45) = 0.7071 APPROXIMATELY
			cont.processUSData((int) (us.getDistance() * 0.7071));
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}
}
