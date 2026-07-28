package Plot;

import Plot.City;
// Imports the City class from the same package.
// A Region has a capital() City, and City has distance(...) for computing tour length.

import java.util.*;
// Imports Java utility classes like List, ArrayList, etc.

public class Tour {
    // Declares a public class Tour. "Tour" represents an ordering of Regions in a cycle.

    private static class Node {
        // A private nested class used only inside Tour.
        // Each Node is one stop in the tour.

        Region value;
        // The Region stored in this node (e.g., a state/province/country).

        Node next;
        // Pointer to the next node in the cycle.

        Node prev;
        // Pointer to the previous node in the cycle.

        Node(Region value) { this.value = value; }
        // Constructor: creates a node holding a region.
    }

    private Node head;  // arbitrary node on the cycle
    // "head" is a reference to any node in the circular list (not necessarily "first").
    // Because it's a cycle, starting point is arbitrary.

    private int size;
    // Stores how many Regions are currently in the tour.

    public Tour(List<Region> initial) {
        // Constructor: builds a tour from an initial list of Regions.

        if (initial == null || initial.isEmpty())
            throw new IllegalArgumentException("Initial tour cannot be empty");
        // Guards against constructing an empty or null tour.

        for (Region r : initial) append(r);
        // Adds each region from the initial list to the tour,
        // linking them into a circular doubly-linked list.
    }

    public int size() { return this.size; }
    // Returns the number of regions in the tour.

    public List<Region> asList() {
        // Converts the circular list structure into a normal Java List in traversal order.

        List<Region> out = new ArrayList<>(this.size);
        // Create an ArrayList sized to fit all regions.

        if (this.head == null) return out;
        // If for some reason the tour is empty, return empty list.

        Node cur = this.head;
        // Start traversal at head.

        for (int i = 0; i < this.size; i++) {
            // Loop exactly size times so we don't circle forever.

            out.add(cur.value);
            // Add the region at the current node to the output list.

            cur = cur.next;
            // Move to the next node.
        }
        return out;
        // Return the ordered list of regions.
    }

    public double lengthKm() {
        // Computes the total length of the cyclic tour in kilometers.

        if (this.size <= 1) return 0.0;
        // A tour with 0 or 1 city has length 0.

        double sum = 0.0;
        // Accumulator for total distance.

        Node cur = this.head;
        // Start at head.

        for (int i = 0; i < this.size; i++) {
            // Visit each edge exactly once. Because it's a cycle,
            // we measure from cur -> cur.next.

            City a = cur.value.capital();
            // Get the capital city of the current region.

            City b = cur.next.value.capital();
            // Get the capital city of the next region.

            sum += a.distance(b);
            // Add distance between capitals to total tour length.

            cur = cur.next;
            // Advance.
        }
        return sum;
        // Return full cyclic distance (includes last->first because list is circular).
    }

    /**
     * Insert region r into the current cycle at the position that produces the
     * smallest increase in total tour length (cheapest insertion).
     */
    public void insertCheapest(Region r) {
        // This implements the "cheapest insertion" heuristic:
        // try inserting r between every consecutive pair (a,b) in the current cycle,
        // compute how much the total tour length increases,
        // and choose the insertion point with the smallest increase.

        if (this.head == null) {
            // If the tour is empty, just append.
            append(r);
            return;
        }
        if (this.size < 2) {
            // If tour has only 1 node, cheapest insertion is trivial—append it.
            append(r);
            return;
        }

        Node bestNode = null;  // insert between bestNode and bestNode.next
        // bestNode represents the node "a" in the edge (a -> b).
        // Inserting after bestNode means inserting between a and a.next.

        double bestIncrease = Double.POSITIVE_INFINITY;
        // Track the smallest increase found so far.

        Node cur = this.head;
        // Start at head.

        for (int i = 0; i < this.size; i++) {
            // Examine every edge (a -> b) once.

            Node a = cur;
            // Current node is a.

            Node b = cur.next;
            // Next node is b.

            double inc = increaseIfInsertedBetween(a.value, b.value, r);
            // Compute increase in tour length if we insert r between a and b.

            if (inc < bestIncrease) {
                // If this insertion is better (smaller increase), remember it.
                bestIncrease = inc;
                bestNode = a;
            }
            cur = cur.next;
            // Move along the cycle.
        }

        if (bestNode == null) {
            // Safety fallback: should not happen, but if it does, just append.
            append(r);
        } else {
            // Insert r between bestNode and bestNode.next.
            insertAfter(bestNode, r);
        }
    }

    // internal list operations

    private void append(Region r) {
        // Adds a new node to the end of the cycle (right before head).

        Node n = new Node(r);
        // Create node holding region r.

        if (this.head == null) {
            // If list is empty, n points to itself in both directions.
            n.next = n;
            n.prev = n;
            this.head = n;
        } else {
            Node tail = this.head.prev;
            // In a circular doubly-linked list, head.prev is the tail.

            // tail <-> n <-> head
            tail.next = n;
            // Link old tail forward to new node.

            n.prev = tail;
            // Link new node back to old tail.

            n.next = this.head;
            // Link new node forward to head.

            this.head.prev = n;
            // Link head back to new node (new node becomes new tail).
        }
        this.size++;
        // Increase the size counter.
    }

    private void insertAfter(Node node, Region r) {
        // Inserts a new node holding r immediately after "node".

        Node n = new Node(r);
        // New node.

        Node after = node.next;
        // The node that currently comes after 'node'.

        node.next = n;
        // node now points forward to the new node.

        n.prev = node;
        // new node points back to node.

        n.next = after;
        // new node points forward to the old "after".

        after.prev = n;
        // old "after" points back to new node.

        this.size++;
        // Size increases.
    }

    private static double increaseIfInsertedBetween(Region a, Region b, Region x) {
        // Computes how much tour length increases if we replace edge (a->b)
        // with two edges (a->x) and (x->b).

        City ca = a.capital();
        // Capital of region a.

        City cb = b.capital();
        // Capital of region b.

        City cx = x.capital();
        // Capital of region x (the region we want to insert).

        return ca.distance(cx) + cx.distance(cb) - ca.distance(cb);
        // Increase = new edges - old edge
        // = dist(a,x) + dist(x,b) - dist(a,b)
    }
}