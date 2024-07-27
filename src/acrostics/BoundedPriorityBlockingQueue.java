package acrostics;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityBlockingQueue<E extends Comparable<E>> {

    private final PriorityBlockingQueue<E> queue;
    private final int size;
    private final ReentrantLock lock;

    public BoundedPriorityBlockingQueue(int size) {
        this.size = size;
        this.queue = new PriorityBlockingQueue<>(size,
                (x, y) -> -x.compareTo(y));
        this.lock = new ReentrantLock();
    }

    public E peek() {
        ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try {
            result = queue.peek();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void offer(E e) {
        if (e == null)
            throw new NullPointerException();
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (queue.size() < this.size) {
                queue.offer(e);
            } else {
                if (queue.peek().compareTo(e) > 0) {
                    queue.poll();
                    queue.offer(e);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public E reversePull() {
        ReentrantLock lock = this.lock;
        lock.lock();
        E result;
        try {
            result = queue.poll();
        } finally {
            lock.unlock();
        }
        return result;
    }
}
