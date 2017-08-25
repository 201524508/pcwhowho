package com.example.yuuuuu.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;

public class CalendarView extends CTCalendarView {

    private String[][] sportsDate;
    private SportDate sd = new SportDate();

    private GridAdapter_sports gridAdapter_sports;
    private Oneday basisDay;
    private int during;
    private int mHour;
    private int mMin;

    String mYear, mMonth, mDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        setTitle("어디 타이틀");
        initialize();
         
        basisDay = new Oneday(this);
        
        Intent intent = getIntent();
        int[] b = intent.getIntArrayExtra("basisDay");
        during = intent.getIntExtra("during", 0);
        if(b != null){
            basisDay.setYear(b[0]);
            basisDay.setMonth(b[1]);
            basisDay.setDay(b[2]);
        } else {
            Calendar cal = Calendar.getInstance();
            basisDay.setYear(cal.get(Calendar.YEAR));
            basisDay.setMonth(cal.get(Calendar.MONTH));
            basisDay.setDay(cal.get(Calendar.DAY_OF_MONTH));
    }
        sportsDate = sd.getDate();
    }

    @Override
    protected void onTouched(Oneday touchedDay){

        final String year = String.valueOf(touchedDay.getYear());
        final String month = doubleString(touchedDay.getMonth() + 1);
        final String date = doubleString(touchedDay.getDay());
        final String _date = oneString(touchedDay.getDay());

        mYear = year;
        mMonth = month;
        mDate = date;

        //System.out.println("&&&&&&&&" + mYear + " " + mMonth + " "+ mDate);

        Intent intent = new Intent(CalendarView.this, AlarmReceiver.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("month",month);
        intent.putExtra("date", date);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CalendarView.this);
        builderSingle.setTitle("경기 일정 " + year + "." + month + "." + date + "(" + touchedDay.getDayOfWeekKorean()+")");

        int t1 = sd.isInDate(_date);

        builderSingle.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if(year.equals("2018") && month.equals("02") && sd.isInDate(_date) != -1){
            //System.out.println("true");
            gridAdapter_sports = new GridAdapter_sports(CalendarView.this, sportsDate[t1], true);
            gridAdapter_sports.getYear(year);
            gridAdapter_sports.getMonth(month);
            gridAdapter_sports.getDate(date);

            onResume(touchedDay);
        }
        else{
            System.out.println("false");
        }

        builderSingle.setAdapter(gridAdapter_sports, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builderSingle.show();
    }
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.calendar_menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         
        switch (item.getItemId()) {

        case R.id.menuitem_calendar_0:
            gotoToday();
            return true;
        }
         
        return false;
    }

    public void onResume(Oneday touchedDay) {

        final String date = doubleString(touchedDay.getDay());
        final String _date = oneString(touchedDay.getDay());

        DBalarm alarm;
        alarm = new DBalarm(CalendarView.this, "Alarm.db", null, 1);
        SQLiteDatabase dbAlarm;
        dbAlarm = alarm.getWritableDatabase();
        alarm.onCreate(dbAlarm);
        Cursor c = dbAlarm.query("Alarm", null, null, null, null, null, null);

        c.moveToFirst();
        if(c.getCount() != 0) {
            String day = c.getString(c.getColumnIndex("day"));
            String sport = c.getString(c.getColumnIndex("sport"));
            //System.out.println("**Db : day sport " + day + " " + sport);

            int t1 = sd.isInDate(_date);

            if(day.equals(date)) {
                //System.out.println(day + " " + date);
                for(int i=2; i<sportsDate[t1].length; i++) {
                    if(sport.equals(sportsDate[t1][i])) {
                        //System.out.println(sport + " " + sportsDate[t1][i]);
                        gridAdapter_sports.checkCheckBox(i-2, true);
                    }
                }
            }

            while(c.moveToNext()) {
                day = c.getString(c.getColumnIndex("day"));
                sport = c.getString(c.getColumnIndex("sport"));

                if(day.equals(date)) {
                    //System.out.println(day + " " + date);
                    for(int i=2; i<sportsDate[t1].length; i++) {
                        if(sport.equals(sportsDate[t1][i])) {
                            //System.out.println(sport + " " + sportsDate[t1][i]);
                            gridAdapter_sports.checkCheckBox(i-2, true);
                        }
                    }
                }
            }

        }

    }
}