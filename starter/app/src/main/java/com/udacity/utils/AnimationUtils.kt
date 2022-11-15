package com.udacity.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

fun AnimatorSet.disableViewDuringAnimation(view: View) {

    // This extension method listens for start/end events on an animation and disables
    // the given view for the entirety of that animation.

    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}