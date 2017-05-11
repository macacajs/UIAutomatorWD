package com.macaca.android.testing.server.charsetUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class UTF7StyleCharsetEncoder extends CharsetEncoder {
    private static final float AVG_BYTES_PER_CHAR = 1.5f;
    private static final float MAX_BYTES_PER_CHAR = 5.0f;
    private final UTF7StyleCharset cs;
    private final Base64Util base64;
    private final byte shift;
    private final byte unshift;
    private final boolean strict;
    private boolean base64mode;
    private int bitsToOutput;
    private int sextet;

    UTF7StyleCharsetEncoder(UTF7StyleCharset cs, Base64Util base64, boolean strict) {
        super(cs, AVG_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
        this.cs = cs;
        this.base64 = base64;
        this.strict = strict;
        this.shift = cs.shift();
        this.unshift = cs.unshift();
    }

    protected void implReset() {
        base64mode = false;
        sextet = 0;
        bitsToOutput = 0;
    }

    protected CoderResult implFlush(ByteBuffer out) {
        if (base64mode) {
            if (out.remaining() < 2)
                return CoderResult.OVERFLOW;
            if (bitsToOutput != 0)
                out.put(base64.getChar(sextet));
            out.put(unshift);
        }
        return CoderResult.UNDERFLOW;
    }

    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.hasRemaining()) {
            if (out.remaining() < 4)
                return CoderResult.OVERFLOW;
            char ch = in.get();
            if (cs.canEncodeDirectly(ch)) {
                unshift(out, ch);
                out.put((byte) ch);
            } else if (!base64mode && ch == shift) {
                out.put(shift);
                out.put(unshift);
            } else
                encodeBase64(ch, out);
        }
        return CoderResult.UNDERFLOW;
    }

    private void unshift(ByteBuffer out, char ch) {
        if (!base64mode)
            return;
        if (bitsToOutput != 0)
            out.put(base64.getChar(sextet));
        if (base64.contains(ch) || ch == unshift || strict)
            out.put(unshift);
        base64mode = false;
        sextet = 0;
        bitsToOutput = 0;
    }

    private void encodeBase64(char ch, ByteBuffer out) {
        if (!base64mode)
            out.put(shift);
        base64mode = true;
        bitsToOutput += 16;
        while (bitsToOutput >= 6) {
            bitsToOutput -= 6;
            sextet += (ch >> bitsToOutput);
            sextet &= 0x3F;
            out.put(base64.getChar(sextet));
            sextet = 0;
        }
        sextet = (ch << (6 - bitsToOutput)) & 0x3F;
    }
}
