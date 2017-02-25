package cniao5.com.cniao5shop.adapter;

import android.content.Context;

import java.util.List;

import cniao5.com.cniao5shop.R;
import cniao5.com.cniao5shop.bean.Category;

/**
 * Created by kang on 16/10/11.
 */
public class CategoryAdapter extends SimpleAdapter<Category> {



    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, R.layout.template_single_text, datas);
    }




    @Override
    protected void convert(BaseViewHolder viewHoder, Category category) {

        viewHoder.getTextView(R.id.textView).setText(category.getName());
    }
}
