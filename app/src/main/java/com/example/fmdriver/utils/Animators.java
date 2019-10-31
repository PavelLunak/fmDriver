package com.example.fmdriver.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;


public class Animators {

    public static void animateStatusAfterStart(View view) {
        ObjectAnimator objectAnimatorScaleX1 = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0f, 1f, 0f, 1f);
        objectAnimatorScaleX1.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX1.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.playSequentially(objectAnimatorScaleX1);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    public static void switchViews(final View v1, final View v2) {

        v2.setVisibility(View.VISIBLE);

        ObjectAnimator animatorHideView = ObjectAnimator.ofFloat(v1, View.ALPHA, 0f);
        ObjectAnimator animatorShowView2 = ObjectAnimator.ofFloat(v2, View.ALPHA, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.play(animatorHideView).with(animatorShowView2);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                v1.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    public static void animateButtonClick(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.95f, 1.05f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.95f, 1.05f, 1f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleY.setStartDelay(50);
        objectAnimatorScaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY);
        animatorSet.start();
    }

    public static void animateButtonClick2(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());
        objectAnimatorAlpha.setDuration(200);
        objectAnimatorAlpha.start();
    }

    public static void animateSideButtons(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.3f, 1f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.1f, 0.9f, 1f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());
        objectAnimatorScaleX.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX);
        animatorSet.start();
    }

    public static void hideRightSideButton(final View view, int translateBy, boolean immediately) {
        view.animate().translationXBy(translateBy).setDuration(immediately ? 0 : 200).start();
    }

    public static void hideLeftSideButton(final View view, int translateBy, boolean immediately) {
        view.animate().translationXBy(-translateBy).setDuration(immediately ? 0 : 200).start();
    }

    public static void showRightSideButton(final View view, int translateBy) {
        view.animate().translationXBy(-translateBy).setDuration(200).start();
    }

    public static void showLeftSideButton(final View view, int translateBy) {
        view.animate().translationXBy(translateBy).setDuration(200).start();
    }

    public static void showViewSmoothly(final View view) {
        if (view == null) return;
        view.animate().alpha(1f).setDuration(400).start();
    }

    public static void hideViewSmoothly(final View view) {
        if (view == null) return;
        view.animate().alpha(0f).setDuration(400).start();
    }

    public static void animateCitySelected(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.8f);
        objectAnimatorScaleX.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.8f);
        objectAnimatorScaleY.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimatorTransX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 250f);
        objectAnimatorTransX.setInterpolator(new LinearInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(objectAnimatorAlpha, objectAnimatorScaleX, objectAnimatorScaleY, objectAnimatorTransX);
        animatorSet.start();
    }

    public static void animateLabelNoMessages(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f);
        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.05f);
        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.05f);
        ObjectAnimator objectAnimatorScaleX2 = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f);
        ObjectAnimator objectAnimatorScaleY2 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f);

        //Scale - zvětšení
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorScaleX, objectAnimatorScaleY);
        //Scale - zmenšení (na původní hodnotu)
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(objectAnimatorScaleX2, objectAnimatorScaleY2);
        //Kompletní animace SCALE
        AnimatorSet animatorSetAllScale = new AnimatorSet();
        animatorSetAllScale.playSequentially(animatorSet, animatorSet2);
        //Dokončení animace zavlněním
        ObjectAnimator objectAnimatorScaleXend = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.95f, 1.05f, 1f);
        objectAnimatorScaleXend.setRepeatMode(ObjectAnimator.REVERSE);
        ObjectAnimator objectAnimatorScaleYend = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.95f, 1.05f, 1f);
        objectAnimatorScaleYend.setStartDelay(50);
        objectAnimatorScaleYend.setRepeatMode(ObjectAnimator.REVERSE);
        AnimatorSet animatorSetEnd = new AnimatorSet();
        animatorSetEnd.playTogether(objectAnimatorScaleXend, objectAnimatorScaleYend);

        //Animace SCALE a ALPHA
        AnimatorSet finalAnimatorSet = new AnimatorSet();
        finalAnimatorSet.playTogether(animatorSetAllScale, objectAnimatorAlpha);

        //VŠE DOHROMADY
        AnimatorSet finalAnimatorSetEnd = new AnimatorSet();
        finalAnimatorSetEnd.playSequentially(finalAnimatorSet, animatorSetEnd);
        finalAnimatorSetEnd.setDuration(100);
        finalAnimatorSetEnd.setInterpolator(new LinearInterpolator());
        finalAnimatorSetEnd.start();
    }

    public static void animateShowToolBar(View view, long startDelay) {
        ObjectAnimator objectAnimatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f);
        ObjectAnimator objectAnimatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f);
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f);

        ObjectAnimator objectAnimX = ObjectAnimator.ofFloat(view, View.SCALE_X,
                0.95f,
                1.05f,
                0.96f,
                1.04f,
                0.97f,
                1.03f,
                0.98f,
                1.02f,
                0.99f,
                1.02f,
                1f,
                1f);
        objectAnimX.setInterpolator(new LinearInterpolator());
        objectAnimX.setRepeatMode(ObjectAnimator.REVERSE);

        ObjectAnimator objectAnimY = ObjectAnimator.ofFloat(view, View.SCALE_Y,
                0.95f,
                1.05f,
                0.96f,
                1.04f,
                0.97f,
                1.03f,
                0.98f,
                1.02f,
                0.99f,
                1.02f,
                1f,
                1f);
        objectAnimY.setInterpolator(new LinearInterpolator());
        objectAnimY.setStartDelay(50);
        objectAnimY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setStartDelay(200);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.playTogether(objectAnimatorScaleX, objectAnimatorScaleY, objectAnimatorAlpha);

        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.setDuration(1200);
        animatorSet2.setInterpolator(new LinearInterpolator());
        animatorSet2.playTogether(objectAnimX, objectAnimY);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.setStartDelay(startDelay);
        finalSet.setInterpolator(new LinearInterpolator());
        finalSet.playSequentially(animatorSet, animatorSet2);
        finalSet.start();
    }

    public static void animateShowLogo(View view) {
        ObjectAnimator objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f);
        objectAnimatorAlpha.setStartDelay(700);
        objectAnimatorAlpha.setDuration(1000);
        objectAnimatorAlpha.setInterpolator(new LinearInterpolator());
        objectAnimatorAlpha.start();
    }
}
