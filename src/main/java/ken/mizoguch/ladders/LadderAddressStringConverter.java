/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import javafx.util.StringConverter;

/**
 *
 * @author mizoguch-ken
 */
public class LadderAddressStringConverter extends StringConverter<String> {

    private String value;

    /**
     *
     */
    public LadderAddressStringConverter() {
        value = "";
    }

    @Override
    public String toString(String object) {
        if (object != null) {
            object = object.trim();
            if ((!object.isEmpty()) && (!object.contains(" "))) {
                value = object;
            }
        }
        return value;
    }

    @Override
    public String fromString(String string) {
        if (string != null) {
            string = string.trim();
            if ((!string.isEmpty()) && (!string.contains(" "))) {
                if (!(value.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX) ^ string.startsWith(Ladders.LADDER_LOCAL_ADDRESS_PREFIX))) {
                    value = string;
                }
            }
        }
        return value;
    }
}
