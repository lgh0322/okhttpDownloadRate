package com.vaca.okhttpdownloadrate

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.OkHttpClient
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PathUtil.initVar(this)
        loadFile("http://65.49.212.218:8000/app-debug.apk","fuck.apk",object: OnDownloadListener{
            override fun onDownloadStart() {
                Log.e("fuck1","了空间的身份将来肯定是")
            }

            override fun onDownloadSuccess(filePath: String?) {
                Log.e("fuck777777771","了空间的身份将来肯定是")
            }

            override fun onDownloading(progress: Int) {
                Log.e("fuck22","了空间的身份将来肯定是   $progress")
            }

            override fun onDownloadFailed() {
                Log.e("fuck1","了空间的身份将来肯定是")
            }

        })
    }

    interface OnDownloadListener {
        /**
         * 开始下载
         */
        fun onDownloadStart()

        /**
         * 下载成功
         * @param filePath 文件下载的路径
         */
        fun onDownloadSuccess(filePath: String?)

        /**
         * @param progress 下载进度
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }
    /**
     * 下载文件
     *
     * @param url      下载地址
     * @param fileName 保存的文件名  文件路径 load
     */
    fun loadFile(url: String?, fileName: String, listener: OnDownloadListener?) {

        val absoluteFilePath: String = PathUtil.getPathX(fileName)
        val file = File(absoluteFilePath)

        val request: Request = Request.Builder().url(url!!).build()
        listener?.onDownloadStart()
        getLoadFileOkHttp().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                listener?.onDownloadFailed()
            }

            override fun onResponse(call: Call, response: Response) {
                if (200 == response.code) {
                    var fileOutputStream: FileOutputStream? = null
                    var inputStream: InputStream? = null
                    try {
                        val total = response.body!!.contentLength()
                        var sum: Long = 0
                        inputStream = response.body!!.byteStream()
                        fileOutputStream = FileOutputStream(file)
                        val buffer = ByteArray(1024 * 1024)
                        var len = 0
                        while (inputStream.read(buffer).also { len = it } != -1) {
                            fileOutputStream.write(buffer, 0, len)
                            if (listener != null) {
                                sum += len.toLong()
                                val progress = (sum * 1.0f / total * 100).toInt()
                                // 下载中
                                listener.onDownloading(progress)
                            }
                        }
                        fileOutputStream.flush()
                        listener?.onDownloadSuccess(absoluteFilePath)
                    } catch (e: IOException) {
                        listener?.onDownloadFailed()
                    } finally {
                        inputStream?.close()
                        fileOutputStream?.close()
                    }
                } else {
                    listener?.onDownloadFailed()
                }
            }

        }


        )
    }




    private var mOkHttpClient: OkHttpClient? = null

    /**
     * 初始化 OkHttpClient
     */
    private fun getLoadFileOkHttp(): OkHttpClient {
        if (mOkHttpClient == null) {
            val sdcache: File =application.cacheDir
            val cacheSize = 10 * 1024 * 1024
            mOkHttpClient = OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(Cache(sdcache.absoluteFile, cacheSize.toLong()))
                    .build()
        }
        return mOkHttpClient as OkHttpClient
    }

}