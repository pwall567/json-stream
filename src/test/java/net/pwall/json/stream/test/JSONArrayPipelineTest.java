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
import net.pwall.json.JSONDouble;
import net.pwall.json.JSONInteger;
import net.pwall.json.JSONString;
import net.pwall.json.JSONValue;
import net.pwall.json.JSONZero;
import net.pwall.json.stream.JSONArrayPipeline;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSONArrayPipelineTest {

    @Test
    public void shouldStreamArray() {
        List<JSONValue> list = new ArrayList<>();
        JSONArrayPipeline pipeline = new JSONArrayPipeline(list::add);
        String json = "[0,true,\"abc\",8.5,200,[]]";
        for (int i = 0, n = json.length(); i < n; i++)
            pipeline.accept(json.charAt(i));
        pipeline.accept(-1);
        assertTrue(pipeline.isClosed());
        assertEquals(6, list.size());
        assertEquals(JSONZero.ZERO, list.get(0));
        assertEquals(JSONBoolean.TRUE, list.get(1));
        assertEquals(new JSONString("abc"), list.get(2));
        assertEquals(new JSONDouble(8.5), list.get(3));
        assertEquals(new JSONInteger(200), list.get(4));
        assertEquals(new JSONArray(), list.get(5));
    }

}
