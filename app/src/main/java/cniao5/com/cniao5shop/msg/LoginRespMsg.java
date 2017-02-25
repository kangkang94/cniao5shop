package cniao5.com.cniao5shop.msg;

/**
 * Created by kang on 16/10/23.
 */

/**
 * 在类名后面添加<T>,当使用此类时，传入具体泛型对象，成员变量和形参均可使用T
 * @param <T>
 */
public class LoginRespMsg<T> extends  BaseRespMsg {

    private String token;
    private T data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



}
