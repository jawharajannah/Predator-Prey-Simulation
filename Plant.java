import java.util.List;

/**
 * Common elements of plants.
 *
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public abstract class Plant extends Being
{
    // If the plant has been eaten or not
    protected boolean isEaten;
    
    /**
     * Constructor for objects of class Plant.
     * @param location The plant's location.
     * @param simulator The simulator currently being used.
     */
    public Plant(Location location, Simulator simulator, boolean isEaten)
    {
        super(location, simulator);
        this.isEaten = false;
    }
    
    public boolean getIsEaten() {
        return this.isEaten;
    }
}
