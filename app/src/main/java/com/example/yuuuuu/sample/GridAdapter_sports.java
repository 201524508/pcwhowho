package com.example.yuuuuu.sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by YUNA on 2017-06-26.
 *         by YUUUUU on 2017-08-13.
 */

public class GridAdapter_sports extends BaseAdapter {
    private Context context;
    private String[] arrayList;
    private LayoutInflater inflater;
    private boolean isListView;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean[] cntCheck;

    private int mHour;
    private int mMin;
    private String tmpYear, tmpMonth, tmpDate;

    long getTime;

    public GridAdapter_sports(Context context, String[] arrayList, boolean isListView) {
        this.context = context;
        this.arrayList = new String[arrayList.length - 2];
        for(int i = 0; i <this.arrayList.length; i++){
            this.arrayList[i] = arrayList[i+2];
        }
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        mSelectedItemsIds = new SparseBooleanArray();
        cntCheck = new boolean[arrayList.length];
        for(int i = 0; i < arrayList.length; i++) {
            System.out.println(arrayList[i]);
        }
    }

    public GridAdapter_sports(){
    }

    public void setArrayList(String[]tmp){
        arrayList = tmp;
        cntCheck = new boolean[tmp.length];
    }

    @Override
    public int getCount() {
        return arrayList.length;
    }

    @Override
    public Object getItem(int i) {
        return arrayList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean
            if (isListView)
                view = inflater.inflate(R.layout.sports_list_custom_row, viewGroup, false);
            else
                view = inflater.inflate(R.layout.sports_list_custom_row, viewGroup, false);

            viewHolder.sports = (TextView) view.findViewById(R.id.sports);
            viewHolder.alarm = (CheckBox) view.findViewById(R.id.alarm);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        viewHolder.sports.setText(arrayList[i]);
        viewHolder.alarm.setChecked(mSelectedItemsIds.get(i));

        viewHolder.alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
                //System.out.println("checkNum : " + i + " - " + mSelectedItemsIds.get(i));
                cntCheck[i] = mSelectedItemsIds.get(i);
            }
        });

        return view;

    }

    private class ViewHolder {
        private TextView sports;
        private CheckBox alarm;
    }

    /**
     * Check the Checkbox if not checked
     * */

    public void getYear(String year){
        tmpYear = year;
    }

    public void getMonth(String month){
        tmpMonth = month;
    }

    public void getDate(String date){
        tmpDate = date;
    }

    public void checkCheckBox(final int position, boolean value) {
        //String overlap;
        DBalarm alarm;
        alarm = new DBalarm(context, "Alarm.db", null, 1);
        SQLiteDatabase dbAlarm;
        dbAlarm = alarm.getWritableDatabase();
        alarm.onCreate(dbAlarm);

        if (value) {
            //System.out.println(position + "에 체크가 되었단다");
            mSelectedItemsIds.put(position, true);

            //알람 설정
            TimePickerDialog.OnTimeSetListener mTimeSetListener =
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar calendar = new GregorianCalendar();

                            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            //System.out.println("oooooTime : " + hourOfDay + ", " + minute);

                            Intent intent = new Intent(context, AlarmReceiver.class);
                            intent.putExtra("hour", Integer.toString(hourOfDay));
                            intent.putExtra("min", Integer.toString(minute));
                            intent.putExtra("sport", arrayList[position]);
                            intent.putExtra("date", tmpDate);

                            PendingIntent sender;// = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                            sender = PendingIntent.getBroadcast(context, 0, intent, 0);

                            String year = tmpYear;
                            String month = tmpMonth;
                            String date = tmpDate;

                            mHour = hourOfDay;
                            mMin = minute;

                            int mYear = Integer.parseInt(year);
                            int mMonth = Integer.parseInt(month);
                            int mDate = Integer.parseInt(date);

                            calendar.setTimeInMillis(System.currentTimeMillis());

                            calendar.set(Calendar.YEAR, 2017);
                            calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                            calendar.set(Calendar.DATE, mDate);
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            //푸시 알림은 2월부터 설정이 가능하기 때문에
                            //기능 확인을 위해 편의상 현재 시간에 바로 푸시 알림이 뜨도록 설정함
                            am.set(AlarmManager.RTC, System.currentTimeMillis(), sender);
                        }
                    };

            Cursor c = dbAlarm.query("Alarm", null, null, null, null, null, null);
            String overlap;
            c.moveToFirst();

            //Alarm DB에 어떠한 내용이라도 존재한다면 그 내용과 checkBox 눌려진 내용과 같은 내용이 DB이 존재하는지 중복체크
            if(c.getCount() != 0) {
                String day = c.getString(c.getColumnIndex("day"));
                String sport = c.getString(c.getColumnIndex("sport"));
                System.out.println("position + " + position);
                System.out.println("db내용물 ### : " + tmpDate + " " + arrayList[position]);
                System.out.println(arrayList[position]);

                if(day.equals(tmpDate) && sport.equals(arrayList[position]) ) {
                    System.out.println("첫번째 중복있음");
                    overlap = "yes";
                }
                else {
                    overlap = "no";
                }

                if(overlap.equals("no")) {
                    while(c.moveToNext()) {
                        day = c.getString(c.getColumnIndex("day"));
                        sport = c.getString(c.getColumnIndex("sport"));

                        System.out.println("position + " + position);
                        System.out.println("db내용물 ### : " + tmpDate + " " + arrayList[position]);

                        if (day.equals(tmpDate) && sport.equals(arrayList[position])) {
                            overlap = "yes";
                            break;
                        } else {
                            overlap = "no";
                        }
                    }
                }

                if(overlap.equals("no")) {
                    if(!arrayList[position].equals(null)) {
                        System.out.println("overlap : " + overlap);
                        alarm.insert(tmpDate, arrayList[position]);
                        new TimePickerDialog(context, mTimeSetListener, mHour, mMin, false).show();
                    }
                }
            }
            else {
                new TimePickerDialog(context, mTimeSetListener, mHour, mMin, false).show();
                if(!arrayList[position].equals(null)) {
                    alarm.insert(tmpDate, arrayList[position]);
                }
            }

            notifyDataSetChanged();
            String result = alarm.getResult();
            System.out.println("*******db \n" + result);

        } else {    //value : false
            System.out.println(position + "에 체크는 개뿔");
            mSelectedItemsIds.delete(position);

            // 알람 remove
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            context.sendBroadcast(intent);
            PendingIntent sender;// = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
            sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.cancel(sender);

            String date = tmpDate;
            alarm.delete(date, arrayList[position]);
            String result = alarm.getResult();
            System.out.println("db************\n" + result);
            notifyDataSetChanged();
        }



    }

    /**
     * Return the selected Checkbox IDs
     * */
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public boolean[] getCntCheck(){
        return cntCheck;
    }

}