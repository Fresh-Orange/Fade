package com.sysu.pro.fade.emotionkeyboard.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.emotionkeyboard.adapter.HorizontalRecyclerviewAdapter;
import com.sysu.pro.fade.emotionkeyboard.adapter.NoHorizontalScrollerVPAdapter;
import com.sysu.pro.fade.emotionkeyboard.emotionkeyboardview.EmotionKeyboard;
import com.sysu.pro.fade.emotionkeyboard.emotionkeyboardview.NoHorizontalScrollerViewPager;
import com.sysu.pro.fade.emotionkeyboard.model.ImageModel;
import com.sysu.pro.fade.emotionkeyboard.utils.EmotionUtils;
import com.sysu.pro.fade.emotionkeyboard.utils.GlobalOnItemClickManagerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zejian
 * Time  16/1/6 下午5:26
 * Email shinezejian@163.com
 * Description:表情主界面
 */
public class EmotionMainFragment extends BaseFragment{

    //是否绑定当前Bar的编辑框的flag
    public static final String BIND_TO_EDITTEXT="bind_to_edittext";
    //是否隐藏bar上的编辑框和发生按钮
    public static final String HIDE_BAR_EDITTEXT_AND_BTN="hide bar's editText and btn";

    public static final String EMOTION_HIDE="EMOTION_HIDE";

    private FrameLayout frameLayout;

    public static int tag = 0;

    public ImageView imageView;
    //当前被选中底部tab
    private static final String CURRENT_POSITION_FLAG="CURRENT_POSITION_FLAG";
    private int CurrentPosition=0;
    //底部水平tab
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    private boolean isHidden;
    private EditText bar_edit_text;
    private ImageView bar_image_add_btn;
    private Button bar_btn_send;
    private LinearLayout rl_editbar_bg;

    //需要绑定的内容view
    private View contentView;
    private EditText et_emotion;
    public static boolean isEmotion = false;


    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;

    //是否绑定当前Bar的编辑框,默认true,即绑定。
    //false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
    private boolean isBindToBarEditText=true;

    //是否隐藏bar上的编辑框和发生按钮,默认不隐藏
    private boolean isHidenBarEditTextAndBtn=false;

    List<Fragment> fragments=new ArrayList<>();

    private View rootView;

    private View emotionView;
    private ImageView emotion_button;


    /**
     * 创建与Fragment对象关联的View视图时调用
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_emotion, container, false);
        isHidenBarEditTextAndBtn= args.getBoolean(EmotionMainFragment.HIDE_BAR_EDITTEXT_AND_BTN);
        //获取判断绑定对象的参数
        isBindToBarEditText=args.getBoolean(EmotionMainFragment.BIND_TO_EDITTEXT);
        initView(rootView);
        if (contentView == null)
            return null;
        mEmotionKeyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(rootView.findViewById(R.id.ll_emotion_layout))//绑定表情面板
                //防止跳闪
                .bindToContent(contentView)//绑定内容view
                .bindToFrameLayout(frameLayout)
                //点击了EditText的情况下，不同情况的讨论
                .bindToEditText((EditText) contentView)//判断绑定那种EditView
                .bindToRl_editbar_bg(rl_editbar_bg)
                //点击了表情按钮的情况下，不同情况的讨论
                .bindToEmotionButton(emotion_button)//绑定表情按钮
                .build();

        initListener();
        initDatas();
        //创建全局监听

            GlobalOnItemClickManagerUtils globalOnItemClickManager=
                    GlobalOnItemClickManagerUtils.getInstance(getActivity());
            if(isBindToBarEditText){
                //绑定当前Bar的编辑框
                globalOnItemClickManager.attachToEditText(bar_edit_text);

            }else{
                // false,则表示绑定contentView,此时外部提供的contentView必定也是EditText
                globalOnItemClickManager.attachToEditText((EditText) contentView);
                mEmotionKeyboard.bindToEditText((EditText)contentView);
            }
        return rootView;
    }

    /**
     * 绑定内容view
     * @param contentView
     * @return
     */
    public void bindToContentView(View contentView){
        this.contentView=contentView;
    }


    /**
     * 初始化view控件
     */
    protected void initView(View rootView){
        viewPager= (NoHorizontalScrollerViewPager) rootView.findViewById(R.id.vp_emotionview_layout);
        recyclerview_horizontal= (RecyclerView) rootView.findViewById(R.id.recyclerview_horizontal);
    }

    /**
     * 初始化监听器
     */
    protected void initListener(){

    }


    /**
     * 数据操作,这里是测试数据，请自行更换数据
     */
    protected void initDatas(){
        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        ImageModel model1=new ImageModel();
        model1.icon= getResources().getDrawable(R.drawable.ic_emotion);
        model1.flag="经典笑脸";
        model1.isSelected=true;
        list.add(model1);
        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(getActivity(),list);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
    }

    private void replaceFragment(){
        //创建fragment的工厂类
        FragmentFactory factory= FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotionComplateFragment f1= (EmotionComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
//        }

        NoHorizontalScrollerVPAdapter adapter =new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
    }


    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     * @return true则隐藏表情布局，拦截返回键操作
     *         false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress(){
        return mEmotionKeyboard.interceptBackPress();
    }


    public void bindToFramelayout(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }

    public void bindToEmotion(ImageView emotion_button) {
        this.emotion_button = emotion_button;
    }

    public void bindToRl_editbar_bg(LinearLayout rl_editbar_bg) {
        this.rl_editbar_bg = rl_editbar_bg;
    }
}


