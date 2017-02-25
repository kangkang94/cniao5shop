package cniao5.com.cniao5shop;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.utils.CartProvider;
import cniao5.com.cniao5shop.widget.CnToolbar;
import dmax.dialog.SpotsDialog;

/**
 * Created by kang on 16/10/22.
 */
public class WareDetailActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.toolbar)
    private CnToolbar mToolbar;

    @ViewInject(R.id.webView)
    private WebView mWebView;

    //Ware封装好的bean对象，用于存储商品信息
    private Wares wares;

    //只要有添加到购物车的功能就要有cartProvider类
    private CartProvider cartProvider;

    private SpotsDialog spotsDialog;


    /**
     * 常常定义了类的引用变量，但是常常会忘记初始化，需要对其new 构造器初始化或者 this.webAppInterface = webAppInterface,
     * 即从方法的实参中赋予
     */
     private  WebAppInterface webAppInterface;

    //继承了AppCompatActivity，必须实现onCreate方法（生命周期）
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先绑定布局文件，在通过注解工具ViewUtils把this绑定到各个组件中
        setContentView(R.layout.activity_ware_detail);

        ViewUtils.inject(this);

        cartProvider = new CartProvider(this);
        //页面加载时使用此dialog，当页面加载完成时则让其消失
        spotsDialog = new SpotsDialog(this,"正在加载中。。。。");
        spotsDialog.show();

        initToolBar();

        //对WebView进行设置
        initWebView();

        Serializable serializable = getIntent().getSerializableExtra(Constants.WARE);
        //如果serializable为空，则直接杀死此activity
        if (serializable ==null)
            this.finish();

        wares = (Wares) serializable;



    }


    private void initToolBar(){

        //点击navigation的button当前activity被回收
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WareDetailActivity.this.finish();
            }
        });

        //如果找不到看看是不是button默认是VISIBLE
        mToolbar.getRightButton().setVisibility(View.VISIBLE);
        mToolbar.setRightButtonText("分享");
        mToolbar.setRightButtonOnClickListener(this);



    }
    @Override
    public void onClick(View v) {
        showShare();

    }

    private void showShare() {

        //初始化sdk
        ShareSDK.initSDK(this);

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(wares.getName());
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(wares.getImgUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    //当activity处于生命周期destroy时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK(this);
    }

    private void initWebView(){

        //WebSettings是webkit的一个类，该类能对WebView进行属性设置
        WebSettings settings = mWebView.getSettings();
        //设置可以调用js代码
        settings.setJavaScriptEnabled(true);
        //对网路图片进行阻塞->false
        settings.setBlockNetworkImage(false);
        //设置能够缓存
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Constants.API.WARES_DETAIL);

        webAppInterface = new WebAppInterface(this);
        /**
         * 通过android的WebAppInterface类与js连接，js通过调用WebAppInterface的后台逻辑，即类的方法
         */
        mWebView.addJavascriptInterface(webAppInterface,"appInterface");
        //对WebView设置WebViewClient的子类
        mWebView.setWebViewClient(new WC());


    }

    /**
     *  1.这个类用于android调用Javascript
     *  2.需要去了解俩个类 :WebViewClient,WebChromeClient
     */

    class  WC extends WebViewClient{

        //当h5的页面加载完毕后调用此方法
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //页面加载时使用此dialog，当页面加载完成时则让其消失
            if(spotsDialog !=null && spotsDialog.isShowing())
                spotsDialog.dismiss();
            webAppInterface.showDetail();


        }
    }

    //class类，android与js交互的接口
    class WebAppInterface{

        private Context mContext;

        public  WebAppInterface(Context context){

            mContext = context;

        }

        //这个应该是android调用js的方法
        @JavascriptInterface
        public  void showDetail(){

            //android调用js方法必在主线程当中
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mWebView.loadUrl("javascript:showDetail("+wares.getId()+")");


                }
            });



        }

        //js中调用buy方法,每个方法都得加上JavailablescriptInterface
        @JavascriptInterface
        public  void buy(long id){

            cartProvider.put(wares);
            Toast.makeText(mContext, "已添加到购物车", Toast.LENGTH_LONG).show();


        }

        //js中调用buy方法
        @JavascriptInterface
        public void addFavorites(long id){


        }

    }











}
