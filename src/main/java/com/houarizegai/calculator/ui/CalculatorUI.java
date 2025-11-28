package com.houarizegai.calculator.ui;

import com.houarizegai.calculator.theme.properties.Theme;
import com.houarizegai.calculator.theme.ThemeLoader;
import com.houarizegai.calculator.util.ExpressionParser;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.*;

import static com.houarizegai.calculator.util.ColorUtil.hex2Color;

public class CalculatorUI {
    private static final String FONT_NAME = "Comic Sans MS";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int STANDARD_WINDOW_WIDTH = 410;
    private static final int STANDARD_WINDOW_HEIGHT = 600;
    private static final int SCIENTIFIC_WINDOW_WIDTH = 600;
    private static final int SCIENTIFIC_WINDOW_HEIGHT = 650;
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 60;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JButton btnC;
    private JButton btnBack;
    private JButton btnMod;
    private JButton btnDiv;
    private JButton btnMul;
    private JButton btnSub;
    private JButton btnAdd;
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
    private JButton btnEqual;

    // Scientific buttons
    private JButton btnSin;
    private JButton btnCos;
    private JButton btnTan;
    private JButton btnAsin;
    private JButton btnAcos;
    private JButton btnAtan;
    private JButton btnLog;
    private JButton btnLn;
    private JButton btnSqrt;
    private JButton btnSquare;
    private JButton btnCube;
    private JButton btnPower;
    private JButton btnPi;
    private JButton btnE;
    private JButton btnFactorial;
    private JButton btnTenPowerX;
    private JButton btnTwoPowerX;
    private JButton btnLeftParen;
    private JButton btnRightParen;
    private JButton btnDegRad;

    private boolean isRadians = true;
    private final Map<String, Theme> themesMap;
    private JPanel buttonPanel;
    private GridBagConstraints gbc;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(STANDARD_WINDOW_WIDTH, STANDARD_WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        initButtonPanel();
        initCalculatorTypeSelector();
        initThemeSelector();

        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private void initInputScreen(JPanel topPanel) {
        JPanel screenPanel = new JPanel(new BorderLayout());
        screenPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputScreen = new JTextField("0") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw cursor
                try {
                    int caretPosition = getCaretPosition();
                    Rectangle r = modelToView(caretPosition);
                    if (r != null) {
                        g.setColor(getForeground());
                        g.fillRect(r.x, r.y, 2, r.height);
                    }
                } catch (BadLocationException e) {
                    // Ignore
                }
            }
        };
        inputScreen.setEditable(true);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        inputScreen.setHorizontalAlignment(JTextField.RIGHT);
        inputScreen.setCaretColor(Color.BLACK);
        inputScreen.setCaretPosition(inputScreen.getText().length());

