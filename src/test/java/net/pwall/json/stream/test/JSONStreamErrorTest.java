package net.pwall.json.stream.test;

import org.junit.jupiter.api.Test;

import net.pwall.json.JSONException;
import net.pwall.json.stream.JSONStream;
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
