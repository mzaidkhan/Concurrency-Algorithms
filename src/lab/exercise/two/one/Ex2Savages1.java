package lab.exercise.two.one;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Ex2Savages1 {

	public static void main(String[] args) {
		if (args.length == 2) {
			int noOfMealsCookPutsInPot = Integer.valueOf(args[0]);
			int noOfSavages = Integer.valueOf(args[1]);
			Random ran = new Random();
			try {
				List<Savage> savages = new ArrayList<Savage>();
				ReentrantLock lock = new ReentrantLock();

				Cook cook = new Cook(noOfMealsCookPutsInPot);
				Pot pot = new Pot(cook.getCookingCapacity());

				for (int i = 1; i <= noOfSavages; i++) {
					Savage savage = new Savage(i, cook, pot, lock);
					savages.add(savage);
				}

				for (Savage savage : savages) {
					savage.start();
					try {
						Thread.sleep(1 + ran.nextInt(100));
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				System.out
						.println("Please provide two numeric input parameters. # of meals and # of Savages respectively.");
			}
		} else {
			System.out
					.println("Please provide two numeric input parameters. # of meals and # of Savages respectively.");
		}

	}
}

class Savage extends Thread {

	private int savageID;
	private Cook cook;
	private Pot pot;
	private ReentrantLock lock = new ReentrantLock();

	public Savage(int savageID, Cook cook, Pot pot, ReentrantLock lock) {
		this.savageID = savageID;
		this.cook = cook;
		this.lock = lock;
		this.pot = pot;
	}

	public void run() {
		StringBuffer printMsg = new StringBuffer();
		printMsg.append("Savage no = " + savageID + ". ");
		lock.lock();
		try {
			if (pot.getNoOfMealsInPot() > 0) {
				printMsg.append(pot.consumeMeal());
			} else {
				printMsg.append(cook.potEmptyRefillImmediately(pot));
				printMsg.append(pot.consumeMeal());
			}
		} finally {
			lock.unlock();
			System.out.println(printMsg);
		}
	}
}

class Cook extends Thread {
	private int cookingCapacity;

	public Cook(int cookingCapacity) {
		this.cookingCapacity = cookingCapacity;
	}

	public int getCookingCapacity() {
		return cookingCapacity;
	}

	public void setCookingCapacity(int cookingCapacity) {
		this.cookingCapacity = cookingCapacity;
	}

	public String potEmptyRefillImmediately(Pot pot) {
		String retMsg = new String();
		if (pot.getNoOfMealsInPot() == 0) {
			pot.setNoOfMealsInPot(cookingCapacity);
			retMsg = new String(" Cook has refilled the pot. ");
		}
		return retMsg;
	}
}

class Pot {
	private volatile int noOfMealsInPot = 0;

	public Pot(int noOFMealsInPot) {
		this.noOfMealsInPot = noOFMealsInPot;
	}

	public String consumeMeal() {
		StringBuffer retMsg = new StringBuffer();
		retMsg.append(" Meals remaining in pot = " + noOfMealsInPot + ". ");
		noOfMealsInPot = noOfMealsInPot - 1;
		retMsg.append(" Meal consumed. Remaining portions = " + noOfMealsInPot
				+ ". ");
		return retMsg.toString();
	}

	public int getNoOfMealsInPot() {
		return noOfMealsInPot;
	}

	public void setNoOfMealsInPot(int noOfMealsInPot) {
		this.noOfMealsInPot = noOfMealsInPot;
	}

}