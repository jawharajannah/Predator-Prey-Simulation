import java.util.*;
/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * animals and plants.
 * 
 * @author David J. Barnes, Michael Kölling, Jawhara Jannah, Fatimah Khan
 * @version 7.2
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a crocodile will be created in any given grid position.
    private static final double CROCODILE_CREATION_PROBABILITY = 0.03;
    // The probability that a bird will be created in any given position.
    private static final double BIRD_CREATION_PROBABILITY = 0.08;    
    // The probability that a snake will be created in any given position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.03; 
    // The probability that a fish will be created in any given position.
    private static final double FISH_CREATION_PROBABILITY = 0.08;
    // The probability that a lizard will be created in any given position.
    private static final double LIZARD_CREATION_PROBABILITY = 0.08; 
    // The probability that a fruit will be created in any given position.
    private static final double FRUIT_CREATION_PROBABILITY = 0.02; 
    // The probability that an algae will be created in any given position.
    private static final double ALGAE_CREATION_PROBABILITY = 0.02; 
    // The probability that a leaf will be created in any given position.
    private static final double LEAF_CREATION_PROBABILITY = 0.02; 

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current time of day (e.g., "daytime" or "night").
    private String timeOfDay;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // The current weather condition in the simulation.
    private Weather weather;

    // Enum representing different weather conditions in the simulation.
    private enum Weather {
        sunny, rainy, foggy
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
        timeOfDay = "daytime";
        weather = Weather.sunny;
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        field = new Field(depth, width);
        view = new SimulatorView(depth, width, this);

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long 
     * period (500 steps).
     */
    public void runLongSimulation()
    {
        simulate(500);
    }

    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(150);         // adjust this to change execution speed
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each being.
     */
    public void simulateOneStep()
    {
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Being> beings = field.getBeings();
        for (Being aBeing : beings) {
            aBeing.act(field, nextFieldState);
        }
        // Every 10 steps represents change in time - from day to night.
        if (step % 10 == 0) {
            if (timeOfDay.equals("daytime")){
                timeOfDay = "night";
            }
            else {
                timeOfDay = "daytime";
            }
            System.out.println("It is now " + timeOfDay);
        }
        
        // Every 20 steps (a full day) there is a random change of weather - sunny, rainy, or foggy.
        if (step % 20 == 0) {
            this.weather = 
            switch (Randomizer.getRandom().nextInt(3)) {
                    case 0 -> Weather.rainy;
                    case 1 -> Weather.foggy;
                    default -> Weather.sunny;};
            System.out.println("The weather today is " + weather);
        }

        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field);
        timeOfDay = "daytime";
        weather = Weather.sunny;
    }

    /**
     * Randomly populate the field with beings.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CROCODILE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Crocodile crocodile = new Crocodile(true, location, this);
                    field.placeBeing(crocodile, location);
                }
                else if(rand.nextDouble() <= BIRD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bird bird = new Bird(true, location, this);
                    field.placeBeing(bird, location);
                }
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, location, this);
                    field.placeBeing(snake, location);
                }
                else if(rand.nextDouble() <= FISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fish fish = new Fish(true, location, this);
                    field.placeBeing(fish, location);
                }
                else if(rand.nextDouble() <= LIZARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lizard lizard = new Lizard(true, location, this);
                    field.placeBeing(lizard, location);
                }
                else if(rand.nextDouble() <= FRUIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fruit fruit = new Fruit(true, location, this, false);
                    field.placeBeing(fruit, location);
                }
                else if(rand.nextDouble() <= ALGAE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Algae algae = new Algae(true, location, this, false);
                    field.placeBeing(algae, location);
                }
                else if(rand.nextDouble() <= LEAF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leaf leaf = new Leaf(true, location, this, false);
                    field.placeBeing(leaf, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of being in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }

    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }

    /**
     * Get the current time of day in the simulation.
     * @return The current time of day as a string (e.g., "daytime" or "night"). Returns an empty space if the value is null.
     */
    public String getTimeOfDay() {
        if (timeOfDay != null) {
            return timeOfDay;
        } 
        else {
            return " ";
        }
    }
    
    /**
     * Get the current weather condition in the simulation.
     * @return The current weather as a string (e.g., "sunny", "rainy", "foggy"). Returns an empty string if the value is null.
     */
    public String getWeather() {
        if (weather != null) {
            return "" + weather;
        } 
        else {
            return "";
        }
    }
}
