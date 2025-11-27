package com.houarizegai.calculator.controller;

import com.houarizegai.calculator.model.CalculatorModel;
import com.houarizegai.calculator.model.CalculatorModel.CalculationHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

public class CalculatorController {
    private final CalculatorModel model;
    private View view;
    
    public CalculatorController(CalculatorModel model) {
        this.model = model;
    }
    
    public void setView(View view) {
        this.view = view;
    }
    
    public void handleNumberInput(String number) {
        if (model.isAddToDisplay()) {
            if (Pattern.matches("[0]*", model.getDisplayValue())) {
                model.setDisplayValue(number);
            } else {
                model.setDisplayValue(model.getDisplayValue() + number);
            }
        } else {
            model.setDisplayValue(number);
            model.setAddToDisplay(true);
        }
        model.setGo(true);
    }
    
    public void handlePointInput() {
        if (model.isAddToDisplay()) {
            if (!model.getDisplayValue().contains(".")) {
                model.setDisplayValue(model.getDisplayValue() + ".");
            }
        } else {
            model.setDisplayValue("0.");
            model.setAddToDisplay(true);
        }
        model.setGo(true);
    }
    
    public void handleClear() {
        model.setDisplayValue("0");
        model.setSelectedOperator(' ');
        model.setTypedValue(0);
    }
    
    public void handleBackspace() {
        String str = model.getDisplayValue();
        StringBuilder str2 = new StringBuilder();
        for (int i = 0; i < (str.length() - 1); i++) {
            str2.append(str.charAt(i));
        }
        if (str2.toString().equals("")) {
            model.setDisplayValue("0");
        } else {
            model.setDisplayValue(str2.toString());
        }
    }
    
    public void handleOperator(char operator) {
        if (!model.isDisplayValid())
            return;

        if (model.isGo()) {
            try {
                double result = model.calculate(model.getTypedValue(), Double.parseDouble(model.getDisplayValue()), model.getSelectedOperator());
                
                // Format the result
                String formattedResult = formatResult(result);
                model.setDisplayValue(formattedResult);
                
                // Add to history only when performing binary operations
                if (model.getSelectedOperator() != ' ' && model.getSelectedOperator() != 's' && model.getSelectedOperator() != 'l' && 
                    model.getSelectedOperator() != 's' && model.getSelectedOperator() != 'c' && model.getSelectedOperator() != 't') {
                    model.addToHistory(model.getTypedValue() + " " + model.getSelectedOperator() + " " + model.getDisplayValue(), result);
                }
                
                model.setTypedValue(result);
                model.setSelectedOperator(operator);
                model.setGo(false);
                model.setAddToDisplay(false);
            } catch (ArithmeticException e) {
                view.showError(e.getMessage());
                model.setDisplayValue("0");
            }
        } else {
            model.setSelectedOperator(operator);
        }
    }
    
    public void handleEquals() {
        if (!model.isDisplayValid() || !model.isGo())
            return;

        try {
            double secondNumber = Double.parseDouble(model.getDisplayValue());
            double result = model.calculate(model.getTypedValue(), secondNumber, model.getSelectedOperator());
            
            // Format the result
            String formattedResult = formatResult(result);
            
            // Add to history
            if (model.getSelectedOperator() != ' ') {
                model.addToHistory(model.getTypedValue() + " " + model.getSelectedOperator() + " " + secondNumber, result);
            } else {
                // For single number, just add it to history
                model.addToHistory(String.valueOf(secondNumber), result);
            }
            
            model.setDisplayValue(formattedResult);
            model.setTypedValue(result);
            model.setSelectedOperator('=');
            model.setAddToDisplay(false);
        } catch (ArithmeticException e) {
            view.showError(e.getMessage());
            model.setDisplayValue("0");
        }
    }
    
    public void handleUnaryFunction(char function) {
        if (!model.isDisplayValid() || !model.isGo())
            return;

        try {
            double result = model.calculate(0, Double.parseDouble(model.getDisplayValue()), function);
            
            // Format the result
            String formattedResult = formatResult(result);
            
            // Add to history
            String functionName = getFunctionName(function);
            model.addToHistory(functionName + "(" + model.getDisplayValue() + ")", result);
            
            model.setDisplayValue(formattedResult);
            model.setTypedValue(result);
            model.setSelectedOperator(function);
            model.setAddToDisplay(false);
        } catch (ArithmeticException e) {
            view.showError(e.getMessage());
            model.setDisplayValue("0");
        }
    }
    
    private String formatResult(double result) {
        if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(result))) {
            return String.valueOf((int) result);
        } else {
            return String.valueOf(result);
        }
    }
    
    private String getFunctionName(char function) {
        switch (function) {
            case 's':
                return "sqrt";
            case 'l':
                return "ln";
            case 's':
                return "sin";
            case 'c':
                return "cos";
            case 't':
                return "tan";
            case '^':
                return "pow";
            default:
                return "";
        }
    }
    
    public void saveHistoryToJson(String filePath) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(model.getHistory(), writer);
        } catch (IOException e) {
            view.showError("Error saving history: " + e.getMessage());
        }
    }
    
    public void loadHistoryFromJson(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type historyListType = new TypeToken<List<CalculationHistory>>(){}.getType();
            List<CalculationHistory> loadedHistory = gson.fromJson(reader, historyListType);
            
            if (loadedHistory != null) {
                model.getHistory().clear();
                model.getHistory().addAll(loadedHistory);
                view.updateHistoryTable();
            }
        } catch (IOException e) {
            view.showError("Error loading history: " + e.getMessage());
        }
    }
    
    public List<CalculationHistory> getHistory() {
        return model.getHistory();
    }
    
    public interface View {
        void showError(String message);
        void updateHistoryTable();
    }
}
