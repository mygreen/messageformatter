package com.github.mygreen.messageformatter;

import lombok.Getter;

/**
 * メッセージのフォーマットに失敗した際にスローされる例外
 * @author T.TSUCHIE
 *
 */
public class MessageParseException extends RuntimeException {

    /**
     * フォーマットに失敗したメッセージ
     */
    @Getter
    private final String value;

    /**
     * インスタンスを作成します。
     * @param value フォーマットに失敗したメッセージ
     * @param message エラーメッセージ
     */
    public MessageParseException(final String value, final String message) {
        super(message);
        this.value = value;
    }
}
