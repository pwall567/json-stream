/*
 * @(#) JSONArrayBuilder.kt
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

public class JSONArrayBuilder implements JSONBuilder {

    private enum State { INITIAL, ENTRY, COMMA, COMPLETE }

    private final List<JSONValue> entries;
    private State state;
    private JSONBuilder child;


    public JSONArrayBuilder() {
        state = State.INITIAL;
        entries = new ArrayList<>();
        child = new JSONValueBuilder();
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("Array not complete");
        return new JSONArray(entries);
    }

    @Override
    public boolean acceptChar(int ch) {
        switch (state) {
            case INITIAL:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == ']')
                        state = State.COMPLETE;
                    else {
                        state = State.ENTRY;
                        child.acceptChar(ch); // always true for first character
                    }
                }
                break;
            case ENTRY:
                boolean consumed = child.acceptChar(ch);
                if (child.isComplete()) {
                    entries.add(child.getResult());
                    state = State.COMMA;
                }
                if (consumed)
                    break;
                state = State.COMMA;
                // will drop through if character not consumed
            case COMMA:
                if (!JSONBuilder.isWhitespace(ch)) {
                    if (ch == ',') {
                        child = new JSONValueBuilder();
                        state = State.ENTRY;
                    }
                    else if (ch == ']')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in JSON array");
                }
                break;
            case COMPLETE:
                JSONBuilder.checkWhitespace(ch);
        }
        return true;
    }

}
