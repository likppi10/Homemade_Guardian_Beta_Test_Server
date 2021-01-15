package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.ReviewActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity.ChatActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.SendNotification;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

//호스트용 포스트 정보 프레그먼트
public class Host_Chat_MarketInfoFragment extends Fragment {

    String MarketModel_Market_Uid;
    String To_User_Uid;
    String currentUser_Uid;
    MarketModel marketModel;
    UserModel userModel;
    TextView Chat_MarketInfo_Title;
    TextView Chat_MarketInfo_Text;
    ImageView Chat_MarketInfo_Image;
    CardView Chat_PostInfo_Card;

    Switch Chat_MarketInfo_reservation;                       //예약 버튼
    Switch Chat_MarketInfo_deal;                              //거래완료 버튼

    ChatActivity chatActivity;

    ArrayList<String> Market_reservationList = new ArrayList<>();
    ArrayList<String> Market_dealList = new ArrayList<>();

    int reservationsetting = 0;
    int dealsetting  = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        chatActivity = (ChatActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        chatActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        final ViewGroup View = (ViewGroup) inflater.inflate(R.layout.fragment_host_chat_marketinfo, container, false);
        Chat_MarketInfo_Title = (TextView)View.findViewById(R.id.Chat_PostInfo_Title);
        Chat_MarketInfo_Text = (TextView)View.findViewById(R.id.Chat_PostInfo_Text);
        Chat_MarketInfo_Image = (ImageView) View.findViewById(R.id.Chat_PostInfo_Image);
        Chat_MarketInfo_reservation = (Switch) View.findViewById(R.id.Chat_PostInfo_reservation);
        Chat_MarketInfo_deal = (Switch) View.findViewById(R.id.Chat_PostInfo_deal);
        Chat_PostInfo_Card = View.findViewById(R.id.Chat_PostInfo_Card);
        Chat_MarketInfo_deal.setEnabled(false);
        Bundle Marketbundle = getArguments();
        MarketModel_Market_Uid = Marketbundle.getString("MarketModel_Market_Uid");
        To_User_Uid = Marketbundle.getString("To_User_Uid");
        currentUser_Uid = Marketbundle.getString("currentUser_Uid");


        Setting_Post_Info();

        return View;
    }

