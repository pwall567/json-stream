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

import java.util.List;

import net.pwall.json.JSON;
import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;
import net.pwall.util.pipeline.AbstractIntObjectPipeline;
import net.pwall.util.pipeline.Acceptor;

/**
 * A pipeline class that takes a stream of characters (Unicode code points) and outputs {@link JSONValue}s.
 */
public class JSONArrayPipeline extends AbstractIntObjectPipeline<JSONValue, List<JSONValue>> {

    private enum State { INITIAL, FIRST, ENTRY, COMMA, COMPLETE }

    private State state;
    private JSONProcessor child;

    public JSONArrayPipeline(Acceptor<JSONValue, List<JSONValue>> valueConsumer) {
        super(valueConsumer);
        state = State.INITIAL;
        child = new JSONStreamProcessor();
    }

    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public void acceptInt(int value) throws Exception {
        switch (state) {
            case INITIAL:
                if (!JSONProcessor.isWhitespace(value)) {
                    if (value == '[')
                        state = State.FIRST;
                    else
                        throw new JSONException("Pipeline must contain array");
                }
                break;
            case FIRST:
                if (!JSONProcessor.isWhitespace(value)) {
                    if (value == ']')
                        state = State.COMPLETE;
                    else {
                        state = State.ENTRY;
                        child.acceptChar((char)value);
                        // always true for first character
                    }
                }
                break;
            case ENTRY:
                boolean consumed = child.acceptChar((char)value);
                if (child.isComplete()) {
                    emit(child.getResult());
                    state = State.COMMA;
                }
                if (consumed)
                    break;
                // will drop through if character not consumed
            case COMMA:
                if (!JSONProcessor.isWhitespace(value)) {
                    if (value == ',') {
                        child = new JSONStreamProcessor();
                        state = State.ENTRY;
                    }
                    else if (value == ']')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in array");
                }
                break;
            case COMPLETE:
                if (!JSONProcessor.isWhitespace(value))
                    throw new JSONException(JSON.EXCESS_CHARS);
        }
    }

    @Override
    public void close() {
        if (!isComplete())
            throw new JSONException("Unexpected end of data in array");
    }

}
