package com.sysu.pro.fade.home.view;

import android.content.Context;
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
	FixBugViewpager pager;
	private imageAdaptiveIndicativeLayout imageLayout;
	public ImageOnlyHolder(View itemView) {
		super(itemView);
		pager = (FixBugViewpager) itemView.findViewById(R.id.pager);
		imageLayout = (imageAdaptiveIndicativeLayout)itemView.findViewById(R.id.image_layout);
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

			//TODO
			float randomH = (float) (Math.random() + 0.8);
			imageLayout.setViewPagerMaxHeight(500);
			imageLayout.setHeightByRatio(randomH);
			imageLayout.setPaths(bean.getImgUrls());
		}
	}
}
