import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ArrayPool {

	private ArrayList<int[]> pool;	//the array pool
	private boolean flag;			//flag that indicate if the sorting is done
	private int n;					//the total number of numbers in the arrays

	// constructors
	public ArrayPool(int n) {
		Random ran = new Random();
		int[] temp = new int[1];
		pool = new ArrayList<int[]>();//creating the pool
		for (int i = 0; i < n; ++i) {//filling the pool with random numbers
			temp[0] = ran.nextInt(99) + 1;
			pool.add(Arrays.copyOf(temp, temp.length));
		}
		flag = false;
		this.n = n;

	}

	// methods
	public int[] getSorted() {//returns the sorted array
		return pool.get(0);
	}

	public void addMerged(int[] newArg) {//adding a merged array back to the pool and updating the flag. this function must be used in a synchronized block
		int[] copy;//the array we get from the calling function will change later so we need to add a copy of it to the pool instead
		copy = Arrays.copyOf(newArg, newArg.length);
		pool.add(copy);
		flag = (n == copy.length);
	}

	public int checkIfPossible() {// this function must be used in a synchronized block
		if (pool.size() >= 2) { // if there is more then 2 arrays in the pool you can take 2 and merge them
			return 0;
		}
		if (pool.size() <= 1 && !flag) {// if there is 1 or less arrays in the pool but the flag is down go to sleep
			return 1;
		}
		return 2; // we will be here only when there is 1 array in the pool and the flag is up,
					// stop yourself the work is done
	}

	public int[] getarr() {// this function must be used in a synchronized block
		Random ran = new Random();
		int[] temp, copy;
		int index;

		index = ran.nextInt(pool.size());//getting a random index
		temp = pool.get(index);//getting the array in the randomized index
		copy = Arrays.copyOf(temp, temp.length);//copying the array
		pool.remove(index);//removing the array from the pool
		return copy;//returning the array
	}

}
