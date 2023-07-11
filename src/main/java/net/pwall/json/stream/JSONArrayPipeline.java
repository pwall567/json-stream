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

import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;
import net.pwall.pipeline.AbstractIntObjectPipeline;
import net.pwall.pipeline.Acceptor;

/**
 * A pipeline class that takes a stream of characters (Unicode code points) and outputs {@link JSONValue}s.
 *
 * @author  Peter Wall
 * @param   <R>     the pipeline result type (may be {@link Void} if all results are processed on the fly)
 */
public class JSONArrayPipeline<R> extends AbstractIntObjectPipeline<JSONValue, R> {

    private enum State { INITIAL, FIRST, ENTRY, COMMA, COMPLETE }

    private State state;
    private JSONBuilder child;

    public JSONArrayPipeline(Acceptor<JSONValue, R> valueConsumer) {
        super(valueConsumer);
        state = State.INITIAL;
        child = new JSONValueBuilder();
    }

    public boolean isComplete() {
        return state == State.COMPLETE;
    }

    @Override
    public void acceptInt(int value) {
        switch (state) {
            case INITIAL:
                if (!JSONBuilder.isWhitespace(value)) {
                    if (value == '[')
                        state = State.FIRST;
                    else
                        throw new JSONException("Pipeline must contain array");
                }
                break;
            case FIRST:
                if (!JSONBuilder.isWhitespace(value)) {
                    if (value == ']')
                        state = State.COMPLETE;
                    else {
                        state = State.ENTRY;
                        child.acceptChar(value);
                        // always true for first character
                    }
                }
                break;
            case ENTRY:
                boolean consumed = child.acceptChar(value);
                if (child.isComplete()) {
                    emit(child.getResult());
                    state = State.COMMA;
                }
                if (consumed)
                    break;
                // will drop through if character not consumed
            case COMMA:
                if (!JSONBuilder.isWhitespace(value)) {
                    if (value == ',') {
                        child = new JSONValueBuilder();
                        state = State.ENTRY;
                    }
                    else if (value == ']')
                        state = State.COMPLETE;
                    else
                        throw new JSONException("Illegal syntax in array");
                }
                break;
            case COMPLETE:
                JSONBuilder.checkWhitespace(value);
        }
    }

    @Override
    public void close() {
        if (!isComplete())
            throw new JSONException("Unexpected end of data in JSON array");
    }

}
