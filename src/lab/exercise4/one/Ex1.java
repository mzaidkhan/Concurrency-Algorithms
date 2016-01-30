package lab.exercise4.one;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//List item adding thread
class ListAdderThread extends Thread {

	private int id;
	private int loop;
	private FineGrainedLockedList linkedList;
	private Random ran = new Random();

	public ListAdderThread(int id, int loop, FineGrainedLockedList linkedList) {
		this.id = id;
		this.loop = loop;
		this.linkedList = linkedList;
	}

	public void run() {
		for (int i = 1; i <= loop; i++) {
			Integer item = new Integer(ran.nextInt(100));
			String msg = "Thread id : " + this.id + ", ";
			linkedList.add(item, msg);
		}
	}

}

// List item remover thread
class ListRemoverThread extends Thread {

	private int id;
	private int loop;
	private FineGrainedLockedList linkedList;
	private Random ran = new Random();

	public ListRemoverThread(int id, int loop, FineGrainedLockedList linkedList) {
		this.id = id;
		this.loop = loop;
		this.linkedList = linkedList;
	}

	public void run() {
		for (int i = 1; i <= loop; i++) {
			Integer item = new Integer(ran.nextInt(100));
			String msg = "Thread id : " + this.id + ", ";
			linkedList.remove(item, msg);
		}

	}
}

public class Ex1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {

			FineGrainedLockedList linkedList = new FineGrainedLockedList();
			List<Thread> addThreads = new ArrayList<Thread>();
			List<Thread> remThreads = new ArrayList<Thread>();
			int loopRun = 100000;

			int n;
			try {
				n = Integer.valueOf(args[0]);
				int loop = loopRun / n;
				if (!(n % 2 == 0)) {
					throw new Exception("No of threads must be even!!!");
				}

				for (int i = 1; i <= n / 2; i++) {
					Thread t = new ListAdderThread(i, loop, linkedList);
					addThreads.add(t);
				}

				for (int i = 1; i <= n / 2; i++) {
					Thread t = new ListRemoverThread(n / 2 + i, loop,
							linkedList);
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
