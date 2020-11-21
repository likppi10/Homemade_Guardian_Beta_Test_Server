package com.example.homemade_guardian_beta.Main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.homemade_guardian_beta.R;
import com.example.homemade_guardian_beta.model.market.MarketModel;
import com.example.homemade_guardian_beta.model.user.ReviewModel;
import com.example.homemade_guardian_beta.model.user.UserModel;
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
import java.util.Locale;

/**
 * Created by Administrator on 2017-08-07.
 */

public class ReviewActivity extends BasicActivity {

    private Context context;
    private FirebaseFirestore Firestore =null;
    private ReviewModel ReviewModel;
    private int ReviewModel_Selected_Review=4;
    String CurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private UserModel currentuserModel,userModel;
    ArrayList<String> UnReViewUserList = new ArrayList<>();
    ArrayList<String> UnReViewPostList = new ArrayList<>();
    ArrayList<String> ReViewList = new ArrayList<>();
    ArrayList<String> UserModel_WritenReviewList = new ArrayList<>();


    public ReviewActivity(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final String To_User_Uid, final String MarketModel_Market_Uid,final UserModel currentuserModel) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.review_dialog);
        dlg.setCancelable(false);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button WriteReview_Button = (Button) dlg.findViewById(R.id.WriteReview_Button);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final TextView Writen_Review_TextView = (TextView) dlg.findViewById(R.id.Writen_Review_TextView);

        final CheckBox kind = dlg.findViewById(R.id.kind);
        final CheckBox correct = dlg.findViewById(R.id.correct);
        final CheckBox complete = dlg.findViewById(R.id.complete);
        final CheckBox bad = dlg.findViewById(R.id.bad);

        final TextView Post_DateOfManufacture = dlg.findViewById(R.id.Post_DateOfManufacture);
        final TextView Post_Title_TextView = dlg.findViewById(R.id.Post_Title_TextView);
        final TextView Post_Contents_TextView = dlg.findViewById(R.id.Post_Contents_TextView);
        final GradientDrawable drawable= (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.image_round);

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
                Post_Title_TextView.setText(marketModel.getMarketModel_Title());
                Post_Contents_TextView.setText(marketModel.getMarketModel_Text());
                Post_DateOfManufacture.setText(new SimpleDateFormat("MM/dd hh:mm", Locale.getDefault()).format(marketModel.getMarketModel_DateOfManufacture()));
            }
        });


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
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                //main_label.setText(Writen_Review_TextView.getText().toString());
                // 커스텀 다이얼로그를 종료한다.



                //최소글자 만들기!!
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
                            final DocumentReference docRef_Users_ReviewWriterUid = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                            docRef_Users_ReviewWriterUid.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> writetask) {
                                    DocumentSnapshot documentwriteSnapshot = writetask.getResult();
                                    UserModel usermodel = new UserModel();
                                    usermodel = documentwriteSnapshot.toObject(UserModel.class);
                                    ReviewModel = new ReviewModel(Reviewmodel_Uid, MarketModel_Market_Uid,  To_User_Uid,usermodel.getUserModel_NickName() , usermodel.getUserModel_ProfileImage(), Writen_Review_TextView.getText().toString(), ReviewModel_Selected_Review, DateOfManufacture);
                                    WriteBatch Batch_REVIEW_ReviewUid = FirebaseFirestore.getInstance().batch();
                                    Batch_REVIEW_ReviewUid.set(docRef_Users_ReviewUid.collection("REVIEW").document(Reviewmodel_Uid), ReviewModel.getReviewModel());
                                    Batch_REVIEW_ReviewUid.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            if (document.exists()) {
                                                                userModel = document.toObject(UserModel.class);
                                                                UnReViewUserList = userModel.getUserModel_UnReViewUserList();
                                                                UnReViewPostList = userModel.getUserModel_UnReViewPostList();
                                                                if(UnReViewUserList.size()>0){
                                                                    UnReViewUserList.remove(0);
                                                                    UnReViewPostList.remove(0);
                                                                    userModel.setUserModel_UnReViewUserList(UnReViewUserList);
                                                                    userModel.setUserModel_UnReViewPostList(UnReViewPostList);
                                                                    final DocumentReference documentReferencesetCurrentUser = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                                                                    documentReferencesetCurrentUser.set(userModel.getUserInfo())
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
                                                            } else {
                                                                Intent intent = new Intent(getApplicationContext(), MemberInitActivity.class);
                                                                startActivityForResult(intent, 1);
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

                    docRef_Users_ReviewUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userModel = documentSnapshot.toObject(UserModel.class);
                            final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
                            switch (ReviewModel_Selected_Review){
                                case 0 :
                                    ReViewList = userModel.getUserModel_kindReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    userModel.setUserModel_kindReviewList(ReViewList);

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
                                    break;
                                case 1 :
                                    ReViewList = userModel.getUserModel_correctReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    userModel.setUserModel_kindReviewList(ReViewList);

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
                                    break;
                                case 2 :
                                    ReViewList = userModel.getUserModel_completeReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    userModel.setUserModel_kindReviewList(ReViewList);

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
                                    break;
                                case 3 :
                                    ReViewList = userModel.getUserModel_badReviewList();
                                    ReViewList.add(Reviewmodel_Uid);
                                    userModel.setUserModel_kindReviewList(ReViewList);

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
                                    break;

                            }


                        }
                    });

                    //내꺼에 내가 누구한테 리뷰를 적었는지 상대의 uid가 적힌다.
                    final DocumentReference docRef_Review_WritenToUid = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUserUid);
                    docRef_Review_WritenToUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userModel = documentSnapshot.toObject(UserModel.class);
                            UserModel_WritenReviewList = userModel.getUserModel_WritenReviewList();
                            UserModel_WritenReviewList.add(To_User_Uid);
                            Log.d("test1","ReviewActivity : "+userModel);
                            Log.d("test1","ReviewActivity : "+userModel.getUserModel_NickName());
                            docRef_Review_WritenToUid.set(userModel.getUserInfo())
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
                    dlg.dismiss();
                    ((InitActivity)InitActivity.mContext).myStartFinishActivity(MainActivity.class, currentuserModel);
                }else {
                    Toast.makeText(context, "리뷰를 작성해 주세요!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        WriteReview_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent Intent_WriteReview_Activity = new Intent(context, WriteReviewActivity.class);
//                context.startActivity(Intent_WriteReview_Activity);
                WriteReviewActivity writeReviewActivity = new WriteReviewActivity(context,Writen_Review_TextView);
                writeReviewActivity.callFunction(Writen_Review_TextView);
                //dlg.dismiss();
            }
        });
    }

}