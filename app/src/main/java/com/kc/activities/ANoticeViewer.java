package com.kc.activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.kc.R;
import com.kc.database.DBHelper;
import com.kc.database.DBHelper.dbType;
import com.kc.database.TNoticeboard;

public class ANoticeViewer extends AppCompatActivity {

    private int ID;
    private String header, body, from;
    private boolean fav;
    private int date, time;

    private DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_noticeviewer);

        ID = getIntent().getIntExtra("ID", 0);
        header = getIntent().getStringExtra("header");
        body = getIntent().getStringExtra("body");
        fav = getIntent().getBooleanExtra("fav", false);
        date = getIntent().getIntExtra("date", 1);
        time = getIntent().getIntExtra("time", 1);
        from = getIntent().getStringExtra("from");

        dbh = new DBHelper(this, dbType.WRITE);
        ContentValues cv = new ContentValues();
        cv.put(TNoticeboard.READ, 1);
        dbh.update(TNoticeboard.TABLE_NAME, cv, TNoticeboard.ID + " = " + ID, null);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        TextView h = (TextView) findViewById(R.id.notice_header_text);
        h.setText(header);

        TextView b = (TextView) findViewById(R.id.notice_body_text);
        b.setText(body);

        TextView dt = (TextView) findViewById(R.id.date_time_text);
        dt.setText(getSyntaxedDateTime(date, time));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbh.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_noticeviewer, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.favorite).setIcon(fav ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
        menu.findItem(R.id.favorite).setTitle(fav ? "un-favorite" : "favorite");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.favorite:
                ContentValues cv = new ContentValues();
                cv.put(TNoticeboard.FAV, fav ? 0 : 1);
                item.setIcon(null);
                item.setIcon(fav ? android.R.drawable.star_big_off : android.R.drawable.star_big_on);
                item.setTitle(fav ? "un-favorite" : "favorite");
                dbh.update(TNoticeboard.TABLE_NAME, cv, TNoticeboard.ID + " = " + ID, null);
                fav = !fav;
                return true;

            case R.id.delete:
                dbh.delete(TNoticeboard.TABLE_NAME, TNoticeboard.ID + " = " + ID, null);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    String getSyntaxedDateTime(int date, int time) {

        // decoding date
        int y = (date / 10000);
        date -= (y * 10000);

        int m = date / 100;
        date -= (m * 100);

        int d = date;

        String da = d + " " + monthName(m) + " " + (y + 2000);

        // decoding time
        boolean am = false;
        if (time < 1200) {
            am = true;
        }

        int h = time / 100;
        time -= (h * 100);
        if (!am) {
            h -= 12;
        }

        int min = time;

        String tm = null;
        if (am) {
            if (min < 10) {
                tm = h + ":0" + min + " am";
            } else {
                tm = h + ":" + min + " am";
            }
        } else {
            if (min < 10) {
                tm = h + ":0" + min + " am";
            } else {
                tm = h + ":" + min + " pm";
            }
        }

        return da + ", " + tm;

    }

    String monthName(int m) {
        switch (m) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "-";
        }
    }

}
