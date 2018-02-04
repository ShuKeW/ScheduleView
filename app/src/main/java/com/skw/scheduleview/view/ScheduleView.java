package com.skw.scheduleview.view;

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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.skw.scheduleview.R;

import java.util.ArrayList;
import java.util.Calendar;
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
     * 列数
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
    private Paint pastTimeNowPaint;
    private int pastTimeColor;
    private int pastTimeLineColor;
    private int pastTimeNowTextColor;
    private float pastTimeNowTextSize;

    /**
     * 添加日程的+的样式信息
     */
    private Paint addTextPaint;
    private int addTextWidth;
    private float addTextSize;
    private int addTextColor;
    private int addBgColor;

    /**
     * 全天事件的样式信息
     */
    private Paint allDayEventTextPaint;
    private Paint allDayEventBgTextPaint;
    private Paint allDayEventCountPaint;
    private float allDayEventCountTextSize;
    private int allDayEventCountTextColor;
    private float allDayEventTextSize;
    private int allDayEventTextColor;
    private int allDayEventBgTextColor;
    private float allDayEventHeight;

    private float allDayEventShowHeight;

    /**
     * 事件相关
     */
    private Paint eventTextPaint;
    private Paint eventBgPaint;
    private float eventTextSize;
    private int eventTextColor;
    private int eventBgColor;
    private int eventBgLineColor;
    private List<EventRect> eventRectList = new ArrayList<EventRect>();

    private Calendar firstDay;
    private Calendar endDay;

    private boolean isFirst;

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
            Log.d(TAG, "onDown");
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
                originOffset.y -= distanceY;
                checkOriginOffsetY();
                Log.d(TAG, "onScroll  originOffset.y" + originOffset.y);
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
            Log.d(TAG, "onFling  " + e1.getY() + "    " + e2.getY());
            Log.d(TAG, "velocityX：  " + velocityX + "    velocityY:" + velocityY);
            if (currentScrollDirection == Direction.HORIZONTAL) {

            } else if (currentScrollDirection == Direction.VERTICAL) {
                lastCurrY = 0;
                if (velocityY > 0) {//向下,认max
                    overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, 0, (int) (Math.abs(originOffset.y) + allDayEventShowHeight));
                } else {//反之
                    overScroller.fling(0, 0, 0, (int) velocityY, 0, 0, -(int) (24 * (rowHeight + lineSize) - getHeight() + allDayEventShowHeight - Math.abs(originOffset.y)), 0);
                }
            }
            ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
            Log.d(TAG, "onShowPress");
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Log.d(TAG, "onLongPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(e);
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.d(TAG, "computeScroll");
        if (overScroller != null && overScroller.computeScrollOffset()) {
            Log.d(TAG, overScroller.getCurrVelocity() + "  " + overScroller.getStartY() + "  " + overScroller.getCurrY() + "  " + overScroller.getFinalY());
            if (currentScrollDirection == Direction.HORIZONTAL) {

            } else if (currentScrollDirection == Direction.VERTICAL) {
                /**
                 * 为什么这么做呢？
                 * 因为在这之前这个值是保存手拖动的具体，并且实时刷新了页面
                 * 那么手离开给了个加速度之后呢，是继续增加了这个距离
                 * scroll和fling属于一次滑动，所以滑动的距离都是相对于手开始触摸的时候，因此需要叠加
                 */
                Log.d(TAG, "computeScroll originOffset.y:" + originOffset.y);
                float currY = overScroller.getCurrY();
                originOffset.y += (currY - lastCurrY);
                lastCurrY = currY;
                checkOriginOffsetY();
            }
            ViewCompat.postInvalidateOnAnimation(ScheduleView.this);
        }
    }

    /**
     * 检查偏移量，不能越界
     */
    private void checkOriginOffsetY() {
        if (originOffset.y >= 0) {
            originOffset.y = 0;
        } else if (Math.abs(originOffset.y) >= (24 * (rowHeight + lineSize) - getHeight() + allDayEventShowHeight)) {
            originOffset.y = -(24 * (rowHeight + lineSize) - getHeight() + allDayEventShowHeight);
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
            pastTimeLineColor = a.getColor(R.styleable.ScheduleView_pastTimeLineColor, pastTimeLineColor);
            pastTimeNowTextColor = a.getColor(R.styleable.ScheduleView_pastTimeNowTextColor, pastTimeNowTextColor);
            pastTimeNowTextSize = a.getDimension(R.styleable.ScheduleView_pastTimeNowTextSize, pastTimeNowTextSize);
            addTextSize = a.getDimension(R.styleable.ScheduleView_addTextSize, addTextSize);
            addTextColor = a.getColor(R.styleable.ScheduleView_addTextColor, addTextColor);
            addBgColor = a.getColor(R.styleable.ScheduleView_addBgColor, addBgColor);
            allDayEventCountTextSize = a.getDimension(R.styleable.ScheduleView_allDayCountTextSize, allDayEventCountTextSize);
            allDayEventCountTextColor = a.getColor(R.styleable.ScheduleView_allDayCountTextColor, allDayEventCountTextColor);
            allDayEventTextSize = a.getDimension(R.styleable.ScheduleView_allDayTextSize, allDayEventTextSize);
            allDayEventTextColor = a.getColor(R.styleable.ScheduleView_allDayTextColor, allDayEventTextColor);
            allDayEventBgTextColor = a.getColor(R.styleable.ScheduleView_allDayTextBgColor, allDayEventBgTextColor);
            allDayEventHeight = a.getDimension(R.styleable.ScheduleView_allDayHeight, allDayEventHeight);
            eventTextSize = a.getDimension(R.styleable.ScheduleView_eventTextSize, eventTextSize);
            eventTextColor = a.getColor(R.styleable.ScheduleView_eventTextColor, eventTextColor);
            eventBgColor = a.getColor(R.styleable.ScheduleView_eventTextBgColor, eventBgColor);
            eventBgLineColor = a.getColor(R.styleable.ScheduleView_eventTextBgLineColor, eventBgLineColor);
        } finally {
            a.recycle();
        }
        allDayEventShowHeight = allDayEventHeight;
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
        pastTimeNowPaint = new Paint();
        pastTimeNowPaint.setAntiAlias(true);
        pastTimeNowPaint.setColor(pastTimeNowTextColor);
        pastTimeNowPaint.setTextSize(pastTimeNowTextSize);

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
        addTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        addTextPaint.setColor(addTextColor);
        addTextPaint.setTextSize(addTextSize);
        addTextPaint.setTextAlign(Paint.Align.CENTER);
        Rect rectAddText = new Rect();
        addTextPaint.getTextBounds("+", 0, 1, rectAddText);
        addTextWidth = rectAddText.width();

        /**
         * 初始化全天
         */
        allDayEventTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        allDayEventTextPaint.setTextSize(allDayEventTextSize);
        allDayEventTextPaint.setColor(allDayEventTextColor);
        allDayEventBgTextPaint = new Paint();
        allDayEventBgTextPaint.setColor(allDayEventBgTextColor);
        allDayEventCountPaint = new TextPaint();
        allDayEventCountPaint.setAntiAlias(true);
        allDayEventCountPaint.setTextAlign(Paint.Align.CENTER);
        allDayEventCountPaint.setTextSize(allDayEventCountTextSize);
        allDayEventCountPaint.setColor(allDayEventCountTextColor);
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
        Log.d(TAG, "==  " + getHeight() + "      " + allDayEventShowHeight);
        /**
         * 说明当前时间比较靠后，需要移动到最底部
         */
        if (((24 - hour) * (rowHeight + lineSize)) < (getHeight() - allDayEventShowHeight)) {
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
                float top = allDayEventShowHeight;
                float right = hourTextWidth + dayNumber * (columnWidth + lineSize);
                float bottom = getHeight();
                canvas.drawRect(left, top, right, bottom, pastTimePaint);
            }

            /**
             * 画今天整小时
             */

            float leftToday = hourTextWidth + dayNumber * columnWidth + dayNumber * lineSize;
            float rightToday = leftToday + columnWidth + lineSize;
            float bottomTodayHour = hourNumber * (rowHeight + lineSize) + originOffset.y + allDayEventShowHeight;
            /**
             * 只画显示的时候
             */
            if (bottomTodayHour > allDayEventShowHeight) {
                float topTodayHour = allDayEventShowHeight;
                canvas.drawRect(leftToday, topTodayHour, rightToday, bottomTodayHour, pastTimePaint);
            }

            /**
             * 画今天不满一小时的部分
             */
            float topTodayMinute = hourNumber * (rowHeight + lineSize) + originOffset.y + allDayEventShowHeight;
            float bottomTodayMinute = topTodayMinute + rowHeight / 60 * minuteNumber;
            if (bottomTodayMinute > allDayEventShowHeight) {
                if (topTodayMinute < allDayEventShowHeight) {
                    topTodayMinute = allDayEventShowHeight;
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
            float y = i * rowHeight + (i + 1) * lineSize + originOffset.y + allDayEventShowHeight;
            if (i == 0) {
                y += hourTextSize;
            } else {
                y += (hourTextSize / 3);
            }
            /**
             * 同理，只画屏幕内显示的
             */
            if (y <= getHeight() + hourTextSize && y >= allDayEventShowHeight - hourTextSize) {
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
            float startY = allDayEventShowHeight;
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
            float startY = allDayEventShowHeight + i * lineSize + i * rowHeight + originOffset.y;
            if (startY >= allDayEventShowHeight && startY <= getHeight()) {
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
                if (eventRectFLineKuang.top > getHeight() && eventRectFLineKuang.bottom < allDayEventShowHeight) {
                    //没有显示进来就不画了
                } else {
                    eventBgPaint.setColor(eventRect.event.getSideLineColor() == 0 ? eventBgLineColor : eventRect.event.getSideLineColor());
                    canvas.drawRect(eventRectFLineKuang, eventBgPaint);
                    eventBgPaint.setColor(eventRect.event.getColor() == 0 ? eventBgColor : eventRect.event.getColor());
                    canvas.drawRect(eventRectF, eventBgPaint);
                    eventBgPaint.setColor(eventRect.event.getHeadLineColor() == 0 ? eventBgLineColor : eventRect.event.getHeadLineColor());
                    canvas.drawRect(eventRectFHeadLine, eventBgPaint);
                }
            }
        }

    }

    /**
     * 传入events
     *
     * @param scheduleViewEventList
     */
    public void setEvents(List<ScheduleViewEvent> scheduleViewEventList) {
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
            if (endTime == null || event.getStartTime().compareTo(endTime) > 0) {
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
            eventRect.rectF.top = event.getStartTime().get(Calendar.HOUR_OF_DAY) * (rowHeight + lineSize) + rowHeight / 60 * event.getStartTime().get(Calendar.MINUTE) + allDayEventShowHeight;
            eventRect.rectF.bottom = event.getEndTime().get(Calendar.HOUR_OF_DAY) * (rowHeight + lineSize) + rowHeight / 60 * event.getEndTime().get(Calendar.MINUTE) + allDayEventShowHeight;
            eventRectList.add(eventRect);

        }
        return eventRectList;
    }

    /**
     * 将事件转换成图形上的坐标对象
     */
    private class EventRect {

        public ScheduleViewEvent event;

        public RectF rectF = new RectF();

        public EventRect() {
        }

        public EventRect(ScheduleViewEvent event, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
        }
    }

}
