package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

// Core JUnit 5
import org.junit.jupiter.api.Test;

// Jqwik
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;

import java.util.*;


public class PropertyTests extends BinarySearchBase {

    @Property
    public boolean checkSingleArrayElt(@ForAll int x) {
        Integer[] arr = { x };
        return find(arr, x) == 0;
    }

    @Provide
    Arbitrary<Integer[]> randomArr() {
        Arbitrary<Integer> arbInt = Arbitraries.integers().between(0, 100);
        return arbInt.array(Integer[].class).ofMinSize(1).ofMaxSize(30);
    }

    @Provide
    Arbitrary<Integer[]> randomSortedArr() {
        Arbitrary<Integer[]> randomArbArr = randomArr();
        return randomArbArr.map(arr -> {
            Arrays.sort(arr); return arr;});
    }

    @Provide
    Arbitrary<Tuple2<Integer[], Integer>> arrayAndInt() {
        Arbitrary<Integer[]> sortedArbArr = randomSortedArr();
        return sortedArbArr.map(arrInt -> {
            int randomIndex = new Random().nextInt(arrInt.length);
            return Tuple.of(arrInt, arrInt[randomIndex]);
        });
    }

    @Property
    public boolean findElementInRandomSortedArray(@ForAll("arrayAndInt") Tuple2<Integer[], Integer> arrAndInt) {
        Integer[] arr = arrAndInt.get1();
        Integer element = arrAndInt.get2();

        return Objects.equals(arr[find(arr, element)], element);
    }

    @Property
    public void throwsForElementBelowLowerBound(@ForAll("randomSortedArr") Integer[] arr) {
        assertThrows(NoSuchElementException.class, () -> find(arr, -1));
    }

    @Property
    public void throwsForElementAboveUpperBound(@ForAll("randomSortedArr") Integer[] arr) {
        assertThrows(NoSuchElementException.class, () -> find(arr, 101));
    }
    
    @Example
    public void throwsForEmptyArray() {
        Integer[] arr = {};
        assertThrows(IllegalArgumentException.class, () -> find(arr, 0));
    }

    @Example
    public void throwsForNullArray() {
        assertThrows(IllegalArgumentException.class, () -> find(null, 0));
    }

    @Property
    public void throwsForNullElement(@ForAll("randomSortedArr") Integer[] arr) {
        assertThrows(IllegalArgumentException.class, () -> find(arr, null));
    }

    @Property
    public void findFirstElement(@ForAll("randomSortedArr") @UniqueElements Integer[] arr) {
        assertEquals(0, find(arr, arr[0]));
    }

    @Property
    public void findLastElement(@ForAll("randomSortedArr") @UniqueElements Integer[] arr) {
        assertEquals(arr.length - 1, find(arr, arr[arr.length - 1]));
    }

    @Property
    public void findIntermediateElements(@ForAll("randomSortedArr") @UniqueElements Integer[] arr) {
        for (int i = 1; i < arr.length - 1; i++) {
            int index = find(arr, arr[i]);
            assertEquals(i, index);
        }
    }

    @Property
    public void findWithUnsortedArray(@ForAll("randomArr") Integer[] arr) {
        try {
            int elementToFind = arr[arr.length / 2];
            int result = find(arr, elementToFind);
            assertTrue(result >= 0 && result < arr.length);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof NoSuchElementException);
        }
    }

    @Property
    public void findDuplicateElements(@ForAll("randomSortedArr") Integer[] arr) {
        for (Integer integer : arr) {
            int index = find(arr, integer);
            assertTrue(index >= 0 && index < arr.length);
            assertEquals(integer, arr[index]);
        }
    }

}

