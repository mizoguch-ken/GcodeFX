# GcodeFX
## Overview 概要
 * G-Code communication, conversion and extension functions.  
 G-Codeの通信,変換と拡張機能  
## Description 説明
 * G-Code transfer using RS-232C  
 RS-232Cを使用したG-Code転送
 * Transfer while processing G-Code to simple code  
 G-Codeを単純なコードに処理しながら転送
 * Adding functions using WebViewer  
 WebViewerを使った機能の追加
 * Plug-in function to WebViewer  
 WebViewerへのプラグイン機能
 * Unique ladder logic  
 独特なラダーロジック
 * G-Code, Ladder Logic and WebView collaboration  
 G-Code,ラダーロジックとWebViewerの連携  
## Requirement 要件
 * Java 8 update 40 or later  
 Java 8 update 40 以上  
## Dependency 依存
    <dependency>
      <groupId>com.googlecode.java-diff-utils</groupId>
      <artifactId>diffutils</artifactId>
    </dependency>
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
    </dependency>
    <dependency>
      <groupId>com.github.jnr</groupId>
      <artifactId>jnr-ffi</artifactId>
    </dependency>
    <dependency>
      <groupId>com.neuronrobotics</groupId>
      <artifactId>nrjavaserial</artifactId>
    </dependency>
 ## Download ダウンロード
 Please download zip file from release  
 リリースからzipファイルをダウンロードしてください  
 ## Configuration(minimum) 設定(最小)
1. Select menu bar [Settings] -> [Virtual Machine]  
メニューバー [設定]->[仮想マシン] を選択  
2. Select the tab of [G Code Group]  
[Gコードグループ]のタブを選択  
3. Set values from [Group 0] to [Group 30]  
Separate G code number with [|]  
Add [@] before the group's initial value  
[グループ0]から[グループ30]までに値を設定  
Gコード番号を[|]で区切る  
グループの初期値の前に[@]を付ける  
  
e.g.  
```
[Group 0]
4 | 5 | 5.1 | 7.1 | 8 | 9 | 10 | 11 | 27 | 28 | 29 | 30 | 31 | 37 | 39 | 45 | 46 | 47 | 48 | 52 | 53 | 60 | 65 | 92 | 92.1
[Group 1]
@0 | 1 | 2 | 3 | 33
[Group 2]
@17 | 18 | 19
[Group 3]
@90 | 91
[Group 4]
@22 | 23
[Group 5]
@94 | 95
[Group 6]
20 | @21
[Group 7]
@40 | 41 | 42
[Group 8]
43 | 44 | @49
[Group 9]
73 | 74 | 76 | @80 | 81 | 82 | 83 | 84 | 85 | 86 | 87 | 88 | 89
[Group 10]
@98 | 99
[Group 11]
@50 | 51
[Group 12]
66 | @67
[Group 13]
96 | @97
[Group 14]
@54 | 54.1 | 55 | 56 | 57 | 58 | 59
[Group 15]
61 | 62 | 63 | @64
[Group 16]
68 | @69
[Group 17]
@15 | 16
[Group 18]
@40.1 | 41.1 | 42.1
[Group 19]
@40.1 | 41.1 | 42.1
[Group 20]
[Group 21]
[Group 22]
@50.1 | 51.1
[Group 23]
[Group 24]
[Group 25]
[Group 26]
[Group 27]
[Group 28]
[Group 29]
[Group 30]
```
## Test テスト
1. Write G code in editor  
エディタにGコードを記入  
  
e.g.  
```
O1
G0G90X0Y0
X100.Y100.
M30
```
2. Fill in the program number in the upper left text box  
左上のテキストボックスにプログラム番号を記入  
  
