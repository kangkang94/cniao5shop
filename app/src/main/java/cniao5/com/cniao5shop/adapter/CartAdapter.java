package cniao5.com.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Iterator;
import java.util.List;

import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.ShoppingCart;
import cniao5.com.cniao5shop.utils.CartProvider;
import cniao5.com.cniao5shop.widget.NumberAddSubView;

/**
 * Created by kang on 16/10/16.
 */
public class CartAdapter extends SimpleAdapter<ShoppingCart> implements BaseAdapter.OnItemClickListener {


    /**
     * 数据传入了Adapter，所以我们在Adapter中进行操作数据；
     * 由于需要使用到Checkbox和TextView，所以必须从构造器中传入俩个组件
     */


    private CheckBox mcheckBox;
    private TextView mtextView;

    private  List<ShoppingCart> datas;

    //一定别忘记初始化，若忘记初始化会报空指针
    private CartProvider cartProvider;



    //每一个adapter代表适配每一个Item
    public CartAdapter(Context context,  List<ShoppingCart> datas,CheckBox checkBox,TextView textView) {

        super(context, R.layout.template_cart, datas);

        this.datas = datas;
        mcheckBox = checkBox;
        //点击checkBox时，若all->每个都isCheck；若none->每个都!isCheck
        mcheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //判断全选或全不选
                checkAll_None(mcheckBox.isChecked());
                //每次对价格操作都得刷新总价
                showTotalPrice();

            }

        });
        mtextView = textView;



        cartProvider = new CartProvider(context);


        //以前的组件监听事件都是点击触发回调函数；
        setOnItemClickListener(this);


        showTotalPrice();



    }








    @Override
    protected void convert(BaseViewHolder viewHoder, final ShoppingCart item) {

        viewHoder.getTextView(R.id.text_title).setText(item.getName());
        viewHoder.getTextView(R.id.text_price).setText("￥"+item.getPrice());
        SimpleDraweeView draweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));

        CheckBox checkBox = (CheckBox) viewHoder.getView(R.id.checkbox);
        checkBox.setChecked(item.isChecked());


        NumberAddSubView numberAddSubView = (NumberAddSubView) viewHoder.getView(R.id.num_control);

        numberAddSubView.setValue(item.getCount());

        numberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClick(View view, int value) {

                item.setCount(value);
                cartProvider.update(item);
                showTotalPrice();


            }

            @Override
            public void onButtonSubClick(View view, int value) {

                item.setCount(value);
                cartProvider.update(item);
                showTotalPrice();
            }
        });


    }

    //计算出购物车的总价格，如果Item被选中
    private  float getTotalPrice(){

        float sum = 0;

        if(!isNull()){
            return  sum;
        }

        for (ShoppingCart cart : datas){

            if (cart.isChecked()){

                sum += cart.getCount()*cart.getPrice();

            }

        }

        return  sum;
    }

    private  boolean isNull(){

        return (datas!=null&&datas.size()>0);
    }


    //显示购物车价格
    public  void showTotalPrice(){

        float total = getTotalPrice();
        mtextView.setText(Html.fromHtml("合计 ￥<span style='color:#eb4f38'>" + total + "</span>"),TextView.BufferType.SPANNABLE);

    }

    //点击之后改变IsCheck状态
    @Override
    public void onItemClick(View view, int position) {

        ShoppingCart cart = getItem(position);
        cart.setIsChecked(!cart.isChecked());
        //通知Item刷新
        notifyItemChanged(position);

        checkListener();

        //购物车价格刷新
        showTotalPrice();

    }


    //对isCheck进行监听，影响CheckBox
    public  void checkListener(){

        int count = 0;
        int checkNum = 0;

        if (datas!=null){

            count = datas.size();
        }

        for (ShoppingCart cart: datas){
            //尽量用！来判断
            if (!cart.isChecked()){
                mcheckBox.setChecked(false);
                return;

            }else {

                checkNum+=1;
            }


        }
        if (checkNum == count){

            mcheckBox.setChecked(true);
        }


    }


    //判断全选或全不选
    public   void checkAll_None(boolean isChecked){

        if (!isNull()){
            return;
        }

        int i=0;
        for (ShoppingCart cart : datas){

            cart.setIsChecked(isChecked);
            //刷新Item
            notifyItemChanged(i);
            i++;

        }



    }

    //把选中商品给删除
    public void delCart(){

        if (!isNull()){
            return;
        }

        //对List进行操作时List中的object删除后，循环会出问题，所以要用迭代器：iterator

//        for (ShoppingCart cart: datas){
//            if (cart.isChecked()){
//
//                //通过索引从list中找到object对象的索引
//                int position = datas.indexOf(cart);
//                cartProvider.delete(cart);
//                datas.remove(cart);
//                notifyItemRemoved(position);
//
//
//            }
//
//
//        }
        //通过Iterator循环
        for (Iterator iterator = datas.iterator();iterator.hasNext();){

            ShoppingCart cart = (ShoppingCart) iterator.next();
            if (cart.isChecked()){

                int position = datas.indexOf(cart);
                cartProvider.delete(cart);
                iterator.remove();
                notifyItemRemoved(position);

            }


        }

//


    }








}
