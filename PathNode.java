public class PathNode
{
    String city; // name of the city
    float cost; // flight cost
    int duration; // flight duration
    PathNode previousCity; // this keeps track of the previous city from the current city

    PathNode(String city, float totalCost, int totalTime, PathNode previousCity)
    {
        this.city = city;
        this.cost = totalCost;
        this.duration = totalTime;
        this.previousCity = previousCity;
    }

    @Override
    public String toString()
    {
        if (previousCity == null) // if the current city is the starting city (no previous city)
            return city; // return the current city
        return previousCity + " -> " + city;
    }
}