import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a lizard.
 * lizards age, move, eat leaves, breed, and die.
 * 
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public class Lizard extends Animal
{
    // Characteristics shared by all lizards (class variables).
    
    // The age at which a lizard can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a lizard can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a lizard breeding.
    private static final double BREEDING_PROBABILITY = 0.98;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;
    // The food value of a single plant. In effect, this is the
    // number of steps a lizard can go before it has to eat again.
    private static final int LEAF_FOOD_VALUE = 100;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The lizard's age.
    private int age;
    
    /**
     * Create a new lizard. A lizard may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the lizard will have a random age.
     * @param location The location within the field.
     * @param simulator The simulator currently being used.
     */
    public Lizard(boolean randomAge, Location location, Simulator simulator)
    {
        super(location, simulator);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = rand.nextInt(LEAF_FOOD_VALUE);
    }
    
    /**
     * This is what the lizard does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * The lizard can only do these things when it daytime.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if (simulator.getTimeOfDay().equals("daytime")){
                if(!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations);
                }
                // Move towards a source of food if found.
                Location nextLocation = findFood(currentField, nextFieldState);
                if(nextLocation == null && ! freeLocations.isEmpty() ) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
                // See if it was possible to move.
                if(nextLocation != null ) {
                    setLocation(nextLocation);
                    nextFieldState.placeBeing(this, nextLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }                
            else if (simulator.getTimeOfDay().equals("night")) {
                nextFieldState.placeBeing(this, getLocation());
            }
            else {
                // Overcrowding.
                setDead();
            }
            infectDisease(currentField);
        }
    }

    @Override
    public String toString() {
        return "lizard{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the lizard's death.
     */
    private void incrementAge()
    {
        if(++age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Look for leaves adjacent to the current location.
     * Only the first leaf is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field, Field nextFieldState)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Being being = field.getBeingAt(loc);
            if(being instanceof Leaf leaf) {
                if(leaf.isAlive()) {
                    leaf.incrementAge(nextFieldState, true);
                    foodLevel = LEAF_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether or not this lizard is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New birds are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (canBreed(nextFieldState)) {
            int births = breed(nextFieldState);
            if(births > 0) {
                for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                    Location loc = freeLocations.remove(0);
                    Lizard young = new Lizard(false, loc, simulator);
                    nextFieldState.placeBeing(young, loc);
                }
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     * @param field Field currently being displayed.
     */
    private int breed(Field field)
    {
        int births;
        if (canBreed(field) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;  
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A lizard can breed if it has reached the breeding age are female and are nearby a male lizard.
     * @return true if the lizard can breed, false otherwise.
     * @param field Field currently being displayed.
     */
    private boolean canBreed(Field field)
    {
        if (this.gender == Gender.FEMALE && age >= BREEDING_AGE) {
                List<Location> adjacentLocations = field.getAdjacentLocations(this.location);
    
                for (Location loc : adjacentLocations) {
                    Being beingAtLocation = field.getBeingAt(loc);  
                    
                    if (beingAtLocation != null && beingAtLocation instanceof Lizard) {
                        Lizard male = (Lizard) beingAtLocation;
                        if (male.gender == Gender.MALE) {
                            return true; 
                        }
                    }
                }
            }
        return false;
    } 
}
