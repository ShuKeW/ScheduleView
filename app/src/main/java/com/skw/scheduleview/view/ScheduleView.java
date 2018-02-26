package com.skw.scheduleview.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

import com.skw.scheduleview.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @创建人 weishukai
 * @创建时间 18/2/1 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class ScheduleView extends View {
    private static final String TAG = "ScheduleView";
    /**
     * 分隔线的相关
     */
    private Paint linePaint;
    private float lineSize = 2;
    private int lineColor;

    /**
     * 列数，取值1或者7
     */
    private int columnNumber;
    private float columnWidth;
    /**
     * 行高
     */
    private float rowHeight;

    /**
     * 背景色
     */
    private int bgColor;

    /**
     * 绘制时间轴的信息
     */
    private Paint hourTextPaint;
    private Paint hourTextAmPaint;
    private float hourTextWidth;
    private float hourTextSize;
    private int hourTextColor;
    private int hourTextBgColor;

    /**
     * 过去时间的相关
     */
    private Paint pastTimePaint;
    private int pastTimeColor;

    /**
     * 当前时间
     */
    private RectF currentTimeRect;
    private Paint currentTimePaint;
    private int currentTimeColor;
    private int currentTimeTextColor;
    private float currentTimeTextSize;

    /**
     * 添加日程的+的样式信息
     */
    private Paint addPaint;
    private int addTextWidth;
    private float addTextSize;
    private int addTextColor;
    private int addBgColor;
    private AddRectF addRectF;

    /**
     * 全天事件的样式信息
     */
    private Paint allDayEventTitlePaint;
    private TextPaint allDayEventPaint;
    private Paint allDayEventCountPaint;
    private int allDayEventBgColor;
    private float allDayEventTitleSize;
    private int allDayEventTitleColor;
    private int allDayEventDefaultColor;
    private float allDayEventSize;
    private float allDayEventCountTextSize;
    private int allDayEventCountTextColor;
    private float allDayEventRowHeight;
    private int allDayEventMinRow;
    private int allDayEventMaxRow;
    private int allDayEventCurrRow;
    private List<EventRect> allDayEventRectList = new ArrayList<EventRect>();
    //全天事件每天的事件数
    private int[] allDayEventCountList;
    //全天事件的滑动距离
    private PointF originOffsetAllDay;
    private boolean isAllDayOpen;
    private Bitmap bell = BitmapFactory.decodeResource(getResources(), R.mipmap.bell_weekview);
    private Bitmap arrowDown = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_down);
    private Bitmap arrowUp = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_up);

    /**
     * 事件相关
     */
    private TextPaint eventTextPaint;
    private Paint eventBgPaint;
    private float eventTextSize;
    private int eventTextColor;
    private int eventBgColor;
    private int eventBgLineColor;
    private List<EventRect> eventRectList = new ArrayList<EventRect>();

    private Calendar firstDay;
    private Calendar endDay;

    private boolean isFirst;

    private OnEventClickListener onEventClickListener;
    private OnEventAddClickListener onEventAddClickListener;

    /**
     * 手势和滑动相关
     */
    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    /**
     * originOffset记录移动的距离，即偏移量，，x记录横轴，y记录纵轴
     * 注意，并不是记录一次滑动的值
     */
    private PointF originOffset;
    private OverScroller overScroller;
    private float lastCurrY;

    private GestureDetectorCompat gestureDetectorCompat;
    private Direction currentScrollDirection = Direction.NONE;
    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown");
            overScroller.forceFinished(true);
            currentScrollDirection = Direction.NONE;
            return true;//返回true了之后，才会拦截，才会走到自己的onScroll
        }

        /**
         *  移动的过程中不停的回调
         * @param e1    down的时候的event
         * @param e2    每一次移动的event
         * @param distanceX 上一次e2.x减去本次e2.x的值
         * @param distanceY 上一次e2.y减去本次e2.y的值，故负值表示向下移动手指
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.d(TAG, "onScroll  " + e1.getY() + "  " + e2.getY());
//            Log.d(TAG, "distanceX:" + distanceX + "      distanceY:" + distanceY);
            /**
             * 开始滑动，根据刚开始的方向来决定是横还是竖
             */
            if (currentScrollDirection == Direction.NONE) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    //横线暂不做处理
                } else {
                    currentScrollDirection = Direction.VERTICAL;
                }
            }
            if (currentScrollDirection == Direction.VERTICAL) {
                /**
                 * 因为后面的处理是在绘制对象的出事位置上加上这个originOffset.y，所以这里采用-=的计算方式来累加移动的距离
                 */
                if (e1.getY() > allDayEventShowHeight()) {
                    originOffset.y -= distanceY;
                    checkOriginOffsetY(flag_event);
                    addRectF = null;
                } else if (isAllDayNeedScroll()) {
                    originOffsetAllDay.y -= distanceY;
                    checkOriginOffsetY(flag_all_day_event);
                }
            }
            invalidate();
            return true;
        }

        /**
         *  最后手离开的时候并且还有速度的时候回调
         * @param e1    down的时候的event
         * @param e2    最后手离开的时候的event，和onScroll最后一次的e2一样
         * @param velocityX
         * @param velocityY     其实速度，手指向下滑动为正值
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.d(TAG, "onFling  " + e1.getY() + "    " + e2.getY());
//            Log.d(TAG, "velocityX：  " + velocityX + "    velocityY:" + velocityY);
            if (currentScrollDirection == Direction.HORIZONTAL) {

            } else if (currentScrollDirection == Direction.VERTICAL) {
                lastCurrY = 0;
                if (velocityY > 0) {//向下,认max
                    if (e1.getY() > allDayEventShowHeight()) {
                        touchPosition = flag_event;
                        overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, 0, (int) (Math.abs(originOffset.y) + minAllDayEventShowHeight()));
                    } else if (isAllDayNeedScroll()) {
                        touchPosition = flag_all_day_event;
                        overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, 0, (int) (Math.abs(originOffsetAllDay.y)));
                    }
                } else {//反之
                    if (e1.getY() > allDayEventShowHeight()) {
                        touchPosition = flag_event;
                        overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, -(int) (24 * (rowHeight + lineSize) - getHeight() + minAllDayEventShowHeight() - Math.abs(originOffset.y)), 0);
                    } else if (isAllDayNeedScroll()) {
                        touchPosition = flag_all_day_event;
                        overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, -(int) (allDayEventCurrRow * allDayEventRowHeight - Math.abs(originOffset.y)), 0);
                    }
                }
            }
            ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed");
            if (addRectF != null && addRectF.contains(e.getX(), e.getY())) {
                Calendar pointTime = getTimeFromPoint(e.getX(), e.getY());
                if (pointTime != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    onEventAddClickListener.onEventAddClicked(pointTime);
                    addRectF = null;
                    invalidate();
                    return true;
                }
            } else {
                /**
                 * 当点击了全天事件区域
                 */
                if (e.getY() <= allDayEventShowHeight()) {
                    if (e.getX() < hourTextWidth) {
                        if (isAllDayOpen) {
                            isAllDayOpen = false;
                        } else {
                            isAllDayOpen = true;
                        }
                        invalidate();
                        return true;
                    } else if (e.getX() < getWidth() - hourTextWidth) {
                        if (allDayEventRectList != null && allDayEventRectList.size() > 0) {
                            for (EventRect eventRect : allDayEventRectList) {
                                RectF rectF = new RectF(eventRect.rectF.left, eventRect.rectF.top + originOffsetAllDay.y, eventRect.rectF.right, eventRect.rectF.bottom + originOffsetAllDay.y);
                                if (rectF.contains(e.getX(), e.getY())) {
                                    if (onEventClickListener != null) {
                                        playSoundEffect(SoundEffectConstants.CLICK);
                                        onEventClickListener.onEventClick(eventRect.event, eventRect.rectF);
                                        addRectF = null;
                                        invalidate();
                                    }
                                    return true;
                                }
                            }
                        }
                    }

                } else {
                    if (eventRectList != null && eventRectList.size() > 0) {
                        for (EventRect eventRect : eventRectList) {
                            RectF rectF = new RectF(eventRect.rectF.left, eventRect.rectF.top + originOffset.y, eventRect.rectF.right, eventRect.rectF.bottom + originOffset.y);
                            if (rectF.contains(e.getX(), e.getY())) {
                                if (onEventClickListener != null) {
                                    playSoundEffect(SoundEffectConstants.CLICK);
                                    onEventClickListener.onEventClick(eventRect.event, eventRect.rectF);
                                    addRectF = null;
                                    invalidate();
                                }
                                return true;
                            }
                        }
                    }
                    if (onEventAddClickListener != null && e.getX() > hourTextWidth && e.getX() < (getWidth() - hourTextWidth) && e.getY() > allDayEventShowHeight()) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        int hIndex = (int) ((e.getX() - hourTextWidth) / (columnWidth + lineSize));
                        int vIndex = (int) ((Math.abs(originOffset.y) + e.getY() - minAllDayEventShowHeight()) / (rowHeight + lineSize));
                        float left = hourTextWidth + hIndex * (columnWidth + lineSize);
                        float top = vIndex * (rowHeight + lineSize) + originOffset.y + minAllDayEventShowHeight();
                        addRectF = new AddRectF(left, top, left + columnWidth + lineSize, top + rowHeight + lineSize);
                        invalidate();
                        playAddAnim();
                        return true;
                    }
                }
            }
            return true;
        }
    };

    /**
     * 根据点得左边获取日期
     *
     * @param x
     * @param y
     * @return
     */
    private Calendar getTimeFromPoint(float x, float y) {
        if (x > hourTextWidth && x < (getWidth() - hourTextWidth)) {
            int hIndex = (int) ((x - hourTextWidth) / (columnWidth + lineSize));
            int vIndex = (int) ((Math.abs(originOffset.y) + y - minAllDayEventShowHeight()) / (rowHeight + lineSize));
            Calendar firstDay = (Calendar) this.firstDay.clone();
            firstDay.add(Calendar.DAY_OF_MONTH, hIndex);
            firstDay.add(Calendar.HOUR_OF_DAY, vIndex);
            return firstDay;
        }
        return null;
    }

    private int touchPosition;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (overScroller != null && overScroller.computeScrollOffset()) {
//            Log.d(TAG, overScroller.getCurrVelocity() + "  " + overScroller.getStartY() + "  " + overScroller.getCurrY() + "  " + overScroller.getFinalY());
            if (currentScrollDirection == Direction.HORIZONTAL) {

            } else if (currentScrollDirection == Direction.VERTICAL) {
                /**
                 * 为什么这么做呢？
                 * 因为在这之前这个值是保存手拖动的具体，并且实时刷新了页面
                 * 那么手离开给了个加速度之后呢，是继续增加了这个距离
                 * scroll和fling属于一次滑动，所以滑动的距离都是相对于手开始触摸的时候，因此需要叠加
                 */
//                Log.d(TAG, "computeScroll originOffset.y:" + originOffset.y);
                float currY = overScroller.getCurrY();
                switch (touchPosition) {
                    case flag_event:
                        originOffset.y += (currY - lastCurrY);
                        checkOriginOffsetY(flag_event);
                        break;
                    case flag_all_day_event:
                        originOffsetAllDay.y += (currY - lastCurrY);
                        checkOriginOffsetY(flag_all_day_event);
                        break;
                }
                lastCurrY = currY;
            }
            ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
        }
    }

    private final int flag_event = 1;
    private final int flag_all_day_event = 2;

    /**
     * 检查偏移量，不能越界
     */
    private void checkOriginOffsetY(int flag) {
        switch (flag) {
            case flag_event:
                if (originOffset.y >= 0) {
                    originOffset.y = 0;
                } else if (Math.abs(originOffset.y) >= (24 * (rowHeight + lineSize) - getHeight() + minAllDayEventShowHeight())) {
                    originOffset.y = -(24 * (rowHeight + lineSize) - getHeight() + minAllDayEventShowHeight());
                }
                break;
            case flag_all_day_event:
                if (isAllDayNeedScroll()) {
                    if (originOffsetAllDay.y >= 0) {
                        originOffsetAllDay.y = 0;
                    } else if (Math.abs(originOffsetAllDay.y) >= (allDayEventCurrRow - allDayEventMaxRow) * allDayEventRowHeight) {
                        originOffsetAllDay.y = -(allDayEventCurrRow - allDayEventMaxRow) * allDayEventRowHeight;
                    }
                }
                break;
        }

    }

    public ScheduleView(Context context) {
        this(context, null, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScheduleView, 0, 0);
        try {
            lineSize = a.getDimension(R.styleable.ScheduleView_lineSize, lineSize);
            lineColor = a.getColor(R.styleable.ScheduleView_lineColor, lineColor);
            columnNumber = a.getInteger(R.styleable.ScheduleView_columnNumber, columnNumber);
            rowHeight = a.getDimension(R.styleable.ScheduleView_rowHeight, rowHeight);
            bgColor = a.getColor(R.styleable.ScheduleView_bgColor, bgColor);
            hourTextSize = a.getDimension(R.styleable.ScheduleView_hourTextSize, hourTextSize);
            hourTextColor = a.getColor(R.styleable.ScheduleView_hourTextColor, hourTextColor);
            hourTextBgColor = a.getColor(R.styleable.ScheduleView_hourTextBgColor, hourTextBgColor);
            pastTimeColor = a.getColor(R.styleable.ScheduleView_pastTimeColor, pastTimeColor);
            currentTimeColor = a.getColor(R.styleable.ScheduleView_currentTimeColor, currentTimeColor);
            currentTimeTextColor = a.getColor(R.styleable.ScheduleView_currentTimeTextColor, currentTimeTextColor);
            currentTimeTextSize = a.getDimension(R.styleable.ScheduleView_currentTimeTextSize, currentTimeTextSize);
            addTextSize = a.getDimension(R.styleable.ScheduleView_addTextSize, addTextSize);
            addTextColor = a.getColor(R.styleable.ScheduleView_addTextColor, addTextColor);
            addBgColor = a.getColor(R.styleable.ScheduleView_addBgColor, addBgColor);
            allDayEventBgColor = a.getColor(R.styleable.ScheduleView_allDayEventBgColor, allDayEventBgColor);
            allDayEventCountTextSize = a.getDimension(R.styleable.ScheduleView_allDayCountTextSize, allDayEventCountTextSize);
            allDayEventCountTextColor = a.getColor(R.styleable.ScheduleView_allDayCountTextColor, allDayEventCountTextColor);
            allDayEventTitleColor = a.getColor(R.styleable.ScheduleView_allDayEventTitleColor, allDayEventTitleColor);
            allDayEventTitleSize = a.getDimension(R.styleable.ScheduleView_allDayEventTitleSize, allDayEventTitleSize);
            allDayEventDefaultColor = a.getColor(R.styleable.ScheduleView_allDayEventDefaultColor, allDayEventDefaultColor);
            allDayEventSize = a.getDimension(R.styleable.ScheduleView_allDayEventSize, allDayEventSize);
            allDayEventRowHeight = a.getDimension(R.styleable.ScheduleView_allDayEventRowHeight, allDayEventRowHeight);
            allDayEventMinRow = a.getInt(R.styleable.ScheduleView_allDayEventMinRow, allDayEventMinRow);
            allDayEventMaxRow = a.getInt(R.styleable.ScheduleView_allDayEventMaxRow, allDayEventMaxRow);
            eventTextSize = a.getDimension(R.styleable.ScheduleView_eventTextSize, eventTextSize);
            eventTextColor = a.getColor(R.styleable.ScheduleView_eventTextColor, eventTextColor);
            eventBgColor = a.getColor(R.styleable.ScheduleView_eventTextBgColor, eventBgColor);
            eventBgLineColor = a.getColor(R.styleable.ScheduleView_eventTextBgLineColor, eventBgLineColor);
        } finally {
            a.recycle();
        }
        allDayEventCurrRow = allDayEventMinRow;
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("columnNumber must > 0");
        }
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        isFirst = true;
        /**
         * 初始化开始时间和结束时间，一般是一周
         */
        firstDay = Calendar.getInstance();
        /**
         * 这个值时周天表示本周的第一天，本view中使用周一表示第一天
         */
        int dayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "++++" + dayOfWeek);
        if (columnNumber > 1) {
            if (dayOfWeek == 1) {
                dayOfWeek = -6;
            } else {
                dayOfWeek = -(dayOfWeek - 2);
            }
            firstDay.add(Calendar.DAY_OF_WEEK, dayOfWeek);
        }
        firstDay.set(Calendar.HOUR_OF_DAY, 0);
        firstDay.set(Calendar.MINUTE, 0);
        firstDay.set(Calendar.SECOND, 0);
        firstDay.set(Calendar.MILLISECOND, 0);
        endDay = (Calendar) firstDay.clone();
        endDay.add(Calendar.DAY_OF_WEEK, columnNumber - 1);
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);
        firstDay.set(Calendar.MILLISECOND, 1000);

        /**
         * 初始化手势相关
         */

        gestureDetectorCompat = new GestureDetectorCompat(context, gestureListener);
        overScroller = new OverScroller(context);
        originOffset = new PointF(0f, 0f);
        originOffsetAllDay = new PointF(0f, 0f);

        /**
         * 初始化分隔线画笔
         */
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setTextSize(lineSize);
        linePaint.setColor(lineColor);
        lineSize = linePaint.getTextSize();

        /**
         * 初始化时间轴画笔相关
         */
        hourTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourTextPaint.setTextSize(hourTextSize);
        hourTextPaint.setColor(hourTextColor);
        hourTextPaint.setTextAlign(Paint.Align.CENTER);
        hourTextWidth = hourTextPaint.measureText("000时");
        hourTextAmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourTextAmPaint.setTextAlign(Paint.Align.CENTER);
        hourTextAmPaint.setTextSize(hourTextSize);
        hourTextAmPaint.setColor(hourTextColor);

        /**
         * 初始化过去时间的画笔
         */
        pastTimePaint = new Paint();
        pastTimePaint.setColor(pastTimeColor);

        /**
         * 初始化当前时间
         */
        currentTimePaint = new Paint();
        currentTimePaint.setAntiAlias(true);
        currentTimePaint.setTextSize(currentTimeTextSize);

        /**
         * 初始化event画笔
         */
        eventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        eventTextPaint.setColor(eventTextColor);
        eventTextPaint.setTextSize(eventTextSize);
        eventBgPaint = new Paint();

        /**
         * 初始化加号
         */
        addPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        addPaint.setTextSize(addTextSize);
        addPaint.setTextAlign(Paint.Align.CENTER);
        Rect rectAddText = new Rect();
        addPaint.getTextBounds("+", 0, 1, rectAddText);
        addTextWidth = rectAddText.width();

        /**
         * 初始化全天
         */
        isAllDayOpen = false;
        allDayEventTitlePaint = new Paint();
        allDayEventTitlePaint.setAntiAlias(true);
        allDayEventTitlePaint.setTextAlign(Paint.Align.CENTER);
        allDayEventTitlePaint.setColor(allDayEventTitleColor);
        allDayEventTitlePaint.setTextSize(allDayEventTitleSize);
        allDayEventPaint = new TextPaint();
        allDayEventPaint.setColor(allDayEventDefaultColor);
        allDayEventPaint.setTextSize(allDayEventSize);
        allDayEventCountPaint = new TextPaint();
        allDayEventCountPaint.setAntiAlias(true);
        allDayEventCountPaint.setTextAlign(Paint.Align.CENTER);
        allDayEventCountPaint.setTextSize(allDayEventCountTextSize);
        allDayEventCountPaint.setColor(allDayEventCountTextColor);

        scaleAnimationListener = new ScaleAnimationListener();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 计算列的宽度
         */
        if (columnNumber == 1) {
            columnWidth = getWidth() - hourTextWidth - lineSize;
        } else {
            columnWidth = (getWidth() - 2 * hourTextWidth - (columnNumber + 1) * lineSize) / columnNumber;
        }

        /**
         * 第一次展示，默认从当前时间开始展示
         */
        if (isFirst) {
            isFirst = false;
            goToCurrentHour();
        }

        drawPastTime(canvas);
        drawHour(canvas);
        drawLine(canvas);
        drawEvents(canvas);
        drawCurrentTime(canvas);
        drawAdd(canvas);
        drawAllDayEvents(canvas);

    }


    /**
     * 滑动到当前时间
     */
    private void goToCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        Log.d(TAG, "+++++++++++++++       hour    " + hour);
        if (hour < 0) {
            hour = 0;
        }
        float height = hour * (rowHeight + lineSize);
        Log.d(TAG, "--  " + 24 * (rowHeight + lineSize));
        /**
         * 说明当前时间比较靠后，需要移动到最底部
         */
        if (((24 - hour) * (rowHeight + lineSize)) < (getHeight() - minAllDayEventShowHeight())) {
            // TODO: 2018/2/2
//            height = 24 * (rowHeight + lineSize) - getHeight() - allDayEventShowHeight;
            height = 24 * (rowHeight + lineSize) - getHeight();
        }
        /**
         * 因为是上移，需要将内容的y减小，所以用-
         */
        originOffset.y = -height;
        Log.d(TAG, "goToCurrentHour originOffset.y:" + originOffset.y);
