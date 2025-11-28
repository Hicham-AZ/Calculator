package com.houarizegai.calculator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ExpressionParser {

    public static double evaluate(String expression) throws Exception {
        List<String> tokens = tokenize(expression);
        List<String> postfix = shuntingYard(tokens);
        return evaluatePostfix(postfix);
    }

    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
                i++;
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.' || expression.charAt(i) == 'e' || expression.charAt(i) == 'E')) {
                    currentToken.append(expression.charAt(i));
                    i++;
                    if (i < expression.length() && (expression.charAt(i) == '+' || expression.charAt(i) == '-') && (expression.charAt(i-1) == 'e' || expression.charAt(i-1) == 'E')) {
                        currentToken.append(expression.charAt(i));
                        i++;
                    }
                }
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
                i++;
            } else if (c == '(' || c == ')') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isLetter(c)) {
                currentToken.append(c);
                i++;
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    currentToken.append(expression.charAt(i));
                    i++;
                }
                tokens.add(currentToken.toString());
                currentToken.setLength(0);
            } else {
                i++;
            }
        }

        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static List<String> shuntingYard(List<String> tokens) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (isFunction(token)) {
                operators.push(token);
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && !operators.peek().equals("(") && 
                       (precedence(operators.peek()) > precedence(token) || 
                        (precedence(operators.peek()) == precedence(token) && associativity(token) == 'L'))) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (operators.isEmpty()) {
                    throw new Exception("Mismatched parentheses");
                }
                operators.pop();
                if (!operators.isEmpty() && isFunction(operators.peek())) {
                    output.add(operators.pop());
                }
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek().equals("(") || operators.peek().equals(")")) {
                throw new Exception("Mismatched parentheses");
            }
            output.add(operators.pop());
        }

        return output;
    }

    private static double evaluatePostfix(List<String> postfix) throws Exception {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new Exception("Insufficient operands");
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = applyOperator(a, b, token);
                stack.push(result);
            } else if (isFunction(token)) {
                if (stack.isEmpty()) {
                    throw new Exception("Insufficient operands for function");
                }
                double a = stack.pop();
                double result = applyFunction(a, token);
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            throw new Exception("Invalid expression");
        }

        return stack.pop();
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("%") || token.equals("^");
    }

    private static boolean isFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("tan") || token.equals("asin") || 
               token.equals("acos") || token.equals("atan") || token.equals("log") || token.equals("ln") || 
               token.equals("sqrt") || token.equals("sqr") || token.equals("cube") || token.equals("fact") || 
               token.equals("10x") || token.equals("2x");
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private static char associativity(String operator) {
        return operator.equals("^") ? 'R' : 'L';
    }

    private static double applyOperator(double a, double b, String operator) throws Exception {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new Exception("Division by zero");
                }
                return a / b;
            case "%":
                return a % b;
            case "^":
                return Math.pow(a, b);
            default:
                throw new Exception("Unknown operator: " + operator);
        }
    }

    private static double applyFunction(double a, String function) throws Exception {
        switch (function) {
            case "sin":
                return Math.sin(a);
            case "cos":
                return Math.cos(a);
            case "tan":
                return Math.tan(a);
            case "asin":
                return Math.asin(a);
            case "acos":
                return Math.acos(a);
            case "atan":
                return Math.atan(a);
            case "log":
                if (a <= 0) {
                    throw new Exception("Logarithm of non-positive number");
                }
                return Math.log10(a);
            case "ln":
                if (a <= 0) {
                    throw new Exception("Natural logarithm of non-positive number");
                }
                return Math.log(a);
            case "sqrt":
                if (a < 0) {
                    throw new Exception("Square root of negative number");
                }
                return Math.sqrt(a);
            case "sqr":
                return a * a;
            case "cube":
                return a * a * a;
            case "fact":
                if (a < 0 || a != Math.floor(a)) {
                    throw new Exception("Factorial of non-integer or negative number");
                }
                return ScientificFunctions.factorial((long) a);
            case "10x":
                return Math.pow(10, a);
            case "2x":
                return Math.pow(2, a);
            default:
                throw new Exception("Unknown function: " + function);
        }
    }
}
