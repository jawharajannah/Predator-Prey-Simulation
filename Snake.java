import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * snakes age, move, eat birds and lizards, and die.
 * 
 * @author Jawhara Jannah and Fatimah Khan
 * @version 8.0
 */
public class Snake extends Animal
{
    // Characteristics shared by all snakes (class variables).

    // The age at which a snakes can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a snakes can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a snakes breeding.
    private static final double BREEDING_PROBABILITY = 0.95;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single prey. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int BIRD_FOOD_VALUE = 80;
    private static final int LIZARD_FOOD_VALUE = 60;    

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The snakes'age.
    private int age;

    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param location The location within the field.
     * @param simulator The simulator currently being used.
     */
    public Snake(boolean randomAge, Location location, Simulator simulator)
    {
        super(location, simulator);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(Math.max(BIRD_FOOD_VALUE,LIZARD_FOOD_VALUE));
    }

    /**
     * This is what the snake does most of the time: it hunts for
     * birds and lizards. In the process, it might breed, die of hunger,
     * or die of old age.
     * The snake can only do these things when it daytime.
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
            if (simulator.getTimeOfDay().equals("daytime")){
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
        return "snake{" +
        "age=" + age +
        ", alive=" + isAlive() +
        ", location=" + getLocation() +
        ", foodLevel=" + foodLevel +
        '}';
    }

    /**
     * Increase the age. This could result in the snake's death.
     */
    private void incrementAge()
    {
        if(++age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Look for birds and lizards adjacent to the current location.
     * Only the first live bird or lizard is eaten.
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
            if(being instanceof Lizard lizard && !simulator.getWeather().equals("foggy")) {
                if(lizard.isAlive()) {
                    lizard.setDead();
                    foodLevel = LIZARD_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (canBreed(nextFieldState)) {
            int births = breed(nextFieldState);
            if(births > 0) {
                for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                    Location loc = freeLocations.remove(0);
                    Snake young = new Snake(false, loc, simulator);
                    nextFieldState.placeBeing(young, loc);
                }
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     * @param field The field currently being displayed.
     */
    private int breed(Field field)
    {
        int births;
        // Pass the field object to canBreed() to check for breeding conditions.
        if (canBreed(field) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;  // Random number of births
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A snake can breed if it has reached the breeding age are female and are nearby a male snake.
     * @return true if the snake can breed, false otherwise.
     * @param field The field currently being displayed.
     */
    private boolean canBreed(Field field)
    {
        if (this.gender == Gender.FEMALE && age >= BREEDING_AGE) {
            List<Location> adjacentLocations = field.getAdjacentLocations(this.location);

            for (Location loc : adjacentLocations) {
                Being beingAtLocation = field.getBeingAt(loc);  

                if (beingAtLocation != null && beingAtLocation instanceof Snake) {
                    Snake male = (Snake) beingAtLocation;
                    if (male.gender == Gender.MALE) {
                        return true; 
                    }
                }
            }
        }
        return false;
    }
}

