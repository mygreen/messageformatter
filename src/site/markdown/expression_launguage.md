# EL式を使ったフォーマット

EL式（Epression Laungage）とは、オブジェクトのアクセスを簡略化して記述できる式言語です。

実装は複数存在し、本ライブラリでは以下の種類をサポートします。

- [SpEL](https://spring.pleiades.io/spring-integration/reference/html/spel.html) : Spring Framework標準の式言語。
- [JEXL](https://commons.apache.org/proper/commons-jexl/) : JSPの式言語を拡張したApache Commons プロジェクトの中のライブラリ。 

JEXLの方がJSPのEL式と仕様が共通しておりわかりやすいですが、SpELはSpring Framework標準の言語であるためSpring環境下で開発するのであればライブラリの追加などが不要です。

## EL式の処理をJEXLに切り替える

pom.xmlにJEXLの依存関係を追加します。
本ライブラリでは、JEXL v2.1を私用します。v3.xには対応していません。

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-jexl</artifactId>
    <version>2.1.1</version>
    <scope>provided</scope>
</dependency>
```

式言語の処理の実装として、 ``JexlExpressionEvaluator`` を指定します。

```java
// JexlExpressionEvaluator を指定します。
MessageInterpolator messageInterpolator = new MessageInterpolator(new JexlExpressionEvaluator());

MessageFormatter messageFormatter = new MessageFormatter(messageSource, messageInterpolator);
```

## EL式での処理方法

メッセージ中では ``${EL式}`` にて定義します。

- 標準で、``java.util.Formatter`` が 変数 ``formatter`` として予め登録されており、``String#format(...)`` の書式でフォーマットできます。
- SpELでは式中の変数を ``#変数名`` で参照します。
- JEXLは式中の変数は、``変数名`` で参照します。


```properties
## SpELの場合
message.el=値（${#formatter.format('%1.2f', #validatedValue)}) は、{min} 以上で設定してください。

## JEXLの場合
message.el=値（${formatter.format('%1.2f', validatedValue)}) は、{min} 以上で設してください。
```

``param(...)`` で指定したパラメータは、EL式中の変数として参照できます。

```java
double validatedValue = 3.1;
String message = messageFormatter.create("message.el")
    .param("validatedValue", validatedValue)
    .param("min", 5)
    .format();
```

## 標準のカスタム関数を使用する

EL式の標準文法だけでは処理が複雑になる場合があるため、クラス ``CustomFunctions`` 内のユーティリティメソッドをカスタム関数として登録しており、利用できます。

| カスタム関数 | 説明  |
| ----------- | ----- |
| ``empty(値)``  | 引数で渡した値が ``null`` または空かどうか判定します。文字列の場合は空文字かどうか、配列やCollection、Mapの場合は要素数が0からどうかも判定します。|
| ``defaultString(値)``  | 引数で渡した値が ``null`` のとき、空文字に変換します。``null`` 出ないときは、値はそのまま。|
| ``join(値, 区切り文字)``  | 引数で渡した値（配列、Collection）を区切り文字で結合します。|
| ``size(値)``  | 引数で渡した値（配列、Collection、文字列）のサイズを返します。|

- SpELの場合は、``#カスタム関数名(引数)`` の書式で呼び出します。
- JEXLの場合は、``f:カスタム関数名(引数)`` の書式で呼び出します。

```properties
## SpELの場合
message.spel=値は ${#join(#array, ',')} の何れかで設定してください。

## JXELの場合
message.jexl=値は ${f:join(array, ',')} の何れかで設定してください。
```

```java
String validatedValue = "-";
String message = messageFormatter.create("message.el")
    .param("validatedValue", validatedValue)
    .param("array", new String[]{"〇", "×", "△"})
    .format();
```

## 独自のカスタム関数を登録する

SpELの場合、``StaticMethodMapCreator`` にて、ユーティリティクラスから、 ``public static`` メソッドを抽出し、カスタム関数として登録します。

```java
// ユーティリティメソッドのマップを作成します
Map<String, Method> functionMap = StaticMethodMapCreator.create(SampleUtils.class);

// カスタム関数を登録します。
SpelExpressionEvaluator expressionEvaluator = new SpelExpressionEvaluator();
expressionEvaluator.getCustomFunctions.putAll(functionMap);

// MessageFormatterの組み立て
MessageFormatter messageFormatter = new MessageFormatter(messageSource, new MessageInterpolator(expressionEvaluator));
```

JEXLの場合、``JexlEngine#getFunction()`` からカスタム関数のマップを種痘して、接頭語を指定して、ユーティリティクラスを登録します。

```java
// カスタム関数を登録します。。
JexlExpressionEvaluator expressionEvaluator = new JexlExpressionEvaluator();
expressionEvaluator.getJexlEngine().getFunctions.put("c", SampleUtils.class);

// MessageFormatterの組み立て
MessageFormatter messageFormatter = new MessageFormatter(messageSource, new MessageInterpolator(expressionEvaluator));
```