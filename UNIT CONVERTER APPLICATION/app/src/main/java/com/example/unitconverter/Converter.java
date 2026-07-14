package com.example.unitconverter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle all unit conversions for Length, Weight, and Temperature.
 * It uses a base-unit architecture:
 * - Length: converted to Meters first, then to the target unit.
 * - Weight: converted to Grams first, then to the target unit.
 * - Temperature: handled via specific formulas due to scale offset.
 */
public class Converter {

    // Category names
    public static final String CATEGORY_LENGTH = "Length";
    public static final String CATEGORY_WEIGHT = "Weight";
    public static final String CATEGORY_TEMPERATURE = "Temperature";

    // Length units conversion factors (in meters)
    private static final Map<String, Double> LENGTH_TO_METER = new HashMap<>();
    static {
        LENGTH_TO_METER.put("Millimeter", 0.001);
        LENGTH_TO_METER.put("Centimeter", 0.01);
        LENGTH_TO_METER.put("Meter", 1.0);
        LENGTH_TO_METER.put("Kilometer", 1000.0);
        LENGTH_TO_METER.put("Inch", 0.0254);
        LENGTH_TO_METER.put("Foot", 0.3048);
        LENGTH_TO_METER.put("Yard", 0.9144);
        LENGTH_TO_METER.put("Mile", 1609.344);
    }

    // Weight units conversion factors (in grams)
    private static final Map<String, Double> WEIGHT_TO_GRAM = new HashMap<>();
    static {
        WEIGHT_TO_GRAM.put("Milligram", 0.001);
        WEIGHT_TO_GRAM.put("Gram", 1.0);
        WEIGHT_TO_GRAM.put("Kilogram", 1000.0);
        WEIGHT_TO_GRAM.put("Pound", 453.59237);
        WEIGHT_TO_GRAM.put("Ounce", 28.349523125);
    }

    /**
     * Main convert method.
     *
     * @param value     The value to convert.
     * @param category  The unit category (Length, Weight, Temperature).
     * @param fromUnit  The source unit.
     * @param toUnit    The target unit.
     * @return The converted value.
     * @throws IllegalArgumentException if units or categories are invalid.
     */
    public static double convert(double value, String category, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) {
            return value;
        }

        switch (category) {
            case CATEGORY_LENGTH:
                return convertLength(value, fromUnit, toUnit);
            case CATEGORY_WEIGHT:
                return convertWeight(value, fromUnit, toUnit);
            case CATEGORY_TEMPERATURE:
                return convertTemperature(value, fromUnit, toUnit);
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }

    private static double convertLength(double value, String fromUnit, String toUnit) {
        Double fromFactor = LENGTH_TO_METER.get(fromUnit);
        Double toFactor = LENGTH_TO_METER.get(toUnit);

        if (fromFactor == null || toFactor == null) {
            throw new IllegalArgumentException("Invalid length units: " + fromUnit + " or " + toUnit);
        }

        // Convert fromUnit -> Meter -> toUnit
        double valueInMeters = value * fromFactor;
        return valueInMeters / toFactor;
    }

    private static double convertWeight(double value, String fromUnit, String toUnit) {
        Double fromFactor = WEIGHT_TO_GRAM.get(fromUnit);
        Double toFactor = WEIGHT_TO_GRAM.get(toUnit);

        if (fromFactor == null || toFactor == null) {
            throw new IllegalArgumentException("Invalid weight units: " + fromUnit + " or " + toUnit);
        }

        // Convert fromUnit -> Gram -> toUnit
        double valueInGrams = value * fromFactor;
        return valueInGrams / toFactor;
    }

    private static double convertTemperature(double value, String fromUnit, String toUnit) {
        // First convert fromUnit -> Celsius
        double celsiusValue;
        switch (fromUnit) {
            case "Celsius":
                celsiusValue = value;
                break;
            case "Fahrenheit":
                celsiusValue = (value - 32.0) * 5.0 / 9.0;
                break;
            case "Kelvin":
                celsiusValue = value - 273.15;
                break;
            default:
                throw new IllegalArgumentException("Invalid temperature unit: " + fromUnit);
        }

        // Now convert Celsius -> toUnit
        switch (toUnit) {
            case "Celsius":
                return celsiusValue;
            case "Fahrenheit":
                return (celsiusValue * 9.0 / 5.0) + 32.0;
            case "Kelvin":
                return celsiusValue + 273.15;
            default:
                throw new IllegalArgumentException("Invalid temperature unit: " + toUnit);
        }
    }

    /**
     * Generates a user-friendly string explaining the formula applied.
     *
     * @param category  The unit category.
     * @param fromUnit  The source unit.
     * @param toUnit    The target unit.
     * @return User-friendly formula representation.
     */
    public static String getFormulaDescription(String category, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) {
            return "No conversion needed (same units).";
        }

        DecimalFormat df = new DecimalFormat("0.####");

        switch (category) {
            case CATEGORY_LENGTH: {
                Double fromFactor = LENGTH_TO_METER.get(fromUnit);
                Double toFactor = LENGTH_TO_METER.get(toUnit);
                if (fromFactor != null && toFactor != null) {
                    double ratio = fromFactor / toFactor;
                    return "1 " + fromUnit + " = " + df.format(ratio) + " " + toUnit;
                }
                break;
            }
            case CATEGORY_WEIGHT: {
                Double fromFactor = WEIGHT_TO_GRAM.get(fromUnit);
                Double toFactor = WEIGHT_TO_GRAM.get(toUnit);
                if (fromFactor != null && toFactor != null) {
                    double ratio = fromFactor / toFactor;
                    return "1 " + fromUnit + " = " + df.format(ratio) + " " + toUnit;
                }
                break;
            }
            case CATEGORY_TEMPERATURE:
                return getTemperatureFormula(fromUnit, toUnit);
        }
        return "N/A";
    }

    private static String getTemperatureFormula(String fromUnit, String toUnit) {
        if (fromUnit.equals("Celsius") && toUnit.equals("Fahrenheit")) {
            return "(°C × 9/5) + 32 = °F";
        } else if (fromUnit.equals("Fahrenheit") && toUnit.equals("Celsius")) {
            return "(°F − 32) × 5/9 = °C";
        } else if (fromUnit.equals("Celsius") && toUnit.equals("Kelvin")) {
            return "°C + 273.15 = K";
        } else if (fromUnit.equals("Kelvin") && toUnit.equals("Celsius")) {
            return "K − 273.15 = °C";
        } else if (fromUnit.equals("Fahrenheit") && toUnit.equals("Kelvin")) {
            return "(°F − 32) × 5/9 + 273.15 = K";
        } else if (fromUnit.equals("Kelvin") && toUnit.equals("Fahrenheit")) {
            return "(K − 273.15) × 9/5 + 32 = °F";
        }
        return "N/A";
    }
}
