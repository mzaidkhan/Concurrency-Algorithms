package lab.exercise4.one;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fine-grained locking for a list of integers
 * 
 */
public class FineGrainedLockedList implements IntegerSet {
	/**
	 * First list entry
	 */
	private Node head;

	/**
	 * Constructor
	 */
	public FineGrainedLockedList() {
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
		head.lock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.lock();
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (curr.key == key) {
					System.out.println(msg + "Adding " + item
							+ " : cannot add same item twice.");
					return false;
				}
				Node newNode = new Node(item);
				newNode.next = curr;
				pred.next = newNode;
				System.out.println(msg + "Adding : " + item);
				return true;
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock();
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
		Node pred = null, curr = null;
		int key = item.hashCode();
		head.lock();
		try {
			pred = head;
			curr = pred.next;
			curr.lock();
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (curr.key == key) {
					pred.next = curr.next;
					System.out.println(msg + "Removing : " + item);
					return true;
				}
				System.out.println(msg + "Removing " + item
						+ " : Item not found.");
				return false;
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock();
		}
	}

	/**
	 * Search the requested element.
	 * 
	 * @param item
	 *            element to remove
	 * @return true if element was present
	 */
	@Override
	public boolean contains(Integer item) {
		Node pred = null, curr = null;
		int key = item.hashCode();
		head.lock();
		try {
			pred = head;
			curr = pred.next;
			curr.lock();
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				return (curr.key == key);
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock();
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
		 * next Node in list
		 */
		Node next;
		/**
		 * Synchronize nodes
		 */
		Lock lock;

		/**
		 * Constructor for usual Node
		 * 
		 * @param item
		 *            element in list
		 */
		public Node(Integer item) {
			this.item = item;
			this.key = item.hashCode();
			this.lock = new ReentrantLock();
		}

		/**
		 * Constructor for sentinel Node
		 * 
		 * @param key
		 *            should be min or max int value
		 */
		public Node(int key) {
			this.item = null;
			this.key = key;
			this.lock = new ReentrantLock();
		}

		/**
		 * Lock Node
		 */
		public void lock() {
			lock.lock();
		}

		/**
		 * Unlock Node
		 */
		public void unlock() {
			lock.unlock();
		}

	}
}
