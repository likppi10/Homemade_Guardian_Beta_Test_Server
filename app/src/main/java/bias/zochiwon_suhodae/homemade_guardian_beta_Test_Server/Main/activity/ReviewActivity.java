package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.SendNotification;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.ReviewModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

// 리뷰를 쓰게 된다면 이동되는 액티비티 / 거래가 완료된 작성자 시점의 채팅방과 상대방의 메인 액티비티에서 이동된다.

public class ReviewActivity extends BasicActivity {                     // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                        // 2. 변수 및 배열
    private int ReviewModel_Selected_Review = 4;                            // 처음 초기 값은 4 : [0 : 친절함] [1 : 정확함] [2 : 완벽함] [3 : 불쾌함]
    private UserModel Usermodel;                                            // Usermodel 선언
    private ReviewModel Reviewmodel;                                        // Reviewmodel 선언
    private ArrayList<String> ReViewList = new ArrayList<>();               // 상대방에게 리뷰가 적힐 리뷰 리스트
                                                                        // 5. 기타 변수
    private Context context;

    public ReviewActivity(Context context) {
        this.context = context;
    }

   // 리뷰 Dialog 생성
    public void callFunction(final String To_User_Uid, final String MarketModel_Market_Uid,final UserModel currentuserModel) {

        // Dialog클래스 생성
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(R.layout.review_dialog);
        dlg.setCancelable(false);
        dlg.show();

        // 후기 적기 Button, 확인 Button, 적은 후기 TextView find
        final Button WriteReview_Button = dlg.findViewById(R.id.WriteReview_Button);
        final Button okButton = dlg.findViewById(R.id.okButton);
        final TextView Writen_Review_TextView = dlg.findViewById(R.id.Writen_Review_TextView);

       // 4가지의 리뷰 CheckBox find
        final CheckBox kind = dlg.findViewById(R.id.kind);
        final CheckBox correct = dlg.findViewById(R.id.correct);
        final CheckBox complete = dlg.findViewById(R.id.complete);
        final CheckBox bad = dlg.findViewById(R.id.bad);

       // 해당 리뷰의 게시물 정보 : 작성일, 제목, 내용
        final TextView Market_DateOfManufacture_TextView = dlg.findViewById(R.id.Post_DateOfManufacture);
        final TextView Market_Title_TextView = dlg.findViewById(R.id.Post_Title_TextView);
        final TextView Market_Contents_TextView = dlg.findViewById(R.id.Post_Contents_TextView);
        final GradientDrawable drawable= (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.image_round);

       // 해당 리뷰의 게시물 정보 받아오기
        final DocumentReference docRef_Markets_MarketUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
        docRef_Markets_MarketUid.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> markettask) {
                DocumentSnapshot documentmarketSnapshot = markettask.getResult();
                MarketModel marketModel = new MarketModel();
                marketModel = documentmarketSnapshot.toObject(MarketModel.class);

                ArrayList<String> ArrayList_ImageList = marketModel.getMarketModel_ImageList();
                if(ArrayList_ImageList != null) {
                    ImageView Post_ImageView = dlg.findViewById(R.id.Post_ImageView);
                    Post_ImageView.setVisibility(View.VISIBLE);
                    Post_ImageView.setBackground(drawable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Post_ImageView.setClipToOutline(true);
                    }
                    String Image = ArrayList_ImageList.get(0);
                    Glide.with(context).load(Image).centerCrop().override(500).thumbnail(0.1f).into(Post_ImageView);
                }else {
                    ImageView Thumbnail_ImageView = dlg.findViewById(R.id.Post_ImageView);
                    Thumbnail_ImageView.setVisibility(View.GONE);
                }
                Market_Title_TextView.setText(marketModel.getMarketModel_Title());
                Market_Contents_TextView.setText(marketModel.getMarketModel_Text());
                Market_DateOfManufacture_TextView.setText(new SimpleDateFormat("MM/dd hh:mm", Locale.getDefault()).format(marketModel.getMarketModel_DateOfManufacture()));
            }
        });

       // 4가지의 리뷰 setOnClickListener : 클릭시 다른 것들은 체크해제
        kind.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind.setChecked(true);
                correct.setChecked(false);
                complete.setChecked(false);
                bad.setChecked(false);
                ReviewModel_Selected_Review = 0;
            }
        }) ;
        correct.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind.setChecked(false);
                correct.setChecked(true);
                complete.setChecked(false);
                bad.setChecked(false);
                ReviewModel_Selected_Review = 1;
            }
        }) ;
        complete.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind.setChecked(false);
                correct.setChecked(false);
                complete.setChecked(true);
                bad.setChecked(false);
                ReviewModel_Selected_Review = 2;
            }
        }) ;
        bad.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                kind.setChecked(false);
                correct.setChecked(false);
                complete.setChecked(false);
                bad.setChecked(true);
                ReviewModel_Selected_Review = 3;
                WriteReviewActivity writeReviewActivity = new WriteReviewActivity(context,Writen_Review_TextView);
                writeReviewActivity.callFunction(Writen_Review_TextView);
            }
        }) ;

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //최소글자 만들기!!
                final String CurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(ReviewModel_Selected_Review!=4){
                    String ReviewModel_Uid = null;
                    ReviewModel_Uid = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid).collection("REVIEW").document().getId();
                    //ReviewModel = new ReviewModel(ReviewModel_Uid, MarketModel_Market_Uid,  To_User_Uid,닉네임 , 프로필이미지, Writen_Review_TextView.getText().toString(), ReviewModel_Selected_Review, DateOfManufacture);
                    final DocumentReference docRef_Users_ReviewUid = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
                    final String Reviewmodel_Uid = ReviewModel_Uid;
                    docRef_Users_ReviewUid.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            final Date DateOfManufacture = new Date();
                           // 리뷰에 넣을 현재 사용자의 정보를 usermodel에 저장
                            final DocumentReference docRef_Users_ReviewWriterUid = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                            docRef_Users_ReviewWriterUid.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> writetask) {
                                    DocumentSnapshot documentwriteSnapshot = writetask.getResult();
                                    UserModel usermodel = new UserModel();
                                    usermodel = documentwriteSnapshot.toObject(UserModel.class);
                                   // 리뷰모델을 생성해서 상대방에 저장
                                    Reviewmodel = new ReviewModel(Reviewmodel_Uid, MarketModel_Market_Uid,  To_User_Uid,usermodel.getUserModel_NickName() , usermodel.getUserModel_ProfileImage(), Writen_Review_TextView.getText().toString(), ReviewModel_Selected_Review, DateOfManufacture);
                                    WriteBatch Batch_REVIEW_ReviewUid = FirebaseFirestore.getInstance().batch();
                                    Batch_REVIEW_ReviewUid.set(docRef_Users_ReviewUid.collection("REVIEW").document(Reviewmodel_Uid), Reviewmodel.getReviewModel());
                                    Batch_REVIEW_ReviewUid.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           // 현재 사용자의 UnReViewUserList, UnReViewPostList의 펏번째 값을 제거
                                            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            if (document.exists()) {
                                                                Usermodel = document.toObject(UserModel.class);
                                                                ArrayList<HashMap<String, String>> UserModel_Unreview = Usermodel.getUserModel_Unreview();

                                                                if(UserModel_Unreview.size()>1){
                                                                    Log.d("test","들어왔음");
                                                                    UserModel_Unreview.remove(1);
                                                                    Usermodel.setUserModel_Unreview(UserModel_Unreview);

                                                                    final DocumentReference documentReferencesetCurrentUser = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                                                                    documentReferencesetCurrentUser.set(Usermodel.getUserInfo())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {Log.d("test","들어왔음2");}
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {}
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                   // [case = 0 : 친절함] [case = 1 : 정확함] [case = 2 : 완벽함] [case = 3 : 불쾌함]
                   // 상대방의 Uid에 해당하는 리뷰 필드에 나의 리뷰를 저장한다.
                    docRef_Users_ReviewUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Usermodel = documentSnapshot.toObject(UserModel.class);
                            final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
                            switch (ReviewModel_Selected_Review){
                                case 0 :
                                    ReViewList = Usermodel.getUserModel_kindReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    Usermodel.setUserModel_kindReviewList(ReViewList);
                                    documentReferencesetToUser.set(Usermodel.getUserInfo())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {}
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {}
                                            });
                                    break;
                                case 1 :
                                    ReViewList = Usermodel.getUserModel_correctReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    Usermodel.setUserModel_kindReviewList(ReViewList);
                                    documentReferencesetToUser.set(Usermodel.getUserInfo())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {}
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {}
                                            });
                                    break;
                                case 2 :
                                    ReViewList = Usermodel.getUserModel_completeReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    Usermodel.setUserModel_kindReviewList(ReViewList);
                                    documentReferencesetToUser.set(Usermodel.getUserInfo()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {}
                                            });
                                    break;
                                case 3 :
                                    ReViewList = Usermodel.getUserModel_badReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    Usermodel.setUserModel_kindReviewList(ReViewList);
                                    documentReferencesetToUser.set(Usermodel.getUserInfo())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {}
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {}
                                            });
                                    break;
                            }
                        }
                    });

                   // 본인이 적은 리뷰 상대의 Uid가 나에게 저장된다.
                    final DocumentReference docRef_Review_WritenToUid = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                    docRef_Review_WritenToUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Usermodel = documentSnapshot.toObject(UserModel.class);
                            ArrayList<String> UserModel_WritenReviewList = new ArrayList<>();
                            UserModel_WritenReviewList = Usermodel.getUserModel_WritenReviewList();
                            UserModel_WritenReviewList.add(To_User_Uid);
                            docRef_Review_WritenToUid.set(Usermodel.getUserInfo())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {}
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {}
                                    });
                        }
                    });
                    SendAlarm(currentuserModel.getUserModel_Uid(),To_User_Uid);
                   // 리뷰창 사라짐
                    dlg.dismiss();

                }else {
                    Toast.makeText(context, "리뷰를 작성해 주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

       // 후기 작성 Button setOnClickListener : 후기를 작성하는 액티비티로 이동한다.
        WriteReview_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteReviewActivity writeReviewActivity = new WriteReviewActivity(context,Writen_Review_TextView);
                writeReviewActivity.callFunction(Writen_Review_TextView);
            }
        });

    }

    private void SendAlarm(String CurrentUser_Uid, final String ToUser_Uid) {
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
                                                SendNotification.sendNotification(ToHostuserModel.getUserModel_Token(), userModel.getUserModel_NickName(), "완료된 거래의 리뷰가 달렸습니다! ");
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