//        mDistanceYAll = Math.abs(originOffset.y);
    }

    private void drawPastTime(Canvas canvas) {
        Calendar today = Calendar.getInstance();
        if (today.after(firstDay) && today.before(endDay)) {
            int dayNumber = today.get(Calendar.DAY_OF_YEAR) - firstDay.get(Calendar.DAY_OF_YEAR);
            int hourNumber = today.get(Calendar.HOUR_OF_DAY);
            int minuteNumber = today.get(Calendar.MINUTE);

            /**
             * 画左边整天的
             */
            if (dayNumber > 0) {
                float left = hourTextWidth;
                float top = minAllDayEventShowHeight();
                float right = hourTextWidth + dayNumber * (columnWidth + lineSize);
                float bottom = getHeight();
                canvas.drawRect(left, top, right, bottom, pastTimePaint);
            }

            /**
             * 画今天整小时
             */

            float leftToday = hourTextWidth + dayNumber * columnWidth + dayNumber * lineSize;
            float rightToday = leftToday + columnWidth + lineSize;
            float bottomTodayHour = hourNumber * (rowHeight + lineSize) + originOffset.y + minAllDayEventShowHeight();
            /**
             * 只画显示的时候
             */
            if (bottomTodayHour > minAllDayEventShowHeight()) {
                float topTodayHour = minAllDayEventShowHeight();
                canvas.drawRect(leftToday, topTodayHour, rightToday, bottomTodayHour, pastTimePaint);
            }

            /**
             * 画今天不满一小时的部分
             */
            float topTodayMinute = hourNumber * (rowHeight + lineSize) + originOffset.y + minAllDayEventShowHeight();
            float bottomTodayMinute = topTodayMinute + rowHeight / 60 * minuteNumber;
            if (bottomTodayMinute > minAllDayEventShowHeight()) {
                if (topTodayMinute < minAllDayEventShowHeight()) {
                    topTodayMinute = minAllDayEventShowHeight();
                }
                canvas.drawRect(leftToday, topTodayMinute, rightToday, bottomTodayMinute, pastTimePaint);
            }
        }
    }

    /**
     * 画时间
     */
    private void drawHour(Canvas canvas) {
        for (int i = 0; i < 24; i++) {
            float y = i * rowHeight + (i + 1) * lineSize + originOffset.y + minAllDayEventShowHeight();
            if (i == 0) {
                y += hourTextSize;
            } else {
                y += (hourTextSize / 3);
            }
            /**
             * 同理，只画屏幕内显示的
             */
            if (y <= getHeight() + hourTextSize && y >= minAllDayEventShowHeight() - hourTextSize) {
                canvas.drawText("" + i + "时", (float) (hourTextWidth * 0.8) / 2, y, hourTextPaint);
            }

            // canvas.drawText("时", mHourTextWidth / 2, top + 2 * mHourTextSize
            // + mHourTextSize / 2, mHourTextAmPaint);
            // if (top > mAllDayEventShowHeight - mHourTextSize && top <
            // mAllDayEventShowHeight + mRowHeight - mHourTextSize) {
            // if (i > 12) {
            // canvas.drawText("下午", mHourTextWidth / 2, top + 2 *
            // mHourTextSize, mHourTextAmPaint);
            // } else {
            // canvas.drawText("上午", mHourTextWidth / 2, top + 2 *
            // mHourTextSize, mHourTextAmPaint);
            // }
            // }
        }
    }

    /**
     * 重写画线的方法 不再按照小格子一根一根的短线来话
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        /**
         * 画固定的竖线
         */
        for (int i = 0; i < columnNumber + 1; i++) {
            float startX = hourTextWidth + i * lineSize + i * columnWidth;
            float startY = minAllDayEventShowHeight();
            canvas.drawLine(startX, startY, startX, startY + getHeight(), linePaint);
        }
        /**
         * 画24根横线,可根据自己需求改变循环次数来决定是否画
         */
        for (int i = 0; i < 24; i++) {
            float startX = 0;
            if (i > 0) {
                startX = hourTextWidth - hourTextWidth / 4 + 3;
            }
            float startY = minAllDayEventShowHeight() + i * lineSize + i * rowHeight + originOffset.y;
            if (startY >= minAllDayEventShowHeight() && startY <= getHeight()) {
                canvas.drawLine(startX, startY, startX + getWidth(), startY, linePaint);
            }
        }
    }

    private void drawEvents(Canvas canvas) {
        if (eventRectList != null && eventRectList.size() > 0) {
            for (int i = 0; i < eventRectList.size(); i++) {
                EventRect eventRect = eventRectList.get(i);
                RectF eventRectF = null;
                RectF eventRectFLineKuang = null;
                RectF eventRectFHeadLine = null;
                if (columnNumber == 1) {
                    eventRectFLineKuang = new RectF(eventRect.rectF.left, eventRect.rectF.top + originOffset.y, eventRect.rectF.right, eventRect.rectF.bottom + originOffset.y);
                    eventRectFHeadLine = new RectF(eventRect.rectF.left, eventRect.rectF.top + originOffset.y + 1, eventRect.rectF.left + 2 * lineSize, eventRect.rectF.bottom + originOffset.y - 1);
                    eventRectF = new RectF(eventRect.rectF.left + 2 * lineSize, eventRect.rectF.top + originOffset.y + 1, eventRect.rectF.right - 1, eventRect.rectF.bottom + originOffset.y - 1);
                } else {
                    eventRectFLineKuang = new RectF(eventRect.rectF.left, eventRect.rectF.top + originOffset.y, eventRect.rectF.right, eventRect.rectF.bottom + originOffset.y);
                    eventRectFHeadLine = new RectF(eventRect.rectF.left + 1, eventRect.rectF.top + originOffset.y, eventRect.rectF.right - 1, eventRect.rectF.top + originOffset.y + 2 * lineSize);
                    eventRectF = new RectF(eventRect.rectF.left + 1, eventRect.rectF.top + originOffset.y + 2 * lineSize, eventRect.rectF.right - 1, eventRect.rectF.bottom + originOffset.y - 1);
                }
                if (eventRectFLineKuang.top > getHeight() && eventRectFLineKuang.bottom < minAllDayEventShowHeight()) {
                    //没有显示进来就不画了
                } else {
                    eventBgPaint.setColor(eventRect.event.getSideLineColor() == 0 ? allDayEventDefaultColor : eventRect.event.getSideLineColor());
                    canvas.drawRect(eventRectFLineKuang, eventBgPaint);
                    eventBgPaint.setColor(eventRect.event.getColor() == 0 ? allDayEventDefaultColor : eventRect.event.getColor());
                    canvas.drawRect(eventRectF, eventBgPaint);
                    eventBgPaint.setColor(eventRect.event.getHeadLineColor() == 0 ? allDayEventDefaultColor : eventRect.event.getHeadLineColor());
                    canvas.drawRect(eventRectFHeadLine, eventBgPaint);
                    if (eventRectFLineKuang.right - eventRectFLineKuang.left > eventTextSize && eventRectFLineKuang.bottom - eventRectFLineKuang.top > eventTextSize) {
                        eventTextPaint.setColor(eventRect.event.getTextColor());
                        drawText(eventRect.event.getContent(), canvas, eventTextPaint, eventRectF, (int) (eventRectF.right - eventRectF.left), eventTextSize);
                    }
                }
            }
        }
    }

    private void drawText(String text, Canvas canvas, TextPaint textPaint, RectF rectF, int width, float textSize) {
        StaticLayout staticLayout = new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        if (staticLayout.getHeight() > rectF.bottom - rectF.top) {
            int availableLineCount = (int) ((rectF.bottom - rectF.top) / (staticLayout.getHeight() / staticLayout.getLineCount()));
            int charLineCount = (int) (width / textSize);
            int availableWidth = (int) (availableLineCount * charLineCount * textSize);
            CharSequence charSequence = TextUtils.ellipsize(text, textPaint, availableWidth, TextUtils.TruncateAt.END);
            staticLayout = new StaticLayout(charSequence, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        }
        canvas.save();
        canvas.translate(rectF.left, rectF.top);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawCurrentTime(Canvas canvas) {
        Calendar today = Calendar.getInstance();
        if (today.after(firstDay) && today.before(endDay)) {
            int dayNumber = today.get(Calendar.DAY_OF_YEAR) - firstDay.get(Calendar.DAY_OF_YEAR);
            int hourNumber = today.get(Calendar.HOUR_OF_DAY);
            int minuteNumber = today.get(Calendar.MINUTE);

            float leftToday = hourTextWidth + dayNumber * columnWidth + dayNumber * lineSize;
            float rightToday = leftToday + columnWidth + lineSize;
            float topTodayMinute = hourNumber * (rowHeight + lineSize) + originOffset.y + minAllDayEventShowHeight();
            float bottomTodayMinute = topTodayMinute + rowHeight / 60 * minuteNumber;

            /**
             * 过去的时间的最后就是当前时间
             */
            currentTimeRect = new RectF(leftToday + 4, bottomTodayMinute - 2, rightToday, bottomTodayMinute);

            currentTimePaint.setColor(currentTimeColor);
            canvas.drawRect(currentTimeRect, currentTimePaint);

            Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.mipmap.scheduleview_current_time_icon);
            float width = currentTimePaint.measureText("00:000");
            RectF rectF = new RectF(currentTimeRect.left - 4, currentTimeRect.top - currentTimeTextSize / 2 - 2, currentTimeRect.left + width,
                    currentTimeRect.bottom + currentTimeTextSize / 2 + 2);
            canvas.drawBitmap(arrow, null, rectF, currentTimePaint);

            String time = getDataHour(today.getTimeInMillis());
            Paint.FontMetrics fontMetrics = currentTimePaint.getFontMetrics();
            float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2 - 1;
            currentTimePaint.setColor(currentTimeTextColor);
            canvas.drawText(time, rectF.left + 2, baseline, currentTimePaint);
        }
    }

    private String getDataHour(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String str = format.format(new Date(timeStamp));
        return str;
    }

    private void drawAdd(Canvas canvas) {
        if (addRectF != null) {
            // canvas.save();
            addPaint.setColor(addBgColor);
            canvas.drawRect(addRectF, addPaint);
            addPaint.setColor(addTextColor);
            Paint.FontMetrics fontMetrics = addPaint.getFontMetrics();
            float baseline = (addRectF.bottom + addRectF.top - fontMetrics.bottom - fontMetrics.top) / 2 - 1;
            canvas.drawText("+", addRectF.centerX(), baseline, addPaint);
            // canvas.restore();
        }
    }

    private void drawAllDayEvents(Canvas canvas) {
        /**
         * step 1:画背景
         */
        allDayEventPaint.setColor(allDayEventBgColor);
        canvas.drawRect(0, 0, getWidth(), allDayEventShowHeight(), allDayEventPaint);

        /**
         * step 2:画最上边一条线
         */
        canvas.drawLine(0, 0, getWidth(), lineSize, linePaint);
//        /**
//         * step 2:画过去的时间
//         * 注释，因为过去的时间和下面过去的时间容易造成视图上的不清晰
//         */
//            Calendar today = Calendar.getInstance();
//            int year = today.get(Calendar.YEAR) - firstDay.get(Calendar.YEAR);
//            if (year == 0) {
//                int dayNumber = today.get(Calendar.DAY_OF_YEAR) - firstDay.get(Calendar.DAY_OF_YEAR) + 1;
//                float right = hourTextWidth + dayNumber * (columnWidth + lineSize);
//                canvas.drawRect(hourTextWidth, 0, right, allDayEventShowHeight, pastTimePaint);
//            } else if (year < 0) {
//                canvas.drawRect(hourTextWidth, 0, getWidth() - hourTextWidth, allDayEventShowHeight, pastTimePaint);
//            }

        /**
         * step 3:画竖线
         */
        for (int i = 0; i < (columnNumber + 1); i++) {
            float startX = hourTextWidth + i * (columnWidth + lineSize);
            float startY = lineSize;
            float endX = startX;
            float endY = startY + allDayEventShowHeight();
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }

        /**
         * step 4：画左边标题
         */
        canvas.drawText("全天", hourTextWidth / 2, allDayEventRowHeight * allDayEventMinRow / 2, allDayEventTitlePaint);
        /**
         * step 5:画标识展开的箭头
         */
        if (allDayEventCurrRow > allDayEventMinRow) {
            float left = hourTextWidth / 2 - arrowUp.getWidth() / 2;
            float bottom = allDayEventShowHeight() - lineSize - arrowUp.getHeight() / 2;
            RectF allDayEventArrowRect = new RectF(left, bottom - arrowUp.getHeight(), left + arrowUp.getWidth(), bottom);
            if (isAllDayOpen) {
                canvas.drawBitmap(arrowUp, null, allDayEventArrowRect, allDayEventTitlePaint);
            } else {
                canvas.drawBitmap(arrowDown, null, allDayEventArrowRect, allDayEventTitlePaint);
            }
        }

        /**
         * step 6:画事件和铃铛
         */
        if (allDayEventCurrRow > allDayEventMinRow && !isAllDayOpen) {
            if (allDayEventCountList != null && allDayEventCountList.length > 0) {
                for (int i = 0; i < allDayEventCountList.length; i++) {
                    if (allDayEventCountList[i] > 0) {
                        float left = (float) (hourTextWidth + i * (columnWidth + lineSize) + columnWidth / 2 - bell.getWidth() * 0.85);
                        float top = allDayEventShowHeight() / 2 - bell.getHeight() / 2;
                        float right = left + bell.getWidth();
                        float bottom = top + bell.getHeight();
                        RectF allDayEventBellRect = new RectF(left, top, right, bottom);
                        canvas.drawBitmap(bell, null, allDayEventBellRect, allDayEventCountPaint);
                        canvas.drawText("" + allDayEventCountList[i], right + allDayEventCountTextSize / 2, allDayEventShowHeight() / 2 + allDayEventCountTextSize / 2 - 3, allDayEventCountPaint);
                    }
                }
            }
        } else {
            if (allDayEventRectList != null && allDayEventRectList.size() > 0) {
                for (EventRect allDayEventRect : allDayEventRectList) {
                    RectF eventRectF = null;
                    RectF eventRectFLineKuang = null;
                    RectF eventRectFHeadLine = null;
                    eventRectFLineKuang = new RectF(allDayEventRect.rectF.left, allDayEventRect.rectF.top + originOffsetAllDay.y, allDayEventRect.rectF.right, allDayEventRect.rectF.bottom + originOffsetAllDay.y);
                    eventRectFHeadLine = new RectF(allDayEventRect.rectF.left, allDayEventRect.rectF.top + originOffsetAllDay.y + 1, allDayEventRect.rectF.left + 2 * lineSize, allDayEventRect.rectF.bottom + originOffsetAllDay.y - 1);
                    eventRectF = new RectF(allDayEventRect.rectF.left + 2 * lineSize, allDayEventRect.rectF.top + originOffsetAllDay.y + 1, allDayEventRect.rectF.right - 1, allDayEventRect.rectF.bottom + originOffsetAllDay.y - 1);
                    if (eventRectFLineKuang.top < (allDayEventShowHeight() - lineSize) && eventRectFLineKuang.bottom > (allDayEventShowHeight() - lineSize)) {
                        eventRectFLineKuang.bottom = (allDayEventShowHeight() - lineSize);
                        eventRectFHeadLine.bottom = (allDayEventShowHeight() - lineSize);
                        eventRectF.bottom = (allDayEventShowHeight() - lineSize);
                    }
                    if (eventRectFLineKuang.top >= (allDayEventShowHeight() - lineSize) || eventRectFLineKuang.bottom <= 0) {
                        //没有显示进来就不画了
                    } else {
                        allDayEventPaint.setColor(allDayEventRect.event.getSideLineColor() == 0 ? eventBgLineColor : allDayEventRect.event.getSideLineColor());
                        canvas.drawRect(eventRectFLineKuang, allDayEventPaint);
                        allDayEventPaint.setColor(allDayEventRect.event.getColor() == 0 ? eventBgColor : allDayEventRect.event.getColor());
                        canvas.drawRect(eventRectF, allDayEventPaint);
                        allDayEventPaint.setColor(allDayEventRect.event.getHeadLineColor() == 0 ? eventBgLineColor : allDayEventRect.event.getHeadLineColor());
                        canvas.drawRect(eventRectFHeadLine, allDayEventPaint);
                        if (eventRectF.bottom - eventRectF.top > allDayEventSize && eventRectF.right - eventRectF.left > allDayEventSize) {
                            allDayEventPaint.setColor(allDayEventRect.event.getTextColor());
                            drawText(allDayEventRect.event.getContent(), canvas, allDayEventPaint, eventRectF, (int) (eventRectF.right - eventRectF.left), allDayEventSize);
                        }
                    }
                }
            }
        }
        /**
         * step 7:画全天时间块的上下两根线
         */
        canvas.drawLine(0, allDayEventShowHeight() - lineSize, getWidth(), allDayEventShowHeight(), linePaint);
    }

    /**
     * 传入events
     *
     * @param scheduleViewEventList
     */

    public void setEvents(List<ScheduleViewEvent> scheduleViewEventList) {
        eventRectList.clear();
        if (scheduleViewEventList != null && scheduleViewEventList.size() > 0) {
            sortEvents(scheduleViewEventList);
            groupEvents(scheduleViewEventList);
            invalidate();
        }
    }


    /**
     * 按照开始时间来排序所有event，当然如果源数据已经排好了可以省略这一步
     * 排序的目的是为了便于把相交的event分在一组
     *
     * @param scheduleViewEventList
     */
    private void sortEvents(List<ScheduleViewEvent> scheduleViewEventList) {
        ScheduleViewEvent temp;
        boolean hasChange;
        for (int i = 0; i < scheduleViewEventList.size(); i++) {
            hasChange = false;
            for (int j = 0; j < scheduleViewEventList.size() - i - 1; j++) {
                if (scheduleViewEventList.get(j).getStartTime().compareTo(scheduleViewEventList.get(j + 1).getStartTime()) > 0) {
                    temp = scheduleViewEventList.get(j);
                    scheduleViewEventList.set(j, scheduleViewEventList.get(j + 1));
                    scheduleViewEventList.set((j + 1), temp);
                    hasChange = true;
                }
            }
            if (!hasChange) {
                break;
            }

        }
    }

    /**
     * 将event有交集的分为一组,前提是已经排序了
     *
     * @param scheduleViewEventList
     */
    private List<List<ScheduleViewEvent>> groupEvents(List<ScheduleViewEvent> scheduleViewEventList) {
        List<List<ScheduleViewEvent>> scheduleViewEventGroup = new ArrayList<>();

        Calendar endTime = null;

        for (int i = 0; i < scheduleViewEventList.size(); i++) {
            ScheduleViewEvent event = scheduleViewEventList.get(i);
            if (endTime == null || event.getStartTime().compareTo(endTime) >= 0) {
                scheduleViewEventGroup.add(new ArrayList<ScheduleViewEvent>());
                /**
                 * 放在这里计算出在页面的rect
                 * 因为分组的目的就是为了计算出在一组的事件在页面的width，因为width = columnWidth/groupIndex.size
                 * 所以避免了再做循环计算，就在此处计算了
                 */
                if (scheduleViewEventGroup.size() > 1) {
                    eventRectList.addAll(calculateEventRect(scheduleViewEventGroup.get(scheduleViewEventGroup.size() - 2)));
                }
            }
            scheduleViewEventGroup.get(scheduleViewEventGroup.size() - 1).add(event);
            if (endTime == null || event.getEndTime().compareTo(endTime) > 0) {
                endTime = event.getEndTime();
            }
        }
        /**
         * 当循环完成之后，把最后一组计算了
         */
        eventRectList.addAll(calculateEventRect(scheduleViewEventGroup.get(scheduleViewEventGroup.size() - 1)));
        return scheduleViewEventGroup;
    }

    /**
     * 将分组了event计算出在页面的rect
     *
     * @param groupIndex
     */
    private List<EventRect> calculateEventRect(List<ScheduleViewEvent> groupIndex) {
        List<EventRect> eventRectList = new ArrayList<>();
        for (int i = 0; i < groupIndex.size(); i++) {
            ScheduleViewEvent event = groupIndex.get(i);
            EventRect eventRect = new EventRect();
            eventRect.event = event;
            float width = (columnWidth + lineSize) / groupIndex.size();
            if (event.getStartTime().compareTo(firstDay) < 0) {
                event.setStartTime(firstDay);
            }
            if (event.getEndTime().compareTo(endDay) > 0) {
                event.setEndTime(endDay);
            }
            int dayOfWeek = event.getStartTime().get(Calendar.DAY_OF_WEEK) - firstDay.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == -1) {
                dayOfWeek = 6;
            }
            eventRect.rectF.left = hourTextWidth + dayOfWeek * columnWidth + dayOfWeek * lineSize + i * width;
            eventRect.rectF.right = eventRect.rectF.left + width;
            /**
             * 计算的时候，和originOffset无关，只需要计算出在整个表格中的位置，然后在绘制的根据originOffset来确定当前的位置
             * 最后+allDayEventShowHeight的原因是因为顶格显示全天事件，图表以及事件都是从全天事件之下开始绘制，相当于其实坐标不再是0，而是allDayEventShowHeight
             */
            eventRect.rectF.top = event.getStartTime().get(Calendar.HOUR_OF_DAY) * (rowHeight + lineSize) + rowHeight / 60 * event.getStartTime().get(Calendar.MINUTE) + minAllDayEventShowHeight();
            eventRect.rectF.bottom = event.getEndTime().get(Calendar.HOUR_OF_DAY) * (rowHeight + lineSize) + rowHeight / 60 * event.getEndTime().get(Calendar.MINUTE) + minAllDayEventShowHeight();
            eventRectList.add(eventRect);

        }
        return eventRectList;
    }

    /**
     * 传入全天事件
     *
     * @param allDayEventList
     */
    public void setAllDayEvents(List<ScheduleViewEvent> allDayEventList) {
        allDayEventRectList.clear();
        if (allDayEventList != null && allDayEventList.size() > 0) {
            List<EventRect> eventRectList = getAllDayEventCountList(allDayEventList);
            List<EventRect>[] eventListArr = sortAndGroupAllDayEventList(eventRectList);
            combinAllDayEventList(eventListArr);
            invalidate();
        }

    }


    /**
     * 获取每天有几个事件
     *
     * @param allDayEventList
     */
    private List<EventRect> getAllDayEventCountList(List<ScheduleViewEvent> allDayEventList) {
        allDayEventCountList = new int[columnNumber];
        List<EventRect> eventRectList = new ArrayList<>();
        for (ScheduleViewEvent event : allDayEventList) {
            EventRect eventRect = getAllDayEventIndex(event);
            eventRectList.add(eventRect);
            int startIndex = eventRect.startIndex;
            while (startIndex < eventRect.endIndex) {
                allDayEventCountList[startIndex]++;
                startIndex++;
            }
        }
        return eventRectList;
    }

    /**
     * 将时间转换为下标
     *
     * @param event
     * @return
     */
    private EventRect getAllDayEventIndex(ScheduleViewEvent event) {
        EventRect eventRect = new EventRect();
        eventRect.event = event;
        /**
         * 此处默认你传进来的全天事件肯定是本天的，本view内部不做处理
         */
        if (columnNumber == 1) {
            eventRect.startIndex = 0;
            eventRect.endIndex = 1;
        } else {
            if (event.getStartTime().before(firstDay)) {
                eventRect.startIndex = 0;
            } else {
                int dayOfWeek = event.getStartTime().get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == 1) {
                    dayOfWeek = 6;
                } else {
                    dayOfWeek -= 2;
                }
                eventRect.startIndex = dayOfWeek;
            }
            if (event.getEndTime().after(endDay)) {
                eventRect.endIndex = 7;
            } else {
                int dayOfWeek = event.getEndTime().get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == 1) {
                    dayOfWeek = 7;
                } else {
                    dayOfWeek -= 1;
                }
                eventRect.endIndex = dayOfWeek;
            }
        }
        return eventRect;
    }

    /**
     * 分组排序全天事件,开始时间一样的在一组，组内按照时间跨度由大到小排序
     *
     * @param eventRectList
     */
    private List<EventRect>[] sortAndGroupAllDayEventList(List<EventRect> eventRectList) {
        List<EventRect>[] scheduleListArr = new List[columnNumber];
        /**
         * 按照列数来循环
         */
        for (int i = 0; i < columnNumber; i++) {
            scheduleListArr[i] = new ArrayList<>();
            for (int m = 0; m < eventRectList.size(); m++) {
                EventRect eventRect = eventRectList.get(m);
                if (i == eventRect.startIndex) {
                    if (scheduleListArr[i].size() == 0) {
                        scheduleListArr[i].add(eventRect);
                        eventRectList.remove(eventRect);
                        m--;
                        continue;
                    }
                    for (int j = scheduleListArr[i].size() - 1; j >= 0; j--) {
                        /**
                         * 起始时间一样的事件按照事件的跨度来排序
                         */
                        if ((eventRect.endIndex - eventRect.startIndex) <= (scheduleListArr[i].get(j).endIndex - scheduleListArr[i].get(j).startIndex)) {
                            if (j == scheduleListArr[i].size() - 1) {
                                scheduleListArr[i].add(eventRect);
                            } else {
                                scheduleListArr[i].add(j + 1, eventRect);
                            }
                            eventRectList.remove(eventRect);
                            m--;
                            break;
                        } else if (j == 0) {
                            scheduleListArr[i].add(0, eventRect);
                            eventRectList.remove(eventRect);
                            m--;
                            break;
                        }
                    }
                }
            }
        }
        return scheduleListArr;
    }

    /**
     * 排序全天事件，是指绘制顺序
     * 规则：按照开始时间顺序，尽可能多的把事件排在一行，这样节省页面上的空间
     *
     * @param eventListArr
     */
    private void combinAllDayEventList(List<EventRect>[] eventListArr) {
        if (columnNumber == 1) {
            for (int i = 0; i < eventListArr[0].size(); i++) {
                allDayEventRectList.add(calculateAllDayEventRect(eventListArr[0].get(i), i));
                allDayEventCurrRow = allDayEventRectList.size();
            }
        } else {
            List<List<EventRect>> allDayEventGroupList = new ArrayList<>();
            for (int i = 0; i < eventListArr.length - 1; i++) {
                for (int m = 0; m < eventListArr[i].size(); m++) {
                    List<EventRect> eventRectList = new ArrayList<>();
                    eventRectList.add(eventListArr[i].get(m));
                    allDayEventGroupList.add(eventRectList);
                    for (int j = i + 1; j < eventListArr.length; j++) {
                        if (eventListArr[j].size() > 0 && eventRectList.get(eventRectList.size() - 1).endIndex <= eventListArr[j].get(0).startIndex) {
                            eventRectList.add(eventListArr[j].get(0));
                            eventListArr[j].remove(0);
                        }
                    }
                    allDayEventRectList.addAll(calculateAllDayEventRectList(eventRectList, allDayEventGroupList.size() - 1));
                }
            }
            allDayEventCurrRow = allDayEventGroupList.size();
        }
    }

    private List<EventRect> calculateAllDayEventRectList(List<EventRect> eventRectList, int rowNumber) {
        for (EventRect eventRect : eventRectList) {
            calculateAllDayEventRect(eventRect, rowNumber);
        }
        return eventRectList;
    }

    private EventRect calculateAllDayEventRect(EventRect eventRect, int rowNumber) {
        eventRect.rectF.left = hourTextWidth + lineSize + eventRect.startIndex * (columnWidth + lineSize);
        eventRect.rectF.right = hourTextWidth + lineSize + eventRect.endIndex * (columnWidth + lineSize) - lineSize;
        eventRect.rectF.top = rowNumber * allDayEventRowHeight + lineSize;
        eventRect.rectF.bottom = rowNumber * allDayEventRowHeight + allDayEventRowHeight + lineSize;
        return eventRect;
    }

    /**
     * 获取当前全天事件展示的行数
     *
     * @return
     */
    private int getAllDayEventRow() {
        if (!isAllDayOpen) {
            return allDayEventMinRow;
        } else {
            if (allDayEventCurrRow > allDayEventMaxRow) {
                return allDayEventMaxRow;
            } else {
                return allDayEventCurrRow;
            }
        }
    }

    private boolean isAllDayNeedScroll() {
        return isAllDayOpen && allDayEventCurrRow > allDayEventMaxRow;
    }

    private float allDayEventShowHeight() {
        return getAllDayEventRow() * allDayEventRowHeight + 2 * lineSize;
    }

    private float minAllDayEventShowHeight() {
        return allDayEventMinRow * allDayEventRowHeight + 2 * lineSize;
    }

    /**
     * 将事件转换成图形上的坐标对象
     */
    private class EventRect {

        public ScheduleViewEvent event;

        public RectF rectF = new RectF();

        /**
         * 全天事件的开始时间转换成开始和结束天，便于计算
         */
        public int startIndex;
        public int endIndex;

        public EventRect() {
        }

        public EventRect(ScheduleViewEvent event, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
        }
    }

    /**
     * 用于动画的加号的矩形
     */
    private class AddRectF extends RectF {

        private float width;

        private float height;

        public AddRectF(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.width = right - left;
            this.height = bottom - top;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            float value = (width - this.width) / 2;
            left -= value;
            right += value;
            top -= value;
            bottom += value;

            this.width = width;

        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            float value = (width - this.width) / 2;
            left -= value;
            right += value;
            top -= value;
            bottom += value;

            this.height = height;
        }
    }

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private ScaleAnimationListener scaleAnimationListener;

    private void playAddAnim() {
        float start = 0;
        float animZoomIn1 = 0;
        float animZoomOut1 = 0;
        float animZoomOut2 = 0;
        if (columnNumber == 1) {
            start = columnWidth * 0.9F;
            animZoomIn1 = columnWidth * 0.992F;
            animZoomOut1 = 1.003F * columnWidth;
            animZoomOut2 = 1.001F * columnWidth;
        } else {
            start = columnWidth * 0.7F;
            animZoomIn1 = columnWidth * 0.9F;
            animZoomOut1 = 1.02F * columnWidth;
            animZoomOut2 = 1.01F * columnWidth;
        }

        ValueAnimator animScale1 = ObjectAnimator.ofFloat(addRectF, "width", start, animZoomOut1);
        animScale1.setDuration(80);
        animScale1.setInterpolator(decelerateInterpolator);
        ValueAnimator animScale2 = ObjectAnimator.ofFloat(addRectF, "width", animZoomOut1, animZoomIn1);
        animScale2.setDuration(50);
        animScale2.setInterpolator(accelerateInterpolator);

        ValueAnimator animScale3 = ObjectAnimator.ofFloat(addRectF, "width", animZoomIn1, animZoomOut2);
        animScale3.setDuration(40);
        animScale3.setInterpolator(decelerateInterpolator);

        ValueAnimator animScale4 = ObjectAnimator.ofFloat(addRectF, "width", animZoomOut2, columnWidth);
        animScale4.setDuration(40);
        animScale4.setInterpolator(accelerateInterpolator);

        animScale1.addUpdateListener(scaleAnimationListener);
        animScale2.addUpdateListener(scaleAnimationListener);
        animScale3.addUpdateListener(scaleAnimationListener);
        animScale4.addUpdateListener(scaleAnimationListener);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animScale1, animScale2, animScale3, animScale4);
        animSet.start();
    }

    private class ScaleAnimationListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            ScheduleView.this.invalidate();
        }
    }

    public void setOnEventClickListener(OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
    }

    public void setOnEventAddClickListener(OnEventAddClickListener onEventAddClickListener) {
        this.onEventAddClickListener = onEventAddClickListener;
    }

    /**
     * 点击了event的回调
     */
    public interface OnEventClickListener {
        void onEventClick(ScheduleViewEvent event, RectF eventRectF);
    }


    public interface OnEventAddClickListener {

        void onEventAddClicked(Calendar time);
    }

}
