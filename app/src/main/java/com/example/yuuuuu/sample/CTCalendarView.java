package com.example.yuuuuu.sample;

import android.app.Activity;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

//http://funpython.com/blog/59?category=2
public class CTCalendarView extends Activity implements OnClickListener {

    private SportDate sd;
    private String[][] sportsDate;
    private Calendar rightNow;
    private GregorianCalendar gCal;
    private int iYear = 0;
    private int iMonth = 0;

    private int startDayOfweek = 0;
    private int maxDay = 0;
    private int oneday_width =0;
    private int oneday_height =0;

    ArrayList<String> daylist;
    ArrayList<String> actlist;

    TextView aDateTxt;

    private int dayCnt;
    private int mSelect = -1;

    protected void initialize(){
        setContentView(R.layout.calendarview);

        rightNow = Calendar.getInstance();
        gCal = new GregorianCalendar();
        iYear = rightNow.get(Calendar.YEAR);
        iMonth = rightNow.get(Calendar.MONTH);

        sd = new SportDate();
        sportsDate = sd.getDate();

        ImageButton btnMPrev = (ImageButton)findViewById(R.id.btn_calendar_prevmonth);
        btnMPrev.setOnClickListener(this);
        ImageButton btnMNext = (ImageButton)findViewById(R.id.btn_calendar_nextmonth);
        btnMNext.setOnClickListener(this);

        aDateTxt = (TextView)findViewById(R.id.CalendarMonthTxt);

        makeCalendardata(iYear, iMonth);
    }

    private void printDate(String thisYear, String thisMonth)
    {

        if(thisMonth.length() == 1) {
            aDateTxt.setText(String.valueOf(thisYear) + "." + "0"+ thisMonth);
        }
        else{
            aDateTxt.setText(String.valueOf(thisYear) + "." + thisMonth);
        }
    }

    private void makeCalendardata(int thisYear, int thisMonth)
    {
        printDate(String.valueOf(thisYear),String.valueOf(thisMonth+1));

        rightNow.set(thisYear, thisMonth, 1);
        gCal.set(thisYear, thisMonth, 1);
        startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);

        maxDay = gCal.getActualMaximum ((Calendar.DAY_OF_MONTH));
        if(daylist==null)daylist = new ArrayList<String>();
        daylist.clear();

        if(actlist==null)actlist = new ArrayList<String>();
        actlist.clear();

        daylist.add("일");actlist.add("");
        daylist.add("월");actlist.add("");
        daylist.add("화");actlist.add("");
        daylist.add("수");actlist.add("");
        daylist.add("목");actlist.add("");
        daylist.add("금");actlist.add("");
        daylist.add("토");actlist.add("");

        if(startDayOfweek != 1) {
            gCal.set(thisYear, thisMonth-1, 1);
            int prevMonthMaximumDay = (gCal.getActualMaximum((Calendar.DAY_OF_MONTH))+2);
            for(int i=startDayOfweek;i>1;i--){
                daylist.add(Integer.toString(prevMonthMaximumDay-i));
                actlist.add("p");
            }
        }

        for(int i=1;i<=maxDay;i++)
        {
            daylist.add(Integer.toString(i));
            actlist.add("");
        }


        int dayDummy = (startDayOfweek-1)+maxDay;
        if(dayDummy >35)
        {
            dayDummy = 42 - dayDummy;
        }else{
            dayDummy = 35 - dayDummy;
        }

        if(dayDummy != 0)
        {
            for(int i=1;i<=dayDummy;i++)
            {
                daylist.add(Integer.toString(i));
                actlist.add("n");
            }
        }

