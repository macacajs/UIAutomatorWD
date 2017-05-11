package com.macaca.android.testing.server.charsetUtils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CharsetProvider extends java.nio.charset.spi.CharsetProvider {
    private static final String UTF7_NAME = "UTF-7";
    private static final String UTF7_O_NAME = "X-UTF-7-OPTIONAL";
    private static final String UTF7_M_NAME = "X-MODIFIED-UTF-7";
    private static final String[] UTF7_ALIASES = new String[] { "UNICODE-1-1-UTF-7",
            "CSUNICODE11UTF7", "X-RFC2152", "X-RFC-2152" };
    private static final String[] UTF7_O_ALIASES = new String[] { "X-RFC2152-OPTIONAL",
            "X-RFC-2152-OPTIONAL" };
    private static final String[] UTF7_M_ALIASES = new String[] { "X-IMAP-MODIFIED-UTF-7",
            "X-IMAP4-MODIFIED-UTF7", "X-IMAP4-MODIFIED-UTF-7", "X-RFC3501", "X-RFC-3501" };
    private Charset utf7charset = new UTF7Charset(UTF7_NAME, UTF7_ALIASES, false);
    private Charset utf7oCharset = new UTF7Charset(UTF7_O_NAME, UTF7_O_ALIASES, true);
    private Charset imap4charset = new ModifiedUTF7Charset(UTF7_M_NAME, UTF7_M_ALIASES);
    private List charsets;

    public CharsetProvider() {
        charsets = Arrays.asList(new Object[] { utf7charset, imap4charset, utf7oCharset });
    }

    public Charset charsetForName(String charsetName) {
        charsetName = charsetName.toUpperCase(Locale.US);
        for (Iterator iter = charsets.iterator(); iter.hasNext();) {
            Charset charset = (Charset) iter.next();
            if (charset.name().equals(charsetName))
                return charset;
        }
        for (Iterator iter = charsets.iterator(); iter.hasNext();) {
            Charset charset = (Charset) iter.next();
            if (charset.aliases().contains(charsetName))
                return charset;
        }
        return null;
    }

    public Iterator charsets() {
        return charsets.iterator();
    }

}
