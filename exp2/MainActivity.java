package com.example.scientificcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private StringBuilder expression = new StringBuilder();
    private ArithmeticParser parser = new ArithmeticParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Number buttons
        setupNumberButton(R.id.btn0);
        setupNumberButton(R.id.btn1);
        setupNumberButton(R.id.btn2);
        setupNumberButton(R.id.btn3);
        setupNumberButton(R.id.btn4);
        setupNumberButton(R.id.btn5);
        setupNumberButton(R.id.btn6);
        setupNumberButton(R.id.btn7);
        setupNumberButton(R.id.btn8);
        setupNumberButton(R.id.btn9);

        // Operator buttons
        setupOperatorButton(R.id.btnAdd, "+");
        setupOperatorButton(R.id.btnSubtract, "-");
        setupOperatorButton(R.id.btnMultiply, "*");
        setupOperatorButton(R.id.btnDivide, "/");

        // Special buttons
        setupSpecialButton(R.id.btnDecimal, ".");
        setupSpecialButton(R.id.btnClear, "C");
        setupSpecialButton(R.id.btnDelete, "Del");
        setupSpecialButton(R.id.btnEquals, "=");

        // Trigonometric buttons
        setupFunctionButton(R.id.btnSin, "sin");
        setupFunctionButton(R.id.btnCos, "cos");
        setupFunctionButton(R.id.btnTan, "tan");

        // Parenthesis buttons
        setupSpecialButton(R.id.btnParenthesisOpen, "(");
        setupSpecialButton(R.id.btnParenthesisClose, ")");
    }

    private void setupNumberButton(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> appendToExpression(button.getText().toString()));
    }

    private void setupOperatorButton(int buttonId, String operator) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> appendToExpression(" " + operator + " "));
    }

    private void setupSpecialButton(int buttonId, String action) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            switch (action) {
                case "C":
                    expression.setLength(0);
                    break;
                case "Del":
                    if (expression.length() > 0) {
                        expression.deleteCharAt(expression.length() - 1);
                        display.setText(expression.toString());
                    }
                    return;
                case "=":
                    try {
                        String result = String.valueOf(parser.parse(expression.toString()));
                        display.setText(result);
                        expression.setLength(0);
                        expression.append(result);
                    } catch (Exception e) {
                        display.setText("Error");
                    }
                    return;
                case "(":
                    appendToExpression("(");
                    return;
                case ")":
                    int openCount = 0;
                    int closeCount = 0;
                    for (int j = 0; j < expression.length(); j++) {
                        if (expression.charAt(j) == '(') openCount++;
                        if (expression.charAt(j) == ')') closeCount++;
                    }
                    if (openCount > closeCount) {
                        appendToExpression(")");
                    }
                    return;
            }
            display.setText(expression.toString());
        });
    }

    private void setupFunctionButton(int buttonId, String function) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> appendToExpression(function + "("));
    }

    private void appendToExpression(String text) {
        if (text.equals("(") || text.equals(")")) {
            expression.append(text);
            String currentText = display.getText().toString();
            display.setText(currentText + text);
        } else {
            String currentText = display.getText().toString();
            display.setText(currentText + text);
            expression.append(text);
        }
    }
}





