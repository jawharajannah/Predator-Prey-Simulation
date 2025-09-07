import java.util.List;
import java.util.Random;

/**
 * A simple model of a leaf.
 * leaves age and die.
 * 
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public class Leaf extends Plant
{
    // Characteristics shared by all leaves (class variables).
    
    // The age to which a leaf can live.
    private static final int MAX_AGE = 30;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The leaf's age.
    private int age;

    /**
     * Create a new leaf. A leaf may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the leaf will have a random age.
     * @param location The location within the field.
     * @param simulator The simulator currently being used.
     * @param isEaten If true, the leaf is assumed to have been eaten.
     */
    public Leaf(boolean randomAge, Location location, Simulator simulator, boolean isEaten)
    {   super(location, simulator, isEaten);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the leaf does most of the time - it grows and stays in place.
     * A leaf can only grow when it is sunny and daytime.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    { 
        if(isAlive()) {
            if (simulator.getTimeOfDay().equals("daytime") && simulator.getWeather().equals("sunny")){
                incrementAge(nextFieldState, false);                
            } 
            nextFieldState.placeBeing(this, getLocation());
        }
    }

    @Override
    public String toString() {
        return "leaf{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", eaten=" + getIsEaten() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the leaf's death.
     * When a leaf dies it triggers the birth of new leaves in
     * free neighbouring cells.
     * @param nextFieldState The updated field.
     * @param isEaten If true, the fruit is assumed to have been eaten.
     */
    public void incrementAge(Field nextFieldState, boolean isEaten)
    {
        if(++age > MAX_AGE || this.isEaten == true) {
            List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
            for (int b = 0; b < freeLocations.size() && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Leaf young = new Leaf(false, loc, simulator, false);
                nextFieldState.placeBeing(young, loc);
            }
            setDead();
        }
    }
}