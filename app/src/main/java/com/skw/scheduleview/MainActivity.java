package com.skw.scheduleview;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.skw.scheduleview.view.ScheduleView;
import com.skw.scheduleview.view.ScheduleViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        scheduleView.setOnEventAddClickListener(new ScheduleView.OnEventAddClickListener() {
            @Override
            public void onEventAddClicked(Calendar time) {
                Toast.makeText(getApplicationContext(), time.getTime().toString(), Toast.LENGTH_LONG).show();
            }
        });
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
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAllDayEvents();
            }
        });

    }

    private void createAllDayEvents() {
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

        for (int i = 0; i < 4; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(true);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            event.setStartTime(startTime);
            endTime.add(Calendar.DAY_OF_WEEK, (i + 1));
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


        for (int i = 4; i < 7; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(true);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            startTime.add(Calendar.DAY_OF_WEEK, 1);
            endTime.add(Calendar.DAY_OF_WEEK, i - 2);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            switch (i) {
                case 4:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_HOLIDY);
                    event.setColor(res.getColor(R.color.sch_holiday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_holiday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_holiday_headline));
                    event.setTextColor(res.getColor(R.color.sch_holiday_text));
                    break;
                case 5:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
                case 6:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
                    event.setColor(res.getColor(R.color.sch_contact_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
                    event.setTextColor(res.getColor(R.color.sch_contact_text));
                    break;
                case 3:
            }
            scheduleViewEventList.add(event);
        }

        for (int i = 7; i < 10; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(true);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            startTime.add(Calendar.DAY_OF_WEEK, 3);
            endTime.add(Calendar.DAY_OF_WEEK, i - 4);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            switch (i) {
                case 7:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CREATE);
                    event.setColor(res.getColor(R.color.sch_create_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_create_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_create_headline));
                    event.setTextColor(res.getColor(R.color.sch_create_text));
                    break;
                case 8:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
                case 9:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
                    event.setColor(res.getColor(R.color.sch_contact_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
                    event.setTextColor(res.getColor(R.color.sch_contact_text));
                    break;
            }
            scheduleViewEventList.add(event);
        }

        for (int i = 10; i < 12; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(true);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            startTime.add(Calendar.DAY_OF_WEEK, 5);
            endTime.add(Calendar.DAY_OF_WEEK, i - 5);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            switch (i) {
                case 10:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_CONTACT);
                    event.setColor(res.getColor(R.color.sch_contact_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_contact_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_contact_headline));
                    event.setTextColor(res.getColor(R.color.sch_contact_text));
                    break;
                case 11:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
            }
            scheduleViewEventList.add(event);
        }

        for (int i = 12; i < 20; i++) {
            event = new ScheduleViewEvent();
            event.setId(i);
            event.setContent("日程" + i);
            event.setAllDayEvent(true);
            startTime = (Calendar) firstDay.clone();
            endTime = (Calendar) firstDay.clone();
            endTime.add(Calendar.DAY_OF_WEEK, 6);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            switch (i) {
                default:
                    event.setScheduleType(ScheduleViewEvent.SCHEDULE_TYPE_BIRTHDAY);
                    event.setColor(res.getColor(R.color.sch_birthday_bg));
                    event.setSideLineColor(res.getColor(R.color.sch_birthday_sideline));
                    event.setHeadLineColor(res.getColor(R.color.sch_birthday_headline));
                    event.setTextColor(res.getColor(R.color.sch_birthday_text));
                    break;
            }
            scheduleViewEventList.add(event);
        }


        scheduleView.setAllDayEvents(scheduleViewEventList);
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
