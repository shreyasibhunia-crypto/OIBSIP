package com.example.unitconverter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;

/**
 * Main activity of the Unit Converter application.
 * Manages UI components, user interactions, button click animations, and validation logic.
 */
public class MainActivity extends AppCompatActivity {

    // UI elements
    private AutoCompleteTextView actvCategory;
    private AutoCompleteTextView actvFromUnit;
    private AutoCompleteTextView actvToUnit;
    private TextInputEditText etValue;
    private MaterialButton btnConvert;
    private MaterialButton btnReset;
    private MaterialButton btnSwap;
    
    // Result Card components
    private MaterialCardView cardResult;
    private TextView tvResultValue;
    private TextView tvFormulaValue;

    // Formatting result values
    private final DecimalFormat decimalFormat = new DecimalFormat("0.####");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initViews();

        // Setup category dropdown
        setupCategoryDropdown();

        // Setup listeners for actions
        setupListeners();
    }

    /**
     * Finds and binds the layout views to the class members.
     */
    private void initViews() {
        actvCategory = findViewById(R.id.actv_category);
        actvFromUnit = findViewById(R.id.actv_from_unit);
        actvToUnit = findViewById(R.id.actv_to_unit);
        etValue = findViewById(R.id.et_value);
        btnConvert = findViewById(R.id.btn_convert);
        btnReset = findViewById(R.id.btn_reset);
        btnSwap = findViewById(R.id.btn_swap);
        
        cardResult = findViewById(R.id.card_result);
        tvResultValue = findViewById(R.id.tv_result_value);
        tvFormulaValue = findViewById(R.id.tv_formula_value);
    }

    /**
     * Fills the category dropdown and configures its listener to update unit dropdowns.
     */
    private void setupCategoryDropdown() {
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);

        // Select the first category (Length) by default
        actvCategory.setText(categories[0], false);
        updateUnitsDropdowns(categories[0]);

        // Category change listener
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            updateUnitsDropdowns(selectedCategory);
            // Clear calculation when category is switched to avoid confusion
            resetResultCard();
        });
    }

    /**
     * Updates the From and To dropdown units depending on the selected category.
     *
     * @param category The selected category (Length, Weight, Temperature).
     */
    private void updateUnitsDropdowns(String category) {
        String[] units;
        switch (category) {
            case Converter.CATEGORY_LENGTH:
                units = getResources().getStringArray(R.array.length_units);
                break;
            case Converter.CATEGORY_WEIGHT:
                units = getResources().getStringArray(R.array.weight_units);
                break;
            case Converter.CATEGORY_TEMPERATURE:
                units = getResources().getStringArray(R.array.temperature_units);
                break;
            default:
                units = new String[0];
                break;
        }

        // Set adapters for From and To dropdowns
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, units);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, units);

        actvFromUnit.setAdapter(fromAdapter);
        actvToUnit.setAdapter(toAdapter);

        // Pre-select distinct default units (e.g. index 0 to index 1)
        if (units.length >= 2) {
            actvFromUnit.setText(units[0], false);
            actvToUnit.setText(units[1], false);
        } else if (units.length > 0) {
            actvFromUnit.setText(units[0], false);
            actvToUnit.setText(units[0], false);
        }
    }

    /**
     * Configures click listeners and dynamic text fields actions.
     */
    private void setupListeners() {
        // Convert button click
        btnConvert.setOnClickListener(v -> animateButton(btnConvert, this::performConversion));

        // Reset button click
        btnReset.setOnClickListener(v -> animateButton(btnReset, this::performReset));

        // Swap button click
        btnSwap.setOnClickListener(v -> animateButton(btnSwap, this::performSwap));

        // Optional: clear conversion cards if user changes values or units to keep UI interactive
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Instantly reset result card if value is cleared
                if (s.toString().trim().isEmpty()) {
                    resetResultCard();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Performs unit conversion with validations.
     */
    private void performConversion() {
        String valueStr = etValue.getText() != null ? etValue.getText().toString().trim() : "";

        // 1. Check for empty input
        if (valueStr.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Parse value and validate number
        double inputVal;
        try {
            inputVal = Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        String category = actvCategory.getText().toString();
        String fromUnit = actvFromUnit.getText().toString();
        String toUnit = actvToUnit.getText().toString();

        // 3. Check if same units selected
        if (fromUnit.equalsIgnoreCase(toUnit)) {
            Toast.makeText(this, R.string.error_same_unit, Toast.LENGTH_SHORT).show();
            return;
        }

        // Execute conversion
        try {
            double result = Converter.convert(inputVal, category, fromUnit, toUnit);
            String formula = Converter.getFormulaDescription(category, fromUnit, toUnit);

            // Format values for beautiful presentation
            String formattedInput = decimalFormat.format(inputVal);
            String formattedResult = decimalFormat.format(result);

            // Populate result cards
            tvResultValue.setText(formattedInput + " " + fromUnit + " = " + formattedResult + " " + toUnit);
            tvFormulaValue.setText(formula);
            
            // Show result card
            cardResult.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "Error in conversion: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Swaps the "From" and "To" units. If a result was already calculated, re-runs the conversion.
     */
    private void performSwap() {
        String fromUnit = actvFromUnit.getText().toString();
        String toUnit = actvToUnit.getText().toString();

        // Swap unit text
        actvFromUnit.setText(toUnit, false);
        actvToUnit.setText(fromUnit, false);

        // If the value input is not empty, automatically re-run calculation
        String valueStr = etValue.getText() != null ? etValue.getText().toString().trim() : "";
        if (!valueStr.isEmpty()) {
            performConversion();
        }
    }

    /**
     * Resets the entire application back to its default state.
     */
    private void performReset() {
        etValue.setText("");
        resetResultCard();

        // Reset category to default (Length)
        String[] categories = getResources().getStringArray(R.array.categories);
        actvCategory.setText(categories[0], false);
        updateUnitsDropdowns(categories[0]);

        Toast.makeText(this, "App Reset Completed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Resets result labels and hides the Result Card.
     */
    private void resetResultCard() {
        tvResultValue.setText(R.string.default_result);
        tvFormulaValue.setText(R.string.default_formula);
    }

    /**
     * Creates a smooth scale click animation for buttons.
     *
     * @param view      The view to animate.
     * @param endAction Action to execute after animation completes.
     */
    private void animateButton(View view, Runnable endAction) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(80)
            .withEndAction(() -> view.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(80)
                .withEndAction(endAction)
                .start())
            .start();
    }
}
