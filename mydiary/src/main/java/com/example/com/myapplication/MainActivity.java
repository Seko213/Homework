package com.example.com.myapplication;
import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView t1;
    Button bsave;
    EditText et1;
    String fileName;


    private int pYear;
    private int pMonth;
    private int pDate;

    static final int DATE_DIALOG_ID = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("다이어리 앱");

        t1 = (TextView) findViewById(R.id.t1);
        bsave = (Button) findViewById(R.id.bsave);
        et1 = (EditText) findViewById(R.id.et1);

        final Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR); pMonth = c.get(Calendar.MONTH); pDate = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        fileName = Integer.toString(pYear) + "_"
                +  Integer.toString(pMonth+1) + "_"
                +  Integer.toString(pDate) + ".txt";
        String str = readDiary(fileName);
        et1.setText(str);

        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File my = new File(sdPath + "/mydiary");

        if(!my.exists()) {
            my.exists();
            Toast.makeText(getApplicationContext(), t1.getText().toString() + "mydiary 디렉터리가 생성됨", Toast.LENGTH_SHORT).show();
        }

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });




        bsave.setOnClickListener(new View.OnClickListener() { //저장 버튼
            @Override
            public void onClick(View v) {
                try {

                    FileOutputStream  outFs = openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
                    String str = et1.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    Toast.makeText(getApplicationContext(), t1.getText().toString() + " error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String readDiary(String fName) {
        String diaryStr = null;
        FileInputStream inFs;
        try{
            inFs = openFileInput(fName);
            byte[] txt = new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
        } catch (IOException e) {
            et1.setHint("                일기없음");
        }
        return diaryStr;
    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this,
                mDateSetListener, pYear, pMonth, pDate);
    }

    private  void updateDisplay() {
        t1.setText(new StringBuilder().append(pYear).append("년 ")
                .append(pMonth + 1).append("월 ").append(pDate).append("일"));
    }

    private  DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    fileName = Integer.toString(year) + "_"
                            +  Integer.toString(monthOfYear+1) + "_"
                            +  Integer.toString(dayOfMonth) + ".txt";
                    String str = readDiary(fileName);
                    et1.setText(str);
                    pYear = year;
                    pMonth = monthOfYear;
                    pDate = dayOfMonth;
                    updateDisplay();
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.iReread) {
            String str = readDiary(fileName);
            et1.setText(str);
            return true;
        } else if (id == R.id.iDelete) {
            openOptionsDialog();
        }
        else if (id == R.id.fLarge) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
        }else if (id == R.id.fMid) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }else if (id == R.id.fSmall) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openOptionsDialog() {
        new AlertDialog.Builder(this).setTitle("Deleting?")
                .setMessage(t1.getText() + " 일기를 정말 삭제하시겠습니까?")
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "삭제가 취소되었습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File nowFile = new File("/mydiary" );
                        nowFile.delete();
                        et1.setText("");
                    }
                }).show();
    }
}