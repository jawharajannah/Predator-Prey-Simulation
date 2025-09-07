import java.util.Random;
/**
 * Common elements of all beings.
 *
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public abstract class Being
{
    // A shared random number generator
    private final Random rand = Randomizer.getRandom();
    // Whether the being is alive or not.
    protected boolean alive;
    // The being's position.
    protected Location location;
    // Reference to the simulator
    protected Simulator simulator;
    
    /**
     * Constructor for objects of class Being.
     * @param location The being's location.
     * @param simulator The simulator currently being used.
     */
    public Being(Location location, Simulator simulator)
    {
        this.alive = true;
        this.location = location;
        this.simulator = simulator;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the being is alive or not.
     * @return true if the being is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the being is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the being's location.
     * @return The being's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the being's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
}
