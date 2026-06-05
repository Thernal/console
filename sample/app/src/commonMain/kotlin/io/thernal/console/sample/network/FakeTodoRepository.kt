package io.thernal.console.sample.network

import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class FakeTodoRepository(
    private val client: io.ktor.client.HttpClient = createPlatformHttpClient(),
) {

    suspend fun fetchTodo(): FakeTodoResponse {
        val response = client.get(urlString = "https://jsonplaceholder.typicode.com/todos/1")

        return FakeTodoResponse(
            statusCode = response.status.value,
            body = response.bodyAsText(),
        )
    }

    suspend fun createPost(): FakeTodoResponse {
        val requestBody = """
            {
              "title": "console sample post",
              "body": "network-ktor request body logging demo",
              "userId": 42
            }
        """.trimIndent()

        val response = client.post(urlString = "https://jsonplaceholder.typicode.com/posts") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        return FakeTodoResponse(
            statusCode = response.status.value,
            body = response.bodyAsText(),
        )
    }

    fun close() {
        client.close()
    }
}

internal data class FakeTodoResponse(
    val statusCode: Int,
    val body: String,
)
