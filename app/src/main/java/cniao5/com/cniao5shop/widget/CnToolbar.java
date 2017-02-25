package cniao5.com.cniao5shop.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.internal.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cniao5.com.cniao5shop.R;



public class CnToolbar extends Toolbar {



    private LayoutInflater mInflater;

    private View mView;
    private TextView mTextTitle;
    private EditText mSearchView;
    private Button mRightButton;


    public CnToolbar(Context context) {
        this(context,null);
    }

    public CnToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CnToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



        initView();
        //设置toolbar的边距
        setContentInsetsRelative(10,10);

        if(attrs != null) {
            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.CnToolbar, defStyleAttr, 0);

            /**
             * 自定义控件的模式：1.通过TintTypedArray类从attrs.xml中取得自定义属性值
             * 2.如果属性值非空，将其赋值
             */
            final Drawable rightIcon = a.getDrawable(R.styleable.CnToolbar_rightButtonIcon);
            //一定要在这里进行条件判断
            if (rightIcon != null) {
                //setNavigationIcon(navIcon);
                setRightButtonIcon(rightIcon);
            }

            //默认false
            boolean isShowSearchView = a.getBoolean(R.styleable.CnToolbar_isShowSearchView,false);
            //如果isShowSearchView为true，把Title隐藏
            if(isShowSearchView){

                showSearchView();
                hideTitleView();

            }

            CharSequence rightButtonText = a.getText(R.styleable.CnToolbar_rightButtonText);
            if(rightButtonText !=null){
                setRightButtonText(rightButtonText);
            }

            a.recycle();
        }

    }

    private void initView() {

        if(mView == null) {

            mInflater = LayoutInflater.from(getContext());
            mView = mInflater.inflate(R.layout.toolbar, null);


            mTextTitle = (TextView) mView.findViewById(R.id.toolbar_title);
            mSearchView = (EditText) mView.findViewById(R.id.toolbar_searchview);
            mRightButton = (Button) mView.findViewById(R.id.toolbar_rightButton);

            //把Toolbar里面的控件组合起来
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            addView(mView, lp);
        }

    }

    //对右边的Button进行Background设置
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void  setRightButtonIcon(Drawable icon){

        if(mRightButton !=null){

            mRightButton.setBackground(icon);
            mRightButton.setVisibility(VISIBLE);
        }

    }

    public void  setRightButtonIcon(int icon){

        setRightButtonIcon(getResources().getDrawable(icon));
    }

    public  void setRightButtonOnClickListener(OnClickListener li){

        mRightButton.setOnClickListener(li);
    }

    public void setRightButtonText(CharSequence text){
        mRightButton.setText(text);
        mRightButton.setVisibility(VISIBLE);
    }


    public void setRightButtonText(int id){
        setRightButtonText(getResources().getString(id));
    }


    public Button getRightButton(){

        return this.mRightButton;
    }

    //设置标题
    @Override
    public void setTitle(int resId) {

        setTitle(getContext().getText(resId));
    }

    @Override
    public void setTitle(CharSequence title) {

        initView();
        if(mTextTitle !=null) {
            mTextTitle.setText(title);
            showTitleView();
        }

    }

    //提供外接方法，实现EditView和TextView的转换
    public  void showSearchView(){

        if(mSearchView !=null)
            mSearchView.setVisibility(VISIBLE);

    }

    public void hideSearchView(){
        if(mSearchView !=null)
            mSearchView.setVisibility(GONE);
    }

    public void showTitleView(){
        if(mTextTitle !=null)
            mTextTitle.setVisibility(VISIBLE);
    }


    public void hideTitleView() {
        if (mTextTitle != null)
            mTextTitle.setVisibility(GONE);

    }


//
//    private void ensureRightButtonView() {
//        if (mRightImageButton == null) {
//            mRightImageButton = new ImageButton(getContext(), null,
//                    android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
//            final LayoutParams lp = generateDefaultLayoutParams();
//            lp.gravity = GravityCompat.START | (Gravity.VERTICAL_GRAVITY_MASK);
//            mRightImageButton.setLayoutParams(lp);
//        }
//    }


}
