package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.home.adapter.RecycleAdapter;
import com.sysu.pro.fade.home.beans.ContentBean;

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
	public void bindView(final Context context, List<ContentBean> data, int  position){
		super.bindView(context, data, position);

		final ContentBean bean = data.get(position);

		if (data != null && data.size() > 0) {
			/**
			 * Whenever you need to dynamically add pagers, you need to set an ID for each pager using ViewPager.setId().
			 * from : https://stackoverflow.com/questions/14920459/placing-viewpager-as-a-row-in-listview
			 */
			pager.setId(++RecycleAdapter.viewPagerTag);

			imageLayout.setViewPagerMaxHeight(500);
			double RatioMax = 0;
			for (double d:bean.getImgSizes()) {
				RatioMax = RatioMax > d ? RatioMax : d;
			}
			imageLayout.setHeightByRatio(((float) RatioMax));
			imageLayout.setPaths(bean.getImgUrls());
		}
	}
}
