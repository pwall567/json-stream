/*
 * @(#) JSONBuilder.kt
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

/**
 * Interface implemented by all {@code JSONXxxxBuilder} classes, which consume a stream of characters representing a
 * JSON value and return the parsed value.
 *
 * @author  Peter Wall
 */
public interface JSONBuilder {

    /**
     * Test whether the JSON being parsed by this builder is complete.
     *
     * @return  {@code true} if the JSON is complete
     */
    boolean isComplete();

    /**
     * Get the {@link JSONValue} result.
     *
     * @return  the result
     * @throws  JSONException if the JSON is not complete
     */
    JSONValue getResult();

    /**
     * Accept a character as part of the JSON, and return {@code true} if the character has been consumed.  The
     * character is in the form of a Unicode code point; since all of the syntactic elements of JSON fall within the
     * ASCII subset, the only place this is relevant is inside strings.
     *
     * @param   ch      the character
     * @return  {@code true} if the character has been consumed
     * @throws  JSONException if the character is not valid
     */
    boolean acceptChar(int ch);

    /**
     * Close the builder.  The default implementation throws an exception if the JSON is not complete.
     *
     * @throws  JSONException if the JSON is not complete
     */
    default void close() {
        if (!isComplete())
            throw new JSONException("Unexpected end of data");
    }

    /**
     * Test a character for whitespace.
     *
     * @param   ch      the character
     * @return  {@code true} if the character is whitespace
     */
    static boolean isWhitespace(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    static void checkWhitespace(int ch) {
        if (!isWhitespace(ch))
            throw new JSONException("Unexpected characters at end of JSON");
    }

}
