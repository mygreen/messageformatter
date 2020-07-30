# 再帰的にメッセージを評価する

- ``MessageFormatter`` にて指定されていないメッセージ中のパラメータ ``{パラメータ名}`` は、メッセージソースの中に定義されている定義でさらに変換が可能です。
- その際に ``formatRecursively()`` にてフォーマットを行います。
  - また、無限に再帰するような定義のとき ``StackOverflowError`` が発生するので、再帰回数を指定し回避することが可能です。

```properties
// メッセージの定義
message.recursion=Hello! {your.name}
your.name=Taro
```

```java
// メッセージのフォーマットの再帰評価
MessageFormatter messageFormatter = ...;

// 結果は「Hello! Taro」となる。
String message = messageFormatter.create("message.recursion")
    .formatRecursively();
```

