package com.macaca.android.testing.server.charsetUtils;

class ModifiedUTF7Charset extends UTF7StyleCharset {
    private static final String MODIFIED_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789+,";

    ModifiedUTF7Charset(String name, String[] aliases) {
        super(name, aliases, MODIFIED_BASE64_ALPHABET, true);
    }

    boolean canEncodeDirectly(char ch) {
        if (ch == shift())
            return false;
        return ch >= 0x20 && ch <= 0x7E;
    }

    byte shift() {
        return '&';
    }

    byte unshift() {
        return '-';
    }
}
