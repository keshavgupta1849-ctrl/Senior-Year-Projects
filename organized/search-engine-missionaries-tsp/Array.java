// Custom array list implementation for storing collections of elements
public class Array<T> implements Iterable<T> {
    private T[] elements;  // Internal array to store elements
    private int size;      // Current number of elements in array

    // Constructor with initial capacity
    @SuppressWarnings("unchecked")
    public Array(int capacity) {
        elements = (T[]) new Object[capacity];
        size = 0;
    }

    // Default constructor with capacity 10
    public Array() {
        this(10);
    }

    // Add element to end of array
    public void add(T element) {
        // Resize array if full
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[size++] = element;
    }

    // Get element at specific index
    public T get(int index) {
        checkIndex(index);
        return elements[index];
    }

    // Set element at specific index
    public void set(int index, T element) {
        checkIndex(index);
        elements[index] = element;
    }

    // Get current number of elements
    public int size() {
        return size;
    }

    // Check if array is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Resize internal array to new capacity
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newElements = (T[]) new Object[newCapacity];
        // Copy elements to new array
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }
        elements = newElements;
    }

    // Check if index is valid
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    // Create iterator for foreach loops
    @Override
    public java.util.Iterator<T> iterator() {
        return new ArrayIterator();
    }

    // Iterator implementation for Array class
    private class ArrayIterator implements java.util.Iterator<T> {
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return elements[current++];
        }
    }
}