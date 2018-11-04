/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

/**
 *
 * @author mizoguch-ken
 */
public enum LadderEnums {
    LADDERS("Ladders"),
    LADDER("Ladder", "ラダー"),
    FILE("File", "ファイル"),
    FILE_NEW("New", "新規作成"),
    FILE_OPEN("Open", "開く"),
    FILE_SAVE("Save", "上書き保存"),
    FILE_SAVE_AS("Save As..", "名前を付けて保存"),
    FILE_EXPORT_PDF("Export as PDF","PDFエクスポート"),
    FILE_DIFFERENCE("Difference", "差分"),
    EDIT("Edit", "編集"),
    EDIT_UNDO("Undo", "元に戻す"),
    EDIT_REDO("Redo", "やり直し"),
    EDIT_CUT("Cut", "切り取り"),
    EDIT_COPY("Copy", "コピー"),
    EDIT_PASTE("Paste", "貼り付け"),
    EDIT_SELECT_LEFT("Select Left", "左を選択"),
    EDIT_SELECT_UP("Select Up", "上を選択"),
    EDIT_SELECT_RIGHT("Select Right", "右を選択"),
    EDIT_SELECT_DOWN("Select Down", "下を選択"),
    EDIT_TAB_NEW("Tab New", "新規タブ作成"),
    EDIT_TAB_CLOSE("Tab Close", "タブを閉じる"),
    EDIT_TAB_RENAME("Rename", "名前の変更"),
    EDIT_TAB_MOVE_LEFT("Tab Move Left", "タブを左へ移動"),
    EDIT_TAB_MOVE_RIGHT("Tab Move Right", "タブを右へ移動"),
    VIEW("View", "表示"),
    VIEW_TAB_SELECT_NEXT("Tab Select Next", "次のタブ"),
    VIEW_TAB_SELECT_PREVIOUS("Tab Select Previous", "前のタブ"),
    TOOLS("Tools", "ツール"),
    TOOLS_CONNECT("Connect", "接続"),
    TOOLS_RUN("Run", "実行"),
    TOOLS_STOP("Stop", "停止"),
    IO_REFERENCE("Reference", "リファレンス"),
    IO_CURRENT("Current", "カレント"),
    IO_NAME("Name", "名前"),
    IO_ADDRESS("Address", "アドレス"),
    IO_BLOCK("Block", "ブロック"),
    IO_COMMENT("Comment", "コメント"),
    IO_VALUE("Value", "値"),
    IO_POSITION("Position", "位置"),
    ADDRESS_GLOBAL("Global"),
    RENAME("Rename", "名前の変更"),
    INPUT("Input", "入力"),
    CYCLE_MIN("Min", "最小"),
    CYCLE_MAX("Max", "最大"),
    CYCLE_IDEAL("Ideal", "理想"),
    DUPLICATE_NAMES_ERROR("Duplicate names", "名前が重複"),
    ADDRESS_NUMERIC_ONLY_ERROR("Numeric only error in address", "アドレスに数値のみ"),
    DATA_FORMAT_UNEXPECTED_ERROR("Data format unexpected error", "予期しないデータの形式"),
    CONNECTION_FAILED("Connection failed", "接続失敗"),
    FILE_NOT_FOUND("File Not Found", "ファイルが見つかりません"),
    SAVE_CHANGE_PROGRAM("Save Changes Program?", "プログラムの変更内容を保存しますか？");

    private int lang_ = 0;
    private final String[] text_ = new String[2];

    private LadderEnums(final String message) {
        for (int i = 0; i < text_.length; i++) {
            text_[i] = message;
        }
    }

    private LadderEnums(final String en, final String ja) {
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
