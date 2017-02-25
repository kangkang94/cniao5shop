package cniao5.com.cniao5shop.msg;

import java.io.Serializable;

/**
 * Created by kang on 16/10/23.
 */

/**
 *
 */
public class BaseRespMsg implements Serializable{


    public final static int STATUS_SUCCESS=1;
    public final static int STATUS_ERROR=0;
    public final static String MSG_SUCCESS="success";
    //返回消息的状态码
    protected int status =STATUS_SUCCESS;
    //返回消息的状态码说明
    protected String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
