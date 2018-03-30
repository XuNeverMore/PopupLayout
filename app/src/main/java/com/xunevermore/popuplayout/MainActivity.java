package com.xunevermore.popuplayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pools;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llContainer;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llContainer = (LinearLayout) findViewById(R.id.ll_container);

        LayoutTransition transition = new LayoutTransition();


        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);

        ObjectAnimator valueAnimator = ObjectAnimator.ofPropertyValuesHolder(null, new PropertyValuesHolder[]{scaleX, scaleY})
                .setDuration(transition.getDuration(LayoutTransition.APPEARING));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ObjectAnimator objectAnimator = (ObjectAnimator) animation;
                View view = (View) objectAnimator.getTarget();
                view.setPivotX(0f);
                view.setPivotY(view.getMeasuredHeight());
            }
        });


        transition.setAnimator(LayoutTransition.APPEARING, valueAnimator);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(null, "alpha", 0, 1).setDuration(LayoutTransition.DISAPPEARING);
        transition.setAnimator(LayoutTransition.DISAPPEARING, objectAnimator);
        llContainer.setLayoutTransition(transition);
    }

    private String[] texts = new String[]{
            "火来我在灰烬中等你。",
            "我对这个世界没什么可说的。",
            "侠之大者，为国为民。",
            "为往圣而继绝学"};

    Pools.SimplePool<TextView> textViewPool = new Pools.SimplePool<>(texts.length);

    private TextView obtainTextView() {
        TextView textView = textViewPool.acquire();
        if (textView == null) {
            textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
            textView.setTextColor(0xffffffff);
            textView.setTextSize(18);
            textView.setTextColor(0xffffffff);
        }
        return textView;
    }

    private int dp2px(float dp) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }


    int index = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0 && llContainer.getChildCount() == 4) {
                llContainer.removeViewAt(0);


            }
            if (index == 4) {
                index = 0;
            }
            TextView textView = obtainTextView();
            switch (index) {
                case 0:
                    textView.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_black));
                    break;
                case 1:
                    textView.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_blue));

                    break;
                case 2:
                    textView.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_green));

                    break;
                case 3:
                    textView.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rect_yellow));

                    break;
            }
            textView.setText(texts[index]);

            llContainer.addView(textView);
            sendEmptyMessageDelayed(0, 2000);
            index++;
        }

        ;
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }
}