        makeCalendar();
    }

    private void makeCalendar()
    {
        final Oneday[] oneday = new Oneday[daylist.size()];
        final Calendar today = Calendar.getInstance();
        TableLayout tl =(TableLayout)findViewById(R.id.tl_calendar_monthly);
        tl.removeAllViews();

        dayCnt = 0;
        int maxRow = ((daylist.size() > 42)? 7:6);
        int maxColumn = 7;


        oneday_width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        oneday_height = getWindow().getWindowManager().getDefaultDisplay().getHeight();

        oneday_height = ((((oneday_height >= oneday_width)?oneday_height:oneday_width) - tl.getTop()) / (maxRow+1))-10;
        oneday_width = (oneday_width / maxColumn)+1;


        int daylistsize =daylist.size()-1;
        for(int i=1;i<=maxRow;i++ )
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            for(int j=1;j<=maxColumn;j++)
            {

                oneday[dayCnt] = new Oneday(getApplicationContext());
                if((dayCnt % 7) == 0){
                    oneday[dayCnt].setTextDayColor(Color.RED);
                } else if((dayCnt % 7) == 6){
                    oneday[dayCnt].setTextDayColor(Color.BLUE);
                } else {
                    oneday[dayCnt].setTextDayColor(Color.BLACK);
                }
                if(dayCnt >= 0 && dayCnt < 7)
                {
                    //요일 나타나는 부분
                    oneday[dayCnt].setBgDayPaint(Color.DKGRAY);
                    oneday[dayCnt].setTextDayTopPadding(10);
                    oneday[dayCnt].setTextDayColor(Color.WHITE);
                    oneday[dayCnt].setTextDaySize(40);
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,70));
                    oneday[dayCnt].isToday = false;

                }else{
                    oneday[dayCnt].isToday = false;
                    oneday[dayCnt].setDayOfWeek(dayCnt%7 + 1);
                    oneday[dayCnt].setDay(Integer.valueOf(daylist.get(dayCnt)).intValue());
                    oneday[dayCnt].setTextActcntSize(14);
                    oneday[dayCnt].setTextActcntColor(Color.GRAY);
                    oneday[dayCnt].setTextActcntTopPadding(12);
                    oneday[dayCnt].setBgSelectedDayPaint(Color.rgb(0, 162, 232));
                    oneday[dayCnt].setBgTodayPaint(Color.LTGRAY);
                    oneday[dayCnt].setBgActcntPaint(Color.rgb(251, 247, 176));
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,oneday_height));


                    if(actlist.get(dayCnt).equals("p")){
                        oneday[dayCnt].setTextDaySize(36);
                        actlist.set(dayCnt, "");
                        oneday[dayCnt].setTextDayTopPadding(-4);

                        if(iMonth - 1 < Calendar.JANUARY){
                            oneday[dayCnt].setMonth(Calendar.DECEMBER);
                            oneday[dayCnt].setYear(iYear - 1);
                        }  else {
                            oneday[dayCnt].setMonth(iMonth - 1);
                            oneday[dayCnt].setYear(iYear);
                        }


                    } else if(actlist.get(dayCnt).equals("n")){
                        oneday[dayCnt].setTextDaySize(36);
                        actlist.set(dayCnt, "");
                        oneday[dayCnt].setTextDayTopPadding(-4);
                        if(iMonth + 1 > Calendar.DECEMBER){
                            oneday[dayCnt].setMonth(Calendar.JANUARY);
                            oneday[dayCnt].setYear(iYear + 1);
                        }  else {
                            oneday[dayCnt].setMonth(iMonth + 1);
                            oneday[dayCnt].setYear(iYear);
                        }

                    }else{
                        oneday[dayCnt].setTextDaySize(48);
                        oneday[dayCnt].setYear(iYear);
                        oneday[dayCnt].setMonth(iMonth);

                        if (oneday[dayCnt].getYear() == 2018 && oneday[dayCnt].getMonth() == 1) {
                            String t1 = String.valueOf(oneday[dayCnt].getDay());
                            if (sd.isInDate(t1) != -1) {
                                int t2 = Integer.parseInt(sportsDate[sd.isInDate(t1)][1]);
                                String t3 = "";
                                for (int k = 0; k < t2; k++) {
                                    t3 = t3 + "*";
                                }
                                actlist.set(dayCnt, t3);

                                oneday[dayCnt].invalidate();
                            }
                            //System.out.println("2018년 2월 달력입니다");
                        }

                        if(oneday[dayCnt].getDay() == today.get(Calendar.DAY_OF_MONTH)
                                && oneday[dayCnt].getMonth() == today.get(Calendar.MONTH)
                                && oneday[dayCnt].getYear() == today.get(Calendar.YEAR)){

                            oneday[dayCnt].isToday = true;
                            actlist.set(dayCnt,"오늘");
                            oneday[dayCnt].invalidate();
                            mSelect = dayCnt;
                        }
                    }


                    oneday[dayCnt].setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, iYear+"-"+iMonth+"-"+oneday[v.getId()].getTextDay(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });

                    oneday[dayCnt].setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if(oneday[v.getId()].getTextDay() != "" && event.getAction() == MotionEvent.ACTION_UP)
                            {
                                if(mSelect != -1){
                                    oneday[mSelect].setSelected(false);
                                    oneday[mSelect].invalidate();
                                }
                                oneday[v.getId()].setSelected(true);
                                oneday[v.getId()].invalidate();
                                mSelect = v.getId();

                                onTouched(oneday[mSelect]);
                            }
                            return false;
                        }
                    });
                }

                oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString());
                oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());
                oneday[dayCnt].setId(dayCnt);
                oneday[dayCnt].invalidate();
                tr.addView(oneday[dayCnt]);

                if(daylistsize != dayCnt)
                {
                    dayCnt++;
                }else{
                    break;
                }
            }
            tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

        }
    }

    /**
     *      * @param value
     * @return
     */
    protected String doubleString(int value)
    {
        String temp;

        if(value < 10){
            temp = "0"+ String.valueOf(value);

        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }

    protected String oneString(int value)
    {
        String temp;

        temp = String.valueOf(value);
        return temp;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btn_calendar_nextmonth:
                if(iMonth == 11)
                {
                    iYear = iYear + 1;
                    iMonth = 0;
                }
                else
                {
                    iMonth = iMonth + 1;
                }
                makeCalendardata(iYear,iMonth);
                break;
            case R.id.btn_calendar_prevmonth:
                if(iMonth == 0)
                {
                    iYear = iYear - 1;
                    iMonth = 11;
                }else{
                    iMonth = iMonth - 1;
                }
                makeCalendardata(iYear,iMonth);
                break;
        }
    }

    /**
     * @param oneday
     */
    protected void onTouched(Oneday oneday){

    }

    /**
     *
     * @param test
     * @param basis
     * @param during
     * @return
     */
    protected boolean isInside(Oneday test, Oneday basis, int during){
        Calendar calbasis = Calendar.getInstance();
        calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
        calbasis.add(Calendar.DAY_OF_MONTH, during);

        Calendar caltest = Calendar.getInstance();
        caltest.set(test.getYear(), test.getMonth(), test.getDay());

        if(caltest.getTimeInMillis() < calbasis.getTimeInMillis()){
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void gotoToday(){
        final Calendar today = Calendar.getInstance();
        iYear = today.get(Calendar.YEAR);
        iMonth = today.get(Calendar.MONTH);
        makeCalendardata(today.get(Calendar.YEAR),today.get(Calendar.MONTH));
    }
}