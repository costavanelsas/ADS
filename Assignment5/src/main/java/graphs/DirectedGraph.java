package graphs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Costa van Elsas
 * Directec graph class
 */
public class DirectedGraph<V extends Identifiable, E> {

    private final Map<String, V> vertices = new HashMap<>();
    private final Map<V, Map<V, E>> edges = new HashMap<>();

    /**
     * representation invariants:
     * 1.  the vertices map stores all vertices by their identifying id (which prevents duplicates)
     * 2.  the edges map stores all directed outgoing edges by their from-vertex and then in the nested map by their to-vertex
     * 3.  there can only be two directed edges between any two given vertices v1 and v2:
     * one from v1 to v2 in edges.get(v1).get(v2)
     * one from v2 to v1 in edges.get(v2).get(v1)
     * 4.  every vertex instance in the key-sets of edges shall also occur in the vertices map and visa versa
     **/

    public DirectedGraph() {
    }

    /**
     * get the vertexes
     *
     * @return values of the vertex
     */
    public Collection<V> getVertices() {
        return vertices.values();
    }

    /**
     * finds the vertex in the graph identified by the given id
     *
     * @param id
     * @return the vertex that matches the given id
     * null if none of the vertices matches the id
     */
    public V getVertexById(String id) {
        if (id == null) return null;

        return vertices.get(id);
    }

    /**
     * retrieves the collection of neighbour vertices that can be reached directly
     * via an out-going directed edge from 'fromVertex'
     *
     * @param fromVertex
     * @return null if fromVertex cannot be found in the graph
     * an empty collection if fromVertex has no neighbours
     */
    public Collection<V> getNeighbours(V fromVertex) {
        if (fromVertex == null) return null;

        return this.edges.get(fromVertex).keySet(); // retrieve the keyset of neigbour vertices of fromVertex
    }

    /**
     * Get the neighbours vertices
     *
     * @param fromVertexId
     * @return the neighbours of fromVertex
     */
    public Collection<V> getNeighbours(String fromVertexId) {
        return this.getNeighbours(this.getVertexById(fromVertexId));
    }

    /**
     * retrieves the collection of edges
     * which connects the 'fromVertex' with its neighbours
     * (only the out-going edges directed from 'fromVertex' towards a neighbour shall be included
     *
     * @param fromVertex
     * @return null if fromVertex cannot be found in the graph
     * an empty collection if fromVertex has no out-going edges
     */
    public Collection<E> getEdges(V fromVertex) {
        if (fromVertex == null) return null;

        return this.edges.get(fromVertex).values(); // returen the edges
    }

    /**
     * Get the edges from a vertex by ID
     *
     * @param fromId
     * @return Edges from vertex
     */
    public Collection<E> getEdges(String fromId) {
        return this.getEdges(this.getVertexById(fromId));
    }

    /**
     * Adds newVertex to the graph, if not yet present and in a way that maintains the representation invariants.
     * If a duplicate of newVertex (with the same id) already exists in the graph,
     * nothing will be added, and the existing duplicate will be kept and returned.
     *
     * @param newVertex
     * @return the duplicate of newVertex with the same id that already exists in the graph,
     * or newVertex itself if it has been added.
     */
    public V addOrGetVertex(V newVertex) {
        if (newVertex == null) return null;

        if (this.vertices.containsKey(newVertex.getId())) {
            return this.vertices.get(newVertex.getId()); // check if vertices contains the vertex ID, if yes return that ID
        } else {
            this.vertices.putIfAbsent(newVertex.getId(), newVertex); // if no vertex ID, return a new vertex
            return newVertex;
        }
    }


    /**
     * Adds a new, directed edge 'newEdge' from vertex 'fromVertex' to vertex 'toVertex'
     * Adds fromVertex or toVertex to the graph first if these don't exist yet
     * No change shall be made if a directed edge already exists between these vertices
     *
     * @param fromVertex the start vertex of the directed edge
     * @param toVertex   the target vertex of the directed edge
     * @param newEdge    the instance with edge information
     * @return whether the edge has been added successfully
     */
    public boolean addEdge(V fromVertex, V toVertex, E newEdge) {
        Map<V, E> map = new HashMap<>();

        addOrGetVertex(fromVertex); // try to add or get the vertices
        addOrGetVertex(toVertex);

        if (fromVertex == null || toVertex == null || newEdge == null) //checks if values are null
            return false;

        if (!this.edges.containsKey(fromVertex)) { // if the edges doesnt contain the fromVertex
            map.put(toVertex, newEdge); // add vertices to the map
            this.edges.put(fromVertex, map); // add the map to the edges datastructure
            return true;
        } else if (!this.edges.get(fromVertex).containsKey(toVertex)) { // if the fromvertex doesnt contain the toVertex
            this.edges.get(fromVertex).put(toVertex, newEdge); // add the edges to the edges datastructures
            return true;
        }

        return false; // if edges already contains the edges, don't add anything
    }

