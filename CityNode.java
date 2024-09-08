import java.util.LinkedList;

public class CityNode
{
    String name; // store the name of the city
    LinkedList<CityPath> path; // keep a linked list of the city paths

    public CityNode(String name)
    {
        this.name = name;
        this.path = new LinkedList<>();
    }

    /* addPath
    This method adds a path to the path list given the details of the path.

    Parameter:
    cost - The flight cost.
    duration - The flight duration.
     */
    public void addPath(String destination, int cost, int duration)
    {
        this.path.add(new CityPath(destination, cost, duration)); // add a new path
    }
}
