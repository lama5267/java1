package ru.progwards.java2.lessons.tests.test.calc;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import progwards.java2.lessons.tests.calc.SimpleCalculator;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class SimpleCalculatorTest {
    @RunWith(Parameterized.class)
    public static class SumCalculatorTest {
        public int val1;
        public int val2;
        public int expected;

        public SumCalculatorTest(int val1, int val2, int expected) {
            this.val1 = val1;
            this.val2 = val2;
            this.expected = expected;
        }

        @Parameterized.Parameters(name = "Тест {index}: ({0}) + ({1}) = {2}")
        public static Iterable<Object[]> dataForTest() {
            return Arrays.asList(new Object[][]{
                    {0, 0, 0},
                    {5, 0, 5},
                    {-5, -5, -10},
                    {34, 55, 89},
                    {-34, -55, -89}
            });
        }

        @Test
        public void testWithParams() {
            assertEquals(expected, SimpleCalculator.sum(val1, val2));
        }
    }

    @RunWith(Parameterized.class)
    public static class DiffCalculatorTest {
        public int val1;
        public int val2;
        public int expected;

        public DiffCalculatorTest(int val1, int val2, int expected) {
            this.val1 = val1;
            this.val2 = val2;
            this.expected = expected;
        }

        @Parameterized.Parameters(name = "Тест {index}: ({0}) - ({1}) = {2}")
        public static Iterable<Object[]> dataForTest() {
            return Arrays.asList(new Object[][]{
                    {0, 0, 0},
                    {5, 0, 5},
                    {-5, -5, 0},
                    {34, 55, -21},
                    {-34, -55, 21}
            });
        }

        @Test
        public void testWithParams() {
            assertEquals(expected, SimpleCalculator.diff(val1, val2));
        }
    }

    @RunWith(Parameterized.class)
    public static class MultCalculatorTest {
        public int val1;
        public int val2;
        public int expected;

        public MultCalculatorTest(int val1, int val2, int expected) {
            this.val1 = val1;
            this.val2 = val2;
            this.expected = expected;
        }

        @Parameterized.Parameters(name = "Тест {index}: ({0}) * ({1}) = {2}")
        public static Iterable<Object[]> dataForTest() {
            return Arrays.asList(new Object[][]{
                    {0, 0, 0},
                    {5, 0, 0},
                    {-5, -5, 25},
                    {3, 5, 15},
                    {-3, 5, -15}
            });
        }

        @Test
        public void testWithParams() {
            assertEquals(expected, SimpleCalculator.mult(val1, val2));
        }
    }

    @RunWith(Parameterized.class)
    public static class DivCalculatorTest {
        public int val1;
        public int val2;
        public int expected;

        public DivCalculatorTest(int val1, int val2, int expected) {
            this.val1 = val1;
            this.val2 = val2;
            this.expected = expected;
        }

        @Parameterized.Parameters(name = "Тест {index}: ({0}) / ({1}) = {2}")
        public static Iterable<Object[]> dataForTest() {
            return Arrays.asList(new Object[][]{
                    {0, 1, 0},
                    {5, 5, 1},
                    {-5, -5, 1},
                    {10, 5, 2},
                    {-10, 5, -2}
            });
        }

        @Test
        public void testWithParams() {
            assertEquals(expected, SimpleCalculator.div(val1, val2));
        }
    }

    public static class CheckExceptionTest {
        @Test(expected = ArithmeticException.class)
        public void SumWithException() {
            SimpleCalculator.sum(Integer.MAX_VALUE, 1);
        }
        @Test(expected = ArithmeticException.class)
        public void MultWithException() {
            SimpleCalculator.mult(Integer.MAX_VALUE, 2);
        }
        @Test(expected = ArithmeticException.class)
        public void DivWithException() {
            SimpleCalculator.div(Integer.MAX_VALUE, 0);
        }
    }
}
