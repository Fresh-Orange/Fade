package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中仅有图片的item的ViewHolder
 */

public class ImageOnlyHolder extends HomeBaseViewHolder{
	ViewPager pager;
	private imageAdaptiveIndicativeItemLayout imageLayout;
	public ImageOnlyHolder(View itemView) {
		super(itemView);
		pager = (ViewPager) itemView.findViewById(R.id.pager);
		imageLayout = (imageAdaptiveIndicativeItemLayout)itemView.findViewById(R.id.image_layout);
	}

	@Override
	public void bindView(final Context context, Handler handler, List<Note> data, int  position){
		super.bindView(context, handler, data, position);

		final Note bean = data.get(position);

		if (data != null && data.size() > 0) {
			/**
			 * Whenever you need to dynamically add pagers, you need to set an ID for each pager using ViewPager.setId().
			 * from : https://stackoverflow.com/questions/14920459/placing-viewpager-as-a-row-in-listview
			 */
			pager.setId(++RecycleAdapter.viewPagerTag);

			double RatioMax = 999;
			for (double d:bean.getImgSizes()) {
				Log.d("Ratio", " "+d);
				RatioMax = RatioMax < d ? RatioMax : d;
			}
			imageLayout.setViewPagerMaxHeight(600);
			//imageLayout.setHeightByRatio(((float) (1.0/RatioMax)));
			imageLayout.setHeightByRatio(1);
			imageLayout.setPaths(bean.getImgUrls());
		}
	}
}
