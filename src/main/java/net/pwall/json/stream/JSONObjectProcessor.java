/*
 * @(#) JSONObjectProcessor.kt
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

import java.util.LinkedHashMap;
import java.util.Map;

import net.pwall.json.JSON;
import net.pwall.json.JSONException;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONValue;

public class JSONObjectProcessor implements JSONProcessor {

    private enum State { INITIAL, NAME, COLON, VALUE, COMMA, NEXT, COMPLETE }

    private State state;
    private Map<String, JSONValue> entries;
    private JSONProcessor child;
    private String name;

    public JSONObjectProcessor() {
        state = State.INITIAL;
        entries = new LinkedHashMap<>();
        child = new JSONStringProcessor();
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (isComplete())
            return new JSONObject(entries);
        throw new JSONException("Object not complete");
    }

    @Override
    public boolean acceptChar(char ch) {
        boolean consumed = true;
        switch (state) {
            case INITIAL:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == '}')
                        state = State.COMPLETE;
                    else if (ch == '"')
                        state = State.NAME;
                    else
                        throw new JSONException("Illegal syntax in object");
                }
                break;
            case NAME:
                child.acceptChar(ch); // JSONStringProcessor always returns true
                if (child.isComplete()) {
                    name = child.getResult().toString();
                    state = State.COLON;
                }
                break;
            case COLON:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ':') {
                        child = new JSONStreamProcessor();
                        state = State.VALUE;
                    }
                    else
                        throw new JSONException("Illegal syntax in object");
                }
                break;
            case VALUE:
                consumed = child.acceptChar(ch);
                if (child.isComplete()) {
                    entries.put(name, child.getResult());
                    child = JSONErrorProcessor.INSTANCE;
                    state = State.COMMA;
                }
                break;
            case COMMA:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ',')
                        state = State.NEXT;
                    else if (ch == '}')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in object");
                }
                break;
            case NEXT:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == '"') {
                        child = new JSONStringProcessor();
                        state = State.NAME;
                    }
                    else
                        throw new JSONException("Illegal syntax in object");
                }
                break;
            case COMPLETE:
                if (!JSONProcessor.isWhitespace(ch))
                    throw new JSONException(JSON.EXCESS_CHARS);
        }
        return consumed;
    }

    @Override
    public void close() {
        if (!isComplete())
            throw new JSONException("Unexpected end of data in object");
    }

}
