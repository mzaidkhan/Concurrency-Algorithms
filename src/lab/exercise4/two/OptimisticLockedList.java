package lab.exercise4.two;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Optimistic List locking.
 * 
 */
public class OptimisticLockedList implements IntegerSet {
	/**
	 * First list node
	 */
	private Node head;

	/**
	 * Constructor
	 */
	public OptimisticLockedList() {
		head = new Node(Integer.MIN_VALUE);
		head.next = new Node(Integer.MAX_VALUE);
	}

	/**
	 * Add an element.
	 * 
	 * @param item
	 *            element to add
	 * @param msg
	 * @return true if element was not there already
	 */
	@Override
	public boolean add(Integer item, String msg) {
		int key = item.hashCode();
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			while (curr.key <= key) {
				pred = curr;
				curr = curr.next;
			}
			pred.lock();
			curr.lock();
			try {
				if (validate(pred, curr)) {
					if (curr.key == key) {
						System.out.println(msg + "Adding " + item
								+ " : cannot add same item twice.");
						return false;
					} else {
						Node entry = new Node(item);
						entry.next = curr;
						pred.next = entry;
						System.out.println(msg + "Adding : " + item);
						return true;
					}
				}
			} finally {
				pred.unlock();
				curr.unlock();
			}
		}
	}

	/**
	 * Remove an element.
	 * 
	 * @param item
	 *            element to remove
	 * @param msg
	 * @return true if element was present
	 */
	@Override
	public boolean remove(Integer item, String msg) {
		int key = item.hashCode();
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			while (curr.key < key) {
				pred = curr;
				curr = curr.next;
			}
			pred.lock();
			curr.lock();
			try {
				if (validate(pred, curr)) {
					if (curr.key == key) {
						pred.next = curr.next;
						System.out.println(msg + "Removing : " + item);
						return true;
					} else {
						System.out.println(msg + "Removing " + item
								+ " : Item not found.");
						return false;
					}
				}
			} finally {
				pred.unlock();
				curr.unlock();
			}
		}
	}

	/**
	 * Check that prev and curr are still in list and adjacent
	 * 
	 * @param pred
	 *            predecessor node
	 * @param curr
	 *            current node
	 * @return boolean whether predecessor and current have changed
	 */
	private boolean validate(Node pred, Node curr) {
		Node entry = head;
		while (entry.key <= pred.key) {
			if (entry == pred)
				return pred.next == curr;
			entry = entry.next;
		}
		return false;
	}

	/**
	 * Test whether element is present
	 * 
	 * @param item
	 *            element to test
	 * @return true if element is present
	 */
	@Override
	public boolean contains(Integer item) {
		int key = item.hashCode();
		while (true) {
			Node pred = this.head;
			Node curr = pred.next;
			while (curr.key < key) {
				pred = curr;
				curr = curr.next;
			}
			try {
				pred.lock();
				curr.lock();
				if (validate(pred, curr)) {
					return (curr.key == key);
				}
			} finally {
				pred.unlock();
				curr.unlock();
			}
		}
	}

	/**
	 * list Node
	 */
	private class Node {
		/**
		 * actual item
		 */
		Integer item;
		/**
		 * item's hash code
		 */
		int key;
		/**
		 * next node in list
		 */
		Node next;
		/**
		 * Synchronizes node.
		 */
		Lock lock;

		/**
		 * Constructor for usual node
		 * 
		 * @param item
		 *            element in list
		 */
		Node(Integer item) {
			this.item = item;
			this.key = item.hashCode();
			lock = new ReentrantLock();
		}

		/**
		 * Constructor for sentinel node
		 * 
		 * @param key
		 *            should be min or max int value
		 */
		Node(int key) {
			this.key = key;
			lock = new ReentrantLock();
		}

		/**
		 * Lock node
		 */
		void lock() {
			lock.lock();
		}

		/**
		 * Unlock node
		 */
		void unlock() {
			lock.unlock();
		}
	}
}