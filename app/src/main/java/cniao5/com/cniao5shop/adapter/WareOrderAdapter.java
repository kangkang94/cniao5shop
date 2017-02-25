package cniao5.com.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.ShoppingCart;

/**
 * Created by kang on 16/11/15.
 */
public class WareOrderAdapter extends  SimpleAdapter<ShoppingCart> {


    public WareOrderAdapter(Context context,  List<ShoppingCart> datas) {
        super(context, R.layout.template_order_wares, datas);


    }

    @Override
    protected void convert(BaseViewHolder viewHoder, ShoppingCart item) {

        //在convert中把布局文件中的id转换为图片显示出来
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));


    }


    //从ShoppingCart中得到数据

    public float getTotalPrice(){

        float sum=0;
        if(!isNull())
            return sum;

        for (ShoppingCart cart:
                datas) {

            sum += cart.getCount()*cart.getPrice();
        }

        return sum;

    }


    private boolean isNull(){

        return (datas !=null && datas.size()>0);
    }






}
