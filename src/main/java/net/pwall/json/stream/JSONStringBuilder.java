/*
 * @(#) JSONStringBuilder.kt
 *
 * json-stream JSON Streaming library for Java
 * Copyright (c) 2020 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.stream;

import net.pwall.json.JSONException;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;

public class JSONStringBuilder implements JSONBuilder {

    private enum State { NORMAL, BACKSLASH, UNICODE1, UNICODE2, UNICODE3, UNICODE4, COMPLETE }

    private final StringBuilder sb;
    private State state;
    private int unicode;

    public JSONStringBuilder() {
        state = State.NORMAL;
        sb = new StringBuilder();
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("Unterminated JSON string");
        return new JSONString(sb);
    }

    @Override
    public boolean acceptChar(int ch) {
        switch (state) {
            case NORMAL:
                acceptNormal(ch);
                break;
            case BACKSLASH:
                acceptBackslash(ch);
                break;
            case UNICODE1:
                acceptUnicode(ch, State.UNICODE2);
                break;
            case UNICODE2:
                acceptUnicode(ch, State.UNICODE3);
                break;
            case UNICODE3:
                acceptUnicode(ch, State.UNICODE4);
                break;
            case UNICODE4:
                acceptUnicode(ch, State.NORMAL);
                sb.append((char)unicode);
                break;
            case COMPLETE:
                JSONBuilder.checkWhitespace(ch);
        }
        return true;
    }

    private void acceptNormal(int ch) {
        if (ch == '"')
            state = State.COMPLETE;
        else if (ch == '\\')
            state = State.BACKSLASH;
        else if (ch <= 0x1F)
            throw new JSONException("Illegal character in JSON string");
        else if (Character.isBmpCodePoint(ch))
            sb.append((char)ch);
        else {
            sb.append(Character.highSurrogate(ch));
            sb.append(Character.lowSurrogate(ch));
        }
    }

    private void acceptBackslash(int ch) {
        if (ch == '"' || ch == '\\' || ch == '/')
            store((char)ch);
        else if (ch == 'b')
            store('\b');
        else if (ch == 'f')
            store('\f');
        else if (ch == 'n')
            store('\n');
        else if (ch == 'r')
            store('\r');
        else if (ch == 't')
            store('\t');
        else if (ch == 'u')
            state = State.UNICODE1;
        else
            throw new JSONException("Illegal escape sequence in JSON string");
    }

    private void store(char ch) {
        sb.append(ch);
        state = State.NORMAL;
    }

    private void acceptUnicode(int ch, State nextState) {
        int digit;
        if (ch >= '0' && ch <= '9')
            digit = ch - '0';
        else if (ch >= 'A' && ch <= 'F')
            digit = ch - 'A' + 10;
        else if (ch >= 'a' && ch <= 'f')
            digit = ch - 'a' + 10;
        else
            throw new JSONException("Illegal Unicode sequence in JSON string");
        unicode = (unicode << 4) | digit;
        state = nextState;
    }

}
