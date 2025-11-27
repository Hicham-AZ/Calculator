package com.houarizegai.calculator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CalculatorModel {
    private static final String DOUBLE_OR_NUMBER_REGEX = "([-]?\\d+[.]\\d*)|(\\d+)|(-\\d+)";
    
    private String displayValue = "0";
    private double typedValue = 0;
    private char selectedOperator = ' ';
    private boolean go = true; // For calculate with Opt != (=)
    private boolean addToDisplay = true; // Connect numbers in display
    
    private final List<CalculationHistory> history = new ArrayList<>();
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public double getTypedValue() {
        return typedValue;
    }
    
    public void setTypedValue(double typedValue) {
        this.typedValue = typedValue;
    }
    
    public char getSelectedOperator() {
        return selectedOperator;
    }
    
    public void setSelectedOperator(char selectedOperator) {
        this.selectedOperator = selectedOperator;
    }
    
    public boolean isGo() {
        return go;
    }
    
    public void setGo(boolean go) {
        this.go = go;
    }
    
    public boolean isAddToDisplay() {
        return addToDisplay;
    }
    
    public void setAddToDisplay(boolean addToDisplay) {
        this.addToDisplay = addToDisplay;
    }
    
    public double calculate(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return firstNumber + secondNumber;
            case '-':
                return firstNumber - secondNumber;
            case '*':
                return firstNumber * secondNumber;
            case '/':
                if (secondNumber == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return firstNumber / secondNumber;
            case '%':
                return firstNumber % secondNumber;
            case '^':
                return Math.pow(firstNumber, secondNumber);
            case 's':
                return Math.sqrt(secondNumber);
            case 'l':
                return Math.log(secondNumber);
            case 's':
                return Math.sin(Math.toRadians(secondNumber));
            case 'c':
                return Math.cos(Math.toRadians(secondNumber));
            case 't':
                return Math.tan(Math.toRadians(secondNumber));
            default:
                return secondNumber;
        }
    }
    
    public boolean isDisplayValid() {
        return Pattern.matches(DOUBLE_OR_NUMBER_REGEX, displayValue);
    }
    
    public void addToHistory(String expression, double result) {
        CalculationHistory historyItem = new CalculationHistory(expression, result);
        history.add(0, historyItem); // Add to the beginning to show most recent first
        
        // Keep only the last 10 operations
        if (history.size() > 10) {
            history.remove(10);
        }
    }
    
    public List<CalculationHistory> getHistory() {
        return history;
    }
    
    public static class CalculationHistory {
        private final String expression;
        private final double result;
        
        public CalculationHistory(String expression, double result) {
            this.expression = expression;
            this.result = result;
        }
        
        public String getExpression() {
            return expression;
        }
        
        public double getResult() {
            return result;
        }
    }
}