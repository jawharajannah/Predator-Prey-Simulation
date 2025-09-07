import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a crocodile.
 * crocodiles age, move, eat birds and fish, and die.
 * 
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public class Crocodile extends Animal
{
    // Characteristics shared by all crocodiles (class variables).
    
    // The age at which a crocodile can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a crocodile can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a crocodile breeding.
    private static final double BREEDING_PROBABILITY = 0.95;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 20;
    // The food value of a single prey. In effect, this is the
    // number of steps a crocodile can go before it has to eat again.
    private static final int BIRD_FOOD_VALUE = 20;
    private static final int FISH_FOOD_VALUE = 80;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The crocodile's age.
    private int age;
    
    /**
     * Create a crocodile. A crocodile can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the crocodile will have random age and hunger level.
     * @param location The location within the field.
     * @param simulator The simulator currently being used.
     */
    public Crocodile(boolean randomAge, Location location, Simulator simulator)
    {
        super(location, simulator);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(Math.max(BIRD_FOOD_VALUE,FISH_FOOD_VALUE));
    }

    /**
     * This is what the crocodile does most of the time: it hunts for
     * birds and fish. In the process, it might breed, die of hunger,
     * or die of old age.
     * The crocodile is nocturnal and can only do these things when it 
     * is night.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();

        if(isAlive()) {
            List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if (simulator.getTimeOfDay().equals("night")){
                if(!freeLocations.isEmpty() ) {
                    giveBirth(nextFieldState, freeLocations);
                }
                // Move towards a source of food if found.
                Location nextLocation = findFood(currentField);
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

            else if (simulator.getTimeOfDay().equals("daytime")) {
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
        return "crocodile{" +
        "age=" + age +
        ", alive=" + isAlive() +
        ", location=" + getLocation() +
        ", foodLevel=" + foodLevel +
        '}';
    }

    /**
     * Increase the age. This could result in the crocodile's death.
     */
    private void incrementAge()
    {
        if(++age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live food is eaten.
     * If it's foggy the predator cannot hunt for food.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Being being = field.getBeingAt(loc);
            if(being instanceof Bird bird && !simulator.getWeather().equals("foggy")) {
                if(bird.isAlive()) {
                    bird.setDead();
                    foodLevel = BIRD_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            if(being instanceof Fish fish && !simulator.getWeather().equals("foggy")) {
                if(fish.isAlive()) {
                    fish.setDead();
                    foodLevel = FISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether or not this crocodile is to give birth at this step.
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
                    Crocodile young = new Crocodile(false, loc, simulator);
                    nextFieldState.placeBeing(young, loc);
                }
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
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
     * A crocodile can breed if it has reached the breeding age are female and are nearby a male crocodile.
     * @return true if the crocodile can breed, false otherwise.
     * @param field Field currently being displayed.
     */
    private boolean canBreed(Field field)
    {
        if (this.gender == Gender.FEMALE && age >= BREEDING_AGE) {
                List<Location> adjacentLocations = field.getAdjacentLocations(this.location);
    
                for (Location loc : adjacentLocations) {
                    Being beingAtLocation = field.getBeingAt(loc);  
                    
                    if (beingAtLocation != null && beingAtLocation instanceof Crocodile) {
                        Crocodile male = (Crocodile) beingAtLocation;
                        if (male.gender == Gender.MALE) {
                            return true; 
                        }
                    }
                }
            }
        return false;
    }
}
