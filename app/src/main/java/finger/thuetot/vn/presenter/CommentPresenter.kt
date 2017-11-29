package finger.thuetot.vn.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import finger.thuetot.vn.model.Comment
import finger.thuetot.vn.model.ResponseListComment
import finger.thuetot.vn.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

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
                        Log.d(register, "onError errorDetail : " + e.message)
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
    private fun getDisposableObserverAddComment(): DisposableObserver<ResponseListComment> {

        return object : DisposableObserver<ResponseListComment>() {

            override fun onNext(response: ResponseListComment) {
                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                Log.e("Size", response!!.comment.toString())
                mCommentView.getStatusAddComent(response.comment!!)
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
                        mCommentView.setErrorMessage(e.errorCode.toString())
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(register, "onError errorDetail : " + e.message)
                        mCommentView.setErrorMessage(e.errorCode.toString())
                    }
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mCommentView.setErrorMessage(e.message.toString())
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
                .subscribeWith(getDisposableObserverAddComment()))

    }
    fun addreplycomment(userid : String, productid : String, content : String) {
        try {
            jsonObject.put("userid", userid)
                jsonObject.put("commentid", productid)
            jsonObject.put("content", content)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("addreplycomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserverAddComment()))

    }

    fun likecomment(idcomment : String, iduserlike : String,type : String) {
        try {
            jsonObject.put("idcomment", idcomment)
            jsonObject.put("iduserlike", iduserlike)
            jsonObject.put("type", type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("likecomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserverlikecomment()))

    }

    private fun getDisposableObserverlikecomment(): DisposableObserver<ResponseListComment> {

        return object : DisposableObserver<ResponseListComment>() {

            override fun onNext(response: ResponseListComment) {
                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
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
                        Log.d("Comment","Like Faile")
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(register, "onError errorDetail : " + e.message)
                        Log.d("Comment","Like Faile")
                    }
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    Log.d("Comment","Like Faile")
                }
            }

            override fun onComplete() {
                Log.d("Comment","Like Success")
            }
        }
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
    fun refreshreplycomment(productid : String) {
        try {
            jsonObject.put("productid", productid)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("refreshrelycomment")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver()))


    }
    fun deletereplycomment(replycommentid : String, commentid : String) {

        try {
            jsonObject.put("replycommentid", replycommentid)
            jsonObject.put("commentid", commentid)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable("deletereplycomment")
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
        fun getStatusAddComent(listcomment :ArrayList<Comment>)
    }
}