    /**
     * Adds a new, directed edge 'newEdge' from vertex with id=fromId to vertex with id=toId
     * No change shall be made if a directed edge already exists between these vertices
     * or if no vertices can be found with id=fromId or id=toId
     *
     * @param fromId  the id of the start vertex of the outgoing edge
     * @param toId    the id of the target vertex of the directed edge
     * @param newEdge the instance with edge information
     * @return whether the edge has been added successfully
     */
    public boolean addEdge(String fromId, String toId, E newEdge) {
        if (this.getVertexById(fromId) == null || this.getVertexById(toId) == null) // checks if the vertices are not null
            return false;

        return addEdge(getVertexById(fromId), getVertexById(toId), newEdge); // returns true if the edges are added
    }

    /**
     * Adds two directed edges: one from v1 to v2 and one from v2 to v1
     * both with the same edge information
     *
     * @param v1
     * @param v2
     * @param newEdge
     * @return whether both edges have been added
     */
    public boolean addConnection(V v1, V v2, E newEdge) {
        return this.addEdge(v1, v2, newEdge) && this.addEdge(v2, v1, newEdge);
    }

    /**
     * Adds two directed edges: one from id1 to id2 and one from id2 to id1
     * both with the same edge information
     *
     * @param id1
     * @param id2
     * @param newEdge
     * @return whether both edges have been added
     */
    public boolean addConnection(String id1, String id2, E newEdge) {
        return this.addEdge(id1, id2, newEdge) && this.addEdge(id2, id1, newEdge);
    }

    /**
     * retrieves the directed edge between 'fromVertex' and 'toVertex' from the graph, if any
     *
     * @param fromVertex the start vertex of the designated edge
     * @param toVertex   the end vertex of the designated edge
     * @return the designated directed edge that has been registered in the graph
     * returns null if no connection has been set up between these vertices in the specified direction
     */
    public E getEdge(V fromVertex, V toVertex) {
        if (fromVertex == null || toVertex == null) return null;

        return this.edges.get(fromVertex).get(toVertex); // retrieve the edge between vertices fromVertex and toVertex
    }

    /**
     * Gets the edges between vertices fromID and toID
     *
     * @param fromId
     * @param toId
     * @return the edges
     */
    public E getEdge(String fromId, String toId) {
        return this.getEdge(this.vertices.get(fromId), this.vertices.get(toId));
    }

    /**
     * @return the total number of vertices in the graph
     */
    public int getNumVertices() {
        return vertices.size();
    }

    /**
     * calculates and returns the total number of directed edges in the graph data structure
     *
     * @return the total number of edges in the graph
     */
    public int getNumEdges() {
        return this.edges.values().stream() // stream over edges
                .mapToInt(map -> map.values() // mapping to an integer value
                        .size()) // get size
                .sum(); // get the total of all the values (int)
    }

    /**
     * Remove vertices without any connection from the graph
     */
    public void removeUnconnectedVertices() {
        this.edges.entrySet().removeIf(e -> e.getValue().size() == 0);
        this.vertices.entrySet().removeIf(e -> !this.edges.containsKey(e.getValue()));
    }

