package com.tds.demo.fragment.IM;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.antiaddictionui.utils.ToastUtils;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;
import com.tds.gson.JsonObject;
import com.tds.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCFile;
import cn.leancloud.LCUser;
import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.LCIMMessage;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.callback.LCIMConversationCallback;
import cn.leancloud.im.v2.callback.LCIMMessagesQueryCallback;
import cn.leancloud.im.v2.callback.LCIMOperationFailure;
import cn.leancloud.im.v2.callback.LCIMOperationPartiallySucceededCallback;
import cn.leancloud.im.v2.messages.LCIMImageMessage;
import cn.leancloud.im.v2.messages.LCIMTextMessage;

/**
 * 2022/10/19
 * Describe?????????
 */
public class AloneChatFragment extends DialogFragment implements View.OnClickListener{

    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.send_message)
    EditText send_message;
    @BindView(R.id.message_recycle)
    RecyclerView message_recycle;
    @BindView(R.id.other_msg_box)
    ConstraintLayout other_msg_box;

    @BindView(R.id.add_type)
    ImageButton add_type;
    @BindView(R.id.send_img)
    ImageButton send_img;

    @BindView(R.id.invite_button)
    TextView invite_button;
    private LCIMConversation conversation;
    private String imgPath = "";
    private static AloneChatFragment aloneChatFragment = null;
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter = null;
    public AloneChatFragment() {
    }

    public static final AloneChatFragment getInstance(ChatBean chatBean) {
        if (aloneChatFragment == null) {
            aloneChatFragment = new AloneChatFragment();
        }
        Bundle args = new Bundle();
        args.putSerializable("chatBean", chatBean);
        aloneChatFragment.setArguments(args);
        return aloneChatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.friends_alone_chat_fragment, container, false);
        ButterKnife.bind(this, view);

        LCIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());
        LCIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());


        return view;
    }


    /**
     * ?????? ObjectId????????????
     *
     * */
    public void searchMsg(){

        // limit ???????????? 1~100???????????? queryMessages ????????? limit ????????????????????? 20 ???????????????
        int limit = 100;
        conversation.queryMessages(limit, new LCIMMessagesQueryCallback() {
            @Override
            public void done(List<LCIMMessage> messages, LCIMException e) {
                if (e == null) {
                    for(int i=0; i< messages.size(); i++){
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(messages.get(i).getContent());
                        // ???????????????????????????
                        if(messages.get(i).getFrom().equals(LCUser.currentUser().getServerData().get("nickname").toString())){
                            // ??????????????????
                            if( jsonObject.get("_lctype").getAsInt() == Msg.TEXT_TYPE){
                                Msg received_content = new Msg();
                                received_content.setContent(jsonObject.get("_lctext").getAsString());
                                received_content.setType(Msg.TYPE_SENT);
                                received_content.setMsg_type(Msg.TEXT_TYPE);
                                msgList.add(received_content);
                            }else {
                                JsonObject jsonFileObject = (JsonObject) new JsonParser().parse(jsonObject.get("_lcfile").toString());
                                Msg received_content = new Msg();
                                received_content.setImagePath(jsonFileObject.get("url").getAsString());
                                received_content.setType(Msg.TYPE_SENT);
                                received_content.setMsg_type(Msg.IMG_TYPE);
                                msgList.add(received_content);
                            }


                        }else{
                            // ??????????????????
                            if( jsonObject.get("_lctype").getAsInt() == Msg.TEXT_TYPE){
                                Msg received_content = new Msg();
                                received_content.setContent(jsonObject.get("_lctext").getAsString());
                                received_content.setType(Msg.TYPE_RECEIVED);
                                received_content.setMsg_type(Msg.TEXT_TYPE);
                                msgList.add(received_content);
                            }else {
                                JsonObject jsonFileObject = (JsonObject) new JsonParser().parse(jsonObject.get("_lcfile").toString());
                                Msg received_content = new Msg();
                                received_content.setImagePath(jsonFileObject.get("url").getAsString());
                                received_content.setType(Msg.TYPE_RECEIVED);
                                received_content.setMsg_type(Msg.IMG_TYPE);
                                msgList.add(received_content);
                            }


                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add_type.setOnClickListener(this);
        send_img.setOnClickListener(this);
        invite_button.setOnClickListener(this);

        send_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    other_msg_box.setVisibility(View.GONE);
                }
            }
        });


        if(getArguments() != null) {
            ChatBean chatBean = (ChatBean) this.getArguments().getSerializable("chatBean");
            conversation = chatBean.getConversation();
            title.setText( conversation.getMembers().get(0)+"???"+conversation.getMembers().get(1));

        }

        searchMsg();

        // ???????????????????????????
        send_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    if(!send_message.getText().toString().isEmpty()){
                        sendMessage(conversation, send_message.getText().toString());
                        send_message.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        message_recycle.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        message_recycle.setAdapter(adapter);


        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack("aloneChatFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // ?????????Activity??????????????????

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this); // ??????
        super.onStop();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        msgList.clear();
        adapter.notifyDataSetChanged();

    }

    // ?????????????????????????????????????????????
    @Subscribe
    public void onEvent(MessageEvent messageEvent) {
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(messageEvent.getlCIMMessage().getContent());

        if( jsonObject.get("_lctype").getAsInt() == Msg.TEXT_TYPE){
            Msg msg = new Msg();
            msg.setContent(jsonObject.get("_lctext").getAsString());
            msg.setType(Msg.TYPE_RECEIVED);
            msg.setMsg_type(Msg.TEXT_TYPE);
            msgList.add(msg);
        }else {
            JsonObject jsonFileObject = (JsonObject) new JsonParser().parse(jsonObject.get("_lcfile").toString());
            Msg received_content = new Msg();
            received_content.setImagePath(jsonFileObject.get("url").getAsString());
            received_content.setType(Msg.TYPE_RECEIVED);
            received_content.setMsg_type(Msg.IMG_TYPE);
            msgList.add(received_content);
        }
        adapter.notifyDataSetChanged();

    }


    // ?????????????????????????????????????????????
    @Subscribe
    public void onInviteEvent(InviteEvent inviteEvent) {
         ToastUtil.showCus("??????"+inviteEvent.getMembers().get(0)+"???????????????", ToastUtil.Type.SUCCEED);

    }

    /**
     * ????????????
     *
     **/
    private void sendMessage(LCIMConversation conversation, String message) {
        LCIMTextMessage msg = new LCIMTextMessage();
        msg.setText(message);
        // ????????????
        conversation.sendMessage(msg, new LCIMConversationCallback() {
            @Override
            public void done(LCIMException e) {
                if (e == null) {
                    Msg send_content = new Msg();
                    send_content.setContent(message);
                    send_content.setType( Msg.TYPE_SENT);
                    send_content.setMsg_type(Msg.TEXT_TYPE);
                    msgList.add(send_content);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_type:

                other_msg_box.setVisibility(View.VISIBLE);
                send_message.clearFocus();
                hideKeyboard(getActivity(), send_message);
                break;
            case R.id.send_img:
                sendImg();
                break;
            case R.id.invite_button:
                inviteUserChat();

                break;
            default:
                break;
        }

    }


    /*
    * ??????????????????
    * */
    private void inviteUserChat() {


        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.popview_invite, null);

        PopupWindow mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopupWindow.setOutsideTouchable(false);//??????????????????
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.alpha = 0.5f;
        getActivity().getWindow().setAttributes(attributes);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams attributes1 = getActivity().getWindow().getAttributes();
            attributes1.alpha = 1;
            getActivity().getWindow().setAttributes(attributes1);
        });

        EditText inviteUser = view.findViewById(R.id.user_edit);
        Button invite_submit = view.findViewById(R.id.invite_submit);
        Button cancel = view.findViewById(R.id.cancel);

        invite_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inviteUser.getText().toString().isEmpty()){
                    ToastUtil.showCus("?????????????????????Tap???????????????", ToastUtil.Type.POINT);
                    return;
                }
                // ???????????????????????????
                LCIMClient testClient = LCIMClient.getInstance(LCUser.currentUser().getServerData().get("nickname").toString());

                final LCIMConversation conv = testClient.getConversation(conversation.getConversationId());
                // ?????? Mary ????????????
                conv.addMembers(Arrays.asList(inviteUser.getText().toString()), new LCIMOperationPartiallySucceededCallback() {
                    @Override
                    public void done(LCIMException e, List<String> successfulClientIds, List<LCIMOperationFailure> failures) {
                        // ????????????
                        mPopupWindow.dismiss();

                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

//        // Tom ??????????????? client?????????????????? clientId ????????????



    }


    /**
     *
     * ????????????????????????
     * */
    private void sendImg(){
        setPhotoImage();


    }

/**
 * ????????????
 *
 * */
    public static void hideKeyboard(Activity activity, View view){
        if(activity==null||view==null){
            return;
        }
        InputMethodManager imm=(InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    /**
     * ??????????????????
     * */

    private final int OPEN_ALBUM_REQUESTCODE = 1; //?????????


    private void setPhotoImage(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK); //????????????
        openAlbumIntent.setType("image/*");     //??????????????????
        startActivityForResult(openAlbumIntent, OPEN_ALBUM_REQUESTCODE); //????????????

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("TAG", "onActivityResult:++++++++++++++++++++++++ " );
        if (requestCode == OPEN_ALBUM_REQUESTCODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                String picPath = getPicPath(uri);
                imgPath =picPath;

                LCFile file = null;
                try {
                    file = LCFile.withAbsoluteLocalPath("my.png", picPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // ????????????????????????
                LCIMImageMessage m = new LCIMImageMessage(file);
                conversation.sendMessage(m, new LCIMConversationCallback() {
                    @Override
                    public void done(LCIMException e) {
                        if (e == null) {
                            // ????????????
                            Msg msg = new Msg();
                            msg.setContent("message");
                            msg.setMsg_type(Msg.IMG_TYPE);
                            msg.setType(Msg.TYPE_SENT);
                            msg.setImagePath(imgPath);
                            msgList.add(msg);
                            adapter.notifyDataSetChanged();

                        }
                    }
                });

            }
        }
    }

    /**
     * ??????????????????
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    private String getPicPath(Uri uri){
        String[] picPathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getActivity().getContentResolver().query(uri, picPathColumns, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(picPathColumns[0]));
    }


}
