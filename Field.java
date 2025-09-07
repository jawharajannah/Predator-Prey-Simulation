import java.util.*;
/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single being/object.
 * 
 * @author David J. Barnes, Michael Kölling, Jawhara Jannah, Fatimah Khan
 * @version 8.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    
    // A Map field that maps Location objects to Being objects
    private final Map<Location, Being> field = new HashMap<>();
    // A List field to store Being objects
    private final List<Being> beings = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place a being at the given location.
     * If there is already a being at the location it will
     * be lost.
     * @param aBeing The being to be placed.
     * @param location Where to place the being.
     */
    public void placeBeing(Being aBeing, Location location)
    {
        //assert location != null;
        Object other = field.get(location);
        if(other != null) {
            beings.remove(other);
        }
        field.put(location, aBeing);
        beings.add(aBeing);
    }
    
    /**
     * Return the being at the given location, if any.
     * @param location Where in the field.
     * @return The being at the given location, or null if there is none.
     */
    public Being getBeingAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Being aBeing = field.get(next);
            if(aBeing == null) {
                free.add(next);
            }
            else if(!aBeing.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of beings in the field.
     */
    public void fieldStats()
    {
        int numCrocodiles = 0, numBirds = 0 , numSnakes = 0, numFish = 0, 
        numLizard = 0, numFruit = 0, numAlgae = 0, numLeaf = 0;
        for(Being aBeing : field.values()) {
            if(aBeing instanceof Crocodile crocodile) {
                if(crocodile.isAlive()) {
                    numCrocodiles++;
                }
            }
            else if(aBeing instanceof Bird bird) {
                if(bird.isAlive()) {
                    numBirds++;
                }
            }
            else if(aBeing instanceof Snake snake) {
                if(snake.isAlive()) {
                    numSnakes++;
                }
            }
            else if(aBeing instanceof Fish fish) {
                 if(fish.isAlive()) {
                    numFish++;
                }
            }
            else if(aBeing instanceof Lizard lizard) {
                 if(lizard.isAlive()) {
                    numLizard++;
                }
            }
            else if(aBeing instanceof Fruit fruit) {
                 if(fruit.isAlive()) {
                    numFruit++;
                }
            }
            else if(aBeing instanceof Algae algae) {
                 if(algae.isAlive()) {
                    numAlgae++;
                }
            }
            else if(aBeing instanceof Leaf leaf) {
                 if(leaf.isAlive()) {
                    numLeaf++;
                }
            }
        }
        System.out.println("Birds: " + numBirds +
                           " crocodiles: " + numCrocodiles +
                           " snakes: " + numSnakes +
                           " fish: " + numFish +
                           " lizard: " + numLizard +
                           " fruit: " + numFruit +
                           " algae: " + numAlgae +
                           " leaf: " + numLeaf) ;
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one bird and one crocodile in the field.
     * @return true if there is at least one bird and one crocodile in the field.
     */
    public boolean isViable()
    {
        boolean birdFound = false;
        boolean crocodileFound = false;
        boolean snakeFound = false;
        boolean fishFound = false;
        boolean lizardFound = false;
        Iterator<Being> it = beings.iterator();
        while(it.hasNext() && ! (birdFound && crocodileFound && snakeFound && fishFound && lizardFound)) {
            Being aBeing = it.next();
            if(aBeing instanceof Bird bird) {
                if(bird.isAlive()) {
                    birdFound = true;
                }
            }
            else if(aBeing instanceof Crocodile crocodile) {
                if(crocodile.isAlive()) {
                    crocodileFound = true;
                }
            }
            else if(aBeing instanceof Snake snake) {
                if(snake.isAlive()) {
                    snakeFound = true;
                }
            }
            else if(aBeing instanceof Fish fish) {
                if(fish.isAlive()) {
                    fishFound = true;
                }
            }
            else if(aBeing instanceof Lizard lizard) {
                if(lizard.isAlive()) {
                    lizardFound = true;
                }
            }
        }
        return birdFound && crocodileFound && snakeFound && fishFound && lizardFound;
    }
    
    /**
     * Get the list of beings.
     */
    public List<Being> getBeings()
    {
        return beings;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}