    //기본 세팅
    void Setting_Post_Info(){
        reservationsetting=0;
        dealsetting=0;
        DocumentReference docRef_MARKETS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
        docRef_MARKETS_HostUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                marketModel = documentSnapshot.toObject(MarketModel.class);
                Chat_MarketInfo_Title.setText(marketModel.getMarketModel_Title());
                Chat_MarketInfo_Text.setText(marketModel.getMarketModel_Text());
                //post의 이미지 섬네일 띄우기
                if(marketModel.getMarketModel_ImageList() != null){
                    Chat_PostInfo_Card.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(marketModel.getMarketModel_ImageList().get(0)).centerCrop().override(500).into(Chat_MarketInfo_Image);
                }
                if(!marketModel.getMarketModel_reservation().equals("X")){
                    Chat_MarketInfo_reservation.setChecked(true);
                    Chat_MarketInfo_deal.setEnabled(true);
                    reservationsetting =1;
                }

                if(!marketModel.getMarketModel_deal().equals("X")){
                    Chat_MarketInfo_reservation.setChecked(true);
                    Chat_MarketInfo_deal.setChecked(true);
                    dealsetting = 1;
                    Chat_MarketInfo_reservation.setEnabled(false);
                    Chat_MarketInfo_deal.setEnabled(false);
                }
                Switch_reservatrion();
                Switch_deal();
            }
        });
    }

    //예약 버튼
    //마켓의 reservation X -> 상대방의 Uid로 바꾸기
    void Switch_reservatrion(){
        Chat_MarketInfo_reservation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && reservationsetting !=1){
                    DocumentReference docRef_MARKETS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
                    docRef_MARKETS_HostUid
                            .update("MarketModel_reservation", To_User_Uid)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Chat_MarketInfo_deal.setEnabled(true);
                                    User_ReservationList(currentUser_Uid,"add");
                                    User_ReservationList(To_User_Uid,"add");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }else{
                    DocumentReference docRef_MARKETS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
                    docRef_MARKETS_HostUid
                            .update("MarketModel_reservation", "X")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Chat_MarketInfo_deal.setEnabled(false);
                                    User_ReservationList(currentUser_Uid,"remove");
                                    User_ReservationList(To_User_Uid,"remove");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }
            }
        });
    }

    //딜 버튼
    void Switch_deal(){
        Chat_MarketInfo_deal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked&&dealsetting !=1){
                    //마켓에 있는 deal X -> 상대방의 Uid로 바꾸기
                    DocumentReference docRef_MARKETS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
                    docRef_MARKETS_HostUid
                            .update("MarketModel_deal", To_User_Uid)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    User_Deal(currentUser_Uid);
                                    User_Deal(To_User_Uid);
                                    To_User_Uid_Review(To_User_Uid);
                                    Review_Appear(currentUser_Uid,To_User_Uid,MarketModel_Market_Uid);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
        });
    }

    //예약리스트에 유저정보에 포스트정보를 추가, 삭제한다.
    void User_ReservationList(final String UserUid, final String info){
        //currentuseruid에 예약한 포스트의 uid 삽입
        final DocumentReference documentReferenceMyUser = FirebaseFirestore.getInstance().collection("USERS").document(UserUid);
        documentReferenceMyUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Market_reservationList = userModel.getUserModel_Market_reservationList();
                if(info.equals("add")){
                    Market_reservationList.add(MarketModel_Market_Uid);
                }
                else{
                    Market_reservationList.remove(MarketModel_Market_Uid);
                }
                userModel.setUserModel_Market_reservationList(Market_reservationList);
                final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(UserUid);
                documentReferencesetToUser.set(userModel.getUserInfo())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }

    //상대방 언 리뷰 정보에 기입한다.
    void To_User_Uid_Review(final String To_User_Uid){
        final DocumentReference documentReferencegetToUser = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
        documentReferencegetToUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                ArrayList<HashMap<String, String>> UserModel_Unreview = userModel.getUserModel_Unreview();
                HashMap<String, String> Unreview = new HashMap<String, String>();
                Unreview.put("UserModel_UnReViewMarketList", MarketModel_Market_Uid);
                Unreview.put("UserModel_UnReViewUserList", currentUser_Uid);
                UserModel_Unreview.add(Unreview);
                userModel.setUserModel_Unreview(UserModel_Unreview);

                final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
                documentReferencesetToUser.set(userModel.getUserInfo())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }

    //딜리스트에 유저정보에 포스트정보를 추가, 삭제한다.
    void User_Deal(final String User_Uid){
        //currentuseruid에 거래 완료된 포스틔의 uid 삽입
        final DocumentReference documentReferenceMyUser = FirebaseFirestore.getInstance().collection("USERS").document(User_Uid);
        documentReferenceMyUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Market_dealList = userModel.getUserModel_Market_dealList();
                Market_reservationList = userModel.getUserModel_Market_reservationList();
                Market_dealList.add(MarketModel_Market_Uid);
                Market_reservationList.remove(MarketModel_Market_Uid);
                userModel.setUserModel_Market_dealList(Market_dealList);
                userModel.setUserModel_Market_reservationList(Market_reservationList);
                final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(User_Uid);
                documentReferencesetToUser.set(userModel.getUserInfo())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SendAlarm(currentUser_Uid,To_User_Uid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }

    //리뷰창을 띄워준다.
    void Review_Appear(String currentUser_Uid, final String To_User_Uid, final String MarketModel_Market_Uid){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(currentUser_Uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //데이터의 존재여부
                            UserModel userModel = document.toObject(UserModel.class);
                            ReviewActivity reviewActivity = new ReviewActivity(getContext());
                            reviewActivity.callFunction(To_User_Uid, MarketModel_Market_Uid, userModel);
                        }
                    }
                }
            }
        });
    }

    void SendAlarm(String CurrentUser_Uid, final String ToUser_Uid){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUser_Uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //데이터의 존재여부
                            final UserModel userModel = document.toObject(UserModel.class);
                            // 리뷰 작성자가 게시물 작성자가 아닐 때
                            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(ToUser_Uid);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {  //데이터의 존재여부
                                                UserModel ToHostuserModel = document.toObject(UserModel.class);
                                                SendNotification.sendNotification(ToHostuserModel.getUserModel_Token(), userModel.getUserModel_NickName(), "완료된 거래의 리뷰를 작성해주세요! ");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}