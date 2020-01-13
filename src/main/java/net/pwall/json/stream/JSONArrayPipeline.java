/*
 * @(#) JSONArrayPipeline.kt
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

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import net.pwall.json.JSON;
import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;

public class JSONArrayPipeline implements IntConsumer {

    private enum State { INITIAL, FIRST, ENTRY, COMMA, COMPLETE }

    private State state;
    private JSONProcessor child;
    public Consumer<JSONValue> valueConsumer;

    public JSONArrayPipeline(Consumer<JSONValue> valueConsumer) {
        this.valueConsumer = valueConsumer;
        state = State.INITIAL;
        child = new JSONStreamProcessor();
    }

    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public void accept(int value) {
        if (value == -1)
            acceptEnd();
        else {
            while (true) {
                if (acceptChar((char)value))
                    break;
            }
        }
    }

    public boolean acceptChar(char ch) {
        boolean consumed = true;
        switch (state) {
            case INITIAL:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == '[')
                        state = State.FIRST;
                    else
                        throw new JSONException("Pipeline must contain array");
                }
                break;
            case FIRST:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ']')
                        state = State.COMPLETE;
                    else {
                        state = State.ENTRY;
                        child.acceptChar(ch); // always true for first character
                    }
                }
                break;
            case ENTRY:
                consumed = child.acceptChar(ch);
                if (child.isComplete()) {
                    valueConsumer.accept(child.getResult());
                    child = JSONErrorProcessor.INSTANCE;
                    state = State.COMMA;
                }
                break;
            case COMMA:
                if (!JSONProcessor.isWhitespace(ch)) {
                    if (ch == ',') {
                        child = new JSONStreamProcessor();
                        state = State.ENTRY;
                    }
                    else if (ch == ']')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in array");
                }
                break;
            case COMPLETE:
                if (!JSONProcessor.isWhitespace(ch))
                    throw new JSONException(JSON.EXCESS_CHARS);
        }
        return consumed;
    }

    public void acceptEnd() {
        if (!isComplete())
            throw new JSONException("Unexpected end of data in array");
    }

}
