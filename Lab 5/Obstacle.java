/* 
 * Obstacle.java
 * @authors     Anass Al-Wohoush        260575013
 *              Malcolm William Watt    260585950
 * @team        42
 */

public class Obstacle {
    private final int WOOD = 0;
    private final int SMURF = 1;

    public int id;
    public double[] position = new double[2];

    private Odometer odometer;

    public Obstacle(int id, double distance, Odometer odometer) {
        this.id = id;
        this.odometer = odometer;

        this.position[0] = odometer.getX() + distance * Math.cos(odometer.getTheta());
        this.position[1] = odometer.getY() + distance * Math.sin(odometer.getTheta());
    }

}
