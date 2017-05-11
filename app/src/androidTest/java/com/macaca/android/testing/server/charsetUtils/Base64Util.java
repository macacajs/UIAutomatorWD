package com.macaca.android.testing.server.charsetUtils;

import java.util.Arrays;

class Base64Util {
    private static final int ALPHABET_LENGTH = 64;
    private final char[] alphabet;
    private final int[] inverseAlphabet;

    Base64Util(final String alphabet) {
        this.alphabet = alphabet.toCharArray();
        if (alphabet.length() != ALPHABET_LENGTH)
            throw new IllegalArgumentException("alphabet has incorrect length (should be 64, not "
                    + alphabet.length() + ")");
        inverseAlphabet = new int[128];
        Arrays.fill(inverseAlphabet, -1);
        for (int i = 0; i < this.alphabet.length; i++) {
            final char ch = this.alphabet[i];
            if (ch >= 128)
                throw new IllegalArgumentException("invalid character in alphabet: " + ch);
            inverseAlphabet[ch] = i;
        }
    }

    int getSextet(final byte ch) {
        if (ch >= 128)
            return -1;
        return inverseAlphabet[ch];
    }

    boolean contains(final char ch) {
        if (ch >= 128)
            return false;
        return inverseAlphabet[ch] >= 0;
    }

    byte getChar(final int sextet) {
        return (byte) alphabet[sextet];
    }
}
