package com.github.nedis.codec;

/**
 * User: roger
 * Date: 12-3-31 下午2:04
 */
public class ErrorReply extends  Reply<String> {
    public static final char MARKER = '-';
    public ErrorReply(String error) {
        super(MARKER, error);
    }
}
