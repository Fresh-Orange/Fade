package com.sysu.pro.fade;

/**
 * Created by road on 2017/7/10.
 */
public class Const {
    /*
    信息流相关常量
     */
    public static final int HOME_NODE_DEFAULT_LIFE = 60;    //单位：分钟
    public static final int DISCOVER_NODE_DEFAULT_LIFE = 15;    //单位：分钟
    public static final String IP  = "https://sysufade.cn/fade"; //阿里云云服务器地址

    public static final String RONG_CLOUD_BASE_IP  = "http://api.cn.ronghub.com/"; //阿里云云服务器地址

    public static final String BASE_IP  = "https://sysufade.cn/fade_pro/"; //阿里云云服务器地址
    public static final String WEBSOCKET_IP  = "ws://sysufade.cn:8080/fade_pro/webSocketServer"; //阿里云云服务器websocket地址
    //public static final String BASE_IP      = "http://172.18.92.209:8080/fade_pro/"; //本地地址
    //public static final String WEBSOCKET_IP  = "ws://172.18.92.209:8080/fade_pro/webSocketServer"; //本地websocket地址
    /*登录注册 个人信息有关
     */
    public static final String USER_SHARE="login_user"; //存储登录后的用户信息sp数据库

    public static final String LOGIN_TYPE = "login_type";//登录类型  0：账号密码登录   1：第三方登录

    //User字段常量
    public static final String USER_ID      = "user_id";
    public static final String TELEPHONE    = "telephone";
    public static final String FADE_NAME    = "fade_name";
    public static final String NICKNAME     = "nickname";
    public static final String PASSWORD     = "password";
    public static final String SEX           = "sex";
    public static final String HEAD_IMAGE_URL    = "head_image_url";
    public static final String REGISTER_TIME= "register_time";
    public static final String SUMMARY      = "summary";
    public static final String WECHAT_ID    = "wechat_id";
    public static final String WEIBO_ID     = "weibo_id";
    public static final String QQ_ID        = "qq_id";
    public static final String CONCERN_NUM  = "concern_num";
    public static final String FANS_NUM     = "fans_num";
    public static final String AREA          = "area";
    public static final String WALLPAPER_URL = "wallpaper_url";
    public static final String MAIL           = "mail";
    public static final String SCHOOL         = "school";

    //Note字段常量（有与user部分重叠的）
    public static final String NOTE_ID         = "note_id";
    public static final String NOTE_CONTENT    = "note_content";
    public static final String POST_TIME       = "post_time";
    public static final String ISDIE_FANS      = "isDie_fans";
    public static final String ISDIE_STRANGER  = "isDie_stranger";
    public static final String COMMENT_NUM     = "comment_num";
    public static final String RELAY_NUM       = "relay_num";
    public static final String GOOD_NUM        = "good_num";
    public static final String ISRELAY         = "isRelay";
    public static final String POST_AREA        = "post_area";
    public static final String NOTE_ENTITY        = "note_entity";
    //当前用户是否点赞
    public static final String IS_GOOD          = "isGood";

    //是否是点击评论进入的
    public static final String IS_COMMENT       = "isComment";
    public static final String COMMENT_ENTITY       = "commentEntity";

    //note请求有关
    public static final String START        = "start";
    public static final String RESULT       = "result";
    public static final String ANS          = "ans";
    public static final String ERR          = "err";


    //一个帖子包括以下数组
    public static final String IMAGE_LIST   = "image_list";  //图片数组
    public static final String TAG_LIST      = "tag_list";    //标签数组
    public static final String RELAY_LIST    = "relay_list"; //转发链数组
    public static final String COMMENT_LIST   =  "comment_list";//三条热评组成的数组

    //Comment字段常量
    //8月1号加入，评论表
    public static final String COMMENT_ID        ="comment_id";
    public static final String TO_COMMENT_ID      ="to_comment_id";
    public static final String COMMENT_TIME       ="comment_time";
    public static final String COMMENT_CONTENT    ="comment_content";
    public static final String COMMENT_GOOD_NUM   ="comment_good_num";
    public static final String ORIGIN_COMMENT     ="origin_comment";
    public static final String COMMENT_ISGOOD      = "comment_isGood";

    //贴自己点赞表相关
    public static final String GOOD_ID              = "good_id";
    public static final String GOOD_TIME            = "good_time";
    //评论点赞表
    public static final String COMMENT_GOOD_ID    ="comment_good_id";

    //一个图片应该包括以下
    public static final String IMAGE_URL =  "image_url";
    public static final String IMAGE_SIZE = "image_size";
    //新增
    public static final String IMAGE_COORDINATE = "image_coordinate";
    public static final String IMAGE_CUT_SIZE     = "image_cut_size";
    public static final int PAGE_SIZE = 5  ; //页面数量
    public static final int HOME = 1;
    public static final int DISCOVER = 4;
    public static final int MESSAGE = 2;
    public static final int PUBLISH = 3;
    public static final int MY = 5;

    //用于leancloud服务器的常量
    public static final String X_LC_ID = "flQyv4KhLIrCGcTQmKJTIigu-gzGzoHsz";
    public static final String X_LC_KEY = "2qHqzvu8oqNzlcpiPE2zsRQW";
    public static final String SEND_SMS_URL = "https://api.leancloud.cn/1.1/requestSmsCode";
    public static final String CHECK_SMS_URL = "https://api.leancloud.cn/1.1/verifySmsCode/";
    public static final String APP_ID = "flQyv4KhLIrCGcTQmKJTIigu-gzGzoHsz";
    public static final String APP_KEY = "2qHqzvu8oqNzlcpiPE2zsRQW";

    //首页大请求的id_list
    public static final String ID_LIST = "id_list";
    public static final String CODE = "code";
    //续一秒更新数组 顶部下拉刷新用
    public static final String GOOD_NUM_LIST = "good_num_list";

    public static final int PUBLISH_REQUEST_CODE = 1; //发送帖子的请求代号,返回码为1的话则发送成功，返回0的话发送失败
}
