package cniao5.com.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.List;

import cniao5.com.cniao5shop.CniaoApplication;
import cniao5.com.cniao5shop.Constants;
import cniao5.com.cniao5shop.LoginActivity;
import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.WareDetailActivity;
import cniao5.com.cniao5shop.adapter.BaseAdapter;
import cniao5.com.cniao5shop.adapter.HWAdatper;
import cniao5.com.cniao5shop.adapter.decoration.DividerItemDecoration;
import cniao5.com.cniao5shop.bean.Page;
import cniao5.com.cniao5shop.bean.User;
import cniao5.com.cniao5shop.bean.Wares;
import cniao5.com.cniao5shop.http.OkHttpHelper;
import cniao5.com.cniao5shop.http.SpotsCallBack;


public class HotFragment extends Fragment {



    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;

    private List<Wares> datas;

    private HWAdatper mAdatper;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLaout;



    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_hot,container,false);

        ViewUtils.inject(this,view);


        initRefreshLayout();
        getData();

        return view ;

    }



    private  void initRefreshLayout(){

        mRefreshLaout.setLoadMore(true);
        //当上拉刷新时回调onRefresh，当下拉刷新时回调onRefreshLoadMore
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (currPage <= totalPage)
                    loadMoreData();
                else {
//                    Toast.makeText()
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }




    private  void refreshData(){

        currPage =1;

        state=STATE_REFREH;
        getData();

    }

    private void loadMoreData(){

        currPage = ++currPage;
        state = STATE_MORE;

        getData();

    }


    private void getData(){



        String url = Constants.API.WARES_HOT+"?curPage="+currPage+"&pageSize="+pageSize;
        httpHelper.get(url, new SpotsCallBack<Page<Wares>>(getContext()) {


            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {


                datas = waresPage.getList();
                currPage = waresPage.getCurrentPage();
                totalPage = waresPage.getTotalPage();

                showData();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }




    private  void showData(){



        switch (state){

            case  STATE_NORMAL:
//                mAdatper = new HotWaresAdapter(datas);
//
//                mRecyclerView.setAdapter(mAdatper);
//
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));


                mAdatper = new HWAdatper(getContext(),datas);

                //当点击HotFragment一整行时，将会跳转到WareDetail页面
                mAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //通过适配器调用getItem得到Item的信息
                        Wares wares = mAdatper.getItem(position);
                        //通过Intent跳转到另一个界面
                        Intent intent = new Intent(getContext(), WareDetailActivity.class);
                        //将点击事件的（Serializable）对象传入intent
                        intent.putExtra(Constants.WARE,wares);

                        startActivity(intent);

                    }
                });

                mRecyclerView.setAdapter(mAdatper);

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));




                break;

            case STATE_REFREH:
                mAdatper.clear();
                mAdatper.addData(datas);

                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;

            case STATE_MORE:
                mAdatper.addData(mAdatper.getDatas().size(),datas);
                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;





        }



    }



    public void startActivity(Intent intent,boolean isNeedLogin){

        /**
         *  判断是否需要登陆，如果需要登陆则先取出User信息，
         *  如无User信息则把Intent信息保存到全局，跳转到登陆activity
         */

        if(isNeedLogin){

            User user = CniaoApplication.getmInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{

                CniaoApplication.getmInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }





}
