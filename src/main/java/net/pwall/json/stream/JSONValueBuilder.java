/*
 * @(#) JSONValueBuilder.kt
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

import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONException;
import net.pwall.json.JSONValue;

public class JSONValueBuilder implements JSONBuilder {

    private JSONBuilder delegate;

    public JSONValueBuilder() {
        delegate = null;
    }

    @Override
    public boolean isComplete() {
        return delegate != null && delegate.isComplete();
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("JSON not complete");
        return delegate.getResult();
    }

    @Override
    public boolean acceptChar(int ch) {
        if (delegate == null) {
            if (!JSONBuilder.isWhitespace(ch)) {
                if (ch == '{')
                    delegate = new JSONObjectBuilder();
                else if (ch == '[')
                    delegate = new JSONArrayBuilder();
                else if (ch == '"')
                    delegate = new JSONStringBuilder();
                else if (ch == '-' || ch >= '0' && ch <= '9')
                    delegate = new JSONNumberBuilder((char)ch);
                else if (ch == 't')
                    delegate = new JSONKeywordBuilder("true", JSONBoolean.TRUE);
                else if (ch == 'f')
                    delegate = new JSONKeywordBuilder("false", JSONBoolean.FALSE);
                else if (ch == 'n')
                    delegate = new JSONKeywordBuilder("null", null);
                else
                    throw new JSONException("Illegal syntax in JSON");
            }
            return true;
        }
        if (delegate.isComplete()) {
            JSONBuilder.checkWhitespace(ch);
            return true;
        }
        return delegate.acceptChar(ch);
    }

    @Override
    public void close() {
        if (delegate != null) {
            if (!delegate.isComplete())
                delegate.close();
        }
        else
            throw new JSONException("JSON value not complete");
    }

}
