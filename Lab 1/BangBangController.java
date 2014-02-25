import lejos.nxt.*;
import lejos.util.Delay;

public class BangBangController implements UltrasonicController {
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private final int motorStraight = 600;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	public boolean facingSideways = true;
	
	public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
						
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {

		// NOTE: ULTRASONICPOLLER.JAVA HAS BEEN TAMPERED WITH TO PROVIDE US WITH CORRECTLY SCALED DISTANCES
		// IN ORDER TO ACCOMMODATE FOR THE 45 DEGREES ORIENTATION OF THE SENSOR

		// SET THE VALUE OF THE PRESCALED DISTANCE
		this.distance = distance;
		
		// CHECK WHETHER SENSOR IS FACING SIDEWAYS OR NOT
		if (facingSideways) {
			// IF SENSOR IS FACING SIDEWAYS, CHECK WHETHER ROBOT IS WITHIN BANDCENTRE +- BANDWIDTH
			if (this.distance <= (this.bandCenter + this.bandwidth) && this.distance >= (this.bandCenter - this.bandwidth)) {
				// IF SO KEEP GOING STRAIGHT
				// EVERYTHING'S FINE
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight);
			} else if (this.distance < (this.bandCenter - this.bandwidth)) {
				// ELSE CHECK IF TOO CLOSE TO WALL
				// AND IF SO TURN RIGHT BY A PRESET VALUE
				// BY ROTATING THE LEFT WHEEL AT MOTORHIGH DEG/SEC
				// AND THE RIGHT AT MOTORLOW DEG/SEC
				// TO DISTANCE ITSELF FROM IT
				leftMotor.setSpeed(motorHigh);
				rightMotor.setSpeed(motorLow);
			} else if (this.distance > (this.bandCenter + this.bandwidth)) {
				// ELSE CHECK IF TOO FAR FROM WALL IN CONVEX CORNER
				// AND IF SO TURN LEFT BY A PRESET VALUE
				// BY ROTATING THE RIGHT WHEEL AT MOTORHIGH DEG/SEC
				// AND BY ROTATING THE LEFT BACKWARDS AT MOTORHIGH DEG/SEC
				// TO APPROACH IT BY PIVOTING ON ITSELF
				rightMotor.setSpeed(motorHigh);
				leftMotor.backward();
				leftMotor.setSpeed(motorHigh);
				// EXPERIMENTAL DELAY TO MAKE SURE TURN IS DONE
				Delay.msDelay(50);
				leftMotor.forward();
			}
		} else {
			// IF SENSOR IS FACING FORWARD
			// CHECK IF TOO CLOSE TO WALL IN FRONT IN CONCAVE CORNER
			if (this.distance < (this.bandCenter - this.bandwidth)) {
				// AND IF SO TURN RIGHT BY A PRESET VALUE
				// BY ROTATING THE LEFT WHEEL AT MOTORHIGH DEG/SEC
				// AND BY ROTATING THE RIGHT BACKWARDS AT MOTORHIGH DEG/SEC
				// TO DISTANCE ITSELF FROM IT BY PIVOTING ON ITSELF
				leftMotor.setSpeed(motorHigh);
				rightMotor.backward();
				rightMotor.setSpeed(motorHigh);
				// EXPERIMENTAL DELAY TO MAKE SURE TURN IS DONE
				Delay.msDelay(50);
				rightMotor.forward();
			} else {
				// OTHERWISE KEEP GOING STRAIGHT
				// EVERYTHING'S FINE
				leftMotor.setSpeed(motorStraight);
				rightMotor.setSpeed(motorStraight);
			}
		}

		// SWITCH BOOLEAN VALUE OF FACING SIDEWAYS IN ORDER TO GO INTO SECOND CONDITION
		facingSideways = !facingSideways;

		// THE ENTIRE PRINCIPLE OF CHECKING WHETHER WE'RE FACING SIDEWAYS OR FORWARDS WAS
		// SET IN PLACE IN ORDER TO KNOW WHAT THE ORIENTATION OF THE SENSOR WAS
		// THIS WAS HOWEVER ABANDONNED ONCE WE DECIDED TO GO FOR A 45 DEGREE ANGLE INSTEAD
		// OF ROTATING THE SENSOR BACK AND FORTH
		// THE ORIGINAL METHOD WAS CONTROLLED BY A TIMER WHICH SWITCHED THE PUBLIC VALUE
		// OF FACING SIDEWAYS EVERYTIME IT WAS CALLED
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
