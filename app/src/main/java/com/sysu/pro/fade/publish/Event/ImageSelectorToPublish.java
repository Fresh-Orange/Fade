package com.sysu.pro.fade.publish.Event;

import java.util.ArrayList;

/**
 * Created by yellow on 2017/12/31.
 */

public class ImageSelectorToPublish {
    private int maxSelectcount;
    private ArrayList<String> images;
    private int newCount;
    public ImageSelectorToPublish(int maxSelectcount, ArrayList<String> images, int newCount) {
        this.maxSelectcount = maxSelectcount;
        this.images = images;
        this.newCount = newCount;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public int getMaxSelectcount() {
        return maxSelectcount;
    }

    public int getNewCount() {
        return newCount;
    }
}
