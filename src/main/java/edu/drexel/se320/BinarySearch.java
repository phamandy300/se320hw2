package edu.drexel.se320;

import java.util.NoSuchElementException;

public class BinarySearch {

    // DO NOT MODIFY THIS SIGNATURE
    // This includes the protected modifier; the autograder currently relies
    // on a combination of overloading and visibility hacks to swap out your
    // code at runtime to test your test suite.
    protected static <T extends Comparable<T>> int binarySearchImplementation(T[] array, T elem) {
	// TODO: Copy over your HW1 implementation of binary search here
        if (array == null || array.length == 0 || elem == null) {
            throw new IllegalArgumentException("Invalid input: array must be non-null and non-empty, and element must be non-null");
        }

        int left = 0, right = array.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            int comparison = array[middle].compareTo(elem);

            if (comparison == 0) {
                return middle;
            } else if (comparison > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        throw new NoSuchElementException("Element not found in the array");
    }
}
