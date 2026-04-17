import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculator extends JFrame {
    private JTextField displayField;
    private JLabel expressionLabel;

    private String currentInput = "0";
    private String previousInput = "";
    private String operator = null;
    private boolean shouldResetScreen = false;

    private final Color BG = new Color(13, 13, 13);
    private final Color PANEL_BG = new Color(10, 10, 10);
    private final Color NORMAL_TEXT = new Color(232, 255, 232);
    private final Color ACCENT = new Color(0, 255, 180);
    private final Color ERROR = new Color(255, 107, 107);

    public Calculator() {
    setTitle("Calculator - CALC MK1");
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setExtendedState(JFrame.MAXIMIZED_BOTH);
setResizable(true);
setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 14));
        mainPanel.setBackground(BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel brandLabel = new JLabel("CALC · MK1");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        brandLabel.setForeground(new Color(0, 255, 180, 90));
        brandLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(PANEL_BG);
        displayPanel.setBorder(new LineBorder(new Color(255, 255, 255, 13), 1));
        displayPanel.setPreferredSize(new Dimension(320, 95));

        expressionLabel = new JLabel(" ");
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        expressionLabel.setForeground(new Color(0, 255, 180, 102));
        expressionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        expressionLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));

        displayField = new JTextField("0");
        displayField.setEditable(false);
        displayField.setFont(new Font("SansSerif", Font.BOLD, 34));
        displayField.setForeground(NORMAL_TEXT);
        displayField.setBackground(PANEL_BG);
        displayField.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setFocusable(false);

        displayPanel.add(expressionLabel);
        displayPanel.add(displayField);

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(BG);

        String[][] buttonLabels = {
                {"AC", "+/−", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"0", ".", "="}
        };

        String[][] buttonOps = {
                {"AC", "sign", "%", "/"},
                {"7", "8", "9", "*"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"0", ".", "="}
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        for (int i = 0; i < buttonLabels.length; i++) {
            int gridX = 0;
            for (int j = 0; j < buttonLabels[i].length; j++) {
                String label = buttonLabels[i][j];
                String op = buttonOps[i][j];

                JButton btn = createButton(label, op);

                gbc.gridy = i;
                gbc.gridx = gridX;

                if (label.equals("0")) {
                    gbc.gridwidth = 2;
                    buttonsPanel.add(btn, gbc);
                    gridX += 2;
                } else {
                    gbc.gridwidth = 1;
                    buttonsPanel.add(btn, gbc);
                    gridX++;
                }
            }
        }

        mainPanel.add(brandLabel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setupKeyBindings(mainPanel);

        setVisible(true);
    }

    private JButton createButton(String label, String op) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Monospaced", Font.BOLD, op.equals("=") ? 22 : 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setPreferredSize(new Dimension(70, 62));
        btn.setOpaque(true);

        if (op.equals("AC")) {
            btn.setBackground(new Color(46, 30, 30));
            btn.setForeground(ERROR);
        } else if (op.equals("sign")) {
            btn.setBackground(new Color(37, 37, 37));
            btn.setForeground(new Color(255, 159, 107));
        } else if (op.equals("%") || op.equals("/") || op.equals("*") || op.equals("-") || op.equals("+")) {
            btn.setBackground(new Color(30, 46, 40));
            btn.setForeground(ACCENT);
        } else if (op.equals("=")) {
            btn.setBackground(new Color(0, 200, 150));
            btn.setForeground(new Color(0, 26, 18));
        } else {
            btn.setBackground(new Color(37, 37, 37));
            btn.setForeground(new Color(221, 221, 221));
        }

        btn.addActionListener(e -> handleButtonClick(op));
        return btn;
    }

    private void setupKeyBindings(JPanel panel) {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        for (char c = '0'; c <= '9'; c++) {
            final String s = String.valueOf(c);
            im.put(KeyStroke.getKeyStroke(c), "num_" + c);
            am.put("num_" + c, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inputNum(s);
                }
            });
        }

        bind(im, am, '.', "dot", () -> inputDot());
        bind(im, am, '+', "plus", () -> inputOp("+"));
        bind(im, am, '-', "minus", () -> inputOp("-"));
        bind(im, am, '*', "mul", () -> inputOp("*"));
        bind(im, am, '/', "div", () -> inputOp("/"));
        bind(im, am, KeyStroke.getKeyStroke("ENTER"), "enter", () -> calculate(false));
        bind(im, am, KeyStroke.getKeyStroke("BACK_SPACE"), "back", this::backspace);
        bind(im, am, KeyStroke.getKeyStroke("ESCAPE"), "esc", this::clearAll);
        bind(im, am, KeyStroke.getKeyStroke('%'), "percent", this::percent);
    }

    private void bind(InputMap im, ActionMap am, char key, String name, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void bind(InputMap im, ActionMap am, KeyStroke key, String name, Runnable action) {
        im.put(key, name);
        am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void handleButtonClick(String op) {
        switch (op) {
            case "AC" -> clearAll();
            case "sign" -> toggleSign();
            case "%" -> percent();
            case "=" -> calculate(false);
            case "." -> inputDot();
            case "+", "-", "*", "/" -> inputOp(op);
            default -> inputNum(op);
        }
    }

    private void updateDisplay() {
        displayField.setForeground(NORMAL_TEXT);
        displayField.setText(currentInput);
    }

    private void inputNum(String n) {
        if (shouldResetScreen) {
            currentInput = "";
            shouldResetScreen = false;
        }
        if (currentInput.equals("0")) {
            currentInput = "";
        }
        if (currentInput.length() >= 15) return;
        currentInput += n;
        updateDisplay();
    }

    private void inputDot() {
        if (shouldResetScreen) {
            currentInput = "0";
            shouldResetScreen = false;
        }
        if (currentInput.contains(".")) return;
        currentInput += ".";
        updateDisplay();
    }

    private void inputOp(String op) {
        if (operator != null && !shouldResetScreen) {
            calculate(true);
        }
        previousInput = currentInput;
        operator = op;
        shouldResetScreen = true;
        expressionLabel.setText(previousInput + " " + getSymbol(op));
    }

    private String getSymbol(String op) {
        return switch (op) {
            case "/" -> "÷";
            case "*" -> "×";
            case "-" -> "−";
            default -> op;
        };
    }

    private void calculate(boolean chained) {
        if (operator == null || (!chained && shouldResetScreen)) return;

        try {
            double a = Double.parseDouble(previousInput);
            double b = Double.parseDouble(currentInput);
            double res = switch (operator) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> {
                    if (b == 0) {
                        showError("÷ 0");
                        yield 0;
                    }
                    yield a / b;
                }
                default -> 0;
            };

            if (displayField.getText().startsWith("Error")) return;

            expressionLabel.setText(previousInput + " " + getSymbol(operator) + " " + currentInput + " =");
            currentInput = formatNumber(res);
            operator = null;
            shouldResetScreen = true;
            updateDisplay();
        } catch (NumberFormatException e) {
            showError("Error");
        }
    }

    private String formatNumber(double num) {
        if (Double.isNaN(num) || Double.isInfinite(num)) return "Error";
        if (num == (long) num) return String.valueOf((long) num);
        String s = String.format("%.10f", num).replaceAll("0+$", "").replaceAll("\\.$", "");
        return s.length() > 15 ? String.format("%.10g", num) : s;
    }

    private void clearAll() {
        currentInput = "0";
        previousInput = "";
        operator = null;
        shouldResetScreen = false;
        expressionLabel.setText(" ");
        updateDisplay();
    }

    private void toggleSign() {
        if (currentInput.equals("0")) return;
        currentInput = formatNumber(Double.parseDouble(currentInput) * -1);
        updateDisplay();
    }

    private void percent() {
        currentInput = formatNumber(Double.parseDouble(currentInput) / 100.0);
        updateDisplay();
    }

    private void backspace() {
        if (shouldResetScreen) return;
        if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            currentInput = "0";
        }
        updateDisplay();
    }

    private void showError(String msg) {
        displayField.setForeground(ERROR);
        displayField.setText("Error: " + msg);
        currentInput = "0";
        previousInput = "";
        operator = null;
        shouldResetScreen = true;
        expressionLabel.setText(" ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}