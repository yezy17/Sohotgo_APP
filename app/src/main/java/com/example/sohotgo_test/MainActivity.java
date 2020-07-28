package com.example.sohotgo_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.sohotgo_test.ListData.RECEIVER;



public class MainActivity extends AppCompatActivity  implements EventListener {

    private List<ListData> lists;
    private ListView lv;
    private ImageButton iv_send;
    //private TextAdapter adapter;
    private ListData listData;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    final  static int LEFT=2;
    final  static int RIGHT=1;

    private String Jsonstr;
    private String userID;

    //百度语音识别相关
    private EventManager asr;
    // 百度语音合成相关
    protected SpeechSynthesizer mSpeechSynthesizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("kwwl","in there");
        initView();
        initTTs();
        initPermission();
        /*  百度语音相关   */
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);

        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Jsonstr = (String) msg.obj;
            refresh(Jsonstr, RECEIVER);
            Log.e("test", "handleMessage: " + Jsonstr);

            readtext(Jsonstr);
        }
    };

    private void initView() {
        Jsonstr = "lollol";
        getRandomUserID();
        iv_send = findViewById(R.id.iv_send);
        lists = new ArrayList<ListData>();
        useAPI_withpost("first", handler);

        recyclerView= (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter=new ItemAdapter(lists,this);
        recyclerView.setAdapter(itemAdapter);
    }

    private void initTTs() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setAppId("11005757");
        mSpeechSynthesizer.setApiKey("Ovcz19MGzIKoDDb3IsFFncG1","e72ebb6d43387fc7f85205ca7e6706e2");

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "1"); // 设置发声的人声音，在线生效
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
    }

    private void getRandomUserID() {
        String idRand="" ;
        for(int i=0;i<8;i++){
            idRand += String.valueOf((int)(Math.random() * 10)) ;
        }
        userID = idRand;
    }

    @SuppressLint("HandlerLeak")
    private void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        // 基于SDK集成2.1 设置识别参数
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号

        /* 语音自训练平台特有参数 */
        // params.put(SpeechConstant.PID, 8002);
        // 语音自训练平台特殊pid，8002：模型类似开放平台 1537  具体是8001还是8002，看自训练平台页面上的显示
        // params.put(SpeechConstant.LMID,1068); // 语音自训练平台已上线的模型ID，https://ai.baidu.com/smartasr/model
        // 注意模型ID必须在你的appId所在的百度账号下
        /* 语音自训练平台特有参数 */

        /* 测试InputStream*/
        // InFileStream.setContext(this);
        // params.put(SpeechConstant.IN_FILE, "#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.createMyPipedInputStream()");

        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        // 复制此段可以自动检测错误
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        Log.w("AutoCheckMessage", message);
                    }
                }
            }
        }, false)).checkAsr(params);
        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        Toast.makeText(this, "小Go在听哦~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 识别相关的结果都在这里
            if (params == null || params.isEmpty()) {
                return;
            }
            if (params.contains("\"nlu_result\"")) {
                // 一句话的语义解析结果
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            } else if (params.contains("\"partial_result\"")) {
                // 一句话的临时识别结果
                logTxt += ", 临时识别结果：" + params;
            }  else if (params.contains("\"final_result\""))  {
                // 一句话的最终识别结果
                logTxt += ", 最终识别结果：" + params;
                getResFromParams(params);
                Toast.makeText(this, "结束收音", Toast.LENGTH_SHORT).show();
            }  else {
                // 一般这里不会运行
                logTxt += " ;params :" + params;
                if (data != null) {
                    logTxt += " ;data length=" + data.length;
                }
            }
        } else {
            // 识别开始，结束，音量，音频数据回调
            if (params != null && !params.isEmpty()){
                logTxt += " ;params :" + params;
            }
            if (data != null) {
                logTxt += " ;data length=" + data.length;
            }
        }
    }

    private void getResFromParams(String params) {
        String asr_res = "";
        try {
            JSONObject result = new JSONObject(params);
            asr_res = (String) result.get("best_result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh(asr_res, ListData.SEND);
        useAPI_withpost(asr_res, handler);
    }


    //刷新页面
    private void refresh(String content,int flag) {
        //如果item数量大于30，清空数据
        if (lists.size() > 30) {
            for (int i = lists.size()-2; i >= 0; i--) {
                // 移除数据
                lists.remove(i);
            }
        }
        itemAdapter.addItem(content,flag);
        recyclerView.smoothScrollToPosition(lists.size());
    }
    

    private void useAPI_withpost(String msg, final Handler handler) {
        msg = msg.replaceAll("[，？]", "");
        Log.d("kwwl","Msg is " + msg);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonStr = "{\"user_id\":\"" + userID + "\"," +
                "\"first_chat\": false," +
                "\"request_text\": \""+ msg +"\"," +
                "\"user_city\": \"北京\"," +
                "\"user_loc\": {\"longitude\": \"32.32\",\"latitude\": \"32.32\"}," +
                "\"user_ip\": \"10.142.115.214\"," +
                "\"equipment_type\": \"tangmao\"}";
        RequestBody body = RequestBody.create(jsonStr, JSON);
        Request request = new Request.Builder()
                .url("http://api.cbd.sogou.com/TaskbotGatewayService/chatInterface?token=27hCui%2FPASMwrc18BgcvgEwL%2FdCXOARzTWwMfcHDloQq66JjllcJ5rjnF%2BT8fqhMTYLbyzV2Fe9PP8cP5t0Y%2FJpFDJwhvYvvIYgEibgqscDuPh9U%2F0yWWr3dD43x4Q7rJi%2Fa1VF72x1%2B8EARV7FThQ%3D%3D&appid=nOPKidm7eLkmCCnF")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                Log.d("kwwl","onFailure");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    Log.d("kwwl","获取数据成功了");
                    Message message = new Message();
                    String tempStr = getAnswer(Objects.requireNonNull(response.body()).string());
                    message.obj = tempStr;
                    handler.sendMessage(message);
                } else {
                    Log.d("kwwl","response no success");
                }
            }
        });
    }

    public String getAnswer(String str){
        try {
            JSONObject ja = new JSONObject(str);
            JSONObject result = ja.getJSONObject("result");
            JSONArray ans_results = result.getJSONArray("answer_results");
            JSONObject ans_info = ans_results.getJSONObject(0);
            Log.d("kwwl",ans_info.getString("answer_text"));
            // refresh(ans_info.getString("answer_text"), RECEIVER);
            return ans_info.getString("answer_text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "get answer failed";
    }

    private void readtext(String str){
        if (str.length() > 60) {
            String[] strs = str.split("[;,，。?？：]");
            String str_add = "";
            for (String s : strs) {
                if (str_add.length() + s.length() > 60) {
                    mSpeechSynthesizer.speak(str_add);
                    str_add = "";
                }
                str_add = str_add + "，" + s;
            }
            mSpeechSynthesizer.speak(str_add);
        } else {
            mSpeechSynthesizer.speak(str);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        Log.i("ActivityMiniRecog", "On pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 基于SDK集成4.2 发送取消事件
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);

        // 基于SDK集成5.2 退出事件管理器
        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr.unregisterListener(this);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}