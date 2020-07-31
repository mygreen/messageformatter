[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/messageformatter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/messageformatter/)
[![Javadocs](http://javadoc.io/badge/com.github.mygreen/messageformatter.svg?color=blue)](http://javadoc.io/doc/com.github.mygreen/messageformatter)
[![Build Status](https://travis-ci.org/mygreen/messageformatter.svg?branch=master)](https://travis-ci.org/mygreen/messageformatter)
[![SonarQube](https://sonarcloud.io/api/project_badges/measure?project=com.github.mygreen%3Amessageformatter&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.mygreen%3Amessageformatter)

# MessageFormatter

Spring Frameworkの ``MessageSource`` を使い、表示メッセージをフォーマットするライブラリです。

メッセージ中にパラメータを渡したり、EL式を評価したりできます。

## Licensee
MIT License

## Dependency

- Java 11+
- SpringFramework 5.0+
- BeanValidation 2.0(optional)
- JEXL 2.1(optional)

## Setup

App dependency. ex) pom.xml

```xml
<dependency>
	<groupId>com.github.mygreen</groupId>
	<artifactId>messageformatter</artifactId>
	<vesion>0.1</version>
</dependency>
```

## How to use
1. Define message property
  ```properties
  test.message=the ${#formatter.format('%1.2f', #validatedValue)} is invalid. Please must be between {min} and {max}.
  ```
2. Create instance the ``MessageFormatter``
  ```java
  // MessageSource
  ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
  messageSource.addBasenames("your_messages");
  messageSource.setDefaultEncoding("UTF-8");
  messageSource.setFallbackToSystemLocale(false);

  // MessageInterpolator with ExpressionEvaluator
  // ex) SpEL
  MessageInterpolator messageInterpolator = new MessageInterpolator(new SpelExpressionEvaluator());

  MessageFormatter messageFormatter = new MessageFormatter(messageSource, messageInterpolator);
  ```
3. Set param and format.
  ```java
  double value = 3.1;
  String message = messageFormatter.create("test.message")
      .param("validatedValue", value)
      .param("min", 1)
      .param("max", 10)
      .format();
  ```

## Documentation

- Project information and manual.
  - https://mygreen.github.io/messageformatter/index.html
- JavaDoc
  - https://mygreen.github.io/messageformatter/apidocs/index.html

