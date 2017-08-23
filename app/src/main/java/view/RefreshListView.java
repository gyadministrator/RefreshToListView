package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gy.refreshtolistview.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/18.
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private View header;
    private int headerHeight;
    private int firstVisibleItem;//第一个可见item的位置
    private boolean isRemark;//标记，当前是在listview最顶端按下的
    private int startY;//按下时的Y值
    private int state;//当前状态
    private final int NONE = 0;//正常状态
    private final int PULL = 1;//提示下拉状态
    private final int RELEASE = 2;//提示释放状态
    private final int REFRESHING = 3;//刷新状态
    private int scrollState;//listview当前滚动状态
    private IRefreshListener listener;

    public void setListener(IRefreshListener listener) {
        this.listener = listener;
    }

    public RefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.activity_header, null);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        topPadding(-headerHeight);
        this.addHeaderView(header);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局，占用的宽和高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELEASE) {
                    state = REFRESHING;
                    //加载最新数据
                    refreshViewByState();
                    listener.onRefresh();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    refreshViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    refreshViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (space > headerHeight + 30 &&
                        scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELEASE;
                    refreshViewByState();
                }
                break;
            case RELEASE:
                topPadding(topPadding);
                if (space > headerHeight + 30) {
                    state = PULL;
                    refreshViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    refreshViewByState();
                }
                break;
            case REFRESHING:
                break;
        }
    }

    /**
     * 根据当前状态，改变界面显示
     */
    private void refreshViewByState() {
        TextView tip = (TextView) header.findViewById(R.id.refresh_text);
        ImageView arrow = (ImageView) header.findViewById(R.id.refresh_down);
        ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.refresh_progressBar);
        RotateAnimation animation = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        RotateAnimation animation1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        switch (state) {
            case NONE:
                topPadding(-headerHeight);
                arrow.clearAnimation();
                break;
            case PULL:
                arrow.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                tip.setText("下拉可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(animation1);
                break;
            case RELEASE:
                arrow.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                tip.setText("松开可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(animation);
                break;
            case REFRESHING:
                topPadding(50);
                arrow.setVisibility(GONE);
                progressBar.setVisibility(VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    /**
     * 获取完数据
     */
    public void refreshComplete() {
        state = NONE;
        isRemark = false;
        refreshViewByState();
        TextView lastupdate= (TextView) header.findViewById(R.id.refresh_time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        String time=format.format(date);
        lastupdate.setText(time);
    }

    /**
     * 刷新数据接口
     */
    public interface IRefreshListener{
        void onRefresh();
    }
}
