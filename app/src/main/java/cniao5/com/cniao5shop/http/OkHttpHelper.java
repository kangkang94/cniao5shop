package cniao5.com.cniao5shop.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cniao5.com.cniao5shop.CniaoApplication;


public class OkHttpHelper {



    public static final int TOKEN_MISSING=401;// token 丢失
    public static final int TOKEN_ERROR=402; // token 错误
    public static final int TOKEN_EXPIRE=403; // token 过期



        public static final String TAG="OkHttpHelper";

        private  static  OkHttpHelper mInstance;
        private OkHttpClient mHttpClient;
        private Gson mGson;

        private Handler mHandler;



        static {
            mInstance = new OkHttpHelper();
        }

        private OkHttpHelper(){

            mHttpClient = new OkHttpClient();
            mHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            mHttpClient.setReadTimeout(10,TimeUnit.SECONDS);
            mHttpClient.setWriteTimeout(30,TimeUnit.SECONDS);

            mGson = new Gson();

            mHandler = new Handler(Looper.getMainLooper());

        }

        public static  OkHttpHelper getInstance(){
            return  mInstance;
        }


    /**
     * 代码重构时，对外提供了一个新方法，传入map键值对，拼接url
     * @param url
     * @param param
     * @param callback
     */

        public void get(String url,Map<String,String> param,BaseCallback callback){


            Request request = buildGetRequest(url,param);

            request(request,callback);

        }
    //重参，尽可能地多提供外接多种参数的方法
    public void get(String url,BaseCallback callback){

            get(url,null,callback);

    }



    public void post(String url,Map<String,String> param, BaseCallback callback){

            Request request = buildPostRequest(url, param);
            request(request,callback);

        }





        public  void request(final Request request,final  BaseCallback callback){

            callback.onBeforeRequest(request);

            mHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    callbackFailure(callback, request, e);

                }

                @Override
                public void onResponse(Response response) throws IOException {

//                    callback.onResponse(response);
                    callbackResponse(callback, response);
                    //response.isSuccessful()是通过状态码code>200&&code<300确定
                    if (response.isSuccessful()) {

                        String resultStr = response.body().string();

                        Log.d(TAG, "result=" + resultStr);

                        if (callback.mType == String.class) {
                            callbackSuccess(callback, response, resultStr);
                        } else {
                            try {

                                Object obj = mGson.fromJson(resultStr, callback.mType);
                                callbackSuccess(callback, response, obj);
                            } catch (com.google.gson.JsonParseException e) { // Json解析的错误
                                callback.onError(response, response.code(), e);
                            }
                        }
                    }else if (response.code() ==TOKEN_MISSING||response.code() ==TOKEN_ERROR||response.code() ==TOKEN_EXPIRE){

                        callbackTokenError(callback,response);

                    }else {
                        callbackError(callback, response, null);
                    }

                }
            });


        }


    private void callbackTokenError(final  BaseCallback callback , final Response response ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onTokenError(response,response.code());
            }
        });
    }



    private void callbackSuccess(final  BaseCallback callback , final Response response, final Object obj ){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(response, obj);
                }
            });
        }


        private void callbackError(final  BaseCallback callback , final Response response, final Exception e ){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(response, response.code(), e);
                }
            });
        }



    private void callbackFailure(final  BaseCallback callback , final Request request, final IOException e ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }


    private void callbackResponse(final  BaseCallback callback , final Response response ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }



    private  Request buildPostRequest(String url,Map<String,String> params){

            return  buildRequest(url,HttpMethodType.POST,params);
        }

        private  Request buildGetRequest(String url,Map<String,String> params){

            return  buildRequest(url,HttpMethodType.GET,params);
        }

        private  Request buildRequest(String url,HttpMethodType methodType,Map<String,String> params){


            Request.Builder builder = new Request.Builder()
                    .url(url);

            if (methodType == HttpMethodType.POST){
                RequestBody body = builderFormData(params);
                builder.post(body);
            }
            else if(methodType == HttpMethodType.GET){

                url = buildParamsUrl(url,params);

                builder.url(url);

                builder.get();
            }


            return builder.build();
        }


    /**
     * url 后面有时候会跟上一些参数，?key=value&key1=value1
     * 传入Map<String,String>,循环添加给一个字符串，最后拼接为url
     *
     */
    private  String buildParamsUrl(String url,Map<String,String> params){

        /**
         * 先拼接后面的键值对，在拼接到url上，最后形成新的url
         */
        if (params == null){
            //此例子指url之前没有参数，但是需要初始化
            params = new HashMap<>(1);
        }

        String token = CniaoApplication.getmInstance().getToken();

        if (TextUtils.isEmpty(token)){

            params.put("token",token);
        }

        //字符串拼接时使用，常用append（）方法
        StringBuilder sb = new StringBuilder();

        //params.entrySet指的是整个Map，Map.Entry<String,String>指的是每一个key-value对
        for (Map.Entry<String,String> map : params.entrySet()){

            sb.append(map.getKey()+"="+map.getValue());
            sb.append("&");

        }
        //把StringBuilder转换为String，去掉最后的“&”
        String s = sb.toString();

        if (s.endsWith("&")){
            s = s.substring(0,s.length()-1);
        }

        if (url.indexOf("?")>0){

            url = url +"&"+s;

        }else {
            url = url+"?"+s;
        }



        return  url;
    }







        private RequestBody builderFormData(Map<String,String> params){


            FormEncodingBuilder builder = new FormEncodingBuilder();

            if(params !=null){

                for (Map.Entry<String,String> entry :params.entrySet() ){

                    builder.add(entry.getKey(),entry.getValue());
                }
            }

            /**
             * 1.要实现Token：是否登录，若登录了则有新的api权限。所以在POST方法中需要加入Token键值对
             * 2.登录时返回User信息和Token值，通过全局保存到本地，从本地中取出Token，加入POST方法中的键值对
             */
            String token = CniaoApplication.getmInstance().getToken();
            //
            if (!TextUtils.isEmpty(token))
                builder.add("token",token);


            return  builder.build();

        }



        enum  HttpMethodType{

            GET,
            POST,

        }



}
