/*
 * @(#) JSONNumberProcessor.kt
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

import net.pwall.json.JSON;
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONException;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;

public class JSONNumberProcessor implements JSONProcessor {

    private enum State { MINUS_SEEN, ZERO_SEEN, INTEGER, DOT_SEEN, FRACTION, E_SEEN, EXPONENT, COMPLETE }

    private State state;
    private StringBuilder number;
    private boolean floating;
    private boolean consumed;

    public JSONNumberProcessor(char initialChar) {
        if (initialChar == '-')
            state = State.MINUS_SEEN;
        else if (initialChar == '0')
            state = State.ZERO_SEEN;
        else if (initialChar >= '1' && initialChar <= '9')
            state = State.INTEGER;
        else
            throw new JSONException(JSON.ILLEGAL_NUMBER);
        number = new StringBuilder();
        number.append(initialChar);
        floating = false;
        consumed = true;
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (isComplete()) {
            if (number.length() == 1 && number.charAt(0) == '0')
                return JSONZero.ZERO;
            if (floating)
                return new JSONDouble(Double.parseDouble(number.toString()));
            long longValue = Long.parseLong(number.toString());
            int intValue = (int)longValue;
            return (long)intValue == longValue ? new JSONInteger(intValue) : new JSONLong(longValue);
        }
        throw new JSONException("Number not complete");
    }

    @Override
    public boolean acceptChar(char ch) {
        consumed = true;
        switch (state) {
            case MINUS_SEEN:
                if (ch == '0')
                    state = State.ZERO_SEEN;
                else if (ch >= '1' && ch <= '9')
                    state = State.INTEGER;
                else
                    throw new JSONException(JSON.ILLEGAL_NUMBER);
                break;
            case ZERO_SEEN:
                if (ch == '.')
                    setDotSeen();
                else if (ch == 'e' || ch == 'E')
                    setESeen();
                else
                    endOfNumber();
                break;
            case INTEGER:
                if (!(ch >= '0' && ch <= '9')) {
                    if (ch == '.')
                        setDotSeen();
                    else if (ch == 'e' || ch == 'E')
                        setESeen();
                    else
                        endOfNumber();
                }
                break;
            case DOT_SEEN:
                if (ch >= '0' && ch <= '9')
                    state = State.FRACTION;
                else
                    throw new JSONException(JSON.ILLEGAL_NUMBER);
                break;
            case FRACTION:
                if (!(ch >= '0' && ch <= '9')) {
                    if (ch == 'e' || ch == 'E')
                        setESeen();
                    else
                        endOfNumber();
                }
                break;
            case E_SEEN:
                if (ch >= '0' && ch <= '9')
                    state = State.EXPONENT;
                else
                    throw new JSONException(JSON.ILLEGAL_NUMBER);
                break;
            case EXPONENT:
                if (!(ch >= '0' && ch <= '9'))
                    endOfNumber();
                break;
            case COMPLETE:
                if (!JSONProcessor.isWhitespace(ch))
                    throw new JSONException(JSON.EXCESS_CHARS);
        }
        if (consumed)
            number.append(ch);
        return consumed;
    }

    private void setDotSeen() {
        state = State.DOT_SEEN;
        floating = true;
    }

    private void setESeen() {
        state = State.E_SEEN;
        floating = true;
    }

    private void endOfNumber() {
        state = State.COMPLETE;
        consumed = false;
    }

    @Override
    public void close() {
        switch (state) {
            case MINUS_SEEN:
            case DOT_SEEN:
            case E_SEEN:
                throw new JSONException(JSON.ILLEGAL_NUMBER);
            case ZERO_SEEN:
            case INTEGER:
            case FRACTION:
            case EXPONENT:
                state = State.COMPLETE;
                break;
            case COMPLETE:
        }
    }

}
