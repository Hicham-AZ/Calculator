package com.houarizegai.calculator.util;

public class ScientificFunctions {
    // Trigonometric functions (in radians by default)
    public static double sin(double x) {
        return Math.sin(x);
    }

    public static double cos(double x) {
        return Math.cos(x);
    }

    public static double tan(double x) {
        return Math.tan(x);
    }

    // Inverse trigonometric functions (return radians)
    public static double asin(double x) {
        return Math.asin(x);
    }

    public static double acos(double x) {
        return Math.acos(x);
    }

    public static double atan(double x) {
        return Math.atan(x);
    }

    // Logarithmic functions
    public static double log(double x) {
        return Math.log10(x);
    }

    public static double ln(double x) {
        return Math.log(x);
    }

    // Power functions
    public static double sqrt(double x) {
        return Math.sqrt(x);
    }

    public static double square(double x) {
        return Math.pow(x, 2);
    }

    public static double cube(double x) {
        return Math.pow(x, 3);
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    // Constants
    public static double pi() {
        return Math.PI;
    }

    public static double e() {
        return Math.E;
    }

    // Factorial function (for non-negative integers)
    public static double factorial(double x) {
        if (x < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        if (x == 0 || x == 1) {
            return 1;
        }
        if (x != Math.floor(x)) {
            throw new IllegalArgumentException("Factorial is only defined for integers");
        }
        double result = 1;
        for (int i = 2; i <= x; i++) {
            result *= i;
        }
        return result;
    }

    // Exponential functions
    public static double tenPowerX(double x) {
        return Math.pow(10, x);
    }

    public static double twoPowerX(double x) {
        return Math.pow(2, x);
    }

    // Degree to radian conversion
    public static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    // Radian to degree conversion
    public static double radiansToDegrees(double radians) {
        return Math.toDegrees(radians);
    }
}
