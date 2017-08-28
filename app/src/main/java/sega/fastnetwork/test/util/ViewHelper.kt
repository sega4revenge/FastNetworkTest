package sega.fastnetwork.test.util

import android.view.View

/**
 * Created by sega4 on 28/08/2017.
 */

object ViewHelper {


    fun setAlpha(view: View, alpha: Float) {

        Honeycomb.setAlpha(view, alpha)

    }


    fun setPivotX(view: View, pivotX: Float) {

        Honeycomb.setPivotX(view, pivotX)

    }


    fun setPivotY(view: View, pivotY: Float) {

        Honeycomb.setPivotY(view, pivotY)

    }


    fun setRotation(view: View, rotation: Float) {

        Honeycomb.setRotation(view, rotation)

    }


    fun setRotationX(view: View, rotationX: Float) {

        Honeycomb.setRotationX(view, rotationX)

    }


    fun setRotationY(view: View, rotationY: Float) {

        Honeycomb.setRotationY(view, rotationY)

    }


    fun setScaleX(view: View, scaleX: Float) {

        Honeycomb.setScaleX(view, scaleX)

    }


    fun setScaleY(view: View, scaleY: Float) {

        Honeycomb.setScaleY(view, scaleY)

    }


    fun setTranslationX(view: View, translationX: Float) {

        Honeycomb.setTranslationX(view, translationX)

    }


    fun setTranslationY(view: View, translationY: Float) {

        Honeycomb.setTranslationY(view, translationY)

    }


    fun getY(view: View): Float = Honeycomb.getY(view)


    private object Honeycomb {


        internal fun setAlpha(view: View, alpha: Float) {
            view.alpha = alpha
        }


        internal fun setPivotX(view: View, pivotX: Float) {
            view.pivotX = pivotX
        }


        internal fun setPivotY(view: View, pivotY: Float) {
            view.pivotY = pivotY
        }


        internal fun setRotation(view: View, rotation: Float) {
            view.rotation = rotation
        }


        internal fun setRotationX(view: View, rotationX: Float) {
            view.rotationX = rotationX
        }


        internal fun setRotationY(view: View, rotationY: Float) {
            view.rotationY = rotationY
        }


        internal fun setScaleX(view: View, scaleX: Float) {
            view.scaleX = scaleX
        }


        internal fun setScaleY(view: View, scaleY: Float) {
            view.scaleY = scaleY
        }


        internal fun setTranslationX(view: View, translationX: Float) {
            view.translationX = translationX
        }


        internal fun setTranslationY(view: View, translationY: Float) {
            view.translationY = translationY
        }


        internal fun getY(view: View): Float {
            return view.y
        }


    }
}