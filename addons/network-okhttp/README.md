# console-network-okhttp

`ConsoleNetworkOkHttpInterceptor`, an OkHttp interceptor that logs every request and its response to Console.
Android and JVM only.

```kotlin
debugImplementation("io.github.thernal:console-network-okhttp:<version>")
```

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(
        ConsoleNetworkOkHttpInterceptor(
            maxBodyBytes = 64 * 1024L,                  // response body read with peekBody, the default
            sensitiveHeaders = SensitiveHeaders.DEFAULT, // optional
        ),
    )
    .build()
```

- The request body is copied into a buffer and read as UTF-8; the response body is read with `peekBody`, so the
  original stream is untouched.
- A failed call is logged as an `Error` response with the exception text and rethrown.
- Logs are sent with `Console.blockingNotify` on the calling thread, so the request waits for the pipeline (and for a
  paused stepper).

Masking, settings and rendering are described in [`network-ui`](../network-ui/README.md).
