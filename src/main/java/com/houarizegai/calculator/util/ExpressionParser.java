package com.houarizegai.calculator.util;

import java.util.*;

public class ExpressionParser {
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    private static final Set<String> FUNCTIONS = new HashSet<>();
    private static final Set<String> CONSTANTS = new HashSet<>();
    private static final String DECIMAL_SEPARATOR = ".";
    private static final String NEGATIVE_SIGN = "-";
    private static final String POSITIVE_SIGN = "+";

    static {
        // Initialize operator precedence (higher number means higher precedence)
        OPERATOR_PRECEDENCE.put("+", 1);
        OPERATOR_PRECEDENCE.put("-", 1);
        OPERATOR_PRECEDENCE.put("*", 2);
        OPERATOR_PRECEDENCE.put("/", 2);
        OPERATOR_PRECEDENCE.put("%", 2);
        OPERATOR_PRECEDENCE.put("^", 3);

        // Initialize functions
        FUNCTIONS.add("sin");
        FUNCTIONS.add("cos");
        FUNCTIONS.add("tan");
        FUNCTIONS.add("asin");
        FUNCTIONS.add("acos");
        FUNCTIONS.add("atan");
        FUNCTIONS.add("log");
        FUNCTIONS.add("ln");
        FUNCTIONS.add("sqrt");
        FUNCTIONS.add("square");
        FUNCTIONS.add("cube");
        FUNCTIONS.add("power");
        FUNCTIONS.add("factorial");
        FUNCTIONS.add("tenPowerX");
        FUNCTIONS.add("twoPowerX");

        // Initialize constants
        CONSTANTS.add("pi");
        CONSTANTS.add("e");
    }

    public static double evaluate(String expression) throws Exception {
        // Remove all whitespace from the expression
        expression = expression.replaceAll("\\s+", "");

        // Tokenize the expression
        List<String> tokens = tokenize(expression);

        // Convert infix to postfix (Reverse Polish Notation) using Shunting-Yard algorithm
        List<String> postfix = infixToPostfix(tokens);

        // Evaluate the postfix expression
        return evaluatePostfix(postfix);
    }

    private static List<String> tokenize(String expression) throws Exception {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int length = expression.length();

        for (int i = 0; i < length; i++) {
            char c = expression.charAt(i);

            // Check if current character is a digit or decimal separator
            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
            } else if (Character.isLetter(c)) {
                // It's a function or constant
                currentToken.append(c);
                // Continue reading until we reach a non-letter character
                while (i + 1 < length && Character.isLetter(expression.charAt(i + 1))) {
                    i++;
                    currentToken.append(expression.charAt(i));
                }
                // Add the function or constant as a token
                String token = currentToken.toString();
                if (!FUNCTIONS.contains(token) && !CONSTANTS.contains(token)) {
                    throw new Exception("Unknown function or constant: " + token);
                }
                tokens.add(token);
                currentToken.setLength(0);
            } else if (c == '(' || c == ')') {
                // Add parentheses as separate tokens
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (isOperator(c)) {
                // Check if this is a unary minus or plus
                boolean isUnary = false;
                if ((c == '-' || c == '+') && (i == 0 || expression.charAt(i - 1) == '(' || isOperator(expression.charAt(i - 1)))) {
                    isUnary = true;
                }

                if (isUnary) {
                    // This is a unary operator, add it as part of the next number
                    currentToken.append(c);
                } else {
                    // This is a binary operator
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
            } else {
                throw new Exception("Unknown character in expression: " + c);
            }
        }

        // Add the last token if any
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    private static List<String> infixToPostfix(List<String> tokens) throws Exception {
        List<String> postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                // If it's a number, add to postfix
                postfix.add(token);
            } else if (FUNCTIONS.contains(token)) {
                // If it's a function, push to stack
                stack.push(token);
            } else if (CONSTANTS.contains(token)) {
                // If it's a constant, evaluate it and add to postfix
                double value = evaluateConstant(token);
                postfix.add(String.valueOf(value));
            } else if (token.equals("(")) {
                // If it's an opening parenthesis, push to stack
                stack.push(token);
            } else if (token.equals(")")) {
                // If it's a closing parenthesis, pop until opening parenthesis is found
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new Exception("Mismatched parentheses");
                }
                stack.pop(); // Pop the opening parenthesis
                // If there's a function on top of the stack, pop it to postfix
                if (!stack.isEmpty() && FUNCTIONS.contains(stack.peek())) {
                    postfix.add(stack.pop());
                }
            } else if (OPERATOR_PRECEDENCE.containsKey(token)) {
                // If it's an operator
                while (!stack.isEmpty() && OPERATOR_PRECEDENCE.containsKey(stack.peek()) &&
                        ((isLeftAssociative(token) && OPERATOR_PRECEDENCE.get(token) <= OPERATOR_PRECEDENCE.get(stack.peek())) ||
                                (!isLeftAssociative(token) && OPERATOR_PRECEDENCE.get(token) < OPERATOR_PRECEDENCE.get(stack.peek())))) {
                    postfix.add(stack.pop());
                }
                stack.push(token);
            } else {
                throw new Exception("Unknown token: " + token);
            }
        }

