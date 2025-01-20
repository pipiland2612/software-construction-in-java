/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    @Test
    public void testNumber() {
        Expression num = new Number(3.5);
        assertEquals("3.5", num.toString());
        assertEquals(new Number(3.5), num);
        assertNotEquals(new Number(4.0), num);
    }

    @Test
    public void testVariable() {
        Expression var = new Variable("x");
        assertEquals("x", var.toString());
        assertEquals(new Variable("x"), var);
        assertNotEquals(new Variable("y"), var);
    }

    @Test
    public void testAddition() {
        Expression add = new Sum(new Number(1), new Variable("x"));
        assertEquals("(1.0 + x)", add.toString());
        assertEquals(new Sum(new Number(1), new Variable("x")), add);
    }

    @Test
    public void testMultiplication() {
        Expression mul = new Product(new Variable("x"), new Variable("y"));
        assertEquals("(x * y)", mul.toString());
        assertEquals(new Product(new Variable("x"), new Variable("y")), mul);
    }

    @Test
    public void testValidParserInput() {
        String testString = "3 + 2.4\n" +
                "3 * x + 2.4\n" +
                "3 * (x + 2.4)\n" +
                "((3 + 4) * x * x)\n" +
                "foo + bar+baz\n" +
                "(2*x    )+    (    y*x    )\n" +
                "4 + 3 * x + 2 * x * x + 1 * x * x * (((x)))\n";
        String[] testCases = testString.split("\n");

        for (String test : testCases) {
            try {
                Expression expression = Expression.parse(test);
                assertNotNull(expression);
            } catch (Exception e) {
                fail("Parser threw an exception for valid input: " + test);
            }
        }
    }


    @Test
    public void testInvalidParserInput() {
        String testString = "3 *\n" +
                "( 3\n" +
                "3 x";
        String[] testCases = testString.split("\n");

        for (String test : testCases) {
            try {
                Expression.parse(test);
                fail("Parser did not throw an exception for invalid input: " + test);
            } catch (IllegalArgumentException e) {
                System.out.println("Correctly threw exception for: " + test);
            }
        }
    }
}

