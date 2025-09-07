/**
 * Provide a counter for a participant in the simulation.
 * This includes an identifying string and a count of how
 * many participants of this type currently exist within 
 * the simulation.
 * 
 * @author David J. Barnes, Michael Kölling, Jawhara Jannah, Fatimah Khan
 * @version 8.0
 */
public class Counter
{
    // A name for this type of simulation participant
    private final String name;
    // How many of this type exist in the simulation.
    private int count;
    // How many animals are infected in the simulation.
    private int infectionCount;
    
    /**
     * Provide a name for one of the simulation types.
     * @param name  A name, e.g. "Fox".
     */
    public Counter(String name)
    {
        this.name = name;
        count = 0;
        infectionCount = 0;
    }
    
    /**
     * @return The short description of this type.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The current count for this type.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Increment the current count by one.
     */
    public void increment()
    {
        count++;
    }
    
    /**
     * Reset the current count to zero.
     */
    public void reset()
    {
        count = 0;
        infectionCount = 0;
    }
    
    /**
     * @return The current infection count for this type.
     */
    public int getInfectionCount()
    {
        return infectionCount;
    }
    
    /**
     * Increment the current infection count by one.
     */
    public void incrementInfections()
    {
        infectionCount++;
    }
}
