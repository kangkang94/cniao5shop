package cniao5.com.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;

import cniao5.com.cniao5shop.CniaoApplication;
import cniao5.com.cniao5shop.Constants;
import cniao5.com.cniao5shop.LoginActivity;
import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.User;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Ivan on 15/9/22.
 */
public class MineFragment extends BaseFragment{


    @ViewInject(R.id.img_head)
    private CircleImageView mImageHead;

    @ViewInject(R.id.txt_username)
    private TextView mTxtUserName;

    @ViewInject(R.id.btn_logout)
    private Button mbtnLogout;




    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return  inflater.inflate(R.layout.fragment_mine,container,false);
    }




    //每次初始化都得得到用户信息，将头像数据和昵称给初始化
    @Override
    public void init() {

        User user = CniaoApplication.getmInstance().getUser();
        showUser(user);

    }


    //对头像和点击登录做监听事件，当点击俩者时跳到登录界面
    @OnClick(value={R.id.img_head,R.id.txt_username})
    public void toLogin(View view){

        //如果未登录，则点击登录对头像和点击登录做监听事件，当点击俩者时跳到登录界面
        if (CniaoApplication.getmInstance().getUser() == null){

            Intent intent = new Intent(getActivity(), LoginActivity.class);
           //采用有返回的跳转
            startActivityForResult(intent, Constants.REQUEST_CODE);

        }

    }


    //退出登陆时，把User信息清楚
    @OnClick(R.id.btn_logout)
    public  void logout(View view){

        CniaoApplication.getmInstance().clearUser();
        showUser(null);


    }






    //对返回消息的处理,Intent代表上一个activity返回的数据，这里把数据保存到本地了。
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //得到User信息，显示头像和昵称
        User user = CniaoApplication.getmInstance().getUser();
        showUser(user);


    }

    private void showUser(User user) {


        if (user!=null){

            if (!TextUtils.isEmpty(user.getLogo_url())){
               //用Picasso加载图片
                Picasso.with(getActivity()).load(user.getLogo_url()).into(mImageHead);
            }

            mTxtUserName.setText(user.getUsername());
            mbtnLogout.setVisibility(View.VISIBLE);

        }else {
            mTxtUserName.setText(R.string.to_login);
            mbtnLogout.setVisibility(View.GONE);
        }

    }




}
