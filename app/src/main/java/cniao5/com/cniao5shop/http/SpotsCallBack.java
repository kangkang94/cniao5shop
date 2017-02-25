package cniao5.com.cniao5shop.http;

import android.content.Context;
import android.content.Intent;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import cniao5.com.cniao5shop.CniaoApplication;
import cniao5.com.cniao5shop.LoginActivity;
import cniao5.com.cniao5shop.utils.ToastUtils;
import dmax.dialog.SpotsDialog;


public abstract class SpotsCallBack<T> extends BaseCallback<T> {


    private  Context mContext;

    private  SpotsDialog mDialog;

    public SpotsCallBack(Context context){

        mContext = context;

        initSpotsDialog();
    }



    private  void initSpotsDialog(){

        mDialog = new SpotsDialog(mContext,"拼命加载中...");

    }

    public  void showDialog(){
        mDialog.show();
    }

    public  void dismissDialog(){
        mDialog.dismiss();
    }


    public void setLoadMessage(int resId){
        mDialog.setMessage(mContext.getString(resId));
    }


    @Override
    public void onFailure(Request request, Exception e) {
        dismissDialog();
    }

    @Override
    public void onBeforeRequest(Request request) {

        showDialog();
    }

    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }

    //验证失败时调用
    @Override
    public void onTokenError(Response response, int code) {

        ToastUtils.show(mContext, "验证失败，请重新调用");

        //跳转到登陆页面，用户信息删除
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);

        CniaoApplication.getmInstance().clearUser();


    }
}
