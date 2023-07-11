/*
 * @(#) JSONStreamErrorTest.kt
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

import net.pwall.json.JSONException;
import net.pwall.json.stream.JSONStream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONStreamErrorTest {

    @Test
    public void shouldRejectIncorrectJSON() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("abc");
        });
        assertEquals("Illegal syntax in JSON", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenJSONIncomplete() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("[");
            proc.close();
        });
        assertEquals("Unexpected end of data", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenGettingResultAndJSONIncomplete() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept('{');
            proc.getResult();
        });
        assertEquals("JSON not complete", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONArray() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("[{}0]");
        });
        assertEquals("Illegal syntax in JSON array", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenExtraCharactersAfterJSON() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("[],");
        });
        assertEquals("Unexpected characters at end of JSON", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenJSONKeywordIncorrect() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("tru*e");
        });
        assertEquals("Illegal character in JSON keyword", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidNumber1() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("0.a");
        });
        assertEquals("Illegal JSON number", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidNumber2() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("0Ea");
        });
        assertEquals("Illegal JSON number", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONObject1() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("{0}");
        });
        assertEquals("Illegal syntax in JSON object", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONObject2() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("{\"aaa\"}");
        });
        assertEquals("Illegal syntax in JSON object", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONObject3() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("{\"aaa\":0,}");
        });
        assertEquals("Illegal syntax in JSON object", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONString1() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("\"a\u001Eb\"");
        });
        assertEquals("Illegal character in JSON string", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONString2() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("\"a\\gb\"");
        });
        assertEquals("Illegal escape sequence in JSON string", exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidJSONString3() {
        JSONException exception = assertThrows(JSONException.class, () -> {
            JSONStream proc = new JSONStream();
            proc.accept("\"a\\uxxxxb\"");
        });
        assertEquals("Illegal Unicode sequence in JSON string", exception.getMessage());
    }

}
