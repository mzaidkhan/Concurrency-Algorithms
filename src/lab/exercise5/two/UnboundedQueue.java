package lab.exercise5.two;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Unbounded lock based queue
 * 
 * @param T
 *            item type
 * @author Mohammed Zaid Khan
 */
public class UnboundedQueue<T> {

	/**
	 * Lock enqueuers and dequeuers
	 */
	ReentrantLock enqLock, deqLock;

	/**
	 * First node in queue.
	 */
	Node head;

	/**
	 * Last node in queue.
	 */
	Node tail;

	/**
	 * Constructor.
	 */
	public UnboundedQueue() {
		head = new Node(null);
		tail = head;
		enqLock = new ReentrantLock();
		deqLock = new ReentrantLock();
	}

	/**
	 * @param msg to print
	 * 
	 * @return remove first element from queue
	 * 
	 */
	public T deq(String msg) throws Exception {
		T result;
		deqLock.lock();
		try {
			if (head.next == null) {
				throw new Exception(msg +"Queue Empty!");
			}
			result = head.next.value;
			head = head.next;
			System.out.println(msg + "Removing : " + result);
		} finally {
			deqLock.unlock();
		}
		return result;
	}

	/**
	 * Add element to end of queue.
	 * 
	 * @param value
	 *            to add
	 * @param msg
	 *            to print
	 */
	public void enq(T value, String msg) {
		if (value == null) {
			throw new NullPointerException("Don't add null value to queue!");
		}
		enqLock.lock();
		try {
			Node newNode = new Node(value);
			tail.next = newNode;
			tail = newNode;
			System.out.println(msg + "Adding : " + newNode.value);
		} finally {
			enqLock.unlock();
		}
	}

	/**
	 * Individual queue element.
	 */
	protected class Node {
		/**
		 * Actual value of queue element.
		 */
		public T value;
		/**
		 * next element in queue
		 */
		public Node next;

		/**
		 * Constructor
		 * 
		 * @param value
		 *            Value of element.
		 */
		public Node(T value) {
			this.value = value;
			next = null;
		}
	}
}