        // Pop all remaining operators from the stack
        while (!stack.isEmpty()) {
            if (stack.peek().equals("(") || stack.peek().equals(")")) {
                throw new Exception("Mismatched parentheses");
            }
            postfix.add(stack.pop());
        }

        return postfix;
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isLeftAssociative(String operator) {
        // All operators are left-associative except exponentiation
        return !operator.equals("^");
    }

    private static double evaluateConstant(String constant) throws Exception {
        switch (constant) {
            case "pi":
                return Math.PI;
            case "e":
                return Math.E;
            default:
                throw new Exception("Unknown constant: " + constant);
        }
    }

    private static double evaluatePostfix(List<String> postfix) throws Exception {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                // If it's a number, push to stack
                stack.push(Double.parseDouble(token));
            } else if (FUNCTIONS.contains(token)) {
                // If it's a function, pop the required number of arguments and evaluate
                if (stack.isEmpty()) {
                    throw new Exception("Insufficient arguments for function: " + token);
                }
                double arg = stack.pop();
                double result = evaluateFunction(token, arg);
                stack.push(result);
            } else if (OPERATOR_PRECEDENCE.containsKey(token)) {
                // If it's an operator, pop two arguments and evaluate
                if (stack.size() < 2) {
                    throw new Exception("Insufficient operands for operator: " + token);
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = evaluateOperator(token, a, b);
                stack.push(result);
            } else {
                throw new Exception("Unknown token in postfix expression: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new Exception("Invalid expression");
        }

        return stack.pop();
    }

    private static double evaluateFunction(String function, double arg) throws Exception {
        switch (function) {
            case "sin":
                return ScientificFunctions.sin(arg);
            case "cos":
                return ScientificFunctions.cos(arg);
            case "tan":
                return ScientificFunctions.tan(arg);
            case "asin":
                return ScientificFunctions.asin(arg);
            case "acos":
                return ScientificFunctions.acos(arg);
            case "atan":
                return ScientificFunctions.atan(arg);
            case "log":
                return ScientificFunctions.log(arg);
            case "ln":
                return ScientificFunctions.ln(arg);
            case "sqrt":
                return ScientificFunctions.sqrt(arg);
            case "square":
                return ScientificFunctions.square(arg);
            case "cube":
                return ScientificFunctions.cube(arg);
            case "factorial":
                return ScientificFunctions.factorial(arg);
            case "tenPowerX":
                return ScientificFunctions.tenPowerX(arg);
            case "twoPowerX":
                return ScientificFunctions.twoPowerX(arg);
            default:
                throw new Exception("Unknown function: " + function);
        }
    }

    private static double evaluateOperator(String operator, double a, double b) throws Exception {
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
                if (b == 0) {
                    throw new Exception("Division by zero");
                }
                return a % b;
            case "^":
                return Math.pow(a, b);
            default:
                throw new Exception("Unknown operator: " + operator);
        }
    }
}
