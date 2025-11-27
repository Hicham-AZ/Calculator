package com.houarizegai.calculator.ui;

import com.houarizegai.calculator.controller.CalculatorController;
import com.houarizegai.calculator.model.CalculatorModel;
import com.houarizegai.calculator.model.CalculatorModel.CalculationHistory;
import com.houarizegai.calculator.theme.properties.Theme;
import com.houarizegai.calculator.theme.ThemeLoader;
import com.houarizegai.calculator.util.ColorUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.List;

public class CalculatorUI implements CalculatorController.View {
    private static final String FONT_NAME = "Comic Sans MS";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH = 490; // Increased width for scientific functions
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 70;
    private static final int MARGIN_X = 20;
    private static final int MARGIN_Y = 60;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    
    // Number buttons
    private JButton btn0;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JButton btn7;
    private JButton btn8;
    private JButton btn9;
    private JButton btnPoint;
    
    // Standard operator buttons
    private JButton btnC;
    private JButton btnBack;
    private JButton btnMod;
    private JButton btnDiv;
    private JButton btnMul;
    private JButton btnSub;
    private JButton btnAdd;
    private JButton btnEqual;
    
    // Scientific function buttons
    private JButton btnRoot;
    private JButton btnPower;
    private JButton btnLog;
    private JButton btnSin;
    private JButton btnCos;
    private JButton btnTan;
    
    // Menu components
    private JMenuItem menuItemSaveHistory;
    