        // Add focus listener to show cursor
        inputScreen.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputScreen.setCaretPosition(inputScreen.getText().length());
            }
        });

        // Add key listener for cursor movement
        inputScreen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caretPosition = inputScreen.getCaretPosition();
                int textLength = inputScreen.getText().length();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (caretPosition > 0) {
                            inputScreen.setCaretPosition(caretPosition - 1);
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (caretPosition < textLength) {
                            inputScreen.setCaretPosition(caretPosition + 1);
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_DELETE:
                        if (caretPosition < textLength) {
                            inputScreen.setText(inputScreen.getText().substring(0, caretPosition) +
                                    inputScreen.getText().substring(caretPosition + 1));
                            inputScreen.setCaretPosition(caretPosition);
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        if (caretPosition > 0) {
                            inputScreen.setText(inputScreen.getText().substring(0, caretPosition - 1) +
                                    inputScreen.getText().substring(caretPosition));
                            inputScreen.setCaretPosition(caretPosition - 1);
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        calculateResult();
                        e.consume();
                        break;
                }
            }
        });

        // Make text field scrollable
        JScrollPane scrollPane = new JScrollPane(inputScreen);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        screenPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(screenPanel, BorderLayout.CENTER);
    }

    private void initButtonPanel() {
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        initStandardButtons();
        window.add(buttonPanel, BorderLayout.CENTER);
    }

    private void initStandardButtons() {
        buttonPanel.removeAll();

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        btnC = createButton("C");
        btnC.addActionListener(e -> {
            inputScreen.setText("0");
            inputScreen.setCaretPosition(1);
        });
        buttonPanel.add(btnC, gbc);

        gbc.gridx = 1;
        btnBack = createButton("<- ");
        btnBack.addActionListener(e -> {
            int caretPosition = inputScreen.getCaretPosition();
            if (caretPosition > 0) {
                inputScreen.setText(inputScreen.getText().substring(0, caretPosition - 1) +
                        inputScreen.getText().substring(caretPosition));
                inputScreen.setCaretPosition(caretPosition - 1);
            } else if (inputScreen.getText().length() > 0) {
                inputScreen.setText("0");
                inputScreen.setCaretPosition(1);
            }
        });
        buttonPanel.add(btnBack, gbc);

        gbc.gridx = 2;
        btnMod = createButton("%");
        btnMod.addActionListener(e -> insertText("%"));
        buttonPanel.add(btnMod, gbc);

        gbc.gridx = 3;
        btnDiv = createButton("/");
        btnDiv.addActionListener(e -> insertText("/"));
        buttonPanel.add(btnDiv, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        btn7 = createButton("7");
        btn7.addActionListener(e -> insertText("7"));
        buttonPanel.add(btn7, gbc);

        gbc.gridx = 1;
        btn8 = createButton("8");
        btn8.addActionListener(e -> insertText("8"));
        buttonPanel.add(btn8, gbc);

        gbc.gridx = 2;
        btn9 = createButton("9");
        btn9.addActionListener(e -> insertText("9"));
        buttonPanel.add(btn9, gbc);

        gbc.gridx = 3;
        btnMul = createButton("*");
        btnMul.addActionListener(e -> insertText("*"));
        buttonPanel.add(btnMul, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        btn4 = createButton("4");
        btn4.addActionListener(e -> insertText("4"));
        buttonPanel.add(btn4, gbc);

        gbc.gridx = 1;
        btn5 = createButton("5");
        btn5.addActionListener(e -> insertText("5"));
        buttonPanel.add(btn5, gbc);

        gbc.gridx = 2;
        btn6 = createButton("6");
        btn6.addActionListener(e -> insertText("6"));
        buttonPanel.add(btn6, gbc);

        gbc.gridx = 3;
        btnSub = createButton("-");
        btnSub.addActionListener(e -> insertText("-"));
        buttonPanel.add(btnSub, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        btn1 = createButton("1");
        btn1.addActionListener(e -> insertText("1"));
        buttonPanel.add(btn1, gbc);

        gbc.gridx = 1;
        btn2 = createButton("2");
        btn2.addActionListener(e -> insertText("2"));
        buttonPanel.add(btn2, gbc);

        gbc.gridx = 2;
        btn3 = createButton("3");
        btn3.addActionListener(e -> insertText("3"));
        buttonPanel.add(btn3, gbc);

        gbc.gridx = 3;
        btnAdd = createButton("+");
        btnAdd.addActionListener(e -> insertText("+"));
        buttonPanel.add(btnAdd, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        btnPoint = createButton(".");
        btnPoint.addActionListener(e -> insertText("."));
        buttonPanel.add(btnPoint, gbc);

        gbc.gridx = 1;
        btn0 = createButton("0");
        btn0.addActionListener(e -> insertText("0"));
        buttonPanel.add(btn0, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        btnEqual = createButton("=");
        btnEqual.addActionListener(e -> calculateResult());
        buttonPanel.add(btnEqual, gbc);
        gbc.gridwidth = 1;

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void initScientificButtons() {
        buttonPanel.removeAll();

        // Scientific buttons - top row
        gbc.gridx = 0;
        gbc.gridy = 0;
        btnSin = createButton("sin");
        btnSin.addActionListener(e -> insertText("sin("));
        buttonPanel.add(btnSin, gbc);

        gbc.gridx = 1;
        btnCos = createButton("cos");
        btnCos.addActionListener(e -> insertText("cos("));
        buttonPanel.add(btnCos, gbc);

        gbc.gridx = 2;
        btnTan = createButton("tan");
        btnTan.addActionListener(e -> insertText("tan("));
        buttonPanel.add(btnTan, gbc);

        gbc.gridx = 3;
        btnAsin = createButton("asin");
        btnAsin.addActionListener(e -> insertText("asin("));
        buttonPanel.add(btnAsin, gbc);

        gbc.gridx = 4;
        btnAcos = createButton("acos");
        btnAcos.addActionListener(e -> insertText("acos("));
        buttonPanel.add(btnAcos, gbc);

        gbc.gridx = 5;
        btnAtan = createButton("atan");
        btnAtan.addActionListener(e -> insertText("atan("));
        buttonPanel.add(btnAtan, gbc);

        // Second row
        gbc.gridx = 0;
        gbc.gridy = 1;
        btnLog = createButton("log");
        btnLog.addActionListener(e -> insertText("log("));
        buttonPanel.add(btnLog, gbc);

        gbc.gridx = 1;
        btnLn = createButton("ln");
        btnLn.addActionListener(e -> insertText("ln("));
        buttonPanel.add(btnLn, gbc);

        gbc.gridx = 2;
        btnSqrt = createButton("√");
        btnSqrt.addActionListener(e -> insertText("sqrt("));
        buttonPanel.add(btnSqrt, gbc);

        gbc.gridx = 3;
        btnSquare = createButton("x²");
        btnSquare.addActionListener(e -> insertText("square("));
        buttonPanel.add(btnSquare, gbc);

        gbc.gridx = 4;
        btnCube = createButton("x³");
        btnCube.addActionListener(e -> insertText("cube("));
        buttonPanel.add(btnCube, gbc);

        gbc.gridx = 5;
        btnPower = createButton("x^y");
        btnPower.addActionListener(e -> insertText("^"));
        buttonPanel.add(btnPower, gbc);

        // Third row
        gbc.gridx = 0;
        gbc.gridy = 2;
        btnPi = createButton("π");
        btnPi.addActionListener(e -> insertText("pi"));
        buttonPanel.add(btnPi, gbc);

        gbc.gridx = 1;
        btnE = createButton("e");
        btnE.addActionListener(e -> insertText("e"));
        buttonPanel.add(btnE, gbc);

        gbc.gridx = 2;
        btnFactorial = createButton("!");
        btnFactorial.addActionListener(e -> insertText("factorial("));
        buttonPanel.add(btnFactorial, gbc);

        gbc.gridx = 3;
        btnTenPowerX = createButton("10^x");
        btnTenPowerX.addActionListener(e -> insertText("tenPowerX("));
        buttonPanel.add(btnTenPowerX, gbc);

        gbc.gridx = 4;
        btnTwoPowerX = createButton("2^x");
        btnTwoPowerX.addActionListener(e -> insertText("twoPowerX("));
        buttonPanel.add(btnTwoPowerX, gbc);

        gbc.gridx = 5;
        btnDegRad = createButton(isRadians ? "Rad" : "Deg");
        btnDegRad.addActionListener(e -> {
            isRadians = !isRadians;
            btnDegRad.setText(isRadians ? "Rad" : "Deg");
        });
        buttonPanel.add(btnDegRad, gbc);

        // Fourth row
        gbc.gridx = 0;
        gbc.gridy = 3;
        btnLeftParen = createButton("(");
        btnLeftParen.addActionListener(e -> insertText("("));
        buttonPanel.add(btnLeftParen, gbc);

        gbc.gridx = 1;
        btnRightParen = createButton(")");
        btnRightParen.addActionListener(e -> insertText(")"));
        buttonPanel.add(btnRightParen, gbc);

        gbc.gridx = 2;
        btnC = createButton("C");
        btnC.addActionListener(e -> {
            inputScreen.setText("0");
            inputScreen.setCaretPosition(1);
        });
        buttonPanel.add(btnC, gbc);

        gbc.gridx = 3;
        btnBack = createButton("<- ");
        btnBack.addActionListener(e -> {
            int caretPosition = inputScreen.getCaretPosition();
            if (caretPosition > 0) {
                inputScreen.setText(inputScreen.getText().substring(0, caretPosition - 1) +
                        inputScreen.getText().substring(caretPosition));
                inputScreen.setCaretPosition(caretPosition - 1);
            } else if (inputScreen.getText().length() > 0) {
                inputScreen.setText("0");
                inputScreen.setCaretPosition(1);
            }
        });
        buttonPanel.add(btnBack, gbc);

        gbc.gridx = 4;
        btnMod = createButton("%");
        btnMod.addActionListener(e -> insertText("%"));
        buttonPanel.add(btnMod, gbc);

        gbc.gridx = 5;
        btnDiv = createButton("/");
        btnDiv.addActionListener(e -> insertText("/"));
        buttonPanel.add(btnDiv, gbc);

        // Fifth row
        gbc.gridx = 0;
        gbc.gridy = 4;
        btn7 = createButton("7");
        btn7.addActionListener(e -> insertText("7"));
        buttonPanel.add(btn7, gbc);

        gbc.gridx = 1;
        btn8 = createButton("8");
        btn8.addActionListener(e -> insertText("8"));
        buttonPanel.add(btn8, gbc);

        gbc.gridx = 2;
        btn9 = createButton("9");
        btn9.addActionListener(e -> insertText("9"));
        buttonPanel.add(btn9, gbc);

        gbc.gridx = 3;
        btnMul = createButton("*");
        btnMul.addActionListener(e -> insertText("*"));
        buttonPanel.add(btnMul, gbc);

        gbc.gridx = 4;
        btnSub = createButton("-");
        btnSub.addActionListener(e -> insertText("-"));
        buttonPanel.add(btnSub, gbc);

        gbc.gridx = 5;
        btnAdd = createButton("+");
        btnAdd.addActionListener(e -> insertText("+"));
        buttonPanel.add(btnAdd, gbc);

        // Sixth row
        gbc.gridx = 0;
        gbc.gridy = 5;
        btnPoint = createButton(".");
        btnPoint.addActionListener(e -> insertText("."));
        buttonPanel.add(btnPoint, gbc);

        gbc.gridx = 1;
        btn0 = createButton("0");
        btn0.addActionListener(e -> insertText("0"));
        buttonPanel.add(btn0, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 4;
        btnEqual = createButton("=");
        btnEqual.addActionListener(e -> calculateResult());
        buttonPanel.add(btnEqual, gbc);
        gbc.gridwidth = 1;

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void initCalculatorTypeSelector() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Add calculator type and theme selectors to a sub-panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific"}, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            String selectedItem = (String) event.getItem();
            switch (selectedItem) {
                case "Standard":
                    window.setSize(STANDARD_WINDOW_WIDTH, STANDARD_WINDOW_HEIGHT);
                    initStandardButtons();
                    break;
                case "Scientific":
                    window.setSize(SCIENTIFIC_WINDOW_WIDTH, SCIENTIFIC_WINDOW_HEIGHT);
                    initScientificButtons();
                    break;
            }
            window.setLocationRelativeTo(null);
        });
        selectorPanel.add(comboCalculatorType);
        topPanel.add(selectorPanel, BorderLayout.PAGE_START);

        // Add input screen to the top panel
        initInputScreen(topPanel);

        window.add(topPanel, BorderLayout.PAGE_START);
    }

    private void initThemeSelector() {
        JPanel topPanel = (JPanel) window.getContentPane().getComponent(0);
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });

        topPanel.add(comboTheme);

        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private JComboBox<String> createComboBox(String[] items, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setPreferredSize(new Dimension(140, 25));
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return combo;
    }

    private JButton createButton(String label) {
        JButton btn = new JButton(label);
        btn.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        btn.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        return btn;
    }

    private void insertText(String text) {
        int caretPosition = inputScreen.getCaretPosition();
        String currentText = inputScreen.getText();
        String newText;

        if (currentText.equals("0") && caretPosition == 1 && !text.equals(".")) {
            newText = text;
            caretPosition = text.length();
        } else {
            newText = currentText.substring(0, caretPosition) + text + currentText.substring(caretPosition);
            caretPosition += text.length();
        }

        inputScreen.setText(newText);
        inputScreen.setCaretPosition(caretPosition);
    }

    private void calculateResult() {
        String expression = inputScreen.getText();
        if (expression.isEmpty() || expression.equals("0")) {
            return;
        }

        try {
            // Convert degrees to radians if needed
            if (!isRadians) {
                // This is a simplified approach - in a real implementation, we would parse the expression
                // and convert all trigonometric function arguments from degrees to radians
                // For now, we'll just evaluate as is (the ExpressionParser uses radians by default)
            }

            double result = ExpressionParser.evaluate(expression);

            // Format the result to remove trailing .0 if it's an integer
            if (result == Math.floor(result)) {
                inputScreen.setText(String.valueOf((long) result));
            } else {
                inputScreen.setText(String.valueOf(result));
            }
            inputScreen.setCaretPosition(inputScreen.getText().length());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window, "Error: " + e.getMessage(), "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));

        // Get top panel
        JPanel topPanel = (JPanel) window.getContentPane().getComponent(0);
        topPanel.setBackground(hex2Color(theme.getApplicationBackground()));

        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setBackground(hex2Color(theme.getApplicationBackground()));

        // Set button colors
        setButtonColors(btn0, theme);
        setButtonColors(btn1, theme);
        setButtonColors(btn2, theme);
        setButtonColors(btn3, theme);
        setButtonColors(btn4, theme);
        setButtonColors(btn5, theme);
        setButtonColors(btn6, theme);
        setButtonColors(btn7, theme);
        setButtonColors(btn8, theme);
        setButtonColors(btn9, theme);
        setButtonColors(btnPoint, theme);
        setButtonColors(btnC, theme);
        setButtonColors(btnBack, theme);
        setButtonColors(btnMod, theme);
        setButtonColors(btnDiv, theme);
        setButtonColors(btnMul, theme);
        setButtonColors(btnSub, theme);
        setButtonColors(btnAdd, theme);
        setButtonColors(btnEqual, theme);

        // Set scientific button colors if they exist
        if (btnSin != null) setButtonColors(btnSin, theme);
        if (btnCos != null) setButtonColors(btnCos, theme);
        if (btnTan != null) setButtonColors(btnTan, theme);
        if (btnAsin != null) setButtonColors(btnAsin, theme);
        if (btnAcos != null) setButtonColors(btnAcos, theme);
        if (btnAtan != null) setButtonColors(btnAtan, theme);
        if (btnLog != null) setButtonColors(btnLog, theme);
        if (btnLn != null) setButtonColors(btnLn, theme);
        if (btnSqrt != null) setButtonColors(btnSqrt, theme);
        if (btnSquare != null) setButtonColors(btnSquare, theme);
        if (btnCube != null) setButtonColors(btnCube, theme);
        if (btnPower != null) setButtonColors(btnPower, theme);
        if (btnPi != null) setButtonColors(btnPi, theme);
        if (btnE != null) setButtonColors(btnE, theme);
        if (btnFactorial != null) setButtonColors(btnFactorial, theme);
        if (btnTenPowerX != null) setButtonColors(btnTenPowerX, theme);
        if (btnTwoPowerX != null) setButtonColors(btnTwoPowerX, theme);
        if (btnLeftParen != null) setButtonColors(btnLeftParen, theme);
        if (btnRightParen != null) setButtonColors(btnRightParen, theme);
        if (btnDegRad != null) setButtonColors(btnDegRad, theme);
    }

    private void setButtonColors(JButton button, Theme theme) {
        if (isNumberButton(button)) {
            button.setBackground(hex2Color(theme.getNumbersBackground()));
            button.setForeground(hex2Color(theme.getTextColor()));
        } else if (button == btnEqual) {
            button.setBackground(hex2Color(theme.getBtnEqualBackground()));
            button.setForeground(hex2Color(theme.getBtnEqualTextColor()));
        } else {
            button.setBackground(hex2Color(theme.getOperatorBackground()));
            button.setForeground(hex2Color(theme.getTextColor()));
        }
    }

    private boolean isNumberButton(JButton button) {
        String label = button.getText();
        return label.equals("0") || label.equals("1") || label.equals("2") || label.equals("3") ||
                label.equals("4") || label.equals("5") || label.equals("6") || label.equals("7") ||
                label.equals("8") || label.equals("9") || label.equals(".");
    }
}
