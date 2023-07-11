/*
 * @(#) JSONValueBuilderTest.kt
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
import net.pwall.json.stream.JSONStream;
import net.pwall.pipeline.IntPipeline;
import net.pwall.pipeline.codec.UTF8_CodePoint;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONValueBuilderTest {

    @Test
    public void shouldParseNumberZero() {
        JSONStream proc = new JSONStream();
        String json = "0";
        proc.accept(json);
        proc.close();
        assertSame(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseNumberZeroWithLeadingSpace() {
        JSONStream proc = new JSONStream();
        String json = " 0";
        proc.accept(json);
        proc.close();
        assertSame(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseNumberZeroWithTrailingSpace() {
        JSONStream proc = new JSONStream();
        String json = "0 ";
        proc.accept(json);
        proc.close();
        assertSame(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseNumberZeroWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "    0  ";
        proc.accept(json);
        proc.close();
        assertSame(JSONZero.ZERO, proc.getResult());
    }

    @Test
    public void shouldParseASimpleInteger() {
        JSONStream proc = new JSONStream();
        String json = "123";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(123), result);
    }

    @Test
    public void shouldParseASimpleIntegerWithLeadingSpace() {
        JSONStream proc = new JSONStream();
        String json = " 4";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(4), result);
    }

    @Test
    public void shouldParseASimpleIntegerWithTrailingSpace() {
        JSONStream proc = new JSONStream();
        String json = "8888 ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(8888), result);
    }

    @Test
    public void shouldParseASimpleIntegerWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "  100001                 ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(100001), result);
    }

    @Test
    public void shouldParseANegativeInteger() {
        JSONStream proc = new JSONStream();
        String json = "-54321";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(-54321), result);
    }

    @Test
    public void shouldParseANegativeIntegerWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "         -876  ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONInteger);
        assertEquals(new JSONInteger(-876), result);
    }

    @Test
    public void shouldParseALongInteger() {
        JSONStream proc = new JSONStream();
        String json = "1234567812345678";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONLong);
        assertEquals(new JSONLong(1234567812345678L), result);
    }

    @Test
    public void shouldParseALongIntegerWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "     1232343454565676787  ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONLong);
        assertEquals(new JSONLong(1232343454565676787L), result);
    }

    @Test
    public void shouldParseASimpleDecimal() {
        JSONStream proc = new JSONStream();
        String json = "123.45678";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseASimpleDecimalWithLeadingSpace() {
        JSONStream proc = new JSONStream();
        String json = " 123.45678";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("123.45678"), result);
    }

    @Test
    public void shouldParseASimpleDecimalWithTrailingSpace() {
        JSONStream proc = new JSONStream();
        String json = "123.5 ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("123.5"), result);
    }

    @Test
    public void shouldParseASimpleDecimalWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = " 98876.25   ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("98876.25"), result);
    }

    @Test
    public void shouldParseANegativeDecimal() {
        JSONStream proc = new JSONStream();
        String json = "-123.45678";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseANegativeDecimalWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "     -123.45678 ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("-123.45678"), result);
    }

    @Test
    public void shouldParseScientificNotation() {
        JSONStream proc = new JSONStream();
        String json = "123e58";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseScientificNotationWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "   1.2345e+10    ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("1.2345e10"), result);
    }

    @Test
    public void shouldParseNegativeScientificNotation() {
        JSONStream proc = new JSONStream();
        String json = "-6789.08e-22";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal(json), result);
    }

    @Test
    public void shouldParseNegativeScientificNotationWithMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = "   -1.777e-5 ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONDecimal);
        assertEquals(new JSONDecimal("-1.777e-5"), result);
    }

    @Test
    public void shouldParseASimpleString() {
        JSONStream proc = new JSONStream();
        String json = "\"abcdef\"";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abcdef"), result);
    }

    @Test
    public void shouldParseASimpleStringWithLeadingSpace() {
        JSONStream proc = new JSONStream();
        String json = " \"ghijk\"";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("ghijk"), result);
    }

    @Test
    public void shouldParseASimpleStringWithTrailingSpace() {
        JSONStream proc = new JSONStream();
        String json = "\"lmnop\" ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("lmnop"), result);
    }

    @Test
    public void shouldParseAStringWithANewline() {
        JSONStream proc = new JSONStream();
        String json = "\"abc\\ndef\"";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abc\ndef"), result);
    }

    @Test
    public void shouldParseEmptyString() {
        JSONStream proc = new JSONStream();
        String json = "\"\"";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString(""), result);
    }

    @Test
    public void shouldParseAStringWithAUnicodeSequence() {
        JSONStream proc = new JSONStream();
        String json = "\"abc\\u000Adef\"";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("abc\ndef"), result);
    }

    @Test
    public void shouldParseAStringWithAnEmoji() throws Exception {
        IntPipeline<JSONValue> proc = new UTF8_CodePoint<>(new JSONStream());
        byte[] json = new byte[] { '"', 'a', 'a', (byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x82, 'b', 'b', '"' };
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONString);
        assertEquals(new JSONString("aa\uD83D\uDE02bb"), result);
    }

    @Test
    public void shouldParseEmptyArray() {
        JSONStream proc = new JSONStream();
        String json = "[]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces1() {
        JSONStream proc = new JSONStream();
        String json = " []";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces2() {
        JSONStream proc = new JSONStream();
        String json = "[ ]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces3() {
        JSONStream proc = new JSONStream();
        String json = " [ ]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces4() {
        JSONStream proc = new JSONStream();
        String json = "[] ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces5() {
        JSONStream proc = new JSONStream();
        String json = " [] ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces6() {
        JSONStream proc = new JSONStream();
        String json = "[ ] ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseEmptyArrayWithSpaces7() {
        JSONStream proc = new JSONStream();
        String json = " [ ] ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(), result);
    }

    @Test
    public void shouldParseArrayWithSingleZeroElement() {
        JSONStream proc = new JSONStream();
        String json = "[0]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithTwoZeroElements() {
        JSONStream proc = new JSONStream();
        String json = "[0,0]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithThreeZeroElementsIncludingExtraSpacing() {
        JSONStream proc = new JSONStream();
        String json = " [0,  0   ,0]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONZero.ZERO, JSONZero.ZERO), result);
    }

    @Test
    public void shouldParseArrayWithThreeStringElements() {
        JSONStream proc = new JSONStream();
        String json = "[\"abcdef\",\"ghijkl\",\"mnopqr\"]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(new JSONString("abcdef"), new JSONString("ghijkl"), new JSONString("mnopqr")),
                result);
    }

    @Test
    public void shouldParseNestedArray() {
        JSONStream proc = new JSONStream();
        String json = "[[12,34],[56,78]]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(new JSONArray(new JSONInteger(12), new JSONInteger(34)),
                new JSONArray(new JSONInteger(56), new JSONInteger(78))), result);
    }

    @Test
    public void shouldParseBooleanTrue() {
        JSONStream proc = new JSONStream();
        String json = "true";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertSame(JSONBoolean.TRUE, result);
    }

    @Test
    public void shouldParseBooleanTrueWithLeadingSpace() {
        JSONStream proc = new JSONStream();
        String json = " true";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertSame(JSONBoolean.TRUE, result);
    }

    @Test
    public void shouldParseBooleanTrueWithTrailingSpace() {
        JSONStream proc = new JSONStream();
        String json = "true ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertEquals(JSONBoolean.TRUE, result);
    }

    @Test
    public void shouldParseBooleanFalse() {
        JSONStream proc = new JSONStream();
        String json = "false";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONBoolean);
        assertEquals(JSONBoolean.FALSE, result);
    }

    @Test
    public void shouldParseKeywordNull() {
        JSONStream proc = new JSONStream();
        String json = "null";
        proc.accept(json);
        proc.close();
        assertNull(proc.getResult());
    }

    @Test
    public void shouldParseHeterogenousArray() {
        JSONStream proc = new JSONStream();
        String json = "[0,true,\"abc\",8.5,200,[]]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONBoolean.TRUE, new JSONString("abc"), new JSONDecimal("8.5"),
                new JSONInteger(200), new JSONArray()), result);
    }

    @Test
    public void shouldParseHeterogenousArrayWithExtraSpacing() {
        JSONStream proc = new JSONStream();
        String json = " [0 ,true,   \"abc\",  8.5 ,    200 ,[   ]]  ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        assertEquals(new JSONArray(JSONZero.ZERO, JSONBoolean.TRUE, new JSONString("abc"), new JSONDecimal("8.5"),
                new JSONInteger(200), new JSONArray()), result);
    }

    @Test
    public void shouldParseSimpleObject() {
        JSONStream proc = new JSONStream();
        String json = "{\"field\":0}";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("field", JSONZero.ZERO);
        assertEquals(map, result);
    }

    @Test
    public void shouldParseObjectWithTwoFields() {
        JSONStream proc = new JSONStream();
        String json = "{\"f1\":0,\"f2\":123}";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("f1", JSONZero.ZERO);
        map.put("f2", new JSONInteger(123));
        assertEquals(map, result);
    }

    @Test
    public void shouldParseObjectWithThreeFields() {
        JSONStream proc = new JSONStream();
        String json = "{\"f1\":0,\"f2\":27.555,\"f3\":true}";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("f1", JSONZero.ZERO);
        map.put("f2", new JSONDecimal("27.555"));
        map.put("f3", JSONBoolean.TRUE);
        assertEquals(map, result);
    }

    @Test
    public void shouldParseObjectWithThreeFieldsAndMultipleSpaces() {
        JSONStream proc = new JSONStream();
        String json = " {   \"f1\" :0 ,\"f2\":   27.555,  \"f3\"    :  true }   ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONObject);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("f1", JSONZero.ZERO);
        map.put("f2", new JSONDecimal("27.555"));
        map.put("f3", JSONBoolean.TRUE);
        assertEquals(map, result);
    }

    @Test
    public void shouldParseArrayOfObject() {
        JSONStream proc = new JSONStream();
        String json = "[{\"aaa\":2000}]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("aaa", JSONInteger.valueOf(2000));
        assertEquals(new JSONArray(new JSONObject(map)), result);
    }

    @Test
    public void shouldParseArrayOfObjectContainingArray() {
        JSONStream proc = new JSONStream();
        String json = "[{\"aaa\":[0]}]";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("aaa", new JSONArray(JSONZero.ZERO));
        assertEquals(new JSONArray(new JSONObject(map)), result);
    }

    @Test
    public void shouldParseArrayOfObjectContainingArrayWithSpaces() {
        JSONStream proc = new JSONStream();
        String json = " [ { \"aaa\" : [ 0 ] } ] ";
        proc.accept(json);
        proc.close();
        JSONValue result = proc.getResult();
        assertTrue(result instanceof JSONArray);
        Map<String, JSONValue> map = new LinkedHashMap<>();
        map.put("aaa", new JSONArray(JSONZero.ZERO));
        assertEquals(new JSONArray(new JSONObject(map)), result);
    }

    @Test
    public void shouldIgnoreByteOrderMark() throws Exception {
        // BOM is not allowed in JSON, but JSONStream ignores it anyway
        IntPipeline<JSONValue> proc = new UTF8_CodePoint<>(new JSONStream());
        byte[] json = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF, '0' };
        proc.accept(json);
        proc.close();
        assertSame(JSONZero.ZERO, proc.getResult());
    }

}
