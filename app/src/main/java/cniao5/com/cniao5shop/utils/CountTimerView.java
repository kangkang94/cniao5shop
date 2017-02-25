package cniao5.com.cniao5shop.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import cniao5.com.cniao5shop.R;



public class CountTimerView extends CountDownTimer {

    public static final int TIME_COUNT = 61000;//时间防止从59s开始显示（以倒计时60s为例子）
    private TextView btn;
    private int endStrRid;


    /**
     * 参数 millisInFuture       倒计时总时间（如60S，120s等）
     * 参数 countDownInterval    渐变时间（每次倒计1s）
     * 参数 btn   点击的按钮(因为Button是TextView子类，为了通用我的参数设置为TextView）
     * 参数 endStrRid   倒计时结束后，按钮对应显示的文字
     */
    public CountTimerView(long millisInFuture, long countDownInterval, TextView btn, int endStrRid) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }


    /**
     *参数上面有注释,构造器重载，当需要自定义时间总长和时间间隔时， super(millisInFuture, countDownInterval);当不需要时，写死
     */
    public CountTimerView(TextView btn, int endStrRid) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public CountTimerView(TextView btn) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = R.string.smssdk_resend_identify_code;
    }



    // 计时完毕时触发
    @Override
    public void onFinish() {

        btn.setText(endStrRid);
        //设置按钮可以点击
        btn.setEnabled(true);
    }

    // 计时过程显示
    @Override
    public void onTick(long millisUntilFinished) {
        //在倒计时期间设置按钮无法点击
        btn.setEnabled(false);
        btn.setText(millisUntilFinished / 1000 + " 秒后可重新发送");

    }
}
