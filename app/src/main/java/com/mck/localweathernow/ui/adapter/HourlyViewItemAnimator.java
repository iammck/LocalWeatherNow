package com.mck.localweathernow.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mck.localweathernow.R;

import java.util.List;

/**
 * Custom animations are handled here.
 * Created by Michael on 7/8/2016.
 */
public class HourlyViewItemAnimator extends DefaultItemAnimator {
    private static final String TAG = "HourlyAnimator";

    public HourlyViewItemAnimator(){
        super();
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new HourlyItemInfoHolder();
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(
            @NonNull RecyclerView.State state,
            @NonNull RecyclerView.ViewHolder viewHolder,
            int changeFlags, @NonNull List<Object> payloads) {

        HourlyItemInfoHolder infoHolder = (HourlyItemInfoHolder)
                super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        infoHolder.isClickAnimation = payloads.contains(HourlyViewHolder.ON_CLICK);
        return infoHolder;
    }

    // TODO will want to go back through and alter for existing animations
    @Override
    public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder,
                                 @NonNull final RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        if (newHolder instanceof HourlyViewHolder && ((HourlyItemInfoHolder) preInfo).isClickAnimation){
            final HourlyViewHolder hourlyViewHolder = (HourlyViewHolder) newHolder;

            // use the height of the hourlyViewHolder to get the height of the detailView.
            int holderViewWidth = hourlyViewHolder.itemView.getWidth();
            int holderViewHeight = hourlyViewHolder.itemView.getHeight();
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewWidth, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(holderViewHeight, View.MeasureSpec.AT_MOST);
            final View detailView = newHolder.itemView.findViewById(R.id.layoutDetails);
            detailView.measure(widthMeasureSpec, heightMeasureSpec);
            int detailViewHeight = detailView.getMeasuredHeight();

            ValueAnimator heightAnimator;
            // if the the details view is visible, then contracting.
            if (hourlyViewHolder.detailsAreVisible){
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
                        hourlyViewHolder.detailsAreVisible = !hourlyViewHolder.detailsAreVisible;
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
                        hourlyViewHolder.detailsAreVisible = !hourlyViewHolder.detailsAreVisible;
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

    private class HourlyItemInfoHolder extends ItemHolderInfo{
        boolean isClickAnimation;
    }
}
