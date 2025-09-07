import java.util.Random;
import java.util.List;

/**
 * Common elements of animals.
 *
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public abstract class Animal extends Being
{
    // The probability that an animal is infected at creation.
    private static final double INFECTION_CREATION_PROBABILITY = 0.10;
    // Indicates whether the animal is infected.
    protected Boolean infected; 
    
    // The gender of the animal, assigned at creation.
    protected Gender gender;
    
    // The current food level of the animal, decreases over time.
    protected int foodLevel;
    
    // Enum representing the gender of the animal
    protected enum Gender {
        MALE, FEMALE
    }
    
    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     * @param simulator The simulator currently being used.
     */
    public Animal(Location location, Simulator simulator)
    {
        super(location, simulator);
        this.gender = (Randomizer.getRandom().nextInt(2) == 0) ? Gender.MALE : Gender.FEMALE;
    }
    
    /**
     * Get the gender of the animal.
     * @return The gender of this animal.
     */
    public Gender getGender() {
        return this.gender;
    }
    
    /**
     * Check if the animal is infected.
     * If the infection status is null, assume it is not infected.
     * @return True if the animal is infected, false otherwise.
     */
    public Boolean isInfected() {
        if (this.infected == null) {
            this.infected = false;
        }
        return this.infected;
    }
    
    /**
     * Randomly infects animals.
     * @param field The field currently occupied.
     */
    protected void infectDisease(Field field) {
        List<Location> adjacentLocations = field.getAdjacentLocations(this.location);
        for (Location loc : adjacentLocations) {
            Being beingAtLocation = field.getBeingAt(loc);
            if (beingAtLocation != null && beingAtLocation instanceof Animal) {
                Animal animal = (Animal) beingAtLocation;
                if (!animal.isInfected() && Randomizer.getRandom().nextDouble() <= INFECTION_CREATION_PROBABILITY) {  
                    animal.infected = true;
                }
            }
        }
    }

    /**
     * Make the animal more hungry. This could result in the animal's death.
     * If infected, hunger decreases by 5.
     */
    protected void incrementHunger() {
        if (isInfected()) {
            foodLevel -= 5;
        } else {
            foodLevel--;
        }
        if (foodLevel <= 0) {
            setDead();
        }
    }
}