package cniao5.com.cniao5shop.msg;

import cniao5.com.cniao5shop.bean.Charge;

/**
 * Created by kang on 16/11/16.
 */
public class OrderRespMsg {

    private String orderNum;
    private Charge charge;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }


}
