package cniao5.com.cniao5shop.utils;

/**
 * Created by kang on 16/10/24.
 */

import android.content.Context;
import android.text.TextUtils;

import cniao5.com.cniao5shop.Constants;
import cniao5.com.cniao5shop.bean.User;

/**
 * 用于存储User信息和Token字段的工具类
 * 把数据存储到本地可以通过PreferenceUtils工具类，这里直接封装出一个User和Token信息单独使用的工具类
 */
public class UserLocalData {

    //传入User信息，用static修饰是为了类调用
    public static  void putUser(Context context,User user){

        String user_json = JSONUtil.toJSON(user);
        PreferencesUtils.putString(context, Constants.USER_JSON,user_json);

    }

    public static  void putToken(Context context,String token){


        PreferencesUtils.putString(context, Constants.USER_JSON,token);

    }


    //得到User信息
    public static User getUser(Context context){

       String user_json =PreferencesUtils.getString(context, Constants.USER_JSON);
        if (!TextUtils.isEmpty(user_json)){
            //User本身就是一个类，如果是List<User>,则需要用new TypeToken<List<User>>(){}.getType()得到类的信息
            return JSONUtil.fromJson(user_json,User.class);

        }

        return null;
    }




    public static  String getToken(Context context){

        return  PreferencesUtils.getString( context,Constants.TOKEN);

    }

    //清楚User信息
    public static void clearUser(Context context){


        PreferencesUtils.putString(context, Constants.USER_JSON,"");

    }

    public static void clearToken(Context context){

        PreferencesUtils.putString(context, Constants.TOKEN,"");
    }








}
