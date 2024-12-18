package ps1.src.warmup;

import java.util.HashSet;
import java.util.Set;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(int a, int b, int c) {
        assert (a != 0);
        Set<Integer> roots = new HashSet<>();
        double discriminant = Math.pow(b, 2) - (4.0 * a * c);
        double sqrt = Math.sqrt(discriminant);

        if(!Double.isNaN(sqrt) && isWholeNumber(sqrt)){
            int dis = (int) sqrt;
            double firstRoot = (-b + dis) / (2.0 * a);
            double secondRoot = (-b - dis) / (2.0 * a);

            if(isWholeNumber(firstRoot)){
                roots.add((int) firstRoot);
                roots.add((int) secondRoot);
            } else if (c == 0 && isWholeNumber(Math.sqrt(-b/a))) {
                roots.add((int) Math.sqrt(-b/a));
            }
        }
        return roots;
    }
    private static boolean isWholeNumber(double num){
        return num == (int) num;
    }
    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
