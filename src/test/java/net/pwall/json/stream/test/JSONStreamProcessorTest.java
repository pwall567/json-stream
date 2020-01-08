/*
 * @(#) JSONStreamProcessorTest.kt
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

package net.pwall.json.stream.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;
import net.pwall.json.stream.JSONStreamProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JSONStreamProcessorTest {

    @Test
    public void shouldParseNumberZero() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "0";
        proc.accept(json);
        assertEquals(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseASimpleInteger() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "123";
        proc.accept(json);
        assertEquals(new JSONInteger(123), proc.getResult());
    }

    @Test
    public void shouldParseANegativeInteger() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "-54321";
        proc.accept(json);
        assertEquals(new JSONInteger(-54321), proc.getResult());
    }

    @Test
    public void shouldParseALongInteger() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "1234567812345678";
        proc.accept(json);
        assertEquals(new JSONLong(1234567812345678L), proc.getResult());
    }

    @Test
    public void shouldParseASimpleDouble() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "123.45678";
        proc.accept(json);
        assertEquals(new JSONDouble(123.45678), proc.getResult());
    }

    @Test
    public void shouldParseANegativeDouble() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "-123.45678";
        proc.accept(json);
        assertEquals(new JSONDouble(-123.45678), proc.getResult());
    }

    @Test
    public void shouldParseASimpleString() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abcdef\"";
        proc.accept(json);
        assertEquals(new JSONString("abcdef"), proc.getResult());
    }

    @Test
    public void shouldParseAStringWithANewline() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abc\\ndef\"";
        proc.accept(json);
        assertEquals(new JSONString("abc\ndef"), proc.getResult());
    }

    @Test
    public void shouldParseAStringWithAUnicodeSequence() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abc\\u000Adef\"";
        proc.accept(json);
        assertEquals(new JSONString("abc\ndef"), proc.getResult());
    }

    @Test
    public void shouldParseEmptyArray() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[]";
        proc.accept(json);
        assertEquals(new JSONArray(), proc.getResult());
    }

    @Test
    public void shouldParseArrayWithSingleZeroElement() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0]";
        proc.accept(json);
        assertEquals(new JSONArray(JSONZero.ZERO), proc.getResult());
    }

    @Test
    public void shouldParseArrayWithTwoZeroElements() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,0]";
        proc.accept(json);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO), proc.getResult());
    }

    @Test
    public void shouldParseArrayWithThreeZeroElementsIncludingExtraSpacing() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,  0   ,0]";
        proc.accept(json);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO, JSONZero.ZERO), proc.getResult());
    }

    @Test
    public void shouldParseArrayWithThreeStringElements() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[\"abcdef\",\"ghijkl\",\"mnopqr\"]";
        proc.accept(json);
        assertEquals(new JSONArray(new JSONString("abcdef"), new JSONString("ghijkl"), new JSONString("mnopqr")),
                proc.getResult());
    }

    @Test
    public void shouldParseNestedArray() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[[12,34],[56,78]]";
        proc.accept(json);
        assertEquals(new JSONArray(new JSONArray(new JSONInteger(12), new JSONInteger(34)),
                new JSONArray(new JSONInteger(56), new JSONInteger(78))), proc.getResult());
    }

    @Test
    public void shouldParseBooleanTrue() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "true";
        proc.accept(json);
        assertEquals(JSONBoolean.TRUE, proc.getResult());
    }

    @Test
    public void shouldParseBooleanFalse() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "false";
        proc.accept(json);
        assertEquals(JSONBoolean.FALSE, proc.getResult());
    }

    @Test
    public void shouldParseKeywordNull() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "null";
        proc.accept(json);
        assertNull(proc.getResult());
    }

    @Test
    public void shouldParseHeterogenousArray() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,true,\"abc\",8.5,200,[]]";
        proc.accept(json);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONBoolean.TRUE, new JSONString("abc"), new JSONDouble(8.5),
                new JSONInteger(200), new JSONArray()), proc.getResult());
    }

    @Test
    public void shouldParseSimpleObject() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "{\"field\":0}";
        proc.accept(json);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("field", JSONZero.ZERO);
        assertEquals(map, proc.getResult());
    }

    @Test
    public void shouldParseObjectWithTwoFields() {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "{\"f1\":0,\"f2\":123}";
        proc.accept(json);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("f1", JSONZero.ZERO);
        map.put("f2", new JSONInteger(123));
        assertEquals(map, proc.getResult());
    }

}