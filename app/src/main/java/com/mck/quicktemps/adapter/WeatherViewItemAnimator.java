package com.mck.quicktemps.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    //private static final String TAG = "WeatherItemAnimator";
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
            AnimationInfo prevAnimationInfo = animationMap.get(newHolder);
            if (prevAnimationInfo != null){
                prevAnimationInfo.animator.cancel();
                prevAnimationInfo.animator.getCurrentPlayTime();
            }
            // if the the details view is visible, then contracting.
            if (((WeatherViewHolder) newHolder).detailsAreVisible) {
                return removeDetails((WeatherViewHolder) newHolder, prevAnimationInfo);
            } else { // details are not visible, make them visible
                return addDetails((WeatherViewHolder) newHolder, prevAnimationInfo);
            }
        } else
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private boolean removeDetails(final WeatherViewHolder weatherViewHolder, AnimationInfo prevAnimationInfo) {
        final ValueAnimator heightAnimator;
        // need to get the holderViewHeight, mainInfoViewHeight and and detailViewHeight
        int holderViewHeight = weatherViewHolder.mView.getHeight();
        final int mainInfoViewHeight;
        final int detailViewHeight;
        // if there is prevAnimationInfo then use those heights.
        if (prevAnimationInfo != null){
            mainInfoViewHeight = prevAnimationInfo.mainInfoViewHeight;
            detailViewHeight = prevAnimationInfo.detailViewHeight;
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

        // remove room used by the details view from current height to main view height
        heightAnimator = ValueAnimator.ofInt( holderViewHeight, 32 + mainInfoViewHeight);
        heightAnimator.setDuration(700);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                weatherViewHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                if (weatherViewHolder.itemView.getParent() != null) weatherViewHolder.itemView.getParent().requestLayout();
            }
        });
        // need to listen for end of animation and set visibility to gone.
        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animationMap.put(weatherViewHolder, new AnimationInfo(heightAnimator, mainInfoViewHeight, detailViewHeight));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                weatherViewHolder.layoutDetails.setVisibility(View.GONE);
                weatherViewHolder.detailsAreVisible = false;
                weatherViewHolder.itemView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                animationMap.remove(weatherViewHolder);
                dispatchChangeFinished(weatherViewHolder, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                weatherViewHolder.detailsAreVisible = false;
                animationMap.remove(weatherViewHolder);
                animation.removeListener(this);
            }
        });

        heightAnimator.start();
        return true;

    }

    private boolean addDetails(final WeatherViewHolder weatherViewHolder, AnimationInfo prevAnimationInfo) {
        final ValueAnimator heightAnimator;
        // need to get the holderViewHeight and and detailViewHeight
        int holderViewHeight = weatherViewHolder.mView.getHeight();
        final int mainInfoViewHeight;
        final int detailViewHeight;
        // if there is prevAnimationInfo then use those heights.
        if (prevAnimationInfo != null){
            mainInfoViewHeight = prevAnimationInfo.mainInfoViewHeight;
            detailViewHeight = prevAnimationInfo.detailViewHeight;
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

        // set up animation.
        heightAnimator = ValueAnimator.ofInt( holderViewHeight, 32 + mainInfoViewHeight + detailViewHeight);
        heightAnimator.setDuration(700);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                weatherViewHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                if (weatherViewHolder.itemView.getParent() != null) weatherViewHolder.itemView.getParent().requestLayout();
            }
        });
        // need to listen for end of animation and set visibility to visible.
        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animationMap.put(weatherViewHolder, new AnimationInfo(heightAnimator, mainInfoViewHeight, detailViewHeight));
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                weatherViewHolder.layoutDetails.setVisibility(View.VISIBLE);
                weatherViewHolder.detailsAreVisible = true;
                weatherViewHolder.itemView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                animationMap.remove(weatherViewHolder);
                dispatchChangeFinished(weatherViewHolder, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                weatherViewHolder.detailsAreVisible = true;
                animationMap.remove(weatherViewHolder);
                animation.removeListener(this);
            }
        });
        heightAnimator.start();
        return true;
    }


    private class WeatherItemInfoHolder extends ItemHolderInfo{
        boolean isClickAnimation;
    }

    private class AnimationInfo {
        ValueAnimator animator;
        int detailViewHeight;
        int mainInfoViewHeight;

        AnimationInfo(ValueAnimator animator, int mainInfoViewHeight, int detailViewHeight){
            this.animator = animator;
            this.mainInfoViewHeight = mainInfoViewHeight;
            this.detailViewHeight = detailViewHeight;
        }
    }


}
