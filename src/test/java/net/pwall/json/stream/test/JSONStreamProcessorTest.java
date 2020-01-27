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

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDecimal;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONLong;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;
import net.pwall.json.stream.JSONStreamProcessor;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JSONStreamProcessorTest {

    @Test
    public void shouldParseNumberZero() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "0";
        proc.accept(json);
        assertSame(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseASimpleInteger() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "123";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(123), result);
    }

    @Test
    public void shouldParseANegativeInteger() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "-54321";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(-54321), result);
    }

    @Test
    public void shouldParseALongInteger() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "1234567812345678";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONLong);
        assertEquals(new JSONLong(1234567812345678L), result);
    }

    @Test
    public void shouldParseASimpleDouble() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "123.45678";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseANegativeDouble() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "-123.45678";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseASimpleString() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abcdef\"";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abcdef"), result);
    }

    @Test
    public void shouldParseAStringWithANewline() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abc\\ndef\"";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abc\ndef"), result);
    }

    @Test
    public void shouldParseAStringWithAUnicodeSequence() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "\"abc\\u000Adef\"";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abc\ndef"), result);
    }

    @Test
    public void shouldParseEmptyArray() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseArrayWithSingleZeroElement() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithTwoZeroElements() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,0]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithThreeZeroElementsIncludingExtraSpacing() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,  0   ,0]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO, JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithThreeStringElements() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[\"abcdef\",\"ghijkl\",\"mnopqr\"]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(new JSONString("abcdef"), new JSONString("ghijkl"), new JSONString("mnopqr")),
                result);
    }

    @Test
    public void shouldParseNestedArray() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[[12,34],[56,78]]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(new JSONArray(new JSONInteger(12), new JSONInteger(34)),
                new JSONArray(new JSONInteger(56), new JSONInteger(78))), result);
    }

    @Test
    public void shouldParseBooleanTrue() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "true";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertEquals(JSONBoolean.TRUE, result);
    }

    @Test
    public void shouldParseBooleanFalse() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "false";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertEquals(JSONBoolean.FALSE, result);
    }

    @Test
    public void shouldParseKeywordNull() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "null";
        proc.accept(json);
        assertNull(proc.getResult());
    }

    @Test
    public void shouldParseHeterogenousArray() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "[0,true,\"abc\",8.5,200,[]]";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONBoolean.TRUE, new JSONString("abc"), new JSONDecimal("8.5"),
                new JSONInteger(200), new JSONArray()), result);
    }

    @Test
    public void shouldParseSimpleObject() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "{\"field\":0}";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("field", JSONZero.ZERO);
        assertEquals(map, result);
    }

    @Test
    public void shouldParseObjectWithTwoFields() throws Exception {
        JSONStreamProcessor proc = new JSONStreamProcessor();
        String json = "{\"f1\":0,\"f2\":123}";
        proc.accept(json);
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("f1", JSONZero.ZERO);
        map.put("f2", new JSONInteger(123));
        assertEquals(map, result);
    }

}
