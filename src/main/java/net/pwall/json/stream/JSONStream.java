/*
 * @(#) JSONStream.kt
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

import net.pwall.json.JSONValue;
import net.pwall.pipeline.AbstractIntAcceptor;

/**
 * A stream class that consumes a sequence of characters and produces a {@link JSONValue} result.  This class may be
 * used to parse JSON on the fly, avoiding the need to allocate memory for the largest possible JSON input.
 *
 * <p>This class conforms to the conventions of the <a href="https://github.com/pwall567/pipelines">pipelines</a>
 * library, allowing it to be used as the consuming class at the end of a pipeline.</p>
 *
 * @author  Peter Wall
 */
public class JSONStream extends AbstractIntAcceptor<JSONValue> {

    private static final int BOM = 0xFEFF;

    private final JSONValueBuilder delegate;
    private boolean started;

    public JSONStream() {
        delegate = new JSONValueBuilder();
        started = false;
    }

    @Override
    public void acceptInt(int value) {
        if (!started) {
            started = true;
            if (value == BOM)
                return;
        }
        while (true) {
            if (delegate.acceptChar(value))
                break;
        }
    }

    @Override
    public JSONValue getResult() {
        return delegate.getResult();
    }

    @Override
    public void close() {
        delegate.close();
    }

}
