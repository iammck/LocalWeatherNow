package com.mck.quicktemps.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mck.quicktemps.R;

import java.util.List;

/**
 * Custom animations are handled here.
 * Created by Michael on 7/8/2016.
 */
public class WeatherViewItemAnimator extends DefaultItemAnimator {
    private static final String TAG = "WeatherItemAnimator";

    public WeatherViewItemAnimator(){
        super();
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

    // TODO will want to go back through and alter for existing animations
    @Override
    public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        if (newHolder instanceof WeatherViewHolder && ((WeatherItemInfoHolder) preInfo).isClickAnimation){
            final WeatherViewHolder weatherViewHolder = (WeatherViewHolder) newHolder;

            // use the height of the weatherViewHolder to get the height of the detailView.
            int holderViewWidth = weatherViewHolder.itemView.getWidth();
            int holderViewHeight = weatherViewHolder.itemView.getHeight();
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewWidth, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewHeight, View.MeasureSpec.AT_MOST);
            final View detailView = newHolder.itemView.findViewById(R.id.layoutDetails);
            detailView.measure(widthMeasureSpec, heightMeasureSpec);
            int detailViewHeight = detailView.getMeasuredHeight();

            ValueAnimator heightAnimator;
            // if the the details view is visible, then contracting.
            if (weatherViewHolder.detailsAreVisible){
                // remove room used by the details view
                heightAnimator = ValueAnimator.ofInt(
                        holderViewHeight, holderViewHeight - detailViewHeight);

                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        newHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                        if (newHolder.itemView.getParent() != null) newHolder.itemView.getParent().requestLayout();
                    }
                });
                // need to listen for end of animation and set visibility to gone.
                heightAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        detailView.setVisibility(View.GONE);
                        weatherViewHolder.detailsAreVisible = !weatherViewHolder.detailsAreVisible;
                        newHolder.itemView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                        dispatchChangeFinished(newHolder, true);
                    }
                });
            } else {
                // add room for the details view
                heightAnimator = ValueAnimator.ofInt(
                        holderViewHeight, holderViewHeight + detailViewHeight);

                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        newHolder.itemView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                        if (newHolder.itemView.getParent() != null) newHolder.itemView.getParent().requestLayout();
                    }
                });
                // need to listen for end of animation and set visibility to visible.
                heightAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        detailView.setVisibility(View.VISIBLE);
                        weatherViewHolder.detailsAreVisible = !weatherViewHolder.detailsAreVisible;
                        newHolder.itemView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                        dispatchChangeFinished(newHolder, true);
                    }
                });
            }
            heightAnimator.start();
            return true;
        } else
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    private class WeatherItemInfoHolder extends ItemHolderInfo{
        boolean isClickAnimation;
    }
}
