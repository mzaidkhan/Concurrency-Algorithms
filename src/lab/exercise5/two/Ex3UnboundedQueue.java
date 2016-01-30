package lab.exercise5.two;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread {
	// an integer that must be shared by all threads
	public final static AtomicInteger mySyncPoint = new AtomicInteger(0);
	public static int numberOfThreads;

	@Override
	public void run() {
		// The barrier
		mySyncPoint.addAndGet(1);
		while (mySyncPoint.get() < numberOfThreads) {
		}
		;
	}
}

// Queue element adding thread
class EnqThread extends MyThread {

	private int id;
	private int loop;
	private UnboundedQueue<Integer> queue;
	private Random ran = new Random();

	public EnqThread(int id, int loop, UnboundedQueue<Integer> queue) {
		this.id = id;
		this.loop = loop;
		this.queue = queue;
	}

	@Override
	public void run() {
		super.run();
		for (int i = 1; i <= loop; i++) {
			Integer item = new Integer(ran.nextInt(100));
			String msg = "Thread id : " + this.id + ", ";
			queue.enq(item, msg);
		}
	}

}

// Queue element remover thread
class DeqThread extends MyThread {

	private int id;
	private int loop;
	private UnboundedQueue<Integer> queue;

	public DeqThread(int id, int loop, UnboundedQueue<Integer> queue) {
		this.id = id;
		this.loop = loop;
		this.queue = queue;
	}

	@Override
	public void run() {
		super.run();
		for (int i = 1; i <= loop; i++) {
			String msg = "Thread id : " + this.id + ", ";
			try {
				queue.deq(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}

public class Ex3UnboundedQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {

			UnboundedQueue<Integer> queue = new UnboundedQueue<Integer>();
			List<Thread> addThreads = new ArrayList<Thread>();
			List<Thread> remThreads = new ArrayList<Thread>();
			int loopRun = 100000;

			int n;
			try {
				n = Integer.valueOf(args[0]);
				MyThread.numberOfThreads = n;
				int loop = loopRun / n;
				if (!(n % 2 == 0)) {
					throw new Exception("No of threads must be even!!!");
				}

				for (int i = 1; i <= n / 2; i++) {
					Thread t = new EnqThread(i, loop, queue);
					addThreads.add(t);
				}

				for (int i = 1; i <= n / 2; i++) {
					Thread t = new DeqThread(n / 2 + i, loop, queue);
					remThreads.add(t);
				}

				long startTime = System.nanoTime();

				for (Thread thread : addThreads) {
					thread.start();
				}

				for (Thread thread : remThreads) {
					thread.start();
				}

				for (Thread thread : addThreads) {
					thread.join();
				}

				for (Thread thread : remThreads) {
					thread.join();
				}

				long endTime = System.nanoTime();
				long totalTime = endTime - startTime;
				System.out.println("Execution time = " + totalTime
						+ " nanoseconds");
			} catch (Exception e) {
				System.out
						.println("Please provide one even numeric input parameters.");
			}

		} else {
			System.out
					.println("Please provide the number of threads as a numeric parameter.");
		}
	}
}
