# 基本的な使い方

## 1.メッセージの定義

Springの ``MessageSource`` から参照できるプロパティを参照するため、まずはじめに、プロパティファイルにメッセージを定義します。

- 書式 ``{パラメータ名}`` にて、パラメータを埋め込むことができます。
- 書式 ``${EL式}`` にて、EL式を埋め込むことができます（詳細は「[EL式を使ったフォーマット](expression_launguage.html)」を参照）。

```properties
## 標準的な値
message.hello=Hello Wolrd ! {your_name}
```

## 2.MessageFormatterのインスタンスの作成

```java
// MessageSourceのインスタンスの作成
ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
messageSource.addBasenames("messages");
messageSource.setDefaultEncoding("UTF-8");
messageSource.setFallbackToSystemLocale(false);

// メッセージを実際にフォーマとする MessageInterpolator のインスタンスの組み立て
// EL式の実装を切り替えることができます。ここでは、SpEL を使用します。
MessageInterpolator messageInterpolator = new MessageInterpolator(new SpelExpressionEvaluator());

// MessageFormatter を作成します。
MessageFormatter messageFormatter = new MessageFormatter(messageSource, messageInterpolator);
```

## 3.メッセージをフォーマットする

- ``create("<プロパティのキー>")`` にて、定義したメッセージのキーを指定します。
- ``param("<パラメータ名>", <値>)`` にて、メッセージ中のパラメータの値を指定します。
  - パラメータで埋め込んだ値は、メソッド ``#toString()`` にて文字列に変換されます。
  - 数値や日付を独自のフォーマットに変換したければ、予めフォーマットした結果を設定するか、「[EL式によるフォーマット](expression_launguage.html)」を行ってください。 
- ``format()`` にて、メッセージをフォーマットします。

```java
String message = messageFormatter.create("message.hello")
    .param("your_name", "Yamada Taro")
    .format();
```