e.g.  
```
O1
```
3.  Select menu bar [File] -> [ngc]  
メニューバー [ファイル]->[ngc] を選択  
## Debug1 デバッグ1
1. Select menu bar [Settings] -> [Virtual Machine]  
メニューバー [設定]->[仮想マシン] を選択  
2. Select the tab of [Base]  
[基本]のタブを選択  
3. Check the check box [Debug]  
チェックボックス [デバッグ] にチェックを付ける  
4.  Select menu bar [File] -> [ngc]  
メニューバー [ファイル]->[ngc] を選択  
5. [***.dbg] [***. dxf] [***. ngc] file is generated in [debug] folder  
[***. dbg] : Debug file  
[***. dxf] : DXF file  
[***. ngc] : Output file  
[debug] フォルダの中に [***.dbg][***.dxf][***.ngc] ファイルが生成  
[***.dbg] : デバッグファイル  
[***.dxf] : DXFファイル  
[***.ngc] : 出力ファイル  
## Debug2 デバッグ2
1. Select menu bar [Settings] -> [Virtual Machine]  
メニューバー [設定]->[仮想マシン] を選択  
2. Select the tab of [Base]  
[基本]のタブを選択  
3. Check the check box [Debug]  
チェックボックス [デバッグ] にチェックを付ける  
4. Select the tab of [Option]  
[オプション]のタブを選択  
5. Check the check box [Debug JSON]  
チェックボックス [デバッグ JSON] にチェックを付ける  
6.  Select menu bar [File] -> [ngc]  
メニューバー [ファイル]->[ngc] を選択  
7. [***.dbg] [***. dxf] [***. ngc] file is generated in [debug] folder  
[***. dbg] : Debug file(JSON)  
[***. dxf] : DXF file  
[***. ngc] : Output file  
[debug] フォルダの中に [***.dbg][***.dxf][***.ngc] ファイルが生成  
[***.dbg] : デバッグファイル(JSON)  
[***.dxf] : DXFファイル  
[***.ngc] : 出力ファイル  
8. Open [debugger] -> [index.html] in the [web] folder  
[web] フォルダの中の [debugger]->[index.html] を開く  
9. [***. dbg] Select file  
[***. dbg] ファイルを選択  

## Ladders ラダー
It is input from the left side of the block and is output from the right side.  
ブロックの左側から入力され、右側から出力されます。  
  
Address value 0 is False, Otherwise it is True.  
アドレスの値が0の場合は False です。 それ以外の場合は True です。  
  
To display the input box, press the Enter key on the ladder.  
入力ボックスを表示するには、ラダーでEnterキーを押します。  
  
Block connection is Ctrl + arrow keys.  
ブロック接続はCtrl + 矢印キーです。  
  
Flip with / key.  
/ キーで反転  
  
Rising / Falling with Ctrl + / keys.  
Ctrl + / キーで立上がり/立下り。  
  
