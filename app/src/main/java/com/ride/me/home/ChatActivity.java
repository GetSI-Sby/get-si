package com.ride.me.home;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ride.me.adapter.ChatAdapter;
import com.ride.me.adapter.ItemListener;
import com.ride.me.api.FCMHelper;
import com.ride.me.GoridemeAplication;
import com.ride.me.model.Chat;
import com.ride.me.model.User;
import com.ride.me.model.json.fcm.FCMMessage;
import com.ride.me.utils.AsyncTaskHelper;
import com.ride.me.utils.Log;
import com.ride.me.utils.db.DBHandler;
import com.ride.me.utils.db.Queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.ride.me.config.General.FCM_KEY;
import static com.ride.me.service.GoridemeMessagingService.BROADCAST_ACTION;

public class ChatActivity extends AppCompatActivity {

    ChatActivity activity;
    ImageView image;
    ArrayList<Chat> arrChat;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviChat;
    private ChatAdapter chatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    int mDatasetTypes[];
    Queries que;
    EditText inputText;
    ImageView butSend;
    Bundle recv;
    Intent newNotif;
    String regID;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Chat chatting;
//            recv = intent.getExtras();
//            chatting = (Chat) recv.getSerializable("chat");
//            que.insertChat(chatting);
            checkIfNeedRefill(intent);
            updateListView();
            removeNotif();
//            Toast.makeText(context, chatting.isi_chat, Toast.LENGTH_SHORT).show();
//            removeNotif();
        }
    };

    private void checkIfNeedRefill(Intent intent) {
        Chat chat = (Chat) intent.getSerializableExtra("chat");
        if(!chat.reg_id_tujuan.equalsIgnoreCase(regID)) {
            regID = chat.reg_id_tujuan;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ride.me.R.layout.content_chat);

        if (!getIntent().hasExtra("reg_id")) {
            finish();
        }
        regID = getIntent().getStringExtra("reg_id");

        activity = ChatActivity.this;
        image = (ImageView) findViewById(com.ride.me.R.id.imageUser);
        reviChat = (RecyclerView) findViewById(com.ride.me.R.id.reviChat);
        reviChat.setHasFixedSize(true);

        inputText = (EditText) findViewById(com.ride.me.R.id.inputText);
        butSend = (ImageView) findViewById(com.ride.me.R.id.butSend);

        que = new Queries(new DBHandler(this));

//        arrChat = que.getAllChat();
//        arrChat = initDataDummy();



        initList();

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputText.getText().toString().equals("")){
                   return;
                }
                Chat chat = new Chat();
                Date date = new Date();
                String waktu = date.getHours()+":"+date.getMinutes();
                User user = GoridemeAplication.getInstance(getApplicationContext()).getLoginUser();
                chat.isi_chat = inputText.getText().toString();
                chat.reg_id_tujuan = user.getRegId();
                chat.nama_tujuan = user.getNamaDepan() + " "+user.getNamaBelakang();
                chat.chat_from = 1;
                chat.id_tujuan = user.getId();
                chat.waktu = waktu;
                chat.reg_id_from = regID;
                sendMessageToDriver(regID, chat);
                Log.w("CHAT", "sent");
            }
        });

    }

//    private ArrayList<Chat> initDataDummy(){
//        ArrayList<Chat> arrD = new ArrayList<>();
//
//        for (int i=0; i<10; i++){
//            Chat chat = new Chat();
//            chat.id_tujuan = "id"+i;
//            chat.nama_tujuan = i+"_nama";
//            if(i % 2 == 0){
//                chat.status = 1;
//                chat.chat_from = 1;
//            }else{
//                chat.status = 0;
//                chat.chat_from = 0;
//            }
//            chat.isi_chat = "isi_chat "+i;
//            chat.reg_id_tujuan = "reg_id"+i;
//            arrD.add(chat);
//        }
//        return arrD;
//    }

    private void setmDatasetTypesFiller(){
        mDatasetTypes = new int[arrChat.size()];

        for (int i=0; i<arrChat.size(); i++){
            mDatasetTypes[i] = arrChat.get(i).chat_from;
        }
    }

    private void initList() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, int position) {
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
        updateListView();
    }

    private void updateListView(){
        ArrayList<Chat> chats = que.getAllChat();
        arrChat = new ArrayList<>();
        User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
        for(Chat chat : chats) {
            if(chat.reg_id_tujuan.equalsIgnoreCase(regID) ||
                    (chat.reg_id_tujuan.equalsIgnoreCase(userLogin.getRegId())
                            && chat.reg_id_from.equalsIgnoreCase(regID))) {
                arrChat.add(chat);
            }
        }

        setmDatasetTypesFiller();
        mLayoutManager = new LinearLayoutManager(activity);
        reviChat.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(activity, arrChat, mDatasetTypes, onItemTouchListener);
        reviChat.setAdapter(chatAdapter);
        reviChat.scrollToPosition(arrChat.size()-1);
    }


    int status=0;
    private void sendMessageToDriver(final String regIDTujuan, final Chat chat ){

        AsyncTaskHelper asyncTask = new AsyncTaskHelper(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelper.OnAsyncTaskListener() {
            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask) {
                FCMMessage fcmMessage = new FCMMessage();
                fcmMessage.setTo(regIDTujuan);
                fcmMessage.setData(chat);
                FCMHelper.sendMessage(FCM_KEY,fcmMessage).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        status = 1;
                        response.body().close();
                    }

                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                        status = 0;
                    }
                });
                Log.w("CHAT", "sent fcm");
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask) {
            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask) {
                if (status == 1){
                    Chat newChat = chat;
                    newChat.status = 1;
                    newChat.chat_from = 0;
                    que.insertChat(newChat);
                    Log.w("CHAT", "status 1");

                }else{
                    Chat newChat = chat;
                    newChat.status = 0;
                    newChat.chat_from = 0;
                    que.insertChat(newChat);
                    Log.w("CHAT", "status 0");
                }
                inputText.setText("");
                chatAdapter.notifyDataSetChanged();
                updateListView();

            }
            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask) {
            }
        });
        asyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }

    private void removeNotif(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeNotif();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}