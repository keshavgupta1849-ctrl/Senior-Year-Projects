// Custom queue implementation using FIFO (First-In-First-Out) principle
public class Queue<T> {
    private Array<T> elements;  // Use custom array for storage
    private int front;          // Index of front element

    // Constructor to initialize empty queue
    public Queue() {
        elements = new Array<>();
        front = 0;
    }

    // Add element to back of queue
    public void enqueue(T element) {
        elements.add(element);
    }

    // Remove and return front element from queue
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        T element = elements.get(front);
        front++;
        // Reset if queue becomes empty
        if (front >= elements.size()) {
            elements = new Array<>();
            front = 0;
        }
        return element;
    }

    // Return front element without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return elements.get(front);
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return front >= elements.size();
    }

    // Get number of elements in queue
    public int size() {
        return elements.size() - front;
    }
}