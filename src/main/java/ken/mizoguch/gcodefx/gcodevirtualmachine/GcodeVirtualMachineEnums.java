/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.gcodefx.gcodevirtualmachine;

/**
 *
 * @author mizoguch-ken
 */
public enum GcodeVirtualMachineEnums {

    DUPLICATE_PROGRAM_NUMBER("Duplicated Program Number", "プログラム番号が重複しています"),
    NO_CORRESPOND_PROGRAM_NUMBER("No Number Corresponding Of Program Number", "プログラム番号に対応する番号がありません"),
    BYTE_CODE_LENGTH_INVALID("Incorrect byte code length", "バイトコード長が不正"),
    NO_G_GROUP("No G Group", "Gグループがありません");

    private int lang_ = 0;
    private final String[] text_ = new String[2];

    private GcodeVirtualMachineEnums(final String message) {
        for (int i = 0; i < text_.length; i++) {
            text_[i] = message;
        }
    }

    private GcodeVirtualMachineEnums(final String en, final String ja) {
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
