package cniao5.com.cniao5shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cniao5.com.cniao5shop.bean.Tab;
import cniao5.com.cniao5shop.fragment.CartFragment;
import cniao5.com.cniao5shop.fragment.CategoryFragment;
import cniao5.com.cniao5shop.fragment.HomeFragment;
import cniao5.com.cniao5shop.fragment.HotFragment;
import cniao5.com.cniao5shop.fragment.MineFragment;
import cniao5.com.cniao5shop.widget.FragmentTabHost;

public class MainActivity extends BaseActivity {


    //用于动态加载View
    private LayoutInflater mInflater;

    private FragmentTabHost mTabhost;



    private CartFragment cartFragment;

    /**
     * 通过Arraylist存储五个Tab对象，再通过循环把Tab对象与FragmentTabHost绑定
     */
    private List<Tab> mTabs = new ArrayList<>(5);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initTab();

    }



    private void initTab() {


        Tab tab_home = new Tab(HomeFragment.class,R.string.home,R.drawable.selector_icon_home);
        Tab tab_hot = new Tab(HotFragment.class,R.string.hot,R.drawable.selector_icon_hot);
        Tab tab_category = new Tab(CategoryFragment.class,R.string.catagory,R.drawable.selector_icon_category);
        Tab tab_cart = new Tab(CartFragment.class,R.string.cart,R.drawable.selector_icon_cart);
        Tab tab_mine = new Tab(MineFragment.class,R.string.mine,R.drawable.selector_icon_mine);

        mTabs.add(tab_home);
        mTabs.add(tab_hot);
        mTabs.add(tab_category);
        mTabs.add(tab_cart);
        mTabs.add(tab_mine);



        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);


        //对Tab变换监听，当变换到CartFragment时进行刷新
        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if (tabId == getString(R.string.cart)) {

                    refData();

                }

            }
        });

        //通过循环把每个封装好的Tab与FragmentTabhost绑定
        for (Tab tab : mTabs){

            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));

                 tabSpec.setIndicator(buildIndicator(tab));

            mTabhost.addTab(tabSpec,tab.getFragment(),null);

        }

        //对每个Tab设置分割线为NONE
        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        //默认初始化为第一个Tab
        mTabhost.setCurrentTab(0);



    }

    //把布局与Tab类信息绑定，封装成view
    private  View buildIndicator(Tab tab){

        //动态加载布局
        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }
    /**
     * 碎片与活动之间的通信，通过获取Fragment的实例，调用Fragment的方法
     */
    public void refData(){

        //如果cartFragment为空，则从管理器中得到fragment
        if (cartFragment == null){

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.cart));

            if (fragment !=null){

                cartFragment = (CartFragment) fragment;

                cartFragment.refData();
                cartFragment.changeToolbar();


            }

        }else {
            cartFragment.refData();
            cartFragment.changeToolbar();
        }




    }





}
