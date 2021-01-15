package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.MainActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment.ChatFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment.Guest_Chat_MarketInfoFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment.Host_Chat_MarketInfoFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment.Nonepost_chat_MarketInfoFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.FirebaseHelper;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//채팅방안 액티비티 - 마켓의 정보(호스트용,게스트용,정보가 없을 때),채팅 기능
public class ChatActivity extends AppCompatActivity implements ChatFragment.RoomUidSetListener{

    private DrawerLayout drawerLayout;  //평소에는 화면의 한쪽에 숨겨져 있다가 사용자가 액션을 취하면 화면에 나타나는 기능을 만들 수 있게 해주는 레이아웃
    private ChatFragment chatFragment;  //실제 채팅 프레그먼트
    private Host_Chat_MarketInfoFragment hostChat_MarketInfoFragment; //내 마켓 채팅 일때의 마켓정보 프레그먼트
    private Guest_Chat_MarketInfoFragment guestChat_MarketInfoFragment; //상대 마켓 채팅 일때의 마켓정보 프레그먼트
    private Nonepost_chat_MarketInfoFragment nonepost_chat_marketInfoFragment; //포스트의 정보가 없는 마켓정보 프레그먼트
    private String currentUser_Uid =null;
    String To_User_Uid =null;  //상대방 uid
    String To_Usermodel_NickName =null;  //상대방 uid
    String ChatRoomListModel_RoomUid =null; //채팅방 uid
    String ChatRoomListModel_Title =null;  //채팅방 이름 (상대방 닉네임)
    String MarketModel_Market_Uid =null; //마켓 uid
    private FirebaseHelper Firebasehelper;          //FirebaseHelper 참조 선언
    private bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.MessageModel MessageModel;                    //UserModel 참조 선언
    public static Context mcontext;
    MarketModel marketModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        final TextView RoomTitle = findViewById(R.id.RoomTitle);
        findViewById(R.id.menu).setOnClickListener(onClickListener);

        ImageView back_Button = findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        To_Usermodel_NickName = getIntent().getStringExtra("To_Usermodel_NickName");
        Log.d("likppi","To_Usermodel_NickName : " + To_Usermodel_NickName);
        To_User_Uid = getIntent().getStringExtra("To_User_Uid");
        ChatRoomListModel_RoomUid = getIntent().getStringExtra("RoomUid"); //마켓액티비티는 없음
        ChatRoomListModel_Title = getIntent().getStringExtra("ChatRoomListModel_Title");
        MarketModel_Market_Uid = getIntent().getStringExtra("MarketModel_Market_Uid");
        currentUser_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (ChatRoomListModel_Title!=null) {
            RoomTitle.setText(ChatRoomListModel_Title);
        }else{
            RoomTitle.setText(To_Usermodel_NickName);
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Log.d("임민규","ChatRoomListModel_Title : " + ChatRoomListModel_Title);
        // 채팅 프레그먼트
        chatFragment = ChatFragment.getInstance(To_User_Uid, ChatRoomListModel_RoomUid, MarketModel_Market_Uid,currentUser_Uid);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, chatFragment )
                .commit();

        Firebasehelper = new FirebaseHelper(this);
        mcontext = this;


        //포스트 정보 프레그먼트
        DocumentReference docRef_USERS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
        docRef_USERS_HostUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                marketModel = documentSnapshot.toObject(MarketModel.class);
                //if문 : 주어진 MarketModel_Market_Uid로 찾아간  marketModel이 null이면 없는 게시물이라는 것을 알려주기 위한 Nonepost_chat_MarketInfoFragment를 띄운다.
                //else, else if문 : currentUser_Uid == PostModel_Host_Uid 일 경우 Host_Chat_PostInfoFragment를 띄우고 아니라면 guest_Chat_PostInfoFragment를 띄운다.
                if(marketModel == null){
                    nonepost_chat_marketInfoFragment = new Nonepost_chat_MarketInfoFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.postinfoFragment, nonepost_chat_marketInfoFragment).commit();
                } else if(currentUser_Uid.equals(marketModel.getMarketModel_Host_Uid())){

                    hostChat_MarketInfoFragment = new Host_Chat_MarketInfoFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.postinfoFragment, hostChat_MarketInfoFragment).commit();
                    Bundle Marketbundle = new Bundle();
                    Marketbundle.putString("MarketModel_Market_Uid", MarketModel_Market_Uid);
                    Marketbundle.putString("To_User_Uid",To_User_Uid);
                    Marketbundle.putString("currentUser_Uid",currentUser_Uid);
                    hostChat_MarketInfoFragment.setArguments(Marketbundle);

                }
                else{
                    guestChat_MarketInfoFragment = new Guest_Chat_MarketInfoFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.postinfoFragment, guestChat_MarketInfoFragment).commit();
                    Bundle Marketbundle = new Bundle();
                    Marketbundle.putString("MarketModel_Market_Uid", MarketModel_Market_Uid);
                    guestChat_MarketInfoFragment.setArguments(Marketbundle);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        chatFragment.backPressed();
        finish();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 메뉴 버튼
                case R.id.menu:
                    showPopup(v);
                    break;
            }
        }
    };

    // 채팅방 우측에 있는 점 3개의 메뉴 버튼
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(ChatActivity.this, v);

        getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.Chat_Delete_Button:

                        RoomUidSet(ChatRoomListModel_RoomUid,To_User_Uid);
                        chatFragment.User_GoOut(currentUser_Uid,MarketModel_Market_Uid,ChatRoomListModel_RoomUid);
                        Firebasehelper.ROOMS_USERS_OUT_CHECK(ChatRoomListModel_RoomUid,currentUser_Uid,To_User_Uid);
                        Intent Intent_MainActivity = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(Intent_MainActivity);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    //챗프레그먼트 연결 함수
    @Override
    public void RoomUidSet(String RoomUid,String ToUid){
        ChatRoomListModel_RoomUid = RoomUid;
        To_User_Uid = ToUid;
    }

    //챗프레그먼트 나가기 함수
    public void ChatFragment_User_GoOut(String Roomuid){
        Log.d("민규","123");
        chatFragment.User_GoOut(currentUser_Uid,MarketModel_Market_Uid,Roomuid);
    }

    public void chatActivity_finish(){
        finish();
    }

}