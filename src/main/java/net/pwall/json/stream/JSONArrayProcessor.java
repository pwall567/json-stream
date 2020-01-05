/*
 * @(#) JSONArrayProcessor.kt
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

import java.util.ArrayList;
import java.util.List;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;

public class JSONArrayProcessor implements JSONProcessor {

    private enum State { INITIAL, ENTRY, COMMA, CLOSED }

    private State state;
    private List<JSONValue> entries;
    private JSONProcessor child;


    public JSONArrayProcessor() {
        state = State.INITIAL;
        entries = new ArrayList<>();
        child = new JSONMainProcessor();
    }

    @Override
    public boolean isClosed() {
        return state == State.CLOSED;
    }

    @Override
    public JSONValue getResult() {
        if (isClosed())
            return new JSONArray(entries);
        throw new JSONException("Array not complete");
    }

    @Override
    public boolean accept(char ch) {
        boolean consumed = true;
        switch (state) {
            case INITIAL:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ']')
                        state = State.CLOSED;
                    else {
                        state = State.ENTRY;
                        child.accept(ch); // always true for first character
                    }
                }
                break;
            case ENTRY:
                consumed = child.accept(ch);
                if (child.isClosed()) {
                    entries.add(child.getResult());
                    child = JSONErrorProcessor.INSTANCE;
                    state = State.COMMA;
                }
                break;
            case COMMA:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ',') {
                        child = new JSONMainProcessor();
                        state = State.ENTRY;
                    }
                    else if (ch == ']')
                        state = State.CLOSED;
                    else
                        throw new JSONException("Illegal syntax in array");
                }
                break;
            case CLOSED:
                if (!JSONProcessor.isWhitespace(ch))
                    throw new JSONException("Processor closed");
        }
        return consumed;
    }

    @Override
    public void acceptEnd() {
        if (!isClosed())
            throw new JSONException("Unexpected end of data in array");
    }

}
