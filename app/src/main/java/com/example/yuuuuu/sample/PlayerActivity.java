package com.example.yuuuuu.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by YUNA 2017-08-14.
 *            s0woo 2017-08-16.
 */

public class PlayerActivity extends Activity {

    String[][] st;
    private Context context;
    private GridAdapter adapter;
    SparseBooleanArray tmp;

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    String country, sport;
    int cnt;

    public ProgressDialog progressDialog;

    DBbookmark bookmark = new DBbookmark(PlayerActivity.this, "Bookmark.db", null, 1);
    SQLiteDatabase dbBM;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_list);

        //이전 화면에서 미리 선택된 선수와 나라에 대한 정보 받아옴
        country = getIntent().getStringExtra("country");
        sport = getIntent().getStringExtra("sport");
        System.out.println("country : " + country + " sport : " + sport);

    }

    public void onStart() {
        super.onStart();

        final ListView listView = (ListView) findViewById(R.id.playerlistview);
        context = this;

        dbBM = bookmark.getWritableDatabase();
        bookmark.onCreate(dbBM);
        final Cursor c = dbBM.query("Bookmark", null, null, null, null, null, null);

        progressBarDialog();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run() {

                count(); //외부 DB인 cubrid와 연결하여 선택된 종목과 나라를 충족하는 선수의 수 받아오기

                Handler h1 = new Handler();
                h1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(cnt==0) { //해당 선수가 없다면 배열을 [1][3]으로 생성
                            st = new String[1][3];
                        } else {     //해당 선수가 있다면 선수의 수 만큼 배열 생성
                            st = new String[cnt][3];
                        }

                        cubrid(); //외부 DB인 cubrid와 연결하여 조건을 충족하는 선수 목록 받아오기

                        Handler h2 = new Handler();
                        h2.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(st[0][0] == null) { //해당 조건을 충족하는 선수가 없다면 Strin 배열 st에 다음과 같은 내용 입력
                                    st[0][0] = "해당 조건의 선수가 없습니다.";
                                    st[0][2] = "No";
                                }

                                adapter = new GridAdapter(context, st, true);
                                //System.out.println("check************");
                                listView.setAdapter(adapter);

                                //bookmark DB에 특정 선수의 정보가 등록되어있다면 checkBox true 표시
                                for(int i=0; i<cnt; i++) {
                                    if(c.getCount() == 0) {
                                        System.out.println("db내용 없음");
                                        break;
                                    }
                                    c.moveToFirst();

                                    String dbName = c.getString(c.getColumnIndex("name"));
                                    String dbSport = c.getString(c.getColumnIndex("sport"));
                                    String dbNation = c.getString(c.getColumnIndex("nation"));
                                    //System.out.println("**Db check : " + dbName + " " + dbSport + " " + dbNation);
                                    if(dbName.equals(st[i][0]) && dbSport.equals(st[i][1]) && dbNation.equals(st[i][2])) {
                                        //System.out.println("i" + i);
                                        adapter.checkCheckBox(i,true);
                                    }

                                    while(c.moveToNext()) {
                                        dbName = c.getString(c.getColumnIndex("name"));
                                        dbSport = c.getString(c.getColumnIndex("sport"));
                                        dbNation = c.getString(c.getColumnIndex("nation"));
                                        //System.out.println("**Db check : " + dbName + " " + dbSport + dbNation);
                                        if(dbName.equals(st[i][0]) && dbSport.equals(st[i][1]) && dbNation.equals(st[i][2])) {
                                            //System.out.println("i" + i);
                                            adapter.checkCheckBox(i,true);
                                        }
                                    }
                                }

                                tmp = adapter.getSelectedIds();
                            }
                        },1500);
                    }
                }, 1500);
            }
        }, 1500);

    }


    /*** 외부 DB인 cubrid 실행 ***
     * MenuActivity.java의 void cubrid()에 적혀있는 주석 참고
     */
    void count() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
                    String jdbcUrl = "jdbc:cubrid:192.168.0.5:30000:sample:::?charset=UTF-8";

                    conn = DriverManager.getConnection(jdbcUrl, "dba", "1234");

                    if(country.equals("All country")) { //나라에 상관없이 특정 종목의 모든 선수 수 세기
                        String sql = "SELECT name, sport, nation FROM info WHERE sport = \'" + sport + "\'";
                        //System.out.println("sql ******* " + sql);
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            cnt++;
                            //System.out.println("############################ cnt" + cnt);
                        }
                    }
                    else {                               //특정 종목과 특정 나라의 선수 수 세기
                        String sql = "SELECT name, sport, nation FROM info WHERE nation = \'" + country + "\' AND sport = \'" + sport + "\'";
                        //System.out.println("sql ******* " + sql);
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            cnt++;
                            //System.out.println("############################ cnt" + cnt);
                        }
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*** 외부 DB인 cubrid 실행 ***
     * MenuActivity.java의 void cubrid()에 적혀있는 주석 참고
     */
    void cubrid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
                    String jdbcUrl = "jdbc:cubrid:192.168.0.5:30000:sample:::?charset=UTF-8";

                    conn = DriverManager.getConnection(jdbcUrl, "dba", "1234");

                    if(country.equals("All country")) { //나라에 상관없이 특정 종목의 모든 선수 정보 받아오기
                        String sql = "SELECT name, sport, nation FROM info WHERE sport = \'" + sport + "\'";
                        System.out.println("sql ******* " + sql);
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                    }
                    else {                               //특정 종목과 특정 나라의 선수 수 정보 받아오기
                        String sql = "SELECT name, sport, nation FROM info WHERE nation = \'" + country + "\' AND sport = \'" + sport + "\'";
                        System.out.println("sql ******* " + sql);
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);
                    }

                    int i = 0;
                     while (rs.next()) { //String 배열 st에 받아온 정보 대입하기
                        st[i][0] = rs.getString("name");
                        String cubrid_name = rs.getString("name");
                        st[i][1] = rs.getString("sport");
                        String cubrid_sport = rs.getString("sport");
                        st[i][2] = rs.getString("nation");
                        String cubrid_nation = rs.getString("nation");
                        //System.out.println("########## name : " + cubrid_name + " sport : " + cubrid_sport + " nation : " + cubrid_nation);
                        i++;
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 동작이 수행되는 동안 dialog 표시
    public Handler mhandler = new Handler();
    public void progressBarDialog() {
        progressDialog = new ProgressDialog(PlayerActivity.this);

        progressDialog.setMessage("선수를 찾고 있습니다.");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        //progressDialog.setProgress(0);
        progressDialog.show();

        mhandler.postDelayed( new Runnable() {
            @Override public void run() {
                try {
                    if (progressDialog!=null&&progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }, 4500);
    }
}
