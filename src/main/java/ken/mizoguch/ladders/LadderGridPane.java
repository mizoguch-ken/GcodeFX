/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import difflib.Delta;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mizoguch-ken
 */
public class LadderGridPane extends AnchorPane {

    private LadderGrid ladderGrid_;

    private static Paint baseBackGroundColor_ = Color.WHITE;
    private static Paint baseStrokeColor_ = Color.BLUE;
    private static double baseStrokeWidth_ = 1.618;
    private static Paint contentsBackGroundColor_ = Color.BLACK;
    private static Paint contentsFontColor_ = Color.WHITE;
    private static Paint editBackGroundColor_ = Color.AQUA;
    private static Paint selectBackGroundColor_ = Color.AQUAMARINE;
    private static Paint selectStrokeColor_ = Color.LIME;
    private static double selectStrokeWidth_ = 1.618;
    private static Paint gridBorderColor_ = Color.GRAY;
    private static Paint onColor_ = Color.YELLOW;
    private static Paint differenceChangeBackGroundColor_ = Color.LIGHTYELLOW;
    private static Paint differenceDeleteBackGroundColor_ = Color.LIGHTBLUE;
    private static Paint differenceInsertBackGroundColor_ = Color.LIGHTPINK;

    private final Group grpSelect_, grpLine_, grpBlock_;
    private final Rectangle rectSelect_;
    private final Line lineVertical_, lineVerticalOr_;
    private boolean isEditing_;

    /**
     *
     * @return
     */
    public static Paint getBaseBackGroundColor() {
        return baseBackGroundColor_;
    }

    /**
     *
     * @param color
     */
    public static void setBaseBackGroundColor(Paint color) {
        baseBackGroundColor_ = color;
    }

    /**
     *
     * @return
     */
    public static Paint getBaseStrokeColor() {
        return baseStrokeColor_;
    }

    /**
     *
     * @param color
     */
    public static void setBaseStrokeColor(Paint color) {
        baseStrokeColor_ = color;
    }

    /**
     *
     * @return
     */
    public static double getBaseStrokeWidth() {
        return baseStrokeWidth_;
    }

    /**
     *
     * @param strokeWidth
     */
    public static void setBaseStrokeWidth(double strokeWidth) {
        baseStrokeWidth_ = strokeWidth;
    }

    /**
     *
     * @return
     */
    public static Paint getContentsBackGroundColor() {
        return contentsBackGroundColor_;
    }

    /**
     *
     * @param contentsBackGroundColor
     */
    public static void setContentsBackGroundColor(Paint contentsBackGroundColor) {
        contentsBackGroundColor_ = contentsBackGroundColor;
    }

    /**
     *
     * @return
     */
    public static Paint getContentsFontColor() {
        return contentsFontColor_;
    }

    /**
     *
     * @param contentsFontColor
     */
    public static void setContentsFontColor(Paint contentsFontColor) {
        contentsFontColor_ = contentsFontColor;
    }

    /**
     *
     * @return
     */
    public static Paint getEditBackGroundColor() {
        return editBackGroundColor_;
    }

    /**
     *
     * @param editBackGroundColor
     */
    public static void setEditBackGroundColor(Paint editBackGroundColor) {
        editBackGroundColor_ = editBackGroundColor;
    }

    /**
     *
     * @return
     */
    public static Paint getSelectBackGroundColor() {
        return selectBackGroundColor_;
    }

    /**
     *
     * @param selectBackGroundColor
     */
    public static void setSelectBackGroundColor(Paint selectBackGroundColor) {
        selectBackGroundColor_ = selectBackGroundColor;
    }

    /**
     *
     * @return
     */
    public static Paint getSelectStrokeColor() {
        return selectStrokeColor_;
    }

    /**
     *
     * @param color
     */
    public static void setSelectStrokeColor(Paint color) {
        selectStrokeColor_ = color;
    }

    /**
     *
     * @return
     */
    public static double getSelectStrokeWidth() {
        return selectStrokeWidth_;
    }

    /**
     *
     * @param strokeWidth
     */
    public static void setSelectStrokeWidth(double strokeWidth) {
        selectStrokeWidth_ = strokeWidth;
    }

    /**
     *
     * @return
     */
    public static Paint getGridBorderColor() {
        return gridBorderColor_;
    }

    /**
     *
     * @param color
     */
    public static void setGridBorderColor(Paint color) {
        gridBorderColor_ = color;
    }

    /**
     *
     * @return
     */
    public static Paint getOnColor() {
        return onColor_;
    }

