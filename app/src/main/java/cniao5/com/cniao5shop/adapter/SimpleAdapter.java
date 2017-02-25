package cniao5.com.cniao5shop.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by kang on 16/10/11.
 */
public abstract class  SimpleAdapter<T> extends BaseAdapter<T,BaseViewHolder>{


    public SimpleAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public SimpleAdapter(Context context, int layoutResId, List<T> datas) {
        super(context, layoutResId, datas);
    }



}
