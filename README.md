# json-stream

JSON Streaming library for Java

This library allows JSON input to be parsed on the fly, avoiding the need to allocate memory for the largest possible
input.

To use:

```java
    JSONStream stream = new JSONStream();
```

As each character is received (for example, from an HTTP connection):

```java
    stream.accept(ch)
```

When all characters have been processed:

```java
    stream.close();
```

(If the characters are being read from a `Reader`, the EOF character may be passed to the stream; this will have the
same effect as `close()`.)

Alternatively, a `String` may be sent in a single operation (although this defeats the purpose of a byte-by-byte
parser!); in this case the `close()` will be sent to the stream at the end of the data.

The resulting `JSONValue` (for example, a `JSONObject`) is available by calling:

```java
    JSONValue = stream.getResult();
```

## Pipeline

And now - `JSONArrayPipeline`.  This class takes an `Acceptor` as a constructor argument
(see [`pipelines`](https://github.com/pwall567/pipelines)), and as characters are fed to it from the string form of a
JSON array, the parsed array elements are passed to the consumer.
See the test for an example.

## Dependency Specification

The latest version of the library is 0.8, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.json</groupId>
      <artifactId>json-stream</artifactId>
      <version>0.8</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.json:json-stream:0.8'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.json:json-stream:0.8")
```

Peter Wall

2021-04-20
