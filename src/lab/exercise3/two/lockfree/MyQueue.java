	package lab.exercise3.two.lockfree;public class MyQueue {	volatile int  head = 0, tail = 0, QSIZE = 0;	volatile int items[];	public MyQueue(int QSIZE) {		this.QSIZE = QSIZE;		items = new int[QSIZE];	}	public void enq(int x) {		while (tail - head == QSIZE) {			// System.out.println("Enq waiting");		}		;		items[tail % QSIZE] = x;		tail++;	}	public int deq() {		while (tail == head) {			// System.out.println("Deq waiting");		}		;		int item = items[head % QSIZE];		head++;		return item;	}}