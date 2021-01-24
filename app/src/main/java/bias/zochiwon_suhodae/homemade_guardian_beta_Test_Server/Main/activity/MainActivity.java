package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar.ChatroomList_BottombarFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar.Community_BottombarFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar.Market_BottombarFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar.MyInfo_BottombarFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar.WriteMarket_BottombarFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Logout_Dialog;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity.ChatActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity.CommunityActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity.MarketActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.RoomModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 어플리케이션이 시작되면 처음 들르는 액티비티로 유저의 로그인 상태 정보를 판별하며, 하단 네비게이션의 각 속성들을 선언, 정의 해주는 액티비티

public class MainActivity extends AppCompatActivity implements Serializable {       // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
    // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private RelativeLayout Writen_ButtonsBackground_Layout;                             // 게시물 쓰기 바텀 네비게이션 (3번째 프레그먼트)버튼 누를시
    // 5. 기타 변수
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        // 게시물 작성 Latout, Market 작성 Button, Community 작성 Button find
        Button Market_Write_Button = findViewById(R.id.Market_Write_Button);
        Button Community_Write_Button = findViewById(R.id.Community_Write_Button);
        Writen_ButtonsBackground_Layout = findViewById(R.id.Writen_ButtonsBackground_Layout);

        // 게시물 작성 Latout, Market 작성 Button, Community 작성 Button setOnClickListener
        Writen_ButtonsBackground_Layout.setOnClickListener(onClickListener);
        Market_Write_Button.setOnClickListener(onClickListener);
        Community_Write_Button.setOnClickListener(onClickListener);

        // 로그인 상태, 회원정보 등록 상태, 리뷰 미작성 여부 상태를 결정하는 함수
        Check_User_State();

