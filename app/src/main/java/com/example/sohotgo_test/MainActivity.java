package com.example.sohotgo_test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.sohotgo_test.ListData.RECEIVER;



public class MainActivity extends AppCompatActivity  implements OnClickListener {

    private List<ListData> lists;
    private ListView lv;
    private ImageView iv_send;
    private TextAdapter adapter;
    private ListData listData;

    private String Jsonstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("kwwl","in there");

        initView();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Jsonstr = (String) msg.obj;
            refresh(Jsonstr, RECEIVER);
            Log.e("test", "handleMessage: " + Jsonstr);
        }
    };

    private void initView() {
        Jsonstr = "lollol";
        lv = findViewById(R.id.lv);
        iv_send = findViewById(R.id.iv_send);
        lists = new ArrayList<ListData>();
        iv_send.setOnClickListener((OnClickListener) this);
        adapter = new TextAdapter(lists, this);
        lv.setAdapter(adapter);
        useAPI_withpost("first conv", handler);
    }

    //刷新页面
    private void refresh(String content,int flag) {
        listData = new ListData(content, flag);
        lists.add(listData);
        //如果item数量大于30，清空数据
        if (lists.size() > 30) {
            for (int i = lists.size()-2; i >= 0; i--) {
                // 移除数据
                lists.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        EditText Msg = findViewById(R.id.getMsg);
        String str = Msg.getText().toString();
        refresh(str, ListData.SEND);
        Msg.setText("");
        useAPI_withpost(str, handler);
    }

    private void useAPI_withpost(String msg, final Handler handler) {
        Log.d("kwwl","Msg is " + msg);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonStr = "{\"user_id\":\"12724243\"," +
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


//    public void display(View v){
//        System.out.println("in there");
//        EditText Msg = findViewById(R.id.getMsg);
//        TextView text = findViewById(R.id.showMsg);
//        text.setText(Msg.getText().toString());
//        System.out.println(Msg.getText().toString());
//    }
}