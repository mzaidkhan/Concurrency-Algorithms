package lab.exercise3.two.one;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author mohammed zaid khan
 * 
 */
class CCASLock implements Lock {

	AtomicInteger state = new AtomicInteger(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#lock()
	 */
	@Override
	public void lock() {
		while (true) {
			while (state.get() == 1) {
			}
			if (state.getAndSet(1) == 0)
				return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#unlock()
	 */
	@Override
	public void unlock() {
		state.set(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#lockInterruptibly()
	 */
	@Override
	public void lockInterruptibly() throws InterruptedException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#newCondition()
	 */
	@Override
	public Condition newCondition() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#tryLock()
	 */
	@Override
	public boolean tryLock() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.locks.Lock#tryLock(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		return false;
	}
}