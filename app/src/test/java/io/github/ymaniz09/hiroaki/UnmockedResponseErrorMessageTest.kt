package io.github.ymaniz09.hiroaki

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.ymaniz09.hiroaki.internal.MockServerSuite
import io.github.ymaniz09.hiroaki.models.success
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Integration test that validates the error message quality when a request is not mocked.
 * 
 * ## Testing Strategy (Lead Developer Perspective)
 * 
 * This test suite follows integration testing best practices:
 * 
 * 1. **Tests behavior, not implementation**: We validate what the user observes (error messages)
 *    rather than internal implementation details (how the dispatcher works).
 * 
 * 2. **Multi-component integration**: Each test exercises the full stack:
 *    - HTTP client (Retrofit/OkHttp)
 *    - MockWebServer with HiroakiDispatcher
 *    - Error response generation
 *    - JSON parsing
 *    This ensures the feature works end-to-end, not just in isolation.
 * 
 * 3. **Resilient to refactoring**: If we change the internal implementation of HiroakiDispatcher
 *    (e.g., use a JSON library instead of string templates), these tests will still pass as long
 *    as the error message contains the required information.
 * 
 * 4. **User-centric validation**: Each test validates what matters to developers using the library:
 *    - Can they identify which request failed?
 *    - Do they have enough information to debug?
 *    - Is the error message actionable?
 * 
 * 5. **Edge case coverage**: Tests include:
 *    - Different HTTP methods (GET, POST)
 *    - Query parameters and path parameters
 *    - Request headers and bodies
 *    - Large payloads (truncation)
 *    - Similar requests (distinguishability)
 * 
 * This approach ensures high-quality, maintainable tests that protect user experience
 * while allowing internal improvements.
 */
class UnmockedResponseErrorMessageTest : MockServerSuite() {

    @Before
    override fun setup() {
        super.setup()
        // Configure the dispatcher by setting up a mock that will never be matched.
        // This ensures HiroakiDispatcher is active and will provide helpful error messages.
        server.whenever(Method.GET, "__never_matched_path__").thenRespond(success())
    }

    private interface TestApi {
        @GET("api/users/{userId}")
        suspend fun getUser(
            @Path("userId") userId: String,
            @Query("include") include: String?,
            @Header("Authorization") authorization: String
        ): JsonObject

        @POST("api/orders")
        suspend fun createOrder(
            @Body orderData: JsonObject,
            @Header("X-Request-ID") requestId: String
        ): JsonObject
    }

    private val gson = Gson()

    @Test
    fun `when GET request is not mocked, error message contains method, path, query params, and headers`() {
        // Setup: Create a real HTTP client using the mock server
        val api = createRetrofitService<TestApi>(GsonConverterFactory.create())

        // Act: Make a request WITHOUT mocking it
        val exception = runCatching {
            runBlocking {
                api.getUser(
                    userId = "12345",
                    include = "profile,settings",
                    authorization = "Bearer token-abc-123"
                )
            }
        }.exceptionOrNull()

        // Assert: Validate the error response contains actionable information
        require(exception is HttpException) { "Expected HttpException but got ${exception?.javaClass?.name}" }
        require(exception.code() == 500) { "Expected HTTP 500 but got ${exception.code()}" }

        val errorBody = exception.response()?.errorBody()?.string()
        requireNotNull(errorBody) { "Error body should not be null" }

        val errorJson = gson.fromJson(errorBody, JsonObject::class.java)

        // Validate error structure and content
        require(errorJson.has("error")) { "Error message should have 'error' field" }
        require(errorJson.has("request")) { "Error message should have 'request' field" }
        require(errorJson.has("suggestion")) { "Error message should have 'suggestion' field" }

        val errorMessage = errorJson.get("error").asString
        require(errorMessage.contains("No mocked response found", ignoreCase = true)) {
            "Error should clearly state that no mock was found. Got: $errorMessage"
        }

        // Validate request details are present
        val requestInfo = errorJson.getAsJsonObject("request")
        require(requestInfo.has("method")) { "Request info should contain method" }
        require(requestInfo.has("path")) { "Request info should contain path" }
        require(requestInfo.has("headers")) { "Request info should contain headers" }

        // Validate the actual request details match what was sent
        require(requestInfo.get("method").asString == "GET") {
            "Method should be GET but was ${requestInfo.get("method").asString}"
        }

        val path = requestInfo.get("path").asString
        require(path.contains("/api/users/12345")) {
            "Path should contain the user ID endpoint. Got: $path"
        }
        require(path.contains("include=profile,settings") || path.contains("include=profile%2Csettings")) {
            "Path should contain query parameters. Got: $path"
        }

        val headers = requestInfo.get("headers").asString
        require(headers.contains("authorization", ignoreCase = true)) {
            "Headers should contain Authorization header. Got: $headers"
        }

        // Validate suggestion is helpful
        val suggestion = errorJson.get("suggestion").asString
        require(suggestion.contains("whenever") || suggestion.contains("enqueue")) {
            "Suggestion should mention how to mock requests. Got: $suggestion"
        }
    }

