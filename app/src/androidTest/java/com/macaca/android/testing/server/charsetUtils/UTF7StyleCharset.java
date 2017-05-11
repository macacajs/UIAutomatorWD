package com.macaca.android.testing.server.charsetUtils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.List;

abstract class UTF7StyleCharset extends Charset {
    private static final List CONTAINED = Arrays.asList(new String[] { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16LE", "UTF-16BE" });
    final boolean strict;
    Base64Util base64;

    protected UTF7StyleCharset(String canonicalName, String[] aliases, String alphabet,
            boolean strict) {
        super(canonicalName, aliases);
        this.base64 = new Base64Util(alphabet);
        this.strict = strict;
    }

    public boolean contains(final Charset cs) {
        return CONTAINED.contains(cs.name());
    }

    public CharsetDecoder newDecoder() {
        return new UTF7StyleCharsetDecoder(this, base64, strict);
    }

    public CharsetEncoder newEncoder() {
        return new UTF7StyleCharsetEncoder(this, base64, strict);
    }

    abstract boolean canEncodeDirectly(char ch);

    abstract byte shift();

    abstract byte unshift();
}