        // 하단 네비겟이션 구성 함수
        Bottomnavigate();
    }

    @Override protected void onResume() { super.onResume(); }

    @Override protected void onPause(){ super.onPause(); }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                Check_User_State();
                Bottomnavigate();
                break;
            case 1:
                WriteMarket_BottombarFragment writeMarketBottombarFragment = new WriteMarket_BottombarFragment();
                Writen_ButtonsBackground_Layout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, writeMarketBottombarFragment).commit();
                break;
        }
    }

    // 로그인 상태, 회원정보 등록 상태, 리뷰 미작성 여부 상태를 결정하는 함수
    private void Check_User_State() {

        // 현재 유저에 대한 파이어베이스 유저 정보
        final FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // if : 현재 파이어베이스 유저 정보가 없다면
        // else : 현재 파이어베이스 유저 정보가 있음
        if (CurrentUser == null) {
            myStartActivity(LoginActivity.class);
        } else {

            // CurruntUser에서 CurruntUser_Uid를 가져와 documentReference
            final String CurruntUser_Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurruntUser_Uid);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            // if : 파이어베이스에 CurruntUser_Uid를 Uid로 가지는 document가 존재한다면 --> 작성하지 않은 리뷰를 작성하게 함
                            // else : CurruntUser는 있으나 CurruntUser_Uid를 Uid로 가지는 document가 존재 X
                            if (document.exists()) {
                                String Popup_Uid = null;
                                String messageBody = null;
                                Popup_Uid = getIntent().getStringExtra("Popup_Uid");
                                messageBody = getIntent().getStringExtra("messageBody");

                                if(messageBody != null){
                                    if(messageBody.equals("물물교환 게시물의 댓글이 달렸습니다!")){
                                        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("MARKETS").document(Popup_Uid);
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null) {
                                                        if (document.exists()) {  //데이터의 존재여부
                                                            final MarketModel Marketmodel = document.toObject(MarketModel.class);
                                                            MarketModel intentmarketmodel = new MarketModel(Marketmodel.getMarketModel_Title(),Marketmodel.getMarketModel_Text(),Marketmodel.getMarketModel_ImageList(),Marketmodel.getMarketModel_DateOfManufacture(),Marketmodel.getMarketModel_Host_Uid(),Marketmodel.getMarketModel_Market_Uid(),Marketmodel.getMarketModel_Category(),Marketmodel.getMarketModel_LikeList(),Marketmodel.getMarketModel_HotMarket(),Marketmodel.getMarketModel_reservation(),Marketmodel.getMarketModel_deal(),Marketmodel.getMarketModel_CommentCount());
                                                            Intent intent = new Intent(getApplicationContext(), MarketActivity.class);
                                                            intent.putExtra("marketInfo", intentmarketmodel);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }else if(messageBody.equals("자유게시판의 댓글이 달렸습니다!")) {
                                        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("COMMUNITY").document(Popup_Uid);
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document != null) {
                                                        if (document.exists()) {  //데이터의 존재여부
                                                            final CommunityModel communityModel = document.toObject(CommunityModel.class);
                                                            CommunityModel intentcommunitymodel = new CommunityModel(communityModel.getCommunityModel_Title(),communityModel.getCommunityModel_Text(),communityModel.getCommunityModel_ImageList(),communityModel.getCommunityModel_DateOfManufacture(),communityModel.getCommunityModel_Host_Uid(),communityModel.getCommunityModel_Community_Uid(),communityModel.getCommunityModel_LikeList(),communityModel.getCommunityModel_HotCommunity(),communityModel.getCommunityModel_CommentCount());
                                                            Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                                                            intent.putExtra("communityInfo", intentcommunitymodel);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }else if(messageBody.equals("메세지가 도착했습니다!")){
                                        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("ROOMS").document(Popup_Uid);
                                        final String finalPopup_Uid = Popup_Uid;
                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    final Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                                    if (document != null) {
                                                        if (document.exists()) {  //데이터의 존재여부
                                                            final RoomModel roomModel = document.toObject(RoomModel.class);
                                                            String intent_ChatRoomListModel_RoomUid = finalPopup_Uid;
                                                            String intent_MarketModel_Market_Uid = roomModel.getRoomModel_PostUid();
                                                            String intent_To_User_Uid = null;
                                                            Map<String, Integer> users = roomModel.getRoomModel_USERS();
                                                            for( String key : users.keySet() ){
                                                                if (!CurruntUser_Uid.equals(key)) {
                                                                    intent_To_User_Uid = users.toString();
                                                                }
                                                            }

                                                            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(intent_To_User_Uid);
                                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document != null) {
                                                                            if (document.exists()) {  //데이터의 존재여부
                                                                                final UserModel userModel = document.toObject(UserModel.class);
                                                                                intent.putExtra("ChatRoomListModel_Title", userModel.getUserModel_NickName());
                                                                                Log.d("임민규","userModel.getUserModel_NickName() : " + userModel.getUserModel_NickName());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                            intent.putExtra("To_User_Uid", intent_To_User_Uid);
                                                            intent.putExtra("RoomUid", intent_ChatRoomListModel_RoomUid);
                                                            //intent.putExtra("ChatRoomListModel_Title", intent_To_User_Uid);
                                                            intent.putExtra("MarketModel_Market_Uid", intent_MarketModel_Market_Uid);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    UserModel userModel = document.toObject(UserModel.class);
                                    ArrayList<HashMap<String, String>> UserModel_Unreview = userModel.getUserModel_Unreview();

                                    if(UserModel_Unreview.size() > 1){
                                        HashMap<String, String> Unreview = (HashMap<String,String>)UserModel_Unreview.get(1);
                                        ReviewActivity reviewActivity = new ReviewActivity(MainActivity.this);
                                        reviewActivity.callFunction(Unreview.get("UserModel_UnReViewUserList") ,Unreview.get("UserModel_UnReViewMarketList"), userModel);
                                    }
                                }

                            } else {
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    }
                }
            });
        }
    }

    // 하단 네비겟이션 구성 함수
    private void Bottomnavigate() {

        // container의 첫 화면은 마켓프레그먼트로 초기화
        Market_BottombarFragment marketBottombarFragment = new Market_BottombarFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, marketBottombarFragment).commit();

        // container의 바텀 메뉴를 누르면 어떤 프레그먼트가 연결되는지 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    // 3번째 프레그먼트를 제외한 어떠한 프레그먼트가 클릭되면, Writen_ButtonsBackground_Layout를 사라지게 함
                    case R.id.Market:
                        Market_BottombarFragment marketBottombarFragment = new Market_BottombarFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, marketBottombarFragment).commit();
                        Writen_ButtonsBackground_Layout.setVisibility(View.GONE);
                        return true;
                    case R.id.Community:
                        Community_BottombarFragment communityBottombarFragment = new Community_BottombarFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, communityBottombarFragment).commit();
                        Writen_ButtonsBackground_Layout.setVisibility(View.GONE);
                        return true;
                    case R.id.Writepost:
                        WriteMarket_BottombarFragment writeMarketBottombarFragment = new WriteMarket_BottombarFragment();
                        Writen_ButtonsBackground_Layout.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, writeMarketBottombarFragment).commit();
                        return true;
                    case R.id.Chatroomlist:
                        ChatroomList_BottombarFragment chatroomFragment = new ChatroomList_BottombarFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, chatroomFragment).commit();
                        Writen_ButtonsBackground_Layout.setVisibility(View.GONE);
                        return true;
                    case R.id.Myinfo:
                        MyInfo_BottombarFragment myinfoBottombarFragment = new MyInfo_BottombarFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, myinfoBottombarFragment).commit();
                        Writen_ButtonsBackground_Layout.setVisibility(View.GONE);
                        return true;
                }
                return false;
            }
        });
    }

    // Market 작성 button, Community 작성 button의 onClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Market_Write_Button:
                    myStartActivity(WriteMarketActivity.class);
                    break;
                case R.id.Community_Write_Button:
                    myStartActivity(WriteCommunityActivity.class);
                    break;
            }
        }
    };

    // MyInfoFragment에서 쓰는 함수인데, 로그아웃시 확인을 요구하는 Dialog 생성
    public void Logout_dialog(){
        Logout_Dialog Logout_Dialog = new Logout_Dialog(this);
        Logout_Dialog.callFunction();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }

    public void myStartFinishActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
        finish();
    }
}