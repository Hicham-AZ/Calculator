package com.houarizegai.calculator.ui;

import com.houarizegai.calculator.theme.properties.Theme;
import com.houarizegai.calculator.theme.ThemeLoader;
import com.houarizegai.calculator.util.ExpressionParser;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import static com.houarizegai.calculator.util.ColorUtil.hex2Color;

public class CalculatorUI {

    private static final String FONT_NAME = "Comic Sans MS";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH_STANDARD = 410;
    private static final int WINDOW_WIDTH_SCIENTIFIC = 600;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTON_GAP = 5;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JPanel buttonPanel;
    private GridBagLayout gridBagLayout;
    private GridBagConstraints gridBagConstraints;

    // Standard buttons
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
    private JButton btnSqr;
    private JButton btnCube;
    private JButton btnPow;
    private JButton btnPi;
    private JButton btnE;
    private JButton btnFact;
    private JButton btn10x;
    private JButton btn2x;
    private JButton btnOpenParen;
    private JButton btnCloseParen;
    private JButton btnDegRad;

    private boolean isScientificMode = false;
    private boolean isDegrees = true;

    private final Map<String, Theme> themesMap;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH_STANDARD, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        initLayout();
        initCalculatorTypeSelector();
        initThemeSelector();
        initStandardButtons();

