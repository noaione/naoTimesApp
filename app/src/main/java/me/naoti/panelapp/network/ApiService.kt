package me.naoti.panelapp.network

import android.content.Context
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import me.naoti.panelapp.Constants
import me.naoti.panelapp.builder.getMoshi
import me.naoti.panelapp.network.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private val httpConnectTimeoutSeconds = 10
private val httpWriteTimeoutSeconds = 10
private val httpReadTimeoutSeconds = 10

interface ApiRoutes {
    // Authentication related
    @GET("auth/user")
    suspend fun getUser(): NetworkResponse<UserInfoModel, UserInfoModel>
    @POST("auth/login")
    suspend fun loginUser(@Body user: LoginModel): NetworkResponse<AuthModel, AuthModel>
    @POST("auth/logout")
    suspend fun logoutUser(): NetworkResponse<AuthModel, AuthModel>
    @POST("auth/register")
    suspend fun registerUser(@Body user: RegisterModel): NetworkResponse<ErrorModel, ErrorModel>

    // Dashboard related
    @GET("showtimes/stats")
    suspend fun getServerStats(): NetworkResponse<ErrorModelWithData<List<StatsKeyValueModel>>, ErrorModel>
    @GET("showtimes/latestanime")
    suspend fun getLatestOngoingProjects(): NetworkResponse<ErrorModelWithData<List<Project>>, ErrorModel>

    // Project related
    @GET("showtimes/proyek")
    suspend fun getProjects(): NetworkResponse<ErrorModelWithData<List<ProjectListModel>>, ErrorModel>
    @GET("showtimes/proyek/{id}")
    suspend fun getProject(@Path("id") id: String): NetworkResponse<ErrorModelWithData<ProjectInfoModel>, ErrorModelWithData<ProjectInfoModel>>
    @POST("showtimes/proyek/tambah")
    suspend fun addProject(@Body project: ProjectAddModel): NetworkResponse<ErrorModel, ErrorModel>
    @DELETE("showtimes/proyek/nuke")
    suspend fun removeProject(@Body project: ProjectRemoveModel): NetworkResponse<ErrorModel, ErrorModel>
    @POST("showtimes/proyek/ubah")
    suspend fun updateProjectStatus(@Body project: ProjectAdjustStatusModel): NetworkResponse<ProjectAdjustStatusResponse, ProjectAdjustStatusResponse>
    @POST("showtimes/proyek/ubah")
    suspend fun updateProjectStaff(@Body project: ProjectAdjustStaffModel): NetworkResponse<ProjectAdjustStaffResponse, ProjectAdjustStaffResponse>
    @POST("showtimes/proyek/episode")
    suspend fun removeProjectEpisode(@Body episodes: ProjectEpisodeRemoveModel): NetworkResponse<ErrorModelWithData<List<ProjectEpisodeRemovedResponse>>, ErrorModel>
    @POST("showtimes/proyek/episode")
    suspend fun addProjectEpisode(@Body episodes: ProjectEpisodeAddModel): NetworkResponse<ErrorModelWithData<List<StatusProject>>, ErrorModel>
    @POST("showtimes/proyek/rilis")
    suspend fun updateReleaseStatus(@Body release: ProjectAdjustReleaseModel): NetworkResponse<ErrorModel, ErrorModel>
    @POST("anilist/find")
    suspend fun findAnime(@Query("q") query: String): NetworkResponse<AnimeFindModel, AnimeFindModel>

    // Settings related
    @GET("showtimes/settings")
    suspend fun getRemoteConfig(): ErrorModelWithData<SettingsModel>
    @GET("fsrss/channelfind")
    suspend fun getServerChannels(): SettingsChannelFindModel
    @POST("showtimes/settings/admin")
    suspend fun updateAdmins(@Body admins: SettingsAdjustAdmin): ErrorModel
    @POST("showtimes/settings/announcer")
    suspend fun updateAnnoucer(@Body announcer: SettingsAdjustAnnouncer): ErrorModel
    @POST("showtimes/settings/name")
    suspend fun updateName(@Body name: SettingsAdjustName): AuthModel
    @POST("auth/reset")
    suspend fun updatePassword(@Body password: SettingsAdjustPassword): ErrorModel
}

class ApiService {
    companion object {
        private const val USER_AGENT = "naoTimesApp/${Constants.APP_VERSION} (+${Constants.APP_REPO})"

        fun getService(context: Context) : ApiRoutes {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            logging.redactHeader("Cookie")
            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .setCookieStore(context)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .build()
                    chain.proceed(request)
                }
                .cache(null)
                .connectTimeout(httpConnectTimeoutSeconds.toLong(), TimeUnit.SECONDS)
                .writeTimeout(httpWriteTimeoutSeconds.toLong(), TimeUnit.SECONDS)
                .readTimeout(httpReadTimeoutSeconds.toLong(), TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = getMoshi()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(NetworkResponseAdapterFactory())
                .client(client)
                .build()

            return retrofit.create(ApiRoutes::class.java)
        }
    }
}
