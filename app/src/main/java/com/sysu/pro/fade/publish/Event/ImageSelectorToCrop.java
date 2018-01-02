package com.sysu.pro.fade.publish.Event;

import java.util.ArrayList;

/**
 * Created by yellow on 2017/12/31.
 */

public class ImageSelectorToCrop {
    private ArrayList<String> images;
    private int count;
    public ImageSelectorToCrop(ArrayList<String> images, int count) {
        this.images = images;
        this.count = count;
    }

    public ArrayList<String> getImages() {
        return images;
    }


    public int getCount() {
        return count;
    }
}
