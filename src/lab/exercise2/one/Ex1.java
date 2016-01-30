package lab.exercise2.one;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//Shared resource without use of volatile
class Counter {
	private int val;

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

// Increment thread
class IncThread extends Thread {
	private int threadID;
	private Counter myCounter;
	private AccessCounter acsCounter;
	private Filter lock;

	public IncThread(Counter c, Filter lock, int ID, AccessCounter acsCounter) {
		myCounter = c;
		this.lock = lock;
		threadID = ID;
		this.acsCounter = acsCounter;
	}

	public void run() {
		boolean loopCondn = true;
		while (loopCondn) {
			lock.lock(threadID);
			try {
				if (myCounter.getVal() >= 300000) {
					loopCondn = false;
				} else {
					myCounter.increment();
					acsCounter.addAccessCount(threadID);
				}
			} finally {
				lock.unlock(threadID);
			}
		}
	}

}

public class Ex1 {
	final static boolean SET_SINGLE_PROC_AFF = false;

	public static void setSolarisAffinity() {
		try {
			// retrieve process id
			String pid_name = java.lang.management.ManagementFactory
					.getRuntimeMXBean().getName();
			String[] pid_array = pid_name.split("@");
			int pid = Integer.parseInt(pid_array[0]);
			// random processor
			int processor = new java.util.Random().nextInt(32);
			// Set process affinity to one processor (on Solaris )
			Process p = Runtime.getRuntime().exec(
					"/ usr / sbin / pbind -b " + processor + " " + pid);
			p.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(SET_SINGLE_PROC_AFF){
			setSolarisAffinity();
		}
		if (args.length == 1) {
			List<Thread> incThreads = new ArrayList<Thread>();
			int n;
			try {
				n = Integer.valueOf(args[0]);
				Filter lock = new Filter(n);
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

// Peterson's generalized algorithm
class Filter {
	private List<AtomicInteger> level = new ArrayList<AtomicInteger>();
	private List<AtomicInteger> victim = new ArrayList<AtomicInteger>();
	int N;

	public Filter(int N) {
		this.N = N;
		for (int i = 0; i < N; i++) {
			level.add(new AtomicInteger());
			victim.add(new AtomicInteger());
		}
	}

	void lock(int threadID) {
		for (int j = 1; j < N; j++) {
			level.get(threadID).set(j);
			victim.get(j).set(threadID);
			// wait while conflicts exist
			while (sameOrHigher(threadID, j) && victim.get(j).get() == threadID)
				;
		}
	}

	boolean sameOrHigher(int i, int j) {
		for (int k = 0; k < N; k++)
			if (k != i && level.get(k).get() >= j)
				return true;
		return false;
	}

	void unlock(int threadID) {
		level.get(threadID).set(0);
	}
}