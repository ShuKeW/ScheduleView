package com.skw.scheduleview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.skw.scheduleview.R;

import java.util.Calendar;

/**
 * @创建人 weishukai
 * @创建时间 18/2/1 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class ScheduleView extends View {
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

    /**
     * 事件相关
     */
    private Paint eventTextPaint;
    private Paint eventBgPaint;
    private float eventTextSize;
    private int eventTextColor;
    private int eventBgColor;
    private int eventBgLineColor;

    private Calendar firstDay;
    private Calendar endDay;


    public ScheduleView(Context context) {
        super(context);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
    }

    /**
     * 初始化
     */
    private void init() {
        /**
         * 初始化开始时间和结束时间，一般是一周
         */
        firstDay = Calendar.getInstance();
        int dayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek = -6;
        } else {
            dayOfWeek = -(dayOfWeek - 2);
        }
        firstDay.add(Calendar.DAY_OF_WEEK, dayOfWeek);
        firstDay.set(Calendar.HOUR_OF_DAY, 0);
        firstDay.set(Calendar.MINUTE, 0);
        firstDay.set(Calendar.SECOND, 0);
        endDay = (Calendar) firstDay.clone();
        endDay.add(Calendar.DAY_OF_WEEK, columnNumber - 1);
        endDay.set(Calendar.HOUR_OF_DAY, 23);
        endDay.set(Calendar.MINUTE, 59);
        endDay.set(Calendar.SECOND, 59);

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
}
