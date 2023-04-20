package com.tds.demo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tds.demo.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.utils.StringUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 2022-10-11
 * Describe：礼包系统
 */
public class GiftFragment extends Fragment implements View.OnClickListener{
    private static GiftFragment giftFragment = null;
    private static final String TAG = "GiftFragment";


    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.client_id)
    EditText client_id;
    @BindView(R.id.gift_code)
    EditText gift_code;
    @BindView(R.id.character_id)
    EditText character_id;
    @BindView(R.id.nonce_str)
    EditText nonce_str;
    @BindView(R.id.timestamp)
    EditText timestamp;
    @BindView(R.id.sign_btn)
    Button sign_btn;
    @BindView(R.id.sign)
    EditText sign;
    @BindView(R.id.request)
    Button request;
    @BindView(R.id.response_info)
    TextView response_info;


    public GiftFragment() {
    }

    public static final GiftFragment getInstance() {
        if (giftFragment == null) {
            giftFragment = new GiftFragment();
        }
        return giftFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_gift_fragment, container, false);
        ButterKnife.bind(this, view);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        sign_btn.setOnClickListener(this);
        request.setOnClickListener(this);

        client_id.setText("hskcocvse6x1cgkklm");
        gift_code.setText("87CfvvEdbRVPM");
        character_id.setText(randomCharacterId());
        nonce_str.setText("DFGH3");
        String timeStr = (System.currentTimeMillis() / 1000)+"";
        timestamp.setText(timeStr);

        textchangedListener(client_id);
        textchangedListener(nonce_str);
        textchangedListener(timestamp);
        textchangedListener(gift_code);
        textchangedListener(character_id);


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(GiftFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.cn/docs/sdk/tds-gift/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.sign_btn:
                getSign();
                break;
            case R.id.request:
                startSubmit();
                break;
            default:
                break;
        }
    }

    private void textchangedListener(EditText view){
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sign.setText("");
                response_info.setText("");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 开始兑换
     */

    private void startSubmit() {
        String url = "";
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("client_id",client_id.getText().toString());
            jsonObject.put("gift_code",gift_code.getText().toString());
            jsonObject.put("character_id",character_id.getText().toString());
            jsonObject.put("nonce_str",nonce_str.getText().toString());
            jsonObject.put("sign",sign.getText().toString());
            jsonObject.put("timestamp",Integer.parseInt(timestamp.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json;charset=utf-8"));
        Request requst = new Request.Builder()
                .url("https://poster-api.xd.cn/api/v1.0/cdk/game/submit-simple")
                .post(body)
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.obj = e.toString();
                mMainHandler.sendMessage(message);


            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                mMainHandler.sendMessage(message);

            }
        });


    }

    private Handler mMainHandler = new Handler(msg -> {
        if (msg.obj != null) {
            response_info.setText(msg.obj.toString());
        }
        return true;
    });



    /**
     * 获取签名
     */

    private void getSign() {
    // sign == sha1(timestamp + nonce_str + client_id)
        if(StringUtil.isEmpty(client_id.getText().toString())){
            sign.setText("");
            Toast.makeText(this.getActivity(), "用户 client_id 不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isEmpty(nonce_str.getText().toString())){
            sign.setText("");
            Toast.makeText(this.getActivity(), "随机字符串不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(StringUtil.isEmpty(timestamp.getText().toString())){
            sign.setText("");
            Toast.makeText(this.getActivity(), "时间戳不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
           String signTxt = shaEncode(timestamp.getText().toString()+nonce_str.getText().toString()+client_id.getText().toString());
           sign.setText(signTxt);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * SHA1 加密工具方法
     *
     */

    public String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    private String randomCharacterId(){

        String characterId ="";

        char[] a_aar = new char[26];
        for (int i = 0; i < a_aar.length; i++) {
            a_aar[i] = (char)(97+i);
        }

        Random random = new Random();
        for (int i=0; i<20; i++ ){
            int random_num = random.nextInt(10);
            if(random_num % 2 == 0){
                // 偶数为字母
                characterId = characterId + a_aar[random.nextInt(26)];
            }else{
                // 奇数为数字
                characterId = characterId + random.nextInt(10);
            }
        }



        return characterId;
    }

}


