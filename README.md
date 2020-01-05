# json-stream
JSON Streaming library for Java

This is a work in progress.  Class names and APIs may change.

To use:

```java
    JSONProcessor processor = new JSONMainProcessor();
```

As each character is received (for example, from an HTTP connection):

```java
    while (true) {
        if (processor.accept(ch))
            break;
    }
```

The `accept` method returns `true` only when the character has been consumed, so the same character should be re-sent
until `true` is received.

When all characters have been processed:

```java
    processor.acceptEnd();
```

The resulting `JSONValue` (for example, a `JSONObject`) is available by calling:

```java
    JSONValue = processor.getResult();
```

Peter Wall

2020-01-05
