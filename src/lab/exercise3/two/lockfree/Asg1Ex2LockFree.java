package lab.exercise3.two.lockfree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Enqueuer thread
class EnqThread extends Thread {
	private static int value = 1;
	private MyQueue queue;
	// Inserting random values to the queue
	final Random ran = new Random();

	public EnqThread(MyQueue c) {
		queue = c;

	}

	public void run() {
		for (int i = 1; i <= 100000; i++) {
			queue.enq(value++);
			/*
			 * System.out.println("Queue status after enqueue");
			 * System.out.println("	-->Head: " + queue.head);
			 * System.out.println("	Tail<--: " + queue.tail);
			 */
		}
	}

}

// Dequeuer thread
class DeqThread extends Thread {
	private MyQueue queue;

	public DeqThread(MyQueue c) {
		queue = c;
	}

	public void run() {
		for (int i = 1; i <= 100000; i++) {
			int value = queue.deq();
			/*
			 * System.out.println("Queue status after dequeue");
			 * System.out.println("	-->Head: " + queue.head);
			 * System.out.println("	Tail<--: " + queue.tail);
 			 * System.out.println("Dequeued value: "+ value);*/
		}
	}
}

public class Asg1Ex2LockFree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {

			List<Thread> enqThreads = new ArrayList<Thread>();
			List<Thread> deqThreads = new ArrayList<Thread>();
			int n;
			try {
				n = Integer.valueOf(args[0]);
				if (!(n % 2 == 0) || n != 2) {
					throw new Exception("No of threads must be two!!!");
				}
				MyQueue c = new MyQueue(100000 * n / 2);

				for (int i = 0; i < n / 2; i++) {
					Thread t = new EnqThread(c);
					enqThreads.add(t);
				}

				for (int i = 0; i < n / 2; i++) {
					Thread t = new DeqThread(c);
					deqThreads.add(t);
				}

				long startTime = System.nanoTime();

				for (Thread thread : enqThreads) {
					thread.start();
				}

				for (Thread thread : deqThreads) {
					thread.start();
				}

				for (Thread thread : enqThreads) {
					thread.join();
				}

				for (Thread thread : deqThreads) {
					thread.join();
				}

				long endTime = System.nanoTime();
				long totalTime = endTime - startTime;
				System.out.println("Queue size: " + c.items.length);
				System.out.println("	-->Head: " + c.head);
				System.out.println("	Tail<--: " + c.tail);
				System.out.println("Execution time = " + totalTime
						+ " nanoseconds");
			} catch (Exception e) {
				System.out
						.println("Please provide digit 2 as input parameters.");
			}

		} else {
			System.out
					.println("Please provide the number of threads as a numeric parameter.");
		}
	}

}
