# json-stream
JSON Streaming library for Java

This is a work in progress.  Class names and APIs may change.

To use:

```java
    JSONStreamProcessor processor = new JSONStreamProcessor();
```

As each character is received (for example, from an HTTP connection):

```java
    processor.accept(ch)
```

The `accept` method returns `true` only when the character has been consumed, so the same character should be re-sent
until `true` is received.

When all characters have been processed:

```java
    processor.accept(-1);
```

(If the characters are being read from a `Reader`, the EOF character may be passed to the processor.)

The resulting `JSONValue` (for example, a `JSONObject`) is available by calling:

```java
    JSONValue = processor.getResult();
```

Peter Wall

2020-01-08
