/*
 * @(#) JSONStreamProcessor.kt
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

import java.util.function.IntConsumer;

import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;

public class JSONStreamProcessor implements JSONProcessor, IntConsumer {

    private enum State { INITIAL, CHILD, COMPLETE }

    private State state;
    private JSONProcessor child;

    public JSONStreamProcessor() {
        state = State.INITIAL;
        child = JSONErrorProcessor.INSTANCE;
    }

    @Override
    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public JSONValue getResult() {
        if (state == State.COMPLETE)
            return child.getResult();
        throw new JSONException("JSON not complete");
    }

    @Override
    public void accept(int value) {
        if (value == -1)
            close();
        else {
            while (true) {
                if (acceptChar((char)value))
                    break;
            }
        }
    }

    public void accept(String str) {
        for (int i = 0, n = str.length(); i < n; i++)
            accept(str.charAt(i));
        close();
    }

    @Override
    public boolean acceptChar(char ch) {
        boolean consumed = true;
        switch (state) {
            case INITIAL:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == '{')
                        startChild(new JSONObjectProcessor());
                    else if (ch == '[')
                        startChild(new JSONArrayProcessor());
                    else if (ch == '"')
                        startChild(new JSONStringProcessor());
                    else if (ch == '-' || ch >= '0' && ch <= '9')
                        startChild(new JSONNumberProcessor(ch));
                    else if (ch == 't')
                        startChild(new JSONKeywordProcessor("true", JSONBoolean.TRUE));
                    else if (ch == 'f')
                        startChild(new JSONKeywordProcessor("false", JSONBoolean.FALSE));
                    else if (ch == 'n')
                        startChild(new JSONKeywordProcessor("null", null));
                    else
                        throw new JSONException("Illegal character in JSON");
                }
                break;
            case CHILD:
                consumed = child.acceptChar(ch);
                if (child.isComplete())
                    state = State.COMPLETE;
                break;
            case COMPLETE:
                if (!JSONProcessor.isWhitespace(ch))
                    throw new JSONException("Illegal character at end of JSON");
        }
        return consumed;
    }

    private void startChild(JSONProcessor processor) {
        child = processor;
        state = State.CHILD;
    }

    @Override
    public void close() {
        switch (state) {
            case CHILD:
                if (!child.isComplete()) {
                    child.close();
                    if (child.isComplete())
                        state = State.COMPLETE;
                }
                break;
            case COMPLETE:
                break;
            default:
                state = State.COMPLETE;
                throw new JSONException("JSON not complete");
        }

    }

}
