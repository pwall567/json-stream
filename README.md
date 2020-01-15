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
    processor.close();
```

(If the characters are being read from a `Reader`, the EOF character may be passed to the processor.)

The resulting `JSONValue` (for example, a `JSONObject`) is available by calling:

```java
    JSONValue = processor.getResult();
```

## Pipeline

And now - `JSONArrayPipeline`.  This class takes an `Acceptor` as a constructor argument
(see [`pipelines`](https://github.com/pwall567/pipelines)), and as characters are fed to it from the string form of a
JSON array, the parsed array elements are passed to the consumer.
See the test for an example.

Peter Wall

2020-01-15
