# PopupLayout

![popup.gif](https://upload-images.jianshu.io/upload_images/3350204-a793916f73878a6f.gif?imageMogr2/auto-orient/strip)

看到qq兴趣部落评论的这个弹幕效果，试着做了下。一开始思考用自定义ViewGroup,想了下肯定很麻烦，后来想到listView的layoutanimation效果貌似可以，但试了下没效果，据说是viewgroup创建的时候才会有效果。后来了解了LayoutTransition，发现是个好东西。当ViewGroup添加删除改变子view时可以加动画效果。做出来发现很简单，LinearLayout和LayoutTransition就搞定了。

### 布局
```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <LinearLayout
        android:layout_gravity="bottom"
        android:animateLayoutChanges="true"
        android:padding="15dp"
        android:divider="@drawable/divider"
        android:showDividers="middle"
        android:id="@+id/ll_container"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

    </LinearLayout>

</FrameLayout>

```

## 代码
```
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
```
