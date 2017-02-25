package cniao5.com.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;

import cniao5.com.cniao5shop.CniaoApplication;
import cniao5.com.cniao5shop.LoginActivity;
import cniao5.com.cniao5shop.bean.User;


/**
 *通过继承BaseFragment，让每个Fragment都实现BaseFragment的方法
 */
public abstract class BaseFragment extends Fragment {





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = createView(inflater,container,savedInstanceState);
        ViewUtils.inject(this, view);

        initToolBar();

        init();

        return view;

    }

    public void  initToolBar(){

    }


    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void init();



    public void startActivity(Intent intent,boolean isNeedLogin){

        /**
         *  判断是否需要登陆，如果需要登陆则先取出User信息，
         *  如无User信息则把Intent信息保存到全局，跳转到登陆activity
         */

        if(isNeedLogin){

            User user =CniaoApplication.getmInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{

                CniaoApplication.getmInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }


}
