package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

// Jqwik
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.Statistics;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;


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
        Arbitrary<Integer> arbInt = Arbitraries.integers().between(0, 100);
        return arbInt.array(Integer[].class).ofMinSize(1).ofMaxSize(30).map(arr -> {
            Arrays.sort(arr); return arr;});
    }

    @Provide
    Arbitrary<Tuple2<Integer[], Integer>> arrayAndInt() {
        Arbitrary<Integer> arbInt = Arbitraries.integers().between(0, 100);
        return arbInt.array(Integer[].class)
                .ofMinSize(1)
                .ofMaxSize(30)
                .map(arrInt -> {
                    Arrays.sort(arrInt);
                    int randomIndex = new Random().nextInt(arrInt.length);
                    return Tuple.of(arrInt, arrInt[randomIndex]);
                });
    }

    @Property
    public boolean checkRandomSortedArray(@ForAll("arrayAndInt") Tuple2<Integer[], Integer> arrAndInt) {
        Integer[] arr = arrAndInt.get1();
        Integer element = arrAndInt.get2();

        return Objects.equals(arr[find(arr, element)], element);
    }

    @Property
    public void checkElementNotInArrayLowerBound(@ForAll("randomSortedArr") Integer[] arr) {
        assertThrows(NoSuchElementException.class, () -> find(arr, -1));
    }

    @Property
    public void checkElementNotInArrayUpperBound(@ForAll("randomSortedArr") Integer[] arr) {
        assertThrows(NoSuchElementException.class, () -> find(arr, 101));
    }

    @Property
    public void checkUnsortedArray(@ForAll("randomArr") Integer[] arr) {

    }

}

