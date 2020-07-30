# BeanValidationとの連携

BeanValidationのメッセージフォーマット処理を本ライブラリに切り替える方法を説明します。

- EL式が使いづらい。
  - BeanValidation のEL式の実装は、JSPのEL式を利用していますが、カスタム関数の登録にTLD形式で登録する必要があるなど面倒.。
- パラメータの埋め込みやEL式の埋め込むための ``${,{,}`` のプレースホルダーの処理に問題があり、エスケープ処理できない。
  - メッセージ中に文字として、`${` を出力したい場合、正しく解釈されないケースがある。

## BeanValidationのメッセージ処理の切り替え方法

BeanValidationの ``MessageInterplator`` の実装 ``CustomMessageInterpolator`` を使用します。

本ライブラリの ``MessageInterpolator`` と BeanValidationのクラス名が同じなので注意してください。

```java
@Configuration
public class ValidationConfig {

    // メッセージソース
    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("test_messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }

    @Description("Validator of BeanValidation")
    @Bean
    public LocalValidatorFactoryBean validator() {
        // BeanValidationにブリッジするCustomMessageInterpolatorの作成
        CustomMessageInterpolator messageInterpolator = new CustomMessageInterpolator(
                messageSource(),
                new MessageInterpolator(new SpelExpressionEvaluator()));

        // BeanValidationのファクトリクラス
        final LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setMessageInterpolator(messageInterpolator);

        return factory;
    }
```


