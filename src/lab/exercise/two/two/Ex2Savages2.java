package lab.exercise.two.two;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ex2Savages2 {

	public static void main(String[] args) {
		if (args.length == 2) {
			int noOfMealsCookPutsInPot = Integer.valueOf(args[0]);
			int noOfSavages = Integer.valueOf(args[1]);
			try {
				List<Savage> savages = new ArrayList<Savage>();
				BakeryLock lock = new BakeryLock(noOfSavages);

				Cook cook = new Cook(noOfMealsCookPutsInPot);
				Pot pot = new Pot(cook.getCookingCapacity());

				for (int i = 0; i < noOfSavages; i++) {
					Savage savage = new Savage(i, cook, pot, lock);
					savages.add(savage);
				}

				for (Savage savage : savages) {
					savage.start();
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
	private BakeryLock lock;

	public Savage(int savageID, Cook cook, Pot pot, BakeryLock lock) {
		this.savageID = savageID;
		this.cook = cook;
		this.lock = lock;
		this.pot = pot;
	}

	public void run() {
		StringBuffer printMsg = new StringBuffer();
		// for (int i = 0; i < 500; i++) {
		while (true) {
			lock.requestCS(savageID);
			printMsg.append("Savage no = " + (savageID + 1) + ". ");
			try {
				if (pot.getNoOfMealsInPot() > 0) {
					printMsg.append(pot.consumeMeal());
				} else {
					printMsg.append(cook.potEmptyRefillImmediately(pot));
					printMsg.append(pot.consumeMeal());
				}
			} finally {
				lock.releaseCS(savageID);
				System.out.println(printMsg);
				printMsg.delete(0, printMsg.length());
			}
		}
	}
}

class Cook extends Thread {
	private int cookingCapacity;
	
	private volatile boolean fillPotAgain = false;

	private Pot pot;
	
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
	
	public void fillMeals(boolean fillPotAgain,  Pot pot) {
		this.fillPotAgain = fillPotAgain;
		this.pot = pot;
		
	}
	
	public void run() {
		this.setDaemon(true);
		while (true) { 
			if(fillPotAgain)
			{
				potEmptyRefillImmediately(this.pot);
				this.pot = null;
			}
		}
	}
	
}

class Pot {
	private int noOfMealsInPot = 0;

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

class BakeryLock implements Lock {
	final int N; // number of processes using this object
	final Random ran = new Random();
	final boolean[] choosing;
	final int[] number;
	final boolean[] inCS;

	public BakeryLock(int n) {
		this.N = n;
		choosing = new boolean[N];
		number = new int[N];
		inCS = new boolean[N];
		for (int i = 0; i < N; ++i) {
			choosing[i] = false;
			number[i] = 0;
			inCS[i] = false;
		}
	}

	@Override
	public void requestCS(int id) {
		// step1
		choosing[id] = true;
		for (int j = 0; j < N; ++j) {
			if (number[j] > number[id])
				number[id] = number[j];
		}
		randomSleep(500);
		number[id] = number[id] + 1;
		choosing[id] = false;

		// step2
		for (int j = 0; j < N; ++j) {
			while (choosing[j]) {
				System.out.print("");
			}
			; // process j in doorway
			while ((number[j] != 0)
					&& ((number[j] < number[id]) || ((number[j] == number[id]) && j < id))) {
				System.out.print(""); // busy waiting
			}
		}

		// enter CS
		inCS[id] = true;
	}

	@Override
	public void releaseCS(int id) {
		for (int i = 0; i < N; ++i) {
			if (i != id && inCS[i]) {
				System.out.println("DATA RACING DETECTED!");
				System.exit(0);
			}
		}
		randomSleep(100);
		inCS[id] = false;
		number[id] = 0;
	}

	private void randomSleep(int time) {
		try {
			Thread.sleep(1 + ran.nextInt(time));
		} catch (InterruptedException e) {
		}
	}
}
