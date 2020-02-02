/*
 * @(#) JSONObjectBuilder.kt
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

import net.pwall.json.JSONException;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONValue;

public class JSONObjectBuilder implements JSONBuilder {

    private enum State { INITIAL, NAME, COLON, VALUE, COMMA, NEXT, COMPLETE }

    private final Map<String, JSONValue> entries;
    private State state;
    private JSONBuilder child;
    private String name;

    public JSONObjectBuilder() {
        state = State.INITIAL;
        entries = new LinkedHashMap<>();
        child = new JSONStringBuilder();
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("JSON object not complete");
        return new JSONObject(entries);
    }

    @Override
    public boolean acceptChar(int ch) {
        switch (state) {
            case INITIAL:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == '}')
                        state = State.COMPLETE;
                    else if (ch == '"')
                        state = State.NAME;
                    else
                        throw new JSONException("Illegal syntax in JSON object");
                }
                break;
            case NAME:
                child.acceptChar(ch); // JSONStringProcessor always returns true
                if (child.isComplete()) {
                    name = child.getResult().toString();
                    if (entries.containsKey(name))
                        throw new JSONException("Duplicate key in JSON object");
                    state = State.COLON;
                }
                break;
            case COLON:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == ':') {
                        child = new JSONValueBuilder();
                        state = State.VALUE;
                    }
                    else
                        throw new JSONException("Illegal syntax in JSON object");
                }
                break;
            case VALUE:
                boolean consumed = child.acceptChar(ch);
                if (child.isComplete()) {
                    entries.put(name, child.getResult());
                    state = State.COMMA;
                }
                if (consumed)
                    break;
                state = State.COMMA;
                // will drop through if character not consumed
            case COMMA:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == ',')
                        state = State.NEXT;
                    else if (ch == '}')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in JSON object");
                }
                break;
            case NEXT:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == '"') {
                        child = new JSONStringBuilder();
                        state = State.NAME;
                    }
                    else
                        throw new JSONException("Illegal syntax in JSON object");
                }
                break;
            case COMPLETE:
                JSONBuilder.checkWhitespace(ch);
        }
        return true;
    }

}
