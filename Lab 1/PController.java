import lejos.nxt.*;
import lejos.util.Delay;
import java.lang.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 300, FILTER_OUT = 10;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int filterControl;
	public boolean facingSideways = true;
	
	public PController(int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {

		// NOTE: ULTRASONICPOLLER.JAVA HAS BEEN TAMPERED WITH TO PROVIDE US WITH CORRECTLY SCALED DISTANCES
		// IN ORDER TO ACCOMMODATE FOR THE 45 DEGREES ORIENTATION OF THE SENSOR
	
		// COUNTS HOW MANY VALUES OF 180 WERE ENCOUNTERED IN A ROW.
		// IF COUNT IS GREATER THAN FILTER_OUT, THEN ROBOT IS SURE THAT THIS IS NOT A FALSE NEGATIVE.
		// 180 IS THE INTEGER VALUE OF 255 * COS(45)
		// THIS FILTER IGNORES SMALL GAPS
		if (distance == 180 && filterControl < FILTER_OUT) {
			// BAD VALUE, DO NOT SET THE DISTANCE
			// INCREMENT THE FILTER VALUE
			filterControl++;
		} else if (distance == 180){
			// TRUE 180, THEREFORE SET DISTANCE TO 180
			this.distance = distance;
		} else {
			// DISTANCE WENT BELOW 180, THEREFORE RESET EVERYTHING
			// MUST HAVE BEEN A FALSE NEGATIVE OR GAP
			filterControl = 0;
			this.distance = distance;
		}
		
		// PROPORTIONAL GAIN OF P-CONTROLLER
		// THE FURTHER AWAY FROM THE BANDCENTRE, THE GREATER THE GAIN
		// 10 HAS BEEN EVALUATED EXPERIMENTALLY 
		int delta = 10 * Math.abs(this.distance - this.bandCenter);
		
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
				// AND IF SO TURN RIGHT PROPORTIONALLY TO THE GAIN
				// BY ROTATING THE LEFT WHEEL AT AN INCREASED SPEED
				// AND THE RIGHT AT A MUCH SLOWER SPEED TO ALLOW FOR SMOOTHER TURNS
				// THE VALUE 10 WAS DETERMINED EXPERIMENTALLY
				// TO DISTANCE ITSELF FROM IT
				leftMotor.setSpeed(motorStraight + delta);
				rightMotor.setSpeed(10);
			} else if (this.distance > (this.bandCenter + this.bandwidth)) {
				// ELSE CHECK IF TOO FAR FROM WALL IN CONVEX CORNER
				// AND IF SO TURN LEFT PROPRTIONALLY TO THE GAIN
				// BY ROTATING THE RIGHT WHEEL AT AN INCREASED SPEED
				// AND THE LEFT AT A MUCH SLOWER SPEED TO ALLOW FOR SMOOTHER TURNS
				// THE VALUE 10 WAS DETERMINED EXPERIMENTALLY
				// TO APPROACH IT
				rightMotor.setSpeed(motorStraight + delta);
				leftMotor.setSpeed(10);
			}
		} else {
			// IF SENSOR IS FACING FORWARD
			// CHECK IF TOO CLOSE TO WALL IN FRONT IN CONCAVE CORNER
			if (this.distance < (this.bandCenter - this.bandwidth)) {
				// AND IF SO TURN RIGHT PROPORTIONALLY TO THE GAIN
				// BY ROTATING THE LEFT WHEEL AT AN INCREASED SPEED
				// AND THE RIGHT AT A MUCH SLOWER SPEED TO ALLOW FOR SMOOTHER TURNS
				// THE VALUE 10 WAS DETERMINED EXPERIMENTALLY
				// TO DISTANCE ITSELF FROM IT
				leftMotor.setSpeed(motorStraight + delta);
				rightMotor.setSpeed(10);
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
