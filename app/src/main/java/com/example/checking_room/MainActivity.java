package com.example.checking_room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    List<Waitmsg> waitmsgList;
    List<Ghmsg> passedList;
    TextView dateTv,roomName1,nowCheckingName1,nowCheckingNumber1,roomName2,nowCheckingName2,nowCheckingNumber2,roomName3,nowCheckingName3,nowCheckingNumber3;
    View room1,room2,room3;
    SmoothScrollLayout<Waitmsg> waitingRv;
    SmoothScrollLayout<Ghmsg> passedRv;
    TextToSpeech tts;
    boolean isTTSInitialised = false;
    private String IP_ADDRESS= "192.168.11.127";
    private int PORT=7001;
    private static final String ERROR_LOG = "10.97.160.13:8281/lcdLog/save";
    private static final int TIME_ERROR = 0x1;
    private  Socket socket;
    List<CheckingRoomResponse> responses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_layout);
        dateTv = findViewById(R.id.date_tv);
        initPermission();
        initDateView();
        initRooms();
        initSmoothScrollLayout();
        initPassedRv();
        initTTS();
        connectTcpAndGetData();
    }

    private void initDateView() {
        dateTv = findViewById(R.id.date_tv);
        startTimeThread();

    }

    private void startTimeThread() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            try{
                                Thread.sleep(1000);
                                Message msg = Message.obtain();
                                msg.what = 0x125;
                                handler.sendMessage(msg);
                            }catch (InterruptedException e){
                                Test(e,TIME_ERROR);
                                Log.e("read time error",e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
    }

    public void Test(Exception e, int position) {
        //  post请求body
        Map map = new HashMap();
        map.put("content", e.getMessage() + "位置:" + position);
        map.put("type", 1);

        Gson gson = new Gson();
//  转换层json字符串
        final String s = gson.toJson(map);
//  创建  RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), s);
//  获取OkhthpClient 实例
        OkHttpClient okHttpClient1 = OkHttpUtils.getInstance().getOkHttpClient();

//  创建请求
        Request request = new Request.Builder().url(ERROR_LOG)
                .post(requestBody).build();
//  请求网络
        okHttpClient1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("sss", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("sss", string);
            }
        });
    }

    private void connectTcpAndGetData() {//链接socket，获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                BufferedReader bufferedReader = null;
                while (true) {
                    String line = null;
                    try {
                        socket = new Socket(IP_ADDRESS, PORT);
                        socket.setSoTimeout(20000);
                        in = socket.getInputStream();
                        BufferedReader bufr = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        Log.e("socket","准备读消息");
                        while ((line = bufr.readLine()) != null) {
                            Log.e("line",line);
                            CheckingRoomResponse[] checkingRoomResponses = new Gson().fromJson(line, CheckingRoomResponse[].class);
                            //responses = Arrays.asList(listeningRoomResponse);
                            Message msg = Message.obtain();
                            msg.obj = checkingRoomResponses;
                            msg.what = 0x123;
                            handler.sendMessage(msg);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    if(!Arrays.asList((CheckingRoomResponse[]) msg.obj).equals(responses)){
                        responses = Arrays.asList((CheckingRoomResponse[]) msg.obj);
                        waitmsgList.clear();
                        passedList.clear();
                        for(int i=0; i<responses.size();i++){
                            CheckingRoomResponse response = responses.get(i);
                            Log.e("response","patient name:"+response.getBrxm()+"patient number:"+response.getPdhm()+"room name:"+response.getFjmc());
                            if(response.getFjmc().equals("room1")){
                                roomName1.setText(response.getFjmc());
                                nowCheckingNumber1.setText(response.getPdhm()+"号");
                                String name = changePatientName(response.getBrxm());
                                nowCheckingName1.setText(name);
                                Waitmsg waitmsg1 = new Waitmsg(response.getFjmc(),response.getBrxm(),response.getPdhm());
                                new VoiceThread(waitmsg1).start();
                                //todo:check if it's same as last time, if not, send data to tts
                                waitmsgList.addAll(response.getWaitmsg());
                                passedList.addAll(response.getGhmsg());
                            }else if(response.getFjmc().equals("room2")){
                                roomName2.setText(response.getFjmc());
                                nowCheckingNumber2.setText(response.getPdhm()+"号");
                                String name = changePatientName(response.getBrxm());
                                nowCheckingName2.setText(name);
                                Waitmsg waitmsg2 = new Waitmsg(response.getFjmc(),response.getBrxm(),response.getPdhm());
                                new VoiceThread(waitmsg2).start();
                                waitmsgList.addAll(response.getWaitmsg());
                                passedList.addAll(response.getGhmsg());
                            }else if(response.getFjmc().equals("room3")){
                                roomName3.setText(response.getFjmc());
                                nowCheckingNumber3.setText(response.getPdhm()+"号");
                                String name = changePatientName(response.getBrxm());
                                nowCheckingName3.setText(name);
                                Waitmsg waitmsg3 = new Waitmsg(response.getFjmc(),response.getBrxm(),response.getPdhm());
                                new VoiceThread(waitmsg3).start();
                                waitmsgList.addAll(response.getWaitmsg());
                                passedList.addAll(response.getGhmsg());
                            }
                        }
                        waitingRv.setData(waitmsgList);
                        passedRv.setData(passedList);
                    }
                    break;
                case 0x125:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd  EEEE  HH:mm:ss", sysTime);
                    dateTv.setText(sysTimeStr);
                    break;
            }
        }
    };

    private String changePatientName(String name){
        int lastLen = name.length();
        if (name.contains("(") || name.contains("（")) {
            lastLen = name.contains("(") ? name.indexOf("(") : name.indexOf("（");
        }
        switch (lastLen) {
            case 2:
            case 3:
                name = name.substring(0, 1) + "*" + name.substring(lastLen-1, name.length()) ;
                break;
            case 4:
                name = name.substring(0, 1) + "**" + name.substring(lastLen-1, name.length()) ;
                break;
        }
        return name;
    }

    private class VoiceThread extends Thread{
        Waitmsg waitmsg;

        public VoiceThread(Waitmsg waitmsg){
            this.waitmsg = waitmsg;
        }
        @Override
        public void run() {//control how many times to play this string
            if(isTTSInitialised){
                //Waitmsg waitmsg = (Waitmsg) msg.obj;
                String textToRead = "请"+waitmsg.getPdhm()+"号"+waitmsg.getBrxm()+"到测量室测量"; //todo, change room name
                for(int i=0;i<3;i++) {
                    tts.speak(textToRead+",", TextToSpeech.QUEUE_ADD, null);
                }
            }
        }
    }

    private void initRooms() {
        room1=findViewById(R.id.room1);
        roomName1=((View) room1).findViewById(R.id.room_name);
        nowCheckingName1=room1.findViewById(R.id.now_checking_name);
        nowCheckingNumber1=room1.findViewById(R.id.now_checking_number);

        room2=findViewById(R.id.room2);
        roomName2=((View) room2).findViewById(R.id.room_name);
        nowCheckingName2=room2.findViewById(R.id.now_checking_name);
        nowCheckingNumber2=room2.findViewById(R.id.now_checking_number);

        room3=findViewById(R.id.room3);
        roomName3=((View) room3).findViewById(R.id.room_name);
        nowCheckingName3=room3.findViewById(R.id.now_checking_name);
        nowCheckingNumber3=room3.findViewById(R.id.now_checking_number);


        roomName1.setText("I am room 1");
        nowCheckingNumber1.setText("1");
        nowCheckingName1.setText("child 1");

    }

    private void initPassedRv() {
        passedRv = findViewById(R.id.passed_scroll_layout);
        passedList=new ArrayList<>();
        passedList.add(new Ghmsg("诊室1","小朋友1","1"));
        passedList.add(new Ghmsg("诊室1","小朋友2","2"));
        passedList.add(new Ghmsg("诊室1","小朋友3","3"));
        passedList.add(new Ghmsg("诊室1","小朋友4","4"));
        //ghAdapter = new MyAdapter();
        passedRv.setData(passedList);
        //ghAdapter.setList(passedList);
    }

    private void initSmoothScrollLayout() {
        waitingRv = findViewById(R.id.waiting_scroll_layout);
        waitmsgList = new ArrayList<>();
        responses = new ArrayList<>();
        //dumb test
        waitmsgList.add(new Waitmsg("检查室1","小朋友1","5"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友2","6"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友3","7"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友4","8"));
        waitmsgList.add(new Waitmsg("检查室1","小朋友5","9"));
        waitingRv.setData(waitmsgList);
    }

    private void initTTS() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == tts.SUCCESS) {
                    isTTSInitialised = true;
                    int result = tts.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE){
                        Toast.makeText(MainActivity.this, "TTS暂时不支持这种语音的朗读！",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.shutdown();//关闭TTS
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }
}
