package com.houarizegai.calculator;

import com.houarizegai.calculator.util.ExpressionParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorUITest {

    @ParameterizedTest
    @CsvSource({"3+5,8", "2-8,-6", "44.5*10,445", "320/5,64", "3%5,3", "5^3,125", "sin(0),0", "cos(0),1", "tan(0),0", "sqrt(4),2", "square(3),9", "cube(2),8", "pi,3.141592653589793", "e,2.718281828459045"})
    void testCalculation(String expression, double expectedResult) throws Exception {
        assertEquals(expectedResult, ExpressionParser.evaluate(expression));
    }
}
