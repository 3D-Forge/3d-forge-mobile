import com.example.a3dforge.base.Cookies
import com.example.a3dforge.base.SSLFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpConfig {
    val cookieJar = Cookies()
    val baseUrl = "https://192.168.0.102:44416/api/user/"
    val client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .sslSocketFactory(SSLFactory().createInsecureSslSocketFactory(), SSLFactory().createX509TrustManager())
        .hostnameVerifier { _, _ -> true }
        .build()
    val gson = Gson()
}
