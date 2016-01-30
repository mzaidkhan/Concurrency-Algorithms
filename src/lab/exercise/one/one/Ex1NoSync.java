package lab.exercise.one.one;

import java.util.ArrayList;
import java.util.List;

// Shared resource
class Counter
{
	private int val;

	public Counter()
	{
		val = 0;
	}

	public  void increment()
	{
		val = val + 1;   
	} 

	public  void  decrement()
	{
		val = val - 1;
	}

	public int getVal()
	{
		return val;
	}

}

//Increment thread
class IncThread extends Thread
{
	private Counter myCounter;

	public IncThread(Counter c)
	{
		myCounter = c;
	}   

	public void run()
	{
		for(int i = 1; i <= 100000; i++)
		{
			myCounter.increment();
		}       		
	}

}

//Decrement Thread
class DecThread extends Thread
{
	private Counter myCounter;


	public DecThread(Counter c)
	{
		myCounter = c;
	}

	public void run()
	{
		for(int i = 1; i <= 100000; i++)
		{
			myCounter.decrement();
		}		
	}
}

public class Ex1NoSync
{
	public static void main(String[] args)
	{        
		if(args.length == 2 )
		{
			List<Thread> incThreads = new ArrayList<Thread>();
			List<Thread> decThreads = new ArrayList<Thread>();
			int n,m;
			try
			{
				n = Integer.valueOf(args[0]);
				m = Integer.valueOf(args[1]);
				Counter c = new Counter();

				for(int i = 0; i<n; i++)
				{
					Thread t = new IncThread(c);
					incThreads.add(t);
				}

				for(int i = 0; i< m; i++)
				{
					Thread t = new DecThread(c);
					decThreads.add(t);
				}

				long startTime = System.nanoTime();
				
				for (Thread thread : incThreads) {
					thread.start();
				}

				for (Thread thread : decThreads) {
					thread.start();
				}
				
				for(Thread thread : incThreads) {	
					thread.join();
				}

				for(Thread thread : decThreads) {
					thread.join();
				}

				long endTime   = System.nanoTime();
				System.out.println("Counter value = " + c.getVal());
				long totalTime = endTime - startTime;
				System.out.println("Execution time = " + totalTime + " nanoseconds");
			}
			catch(Exception e)
			{
				System.out.println("Please provide two numeric input parameters.");
			}

		}
		else
		{
			System.out.println("Please provide two numeric input parameters.");
		}
	}
}