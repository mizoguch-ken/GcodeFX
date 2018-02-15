/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.serial;

/**
 *
 * @author mizoguch-ken
 */
public enum SerialEnums {
    SERIAL("Serial"),
    SERIAL_PORT_NOT_FOUND("Port can not be found", "ポートが見つかりません"),
    SERIAL_PORT_USED("The port is already in use", "ポートが既に使用されています"),
    SERIAL_NOT_SUPPORT("Serial is not supported", "シリアルがサポートされていません");

    private int lang_ = 0;
    private final String[] text_ = new String[2];

    private SerialEnums(final String message) {
        for (int i = 0; i < text_.length; i++) {
            text_[i] = message;
        }
    }

    private SerialEnums(final String en, final String ja) {
        text_[0] = en;
        text_[1] = ja;
    }

    public void setLang(int l) {
        lang_ = l;
    }

    @Override
    public String toString() {
        return text_[lang_];
    }
}
