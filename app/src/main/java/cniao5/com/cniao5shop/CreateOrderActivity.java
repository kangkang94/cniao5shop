package cniao5.com.cniao5shop;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cniao5.com.cniao5shop.adapter.WareOrderAdapter;
import cniao5.com.cniao5shop.adapter.layoutmanager.FullyLinearLayoutManager;
import cniao5.com.cniao5shop.bean.Charge;
import cniao5.com.cniao5shop.bean.ShoppingCart;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.http.SpotsCallBack;
import cniao5.com.cniao5shop.msg.CreateOrderRespMsg;
import cniao5.com.cniao5shop.utils.CartProvider;
import cniao5.com.cniao5shop.utils.JSONUtil;

/**
 * Created by kang on 16/11/15.
 */
public class CreateOrderActivity extends  BaseActivity implements View.OnClickListener {




    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";





    @ViewInject(R.id.txt_order)
    private TextView txtOrder;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mLayoutAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mLayoutWechat;

    @ViewInject(R.id.rl_bd)
    private RelativeLayout mLayoutBd;


    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_webchat)
    private RadioButton mRbWechat;

    @ViewInject(R.id.rb_bd)
    private RadioButton mRbBd;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnCreateOrder;

    @ViewInject(R.id.txt_total)
    private TextView mTxtTotal;








    private CartProvider cartProvider;

    private WareOrderAdapter wareOrderAdapter;


    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
    private float amount;
    private String orderNum;
    //默认为支付宝支付
    private String payChannel = CHANNEL_ALIPAY;


    private HashMap<String,RadioButton> channels = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_order);
        ViewUtils.inject(this);

        showData();

        init();



    }

    //把购物车的数据在RecycleView中展现出来

    public void showData(){

        cartProvider = new CartProvider(this);
        //实例化适配器
        wareOrderAdapter = new WareOrderAdapter(this,cartProvider.getAll());


        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(wareOrderAdapter);



    }

    /**
     * 当点击每行时将要对RadioButton进行选中，但是选中状态是互斥的.
     * 到底如何去互斥呢？
     */
    /**
     * 思路：1.在每行的xml文件里都设置可点击，并且都设置Tag
     * 2.把每个RadioButton与它所对应的关键字都存入HashMap中。
     * 3.循环取出RadioButton，把点击的Tag取出，看取出的Tag isCheck，当单击时则改变选择状态
     */
    private void init(){

        channels.put(CHANNEL_ALIPAY,mRbAlipay);
        channels.put(CHANNEL_WECHAT,mRbWechat);
        channels.put(CHANNEL_BFB,mRbBd);


        mLayoutAlipay.setOnClickListener(this);
        mLayoutWechat.setOnClickListener(this);
        mLayoutBd.setOnClickListener(this);


        amount = wareOrderAdapter.getTotalPrice();

        mTxtTotal.setText("应付款  ￥"+amount);

    }



    @Override
    public void onClick(View v) {


        selectPayChannel(v.getTag().toString());

    }

    //传入v的Tag
        public void selectPayChannel(String paychannel){


            for(Map.Entry<String,RadioButton> entry :channels.entrySet()){

                payChannel = paychannel;
                RadioButton button = entry.getValue();

                if (entry.getKey().equals(paychannel)){

                    boolean isCheck = button.isChecked();

                    button.setChecked(!isCheck);
                }else {

                    button.setChecked(false);

                }


            }



        }

    //当我们点击提交订单时
    @OnClick(R.id.btn_createOrder)
    public void createNewOrder(View v){


        postNewOrder();

    }


    /**
     * 请求新的订单，也就是把购物车里的订单提交到服务端
     * 1.关键是如何拼接url
     * 2.建立内部类，从购物车中获取信息
     */

    private void postNewOrder(){


        final List<ShoppingCart> carts = wareOrderAdapter.getDatas();

        List<WareItem> items = new ArrayList<>(carts.size());

        for (ShoppingCart c : carts){

            WareItem item = new WareItem(c.getId(),c.getPrice().intValue());
            items.add(item);
        }

        String item_json = JSONUtil.toJSON(items);

        Map<String,String> params = new HashMap<>(5);
        params.put("user_id",CniaoApplication.getmInstance().getUser().getId()+"");
        params.put("item_json",item_json);
        params.put("pay_channel",payChannel);
        params.put("amount",(int)amount+"");
        params.put("addr_id",1+"");

        //点完购买之后不能再点击购买
        mBtnCreateOrder.setEnabled(false);


        okHttpHelper.post(Constants.API.ORDER_CREATE, params, new SpotsCallBack<CreateOrderRespMsg>(this) {

            @Override
            public void onSuccess(Response response, CreateOrderRespMsg respMsg) {

                mBtnCreateOrder.setEnabled(true);

                orderNum = respMsg.getData().getOrderNum();
                Charge charge = respMsg.getData().getCharge();

            }

            @Override
            public void onError(Response response, int code, Exception e) {

                mBtnCreateOrder.setEnabled(true);

            }
        });


    }




    //建立一个商品列表的内部类
    private class WareItem{

        private Long ware_id;
        private int amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }








}
