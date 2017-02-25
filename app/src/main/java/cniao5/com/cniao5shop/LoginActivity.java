package cniao5.com.cniao5shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Map;

import cniao5.com.cniao5shop.bean.User;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.http.SpotsCallBack;
import cniao5.com.cniao5shop.msg.LoginRespMsg;
import cniao5.com.cniao5shop.utils.DESUtil;
import cniao5.com.cniao5shop.utils.ToastUtils;
import cniao5.com.cniao5shop.widget.ClearEditText;
import cniao5.com.cniao5shop.widget.CnToolbar;

/**
 * Created by kang on 16/10/23.
 */
public class LoginActivity extends AppCompatActivity {

    @ViewInject(R.id.toolbar)
    private CnToolbar mToolBar;
    //自定义的手机号和密码输入框
    @ViewInject(R.id.etxt_phone)
    private ClearEditText mEtxtPhone;
    @ViewInject(R.id.etxt_pwd)
    private ClearEditText mEtxtPwd;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        initToolBar();


    }

    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });


    }

    //单独获取back键，做操作
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    //当点击登录按钮时，做如下操作
    @OnClick(R.id.btn_login)
    public void login(View view){

        //得到phone和pwd的输入框的字符串，.trim()是指除去前面空格和后面空格得到的字符串
        String phone = mEtxtPhone.getText().toString().trim();
        //判断字符串是否为空
        if (TextUtils.isEmpty(phone)){
            ToastUtils.show(this,"请输入手机号码");
            return;
        }

        String pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)){
            ToastUtils.show(this,"请输入密码");
            return;
        }

        /**
         * 把得到的phone和pwd保存在Map<String,String>里。用Okhttp post网络请求，返回LoginRespMsg<User>值，保存在本地
         */

        Map<String,String> param = new HashMap<>(2);

        param.put("phone",phone);

        //使用DESUtil工具类对密码进行DES加密
        param.put("password", DESUtil.encode(Constants.DES_KEY, pwd));

        okHttpHelper.post(Constants.API.LOGIN, param, new SpotsCallBack<LoginRespMsg<User>>(this) {

            //用post的方法，向服务器提交了param，返回LoginRespMsg<User>信息
            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {


                //将用户信息保存到本地，通过全局方法保存
                CniaoApplication application = CniaoApplication.getmInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                if (application.getIntent() == null) {

                    //上一个activity跳转到此activity，此activity返回信息
                    setResult(RESULT_OK);
                    finish();

                } else {

                    application.jumpToTargetActivity(LoginActivity.this);
                    finish();
                }


            }

            @Override
            public void onError(Response response, int code, Exception e) {


            }


        });











    }

    //需要有View作为形参
    @OnClick(R.id.txt_toReg)
    public void register(View view){

        Intent intent  = new Intent(LoginActivity.this,RegActivity.class);
        startActivity(intent);

    }











}
