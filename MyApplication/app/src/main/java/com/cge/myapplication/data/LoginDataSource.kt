package com.cge.example.data

import android.content.res.XmlResourceParser
import android.os.AsyncTask
import com.cge.example.data.model.LoggedInUser
import com.cge.myapplication.App
import com.cge.myapplication.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String): Result<LoggedInUser> {
        try {
            val hostSettings = readSettings(R.xml.settings, "host")

            val url: String = String.format(
                hostSettings.getAttributeValue(null, "login_uri"),
                hostSettings.getAttributeValue(null, "name"),
                username
            )

            val response = HTTPAsyncGetTask().execute(url).get()
            val data = JSONObject(response)

            return when {
                data.has("Success") -> {
                    val result = data.get("Success")
                    val userid = (result as JSONObject).keys().next()
                    val user = LoggedInUser(userid, result.getString(userid))
                    Result.Success(user)
                }
                data.has("Error") -> Result.Error(Exception(data.getString("Error")))
                else -> Result.Error(Exception("Unspecified Error"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }


    fun logout() {
        // TODO: revoke authentication
    }
    private fun readSettings(resource_id: Int, tag: String): XmlResourceParser {
        val xml = App.resourses.getXml(resource_id)
        var eventType = -1

        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                if (xml.name == tag)
                    return xml
            }
            eventType = xml.next()
        }
        throw Exception("Invalid settings.xml file")
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray()))
            .toString(16).padStart(32, '0')
    }

    inner class HTTPAsyncGetTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return URL(params[0]).readText()
        }
    }

    inner class HTTPAsyncPostTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[0])

            val urlParams:String = String.format(
                params[1].toString(), params[2], params[3], params[4]
            )

            val response = StringBuffer()

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                val wr = OutputStreamWriter(outputStream)
                wr.write(urlParams)
                wr.flush()

                // I have responseCode as well
                BufferedReader(InputStreamReader(inputStream)).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                }
            }
            return response.toString()
        }
    }

}