    /**
     * represents a path of connected vertices and edges in the graph
     */
    public class DGPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are connected in the graph by a directed edge,
         * i.e. FOR ALL i: 0 < i < vertices.length: this.getEdge(vertices[i-1],vertices[i]) will provide edge information of the connection
         * 2. a path with one vertex has no edges
         * 3. a path without vertices is empty
         * totalWeight is a helper attribute to capture additional info from searches, not a fundamental property of a path
         * visited is a helper set to be able to track visited vertices in searches, not a fundamental property of a path
         **/

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            for (V v : this.vertices) {
                sb.append(separator + v.getId());
                separator = ", ";
            }
            sb.append(")");
            return sb.toString();
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() {
            return this.visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath depthFirstSearch(String startId, String targetId) {

        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        DGPath path = new DGPath();

        return depthFirstSearch(start, target, path);
    }

    /**
     * Uses a recursive depth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     *
     * @param current
     * @param target
     * @param path
     * @return the path or null when there is no path found
     */
    private DGPath depthFirstSearch(V current, V target, DGPath path) {
        if (path.visited.contains(current)) return null; // if the current is visited return null
        path.visited.add(current); //add the current to the visited nodes

        if (current.equals(target)) {
            path.vertices.addLast(current);
            return path;
        } // add the current to the end and return the path

        for (V neighbour : this.getNeighbours(current)) { // loop through the neigbours
            DGPath newPath = depthFirstSearch(neighbour, target, path); //recursivly search from each child neighbour
            if (newPath != null) {
                path.vertices.addFirst(current);//if the target was found via recursive search, add first and return the path
                return path;
            }
        }

        return null; // when no path is found
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the start vertex to the target vertex in the graph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath breadthFirstSearch(String startId, String targetId) {

        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.visited.add(start);

        // easy target
        if (start.equals(target)) {
            path.vertices.add(target);
            return path;
        }

        path.vertices.addLast(target);// add target the the last position in the path
        Queue<V> queue = new LinkedList<>(); //fifo queue
        Map<V, V> visitedFrom = new HashMap<>(); // map with the visited vcalues

        queue.offer(start);
        visitedFrom.put(start, null);

        V current = queue.poll(); // get the first element in the queue which is the start of the vertex

        while (current != null) { // loop until there are no items left in the queue
            for (V n : this.getNeighbours(current)) { // loop through each neighbour of current
                if (n.equals(target)) { // if its equal loop again and creath the path
                    while (current != null) {
                        path.vertices.addFirst(current); // add the value to the path
                        current = visitedFrom.get(current); // update current value
                    }
                    return path;
                } else if (!visitedFrom.containsKey(n)) { // check if the visitedFrom doesnt have the neigbour value
                    path.visited.add(n); // add neighbours to visited path
                    visitedFrom.put(n, current); // remember its predecessor
                    queue.offer(n); // enqueue the neighbour
                }
            }
            current = queue.poll(); // pick the next vertex
        }

        return null; // when no path is found
    }

    // helper class to build the spanning tree of visited vertices in dijkstra's shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class DSPNode implements Comparable<DSPNode> {
        protected V vertex;                // the graph vertex that is concerned with this DSPNode
        protected V fromVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex

        private DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(weightSumTo, dspv.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target
     * according to Dijkstra's algorithm of a minimum spanning tree
     *
     * @param startId      id of the start vertex of the search
     * @param targetId     id of the target vertex of the search
     * @param weightMapper provides a function, by which the weight of an edge can be retrieved or calculated
     * @return the shortest path from start to target
     * returns null if either start or target cannot be matched with a vertex in the graph
     * or no path can be found from start to target
     */
    public DGPath dijkstraShortestPath(String startId, String targetId,
                                       Function<E, Double> weightMapper) {

        V start = getVertexById(startId);
        V target = getVertexById(targetId);
        if (start == null || target == null) return null;

        // initialise the result path of the search
        DGPath path = new DGPath();
        path.visited.add(start);

        // easy target
        if (start.equals(target)) {
            path.vertices.add(start);
            return path;
        }

        // keep track of the DSP status of all visited nodes
        // you may choose a different approach of tracking progress of the algorithm, if you wish
        Map<V, DSPNode> progressData = new HashMap<>();

        // initialise the progress of the start node
        DSPNode nextDspNode = new DSPNode(start);
        nextDspNode.weightSumTo = 0.0;
        progressData.put(start, nextDspNode);

        while (nextDspNode != null) {

            V currentVertex = nextDspNode.vertex; // get the current vertext

            if (currentVertex.equals(target)) { // if current is equal to the target
                path.totalWeight = (progressData.get(target).weightSumTo); // total weight is the target sum weight
                DSPNode newNode = nextDspNode;

                while (newNode != null) {
                    path.vertices.push(newNode.vertex); // add node vertex to the vertices
                    newNode = progressData.get(newNode.fromVertex); // get the new node from vertex
                }

                return path;
            }

            for (V n : getNeighbours(currentVertex)) { // loop through the neigbours

                double distance = 0.0;
                if (weightMapper != null)
                    distance = weightMapper.apply(getEdge(currentVertex, n)); // apply edges to the weighmapper

                double vertexDistance = nextDspNode.weightSumTo + distance; // vertex distance

                // Check if neigbour n is not un progressData or the vDistance smaller then the n sum weight
                if (!progressData.containsKey(n) || vertexDistance < progressData.get(n).weightSumTo) {
                    DSPNode nNode = new DSPNode(n); //new dsp node

                    nNode.fromVertex = currentVertex; // from becomes the current
                    nNode.weightSumTo = vertexDistance; // total weight sum is the vDistance

                    progressData.put(n, nNode); // put the neigbour and the new node in the progress data
                    path.getVisited().add(n); // add to the visited nodes
                }
            }

            nextDspNode.marked = true; // mark the node

            // find the next nearest node that is not marked yet
            nextDspNode = progressData.values().stream() // stream over progress data
                    .filter(node -> !node.marked) // filter marked nodes
                    .min(DSPNode::compareTo) // comopare nodes
                    .orElse(null); // else return nulkl
        }

        // no path found, graph was not connected ???
        return null;
    }


    @Override
    public String toString() {
        return this.getVertices().stream()
                .map(v -> v.toString() + ": " +
                        this.edges.get(v).entrySet().stream()
                                .map(e -> e.getKey().toString() + "(" + e.getValue().toString() + ")")
                                .collect(Collectors.joining(",", "[", "]"))
                )
                .collect(Collectors.joining(",\n  ", "{ ", "\n}"));
    }
}