    // History dialog components
    private JDialog historyDialog;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    private final Map<String, Theme> themesMap;
    private final CalculatorModel model;
    private final CalculatorController controller;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();
        model = new CalculatorModel();
        controller = new CalculatorController(model);
        controller.setView(this);

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);

        int[] columns = {MARGIN_X, MARGIN_X + 90, MARGIN_X + 90 * 2, MARGIN_X + 90 * 3, MARGIN_X + 90 * 4};
        int[] rows = {MARGIN_Y, MARGIN_Y + 100, MARGIN_Y + 100 + 80, MARGIN_Y + 100 + 80 * 2, MARGIN_Y + 100 + 80 * 3, MARGIN_Y + 100 + 80 * 4};

        initMenuBar();
        initInputScreen(columns, rows);
        initButtons(columns, rows);
        initCalculatorTypeSelector();
        initThemeSelector();
        initHistoryDialog();

        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        menuItemSaveHistory = new JMenuItem("Save History...");
        menuItemSaveHistory.addActionListener(this::handleSaveHistory);
        
        fileMenu.add(menuItemSaveHistory);
        menuBar.add(fileMenu);
        
        JMenu viewMenu = new JMenu("View");
        JMenuItem showHistoryItem = new JMenuItem("Show History");
        showHistoryItem.addActionListener(e -> historyDialog.setVisible(true));
        viewMenu.add(showHistoryItem);
        menuBar.add(viewMenu);
        
        window.setJMenuBar(menuBar);
    }

    private void initHistoryDialog() {
        historyDialog = new JDialog(window, "Calculation History", true);
        historyDialog.setSize(400, 300);
        historyDialog.setLocationRelativeTo(window);
        
        // Create table model
        historyTableModel = new DefaultTableModel(new Object[]{"Expression", "Result"}, 0);
        historyTable = new JTable(historyTableModel);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Add clear button
        JButton btnClearHistory = new JButton("Clear History");
        btnClearHistory.addActionListener(e -> {
            model.getHistory().clear();
            updateHistoryTable();
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClearHistory);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSaveHistory(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(window);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            // Ensure file has .json extension
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }
            controller.saveHistoryToJson(filePath);
        }
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void updateHistoryTable() {
        historyTableModel.setRowCount(0); // Clear existing rows
        List<CalculationHistory> history = controller.getHistory();
        for (CalculationHistory item : history) {
            historyTableModel.addRow(new Object[]{item.getExpression(), item.getResult()});
        }
    }

    private void initInputScreen(int[] columns, int[] rows) {
        inputScreen = new JTextField("0");
        inputScreen.setBounds(columns[0], rows[0], 430, 70); // Increased width
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        window.add(inputScreen);
    }

    private void initCalculatorTypeSelector() {
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific"}, 20, 30, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedItem = (String) event.getItem();
            switch (selectedItem) {
                case "Standard":
                    window.setSize(410, WINDOW_HEIGHT); // Original width
                    btnRoot.setVisible(false);
                    btnPower.setVisible(false);
                    btnLog.setVisible(false);
                    btnSin.setVisible(false);
                    btnCos.setVisible(false);
                    btnTan.setVisible(false);
                    break;
                case "Scientific":
                    window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    btnRoot.setVisible(true);
                    btnPower.setVisible(true);
                    btnLog.setVisible(true);
                    btnSin.setVisible(true);
                    btnCos.setVisible(true);
                    btnTan.setVisible(true);
                    break;
            }
        });
    }

    private void initThemeSelector() {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), 230, 30, "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });

        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initButtons(int[] columns, int[] rows) {
        // Standard buttons
        btnC = createButton("C", columns[0], rows[1]);
        btnC.addActionListener(event -> controller.handleClear());

        btnBack = createButton("<-", columns[1], rows[1]);
        btnBack.addActionListener(event -> controller.handleBackspace());

        btnMod = createButton("%", columns[2], rows[1]);
        btnMod.addActionListener(event -> controller.handleOperator('%'));

        btnDiv = createButton("/", columns[3], rows[1]);
        btnDiv.addActionListener(event -> controller.handleOperator('/'));

        btn7 = createButton("7", columns[0], rows[2]);
        btn7.addActionListener(event -> controller.handleNumberInput("7"));

        btn8 = createButton("8", columns[1], rows[2]);
        btn8.addActionListener(event -> controller.handleNumberInput("8"));

        btn9 = createButton("9", columns[2], rows[2]);
        btn9.addActionListener(event -> controller.handleNumberInput("9"));

        btnMul = createButton("*", columns[3], rows[2]);
        btnMul.addActionListener(event -> controller.handleOperator('*'));

        btn4 = createButton("4", columns[0], rows[3]);
        btn4.addActionListener(event -> controller.handleNumberInput("4"));

        btn5 = createButton("5", columns[1], rows[3]);
        btn5.addActionListener(event -> controller.handleNumberInput("5"));

        btn6 = createButton("6", columns[2], rows[3]);
        btn6.addActionListener(event -> controller.handleNumberInput("6"));

        btnSub = createButton("-", columns[3], rows[3]);
        btnSub.addActionListener(event -> controller.handleOperator('-'));

        btn1 = createButton("1", columns[0], rows[4]);
        btn1.addActionListener(event -> controller.handleNumberInput("1"));

        btn2 = createButton("2", columns[1], rows[4]);
        btn2.addActionListener(event -> controller.handleNumberInput("2"));

        btn3 = createButton("3", columns[2], rows[4]);
        btn3.addActionListener(event -> controller.handleNumberInput("3"));

        btnAdd = createButton("+", columns[3], rows[4]);
        btnAdd.addActionListener(event -> controller.handleOperator('+'));

        btnPoint = createButton(".", columns[0], rows[5]);
        btnPoint.addActionListener(event -> controller.handlePointInput());

        btn0 = createButton("0", columns[1], rows[5]);
        btn0.addActionListener(event -> controller.handleNumberInput("0"));

        btnEqual = createButton("=", columns[2], rows[5]);
        btnEqual.addActionListener(event -> {
            controller.handleEquals();
            updateHistoryTable(); // Update history table after calculation
        });
        btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);

        // Scientific function buttons
        btnRoot = createButton("sqrt", columns[4], rows[1]);
        btnRoot.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnRoot.addActionListener(event -> controller.handleUnaryFunction('s'));
        btnPower = createButton("pow", columns[4], rows[2]);
        btnPower.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnPower.addActionListener(event -> controller.handleOperator('^'));

        btnLog = createButton("ln", columns[4], rows[3]);
        btnLog.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnLog.addActionListener(event -> controller.handleUnaryFunction('l'));

        btnSin = createButton("sin", columns[4], rows[4]);
        btnSin.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnSin.addActionListener(event -> controller.handleUnaryFunction('s'));

        btnCos = createButton("cos", columns[4], rows[5]);
        btnCos.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnCos.addActionListener(event -> controller.handleUnaryFunction('c'));

        btnTan = createButton("tan", columns[4], rows[6]); // New row for tan button
        btnTan.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnTan.addActionListener(event -> controller.handleUnaryFunction('t'));
        
        // Initially hide scientific buttons
        btnRoot.setVisible(false);
        btnPower.setVisible(false);
        btnLog.setVisible(false);
        btnSin.setVisible(false);
        btnCos.setVisible(false);
        btnTan.setVisible(false);
    }

    private JComboBox<String> createComboBox(String[] items, int x, int y, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBounds(x, y, 140, 25);
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        window.add(combo);

        return combo;
    }

    private JButton createButton(String label, int x, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        btn.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);

        return btn;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(ColorUtil.hex2Color(theme.getApplicationBackground()));

        // Apply theme to all components
        comboCalculatorType.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        comboTheme.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        inputScreen.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        
        // Number buttons
        btn0.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn1.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn2.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn3.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn4.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn5.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn6.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn7.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn8.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btn9.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnPoint.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        
        // Operator buttons
        btnC.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnBack.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnMod.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnDiv.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnMul.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnSub.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnAdd.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        
        // Scientific function buttons
        btnRoot.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnLog.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnPower.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnSin.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnCos.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        btnTan.setForeground(ColorUtil.hex2Color(theme.getTextColor()));
        
        // Equal button
        btnEqual.setForeground(ColorUtil.hex2Color(theme.getBtnEqualTextColor()));

        // Background colors
        comboCalculatorType.setBackground(ColorUtil.hex2Color(theme.getApplicationBackground()));
        comboTheme.setBackground(ColorUtil.hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(ColorUtil.hex2Color(theme.getApplicationBackground()));
        
        // Number buttons background
        btn0.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn1.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn2.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn3.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn4.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn5.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn6.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn7.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn8.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btn9.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        btnPoint.setBackground(ColorUtil.hex2Color(theme.getNumbersBackground()));
        
        // Operator buttons background
        btnC.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnBack.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnMod.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnDiv.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnMul.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnSub.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnAdd.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        
        // Scientific function buttons background
        btnRoot.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnLog.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnPower.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnSin.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnCos.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        btnTan.setBackground(ColorUtil.hex2Color(theme.getOperatorBackground()));
        
        // Equal button background
        btnEqual.setBackground(ColorUtil.hex2Color(theme.getBtnEqualBackground()));
    }
}