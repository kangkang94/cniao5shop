package cniao5.com.cniao5shop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;

import cniao5.com.cniao5shop.bean.User;
import cniao5.com.cniao5shop.utils.UserLocalData;

/**
 *
 * 该类继承了Application，在配置文件中注册，使得该类的信息能在多个activity中共享
 */
public class CniaoApplication extends Application {

    //在多个activity中使用User信息
    private User user;

    //将该类改为单例模式
    private static CniaoApplication mInstance;

    public static CniaoApplication getmInstance(){

        return mInstance;

    }



    @Override
    public void onCreate() {
        super.onCreate();
        //在这里初始化，Application有自己的生命周期，不能new
        mInstance = this;
        initUser();
        Fresco.initialize(this);
    }


    private void initUser() {

        //类调用，所以不用初始化
       this.user = UserLocalData.getUser(this);

    }


    public User getUser(){

        return user;
    }


    //把User对象和token存入本地
    public void putUser(User user,String token){
        this.user = user;
        UserLocalData.putUser(this,user);
        UserLocalData.putToken(this,token);
    }


    public void clearUser(){
        this.user =null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);


    }



    public String getToken(){

        return  UserLocalData.getToken(this);
    }




    /**
     * 存储要跳转但因为未登陆而被拦截的Intent信息，存到全局
     */
    private Intent intent;
    public void putIntent(Intent intent){
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }



    /**
     *  在登陆页面登陆后直接跳转到想要去的activity，然后把保存的Intent赋值为null
    */
    public void jumpToTargetActivity(Context context){

        context.startActivity(intent);
        this.intent =null;

    }




}
