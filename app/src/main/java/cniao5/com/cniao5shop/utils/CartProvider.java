package cniao5.com.cniao5shop.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cniao5.com.cniao5shop.bean.ShoppingCart;
import cniao5.com.cniao5shop.bean.Wares;

/**
 * Created by kang on 16/10/16.
 */

/**
 * 1.内存处理SparseArray<ShoppingCart>,用put,update,delete，getAll,来实现增伤改查
 * 2.每次增删改查之后都得把内存中的数据存入本地 SparseArray->List<ShoppingCart>->String json
 * 3.初始化时，要把本地数据转化为内存数据
 */
public class CartProvider {

    public static final String CART_JSON="cart_json";

    //key必须是int值；ShoopingCart是value
    SparseArray<ShoppingCart> datas = null;

    private Context mContext;

    public CartProvider(Context context){
        mContext = context;

        datas = new SparseArray<>(10);
        //每次初始化都得把本地数据传给内存
        listToSparse();

    }


    //将对象为ShoppingCart类型的对象存入购物车
    public  void put(ShoppingCart cart){

        ShoppingCart temp = datas.get(cart.getId().intValue());
        if (temp !=null && !"".equals(temp)){

            temp.setCount(temp.getCount() +1);
        }else{

            temp = cart;
            temp.setCount(1);
        }

        datas.put(temp.getId().intValue(),temp);

        commit();


    }

    /**
     * 在写代码时不断对代码进行重构，现在添加购物车是传入一个Ware类型的数据
     *  使用convertData方法转换wares，再调用上面的put方法
     */
    public  void put(Wares wares){

        ShoppingCart cart = convertData(wares);
        put(cart);

    }






    ///更新购物车
    public void update(ShoppingCart cart){

        datas.put(cart.getId().intValue(),cart);
        commit();
    }

    //删除购物车
    public void delete(ShoppingCart cart){

        datas.remove(cart.getId().intValue());
        commit();
    }







    //获取所有的商品
    public List<ShoppingCart> getAll(){


        return  getDataFromLocal();
    }



    /**
     * 1.put,update,delete操作把商品数据存入SparseArray中，未存入本地；
     * 2.需要把SparseArray，datas转化为List<ShoppingCart>，然后存入本地
     *
     */
    public void commit(){

        String json = JSONUtil.toJSON(SparseToList());
        //然后存入本地
        PreferencesUtils.putString(mContext,CART_JSON,json);



    }

    //2.需要把SparseArray，datas转化为List<ShoppingCart>，然后存入本地
    public List<ShoppingCart> SparseToList(){

        int size = datas.size();
        List<ShoppingCart> mCarts = new ArrayList<>(size);
        for (int i=0; i<size;i++){

            mCarts.add(datas.valueAt(i));
        }

        return mCarts;


    }

    //把本地数据传给内存
    private void listToSparse(){

        List<ShoppingCart> mcarts = getDataFromLocal();

        if (mcarts !=null && mcarts.size()>0){

            for (int i=0;i<mcarts.size();i++ ){

                datas.put(mcarts.get(i).getId().intValue(),mcarts.get(i));
            }

        }

    }







    //从本地的SharedPreference中返回List<ShoppingCart>
    public List<ShoppingCart> getDataFromLocal(){

        //一次存储全部商品列表
        String json = PreferencesUtils.getString(mContext, CART_JSON);
        List<ShoppingCart> carts = null;
        if (json !=null){

             carts =  JSONUtil.fromJson(json, new TypeToken< List<ShoppingCart>>() {}.getType());
        }



        return carts;
    }



    //由于wares是ShoppingCart的父类，所以无法强转,要把wares转换为ShoppingCart需要把父类的一个个属性提取出来赋值过去

    public ShoppingCart convertData(Wares item){

        ShoppingCart cart = new ShoppingCart();
        //所以一个个赋值过去
        cart.setId(item.getId());
        cart.setDescription(item.getDescription());
        cart.setImgUrl(item.getImgUrl());
        cart.setName(item.getName());
        cart.setPrice(item.getPrice());

        return  cart;
    }








}
