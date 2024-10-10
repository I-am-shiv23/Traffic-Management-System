import java.util.*;

// --------- Graph Representation (City Road Network) ---------
class CityGraph {
    Map<Intersection, List<Road>> roadMap = new HashMap<>();

    // Add an intersection (node)
    public void addIntersection(Intersection intersection) {
        roadMap.putIfAbsent(intersection, new ArrayList<>());
    }

    // Add a road (edge) between two intersections
    public void addRoad(Intersection from, Intersection to, int travelTime) {
        roadMap.get(from).add(new Road(to, travelTime));
    }

    public List<Road> getRoadsFrom(Intersection intersection) {
        return roadMap.getOrDefault(intersection, new ArrayList<>());
    }
}

// Intersection class representing nodes
class Intersection {
    String name;

    public Intersection(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Intersection) {
            return this.name.equals(((Intersection) obj).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

// Road class representing edges
class Road {
    Intersection destination;
    int travelTime;

    public Road(Intersection destination, int travelTime) {
        this.destination = destination;
        this.travelTime = travelTime;
    }
}

// --------- Traffic Light Management (Queue) ---------
class TrafficSignal {
    Queue<Vehicle> vehicleQueue = new LinkedList<>();

    public void addVehicleToQueue(Vehicle vehicle) {
        vehicleQueue.add(vehicle);
    }

    public void passVehicle() {
        if (!vehicleQueue.isEmpty()) {
            Vehicle passed = vehicleQueue.poll();  // Vehicle passes the light
            System.out.println("Vehicle " + passed.licensePlate + " passed the signal.");
        }
    }
}

// Vehicle class for vehicle information
class Vehicle {
    String licensePlate;

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}

// --------- Road Vehicle Management (Linked List) ---------
class RoadSegment {
    LinkedList<Vehicle> vehiclesOnRoad = new LinkedList<>();

    public void addVehicle(Vehicle vehicle) {
        vehiclesOnRoad.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehiclesOnRoad.remove(vehicle);
    }

    public void displayVehicles() {
        for (Vehicle v : vehiclesOnRoad) {
            System.out.println(v.licensePlate + " is on the road.");
        }
    }
}

// --------- Route Calculation (Dynamic Programming) ---------
class RoutePlanner {

    // Dijkstra’s Algorithm to find the shortest path
    public int[] dijkstra(CityGraph graph, Intersection start, Map<Intersection, Integer> intersectionIndex) {
        int n = intersectionIndex.size();
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[intersectionIndex.get(start)] = 0;

        PriorityQueue<Intersection> pq = new PriorityQueue<>((a, b) -> distances[intersectionIndex.get(a)] - distances[intersectionIndex.get(b)]);
        pq.add(start);

        while (!pq.isEmpty()) {
            Intersection current = pq.poll();
            int currentIndex = intersectionIndex.get(current);

            for (Road road : graph.getRoadsFrom(current)) {
                int newDist = distances[currentIndex] + road.travelTime;
                int neighborIndex = intersectionIndex.get(road.destination);
                if (newDist < distances[neighborIndex]) {
                    distances[neighborIndex] = newDist;
                    pq.add(road.destination);
                }
            }
        }

        return distances;
    }
}

// --------- Backtracking for Alternative Routes (Stack) ---------
class BacktrackingRouteFinder {

    public List<Intersection> findAlternativeRoute(CityGraph graph, Intersection start, Intersection end, Set<Intersection> visited) {
        Stack<Intersection> stack = new Stack<>();
        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            Intersection current = stack.pop();
            if (current.equals(end)) {
                return buildRoute(stack);  // Helper function to build route
            }
            for (Road road : graph.getRoadsFrom(current)) {
                if (!visited.contains(road.destination)) {
                    stack.push(road.destination);
                    visited.add(road.destination);
                }
            }
        }
        return null;  // No route found
    }

    // Helper function to reconstruct the route from the stack
    private List<Intersection> buildRoute(Stack<Intersection> stack) {
        List<Intersection> route = new ArrayList<>(stack);
        Collections.reverse(route);
        return route;
    }
}

// --------- Traffic Control Hierarchy (Tree) ---------
class TrafficRegion {
    String name;
    List<TrafficRegion> subRegions = new ArrayList<>();

    public TrafficRegion(String name) {
        this.name = name;
    }

    public void addSubRegion(TrafficRegion region) {
        subRegions.add(region);
    }

    public void displaySubRegions() {
        for (TrafficRegion region : subRegions) {
            System.out.println(region.name);
        }
    }
}

// --------- Main Class: Traffic Management System ---------
public class TrafficManagementSystem {

    public static void main(String[] args) {
        // 1. Initialize Graph
        CityGraph cityGraph = new CityGraph();
        Intersection i1 = new Intersection("A");
        Intersection i2 = new Intersection("B");
        Intersection i3 = new Intersection("C");
        cityGraph.addIntersection(i1);
        cityGraph.addIntersection(i2);
        cityGraph.addIntersection(i3);

        cityGraph.addRoad(i1, i2, 5);  // Road from A to B takes 5 units of time
        cityGraph.addRoad(i2, i3, 10); // Road from B to C takes 10 units of time

        // 2. Initialize Vehicles and Traffic Signals (Queue)
        TrafficSignal signal = new TrafficSignal();
        Vehicle v1 = new Vehicle("KA-01-1234");
        Vehicle v2 = new Vehicle("KA-01-5678");
        signal.addVehicleToQueue(v1);
        signal.addVehicleToQueue(v2);
        signal.passVehicle(); // Vehicle 1 passes

        // 3. Initialize RoadSegment (LinkedList for vehicles on road)
        RoadSegment roadSegment = new RoadSegment();
        roadSegment.addVehicle(v1);
        roadSegment.addVehicle(v2);
        roadSegment.displayVehicles();

        // 4. Shortest Path Calculation (Dijkstra - Dynamic Programming)
        RoutePlanner routePlanner = new RoutePlanner();
        Map<Intersection, Integer> intersectionIndex = new HashMap<>();
        intersectionIndex.put(i1, 0);
        intersectionIndex.put(i2, 1);
        intersectionIndex.put(i3, 2);

        int[] distances = routePlanner.dijkstra(cityGraph, i1, intersectionIndex);
        System.out.println("Shortest distance from A to C: " + distances[intersectionIndex.get(i3)]);

        // 5. Backtracking for alternative route (Stack)
        BacktrackingRouteFinder backtrackingFinder = new BacktrackingRouteFinder();
        Set<Intersection> visited = new HashSet<>();
        List<Intersection> alternativeRoute = backtrackingFinder.findAlternativeRoute(cityGraph, i1, i3, visited);
        if (alternativeRoute != null) {
            System.out.println("Alternative Route found: " + alternativeRoute);
        } else {
            System.out.println("No alternative route found");
        }

        // 6. Traffic Control Hierarchy (Tree)
        TrafficRegion city = new TrafficRegion("City");
        TrafficRegion northZone = new TrafficRegion("North Zone");
        TrafficRegion southZone = new TrafficRegion("South Zone");

        city.addSubRegion(northZone);
        city.addSubRegion(southZone);
        city.displaySubRegions();
    }
}
