package lab.exercise5.two;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Lock-free queue
 * 
 * @param T
 *            element type
 * @author Mohammed Zaid Khan
 */
public class LockFreeQueue<T> {
	/**
	 * Sentinel head and tail references
	 */
	private AtomicReference<Node> head;
	private AtomicReference<Node> tail;

	/**
	 * Constructor
	 */
	public LockFreeQueue() {
		Node sentinel = new Node(null);
		this.head = new AtomicReference<Node>(sentinel);
		this.tail = new AtomicReference<Node>(sentinel);
	}

	/**
	 * Add element to end of queue.
	 * 
	 * @param item 
	 * @param msg
	 *            to print
	 */
	public void enq(T item, String msg) {
		if (item == null) {
			throw new NullPointerException("Don't add null value to queue!");
		}
		Node node = new Node(item);
		while (true) {
			Node last = tail.get();
			Node next = last.next.get();
			if (last == tail.get()) {
				if (next == null) {
					if (last.next.compareAndSet(next, node)) {
						tail.compareAndSet(last, node);
						System.out.println(msg + "Adding : " + node.value);
						return;
					}
				} else {
					tail.compareAndSet(last, next);
				}
			}
		}
	}

	/**
	 * Remove and return head of queue.
	 * @param msg
	 *            to print
	 * @return remove first element in queue
	 * @throws Exception
	 */
	public T deq(String msg) throws Exception {
		while (true) {
			Node first = head.get();
			Node last = tail.get();
			Node next = first.next.get();
			if (first == head.get()) {
				if (first == last) {
					if (next == null) {
						throw new Exception("Queue is empty!");
					}
					tail.compareAndSet(last, next);
				} else {
					T value = next.value;
					if (head.compareAndSet(first, next)){
						System.out.println(msg + "Removing : " + value);
						return value;
					}
					
				}
			}
		}
	}

	/**
	 * Individual queue element.
	 */
	public class Node {
		/**
		 * Actual value of queue element.
		 */
		public T value;
		/**
		 * next element in queue
		 */
		public AtomicReference<Node> next;

		/**
		 * Constructor
		 * @param value
		 */
		public Node(T value) {
			this.value = value;
			this.next = new AtomicReference<Node>(null);
		}
	}

}
