package io.smartcat.ranger.load.generator.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Blocking queue with eviction policy to evict elements if put is attempted when queue is full. Depending on
 * <code>dropFromHead</code> flag, can drop either from head or from tail. Implementation taken from
 * {@link java.util.concurrent.LinkedBlockngDeque} and adjusted.
 *
 * @param <T> Type of elements held in this collection.
 */
public class LinkedEvictingBlockingQueue<T> {

    private final boolean dropFromHead;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    private transient int count;
    private transient Node<T> head;
    private transient Node<T> tail;

    /**
     * Constructs evicting blocking queue. <code>dropFromHead</code> is set to true and <code>capacity</code> is set to
     * {@code Integer.MAX_VALUE}.
     */
    public LinkedEvictingBlockingQueue() {
        this(true);
    }

    /**
     * Constructs evicting blocking queue with specified <code>dropFromHead</code> flag. <code>capacity</code> is set to
     * {@code Integer.MAX_VALUE}.
     *
     * @param dropFromHead Flag to determine whether element from head or from tail will be dropped when put is invoked
     *            on full queue.
     */
    public LinkedEvictingBlockingQueue(boolean dropFromHead) {
        this(dropFromHead, Integer.MAX_VALUE);
    }

    /**
     * Constructs evicting blocking queue with specified <code>dropFromHead</code> flag and fixed <code>capacity</code>.
     *
     * @param dropFromHead Flag to determine whether element from head or from tail will be dropped when put is invoked
     *            on full queue.
     * @param capacity Capacity of this queue, must be positive number.
     */
    public LinkedEvictingBlockingQueue(boolean dropFromHead, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive number.");
        }
        this.dropFromHead = dropFromHead;
        this.capacity = capacity;
    }

    /**
     * Inserts the specified element at the tail of this queue. If queue if full, element is dropped and then specified
     * element is put. Depending on <code>dropFromHead</code> either element from head or from tail will be dropped from
     * the queue.
     *
     * @param e Element to put in this queue.
     * @return Element that was dropped, or null if no element was dropped.
     */
    public T put(T e) {
        Node<T> node = new Node<T>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            T droppedNode = null;
            if (count >= capacity) {
                droppedNode = dropNode();
            }
            linkToTail(node);
            return droppedNode;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
     *
     * @return The read of this queue.
     * @throws InterruptedException If interrupted while waiting.
     */
    public T take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            T x;
            while ((x = unlinkFromHead()) == null)
                notEmpty.await();
            return x;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue
     */
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    private T dropNode() {
        if (dropFromHead) {
            return unlinkFromHead();
        } else {
            return unlinkFromTail();
        }
    }

    /**
     * Links node at tail, or returns false if full.
     */
    private boolean linkToTail(Node<T> node) {
        // assert lock.isHeldByCurrentThread();
        if (count >= capacity)
            return false;
        Node<T> l = tail;
        node.prev = l;
        tail = node;
        if (head == null)
            head = node;
        else
            l.next = node;
        ++count;
        notEmpty.signal();
        return true;
    }

    /**
     * Removes and returns element from head, or null if empty.
     */
    private T unlinkFromHead() {
        Node<T> f = head;
        if (f == null)
            return null;
        Node<T> n = f.next;
        T item = f.item;
        f.item = null;
        f.next = f; // help GC
        head = n;
        if (n == null)
            tail = null;
        else
            n.prev = null;
        --count;
        return item;
    }

    /**
     * Removes and returns element from tail, or null if empty.
     */
    private T unlinkFromTail() {
        Node<T> l = tail;
        if (l == null)
            return null;
        Node<T> p = l.prev;
        T item = l.item;
        l.item = null;
        l.prev = l; // help GC
        tail = p;
        if (p == null)
            head = null;
        else
            p.next = null;
        --count;
        return item;
    }

    /**
     * Doubly-linked list node class.
     *
     * @param <E> Type of node.
     */
    private static class Node<E> {

        E item;
        Node<E> prev;
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }
}
