package cniao5.com.cniao5shop;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cniao5.com.cniao5shop.adapter.HWAdatper;
import cniao5.com.cniao5shop.adapter.decoration.DividerGridItemDecoration;
import cniao5.com.cniao5shop.adapter.decoration.DividerItemDecoration;
import cniao5.com.cniao5shop.bean.Page;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.utils.Pager;
import cniao5.com.cniao5shop.widget.CnToolbar;

/**
 * Created by kang on 16/10/19.
 */
public class WareListActivity extends AppCompatActivity implements Pager.OnPageListener<Wares>, TabLayout.OnTabSelectedListener, View.OnClickListener {



    public static final int TAG_DEFAULT = 0;
    public static final int TAG_SALE = 1;
    public static final int TAG_PRICE = 2;

    //切换页面的常量
    public static final int ACTION_LIST =1;
    public static final int ACTION_GRID =2;



    @ViewInject(R.id.tab_layout)
    private TabLayout mTablayout;

    @ViewInject(R.id.txt_summary)
    private TextView mTxtSummary;


    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview_wares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    @ViewInject(R.id.toolbar)
    private CnToolbar mToolbar;


    private  int orderBy = 0;
    private  long campaignId = 0;

    private HWAdatper mWaresAdapter;

    private  Pager pager;



    //Activity启动即回调onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_warelist);

        ViewUtils.inject(this);

        //从HomeFragment的点击事件中拿到campaign_id
        campaignId = getIntent().getLongExtra(Constants.COMPAINGAIN_ID,0);

        initTab();

        requestData();
        initToolbar();

    }

    private  void initToolbar(){

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //activity处于onStop状态时，activity会回到返回栈中；栈顶的activity是处于可见状态
                WareListActivity.this.finish();

            }
        });

        //对Toolbar右图标进行设置
        mToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
        mToolbar.getRightButton().setTag(ACTION_LIST);

        mToolbar.setRightButtonOnClickListener(this);


    }





    //对TabLayout的Tab进行设置,设置标志 setTag();
    private  void initTab(){


        TabLayout.Tab tab =mTablayout.newTab();
        tab.setText("默认");
        tab.setTag(TAG_DEFAULT);

        mTablayout.addTab(tab);

        tab =mTablayout.newTab();
        tab.setText("销量");
        tab.setTag(TAG_SALE);

        mTablayout.addTab(tab);

         tab =mTablayout.newTab();
        tab.setText("价格");
        tab.setTag(TAG_PRICE);

        mTablayout.addTab(tab);

        //在初始化Tab这里对TabLayout监听
        mTablayout.setOnTabSelectedListener(this);



    }




    //获取数据Ware
    private  void requestData(){

         pager = Pager.newBuilder().setUrl(Constants.API.WARES_CAMPAIN_LIST)
                .putParam("campaignId",campaignId)
                .putParam("orderBy",orderBy)
                .setLoadMore(true)
                .setRefreshLayout(mRefreshLayout)
                .setOnPageListener(this)
                .build(this,new TypeToken<Page<Wares>>(){}.getType());

        pager.request();



    }


    //OnPageListener接口的三个方法。正常模式，上拉刷新，下拉加载
    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {

        mTxtSummary.setText("共有" + totalCount + "件商品");

        if (mWaresAdapter == null) {
            mWaresAdapter = new HWAdatper(this, datas);
            mRecyclerview_wares.setAdapter(mWaresAdapter);
            mRecyclerview_wares.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview_wares.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
            mRecyclerview_wares.setItemAnimator(new DefaultItemAnimator());
        } else {
            mWaresAdapter.refreshData(datas);
        }
    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {


        mWaresAdapter.refreshData(datas);
        mRecyclerview_wares.scrollToPosition(0);

        mRefreshLayout.finishRefresh();


    }
    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {

        mWaresAdapter.loadMoreData(datas);

        mRefreshLayout.finishRefreshLoadMore();


    }

    //tab被选中时回调
    @Override
    public void onTabSelected(TabLayout.Tab tab) {


        orderBy = (int) tab.getTag();
        pager.putParam("orderBy",orderBy);
        pager.request();


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {


    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {


    }

    //点击Toolbar的RightButton
    @Override
    public void onClick(View v) {

        int action = (int) v.getTag();
        if (ACTION_LIST == action){

            mToolbar.setRightButtonIcon(R.drawable.icon_list_32);
            mToolbar.getRightButton().setTag(ACTION_GRID);
            mWaresAdapter.resetLayout(R.layout.template_grid_wares);

            mRecyclerview_wares.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerview_wares.addItemDecoration(new DividerGridItemDecoration(this));

        }else if (ACTION_GRID == action){

            mToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
            mToolbar.getRightButton().setTag(ACTION_LIST);
            mWaresAdapter.resetLayout(R.layout.template_hot_wares);

            mRecyclerview_wares.setLayoutManager(new LinearLayoutManager(this));

            mRecyclerview_wares.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));



        }






    }
}
