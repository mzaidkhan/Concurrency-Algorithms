package lab.exercise3.one.b;

import java.util.ArrayList;
import java.util.List;

//Shared resource with use of volatile
class Counter {
	private volatile int val;

	public Counter() {
		val = 0;
	}

	public void increment() {
		val = val + 1;
	}

	public void decrement() {
		val = val - 1;
	}

	public int getVal() {
		return val;
	}

}

class AccessCounter {
	private int count[];

	public AccessCounter(int N) {
		count = new int[N];
	}

	public int[] getCount() {
		return count;
	}

	// Increment the access count by the associated threadID
	public void addAccessCount(int threadID) {
		count[threadID]++;
	}

}

//Increment thread
class IncThread extends Thread {
	private int threadID;
	private Counter myCounter;
	private AccessCounter acsCounter;
	private CCASLock lock;

	public IncThread(Counter c, CCASLock lock, int ID, AccessCounter acsCounter) {
		myCounter = c;
		this.lock = lock;
		threadID = ID;
		this.acsCounter = acsCounter;
	}

	public void run() {
		boolean loopCondn = true;
		while (loopCondn) {
			lock.lock();
			try {
				if (myCounter.getVal() >= 300000) {
					loopCondn = false;
				} else {
					acsCounter.addAccessCount(threadID);
					myCounter.increment();
				}
			} finally {
				lock.unlock();
			}
		}
	}
}

public class Asg1Ex1B {
	public static void main(String[] args) {
		if (args.length == 1) {
			List<Thread> incThreads = new ArrayList<Thread>();
			int n;
			try {
				n = Integer.valueOf(args[0]);
				CCASLock lock = new CCASLock();
				Counter c = new Counter();
				AccessCounter acsCounter = new AccessCounter(n);

				for (int i = 0; i < n; i++) {
					Thread t = new IncThread(c, lock, i, acsCounter);
					incThreads.add(t);
				}

				long startTime = System.nanoTime();

				for (Thread thread : incThreads) {
					thread.start();
				}

				for (Thread thread : incThreads) {
					thread.join();
				}
				long endTime = System.nanoTime();

				System.out.println("Counter value = " + c.getVal());

				int[] acsCount = acsCounter.getCount();
				int totalCount = 0;
				for (int i = 0; i < n; i++) {
					int countVal = acsCount[i];
					totalCount += countVal;
					System.out.println("Counter accessed " + countVal
							+ " by threadID " + i + "");
				}
				System.out.println("Total count accesses : " + totalCount);
				long totalTime = endTime - startTime;
				System.out.println("Execution time = " + totalTime
						+ " nanoseconds");
			} catch (Exception e) {
				System.out
						.println("Please provide the number of threads as a numeric parameter.");
			}

		} else {
			System.out
					.println("Please provide the number of threads as a numeric parameter.");
		}
	}
}