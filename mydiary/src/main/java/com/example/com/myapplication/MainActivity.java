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

        // 년, 월, 일 변수에 값을 설정
        pYear = c.get(Calendar.YEAR); pMonth = c.get(Calendar.MONTH); pDate = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        // 파일 이름 설정
        fileName = Integer.toString(pYear) + "_"
                +  Integer.toString(pMonth+1) + "_"
                +  Integer.toString(pDate) + ".txt";
        String str = readDiary(fileName); // 초기 다이어리 불러오기
        et1.setText(str); // 다이어리 내용 Et1에 출력

        // 외부저장소 주소 가져오기
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File my = new File(sdPath + "/mydiary"); // 외부저장소에 주소값 + 'mydiary' 디렉토리 생성

        // 디렉토리가 정상적으로 생성되었으면 생성되었다고 토스트메세지 출력
        if(!my.exists()) {
            my.exists();
            Toast.makeText(getApplicationContext(), t1.getText().toString() + "mydiary 디렉터리가 생성됨", Toast.LENGTH_SHORT).show();
        }

        // t1을 클릭하여 날짜 설정가능, 달력 불러옴
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // 저장버튼
        bsave.setOnClickListener(new View.OnClickListener() { //저장 버튼
            @Override
            public void onClick(View v) {
                try {
                    // 설정한대로 파일이름을 fileName으로 지정하고 저장. 여기에서는 에뮬레이터상 외부가 아닌 내부저장소에 저장되게 하였습니다.
                    FileOutputStream  outFs = openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
                    String str = et1.getText().toString();
                    outFs.write(str.getBytes()); // et1에 입력된 일기 내용을 가져와 byte값으로 받아 저장소에 저장.
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show(); // 파일이름과 함께 저장되었다고 출력
                }
                catch (IOException e) { // 예외오류 처리문. 에러라고 토스트메세지를 출력
                    Toast.makeText(getApplicationContext(), fileName + " error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // 저장된 다이어리 내용을 불러오는 readDiary
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
        return diaryStr; // diaryStr를 리턴함
    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this,
                mDateSetListener, pYear, pMonth, pDate);
    }

    private  void updateDisplay() { // 호출되면 t1에 지정된 값을 가져와 setText함
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
    // menu_main.xml에서 내용을 가져와 옵션메뉴를 구현
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { // 옵션메뉴에서 선택되는 항목을 실행
        int id = item.getItemId();
        if(id == R.id.iReread) { //불러오기 옵션. 기존에 저장되었던 일기 내용을 다시 불러온다.
            String str = readDiary(fileName);
            et1.setText(str);
            return true;
        } else if (id == R.id.iDelete) { // 삭제 옵션으로 아래 옵션다이얼로그에서 구현되어있음. 그것을 호출함.
            openOptionsDialog();
        }
        // 글자 크기 옵션으로 글자 크기를 조정.
        else if (id == R.id.fLarge) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45);
        }else if (id == R.id.fMid) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }else if (id == R.id.fSmall) {
            et1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openOptionsDialog() { // 삭제 옵션으로 다이얼로그를 띄우고 [아니오/네]의 선택 사항에 따라 실행되게 구현함.
        new AlertDialog.Builder(this).setTitle("Deleting?")
                .setMessage(t1.getText() + " 일기를 정말 삭제하시겠습니까?")
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // [아니오] 선택 시 토스트 메세지와 함께 아무것도 실행, 리턴 하지 않음.
                        Toast.makeText(getApplicationContext(), "삭제가 취소되었습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .setPositiveButton("네", new DialogInterface.OnClickListener() { // [네] 선택 시 토스트 메세지 출력하고 저장소에 저장된 파일을 삭제, 현재 et1의 내용 또한 삭제
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File nowFile = new File("/mydiary" );
                        nowFile.delete();
                        et1.setText("");
                        Toast.makeText(getApplicationContext(), "정상적으로 삭제됨", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }
}
