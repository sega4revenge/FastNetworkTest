package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import sega.fastnetwork.test.model.Comment
import sega.fastnetwork.test.model.ResponseListComment
import sega.fastnetwork.test.util.Constants

/**
 * Created by cc on 8/17/2017.
 */
class CommentPresenter(view: CommentView) {

    internal var mCommentView: CommentView = view
    var register = "REGISTER"
    fun onCreate() {
        // initialization
    }

    fun addcomment(userid : String, productid : String, content : String) {


        val jsonObject = JSONObject()
        try {
            jsonObject.put("userid", userid)
            jsonObject.put("productid", productid)
            jsonObject.put("content", content)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "addcomment")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register, " bytesSent : " + bytesSent)
                    Log.d(register, " bytesReceived : " + bytesReceived)
                    Log.d(register, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListComment::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseListComment> {
                    override fun onNext(response: ResponseListComment?) {
                        Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        Log.e("Size", response?.comment!!.size.toString())
                        mCommentView.getCommentDetail(response.comment!!)

                    }


                    override fun onComplete() {

                        mCommentView.isCommentSuccessful(true)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + anError.errorCode)
                                Log.d(register, "onError errorBody : " + anError.errorBody)
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mCommentView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    fun refreshcomment(productid : String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("productid", productid)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "refreshcomment")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register, " bytesSent : " + bytesSent)
                    Log.d(register, " bytesReceived : " + bytesReceived)
                    Log.d(register, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListComment::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseListComment> {
                    override fun onNext(response: ResponseListComment?) {
                        Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        Log.e("Size", response!!.comment.toString())
                        mCommentView.getCommentDetail(response.comment!!)
                    }


                    override fun onComplete() {

                        mCommentView.isCommentSuccessful(true)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + anError.errorCode)
                                Log.d(register, "onError errorBody : " + anError.errorBody)
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mCommentView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    fun deletecomment(commentid : String, productid : String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("commentid", commentid)
            jsonObject.put("productid", productid)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Rx2AndroidNetworking.post(Constants.BASE_URL + "deletecomment")
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register, " bytesSent : " + bytesSent)
                    Log.d(register, " bytesReceived : " + bytesReceived)
                    Log.d(register, " isFromCache : " + isFromCache)
                }
                .getObjectObservable(ResponseListComment::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseListComment> {
                    override fun onNext(response: ResponseListComment?) {
                        Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                        Log.e("Size", response!!.comment.toString())
                        mCommentView.getCommentDetail(response.comment!!)
                    }


                    override fun onComplete() {

                        mCommentView.isCommentSuccessful(true)
                    }

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            val anError = e
                            if (anError.errorCode != 0) {
                                // received ANError from server
                                // error.getErrorCode() - the ANError code from server
                                // error.getErrorBody() - the ANError body from server
                                // error.getErrorDetail() - just a ANError detail
                                Log.d(register, "onError errorCode : " + anError.errorCode)
                                Log.d(register, "onError errorBody : " + anError.errorBody)
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d(register, "onError errorDetail : " + anError.errorDetail)
                                mCommentView.setErrorMessage(anError.errorDetail)
                            }
                        } else {
                            Log.d(register, "onError errorMessage : " + e.message)
                            mCommentView.setErrorMessage(e.message!!)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                })
    }
    interface CommentView {

        fun isCommentSuccessful(isCommentSuccessful: Boolean)
        fun setErrorMessage(errorMessage: String)
        fun getCommentDetail(listcomment :ArrayList<Comment>)

    }
}