package com.example.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilNum1, tilNum2;
    private TextInputEditText etNum1, etNum2;
    private TextView tvResultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Views
        tilNum1 = findViewById(R.id.tilNum1);
        tilNum2 = findViewById(R.id.tilNum2);
        etNum1 = findViewById(R.id.etNum1);
        etNum2 = findViewById(R.id.etNum2);
        tvResultValue = findViewById(R.id.tvResultValue);

        MaterialButton btnAdd = findViewById(R.id.btn_add);
        MaterialButton btnSub = findViewById(R.id.btn_sub);
        MaterialButton btnMul = findViewById(R.id.btn_mul);
        MaterialButton btnDiv = findViewById(R.id.btn_div);
        MaterialButton btnMod = findViewById(R.id.btn_mod);
        MaterialButton btnPow = findViewById(R.id.btn_pow);
        MaterialButton btnClear = findViewById(R.id.btn_clear);

        // Set Click Listeners
        btnAdd.setOnClickListener(v -> performCalculation('+'));
        btnSub.setOnClickListener(v -> performCalculation('-'));
        btnMul.setOnClickListener(v -> performCalculation('*'));
        btnDiv.setOnClickListener(v -> performCalculation('/'));
        btnMod.setOnClickListener(v -> performCalculation('%'));
        btnPow.setOnClickListener(v -> performCalculation('^'));

        btnClear.setOnClickListener(v -> clearFields());
    }

    private void performCalculation(char operator) {
        // Clear previous errors
        tilNum1.setError(null);
        tilNum2.setError(null);

        String input1 = etNum1.getText() != null ? etNum1.getText().toString().trim() : "";
        String input2 = etNum2.getText() != null ? etNum2.getText().toString().trim() : "";

        // Validate empty fields
        if (TextUtils.isEmpty(input1)) {
            tilNum1.setError(getString(R.string.err_empty_input));
            return;
        }
        if (TextUtils.isEmpty(input2)) {
            tilNum2.setError(getString(R.string.err_empty_input));
            return;
        }

        double num1, num2;
        try {
            num1 = Double.parseDouble(input1);
        } catch (NumberFormatException e) {
            tilNum1.setError(getString(R.string.err_invalid_format));
            return;
        }

        try {
            num2 = Double.parseDouble(input2);
        } catch (NumberFormatException e) {
            tilNum2.setError(getString(R.string.err_invalid_format));
            return;
        }

        double result = 0;
        boolean hasError = false;

        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                if (num2 == 0) {
                    tilNum2.setError(getString(R.string.err_div_zero));
                    hasError = true;
                } else {
                    result = num1 / num2;
                }
                break;
            case '%':
                if (num2 == 0) {
                    tilNum2.setError(getString(R.string.err_div_zero));
                    hasError = true;
                } else {
                    result = num1 % num2;
                }
                break;
            case '^':
                result = Math.pow(num1, num2);
                break;
        }

        if (!hasError) {
            tvResultValue.setText(formatResult(result));
        } else {
            tvResultValue.setText(getString(R.string.placeholder_result));
        }
    }

    private void clearFields() {
        tilNum1.setError(null);
        tilNum2.setError(null);
        etNum1.setText("");
        etNum2.setText("");
        tvResultValue.setText(getString(R.string.placeholder_result));
        etNum1.requestFocus();
    }

    private String formatResult(double val) {
        if (Double.isInfinite(val)) {
            return "Infinity";
        }
        if (Double.isNaN(val)) {
            return "NaN";
        }
        if (val == (long) val) {
            return String.format("%d", (long) val);
        } else {
            String str = String.valueOf(val);
            if (str.contains(".")) {
                str = str.replaceAll("0*$", "");
                if (str.endsWith(".")) {
                    str = str.substring(0, str.length() - 1);
                }
            }
            return str;
        }
    }
}