    @Test
    fun `when POST request with body is not mocked, error message includes the request body`() {
        // Setup
        val api = createRetrofitService<TestApi>(GsonConverterFactory.create())

        val orderData = JsonObject().apply {
            addProperty("product_id", "prod-789")
            addProperty("quantity", 3)
            addProperty("customer_email", "test@example.com")
        }

        // Act: Make a POST request WITHOUT mocking it
        val exception = runCatching {
            runBlocking {
                api.createOrder(
                    orderData = orderData,
                    requestId = "req-uuid-456"
                )
            }
        }.exceptionOrNull()

        // Assert
        require(exception is HttpException) { "Expected HttpException but got ${exception?.javaClass?.name}" }

        val errorBody = exception.response()?.errorBody()?.string()
        requireNotNull(errorBody) { "Error body should not be null" }

        val errorJson = gson.fromJson(errorBody, JsonObject::class.java)
        val requestInfo = errorJson.getAsJsonObject("request")

        // Validate method and path
        require(requestInfo.get("method").asString == "POST") {
            "Method should be POST but was ${requestInfo.get("method").asString}"
        }

        val path = requestInfo.get("path").asString
        require(path.contains("/api/orders")) {
            "Path should contain orders endpoint. Got: $path"
        }

        // Validate body is present in error message
        val bodyInfo = requestInfo.get("body").asString
        require(bodyInfo.contains("prod-789")) {
            "Body should contain product_id from request. Got: $bodyInfo"
        }
        require(bodyInfo.contains("customer_email")) {
            "Body should contain customer_email field. Got: $bodyInfo"
        }

        // Validate custom headers
        val headers = requestInfo.get("headers").asString
        require(headers.contains("x-request-id", ignoreCase = true)) {
            "Headers should contain X-Request-ID. Got: $headers"
        }
    }

    @Test
    fun `when multiple unmocked requests are made, each gets a descriptive error with correct details`() {
        // Setup: Use raw OkHttp to have more control
        val client = OkHttpClient()
        val baseUrl = server.url("")

        // Act & Assert: Make multiple different requests and validate each error is specific
        
        // Request 1: GET with query params
        val request1 = Request.Builder()
            .url(baseUrl.newBuilder()
                .addPathSegment("search")
                .addQueryParameter("q", "kotlin")
                .addQueryParameter("page", "1")
                .build())
            .addHeader("User-Agent", "TestClient/1.0")
            .get()
            .build()

        val response1 = client.newCall(request1).execute()
        require(response1.code == 500) { "Should return 500 for unmocked request" }
        
        val error1 = gson.fromJson(response1.body?.string(), JsonObject::class.java)
        val request1Info = error1.getAsJsonObject("request")
        val path1 = request1Info.get("path").asString
        
        require(path1.contains("search")) { "First error should mention search endpoint. Got: $path1" }
        require(path1.contains("q=kotlin")) { "First error should include query param. Got: $path1" }

        // Request 2: POST with different path
        val request2 = Request.Builder()
            .url(baseUrl.newBuilder().addPathSegment("submit").build())
            .addHeader("Content-Type", "application/json")
            .post("""{"action":"delete","id":"999"}""".toRequestBody("application/json".toMediaType()))
            .build()

        val response2 = client.newCall(request2).execute()
        require(response2.code == 500) { "Should return 500 for unmocked request" }
        
        val error2 = gson.fromJson(response2.body?.string(), JsonObject::class.java)
        val request2Info = error2.getAsJsonObject("request")
        
        require(request2Info.get("method").asString == "POST") { 
            "Second error should show POST method" 
        }
        require(request2Info.get("path").asString.contains("submit")) { 
            "Second error should mention submit endpoint" 
        }
        require(request2Info.get("body").asString.contains("delete")) { 
            "Second error should include request body content" 
        }

        // This validates that each unmocked request gets its own specific error,
        // proving the error message is contextual and helpful for debugging
    }

