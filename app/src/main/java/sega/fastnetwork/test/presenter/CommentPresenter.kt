package sega.fastnetwork.test.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
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
    var register = "Comment"
    private val disposables = CompositeDisposable()
    private val jsonObject = JSONObject()
    fun onCreate() {
        // initialization
    }
    private fun getObservable(typesearch: String): Observable<ResponseListComment> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag("Comment")
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
                    Log.d(register, " bytesSent : " + bytesSent)
                    Log.d(register, " bytesReceived : " + bytesReceived)
                    Log.d(register, " isFromCache : " + isFromCache)
                }

                .getObjectObservable(ResponseListComment::class.java)
    }
    private fun getDisposableObserver(): DisposableObserver<ResponseListComment> {

        return object : DisposableObserver<ResponseListComment>() {

            override fun onNext(response: ResponseListComment) {
                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                Log.e("Size", response!!.comment.toString())
                mCommentView.getCommentDetail(response.comment!!)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                    if (e.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just a ANError detail
                        Log.d(register, "onError errorCode : " + e.errorCode)
                        Log.d(register, "onError errorBody : " + e.errorBody)
                        Log.d(register, "onError errorDetail : " + e.errorDetail)
                        mCommentView.setErrorMessage(e.errorDetail)
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(register, "onError errorDetail : " + e.errorDetail)
                        mCommentView.setErrorMessage(e.errorDetail)
                    }
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mCommentView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {
                mCommentView.isCommentSuccessful(true)
            }
        }
    }
    fun addcomment(userid : String, productid : String, content : String) {
        try {
            jsonObject.put("userid", userid)
            jsonObject.put("productid", productid)
            jsonObject.put("content", content)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("addcomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))

    }
    fun refreshcomment(productid : String) {
        try {
            jsonObject.put("productid", productid)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("refreshcomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }
    fun deletecomment(commentid : String, productid : String) {

        try {
            jsonObject.put("commentid", commentid)
            jsonObject.put("productid", productid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("deletecomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }
    fun cancelRequest() {
        disposables.clear()
    }
    interface CommentView {

        fun isCommentSuccessful(isCommentSuccessful: Boolean)
        fun setErrorMessage(errorMessage: String)
        fun getCommentDetail(listcomment :ArrayList<Comment>)

    }
}