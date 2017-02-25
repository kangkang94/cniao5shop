package cniao5.com.cniao5shop.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

import cniao5.com.cniao5shop.Constants;
import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.adapter.BaseAdapter;
import cniao5.com.cniao5shop.adapter.CategoryAdapter;
import cniao5.com.cniao5shop.adapter.WaresAdapter;
import cniao5.com.cniao5shop.adapter.decoration.DividerGridItemDecoration;
import cniao5.com.cniao5shop.adapter.decoration.DividerItemDecoration;
import cniao5.com.cniao5shop.bean.Banner;
import cniao5.com.cniao5shop.bean.Category;
import cniao5.com.cniao5shop.bean.Page;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.http.BaseCallback;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.http.SpotsCallBack;

/**
 * Created by Ivan on 15/9/22.
 */
public class CategoryFragment extends BaseFragment {


    private List<Category> mDatas;
    private CategoryAdapter mAdapter;
    private WaresAdapter mWaresAdapter;

    private List<Wares> mWares;


    private int categoryId = 1;
    private int curPage = 1;
    private int pageSize = 10;
     private int  totalPage;


    /**
     * 上拉下拉刷新的三种状态
     */
    public static final int STATE_NORMAL =0;
    public static final int STATE_REFRESH =1;
    public static final int STATE_MORE =2;

    public int state = STATE_NORMAL;





    //绑定fragment_category里布局的组件

    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.recyclerview_wares)
    private RecyclerView mRecyclerviewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLaout;

    @ViewInject(R.id.slider)
    private  SliderLayout mSliderLayout;


    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();



    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category,container,false);
    }

    @Override
    public void init() {


        //得到一级菜单数据
        requestCategoryData();

        //请求SladerLayout数据
        requestBannerData();


        //初始化上拉刷新和下拉加载
        initRefreshLayout();
    }


    //初始化上拉刷新和下拉加载
    public void initRefreshLayout(){

        mRefreshLaout.setLoadMore(true);

        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }


            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout){

                if (curPage <= totalPage){
                    refreshLoadMore();
                }else {
                    mRefreshLaout.finishRefreshLoadMore();
                }


            }


        });


    }

    //当上拉刷新时
    public void refreshData(){
        curPage = 1;
        state = STATE_REFRESH;
        requestCategoryWares(categoryId);


    }


    //当下拉刷新时
    public void refreshLoadMore(){

        curPage =  ++curPage;
        state = STATE_MORE;

        requestCategoryWares(categoryId);

    }









    //得到一级菜单数据
    public void requestCategoryData(){

        okHttpHelper.get(Constants.API.CATEGORY_LIST, new SpotsCallBack<List<Category>>(getContext()) {


            @Override
            public void onSuccess(Response response, List<Category> categories) {

                if (categories != null && categories.size() >0){
                    categoryId = (int) categories.get(0).getId();
                    //请求Wares
                    requestCategoryWares(categoryId);
                }

                mDatas = categories;
                //初始化一级菜单以及点击事件
                showCategoryData();

            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }




    //初始化一级菜单以及点击事件
    public void showCategoryData(){
        mAdapter = new CategoryAdapter(getContext(),mDatas);


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));


        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                categoryId = (int) mDatas.get(position).getId();
                //换到另外一个Item所指向的categoryId，需要初始化curPage，state
                curPage = 1;
                state = STATE_NORMAL;

                //请求Wares
                requestCategoryWares(categoryId);

            }
        });

    }

    //请求SladerLayout数据
    private void requestBannerData( ) {



        String url = Constants.API.BANNER+"?type=1";

        okHttpHelper.get(url, new SpotsCallBack<List<Banner>>(getContext()) {


            @Override
            public void onSuccess(Response response, List<Banner> banners) {

                showSliderViews(banners);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }



    private void showSliderViews(List<Banner> banners){




        if(banners !=null){

            for (Banner banner : banners){


                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
                sliderView.image(banner.getImgUrl());
                sliderView.description(banner.getName());
                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(sliderView);

            }
        }



        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setDuration(3000);


    }

    //请求Ware数据
    public void requestCategoryWares(int categoryId){

        String url = Constants.API.WARES_LIST+"?categoryId="+categoryId+"&curPage="+curPage+"&pageSize="+pageSize;

        okHttpHelper.get(url, new BaseCallback<Page<Wares>>() {
            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {

                mWares = waresPage.getList();
                curPage = waresPage.getCurrentPage();
                totalPage = waresPage.getTotalPage();

                showWaresData();

            }


            @Override
            public void onError(Response response, int code, Exception e) {


            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });



    }

    public void showWaresData(){

        switch (state){

            case STATE_NORMAL:

                //新建了Adapter就没有必要重新新建Adapter
                if (mWaresAdapter == null){
                    mWaresAdapter = new WaresAdapter(getContext(),mWares);
                    mRecyclerviewWares.setAdapter(mWaresAdapter);
                    mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(getContext()));
                }else{
                    mWaresAdapter.clear();
                    mWaresAdapter.addData(mWares);

                }


                break;
            case STATE_REFRESH:
                mWaresAdapter.clear();
                mWaresAdapter.addData(mWares);

                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();

                break;

            case STATE_MORE:

                mWaresAdapter.addData(mWaresAdapter.getDatas().size(),mWares);
                mRecyclerView.scrollToPosition(mWaresAdapter.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();




                break;



        }




    }
















}



