package com.mck.quicktemps.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * Custom animations are handled here.
 * Created by Michael on 7/8/2016.
 */
public class WeatherViewItemAnimator extends DefaultItemAnimator {
    private static final long HEIGHT_ANIMATION_DURATION = 400;
    //private static final String TAG = "WeatherItemAnimator";
    private static final long TEXT_FADE_IN_DURATION = 750;
    private static final long TEXT_FADE_OUT_DURATION = 250;
    private HashMap<RecyclerView.ViewHolder, AnimationInfo> animationMap;
    public WeatherViewItemAnimator(){
        super();
        animationMap = new HashMap<>();
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new WeatherItemInfoHolder();
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(
            @NonNull RecyclerView.State state,
            @NonNull RecyclerView.ViewHolder viewHolder,
            int changeFlags, @NonNull List<Object> payloads) {

        WeatherItemInfoHolder infoHolder = (WeatherItemInfoHolder)
                super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        infoHolder.isClickAnimation = payloads.contains(WeatherViewHolder.ON_CLICK);
        return infoHolder;
    }

    @Override
    public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        if (newHolder instanceof WeatherViewHolder && ((WeatherItemInfoHolder) preInfo).isClickAnimation){
            final WeatherViewHolder weatherViewHolder = (WeatherViewHolder) newHolder;
            final ValueAnimator heightAnimator;
            ValueAnimator textAnimator;

            AnimationInfo prevAnimationInfo = animationMap.get(newHolder);
            if (prevAnimationInfo != null){
                prevAnimationInfo.animatorSet.cancel();
            }

            // need to get the holderViewHeight, mainInfoViewHeight and and detailViewHeight
            int holderViewHeight = weatherViewHolder.mView.getHeight();
            final int mainInfoViewHeight;
            final int detailViewHeight;
            // will also need the detail view alpha
            float detailViewAlpha = -1;

            // if there is prevAnimationInfo then use those heights and starting alpha.
            if (prevAnimationInfo != null){
                mainInfoViewHeight = prevAnimationInfo.mainInfoViewHeight;
                detailViewHeight = prevAnimationInfo.detailViewHeight;
                detailViewAlpha = weatherViewHolder.layoutDetails.getAlpha();
            } else {
                // use the height of the weatherViewHolder to get mainViewHeight and detailViewHeight.
                int holderViewWidth = weatherViewHolder.mView.getWidth();
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewHeight, View.MeasureSpec.UNSPECIFIED);

                View mainInfoView = weatherViewHolder.layoutMainInfo;
                mainInfoView.measure(widthMeasureSpec, heightMeasureSpec);
                mainInfoViewHeight = mainInfoView.getMeasuredHeight();

                View detailView = weatherViewHolder.layoutDetails;
                detailView.measure(widthMeasureSpec, heightMeasureSpec);
                detailViewHeight = detailView.getMeasuredHeight();
            }

            final AnimatorSet animatorSet = new AnimatorSet();
            // if the the details view is visible, then contracting.
            if (((WeatherViewHolder) newHolder).detailsAreVisible) {
                heightAnimator = getRemoveDetailsAnimator(
                        (WeatherViewHolder) newHolder, holderViewHeight, mainInfoViewHeight);
                textAnimator = getHideTextAnimator((WeatherViewHolder) newHolder, detailViewAlpha);
                animatorSet.playTogether(textAnimator, heightAnimator);
            } else { // details are not visible, make them visible
                heightAnimator = getAddDetailsAnimator(
                        (WeatherViewHolder) newHolder, holderViewHeight, mainInfoViewHeight, detailViewHeight);
                textAnimator = getShowTextAnimator((WeatherViewHolder) newHolder, detailViewAlpha);
                animatorSet.playTogether(heightAnimator, textAnimator);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    animatorSet.removeListener(this);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    weatherViewHolder.detailsAreVisible = !weatherViewHolder.detailsAreVisible;
                    weatherViewHolder.itemView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    animationMap.remove(weatherViewHolder);
                    dispatchChangeFinished(weatherViewHolder, true);
                }
            });
            animationMap.put(weatherViewHolder, new AnimationInfo(animatorSet, mainInfoViewHeight, detailViewHeight));
            animatorSet.start();
            return true;
        } else
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private ValueAnimator getRemoveDetailsAnimator(
            final WeatherViewHolder weatherViewHolder,
            int holderViewHeight, final int mainInfoViewHeight) {

        // remove room used by the details view from current height to main view height
        final ValueAnimator heightAnimator = ValueAnimator.ofInt( holderViewHeight, 32 + mainInfoViewHeight);
        heightAnimator.setDuration(HEIGHT_ANIMATION_DURATION);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                weatherViewHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                if (weatherViewHolder.itemView.getParent() != null) weatherViewHolder.itemView.getParent().requestLayout();
            }
        });
        return heightAnimator;
    }

    private ValueAnimator getAddDetailsAnimator(
            final WeatherViewHolder weatherViewHolder,
            int holderViewHeight, final int mainInfoViewHeight, final int detailViewHeight) {
        // set up animation.
        final ValueAnimator heightAnimator = ValueAnimator.ofInt( holderViewHeight, 32 + mainInfoViewHeight + detailViewHeight);
        heightAnimator.setDuration(HEIGHT_ANIMATION_DURATION);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                weatherViewHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                if (weatherViewHolder.itemView.getParent() != null) weatherViewHolder.itemView.getParent().requestLayout();
            }
        });
        return heightAnimator;
    }

    private ObjectAnimator getShowTextAnimator(final WeatherViewHolder newHolder, float detailTextAlpha) {
        float startValue = (detailTextAlpha == -1)? 0: detailTextAlpha;
        ObjectAnimator result = ObjectAnimator.ofFloat(newHolder.layoutDetails, "alpha", startValue, 1);
        result.setDuration(TEXT_FADE_IN_DURATION);
        result.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                newHolder.layoutDetails.setVisibility(View.VISIBLE);
            }
        });

        return result;
    }

    private ObjectAnimator getHideTextAnimator(final WeatherViewHolder newHolder, float detailTextAlpha) {
        float startValue = (detailTextAlpha == -1)? 1: detailTextAlpha;
        ObjectAnimator result = ObjectAnimator.ofFloat(newHolder.layoutDetails, "alpha", startValue, 0);
        result.setDuration(TEXT_FADE_OUT_DURATION);
        result.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animation.removeListener(this);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                newHolder.layoutDetails.setVisibility(View.GONE);
            }
        });
        return  result;
    }

    private class WeatherItemInfoHolder extends ItemHolderInfo{
        boolean isClickAnimation;
    }

    private class AnimationInfo {
        AnimatorSet animatorSet;
        int detailViewHeight;
        int mainInfoViewHeight;

        AnimationInfo(AnimatorSet animatorSet, int mainInfoViewHeight, int detailViewHeight){
            this.animatorSet = animatorSet;
            this.mainInfoViewHeight = mainInfoViewHeight;
            this.detailViewHeight = detailViewHeight;
        }

    }


}
