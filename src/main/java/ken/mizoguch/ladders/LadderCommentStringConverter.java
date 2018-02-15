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
public class LadderCommentStringConverter extends StringConverter<String> {

    private String value;

    /**
     *
     */
    public LadderCommentStringConverter() {
        value = "";
    }

    @Override
    public String toString(String object) {
        if (object != null) {
            if (!object.contains(";")) {
                value = object.trim();
            }
        }
        return value;
    }

    @Override
    public String fromString(String string) {
        if (string != null) {
            if (!string.contains(";")) {
                value = string.trim();
            }
        }
        return value;
    }
}