        window.setVisible(true);
    }

    private void initComponents() {
        // Input screen with scrolling and cursor
        inputScreen = new JTextField("0");
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        inputScreen.setHorizontalAlignment(JTextField.RIGHT);
        DefaultCaret caret = (DefaultCaret) inputScreen.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Button panel with GridBagLayout
        buttonPanel = new JPanel();
        gridBagLayout = new GridBagLayout();
        buttonPanel.setLayout(gridBagLayout);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(BUTTON_GAP, BUTTON_GAP, BUTTON_GAP, BUTTON_GAP);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
    }

    private void initLayout() {
        window.setLayout(new BorderLayout());

        // Top panel with type and theme selectors
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        window.add(topPanel, BorderLayout.NORTH);

        // Input screen panel
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new BorderLayout());
        screenPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        screenPanel.add(inputScreen, BorderLayout.CENTER);
        window.add(screenPanel, BorderLayout.CENTER);

        // Button panel with scroll pane (in case needed)
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        window.add(scrollPane, BorderLayout.SOUTH);
    }

    private void initCalculatorTypeSelector() {
        comboCalculatorType = createComboBox(new String[] {"Standard", "Scientific"}, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            String selectedItem = (String) event.getItem();
            if (selectedItem.equals("Scientific")) {
                window.setSize(WINDOW_WIDTH_SCIENTIFIC, WINDOW_HEIGHT);
                isScientificMode = true;
                initScientificButtons();
            } else {
                window.setSize(WINDOW_WIDTH_STANDARD, WINDOW_HEIGHT);
                isScientificMode = false;
                removeScientificButtons();
            }
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("Type:"));
        typePanel.add(comboCalculatorType);
        ((JPanel) window.getContentPane().getComponent(0)).add(typePanel);
    }

    private void initThemeSelector() {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });

        JPanel themePanel = new JPanel();
        themePanel.add(new JLabel("Theme:"));
        themePanel.add(comboTheme);
        ((JPanel) window.getContentPane().getComponent(0)).add(themePanel);

        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initStandardButtons() {
        buttonPanel.removeAll();

        // Row 0
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        btnC = createButton("C");
        btnC.addActionListener(e -> inputScreen.setText("0"));
        buttonPanel.add(btnC, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnBack = createButton("<- ");
        btnBack.addActionListener(e -> {
            String text = inputScreen.getText();
            if (text.length() > 1) {
                inputScreen.setText(text.substring(0, text.length() - 1));
            } else {
                inputScreen.setText("0");
            }
        });
        buttonPanel.add(btnBack, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btnMod = createButton("%");
        btnMod.addActionListener(e -> appendToScreen("%"));
        buttonPanel.add(btnMod, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnDiv = createButton("/");
        btnDiv.addActionListener(e -> appendToScreen("/"));
        buttonPanel.add(btnDiv, gridBagConstraints);

        // Row 1
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        btn7 = createButton("7");
        btn7.addActionListener(e -> appendToScreen("7"));
        buttonPanel.add(btn7, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn8 = createButton("8");
        btn8.addActionListener(e -> appendToScreen("8"));
        buttonPanel.add(btn8, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btn9 = createButton("9");
        btn9.addActionListener(e -> appendToScreen("9"));
        buttonPanel.add(btn9, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnMul = createButton("*");
        btnMul.addActionListener(e -> appendToScreen("*"));
        buttonPanel.add(btnMul, gridBagConstraints);

        // Row 2
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        btn4 = createButton("4");
        btn4.addActionListener(e -> appendToScreen("4"));
        buttonPanel.add(btn4, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn5 = createButton("5");
        btn5.addActionListener(e -> appendToScreen("5"));
        buttonPanel.add(btn5, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btn6 = createButton("6");
        btn6.addActionListener(e -> appendToScreen("6"));
        buttonPanel.add(btn6, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnSub = createButton("-");
        btnSub.addActionListener(e -> appendToScreen("-"));
        buttonPanel.add(btnSub, gridBagConstraints);

        // Row 3
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        btn1 = createButton("1");
        btn1.addActionListener(e -> appendToScreen("1"));
        buttonPanel.add(btn1, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn2 = createButton("2");
        btn2.addActionListener(e -> appendToScreen("2"));
        buttonPanel.add(btn2, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btn3 = createButton("3");
        btn3.addActionListener(e -> appendToScreen("3"));
        buttonPanel.add(btn3, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnAdd = createButton("+");
        btnAdd.addActionListener(e -> appendToScreen("+"));
        buttonPanel.add(btnAdd, gridBagConstraints);

        // Row 4
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        btnPoint = createButton(".");
        btnPoint.addActionListener(e -> appendToScreen("."));
        buttonPanel.add(btnPoint, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn0 = createButton("0");
        btn0.addActionListener(e -> appendToScreen("0"));
        buttonPanel.add(btn0, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        btnEqual = createButton("=");
        btnEqual.addActionListener(e -> calculateResult());
        buttonPanel.add(btnEqual, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void initScientificButtons() {
        buttonPanel.removeAll();

        // Row 0 (Scientific functions)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        btnSin = createButton("sin");
        btnSin.addActionListener(e -> appendToScreen("sin("));
        buttonPanel.add(btnSin, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnCos = createButton("cos");
        btnCos.addActionListener(e -> appendToScreen("cos("));
        buttonPanel.add(btnCos, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btnTan = createButton("tan");
        btnTan.addActionListener(e -> appendToScreen("tan("));
        buttonPanel.add(btnTan, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnAsin = createButton("asin");
        btnAsin.addActionListener(e -> appendToScreen("asin("));
        buttonPanel.add(btnAsin, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btnAcos = createButton("acos");
        btnAcos.addActionListener(e -> appendToScreen("acos("));
        buttonPanel.add(btnAcos, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btnAtan = createButton("atan");
        btnAtan.addActionListener(e -> appendToScreen("atan("));
        buttonPanel.add(btnAtan, gridBagConstraints);

        // Row 1 (More scientific functions)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        btnLog = createButton("log");
        btnLog.addActionListener(e -> appendToScreen("log("));
        buttonPanel.add(btnLog, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnLn = createButton("ln");
        btnLn.addActionListener(e -> appendToScreen("ln("));
        buttonPanel.add(btnLn, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btnSqrt = createButton("√");
        btnSqrt.addActionListener(e -> appendToScreen("sqrt("));
        buttonPanel.add(btnSqrt, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnSqr = createButton("x²");
        btnSqr.addActionListener(e -> appendToScreen("sqr("));
        buttonPanel.add(btnSqr, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btnCube = createButton("x³");
        btnCube.addActionListener(e -> appendToScreen("cube("));
        buttonPanel.add(btnCube, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btnPow = createButton("x^y");
        btnPow.addActionListener(e -> appendToScreen("^"));
        buttonPanel.add(btnPow, gridBagConstraints);

        // Row 2 (Constants and functions)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        btnPi = createButton("π");
        btnPi.addActionListener(e -> appendToScreen(String.valueOf(Math.PI)));
        buttonPanel.add(btnPi, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnE = createButton("e");
        btnE.addActionListener(e -> appendToScreen(String.valueOf(Math.E)));
        buttonPanel.add(btnE, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btnFact = createButton("!");
        btnFact.addActionListener(e -> appendToScreen("fact("));
        buttonPanel.add(btnFact, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btn10x = createButton("10^x");
        btn10x.addActionListener(e -> appendToScreen("10x("));
        buttonPanel.add(btn10x, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btn2x = createButton("2^x");
        btn2x.addActionListener(e -> appendToScreen("2x("));
        buttonPanel.add(btn2x, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btnDegRad = createButton("Deg");
        btnDegRad.addActionListener(e -> toggleDegRad());
        buttonPanel.add(btnDegRad, gridBagConstraints);

        // Row 3 (Parentheses and operators)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        btnOpenParen = createButton("(");
        btnOpenParen.addActionListener(e -> appendToScreen("("));
        buttonPanel.add(btnOpenParen, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnCloseParen = createButton(")");
        btnCloseParen.addActionListener(e -> appendToScreen(")"));
        buttonPanel.add(btnCloseParen, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btnMod = createButton("%");
        btnMod.addActionListener(e -> appendToScreen("%"));
        buttonPanel.add(btnMod, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btnDiv = createButton("/");
        btnDiv.addActionListener(e -> appendToScreen("/"));
        buttonPanel.add(btnDiv, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btnMul = createButton("*");
        btnMul.addActionListener(e -> appendToScreen("*"));
        buttonPanel.add(btnMul, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btnSub = createButton("-");
        btnSub.addActionListener(e -> appendToScreen("-"));
        buttonPanel.add(btnSub, gridBagConstraints);

        // Row 4 (Numbers)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        btn7 = createButton("7");
        btn7.addActionListener(e -> appendToScreen("7"));
        buttonPanel.add(btn7, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn8 = createButton("8");
        btn8.addActionListener(e -> appendToScreen("8"));
        buttonPanel.add(btn8, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btn9 = createButton("9");
        btn9.addActionListener(e -> appendToScreen("9"));
        buttonPanel.add(btn9, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btn4 = createButton("4");
        btn4.addActionListener(e -> appendToScreen("4"));
        buttonPanel.add(btn4, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btn5 = createButton("5");
        btn5.addActionListener(e -> appendToScreen("5"));
        buttonPanel.add(btn5, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btn6 = createButton("6");
        btn6.addActionListener(e -> appendToScreen("6"));
        buttonPanel.add(btn6, gridBagConstraints);

        // Row 5 (Numbers and operations)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        btn1 = createButton("1");
        btn1.addActionListener(e -> appendToScreen("1"));
        buttonPanel.add(btn1, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btn2 = createButton("2");
        btn2.addActionListener(e -> appendToScreen("2"));
        buttonPanel.add(btn2, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        btn3 = createButton("3");
        btn3.addActionListener(e -> appendToScreen("3"));
        buttonPanel.add(btn3, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        btn0 = createButton("0");
        btn0.addActionListener(e -> appendToScreen("0"));
        buttonPanel.add(btn0, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        btnPoint = createButton(".");
        btnPoint.addActionListener(e -> appendToScreen("."));
        buttonPanel.add(btnPoint, gridBagConstraints);

        gridBagConstraints.gridx = 5;
        btnAdd = createButton("+");
        btnAdd.addActionListener(e -> appendToScreen("+"));
        buttonPanel.add(btnAdd, gridBagConstraints);

        // Row 6 (Control buttons)
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        btnC = createButton("C");
        btnC.addActionListener(e -> inputScreen.setText("0"));
        buttonPanel.add(btnC, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        btnBack = createButton("<- ");
        btnBack.addActionListener(e -> {
            String text = inputScreen.getText();
            if (text.length() > 1) {
                inputScreen.setText(text.substring(0, text.length() - 1));
            } else {
                inputScreen.setText("0");
            }
        });
        buttonPanel.add(btnBack, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 4;
        btnEqual = createButton("=");
        btnEqual.addActionListener(e -> calculateResult());
        buttonPanel.add(btnEqual, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void removeScientificButtons() {
        initStandardButtons();
    }

    private void toggleDegRad() {
        isDegrees = !isDegrees;
        btnDegRad.setText(isDegrees ? "Deg" : "Rad");
    }

    private void appendToScreen(String text) {
        if (inputScreen.getText().equals("0")) {
            inputScreen.setText(text);
        } else {
            inputScreen.setText(inputScreen.getText() + text);
        }
    }

    private void calculateResult() {
        try {
            String expression = inputScreen.getText();
            // Convert degrees to radians if needed
            if (isDegrees) {
                // This is a simplified version - in a real implementation, we'd need to parse the expression
                // and convert only the arguments of trigonometric functions
            }
            double result = ExpressionParser.evaluate(expression);
            inputScreen.setText(String.valueOf(result));
        } catch (Exception e) {
            inputScreen.setText("Error");
        }
    }

    private JComboBox<String> createComboBox(String[] items, String tooltip) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setToolTipText(tooltip);
        comboBox.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        return comboBox;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        return button;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(hex2Color(theme.getNumbersBackground()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));

        // Apply theme to all buttons
        Component[] components = buttonPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                // Determine button type based on text to apply correct background color
                String buttonText = button.getText();
                if (buttonText.equals("=")) {
                    button.setBackground(hex2Color(theme.getBtnEqualBackground()));
                    button.setForeground(hex2Color(theme.getBtnEqualTextColor()));
                } else if (buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("×") || buttonText.equals("÷") || buttonText.equals("%")) {
                    button.setBackground(hex2Color(theme.getOperatorBackground()));
                    button.setForeground(hex2Color(theme.getTextColor()));
                } else {
                    button.setBackground(hex2Color(theme.getNumbersBackground()));
                    button.setForeground(hex2Color(theme.getTextColor()));
                }
                // Remove border since it's not defined in the theme
                button.setBorder(null);
            }
        }

        // Apply theme to combo boxes
        comboCalculatorType.setBackground(hex2Color(theme.getNumbersBackground()));
        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setBackground(hex2Color(theme.getNumbersBackground()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
    }
}