    @Test
    fun `error message helps distinguish between similar unmocked requests`() {
        // This test validates that the error message is specific enough to help developers
        // identify which exact request failed when they have similar endpoints
        
        val client = OkHttpClient()
        val baseUrl = server.url("")

        // Scenario: Similar endpoints but different IDs
        val request1 = Request.Builder()
            .url(baseUrl.newBuilder().addPathSegment("api").addPathSegment("items").addPathSegment("100").build())
            .get()
            .build()

        val request2 = Request.Builder()
            .url(baseUrl.newBuilder().addPathSegment("api").addPathSegment("items").addPathSegment("200").build())
            .get()
            .build()

        val response1 = client.newCall(request1).execute()
        val response2 = client.newCall(request2).execute()

        val error1 = gson.fromJson(response1.body?.string(), JsonObject::class.java)
        val error2 = gson.fromJson(response2.body?.string(), JsonObject::class.java)

        val path1 = error1.getAsJsonObject("request").get("path").asString
        val path2 = error2.getAsJsonObject("request").get("path").asString

        // Validate that each error message contains the specific ID that was requested
        require(path1.contains("100")) { "First error should show item 100. Got: $path1" }
        require(path2.contains("200")) { "Second error should show item 200. Got: $path2" }
        require(path1 != path2) { "Error messages should be different for different requests" }

        // This is crucial: when a developer has multiple similar requests and forgets to mock one,
        // they need to know EXACTLY which one failed
    }

    @Test
    fun `large request body is truncated but still useful in error message`() {
        // This validates that even with large payloads, the error message remains readable
        
        val client = OkHttpClient()
        val baseUrl = server.url("")

        // Create a large request body (> 500 characters)
        val largeBody = buildString {
            append("""{"data":[""")
            repeat(50) { i ->
                if (i > 0) append(",")
                append("""{"id":$i,"name":"item$i","description":"This is a long description for item $i"}""")
            }
            append("]}")
        }

        val request = Request.Builder()
            .url(baseUrl.newBuilder().addPathSegment("bulk-upload").build())
            .post(largeBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val error = gson.fromJson(response.body?.string(), JsonObject::class.java)
        val bodyInError = error.getAsJsonObject("request").get("body").asString

        // Validate the body is included but not overwhelming
        require(bodyInError.isNotEmpty()) { "Body should be present in error" }
        
        // Should contain the beginning of the request (most important part for debugging)
        require(bodyInError.contains("data")) { "Should contain start of body. Got: $bodyInError" }
        
        // Should indicate truncation if body was too large
        if (largeBody.length > 500) {
            require(bodyInError.contains("truncated") || bodyInError.length <= 550) {
                "Large bodies should be truncated or indicated as such. Body length: ${bodyInError.length}"
            }
        }

        // The key insight: Error messages should be informative but not overwhelming
    }

    private inline fun <reified T> createRetrofitService(converterFactory: GsonConverterFactory): T {
        return Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(converterFactory)
            .build()
            .create(T::class.java)
    }
}

