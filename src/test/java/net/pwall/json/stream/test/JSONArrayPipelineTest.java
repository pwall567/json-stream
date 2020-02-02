/*
 * @(#) JSONArrayPipelineTest.kt
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

import java.util.ArrayList;
import java.util.List;

import net.pwall.json.JSONArray;
import net.pwall.json.JSONBoolean;
import net.pwall.json.JSONDecimal;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;
import net.pwall.json.stream.JSONArrayPipeline;
import net.pwall.util.pipeline.AbstractAcceptor;
import net.pwall.util.pipeline.ListAcceptor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONArrayPipelineTest {

    @Test
    public void shouldStreamArrayToReceivingLambda() throws Exception {
        JSONArrayPipeline<List<JSONValue>> pipeline = new JSONArrayPipeline<>(new ListAcceptor<>());
        String json = "[0,true,\"abc\",8.5,200,[]]";
        pipeline.accept(json);
        assertTrue(pipeline.isComplete());
        List<JSONValue> list = pipeline.getResult();
        assertEquals(6, list.size());
        assertEquals(JSONZero.ZERO, list.get(0));
        assertEquals(JSONBoolean.TRUE, list.get(1));
        assertEquals(new JSONString("abc"), list.get(2));
        assertEquals(new JSONDecimal("8.5"), list.get(3));
        assertEquals(new JSONInteger(200), list.get(4));
        assertEquals(new JSONArray(), list.get(5));
    }

    @Test
    public void shouldStreamArrayToVoidAcceptor() throws Exception {
        List<JSONValue> list = new ArrayList<>();
        JSONArrayPipeline<Void> pipeline = new JSONArrayPipeline<>(new AbstractAcceptor<JSONValue, Void>() {
            @Override
            public void acceptObject(JSONValue value) {
                list.add(value);
            }
        });
        String json = "[ \"abc\", 5.16e10, 999, [ ] ]";
        pipeline.accept(json);
        assertTrue(pipeline.isComplete());
        assertEquals(4, list.size());
        assertEquals(new JSONString("abc"), list.get(0));
        assertEquals(new JSONDecimal("5.16e10"), list.get(1));
        assertEquals(new JSONInteger(999), list.get(2));
        assertEquals(new JSONArray(), list.get(3));
    }

}
