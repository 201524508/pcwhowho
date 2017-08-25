package com.example.yuuuuu.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by YUNA 2017-08-10.
 *            s0woo 2017-08-16.
 */


public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private GridAdapter adapter;
    String[][] st = new String[114][3]; //전체 선수 114명
    SparseBooleanArray tmp;

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    DBbookmark bookmark = new DBbookmark(MenuActivity.this, "Bookmark.db", null, 1);
    SQLiteDatabase dbBM;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final ListView listView = (ListView) findViewById(R.id.listView);

        dbBM = bookmark.getWritableDatabase();
        bookmark.onCreate(dbBM);
        final Cursor c = dbBM.query("Bookmark", null, null, null, null, null, null);

        cubrid();

        context = this;
        adapter = new GridAdapter(context, st, true);

        listView.setAdapter(adapter);

        tmp = adapter.getSelectedIds();

    }

    //화면이 실행될 때 마다 다음 함수 실행
    @Override
    public void onStart() {
        super.onStart();

        dbBM = bookmark.getWritableDatabase();
        bookmark.onCreate(dbBM);
        final Cursor c = dbBM.query("Bookmark", null, null, null, null, null, null);

        context = this;
        adapter = new GridAdapter(context, st, true);

        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        tmp = adapter.getSelectedIds();

        progressBarDialog();

        //bookmark DB에 저장된 내용이라면 체크박스 true 표시
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run() {
                adapter.notifyDataSetChanged();
                for(int i=0; i<114; i++) {
                    if(c.getCount() == 0) {
                        System.out.println("db내용 없음");
                        adapter.checkCheckBox(i,false);
                        break;
                    }

                    c.moveToFirst();
                    String dbName = c.getString(c.getColumnIndex("name"));
                    String dbSport = c.getString(c.getColumnIndex("sport"));
                    String dbNation = c.getString(c.getColumnIndex("nation"));
                    //System.out.println("**Db check : " + dbName + " " + dbSport + dbNation);
                    //System.out.println("st 내용    : " + st[i][0] + " " + st[i][1] + " " + st[i][2]);

                    if(dbName.equals(st[i][0]) && dbSport.equals(st[i][1]) && dbNation.equals(st[i][2])) {
                        System.out.println("i" + i);
                        adapter.checkCheckBox(i,true);
                    }

                    while(c.moveToNext()) {
                        dbName = c.getString(c.getColumnIndex("name"));
                        dbSport = c.getString(c.getColumnIndex("sport"));
                        dbNation = c.getString(c.getColumnIndex("nation"));
                        System.out.println("**Db check : " + dbName + " " + dbSport + dbNation);
                        System.out.println("st 내용    : " + st[i][0] + " " + st[i][1] + " " + st[i][2]);
                        if(dbName.equals(st[i][0]) && dbSport.equals(st[i][1]) && dbNation.equals(st[i][2])) {
                            System.out.println("i" + i);
                            adapter.checkCheckBox(i,true);
                        }
                    }
                }
            }
        }, 1500);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.LIKE) {
            Intent intent = new Intent(getApplication(), BookmarkActivity.class);
            startActivity(intent);

        } else if (id == R.id.ALP) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","알파인 스키");
            startActivity(intent);

        } else if (id == R.id.BTH) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","바이애슬론");
            startActivity(intent);

        } else if (id == R.id.CCS) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","크로스컨트리 스키");
            startActivity(intent);

        } else if (id == R.id.FRS) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","프리스타일 스키");
            startActivity(intent);

        } else if (id == R.id.NCB) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","노르딕 복합");
            startActivity(intent);

        } else if (id == R.id.SBD) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","스노보드");
            startActivity(intent);

        } else if (id == R.id.SJP) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","스키점프");
            startActivity(intent);

        } else if (id == R.id.CUR) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","컬링");
            startActivity(intent);

        } else if (id == R.id.FSK) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","피겨 스케이팅");
            startActivity(intent);

        } else if (id == R.id.IHO) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","아이스 하키");
            startActivity(intent);

        } else if (id == R.id.SSK) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","스피드 스케이팅");
            startActivity(intent);

        } else if (id == R.id.STK) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","쇼트트랙 스피드 스케이팅");
            startActivity(intent);

        } else if (id == R.id.BOB) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","봅슬레이");
            startActivity(intent);

        } else if (id == R.id.LUG) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","루지");
            startActivity(intent);

        } else if (id == R.id.SKN) {
            Intent intent = new Intent(getApplication(), FlagListActivity.class);
            intent.putExtra("sport","스켈레톤");
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

     /*** 외부 DB인 cubrid 실행 ***
      * String jdbcUrl의 172.30.1.48은 현재 큐브리드가 설치된 컴퓨터(노트북)와 연결된 LAN(혹은 무선 WIFI)의 IPv4 주소이며,
      * 어플리케이션을 실행하는 핸드폰도 컴퓨터와 같은 LAN(혹은 무선WIFI)에 연결되어 있어야 큐브리드의 내용을 조회 가능.
      *
      * 컴퓨터와 연결된 LAN(혹은 무선WIFI)가 달라진다면,
      * cmd창에서 'ipconfig /all' 명령어를 수행하고 LAN(혹은 무선 WIFI)의 IPv4 주소를 '172.30.1.48' 위치에 입력하여야 한다.
      * IPv4 주소가 잘못될경우 에러가 발생하며 어플리케이션은 강제 종료된다.
      * 'PlayerActivity.java' 에도 cubrid와 연결하는 함수가 2개 있다. (void count(), void cubrid()) 모두 수정해주어야 한다.
      * 'PlayerDetailActivity.java'에도 연결하는 함수가 1개 있다. (void cubrid()) 수정해주어야 한다.
      * 'CameraActivity.java'에도 연결하는 함수가 1개 있다. (void cubrid()) 수정해주어야 한다.
      */
    void cubrid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
                    String jdbcUrl = "jdbc:cubrid:192.168.0.5:30000:sample:::?charset=UTF-8";

                    conn = DriverManager.getConnection(jdbcUrl, "dba", "1234");

                    String sql = "SELECT name, sport, nation FROM info";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    //System.out.println("=====================================================");

                    int i=0;
                    while (rs.next()) {
                        st[i][0] = rs.getString("name");
                        //name = rs.getString("name");
                        st[i][1] = rs.getString("sport");
                        // sport = rs.getString("sport");
                        st[i][2] = rs.getString("nation");
                        //nation = rs.getString("nation");
                        //System.out.println("name : " + name + " " + st[i][0] + " sport : " + sport + " nation : " + nation);
                        i++;
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch(NullPointerException e) {
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
        progressDialog = new ProgressDialog(MenuActivity.this);

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
        }, 1500);
    }

}




