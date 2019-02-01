package com.underscoretec.reeflix;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * @author S.Shahini
 * @since 2/12/18
 */

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide("https://d2ve01upp8znde.cloudfront.net/images/banner1.jpg");
                break;
            case 1:
                viewHolder.bindImageSlide("https://d2ve01upp8znde.cloudfront.net/images/banner2.jpg");
                break;
            case 2:
                viewHolder.bindImageSlide("https://d2ve01upp8znde.cloudfront.net/images/banner3.jpg");
                break;
            case 3:
                viewHolder.bindImageSlide("https://d2ve01upp8znde.cloudfront.net/images/banner4.jpg");
                break;
        }
    }
}
