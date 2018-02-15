/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.StringConverter;

/**
 *
 * @author mizoguch-ken
 */
public class LadderValueStringConverter extends StringConverter<Double> {

    private Double value;
    private int radix;
    private Matcher mRealNumber;

    private static final Pattern PATTERN_REAL_NUMBER = Pattern.compile("^[\\+\\-]?(?:((?:\\d*\\.\\d+|\\d+\\.?\\d*)(?:[eE][\\+\\-]?\\d+|))|0[xX]([0-9a-fA-F]+)|0[bB]([0-1]+))$");

    /**
     *
     */
    public LadderValueStringConverter() {
        value = 0.0;
        radix = 10;
    }

    @Override
    public String toString(Double object) {
        if (object != null) {
            if (!object.isInfinite() && !object.isNaN()) {
                value = object;
            }
        }

        switch (radix) {
            case 10:
                return value.toString();
            case 16:
                return "0x" + Long.toString(value.longValue(), 16);
            case 2:
                return "0b" + Long.toString(value.longValue(), 2);
        }
        return value.toString();
    }

    @Override
    public Double fromString(String string) {
        if (string != null) {
            string = string.trim();
            mRealNumber = PATTERN_REAL_NUMBER.matcher(string);
            if (mRealNumber.find()) {
                if (mRealNumber.group(1) != null) {
                    value = Double.parseDouble(string);
                    radix = 10;
                } else if (mRealNumber.group(2) != null) {
                    value = (double) Long.parseLong(string, 16);
                    radix = 16;
                } else if (mRealNumber.group(3) != null) {
                    value = (double) Long.parseLong(string, 2);
                    radix = 2;
                }
            }
        }
        return value;
    }
}
