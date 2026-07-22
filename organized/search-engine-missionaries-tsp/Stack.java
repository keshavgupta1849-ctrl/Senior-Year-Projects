// Custom stack implementation using LIFO (Last-In-First-Out) principle
public class Stack<T> {
    private Array<T> elements;  // Use custom array for storage

    // Constructor to initialize empty stack
    public Stack() {
        elements = new Array<>();
    }

    // Push element onto top of stack
    public void push(T element) {
        elements.add(element);
    }

    // Remove and return top element from stack
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        T element = elements.get(elements.size() - 1);
        // Note: Custom array doesn't support remove, so we simulate it
        @SuppressWarnings("unchecked")
        Array<T> newElements = new Array<>(elements.size() - 1);
        for (int i = 0; i < elements.size() - 1; i++) {
            newElements.add(elements.get(i));
        }
        elements = newElements;
        return element;
    }

    // Return top element without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return elements.get(elements.size() - 1);
    }

    // Check if stack is empty
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    // Get number of elements in stack
    public int size() {
        return elements.size();
    }
}