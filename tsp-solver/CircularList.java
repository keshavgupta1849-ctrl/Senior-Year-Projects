import java.util.Iterator;
import java.util.NoSuchElementException;

public class CircularList<T> implements Iterable<T> {

    // We implement the list as a circular doubly-linked list:
    // Each node has next and prev, and the last node points to the first node.
    // If the list is empty, head == null.

    private Node head;   // points to the "start" node (arbitrary in a circular list)
    private int size;    // number of items in the list

    private class Node {
        // A node stores one item and links to neighbors.
        T item;
        Node next;
        Node prev;

        // Simple constructor.
        Node(T item) {
            this.item = item;
            this.next = this;
            this.prev = this;
        }
    }

    public CircularList() {
        // Empty list.
        head = null;
        size = 0;
    }

    public CircularList(T item) {
        //Creates an empty list and uses the add method to add the item
        this();
        add(item);
    }

    public CircularList(T[] items) {
        //Creates an empty list using constructor then adds them
        this();
        if (items != null) {
            for (T x : items) {
                add(x);
            }
        }
    }

    public int size() {
        // Return number of items.
        return size;
    }

    public boolean isEmpty() {
        // Empty if size == 0.
        return size == 0;
    }

    public boolean contains(T item) {  // make independent from find, dont use iterator or cursor
        // True if we find at least one equal item.
        return !find(item).isNil();
    }

    public Cursor find(T item) { // Do nont use
        // Return a cursor to the first node whose item equals item.
        // If not found, return null cursor.
        if (head == null) return new Cursor(null);

        Node current = head;
        for (int i = 0; i < size; i++) {
            if (equalsItem(current.item, item)) {
                return new Cursor(current);
            }
            current = current.next;
        }
        return new Cursor(null);
    }

    public void add(T item) {
        // Append at the end, i want to keep head as the first inserted node unless the list is empty.
        if (head == null) {
            head = new Node(item);
            size = 1;
            return;
        }

        // tail is head.prev in a circular doubly-linked list
        Node tail = head.prev;

        Node n = new Node(item);

        // Link: tail <-> n <-> head
        n.prev = tail;
        n.next = head;
        tail.next = n;
        head.prev = n;

        size++;
    }

    public void remove(Cursor cursor) {
        // Remove the node at cursor.
        // If cursor is null or list empty: do nothing.
        if (cursor == null || cursor.node == null || head == null) return;
        Node target = cursor.node;
        // If the list has one node, removing it makes the list empty.
        if (size == 1) {
            head = null;
            size = 0;
            cursor.node = null; // invalidate cursor
            return;
        }

        // Unlink target from the ring.
        target.prev.next = target.next;
        target.next.prev = target.prev;

        // If we removed head, move head to a remaining node.
        if (target == head) {
            head = target.next;
        }

        size--;
        cursor.node = null; // invalidate cursor
    }

    public void insertBefore(Cursor cursor, T item) {
        // Insert item immediately before cursor position.
        // If cursor is nil or list empty, we just add (append).
        if (cursor == null || cursor.node == null || head == null) {
            add(item);
            return;
        }

        Node cur = cursor.node;
        Node before = cur.prev;

        Node n = new Node(item);

        // Link: before <-> n <-> cur
        n.prev = before;
        n.next = cur;
        before.next = n;
        cur.prev = n;

        // If we inserted before head, make new node the head
        if (cur == head) {
            head = n;
        }

        size++;
    }

    public void insertAfter(Cursor cursor, T item) {
        // Insert item immediately after cursor position.
        // If cursor is nil or list empty, we just add (append).
        if (cursor == null || cursor.node == null || head == null) {
            add(item);
            return;
        }

        Node cur = cursor.node;
        Node after = cur.next;

        Node n = new Node(item);

        // Link: cur <-> n <-> after
        n.prev = cur;
        n.next = after;
        cur.next = n;
        after.prev = n;

        size++;
    }

    public boolean equals(CircularList other) {
        // Two circular lists are equal if they contain the same elements
        // in the same order, up to rotation.
        if (other == null) return false;
        if (this.size != other.size) return false;

        // Two empty lists are equal.
        if (this.size == 0) return true;

        // We try to find a rotation alignment:
        // Find a node in 'other' that matches this.head.item,
        // then compare forward for 'size' steps.
        Node aStart = this.head;

        // Scan other list for matching starting points.
        Node b = other.head;
        for (int i = 0; i < other.size; i++) {
            if (equalsItem(aStart.item, b.item)) {
                // Check full match if we start at b.
                if (matchesFrom(aStart, b, other.size)) return true;
            }
            b = b.next;
        }

        return false;
    }

    @Override
    public String toString() {
        // Example: "[cat,dog,pig]"
        if (head == null) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node cur = head;
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(",");
            sb.append(cur.item);
            cur = cur.next;
        }

        sb.append("]");
        return sb.toString();
    }

    public class Cursor {

        // A representation of a position in the list.
        private Node node;

        public Cursor() {
            this(null);
        }

        private Cursor(Node node) {
            // Store node reference (may be null).
            this.node = node;
        }

        public boolean isNil() {
            // Nil cursor has no node.
            return node == null;
        }

        public T get() { // throw the no such element exception stuff
            // Return item at cursor.
            // If Nil, return null (simple behavior).
            if (node == null) return null;
            return node.item;
        }

        public void set(T item) {
            node.item = item;
        }

        public void swap(Cursor other) {
            T temp = this.node.item;
            this.node.item = other.node.item;
            other.node.item = temp;
        }

        public Cursor next() {
            return new Cursor(node.next);
        }

        public Cursor prev() {
            return new Cursor(node.prev);
        }

        public boolean equals(Cursor other) {
            // Two cursors equal if they point to same node reference.
            if (other == null) return false;
            return this.node == other.node;
        }
    }

    // Iterators
    @Override
    public Iterator<T> iterator() {
        // Default iterator starts at head.
        return new CircularListIterator();
    }

    public Iterator<T> iterator(Cursor start) {
        // Iterator starts at the given cursor.
        return new CircularListIterator(start);
    }

    private class CircularListIterator implements Iterator<T> { //fix to not have the remaining, but starting node, has started, and current or smth like that. The ramining thing does not work, + throw new elemenets.

        private Node current;   // next node to return
        private int remaining;  // how many items left to return

        public CircularListIterator(Cursor start) {
            // If list empty or start nul, produce empty iterator.
            if (head == null || start == null || start.node == null) {
                current = null;
                remaining = 0;
            } else {
                current = start.node;
                remaining = size; // we return each element exactly once
            }
        }

        public CircularListIterator() {
            // Start at head.
            if (head == null) {
                current = null;
                remaining = 0;
            } else {
                current = head;
                remaining = size; // we return each element exactly once
            }
        }

        @Override
        public boolean hasNext() {
            // We have next while we still have items remaining.
            return remaining > 0;
        }

        @Override
        public T next() {
            // Return next item or throw if none.
            if (!hasNext()) throw new NoSuchElementException();
            T value = current.item;
            current = current.next;   // wrap happens naturally in circular links
            remaining--;
            return value;
        }
    }

    // Helper methods

    private boolean equalsItem(T a, T b) {
        // Safe equality helper: handles nulls.
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private boolean matchesFrom(Node a, Node b, int count) {
        // Check if the next 'count' items match when walking forward from a and b.
        Node x = a;
        Node y = b;
        for (int i = 0; i < count; i++) {
            if (!equalsItem(x.item, y.item)) return false;
            x = x.next;
            y = y.next;
        }
        return true;
    }
}