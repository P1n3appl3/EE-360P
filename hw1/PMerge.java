// UT-EID=jkj858
// UT-EID=jbr2558

import java.util.*;
import java.util.concurrent.*;

class Slice {
    int[] arr;
    int start;
    int end;

    public Slice(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(arr, start, end));
    }
}

public class PMerge extends RecursiveTask<int[]> {
    Slice a;
    Slice b;

    public PMerge(Slice a, Slice b) {
        this.a = a;
        this.b = b;
    }

    public static void parallelMerge(int[] A, int[] B, int[] C,
                                     int numThreads) {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        Slice a = new Slice(A, 0, A.length);
        Slice b = new Slice(B, 0, B.length);

        PMerge p = new PMerge(a, b);
        int[] sorted = pool.invoke(p);

        for (int i = 0; i < sorted.length; i++) {
            C[sorted.length - 1 - i] = sorted[i];
        }
    }

    @Override
    protected int[] compute() {
        int aLength = a.end - a.start;
        int bLength = b.end - b.start;
        //System.out.println(aLength + " " + bLength);

        // Base case: if one of the arrays are empty or both arrays are of length 1 swap accordingly
        if (aLength == 0) {
            return Arrays.copyOfRange(b.arr, b.start, b.end);
        } else if (bLength == 0) {
            return Arrays.copyOfRange(a.arr, a.start, a.end);
        } else if (aLength == 1 && bLength == 1) {
            int[] c = new int[2];
            if (a.arr[a.start] < b.arr[b.start]) {
                c[0] = a.arr[a.start];
                c[1] = b.arr[b.start];
            } else {
                c[0] = b.arr[b.start];
                c[1] = a.arr[a.start];
            }

            return c;
        }

        // Always have larger array as a
        if (bLength > aLength) {
            Slice temp = a;
            a = b;
            b = temp;
        }

        int midIndex = a.start + aLength / 2;

        // Find index for value of middle element in array b
        int bIndex = Arrays.binarySearch(b.arr, b.start, b.end, a.arr[midIndex]);
        if (bIndex < 0) bIndex = (bIndex * -1) - 1;

        // Slice arrays into new segments and call next level
        Slice a1 = new Slice(a.arr, a.start, midIndex);
        Slice a2 = new Slice(a.arr, midIndex, a.end);

        Slice b1 = new Slice(b.arr, b.start, bIndex);
        Slice b2 = new Slice(b.arr, bIndex, b.end);

        PMerge p1 = new PMerge(a1, b1);
        p1.fork();
        PMerge p2 = new PMerge(a2, b2);

        int[] newB = p2.compute();
        int[] newA = p1.join();

        //System.out.println("A: " + Arrays.toString(newA));
        //System.out.println("B: " + Arrays.toString(newB));

        // Merge two sorted arrays
        int[] c = Arrays.copyOf(newA, newA.length + newB.length);
        for (int i = 0; i < newB.length; i++) {
            c[i + newA.length] = newB[i];
        }

        return c;
    }
}
