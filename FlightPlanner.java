import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FlightPlanner
{
    private LinkedList<CityNode> cityList = new LinkedList<>(); // keep a linked list of cities

    /* readFlightData
    This method will read data from the flight data text file and process each data row.

    Parameters:
    flightDataFile - The flight data text file to read
    outputFile - The output file
     */
    private boolean readFlightData(String flightDataFile, String outputFile) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(flightDataFile));

        if (lines.isEmpty()) // ensure there is flight data to read
        {
            Files.writeString(Paths.get(outputFile), "No flight plan viable.");
            return false; // exit immediately if no flight data is present in the text file
        }

        for (int i = 1; i < lines.size(); i++) // iterate over all data rows
        {
            StringTokenizer rowData = new StringTokenizer(lines.get(i), "|"); // break the line split by the delimiter

            String source = rowData.nextToken(); // get the source city from token
            String destination = rowData.nextToken(); // get the destination city from token

            int flightCost = Integer.parseInt(rowData.nextToken()); // get the flight cost from token and convert to an Integer
            int flightDuration = Integer.parseInt(rowData.nextToken()); // get the flight duration from token and convert to an Integer
            addFlight(source, destination, flightCost, flightDuration); // add the flight with the given data
        }
        return true; // return true if there is data present in the text file
    }

    /* findCity
    This method finds a given city in cityList then returns it.

    Parameter:
    cityName - The name of the city to find.
     */
    private CityNode findCity(String cityName)
    {
        for (CityNode city : cityList) // iterate over all cities inside cityList
        {
            if (city.name.equals(cityName)) // if we find the city then return it
                return city;
        }
        return null; // if the city is not in cityList
    }

    /* addFlight
    This method sets the flight details for both the source city and destination city.

    Parameters:
    source - The source city.
    destination - The destination city.
    flightCost - The flight cost.
    flightDuration - The flight duration.
     */
    public void addFlight(String source, String destination, int flightCost, int flightDuration)
    {
        CityNode sourceCity = findCity(source); // find a source city
        if (sourceCity == null) // checking if we don't have a source city yet
        {
            sourceCity = new CityNode(source); // create a new node for the city
            cityList.add(sourceCity); // add the source city to the list of cities
        }
        sourceCity.addPath(destination, flightCost, flightDuration); // add a path to the destination city and set the flight cost and duration of that path

        CityNode destinationCity = findCity(destination); // find a destination city
        if (destinationCity == null) // checking if we don't have a destination city yet
        {
            destinationCity = new CityNode(destination); // create a new node for the city
            cityList.add(destinationCity); // add the destination city to the list of cities
        }
        destinationCity.addPath(source, flightCost, flightDuration); // add a path back to the source city and set the flight cost and duration of that path
    }

    /* readFlightPlan
    This method reads in the requested flight plan text file, process the data rows, perform DFS on each request, and write to the output file.

    Parameters:
    requestedFlightPlanFile - The requested flight plan file to read.
    outputFile - The output file.
     */
    public void readFlightRequest(String requestFlightPlanFile, String outputFile) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(requestFlightPlanFile)); // read all lines from the text file
        List<String> output = new ArrayList<>(); // keep an array list of strings for the output
        Path outputPath = Paths.get(outputFile); // get the path of the output text file

        if (lines.isEmpty()) // ensure there is flight data to read
        {
            output.add("No flight plan viable.");
            Files.write(outputPath, output);
            return; // exit immediately if no data in requested plan is present
        }

        for (int i = 1; i < lines.size(); i++) // iterate over all data rows
        {
            StringTokenizer st = new StringTokenizer(lines.get(i), "|"); // break the line split by the delimiter
            String source = st.nextToken(); // get the source city from token
            String destination = st.nextToken(); // get the destination city from token
            char sortPref = st.nextToken().charAt(0); // get the sorting preference from the token by only looking at the first character

            LinkedList<PathNode> paths = performDFS(source, destination, sortPref); // call the DFS algorithm

            output.add(String.format("Flight %d: %s, %s (%s)", i, source, destination, sortPref == 'T' ? "Time" : "Cost"));
            
            if (paths.isEmpty())
                output.add(String.format("No direct path from %s to %s.", source, destination));

            for (int j = 0; j < Math.min(3, paths.size()); j++) // we're going to process the three most efficient flight paths
            {
                PathNode path = paths.get(j); // get the path
                output.add(String.format("Path %d: %s. Time: %d Cost: %.2f", j + 1, path.toString(), path.duration, path.cost));
            }
            output.add("");
        }
        Files.write(outputPath, output); // write to the output file
    }

    /* isVisited
    The purpose of this method is to check if the current city has been visited yet or not.

    Parameters:
    City - The city to check.
    Current - The current city in the path that is being traversed by DFS.
     */
    private boolean cityVisited(String city, PathNode current)
    {
        while (current != null) // traverse back from the current node to the start node
        {
            if (current.city.equals(city)) // check if the current node (city) matches the city we're checking
                return true; // set the current node to: visited already
            current = current.previousCity; // set to the previous node to check
        }
        return false; // set to not visited if none of the nodes being traversed has been visited
    }

    /* performDFS
    This method performs depth-first search to find the best paths between cities.

    Parameters:
    source - The source city.
    destination - The destination city.
    preference - Preference on how the result should be sorted.
     */
    public LinkedList<PathNode> performDFS(String source, String destination, char sortPref)
    {
        LinkedList<PathNode> results = new LinkedList<>(); // initialize a linked list: this stores the path from source city to destination city
        Stack<PathNode> stack = new Stack<>(); // initialize a stack: DFS will keep track of which node (city) to explore
        CityNode sourceCity = findCity(source); // get a start city

        if (sourceCity != null) // ensure the start city is valid
        {
            stack.push(new PathNode(source, 0, 0, null)); // push the starting point onto the stack

            while (!stack.isEmpty()) // loop until all nodes have been visited
            {
                PathNode current = stack.pop(); // pop the top node from the stack: represents current location during DFS

                if (current.city.equals(destination)) // checking if the current node is the destination city
                {
                    results.add(current); // add the destination city to result
                    continue;
                }

                CityNode currentCity = findCity(current.city); // get the current city node
                if (currentCity != null) // check that the city exists
                {
                    for (CityPath path : currentCity.path) // iterate through all the paths possible from the given city
                    {
                        if (!cityVisited(path.destination, current)) // ensure the destination city at the end of the path has not been visited yet
                            stack.push(new PathNode(path.destination, current.cost + path.cost, current.duration + path.duration, current)); // push onto stack: update flight cost and flight duration
                    }
                }
            }
            results.sort(new SortPath(sortPref)); // sort the path based on the sorting preference given
        }
        return results; // return the all the possible paths
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 3) // ensuring there are 3 arguments passed
        {
            System.out.println("Usage: <flightDataFile> <requestedFlightPlanFile> <outputFile>");
            return;
        }

        // accepting command line arguments
        String flightDataFile = args[0];
        String requestedFlightPlanFile = args[1];
        String outputFile = args[2];

        FlightPlanner flightPlanner = new FlightPlanner();

        if (flightPlanner.readFlightData(flightDataFile, outputFile)) // ensure that there is flight data to read, otherwise don't proceed processing
            flightPlanner.readFlightRequest(requestedFlightPlanFile, outputFile);
    }
}