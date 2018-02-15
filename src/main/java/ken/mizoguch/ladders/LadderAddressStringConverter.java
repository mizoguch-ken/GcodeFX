/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.concurrent.ConcurrentHashMap;
import javafx.util.StringConverter;

/**
 *
 * @author mizoguch-ken
 */
public class LadderAddressStringConverter extends StringConverter<String> {

    private final ConcurrentHashMap<String, LadderIo> ioMap_;
    private String value;

    /**
     *
     * @param ioMap
     */
    public LadderAddressStringConverter(ConcurrentHashMap<String, LadderIo> ioMap) {
        ioMap_ = ioMap;
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
                if (!ioMap_.containsKey(string)) {
                    value = string;
                }
            }
        }
        return value;
    }
}
