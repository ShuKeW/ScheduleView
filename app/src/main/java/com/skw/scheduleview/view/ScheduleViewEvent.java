package com.skw.scheduleview.view;

import java.util.Calendar;

/**
 * @创建人 weishukai
 * @创建时间 18/2/1 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class ScheduleViewEvent {
    /**
     * 日程的类型
     */
    private int scheduleType;
    private long id;
    /**
     * 开始时间
     */
    private Calendar startTime;
    /**
     * 结束时间
     */
    private Calendar endTime;
    /**
     * 开始时间转换成图标上的开始值
     */
    private int startIndex;
    private int endIndex;
    /**
     * 是否是全天事件
     */
    private boolean isAllDayEvent;
    /**
     * 日程的内容
     */
    private String content;
    /**
     * 块的颜色
     */
    private int color;
    /**
     * 边框的颜色
     */
    private int sideLineColor;
    /**
     * 顶部线条的颜色
     */
    private int headLineColor;
    /**
     * 文字的颜色
     */
    private int textColor;

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public boolean isAllDayEvent() {
        return isAllDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        isAllDayEvent = allDayEvent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSideLineColor() {
        return sideLineColor;
    }

    public void setSideLineColor(int sideLineColor) {
        this.sideLineColor = sideLineColor;
    }

    public int getHeadLineColor() {
        return headLineColor;
    }

    public void setHeadLineColor(int headLineColor) {
        this.headLineColor = headLineColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