    /**
     *
     * @param color
     */
    public static void setOnColor(Paint color) {
        onColor_ = color;
    }

    /**
     *
     * @return
     */
    public static Paint getDifferenceChangeBackGroundColor() {
        return differenceChangeBackGroundColor_;
    }

    /**
     *
     * @param differenceChangeBackGroundColor
     */
    public static void setDifferenceChangeBackGroundColor(Paint differenceChangeBackGroundColor) {
        differenceChangeBackGroundColor_ = differenceChangeBackGroundColor;
    }

    /**
     *
     * @return
     */
    public static Paint getDifferenceDeleteBackGroundColor() {
        return differenceDeleteBackGroundColor_;
    }

    /**
     *
     * @param differenceDeleteBackGroundColor
     */
    public static void setDifferenceDeleteBackGroundColor(Paint differenceDeleteBackGroundColor) {
        differenceDeleteBackGroundColor_ = differenceDeleteBackGroundColor;
    }

    /**
     *
     * @return
     */
    public static Paint getDifferenceInsertBackGroundColor() {
        return differenceInsertBackGroundColor_;
    }

    /**
     *
     * @param differenceInsertBackGroundColor
     */
    public static void setDifferenceInsertBackGroundColor(Paint differenceInsertBackGroundColor) {
        differenceInsertBackGroundColor_ = differenceInsertBackGroundColor;
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     */
    public LadderGridPane(int columnIndex, int rowIndex) {
        this(columnIndex, rowIndex, 1, 1, null, null, null, null);
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param colspan
     * @param rowspan
     */
    public LadderGridPane(int columnIndex, int rowIndex, int colspan, int rowspan) {
        this(columnIndex, rowIndex, colspan, rowspan, null, null, null, null);
    }

    /**
     *
     * @param columnIndex
     * @param rowIndex
     * @param colspan
     * @param rowspan
     * @param leftLadderGrid
     * @param upLadderGrid
     * @param rightLadderGrid
     * @param downLadderGrid
     */
    public LadderGridPane(int columnIndex, int rowIndex, int colspan, int rowspan, LadderGrid leftLadderGrid, LadderGrid upLadderGrid, LadderGrid rightLadderGrid, LadderGrid downLadderGrid) {
        ladderGrid_ = new LadderGrid(columnIndex, rowIndex, colspan, rowspan, leftLadderGrid, upLadderGrid, rightLadderGrid, downLadderGrid);

        grpSelect_ = new Group();
        grpLine_ = new Group();
        grpBlock_ = new Group();

        setBorder(new Border(new BorderStroke(gridBorderColor_, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        rectSelect_ = new Rectangle();
        rectSelect_.setFill(null);
        rectSelect_.setStrokeWidth(selectStrokeWidth_);
        grpSelect_.getChildren().add(rectSelect_);

        lineVertical_ = new Line();
        lineVertical_.setStrokeWidth(baseStrokeWidth_);
        lineVerticalOr_ = new Line();
        lineVerticalOr_.setStrokeWidth(baseStrokeWidth_);
        grpLine_.getChildren().addAll(lineVertical_, lineVerticalOr_);
        isEditing_ = false;

        super.getChildren().addAll(grpSelect_, grpLine_, grpBlock_);

        init();
    }

    private void init() {
        changeBlock();
        changeEditing();
        changeStrokeSelect();
        changeStrokeVertical();
        changeStrokeVerticalOr();

        widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                changeWidth(newValue.doubleValue());
            }
        });
        heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue != null) {
                changeHeight(newValue.doubleValue());
            }
        });
    }

    /**
     *
     */
    public void clear() {
        ladderGrid_.clear();
        changeBlock();
    }

    /**
     *
     * @return
     */
    public LadderGridPane copy() {
        return (new LadderGridPane(ladderGrid_.getColumnIndex(), ladderGrid_.getRowIndex(), ladderGrid_.getColSpan(), ladderGrid_.getRowSpan(), ladderGrid_.getLeftLadderGrid(), ladderGrid_.getUpLadderGrid(), ladderGrid_.getRightLadderGrid(), ladderGrid_.getDownLadderGrid()));
    }

    /**
     *
     * @return
     */
    public LadderGrid getLadderGrid() {
        return ladderGrid_;
    }

    /**
     *
     * @return
     */
    public boolean isEditing() {
        return isEditing_;
    }

    /**
     *
     * @param isEditing
     */
    public void setEditing(boolean isEditing) {
        if (isEditing_ != isEditing) {
            isEditing_ = isEditing;
            changeEditing();
        }
    }

    /**
     *
     */
    public void changeEditing() {
        if (isEditing_) {
            setBackground(new Background(new BackgroundFill(editBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            setBackground(new Background(new BackgroundFill(baseBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     *
     */
    public void changeBlock() {
        Rectangle rectangle;
        Line line;
        Arc arc;
        Label label;

        grpBlock_.getChildren().clear();
        switch (ladderGrid_.getBlock()) {
            case CONTENTS:
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setFont(Font.font(Ladders.LADDER_DEFAULT_GRID_CONTENTS_HIGHT * 0.618));
                label.setTextFill(contentsFontColor_);
                grpBlock_.getChildren().add(label); // number

                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeContents();
                break;
            case CONNECT_LINE:
                // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -

                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                break;
            case LOAD:
                // address value
                // -| |-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case LOAD_NOT:
                // address value
                // -|/|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case LOAD_RISING:
                // address value
                // -|↑|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|↑|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case LOAD_RISING_NOT:
                // address value
                // -|/↑|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/|-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/↑|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case LOAD_FALLING:
                // address value
                // -|↓|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|↓|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case LOAD_FALLING_NOT:
                // address value
                // -|/↓|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/|-
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/↓|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT:
                // address value
                // -( )
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT_NOT:
                // address value
                // -( )
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(/)

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT_RISING:
                // address value
                // -(↑)
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(↑)

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT_RISING_NOT:
                // address value
                // -(/↑)
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(/)
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(/↑)

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT_FALLING:
                // address value
                // -(↓)
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(↓)

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case OUT_FALLING_NOT:
                // address value
                // -(/↓)
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(90.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -(
                arc = new Arc();
                arc.setStroke(baseStrokeColor_);
                arc.setStrokeWidth(baseStrokeWidth_);
                arc.setStartAngle(270.0);
                arc.setLength(180.0);
                grpBlock_.getChildren().add(arc); // -( )
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(/)
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line);
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -(/↓)

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case COMPARISON_EQUAL:
                // address value
                // -|= address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_EQUAL.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|=|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_NOT_EQUAL:
                // address value
                // -|<> address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_NOT_EQUAL.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|<>|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_LESS:
                // address value
                // -|< address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_LESS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|<|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_LESS_EQUAL:
                // address value
                // -|<= address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_LESS_EQUAL.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|<=|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_GREATER:
                // address value
                // -|> address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_GREATER.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|>|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_GREATER_EQUAL:
                // address value
                // -|>= address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_GREATER_EQUAL.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|>=|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_AND_BITS:
                // address value
                // -|& address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_AND_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|&|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_OR_BITS:
                // address value
                // -|| address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_OR_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|||-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COMPARISON_XOR_BITS:
                // address value
                // -|^ address|-
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |-
                label = new Label(Ladders.LADDER_BLOCK.COMPARISON_XOR_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|^|-

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SET:
                // address value
                // -|SET|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SET.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SET|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case RESET:
                // address value
                // -|RES|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.RESET.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|RES|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case AND_BITS:
                // address value
                // -|AND address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.AND_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|AND|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case OR_BITS:
                // address value
                // -|OR address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.OR_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|OR|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case XOR_BITS:
                // address value
                // -|XOR address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.XOR_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|XOR|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case NOT_BITS:
                // address value
                // -|NOT address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.NOT_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|NOT|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case ADDITION:
                // address value
                // -|ADD address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.ADDITION.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|ADD|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SUBTRACTION:
                // address value
                // -|SUB address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SUBTRACTION.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SUB|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case MULTIPLICATION:
                // address value
                // -|MUL address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.MULTIPLICATION.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|MUL|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case DIVISION:
                // address value
                // -|DIV address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.DIVISION.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|DIV|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case AVERAGE:
                // address value
                // -|AVE address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.AVERAGE.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|AVE|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SHIFT_LEFT_BITS:
                // address value
                // -|SFL address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SHIFT_LEFT_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SFL|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SHIFT_RIGHT_BITS:
                // address value
                // -|SFR address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SHIFT_RIGHT_BITS.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SFR|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SIGMOID:
                // address value
                // -|SIG address address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SIGMOID.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SIG|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case RANDOM:
                // address value
                // -|RND|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.RANDOM.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|RND|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                break;
            case TIMER:
                // address value
                // -|TIM time|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // time
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.TIMER.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|TIM|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case TIMER_NOT:
                // address value
                // -|/TIM time|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // time
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/|
                label = new Label(Ladders.LADDER_BLOCK.TIMER.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|/TIM|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COUNTER:
                // address value
                // -|CNT count|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // count
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.COUNTER.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|CNT|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case COUNTER_NOT:
                // address value
                // -|/CNT count|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // count
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|/|
                label = new Label(Ladders.LADDER_BLOCK.COUNTER.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|/CNT|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case MOVE:
                // address value
                // -|MOV address|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.MOVE.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|MOV|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockFunction();
                break;
            case SCRIPT:
                // address value
                // -|SCR function|
                // comment
                rectangle = new Rectangle();
                grpBlock_.getChildren().add(rectangle); // I/O
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.LEFT);
                grpBlock_.getChildren().add(label); // address
                label = new Label();
                label.setAlignment(Pos.CENTER_RIGHT);
                label.setTextAlignment(TextAlignment.RIGHT);
                grpBlock_.getChildren().add(label); // value
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // comment
                label = new Label();
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // function
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -|
                line = new Line();
                line.setStroke(baseStrokeColor_);
                line.setStrokeWidth(baseStrokeWidth_);
                grpBlock_.getChildren().add(line); // -| |
                label = new Label(Ladders.LADDER_BLOCK.SCRIPT.toCommand());
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                grpBlock_.getChildren().add(label); // -|SCR|

                changeBlockIO();
                changeBlockWidth(getWidth());
                changeBlockHeight(getHeight());
                changeAddress();
                changeComment();
                changeBlockScript();
                break;
        }
    }

    /**
     *
     */
    public void changeBlockIO() {
        switch (ladderGrid_.getBlock()) {
            case LOAD:
            case LOAD_RISING:
            case LOAD_FALLING:
                if (ladderGrid_.isBlockLd()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case LOAD_NOT:
            case LOAD_RISING_NOT:
            case LOAD_FALLING_NOT:
                if (!ladderGrid_.isBlockLd()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case OUT:
            case OUT_NOT:
            case OUT_RISING:
            case OUT_RISING_NOT:
            case OUT_FALLING:
            case OUT_FALLING_NOT:
                if (ladderGrid_.isBlockLd()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                    ((Arc) grpBlock_.getChildren().get(5)).setFill(onColor_);
                    ((Arc) grpBlock_.getChildren().get(6)).setFill(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                    ((Arc) grpBlock_.getChildren().get(5)).setFill(null);
                    ((Arc) grpBlock_.getChildren().get(6)).setFill(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_EQUAL:
                if (ladderGrid_.getBlockValue() == ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_NOT_EQUAL:
                if (ladderGrid_.getBlockValue() != ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_LESS:
                if (ladderGrid_.getBlockValue() < ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_LESS_EQUAL:
                if (ladderGrid_.getBlockValue() <= ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_GREATER:
                if (ladderGrid_.getBlockValue() > ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_GREATER_EQUAL:
                if (ladderGrid_.getBlockValue() >= ladderGrid_.getBlockFunctions()[0].getValue()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_AND_BITS:
                if ((((long) ladderGrid_.getBlockValue()) & ((long) ladderGrid_.getBlockFunctions()[0].getValue())) != 0) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_OR_BITS:
                if ((((long) ladderGrid_.getBlockValue()) | ((long) ladderGrid_.getBlockFunctions()[0].getValue())) != 0) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case COMPARISON_XOR_BITS:
                if ((((long) ladderGrid_.getBlockValue()) ^ ((long) ladderGrid_.getBlockFunctions()[0].getValue())) != 0) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
            case SET:
            case RESET:
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case NOT_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
            case RANDOM:
            case TIMER:
            case TIMER_NOT:
            case COUNTER:
            case COUNTER_NOT:
            case MOVE:
            case SCRIPT:
                if (ladderGrid_.isBlockLd()) {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(onColor_);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(onColor_);
                } else {
                    ((Rectangle) grpBlock_.getChildren().get(0)).setFill(null);
                    ((Rectangle) grpBlock_.getChildren().get(0)).setStroke(null);
                }
                ((Label) grpBlock_.getChildren().get(2)).setText(Double.toString(ladderGrid_.getBlockValue()));
                break;
        }
    }

    private void changeBlockWidth(double width) {
        double width2 = width / 2.0;
        double width3 = width / 3.0;
        double width6 = width / 6.0;
        double width12 = width / 12.0;

        switch (ladderGrid_.getBlock()) {
            case CONTENTS:
                ((Label) grpBlock_.getChildren().get(0)).setPrefWidth(width);
                break;
            case CONNECT_LINE:
                ((Line) grpBlock_.getChildren().get(0)).setEndX(width);
                break;
            case LOAD:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                break;
            case LOAD_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width6);
                break;
            case LOAD_RISING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 + width12);
                break;
            case LOAD_RISING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(11)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(11)).setEndX(width2 + width12);
                break;
            case LOAD_FALLING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 + width12);
                break;
            case LOAD_FALLING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(11)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(11)).setEndX(width2 + width12);
                break;
            case OUT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                break;
            case OUT_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width3);
                break;
            case OUT_RISING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 + width12);
                break;
            case OUT_RISING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width3);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 + width12);
                break;
            case OUT_FALLING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 + width12);
                break;
            case OUT_FALLING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width3);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterX(width3);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusX(width6);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width3);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width3);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(9)).setEndX(width2 - width12);
                ((Line) grpBlock_.getChildren().get(10)).setStartX(width2);
                ((Line) grpBlock_.getChildren().get(10)).setEndX(width2 + width12);
                break;
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(4)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width - width6);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width);
                ((Label) grpBlock_.getChildren().get(9)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(9)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                break;
            case SET:
            case RESET:
            case RANDOM:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(4)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width - width3 + width6);
                ((Label) grpBlock_.getChildren().get(7)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(7)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                break;
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(4)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                ((Label) grpBlock_.getChildren().get(5)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(5)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width - width3 + width6);
                ((Label) grpBlock_.getChildren().get(9)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(9)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                break;
            case NOT_BITS:
            case TIMER:
            case COUNTER:
            case MOVE:
            case SCRIPT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(4)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width - width3 + width6);
                ((Label) grpBlock_.getChildren().get(8)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(8)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                break;
            case TIMER_NOT:
            case COUNTER_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutX(width6);
                ((Rectangle) grpBlock_.getChildren().get(0)).setWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(1)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(1)).setPrefWidth(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setLayoutX(width - width3);
                ((Label) grpBlock_.getChildren().get(2)).setPrefWidth(width3 - baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setLayoutX(baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(3)).setPrefWidth(width - (baseStrokeWidth_ * 2.0));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(4)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                ((Line) grpBlock_.getChildren().get(5)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setStartX(width6);
                ((Line) grpBlock_.getChildren().get(6)).setEndX(width6);
                ((Line) grpBlock_.getChildren().get(7)).setStartX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(7)).setEndX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(8)).setStartX(width - width3 + width6);
                ((Line) grpBlock_.getChildren().get(8)).setEndX(width6);
                ((Label) grpBlock_.getChildren().get(9)).setLayoutX(width6 + baseStrokeWidth_);
                ((Label) grpBlock_.getChildren().get(9)).setPrefWidth(width - width3 - (baseStrokeWidth_ * 2.0));
                break;
        }
    }

    private void changeBlockHeight(double height) {
        double height2 = height / 2.0;
        double height4 = height / 4.0;
        double height5 = height / 5.0;
        double height6 = height / 6.0;
        double height12 = height / 12.0;

        switch (ladderGrid_.getBlock()) {
            case CONTENTS:
                ((Label) grpBlock_.getChildren().get(0)).setPrefHeight(height);
                break;
            case CONNECT_LINE:
                ((Line) grpBlock_.getChildren().get(0)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(0)).setEndY(height2);
                break;
            case LOAD:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                break;
            case LOAD_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                break;
            case LOAD_RISING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height4 + height12);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height4 + height12);
                break;
            case LOAD_RISING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height4 + height12);
                ((Line) grpBlock_.getChildren().get(11)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(11)).setEndY(height4 + height12);
                break;
            case LOAD_FALLING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height - height4 - height12);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height - height4 - height12);
                break;
            case LOAD_FALLING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height - height4 - height12);
                ((Line) grpBlock_.getChildren().get(11)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(11)).setEndY(height - height4 - height12);
                break;
            case OUT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                break;
            case OUT_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                break;
            case OUT_RISING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height4 + height12);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height4 + height12);
                break;
            case OUT_RISING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height4 + height12);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height4 + height12);
                break;
            case OUT_FALLING:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4 - height12);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height - height4 - height12);
                break;
            case OUT_FALLING_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Arc) grpBlock_.getChildren().get(5)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(5)).setCenterY(height2);
                ((Arc) grpBlock_.getChildren().get(6)).setRadiusY(height4);
                ((Arc) grpBlock_.getChildren().get(6)).setCenterY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(9)).setEndY(height - height4 - height12);
                ((Line) grpBlock_.getChildren().get(10)).setStartY(height - height4);
                ((Line) grpBlock_.getChildren().get(10)).setEndY(height - height4 - height12);
                break;
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(4)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutY(height2 - height12);
                ((Label) grpBlock_.getChildren().get(4)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height2);
                ((Label) grpBlock_.getChildren().get(9)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(9)).setLayoutY(height5);
                ((Label) grpBlock_.getChildren().get(9)).setPrefHeight(height6);
                break;
            case SET:
            case RESET:
            case RANDOM:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(4)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(4)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Label) grpBlock_.getChildren().get(7)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(7)).setLayoutY(height5);
                ((Label) grpBlock_.getChildren().get(7)).setPrefHeight(height6);
                break;
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(4)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutY(height2 - height12);
                ((Label) grpBlock_.getChildren().get(4)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(5)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(5)).setLayoutY(height2 + height12);
                ((Label) grpBlock_.getChildren().get(5)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Label) grpBlock_.getChildren().get(9)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(9)).setLayoutY(height5);
                ((Label) grpBlock_.getChildren().get(9)).setPrefHeight(height6);
                break;
            case NOT_BITS:
            case TIMER:
            case COUNTER:
            case MOVE:
            case SCRIPT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(4)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutY(height2 - height12);
                ((Label) grpBlock_.getChildren().get(4)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Label) grpBlock_.getChildren().get(8)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(8)).setLayoutY(height5);
                ((Label) grpBlock_.getChildren().get(8)).setPrefHeight(height6);
                break;
            case TIMER_NOT:
            case COUNTER_NOT:
                ((Rectangle) grpBlock_.getChildren().get(0)).setLayoutY(height4);
                ((Rectangle) grpBlock_.getChildren().get(0)).setHeight(height - height2);
                ((Label) grpBlock_.getChildren().get(1)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(1)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(2)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(2)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(3)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(3)).setLayoutY(height - height4);
                ((Label) grpBlock_.getChildren().get(3)).setPrefHeight(height6);
                ((Label) grpBlock_.getChildren().get(4)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(4)).setLayoutY(height2 - height12);
                ((Label) grpBlock_.getChildren().get(4)).setPrefHeight(height6);
                ((Line) grpBlock_.getChildren().get(5)).setStartY(height2);
                ((Line) grpBlock_.getChildren().get(5)).setEndY(height2);
                ((Line) grpBlock_.getChildren().get(6)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(6)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(7)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(7)).setEndY(height - height4);
                ((Line) grpBlock_.getChildren().get(8)).setStartY(height4);
                ((Line) grpBlock_.getChildren().get(8)).setEndY(height - height4);
                ((Label) grpBlock_.getChildren().get(9)).setFont(Font.font(height6));
                ((Label) grpBlock_.getChildren().get(9)).setLayoutY(height5);
                ((Label) grpBlock_.getChildren().get(9)).setPrefHeight(height6);
                break;
        }
    }

    private void changeWidth(double width) {
        changeBlockWidth(width);

        rectSelect_.setLayoutX(selectStrokeWidth_);
        rectSelect_.setWidth(width - (selectStrokeWidth_ * 2.0));
    }

    private void changeHeight(double height) {
        double height2 = height / 2.0;

        changeBlockHeight(height);

        rectSelect_.setLayoutY(selectStrokeWidth_);
        rectSelect_.setHeight(height - (selectStrokeWidth_ * 2.0));
        lineVertical_.setEndY(height2);
        lineVerticalOr_.setStartY(height2);
        lineVerticalOr_.setEndY(height);
    }

    /**
     *
     */
    public void changeBackGroundSelect() {
        if (ladderGrid_.isSelectRange()) {
            rectSelect_.setFill(selectBackGroundColor_);
        } else {
            rectSelect_.setFill(null);
        }
    }

    /**
     *
     */
    public void changeStrokeSelect() {
        if (ladderGrid_.isSelect()) {
            rectSelect_.setStroke(selectStrokeColor_);
        } else {
            rectSelect_.setStroke(null);
        }
    }

    /**
     *
     */
    public void changeStrokeVertical() {
        if (ladderGrid_.isVertical()) {
            lineVertical_.setStroke(baseStrokeColor_);
        } else {
            lineVertical_.setStroke(null);
        }
    }

    /**
     *
     */
    public void changeStrokeVerticalOr() {
        if (ladderGrid_.isVerticalOr()) {
            lineVerticalOr_.setStroke(baseStrokeColor_);
        } else {
            lineVerticalOr_.setStroke(null);
        }
    }

    /**
     *
     */
    public void changeContents() {
        switch (ladderGrid_.getBlock()) {
            case CONTENTS:
                setBackground(new Background(new BackgroundFill(contentsBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
                if ((ladderGrid_.getColumnIndex() == 0) && (ladderGrid_.getRowIndex() == 0)) {
                    ((Label) grpBlock_.getChildren().get(0)).setText("0");
                } else if (ladderGrid_.getColumnIndex() == 0) {
                    ((Label) grpBlock_.getChildren().get(0)).setText(Integer.toString(ladderGrid_.getRowIndex()));
                } else if (ladderGrid_.getRowIndex() == 0) {
                    ((Label) grpBlock_.getChildren().get(0)).setText(Integer.toString(ladderGrid_.getColumnIndex()));
                }
                break;
        }
    }

    /**
     *
     */
    public void changeAddress() {
        switch (ladderGrid_.getBlock()) {
            case LOAD:
            case LOAD_NOT:
            case LOAD_RISING:
            case LOAD_RISING_NOT:
            case LOAD_FALLING:
            case LOAD_FALLING_NOT:
            case OUT:
            case OUT_NOT:
            case OUT_RISING:
            case OUT_RISING_NOT:
            case OUT_FALLING:
            case OUT_FALLING_NOT:
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
            case SET:
            case RESET:
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case NOT_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
            case RANDOM:
            case TIMER:
            case TIMER_NOT:
            case COUNTER:
            case COUNTER_NOT:
            case MOVE:
            case SCRIPT:
                ((Label) grpBlock_.getChildren().get(1)).setText(ladderGrid_.getAddress());
                break;
        }
    }

    /**
     *
     */
    public void changeComment() {
        switch (ladderGrid_.getBlock()) {
            case LOAD:
            case LOAD_NOT:
            case LOAD_RISING:
            case LOAD_RISING_NOT:
            case LOAD_FALLING:
            case LOAD_FALLING_NOT:
            case OUT:
            case OUT_NOT:
            case OUT_RISING:
            case OUT_RISING_NOT:
            case OUT_FALLING:
            case OUT_FALLING_NOT:
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
            case SET:
            case RESET:
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case NOT_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
            case RANDOM:
            case TIMER:
            case TIMER_NOT:
            case COUNTER:
            case COUNTER_NOT:
            case MOVE:
            case SCRIPT:
                ((Label) grpBlock_.getChildren().get(3)).setText(ladderGrid_.getComment());
                break;
        }
    }

    /**
     *
     */
    public void changeBlockFunction() {
        switch (ladderGrid_.getBlock()) {
            case COMPARISON_EQUAL:
            case COMPARISON_NOT_EQUAL:
            case COMPARISON_LESS:
            case COMPARISON_LESS_EQUAL:
            case COMPARISON_GREATER:
            case COMPARISON_GREATER_EQUAL:
            case COMPARISON_AND_BITS:
            case COMPARISON_OR_BITS:
            case COMPARISON_XOR_BITS:
            case NOT_BITS:
            case MOVE:
                if (ladderGrid_.getBlockFunctions()[0].isNumber()) {
                    switch (ladderGrid_.getBlockFunctions()[0].getRadix()) {
                        case 10:
                            ((Label) grpBlock_.getChildren().get(4)).setText(Double.toString(ladderGrid_.getBlockFunctions()[0].getValue()));
                            break;
                        case 16:
                            ((Label) grpBlock_.getChildren().get(4)).setText("0x" + Long.toString((long) ladderGrid_.getBlockFunctions()[0].getValue(), 16));
                            break;
                        case 2:
                            ((Label) grpBlock_.getChildren().get(4)).setText("0b" + Long.toString((long) ladderGrid_.getBlockFunctions()[0].getValue(), 2));
                            break;
                    }
                } else {
                    ((Label) grpBlock_.getChildren().get(4)).setText(ladderGrid_.getBlockFunctions()[0].getAddress());
                }
                break;
            case AND_BITS:
            case OR_BITS:
            case XOR_BITS:
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
            case AVERAGE:
            case SHIFT_LEFT_BITS:
            case SHIFT_RIGHT_BITS:
            case SIGMOID:
                if (ladderGrid_.getBlockFunctions()[0].isNumber()) {
                    switch (ladderGrid_.getBlockFunctions()[0].getRadix()) {
                        case 10:
                            ((Label) grpBlock_.getChildren().get(4)).setText(Double.toString(ladderGrid_.getBlockFunctions()[0].getValue()));
                            break;
                        case 16:
                            ((Label) grpBlock_.getChildren().get(4)).setText("0x" + Long.toString((long) ladderGrid_.getBlockFunctions()[0].getValue(), 16));
                            break;
                        case 2:
                            ((Label) grpBlock_.getChildren().get(4)).setText("0b" + Long.toString((long) ladderGrid_.getBlockFunctions()[0].getValue(), 2));
                            break;
                    }
                } else {
                    ((Label) grpBlock_.getChildren().get(4)).setText(ladderGrid_.getBlockFunctions()[0].getAddress());
                }

                if (ladderGrid_.getBlockFunctions()[1].isNumber()) {
                    switch (ladderGrid_.getBlockFunctions()[1].getRadix()) {
                        case 10:
                            ((Label) grpBlock_.getChildren().get(5)).setText(Double.toString(ladderGrid_.getBlockFunctions()[1].getValue()));
                            break;
                        case 16:
                            ((Label) grpBlock_.getChildren().get(5)).setText("0x" + Long.toString((long) ladderGrid_.getBlockFunctions()[1].getValue(), 16));
                            break;
                        case 2:
                            ((Label) grpBlock_.getChildren().get(5)).setText("0b" + Long.toString((long) ladderGrid_.getBlockFunctions()[1].getValue(), 2));
                            break;
                    }
                } else {
                    ((Label) grpBlock_.getChildren().get(5)).setText(ladderGrid_.getBlockFunctions()[1].getAddress());
                }
                break;
            case TIMER:
            case COUNTER:
                if (ladderGrid_.getBlockFunctions()[0].isNumber()) {
                    if (!ladderGrid_.isBlockLd()) {
                        switch (ladderGrid_.getBlockFunctions()[0].getRadix()) {
                            case 10:
                                ((Label) grpBlock_.getChildren().get(4)).setText(Double.toString(ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()));
                                break;
                            case 16:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0x" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()), 16));
                                break;
                            case 2:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0b" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()), 2));
                                break;
                        }
                    } else {
                        ((Label) grpBlock_.getChildren().get(4)).setText("0");
                    }
                } else {
                    ((Label) grpBlock_.getChildren().get(4)).setText(ladderGrid_.getBlockFunctions()[0].getAddress());
                }
                break;
            case TIMER_NOT:
            case COUNTER_NOT:
                if (ladderGrid_.getBlockFunctions()[0].isNumber()) {
                    if (ladderGrid_.isBlockLd()) {
                        switch (ladderGrid_.getBlockFunctions()[0].getRadix()) {
                            case 10:
                                ((Label) grpBlock_.getChildren().get(4)).setText(Double.toString(ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()));
                                break;
                            case 16:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0x" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()), 16));
                                break;
                            case 2:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0b" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue() - ladderGrid_.getCumulativeValue()), 2));
                                break;
                        }
                    } else {
                        switch (ladderGrid_.getBlockFunctions()[0].getRadix()) {
                            case 10:
                                ((Label) grpBlock_.getChildren().get(4)).setText(Double.toString(ladderGrid_.getBlockFunctions()[0].getValue()));
                                break;
                            case 16:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0x" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue()), 16));
                                break;
                            case 2:
                                ((Label) grpBlock_.getChildren().get(4)).setText("0b" + Long.toString((long) (ladderGrid_.getBlockFunctions()[0].getValue()), 2));
                                break;
                        }
                    }
                } else {
                    ((Label) grpBlock_.getChildren().get(4)).setText(ladderGrid_.getBlockFunctions()[0].getAddress());
                }
                break;
        }
    }

    /**
     *
     */
    public void changeBlockScript() {
        switch (ladderGrid_.getBlock()) {
            case SCRIPT:
                ((Label) grpBlock_.getChildren().get(4)).setText(ladderGrid_.getBlockScript());
                break;
        }
    }

    /**
     *
     * @param type
     */
    public void changeDifference(Delta.TYPE type) {
        switch (type) {
            case CHANGE:
                setBackground(new Background(new BackgroundFill(differenceChangeBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            case DELETE:
                setBackground(new Background(new BackgroundFill(differenceDeleteBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
            case INSERT:
                setBackground(new Background(new BackgroundFill(differenceInsertBackGroundColor_, CornerRadii.EMPTY, Insets.EMPTY)));
                break;
        }
    }
}
