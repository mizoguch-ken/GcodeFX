# <u>GcodeFX</u>
## <u>Overview 概要</u>
 * G-Code communication, conversion and extension functions.  
 G-Codeの通信,変換と拡張機能  
## <u>Description 説明</u>
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
## <u>Requirement 要件</u>
 * Java 8 update 121 or later  
 Java 8 update 121 以上  
## <u>Dependency 依存</u>
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
      <groupId>net.java.dev.jna</groupId>
      <artifactId>jna</artifactId>
    </dependency>
    <dependency>
      <groupId>net.java.dev.jna</groupId>
      <artifactId>jna-platform</artifactId>
    </dependency>
    <dependency>
      <groupId>com.neuronrobotics</groupId>
      <artifactId>nrjavaserial</artifactId>
    </dependency>
    <dependency>
      <groupId>org.apache.pdfbox</groupId>
      <artifactId>pdfbox</artifactId>
    </dependency>
 ## <u>Download ダウンロード</u>
 Please download zip file from release  
 リリースからzipファイルをダウンロードしてください  
 ## <u>Java11.0.2(Windows)</u>
1. Download JDK 11.0.2 and extract it to the GcodeFX folder  
JDK 11をダウンロードしてGcodeFXフォルダに解凍します  
2. Download JavaFX SDK 11.0.2 and extract it to the GcodeFX folder  
JavaFX SDK 11をダウンロードしてGcodeFXフォルダに解凍します  
3. Create GcodeFX.bat in the GcodeFX folder and write the contents as follows  
GcodeFXフォルダにGcodeFX.batを作成し、内容を次のように書きます  
```
%~dp0jdk-11.0.2\bin\java.exe -jar --module-path=%~dp0javafx-sdk-11.0.2\lib --add-modules=javafx.fxml,javafx.web GcodeFX.jar
```
4. Run GcodeFX.bat  
GcodeFX.batを実行します  

## <u>Configuration 設定</u>

[Gcode](https://github.com/mizoguch-ken/GcodeFX/wiki/Gcode)

[Ladders](https://github.com/mizoguch-ken/GcodeFX/wiki/Ladders)

[EtherCAT](https://github.com/mizoguch-ken/GcodeFX/wiki/EtherCAT)