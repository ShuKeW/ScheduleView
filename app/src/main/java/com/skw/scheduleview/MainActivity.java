package com.skw.scheduleview;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.skw.scheduleview.view.ScheduleView;
import com.skw.scheduleview.view.ScheduleViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7;
    private String[] weeks = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
    private ScheduleView scheduleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleView = (ScheduleView) findViewById(R.id.scheduleView);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);
        textView4 = (TextView) findViewById(R.id.text4);
        textView5 = (TextView) findViewById(R.id.text5);
        textView6 = (TextView) findViewById(R.id.text6);
        textView7 = (TextView) findViewById(R.id.text7);

        textView1.setText(weeks[0]);
        textView2.setText(weeks[1]);
        textView3.setText(weeks[2]);
        textView4.setText(weeks[3]);
        textView5.setText(weeks[4]);
        textView6.setText(weeks[5]);
        textView7.setText(weeks[6]);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvents();
            }
        });

    }

    private void createEvents() {
        List<ScheduleViewEvent> scheduleViewEventList = new ArrayList<>();
        Calendar firstDay = Calendar.getInstance();
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
        firstDay.set(Calendar.MILLISECOND, 0);
        ScheduleViewEvent event = null;
        Resources res = getResources();

        Calendar startTime = null, endTime = null;

        event = new ScheduleViewEvent();
        event.setId(4);
        event.setContent("日程" + 4);
        event.setAllDayEvent(false);
        startTime = (Calendar) firstDay.clone();
        endTime = (Calendar) firstDay.clone();
        startTime.add(Calendar.HOUR_OF_DAY, 4);
        startTime.add(Calendar.MINUTE, 30);
        event.setStartTime(startTime);
        endTime.add(Calendar.HOUR_OF_DAY, 8);
        event.setEndTime(endTime);
        event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
        event.setColor(res.getColor(R.color.sch_birthday_bg));
        event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
        event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
        event.setTextColor(res.getColor(R.color.sch_birthday_text));
        scheduleViewEventList.add(event);

        for (int i = 0; i < 4; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(false);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            startTime.add(Calendar.HOUR_OF_DAY, (i + 1) * 1);
            event.setStartTime(startTime);
            endTime.add(Calendar.HOUR_OF_DAY, (i + 1) * 2);
            event.setEndTime(endTime);
            switch (i) {
                case 0:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CREATE);
                    event.setColor(res.getColor(R.color.sch_create_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_create_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_create_headline));
                    event.setTextColor(res.getColor(R.color.sch_create_text));
                    break;
                case 1:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
                case 2:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
                    event.setColor(res.getColor(R.color.sch_contact_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
                    event.setTextColor(res.getColor(R.color.sch_contact_text));
                    break;
                case 3:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_HOLIDY);
                    event.setColor(res.getColor(R.color.sch_holiday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_holiday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_holiday_headline));
                    event.setTextColor(res.getColor(R.color.sch_holiday_text));
                    break;
            }
            scheduleViewEventList.add(event);
        }

        event = new ScheduleViewEvent();
        event.setId(5);
        event.setContent("日程" + 5);
        event.setAllDayEvent(false);
        startTime = (Calendar) firstDay.clone();
        endTime = (Calendar) firstDay.clone();
        startTime.add(Calendar.DAY_OF_WEEK, 1);
        startTime.add(Calendar.HOUR_OF_DAY, 3);
        startTime.add(Calendar.MINUTE, 30);
        event.setStartTime(startTime);
        endTime.add(Calendar.DAY_OF_WEEK, 1);
        endTime.add(Calendar.HOUR_OF_DAY, 8);
        event.setEndTime(endTime);
        event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_HOLIDY);
        event.setColor(res.getColor(R.color.sch_holiday_bg));
        event.setSideLineColor(res.getColor(R.color.sch_holiday_sideline));
        event.setHeadLineColor(res.getColor(R.color.sch_holiday_headline));
        event.setTextColor(res.getColor(R.color.sch_holiday_text));
        scheduleViewEventList.add(event);

        event = new ScheduleViewEvent();
        event.setId(6);
        event.setContent("日程" + 6);
        event.setAllDayEvent(false);
        startTime = (Calendar) firstDay.clone();
        endTime = (Calendar) firstDay.clone();
        startTime.add(Calendar.DAY_OF_WEEK, 2);
        startTime.add(Calendar.HOUR_OF_DAY, 2);
        startTime.add(Calendar.MINUTE, 20);
        event.setStartTime(startTime);
        endTime.add(Calendar.DAY_OF_WEEK, 2);
        endTime.add(Calendar.HOUR_OF_DAY, 5);
        event.setEndTime(endTime);
        event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
        event.setColor(res.getColor(R.color.sch_contact_bg));
        event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
        event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
        event.setTextColor(res.getColor(R.color.sch_contact_text));
        scheduleViewEventList.add(event);

        event = new ScheduleViewEvent();
        event.setId(7);
        event.setContent("日程" + 7);
        event.setAllDayEvent(false);
        startTime = (Calendar) firstDay.clone();
        endTime = (Calendar) firstDay.clone();
        startTime.add(Calendar.DAY_OF_WEEK, 2);
        startTime.add(Calendar.HOUR_OF_DAY, 6);
        startTime.add(Calendar.MINUTE, 45);
        event.setStartTime(startTime);
        endTime.add(Calendar.DAY_OF_WEEK, 2);
        endTime.add(Calendar.HOUR_OF_DAY, 9);
        endTime.add(Calendar.MINUTE, 56);
        event.setEndTime(endTime);
        event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CREATE);
        event.setColor(res.getColor(R.color.sch_create_bg));
        event.setSideLineColor(res.getColor(R.color.sch_create_sideline));
        event.setHeadLineColor(res.getColor(R.color.sch_create_headline));
        event.setTextColor(res.getColor(R.color.sch_create_text));
        scheduleViewEventList.add(event);

        for (int i = 0; i < 4; i++) {
            event = new ScheduleViewEvent();
            event.setId(8 + i);
            event.setContent("日程" + (8 + i));
            event.setAllDayEvent(false);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            startTime.add(Calendar.DAY_OF_WEEK, 3);
            endTime.add(Calendar.DAY_OF_WEEK, 3);
            startTime.add(Calendar.HOUR_OF_DAY, 4);
            event.setStartTime(startTime);
            endTime.add(Calendar.HOUR_OF_DAY, 10);
            event.setEndTime(endTime);
            switch (i) {
                case 0:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CREATE);
                    event.setColor(res.getColor(R.color.sch_create_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_create_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_create_headline));
                    event.setTextColor(res.getColor(R.color.sch_create_text));
                    break;
                case 1:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
                case 2:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
                    event.setColor(res.getColor(R.color.sch_contact_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
                    event.setTextColor(res.getColor(R.color.sch_contact_text));
                    break;
                case 3:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_HOLIDY);
                    event.setColor(res.getColor(R.color.sch_holiday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_holiday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_holiday_headline));
                    event.setTextColor(res.getColor(R.color.sch_holiday_text));
                    break;
            }
            scheduleViewEventList.add(event);
        }


        scheduleView.setEvents(scheduleViewEventList);
    }
}
