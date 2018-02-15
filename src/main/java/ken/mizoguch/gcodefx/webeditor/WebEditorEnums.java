/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.webeditor;

/**
 *
 * @author mizoguch-ken
 */
public enum WebEditorEnums {
    WEB_EDITOR("WebEditor"),;

    private int lang_ = 0;
    private final String[] text_ = new String[2];

    private WebEditorEnums(final String message) {
        for (int i = 0; i < text_.length; i++) {
            text_[i] = message;
        }
    }

    private WebEditorEnums(final String en, final String ja) {
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
