import java.util.Comparator;

public class SortPath implements Comparator<PathNode>
{
    private char sortPref; // store the sorting preference

    public SortPath(char sortPref)
    {
        this.sortPref = sortPref;
    }

    /* compare
    This method will use the comparator interface to sort the city paths based on a given sorting preference.

    Parameters:
    sourceCity - The source city node.
    destinationCity - The destination city node.
     */
    @Override
    public int compare(PathNode sourceCity, PathNode destinationCity)
    {
        if (sortPref == 'C') // checking if we are sorting by cost, if not sort by duration
            return Float.compare(sourceCity.cost, destinationCity.cost);
        else
            return Integer.compare(sourceCity.duration, destinationCity.duration);
    }
}
