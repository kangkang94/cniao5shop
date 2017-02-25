package cniao5.com.cniao5shop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import cniao5.com.cniao5shop.bean.User;

/**
 * Created by kang on 16/10/29.
 */
public class BaseActivity extends AppCompatActivity {


    //判断是否需要登陆才能跳转
    public void startActivity(Intent intent,boolean isNeedLogin){

        if (isNeedLogin){

            User user = CniaoApplication.getmInstance().getUser();
            if (user!=null){

                super.startActivity(intent);

            }else {
                CniaoApplication.getmInstance().putIntent(intent);

                Intent loginIntent = new Intent(this,LoginActivity.class);

                super.startActivity(loginIntent);
            }
        }else{

            super.startActivity(intent);

        }

    }


}
