package cniao5.com.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.utils.CartProvider;
import cniao5.com.cniao5shop.utils.ToastUtils;

/**
 * Created by kang on 16/10/11.
 */

/**
 * HotFragment的adapter
 */
public class HWAdatper extends  SimpleAdapter<Wares> {

    private CartProvider provider;

    public HWAdatper(Context context, List<Wares> datas) {

        super(context, R.layout.template_hot_wares, datas);
        provider = new CartProvider(context);

    }

    @Override
    public void convert(BaseViewHolder viewHolder, final Wares wares) {

        SimpleDraweeView draweeView = (SimpleDraweeView) viewHolder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        viewHolder.getTextView(R.id.text_title).setText(wares.getName());
        viewHolder.getTextView(R.id.text_price).setText("$"+wares.getPrice());

        Button button =viewHolder.getButton(R.id.btn_add);


        if (button !=null){

            //点击Button将Ware送入购物车（存入本地中)
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    provider.put(wares);

                    ToastUtils.show(context,"已添加到购物车");


                }
            });
        }




    }


    //重新更改adapter的layout布局
    public void resetLayout(int layoutId){

        this.layoutResId = layoutId;
        notifyItemRangeChanged(0,getDatas().size());


    }





}
