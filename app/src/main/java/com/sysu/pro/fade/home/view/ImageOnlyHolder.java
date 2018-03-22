package com.sysu.pro.fade.home.view;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.Note;
import com.sysu.pro.fade.home.adapter.NotesAdapter;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/8/2.
 * 信息流中仅有图片的item的ViewHolder
 */

public class ImageOnlyHolder extends HomeBaseViewHolder{
	ViewPager pager;//仅用于设置id
	private imageAdaptiveIndicativeItemLayout imageLayout;
	public ImageOnlyHolder(View itemView) {
		super(itemView);
		pager = (ViewPager) itemView.findViewById(R.id.pager);
		imageLayout = (imageAdaptiveIndicativeItemLayout)itemView.findViewById(R.id.image_layout);
	}

	@Override
	public void bindView(final Activity context, List<Note> data, int  position){
		super.bindView(context, data, position);

		final Note bean = data.get(position);


		if (data != null && data.size() > 0) {
			/**
			 * Whenever you need to dynamically add pagers, you need to set an ID for each pager using ViewPager.setId().
			 * from : https://stackoverflow.com/questions/14920459/placing-viewpager-as-a-row-in-listview
			 */
			pager.setId(++NotesAdapter.viewPagerTag);

			setImagePager(bean, imageLayout);
		}
	}

	public static void setImagePager(Note bean, imageAdaptiveIndicativeItemLayout imageLayout) {
		double ratio = getNoteRatio(bean);
		imageLayout.setViewPagerMaxHeight(600);
		//imageLayout.setHeightByRatio(((float) (1.0/ratio)));
		imageLayout.setImgCoordinates(bean.getImgCoordinates());
		imageLayout.setHeightByRatio((float)(1.0/ratio));
		imageLayout.setPaths(Const.BASE_IP, bean.getImgUrls());
	}

	static private double getNoteRatio(Note bean) {
		double ratio = 1.0;
		if (bean.getImages().size() > 1){
			ratio = 1.0;
		}
		else if (bean.getImages().size() == 1){
			double imgRadio = bean.getImgSizes().get(0);
			if (imgRadio > 2)
				ratio = 2.0;
			else if (imgRadio < 0.75)
				ratio = 0.75;
			else
				ratio = imgRadio;
		}

		/*int cutSize = bean.getImgCutSize();
		if (cutSize == 1)
			ratio = 5.0/4;
		else if (cutSize == 2)
			ratio = 8.0/15;
		else{//USELESS!!
			ratio = 999;
			for (double d:bean.getImgSizes()) {
				Log.d("Ratio", "out "+d);
				ratio = ratio < d ? ratio : d;
			}
		}*/

		return ratio;
	}
}
