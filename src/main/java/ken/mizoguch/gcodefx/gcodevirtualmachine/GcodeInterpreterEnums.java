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
public enum GcodeInterpreterEnums {
    GCODE_FANUC_TWO_BYTE_CHARACTER_CODE("GcodeFanucTwoByteCharacterCode"),
    NO_BYTECODE("No Bytecode", "バイトコードがありません"),
    NO_PROGRAM_NUMBER("No Program Number", "プログラム番号がありません"),
    NO_SEQUENCE_NUMBER("No Sequence Number", "シーケンス番号がありません"),
    NO_GOTO_SEQUENCE_NUMBER("No GOTO Sequence Number", "GOTO シーケンス番号がありません"),
    DESTINATION_UNKNOWN_IF("Destination Unknown Of IF Condition Not Satisfied", "IF条件不成立時の行先不明"),
    NO_DO_NUMBER_WHILE("No DO Number WHILE", "WHILE DO番号がありません"),
    NO_END_NUMBER_WHILE("No END Number WHILE", "WHILE END番号がありません"),
    MIRROR_CODE_ERROR("Mirror Code Error", "ミラーコード異常"),
    DISABLE_MIRROR_AXIS("Disabled Mirror Axis", "ミラー軸で使用不可"),
    ABNORMAL_PLANE_SELECTION("Abnormal Plane Selection", "平面選択が異常"),
    NO_G_GROUP("No G Group", "Gグループがありません"),
    VARIABLE_ASSIGNMENT_BAN("Variable Assignment Ban", "変数に代入できません"),
    NO_VARIABLE("No Variable", "変数がありません"),
    NOT_OPEN_PRINT("Not Open Print", "プリントが開かれていません"),
    INVALID("Incorrect", "不正"),
    DISCONNECTED_SERIAL_CONNECTION("Disconnected Serial Connection", "シリアル接続が切断されました"),
    LADDER_NOT_RUNNING("Ladder is not running", "ラダーが実行されていません"),
    ABNORMAL_PROGRAM_COUNTER("Abnormal Program Counter Value", "プログラムカウンタの値が異常"),
    PROGRAM_NUMBER_NO_DECLARATION("Program number No declaration", "プログラム番号の宣言がありません"),
    EXTERNAL_SUBPROGRAM_MULTIPLE_CALL("External subprogram multiple call", "外部サブプログラム多重呼出"),
    MACRO_ALARM("Macro Alarm", "マクロアラーム"),
    MACRO_STOP("Macro Stop", "マクロストップ");

    private int lang_ = 0;
    private final String[] text_ = new String[2];

    private GcodeInterpreterEnums(final String message) {
        for (int i = 0; i < text_.length; i++) {
            text_[i] = message;
        }
    }

    private GcodeInterpreterEnums(final String en, final String ja) {
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
