import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
	public static ArrayPool pool = null;//static array pool, its static so all the threads can access it

	public static void main(String[] args) {
		int n, m;
		n = getInput(0);//getting the number of numbers the user wish to sort
		m = getInput(1);//getting the number of threads the user wish to use
		pool = new ArrayPool(n);//creating the pool
		ExecutorService ex = Executors.newFixedThreadPool(m);
		for (int i = 0; i < m; ++i) {//lunching the threads
			ex.execute(new SortThread());
		}
		ex.shutdown();
		try {//waiting for all the threads to finish their work
			if (!ex.awaitTermination(10, TimeUnit.MINUTES)) {
				ex.shutdownNow();
			}
		} catch (InterruptedException e) {
			ex.shutdownNow();
			Thread.currentThread().interrupt();
		}
		System.out.println("The sorting is over!\n" + "The final array is: " + Arrays.toString(pool.getSorted()));//printing the sorted array
		System.out.println("Bye bye!");
	}

	public static int getInput(int i) {//input function
		Scanner sc = new Scanner(System.in);
		int val;

		if (i == 0) {//getting the number of number
			System.out.print("Hello and welcome!\n" + "Please enter the number of numbers you wish to sort:");
			val = sc.nextInt();
			while (true) {
				if (val <= 0) {
					System.out.print("Invalid input! Please try again\n"
							+ "Please enter the number of numbers you wish to sort:");
					val = sc.nextInt();
				} else {
					return val;
				}
			}
		} else {// getting the number of threads
			System.out.print("Please enter the number of Threads you wish to use:");
			val = sc.nextInt();
			while (true) {
				if (val <= 0) {
					System.out.print("Invalid input! Please try again\n"
							+ "Please enter the number of Threads you wish to use:");
					val = sc.nextInt();
				} else {
					return val;
				}
			}
		}
	}

	public static class SortThread extends Thread {// thread class
		private int[] firstArr;//array from the pool
		private int[] secondArr;//array from the pool 
		private int[] sorted;//sorted array
		
		//constructor
		public SortThread() {
			firstArr = null;
			secondArr = null;
			sorted = null;

		}

		@Override
		public void run() {
			int status, fIndex = 0, sIndex = 0;

			while (true) {
				synchronized (pool) {//checking if the pool has 2 available arrays if so getting them
					if ((status = pool.checkIfPossible()) == 0) {
						firstArr = pool.getarr();
						secondArr = pool.getarr();
					}
				}
				switch (status) {
				case 0://if we have 2 arrays
					fIndex = 0;
					sIndex = 0;
					sorted = new int[firstArr.length + secondArr.length];//setting the array size
					for (int i = 0; i < sorted.length; ++i) {//sorting the arrays into sorted array
						if (fIndex < firstArr.length && sIndex < secondArr.length) {
							if (firstArr[fIndex] <= secondArr[sIndex]) {
								sorted[i] = firstArr[fIndex];
								fIndex++;
							} else {
								sorted[i] = secondArr[sIndex];
								sIndex++;
							}
						} else if (fIndex < firstArr.length) {
							sorted[i] = firstArr[fIndex];
							fIndex++;
						} else {
							sorted[i] = secondArr[sIndex];
							sIndex++;
						}
					}
					synchronized (pool) {//adding the sorted array to the pool
						pool.addMerged(sorted);
					}
					break;
				case 1://if the pool didn't have 2 available arrays for us, sleep between 0-1 sec and try again
					try {
						Thread.sleep((int) Math.random() * 1000);
					} catch (InterruptedException e) {
						continue;
					}
					break;
				case 2://the work is done, stop running
					return;
				}
			}
		}
	}
}
