package com.karoke.myoyasing.data.remote

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface ApiResult<out T> {

    data class Success<T>(
        val data: T
    ) : ApiResult<T>

    data class Error(
        val message: String,
        val code: Int? = null
    ) : ApiResult<Nothing>
}

object SafeApiCall {

    suspend fun <T : Any> execute(
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {

        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(
                        message = "The server returned an empty response.",
                        code = response.code()
                    )
                }
            } else {
                ApiResult.Error(
                    message = extractErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    ),
                    code = response.code()
                )
            }

        } catch (e: CancellationException) {
            // Preserve coroutine cancellation.
            throw e

        } catch (e: SocketTimeoutException) {
            ApiResult.Error(
                message = "Request timed out. Please try again."
            )

        } catch (e: IOException) {
            ApiResult.Error(
                message = "Network error. Check your internet connection."
            )

        } catch (e: JsonParseException) {
            ApiResult.Error(
                message = "Invalid response received from the server."
            )

        } catch (e: Exception) {
            // Covers HttpException, unexpected parsing failures,
            // and other ordinary exceptions from the API call.
            ApiResult.Error(
                message = "Something went wrong. Please try again."
            )
        }
    }

    private fun extractErrorMessage(
        code: Int,
        errorBody: String?
    ): String {
        val serverMessage = try {
            if (errorBody.isNullOrBlank()) {
                null
            } else {
                JsonParser
                    .parseString(errorBody)
                    .asJsonObject
                    .get("message")
                    ?.takeIf { it.isJsonPrimitive }
                    ?.asString
                    ?.takeIf { it.isNotBlank() }
            }
        } catch (_: Exception) {
            null
        }

        return serverMessage ?: when (code) {
            400 -> "Invalid request. Please check your details."
            401 -> "Authentication failed. Please try again."
            403 -> "You are not allowed to perform this action."
            404 -> "The requested service was not found."
            429 -> "Too many requests. Please try again later."
            in 500..599 -> "Server error. Please try again later."
            else -> "Request failed (HTTP $code)."
        }
    }
}