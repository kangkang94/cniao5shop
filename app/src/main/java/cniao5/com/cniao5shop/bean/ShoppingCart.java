package cniao5.com.cniao5shop.bean;

import java.io.Serializable;

/**
 * Created by kang on 16/10/16.
 */

/**
 * ShoppingCart继承Wares，用于存储购物车商品信息
 */
public class ShoppingCart extends Wares implements Serializable {

    private int count;
    private boolean isChecked = true;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}



