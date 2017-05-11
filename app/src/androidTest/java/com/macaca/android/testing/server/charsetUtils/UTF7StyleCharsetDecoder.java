package com.macaca.android.testing.server.charsetUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

class UTF7StyleCharsetDecoder extends CharsetDecoder {
    private final Base64Util base64;
    private final byte shift;
    private final byte unshift;
    private final boolean strict;
    private boolean base64mode;
    private int bitsRead;
    private int tempChar;
    private boolean justShifted;
    private boolean justUnshifted;

    UTF7StyleCharsetDecoder(UTF7StyleCharset cs, Base64Util base64, boolean strict) {
        super(cs, 0.6f, 1.0f);
        this.base64 = base64;
        this.strict = strict;
        this.shift = cs.shift();
        this.unshift = cs.unshift();
    }

    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        while (in.hasRemaining()) {
            byte b = in.get();
            if (base64mode) {
                if (b == unshift) {
                    if (base64bitsWaiting())
                        return malformed(in);
                    if (justShifted) {
                        if (!out.hasRemaining())
                            return overflow(in);
                        out.put((char) shift);
                    } else
                        justUnshifted = true;
                    setUnshifted();
                } else {
                    if (!out.hasRemaining())
                        return overflow(in);
                    CoderResult result = handleBase64(in, out, b);
                    if (result != null)
                        return result;
                }
                justShifted = false;
            } else {
                if (b == shift) {
                    base64mode = true;
                    if (justUnshifted && strict)
                        return malformed(in);
                    justShifted = true;
                    continue;
                }
                if (!out.hasRemaining())
                    return overflow(in);
                out.put((char) b);
                justUnshifted = false;
            }
        }
        return CoderResult.UNDERFLOW;
    }

    private CoderResult overflow(ByteBuffer in) {
        in.position(in.position() - 1);
        return CoderResult.OVERFLOW;
    }

    private CoderResult handleBase64(ByteBuffer in, CharBuffer out, byte lastRead) {
        CoderResult result = null;
        int sextet = base64.getSextet(lastRead);
        if (sextet >= 0) {
            bitsRead += 6;
            if (bitsRead < 16) {
                tempChar += sextet << (16 - bitsRead);
            } else {
                bitsRead -= 16;
                tempChar += sextet >> (bitsRead);
                out.put((char) tempChar);
                tempChar = (sextet << (16 - bitsRead)) & 0xFFFF;
            }
        } else {
            if (strict)
                return malformed(in);
            out.put((char) lastRead);
            if (base64bitsWaiting())
                result = malformed(in);
            setUnshifted();
        }
        return result;
    }

    protected CoderResult implFlush(CharBuffer out) {
        if ((base64mode && strict) || base64bitsWaiting())
            return CoderResult.malformedForLength(1);
        return CoderResult.UNDERFLOW;
    }

    protected void implReset() {
        setUnshifted();
        justUnshifted = false;
    }

    private CoderResult malformed(ByteBuffer in) {
        in.position(in.position() - 1);
        return CoderResult.malformedForLength(1);
    }

    private boolean base64bitsWaiting() {
        return tempChar != 0 || bitsRead >= 6;
    }

    private void setUnshifted() {
        base64mode = false;
        bitsRead = 0;
        tempChar = 0;
    }
}
