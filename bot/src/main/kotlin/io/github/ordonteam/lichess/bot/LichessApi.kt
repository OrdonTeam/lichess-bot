package io.github.ordonteam.lichess.bot

import com.squareup.moshi.Moshi
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming


class LichessApi(lichessToken: String) {
    private val moshi = Moshi.Builder().build()
    private val globalEventAdapter = moshi.adapter(InternalLichessGlobalEvent::class.java)
    private val gameEventAdapter = moshi.adapter(InternalLichessGameEvent::class.java)
    private val api = Retrofit.Builder()
        .baseUrl("https://lichess.org/api/")
        .client(OkHttpClient().newBuilder().addInterceptor(AuthInterceptor(lichessToken)).build())
        .build()
        .create(RetrofitLichessApi::class.java)

    fun lichessGlobalEvents(): Observable<LichessGlobalEvent> {
        return api.streamEvents().parseStream(::parseLichessGlobalEvent)
    }

    private fun parseLichessGlobalEvent(event: String): LichessGlobalEvent {
        if (event.isBlank()) {
            return LichessGlobalEvent.Health
        }
        val fromJson =
            globalEventAdapter.fromJson(event)!!
        return when (fromJson.type) {
            "challenge" -> LichessGlobalEvent.Challenge(fromJson.challenge!!.id)
            "gameStart" -> LichessGlobalEvent.GameStart(fromJson.game!!.id)
            "gameFinish" -> LichessGlobalEvent.GameFinish(fromJson.game!!.id)
            else -> LichessGlobalEvent.Health.also {
                println("Unsupported global event: $event")
            }
        }
    }

    fun acceptChallenge(challengeId: String) {
        val response = api.acceptChallenge(challengeId).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Accepting challenge failed. challengeId:$challengeId response:$response")
        }
    }

    fun lichessGameEvents(gameId: String): Observable<LichessGameEvent> {
        return api.streamEvents(gameId).parseStream(::parseGameEvent)
    }

    private fun parseGameEvent(event: String): LichessGameEvent {
        if (event.isBlank()) {
            return LichessGameEvent.Health
        }
        val fromJson = gameEventAdapter.fromJson(event)!!
        return when (fromJson.type) {
            "gameFull" -> LichessGameEvent.GameFull(fromJson.white!!.id, fromJson.black!!.id, fromJson.state!!.moves)
            "gameState" -> LichessGameEvent.GameState(fromJson.moves!!)
            else -> LichessGameEvent.Health.also {
                println("Unsupported game event: $event")
            }
        }
    }

    fun makeMove(gameId: String, move: String) {
        val response = api.makeMove(gameId, move).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Making a move failed. gameId:$gameId move:$move response:$response")
        }
    }

    fun resign(gameId: String) {
        val response = api.resign(gameId).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("Resigning a game failed. gameId:$gameId response:$response")
        }
    }

    private fun <T> Call<ResponseBody>.parseStream(parse: (t: String) -> T): Observable<T> {
        return Observable.fromIterable(
            execute().body()!!.charStream().buffered().lineSequence().asIterable()
        ).map(parse)
    }

    private class AuthInterceptor(private val token: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
        }
    }

    private interface RetrofitLichessApi {
        @GET("stream/event")
        @Streaming
        fun streamEvents(): Call<ResponseBody>

        @POST("challenge/{challengeId}/accept")
        fun acceptChallenge(@Path("challengeId") challengeId: String): Call<ResponseBody>

        @GET("bot/game/stream/{gameId}")
        @Streaming
        fun streamEvents(@Path("gameId") gameId: String): Call<ResponseBody>

        @POST("bot/game/{gameId}/move/{move}")
        fun makeMove(@Path("gameId") gameId: String, @Path("move") move: String): Call<ResponseBody>

        @POST("bot/game/{gameId}/resign")
        fun resign(@Path("gameId") gameId: String): Call<ResponseBody>
    }

    private data class InternalLichessGlobalEvent(
        val type: String,
        val challenge: InternalLichessEventChallenge? = null,
        val game: InternalLichessEventGame? = null
    )

    private data class InternalLichessEventChallenge(
        val id: String
    )

    private data class InternalLichessEventGame(
        val id: String
    )

    sealed class LichessGlobalEvent {
        object Health : LichessGlobalEvent()
        data class Challenge(val id: String) : LichessGlobalEvent()
        data class GameStart(val id: String) : LichessGlobalEvent()
        data class GameFinish(val id: String) : LichessGlobalEvent()
    }

    private data class InternalLichessGameEvent(
        val type: String,
        val white: InternalPlayer?,
        val black: InternalPlayer?,
        val state: InternalLichessGameEventState?,
        val moves: String?
    )

    private data class InternalLichessGameEventState(
        val moves: String
    )

    private data class InternalPlayer(val id: String, val title: String?)

    sealed class LichessGameEvent {
        object Health : LichessGameEvent()

        data class GameFull(
            val whiteId: String,
            val blackId: String,
            val moves: String
        ) : LichessGameEvent()

        data class GameState(
            val moves: String
        ) : LichessGameEvent()
    }
}