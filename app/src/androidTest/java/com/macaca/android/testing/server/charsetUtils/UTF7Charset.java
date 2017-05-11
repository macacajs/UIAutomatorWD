package com.macaca.android.testing.server.charsetUtils;

class UTF7Charset extends UTF7StyleCharset {
    private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789+/";
    private static final String SET_D = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'(),-./:?";
    private static final String SET_O = "!\"#$%&*;<=>@[]^_`{|}";
    private static final String RULE_3 = " \t\r\n";
    final String directlyEncoded;

    UTF7Charset(String name, String[] aliases, boolean includeOptional) {
        super(name, aliases, BASE64_ALPHABET, false);
        if (includeOptional)
            this.directlyEncoded = SET_D + SET_O + RULE_3;
        else
            this.directlyEncoded = SET_D + RULE_3;
    }

    boolean canEncodeDirectly(char ch) {
        return directlyEncoded.indexOf(ch) >= 0;
    }

    byte shift() {
        return '+';
    }

    byte unshift() {
        return '-';
    }
}
