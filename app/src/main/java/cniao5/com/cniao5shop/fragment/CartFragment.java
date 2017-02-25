package cniao5.com.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

import cniao5.com.cniao5shop.CreateOrderActivity;
import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.adapter.CartAdapter;
import cniao5.com.cniao5shop.adapter.decoration.DividerItemDecoration;
import cniao5.com.cniao5shop.bean.ShoppingCart;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.utils.CartProvider;
import cniao5.com.cniao5shop.widget.CnToolbar;

/**
 * Created by Ivan on 15/9/22.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener {



    public static final int ACTION_EDIT = 1;
    public static final int ACTION_DELETE = 2;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.checkbox_all)
    private CheckBox mCheckBox;

    @ViewInject(R.id.txt_total)
    private TextView mTextTotal;

    @ViewInject(R.id.btn_order)
    private Button mBtnOrder;

    @ViewInject(R.id.btn_del)
    private Button mBtnDel;

    @ViewInject(R.id.toolbar)
    private  CnToolbar mToolbar;

    private CartProvider cartProvider;
    private CartAdapter mAdapter;




    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();


    /**
     * 每次新建Fragment时将会将会回调onCreateView方法，但是由于改写了Tabhost，导致切换tab时没有新建Fragment，所以需要做额外处理
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cart,container, false);

    }

    @Override
    public void init() {

        cartProvider = new CartProvider(getContext());
//        把Toolbar模式从Searchview改为TextView
        changeToolbar();
        showData();

    }

    //点击删除按钮，对datas的数据从本地删除，内存也删除
    @OnClick(R.id.btn_del)
    public  void delCart(View view){

        mAdapter.delCart();

    }

    //点击去结算按钮
    @OnClick(R.id.btn_order)
    public void toOrder(View view){

        Intent intent = new Intent(getActivity(), CreateOrderActivity.class);

        //要设为true，但是暂时设置为false
        startActivity(intent, false);

    }






    //从本地获取数据，传入adapter里面
    private  void showData(){

        List<ShoppingCart>  mcarts = cartProvider.getAll();

        mAdapter = new CartAdapter(getContext(),mcarts,mCheckBox,mTextTotal);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


    }


    //对CartFragment进行刷新,把adapter的数据都清除，把从本地取出数据
    public void refData(){

        mAdapter.clear();
        List<ShoppingCart>  mcarts = cartProvider.getAll();
        mAdapter.addData(mcarts);
        mAdapter.showTotalPrice();



    }

    /**
     * 在Fragment里绑定监听事件，然后获取MainActivity的对象，对toolbar进行控制，Fragment与Activity之间的通信
     */
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        if (context instanceof MainActivity){
//
//            MainActivity activity = (MainActivity) context;
//            mToolbar = (CnToolbar) activity.findViewById(R.id.toolbar);
//
//            //把Toolbar模式从Searchview改为TextView
//            changeToolbar();
//
//        }
//
//
//
//
//
//
//    }

    //把Toolbar模式从Searchview改为TextView,在CartFragment里面
    public void changeToolbar(){


        mToolbar.hideSearchView();
        mToolbar.showTitleView();
        mToolbar.setTitle(R.string.cart);

        mToolbar.getRightButton().setVisibility(View.VISIBLE);
        mToolbar.setRightButtonText("编辑");

        mToolbar.getRightButton().setOnClickListener(this);

        //第一次显示“编辑”标志;
        mToolbar.getRightButton().setTag(ACTION_EDIT);


    }

    /**
     * 俩个状态通过设置常量（action）标记->对需要改变的组件进行setTag(Object object(常量))，取出getTag(),然后进行判断
     * @param v
     */
    //监听右Button点击事件
    @Override
    public void onClick(View v) {

        int action = (int) v.getTag();

        if (action == ACTION_EDIT){

            showDeleteControl();
        }
        if (action == ACTION_DELETE){

            hideDeleteControl();
        }

    }
    //点击后处于编辑状态
    private void showDeleteControl(){

        mToolbar.getRightButton().setText("完成");
        mTextTotal.setVisibility(View.GONE);

        mBtnOrder.setVisibility(View.GONE);
        mBtnDel.setVisibility(View.VISIBLE);

        //将状态改为完成状态
        mToolbar.getRightButton().setTag(ACTION_DELETE);

        //把所有check改为不选
        mAdapter.checkAll_None(false);
        mCheckBox.setChecked(false);

    }

    //点击后处于完成状态

    private void hideDeleteControl(){

        mToolbar.getRightButton().setText("编辑");
        mTextTotal.setVisibility(View.VISIBLE);

        mBtnOrder.setVisibility(View.VISIBLE);
        mBtnDel.setVisibility(View.GONE);

        //将状态改为完成状态
        mToolbar.getRightButton().setTag(ACTION_EDIT);

        //把所有check改为全选
        mAdapter.checkAll_None(true);
        mAdapter.showTotalPrice();
        mCheckBox.setChecked(true);

    }













}