### Load 入力
|Name<br>名前|Block<br>ブロック| |Writing<br>([] Is optional)<br>書き方<br>([]はオプション)|
|:-|:-|:-|:-|
|LOAD| addr<br>-\|   \|-|if addr<>0 then True else False|ld addr[;comment]|
|LOAD_NOT| addr<br>-\|/  \|-|if addr<>0 then False else True|/ld addr[;comment]|
|LOAD_RISING| addr<br>-\| ↑ \|-|if Rising-addr then True else False|@ld addr[;comment]|
|LOAD_RISING_NOT| addr<br>-\|/↑ \|-|if Rising-addr then False else True|/@ld addr[;comment]|
|LOAD_FALLING| addr<br>-\| ↓ \|-|if Falling-addr then True else False|%ld addr[;comment]|
|LOAD_FALLING_NOT| addr<br>-\|/↓ \|-|if Falling-addr then False else True|/%ld addr[;comment]|
### Out 出力
|Name<br>名前|Block<br>ブロック| |Writing<br>([] Is optional)<br>書き方<br>([]はオプション)|
|:-|:-|:-|:-|
|OUT| addr<br>-(   )|if True then addr=1 else addr=0|out addr[;comment]|
|OUT_NOT| addr<br>-(/  )|if True then addr=0 else addr=1|/out addr[;comment]|
|OUT_RISING| addr<br>-( ↑ )|if Rising then addr=1 else addr=0|@out addr[;comment]|
|OUT_RISING_NOT| addr<br>-(/↑ )|if Rising then addr=0 else addr=1|/@out addr[;comment]|
|OUT_FALLING| addr<br>-( ↓ )|if Falling then addr=1 else addr=0|%out addr[;comment]|
|OUT_FALLING_NOT| addr<br>-(/↓ )|if Falling then addr=0 else addr=1|/%out addr[;comment]|
### Load Function 入力機能
|Name<br>名前|Block<br>ブロック| |Writing<br>([] Is optional)<br>(addr/const Is addr or const)<br>書き方<br>([]はオプション)<br>(addr/constは addr または const)|
|:-|:-|:-|:-|
|COMPARISON_EQUAL| addr<br>-\|= addr1/const1\|-|if addr = addr1/const1 then True else False|= addr addr1/const1[;comment]|
|COMPARISON_NOT_EQUAL| addr<br>-\|<> addr1/const1\|-|if addr <> addr1/const1 then True else False|<> addr addr1/const1[;comment]|
|COMPARISON_LESS| addr<br>-\|< addr1/const1\|-|if addr < addr1/const1 then True else False|< addr addr1/const1[;comment]|
|COMPARISON_LESS_EQUAL| addr<br>-\|<= addr1/const1\|-|if addr <= addr1/const1 then True else False|<= addr addr1/const1[;comment]|
|COMPARISON_GREATER| addr<br>-\|> addr1/const1\|-|if addr > addr1/const1 then True else False|> addr addr1/const1[;comment]|
|COMPARISON_GREATER_EQUAL| addr<br>-\|>= addr1/const1\|-|if addr >= addr1/const1 then True else False|>= addr addr1/const1[;comment]|
|COMPARISON_AND_BITS| addr<br>-\|& addr1/const1\|-|if addr & addr1/const1 then True else False|& addr addr1/const1[;comment]|
|COMPARISON_OR_BITS| addr<br>-\|\| addr1/const1\|-|if addr \| addr1/const1 then True else False|\| addr addr1/const1[;comment]|
|COMPARISON_XOR_BITS| addr<br>-\|^ addr1/const1\|-|if addr ^ addr1/const1 then True else False|^ addr addr1/const1[;comment]|
### Out Function 出力機能
|Name<br>名前|Block<br>ブロック| |Writing<br>([] Is optional)<br>(addr/const Is addr or const)<br>書き方<br>([]はオプション)<br>(addr/constは addr または const)|
|:-|:-|:-|:-|
|SET| addr<br>-\|SET \||addr=1|set addr[;comment]|
|RESET| addr<br>-\|RES \||addr=0|res addr[;comment]|
|AND_BITS| addr<br>-\|AND addr1/const1 addr2/const2\||addr=addr1/const1 & addr2/const2|and addr addr1/const1 addr2/const2[;comment]|
|OR_BITS| addr<br>-\|OR addr1/const1 addr2/const2\||addr=addr1/const1 \| addr2/const2|or addr addr1/const1 addr2/const2[;comment]|
|XOR_BITS| addr<br>-\|XOR addr1/const1 addr2/const2\||addr=addr1/const1 ^ addr2/const2|xor addr addr1/const1 addr2/const2[;comment]|
|NOT_BITS| addr<br>-\|NOT addr1/const1\||addr=~addr1/const1|not addr addr1/const1[;comment]|
|ADDITION| addr<br>-\|ADD addr1/const1 addr2/const2\||addr=addr1/const1 + addr2/const2|add addr addr1/const1 addr2/const2[;comment]|
|SUBTRACTION| addr<br>-\|SUB addr1/const1 addr2/const2\||addr=addr1/const1 - addr2/const2|sub addr addr1/const1 addr2/const2[;comment]|
|MULTIPLICATION| addr<br>-\|MUL addr1/const1 addr2/const2\||addr=addr1/const1 * addr2/const2|mul addr addr1/const1 addr2/const2[;comment]|
|DIVISION| addr<br>-\|DIV addr1/const1 addr2/const2\||addr=addr1/const1 / addr2/const2|div addr addr1/const1 addr2/const2[;comment]|
|AVERAGE| addr<br>-\|AVE addr1/const1 addr2/const2\||addr=(addr1/const1 + addr2/const2) / 2|ave addr addr1/const1 addr2/const2[;comment]|
|SHIFT_LEFT_BITS| addr<br>-\|SFL addr1/const1 addr2/const2\||addr=addr1/const1 << addr2/const2|sfl addr addr1/const1 addr2/const2[;comment]|
|SHIFT_RIGHT_BITS| addr<br>-\|SFR addr1/const1 addr2/const2\||addr=addr1/const1 >> addr2/const2|sfr addr addr1/const1 addr2/const2[;comment]|
|SIGMOID| addr<br>-\|SIG addr1/const1 addr2/const2\||addr=(Tanh(addr1/const1 * addr2/const2 / 2) + 1) / 2|sig addr addr1/const1 addr2/const2[;comment]|
|RANDOM| addr<br>-\|RND \||addr=Random()|rnd addr[;comment]|
|TIMER| addr<br>-\|TIM addr1/const1\|| |tim addr addr1/const1[;comment]|
|TIMER_NOT| addr<br>-\|/TIM addr1/const1\|| |/tim addr addr1/const1[;comment]|
|COUNTER| addr<br>-\|CNT addr1/const1\|| |cnt addr addr1/const1[;comment]|
|COUNTER_NOT| addr<br>-\|/CNT addr1/const1\|| |/cnt addr addr1/const1[;comment]|
|MOVE| addr<br>-\|MOV addr1/const1\||addr=addr1/const1|mov addr addr1/const1[;comment]|
### Collaboration 連携
It is necessary to register WEB in script.  
WEBをスクリプトで登録する必要があります。  
```
ladders.registerWeb();
```
|Name<br>名前|Block<br>ブロック| |Writing<br>([] Is optional.)<br>書き方<br>([]はオプション)|
|:-|:-|:-|:-|
|SCRIPT| addr<br>-\|SCR script\|| |scr addr;script[;comment]|
