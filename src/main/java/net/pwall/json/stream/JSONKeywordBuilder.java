/*
 * @(#) JSONKeywordBuilder.kt
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

public class JSONKeywordBuilder implements JSONBuilder {

    private final String keyword;
    private final JSONValue value;
    private int offset;

    public JSONKeywordBuilder(String keyword, JSONValue value) {
        this.keyword = keyword;
        this.value = value;
        offset = 1;
    }

    @Override
    public boolean isComplete() {
        return offset == keyword.length();
    }

    @Override
    public JSONValue getResult() {
        if (!isComplete())
            throw new JSONException("Keyword not complete");
        return value;
    }

    @Override
    public boolean acceptChar(int ch) {
        if (isComplete())
            throw new JSONException("Unexpected characters at end of JSON keyword");
        if (ch != keyword.charAt(offset))
            throw new JSONException("Illegal character in JSON keyword");
        offset++;
        return true;
    }

}
