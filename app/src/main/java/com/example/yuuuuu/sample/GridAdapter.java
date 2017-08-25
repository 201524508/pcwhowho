package com.example.yuuuuu.sample;

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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by YUNA on 2017-06-26.
 *            YUUUUU on 2017-08-13.
 *            s0woo on 2017-08-16.
 */

public class GridAdapter extends BaseAdapter {
    private Context context;
    private String[][] arrayList;
    private LayoutInflater inflater;
    private boolean isListView;
    private SparseBooleanArray mSelectedItemsIds;
    private boolean[] cntCheck;

    public GridAdapter(Context context, String[][] arrayList, boolean isListView) {
        this.context = context;
        this.arrayList = arrayList;
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        mSelectedItemsIds = new SparseBooleanArray();
        cntCheck = new boolean[arrayList.length];
    }

    public void setArrayList(String[][]tmp){
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
                view = inflater.inflate(R.layout.player_list_custom_row, viewGroup, false);
            else
                view = inflater.inflate(R.layout.player_list_custom_row, viewGroup, false);

            viewHolder.player = (TextView) view.findViewById(R.id.player);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.data = (TextView) view.findViewById(R.id.data);
            viewHolder.country = (ImageView) view.findViewById(R.id.country);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.player.setText(arrayList[i][0]);
        viewHolder.data.setText(arrayList[i][1]);
        viewHolder.checkBox.setChecked(mSelectedItemsIds.get(i));

        if(arrayList[i][0]!=null){
            //arrayList[i][2]번째의 값에 따라 나라 국기 다르게 표현
            if(arrayList[i][2].equalsIgnoreCase("Albania")) {
                viewHolder.country.setImageResource(R.drawable.flag_albania);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Andorra")){
                viewHolder.country.setImageResource(R.drawable.flag_andorra);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Argentina")){
                viewHolder.country.setImageResource(R.drawable.flag_argen);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Armenia")){
                viewHolder.country.setImageResource(R.drawable.flag_armenia);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Australia")){
                viewHolder.country.setImageResource(R.drawable.flag_australia);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Azerbaycan")){
                viewHolder.country.setImageResource(R.drawable.flag_azer);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Bermuda")){
                viewHolder.country.setImageResource(R.drawable.flag_bermuda);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Estonia")){
                viewHolder.country.setImageResource(R.drawable.flag_estonia);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Finland")){
                viewHolder.country.setImageResource(R.drawable.flag_finland);
            }
            else if(arrayList[i][2].equalsIgnoreCase("France")){
                viewHolder.country.setImageResource(R.drawable.flag_france);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Germany")){
                viewHolder.country.setImageResource(R.drawable.flag_germany);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Greece")){
                viewHolder.country.setImageResource(R.drawable.flag_greece);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Israel")){
                viewHolder.country.setImageResource(R.drawable.flag_israel);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Armenia")){
                viewHolder.country.setImageResource(R.drawable.flag_armenia);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Japan")){
                viewHolder.country.setImageResource(R.drawable.flag_japan);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Kenya")){
                viewHolder.country.setImageResource(R.drawable.flag_kenya);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Korea")){
                viewHolder.country.setImageResource(R.drawable.flag_korea);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Luxembourg")){
                viewHolder.country.setImageResource(R.drawable.flag_luxembourg);
            }
            else if(arrayList[i][2].equalsIgnoreCase("Malaysia")){
                viewHolder.country.setImageResource(R.drawable.flag_malaysia);
            }
            else if(arrayList[i][2].equalsIgnoreCase("USA")){
                viewHolder.country.setImageResource(R.drawable.flag_usa);
            }
            else if(arrayList[i][2].equalsIgnoreCase("No")){
                viewHolder.country.setImageResource(R.drawable.btn_no_flag);
                System.out.println("에러란다!!!!!!!!!!!");
            }
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
                //System.out.println("checkNum : " + i + " - " + mSelectedItemsIds.get(i));
                cntCheck[i] = mSelectedItemsIds.get(i);
            }
        });

        viewHolder.player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkCheckBox(i, !mSelectedItemsIds.get(i));
                detailView(i, !mSelectedItemsIds.get(i));
                //System.out.println("checkNum : " + i + " - " + mSelectedItemsIds.get(i));
                cntCheck[i] = mSelectedItemsIds.get(i);
            }
        });

        viewHolder.data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkCheckBox(i, !mSelectedItemsIds.get(i));
                detailView(i, !mSelectedItemsIds.get(i));
                // System.out.println("checkNum : " + i + " - " + mSelectedItemsIds.get(i));
                cntCheck[i] = mSelectedItemsIds.get(i);
            }
        });

        viewHolder.country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkCheckBox(i, !mSelectedItemsIds.get(i));
                detailView(i, !mSelectedItemsIds.get(i));
                //System.out.println("checkNum : " + i + " - " + mSelectedItemsIds.get(i));
                cntCheck[i] = mSelectedItemsIds.get(i);
            }
        });

        return view;
    }


    private class ViewHolder {
        private TextView player;
        private CheckBox checkBox;
        private TextView data;
        private ImageView country;
    }

    /**
     * Check the Checkbox if not checked
     * */

    public void checkCheckBox (int position, boolean value) {

        DBbookmark bookmark;
        bookmark = new DBbookmark(this.context, "Bookmark.db", null, 1);
        SQLiteDatabase dbBM;
        dbBM = bookmark.getWritableDatabase();
        bookmark.onCreate(dbBM);
        Cursor c = dbBM.query("Bookmark", null, null, null, null, null, null);

        String overlap;

        //checkBox에 체크가 된다면
        if (value){
            //System.out.println(position + "에 체크가 되었단다");
            mSelectedItemsIds.put(position, true);
            //System.out.println("array ::::::" + arrayList[position][0]);

            c.moveToFirst();

            //bookmark DB에 어떠한 내용이라도 존재한다면 그 내용과 checkBox 눌려진 내용과 같은 내용이 DB이 존재하는지 중복체크
            if(c.getCount() != 0) {
                String dbName = c.getString(c.getColumnIndex("name"));
                String dbSport = c.getString(c.getColumnIndex("sport"));
                String dbNation = c.getString(c.getColumnIndex("nation"));
                //System.out.println("position + " + position);
                //System.out.println("db내용물 ### : " + dbName + " " + dbSport + " " + dbNation);
                //System.out.println(arrayList[position][0] + " " + arrayList[position][1] + " " + arrayList[position][2]);

                if(dbName.equals(arrayList[position][0]) && dbSport.equals(arrayList[position][1]) && dbNation.equals(arrayList[position][2])) {
                    //System.out.println("첫번째 중복있음");
                    overlap = "yes";
                }
                else {
                    overlap = "no";
                }

                if(overlap.equals("no")) {
                    while(c.moveToNext()) {
                        dbName = c.getString(c.getColumnIndex("name"));
                        dbSport = c.getString(c.getColumnIndex("sport"));
                        dbNation = c.getString(c.getColumnIndex("nation"));

                        //System.out.println("db내용물 ### : " + dbName + " " + dbSport + " " + dbNation);
                        //System.out.println(arrayList[position][0] + " " + arrayList[position][1] + " " + arrayList[position][2]);

                        if (dbName.equals(arrayList[position][0]) && dbSport.equals(arrayList[position][1]) && dbNation.equals(arrayList[position][2])) {
                            //System.out.println("중복있음");
                            overlap = "yes";
                            break;
                        } else {
                            overlap = "no";
                        }
                    }
                }

                if(overlap.equals("no")) {
                    if(!arrayList[position][0].equals(null)) {
                        //System.out.println("overlap : " + overlap);
                        bookmark.insert(arrayList[position][0], arrayList[position][1], arrayList[position][2]);
                    }
                }
            }
            else {
                //System.out.println("db 내용물 없어서 내용 입력");
                if(!arrayList[position][0].equals(null)) {
                    bookmark.insert(arrayList[position][0], arrayList[position][1], arrayList[position][2]);
                }
            }

            String result = bookmark.getResult();
            System.out.println("*******db \n" + result);

        }
        //checkBox에 체크가 해제된다면 DB에서 내용삭제
        else{
            //System.out.println(position + "에 체크는 개뿔");
            mSelectedItemsIds.delete(position);
            bookmark.delete(arrayList[position][0], arrayList[position][1], arrayList[position][2]);
            String result = bookmark.getResult();
            System.out.println("*******db \n" + result);
        }
        notifyDataSetChanged();
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

    public void detailView(int position, boolean value) {
        Intent intent = new Intent(this.context, PlayerDetailActivity.class);
        intent.putExtra("name", arrayList[position][0]);
        intent.putExtra("sport", arrayList[position][1]);
        intent.putExtra("nation", arrayList[position][2]);
        context.startActivity(intent);
    }
}