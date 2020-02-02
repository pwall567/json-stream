/*
 * @(#) JSONNumberBuilder.kt
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

import net.pwall.json.JSONDecimal;
import net.pwall.json.JSONException;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;

public class JSONNumberBuilder implements JSONBuilder {

    private enum State { MINUS_SEEN, ZERO_SEEN, INTEGER, DOT_SEEN, FRACTION, E_SEEN, E_SIGN_SEEN, EXPONENT, COMPLETE }

    private final StringBuilder number;
    private State state;
    private boolean floating;

    public JSONNumberBuilder(char initialChar) {
        if (initialChar == '-')
            state = State.MINUS_SEEN;
        else if (initialChar == '0')
            state = State.ZERO_SEEN;
        else if (initialChar >= '1' && initialChar <= '9')
            state = State.INTEGER;
        else
            throw new JSONException("Illegal JSON number");
        number = new StringBuilder();
        number.append(initialChar);
        floating = false;
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("Number not complete");
        if (number.length() == 1 && number.charAt(0) == '0')
            return JSONZero.ZERO;
        if (floating)
            return new JSONDecimal(number.toString());
        long longValue = Long.parseLong(number.toString());
        int intValue = (int)longValue;
        return (long)intValue == longValue ? new JSONInteger(intValue) : new JSONLong(longValue);
    }

    @Override
    public boolean acceptChar(int ch) {
        switch (state) {
            case MINUS_SEEN:
                if (ch == '0')
                    state = State.ZERO_SEEN;
                else if (ch >= '1' && ch <= '9')
                    state = State.INTEGER;
                else
                    throw new JSONException("Illegal JSON number");
                break;
            case ZERO_SEEN:
                if (ch == '.')
                    state = State.DOT_SEEN;
                else if (ch == 'e' || ch == 'E')
                    state = State.E_SEEN;
                else
                    state = State.COMPLETE;
                break;
            case INTEGER:
                if (!(ch >= '0' && ch <= '9')) {
                    if (ch == '.')
                        state = State.DOT_SEEN;
                    else if (ch == 'e' || ch == 'E')
                        state = State.E_SEEN;
                    else
                        state = State.COMPLETE;
                }
                break;
            case DOT_SEEN:
                floating = true;
                if (ch >= '0' && ch <= '9')
                    state = State.FRACTION;
                else
                    throw new JSONException("Illegal JSON number");
                break;
            case FRACTION:
                if (!(ch >= '0' && ch <= '9')) {
                    if (ch == 'e' || ch == 'E')
                        state = State.E_SEEN;
                    else
                        state = State.COMPLETE;
                }
                break;
            case E_SEEN:
                floating = true;
                if (ch == '-' || ch == '+')
                    state = State.E_SIGN_SEEN;
                else if (ch >= '0' && ch <= '9')
                    state = State.EXPONENT;
                else
                    throw new JSONException("Illegal JSON number");
                break;
            case E_SIGN_SEEN:
                if (ch >= '0' && ch <= '9')
                    state = State.EXPONENT;
                else
                    throw new JSONException("Illegal JSON number");
                break;
            case EXPONENT:
                if (!(ch >= '0' && ch <= '9'))
                    state = State.COMPLETE;
                break;
            case COMPLETE:
                JSONBuilder.checkWhitespace(ch);
                return true;
        }
        if (isComplete())
            return false;
        number.append((char)ch);
        return true;
    }

    @Override
    public void close() {
        switch (state) {
            case MINUS_SEEN:
            case DOT_SEEN:
            case E_SEEN:
            case E_SIGN_SEEN:
                throw new JSONException("Illegal JSON number");
            case ZERO_SEEN:
            case INTEGER:
            case FRACTION:
            case EXPONENT:
                state = State.COMPLETE;
            case COMPLETE:
        }
    }

}
