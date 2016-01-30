package lab2;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreExperiment {
    public static void main(String[] args) {

        int numberOfThreadsWantingToEat = 10;
        int numberOfAvailableSpoons = 5;

        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreadsWantingToEat); 
        // this thread pool would have 1 thread in it
        //once the work in the thread pool is done - the thread would *go back to the pool*

        SpoonResourcePool spoonResourcePool = new SpoonResourcePool(numberOfAvailableSpoons);

        for (int i = 0; i < numberOfThreadsWantingToEat; i++) {
            executorService.submit(new HungryChild(spoonResourcePool));
        }

    }
}

class HungryChild implements Runnable {

    private SpoonResourcePool spoonResourcePool;

    HungryChild(SpoonResourcePool spoonResourcePool) {
        this.spoonResourcePool = spoonResourcePool;
    }

    @Override
    public void run() {
        SpoonResource spoon = spoonResourcePool.getASpoonFromThePool();
        try {
            spoon.eat();
        } finally {
            spoonResourcePool.returnToThePool(spoon);
        }

    }
}


class SpoonResourcePool {

    private ConcurrentLinkedQueue<SpoonResource> availableSpoons;
    private Semaphore semaphore;

    public SpoonResourcePool(int numberOfSpoons) {

        availableSpoons = new ConcurrentLinkedQueue();

        for (int i = 0; i < numberOfSpoons; i++) {
            SpoonResource currentSpoon = new SpoonResource("grey");
            availableSpoons.add(currentSpoon);
        }
        semaphore = new Semaphore(numberOfSpoons);
    }

    // give a spool from the pool to a thread (child that want to eat with it)
    public SpoonResource getASpoonFromThePool() {

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SpoonResource nextSpoon = availableSpoons.poll(); // think of this as a retrieval operation from the collections
        //Retrieves and removes the head of this queue, or returns null if this queue is empty.

        return nextSpoon;

    }

    // called by the thread (child) that does not need the resource (spoon) anymore
    public void returnToThePool(SpoonResource spoon) {

        spoon.cleanMySpoon();
        availableSpoons.add(spoon);
        semaphore.release();

    }

}


class SpoonResource {

    public String color;
    public int numberOfTimesIateWithThisSpoon;
    public boolean dirty;

    public SpoonResource(String color) {
        this.color = color;
        dirty = false;
        numberOfTimesIateWithThisSpoon = 0;
    }

    //executed by the kid, that would eat with the spoon
    public void eat() {
        numberOfTimesIateWithThisSpoon++;
        dirty = true;
        System.out.println("HungryChild is eating");
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // executed by the pool
    public void cleanMySpoon() {
        dirty = false;
    }
}
