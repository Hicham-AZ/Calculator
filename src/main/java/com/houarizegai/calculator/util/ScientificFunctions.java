package com.houarizegai.calculator.util;

public class ScientificFunctions {

    public static double factorial(long n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        double result = 1;
        for (long i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
}
