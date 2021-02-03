// UT-EID=jkj858
// UT-EID=jbr2558

import java.util.*;
import java.util.concurrent.*;

public class PSort extends RecursiveAction {
    int[] A;
    int begin;
    int end;

    public PSort(int[] A, int begin, int end) {
        this.A = A;
        this.begin = begin;
        this.end = end;
    }

    public static void parallelSort(int[] A, int begin, int end) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new PSort(A, begin, end));
        // System.out.print("New A:");
        // for (int k = begin; k < end; ++k) { System.out.print(" " + A[k]); }
        // System.out.println();
    }

    @Override
    protected void compute() {
        if (end - begin <= 16) {
            for (int i = begin + 1; i < end; ++i) {
                int current = A[i];
                int j = i - 1;
                while (j >= begin && current < A[j]) {
                    A[j + 1] = A[j];
                    --j;
                }
                A[j + 1] = current;
            }
        } else {
            int pivot = A[end - 1];
            int i = begin;
            for (int j = i; j < end; ++j) {
                if (A[j] < pivot) {
                    int temp = A[i];
                    A[i] = A[j];
                    A[j] = temp;
                    ++i;
                }
            }
            A[end - 1] = A[i];
            A[i] = pivot;
            ForkJoinTask.invokeAll(new PSort(A, begin, i),
                                   new PSort(A, i, end));
        }
    }
}
