package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity.GalleryActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util;

// MyInfoFragment에서 회원의 정보를 바꾸는 액티비티

public class UpdateInfoActivity extends BasicActivity {     // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
    // 2. 변수 및 배열
    private UserModel Usermodel = new UserModel();              // Usermodel 선언
    private int UserModel_University;                           // 대학교 지정 변수 (0 = 홍대, 1 = 고대)
    private String BirthDay;                                    // 생일
    private String SelectedImagePath;                           // 프로필 이미지
    // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private RelativeLayout LoaderLayout;                        // 로딩중을 나타내는 layout 선언
    private ImageView Myinfo_Profile_ImageView;                 // 프로필 이미지가 담기는 ImageView
    private EditText Myinfo_Profile_NickName_EditText;          // 닉네임이 담기는 EditTextView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

        // 프로필 사진 layput, 뒤로가기 Button, 정보 수정 Button, 진행중 레이아웃, 프로필 닉네임, 프로필 ImageView find
        ConstraintLayout Myinfo_profileImage_layout = findViewById(R.id.Myinfo_profileImage_layout);
        ImageView back_Button = findViewById(R.id.back_Button);
        Button UpdateInfo_Button = findViewById(R.id.UpdateInfo_Button);
        LoaderLayout = findViewById(R.id.Loader_Lyaout);
        Myinfo_Profile_NickName_EditText = findViewById(R.id.Myinfo_profileNickName);
        Myinfo_Profile_ImageView = findViewById(R.id.Myinfo_profileImage);

        // 프로필 사진 layput, 뒤로가기 Button, 정보 수정 Button setOnClickListener
        Myinfo_profileImage_layout.setOnClickListener(onClickListener);
        UpdateInfo_Button.setOnClickListener(onClickListener);
        back_Button.setOnClickListener(onClickListener);

        // 현재 유저에 대한 정보 getIntent
        Usermodel = (UserModel) getIntent().getSerializableExtra("userModel");

        // 받아온 현재 유저 정보를 보여주는 함수
        ShowInfo();
        Log.d("울라랄라아","Usermodel이미지"+ Usermodel.getUserModel_ProfileImage());
    }

    // 정보 수정 Button, 프로필 ImageView, 뒤로가기 Button의 OnClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.UpdateInfo_Button:
                    MemberInit_Storage_Uploader();
                    break;
                case R.id.Myinfo_profileImage_layout:
                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.back_Button:
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                // GalleryActivity에서 이미지를 골랐다면 RESULT_OK / 고르지 않았다면 RESULT_CANCELED 실행
                if (resultCode == Activity.RESULT_OK) {
                    SelectedImagePath = data.getStringExtra(Util.INTENT_PATH);
                    if(SelectedImagePath != null){
                        Glide.with(this).load(SelectedImagePath).centerCrop().override(500).into(Myinfo_Profile_ImageView);
                    } else{
                        if(Usermodel.getUserModel_ProfileImage() ==null){
                            Glide.with(this).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_Profile_ImageView);
                        }
                        else{
                            Glide.with(this).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_Profile_ImageView);
                        }
                    }
                }
                if(resultCode == Activity.RESULT_CANCELED){
                    SelectedImagePath = null;
                    if(Usermodel.getUserModel_ProfileImage() ==null){
                        Glide.with(this).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_Profile_ImageView);
                    }
                    else{
                        if(Usermodel.getUserModel_ProfileImage() ==null){
                            Glide.with(this).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_Profile_ImageView);
                        }
                        else{
                            Glide.with(this).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_Profile_ImageView);
                        }
                    }
                }
                break;
            }
        }
    }

    // 현재 유저 정보를 보여주는 함수
    public void ShowInfo(){
        TextView Myinfo_profileID, Myinfo_profileManufacture;
        // 사용자 닉네임 set
        Myinfo_Profile_NickName_EditText.setText(Usermodel.getUserModel_NickName());

        // 사용자 생일 set
        DatePicker BirthDay_Picker = findViewById(R.id.BirthDay_Picker);
        BirthDay = Usermodel.getUserModel_BirthDay();
        if(BirthDay != null){
            String[] array = BirthDay.split("/");
            BirthDay_Picker.init( Integer.parseInt(array[0]),  Integer.parseInt(array[1])-1, Integer.parseInt(array[2]), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    BirthDay = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                }
            });
        }else {
            BirthDay_Picker.init(2020, 0, 1, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    BirthDay = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                }
            });
        }

        //사용자 학교 set : listener가 있어서 spinner가 가르키는 값으로 순간순간 바뀜
        Spinner Myinfo_profileUniversity = findViewById(R.id.Myinfo_profileUniversity);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.University, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Myinfo_profileUniversity.setAdapter(monthAdapter);
        Myinfo_profileUniversity.setSelection(Usermodel.getUserModel_University());
        Myinfo_profileUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserModel_University = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 사용자의 이미지 set
        if(Usermodel.getUserModel_ProfileImage() != null){
            Log.d("울라랄라아","민규1");
            Glide.with(this).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_Profile_ImageView);
        }else{
            if(Usermodel.getUserModel_ProfileImage() ==null){
                Log.d("울라랄라아","민규2");
                Glide.with(this).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_Profile_ImageView);
            }
            else{
                Log.d("울라랄라아","민규3");
                Glide.with(this).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_Profile_ImageView);
            }
        }

        Myinfo_profileID = findViewById(R.id.Myinfo_profileID);
        Myinfo_profileID.setText(Usermodel.getUserModel_ID());
        Myinfo_profileManufacture = findViewById(R.id.Myinfo_profileManufacture);
        Myinfo_profileManufacture.setText(new SimpleDateFormat("yyyy-MM-dd / hh:mm:ss", Locale.getDefault()).format(Usermodel.getUserModel_DateOfManufacture()));


    }

    // 변경사항을 파이어스토리지에 등록하는 함수
    private void MemberInit_Storage_Uploader() {

        // 입력된 닉네임을 get
        final String UserModel_Nickname = Myinfo_Profile_NickName_EditText.getText().toString();

        // if : 닉네임은 20자 이하
        if (UserModel_Nickname.length() < 20) {

            // LoaderLayout으로 파이어베이스에 접근 할 때에는 다른 행동을 방지한다.
            LoaderLayout.setVisibility(View.VISIBLE);

            // 파이어베이스 스토리지에 대한 Reference와 저장 졍로를 받아온다.
            FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();
            StorageReference Storagereference = Firebasestorage.getReference();
            final StorageReference ImageRef_USERS_Uid = Storagereference.child("USERS/" + Usermodel.getUserModel_Uid() + "/USERSImage.jpg");

            // if : 등록하고자 하는 이미지가 없다면
            if (SelectedImagePath == null) {
                Log.d("울라랄라아","민규4");
                if(Usermodel.getUserModel_ProfileImage() !=null){
                    Log.d("울라랄라아","민규5");
                    Usermodel.setUserModel_ProfileImage(Usermodel.getUserModel_ProfileImage());
                }

                // if : 닉네임이 없다면 이전의 닉네임
                // else : 적힌 것이 있다면 그것으로
                if(UserModel_Nickname.equals("")){
                    Usermodel.setUserModel_NickName(Usermodel.getUserModel_NickName());
                }else{
                    Usermodel.setUserModel_NickName(Myinfo_Profile_NickName_EditText.getText().toString());
                }

                // 생일, 학교, 이미지는 null로 set
                Usermodel.setUserModel_BirthDay(BirthDay);
                Usermodel.setUserModel_University(UserModel_University);
                //Usermodel.setUserModel_ProfileImage(null);

                // 이전에 있던 이미지가 존재한다면 스토리지에서도 삭제
                ImageRef_USERS_Uid.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) { }
                });

                // 변경사항을 파이어스토어에 등록하는 함수
                MemberInit_Store_Uploader(Usermodel);
            }
            // else : 등록하고자 하는 이미지가 있다면
            else {
                try {
                    // 스토리지에 등록
                    InputStream Stream = new FileInputStream(new File(SelectedImagePath));
                    UploadTask Uploadtask = ImageRef_USERS_Uid.putStream(Stream);
                    Uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ImageRef_USERS_Uid.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri DownloadUri = task.getResult();

                                // if : 닉네임이 없다면 이전의 닉네임
                                // else : 적힌 것이 있다면 그것으로
                                if(UserModel_Nickname.equals("")){
                                    Usermodel.setUserModel_NickName(extractIDFromEmail(Usermodel.getUserModel_ID()));
                                }else{
                                    Usermodel.setUserModel_NickName(Myinfo_Profile_NickName_EditText.getText().toString());
                                }

                                // 생일, 학교, 이미지를 set
                                Usermodel.setUserModel_BirthDay(BirthDay);
                                Usermodel.setUserModel_University(UserModel_University);
                                Usermodel.setUserModel_ProfileImage(DownloadUri.toString());

                                // 변경사항을 파이어스토어에 등록하는 함수
                                MemberInit_Store_Uploader(Usermodel);
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        }else {
            Util.showToast(UpdateInfoActivity.this, "닉네임은 최대 20자까지 가능합나다.");
        }
    }

    // 변경사항을 파이어스토어에 등록하는 함수
    private void MemberInit_Store_Uploader(final UserModel Usermodel) {
        FirebaseFirestore docSet_USERS_Uid = FirebaseFirestore.getInstance();
        docSet_USERS_Uid.collection("USERS").document(this.Usermodel.getUserModel_Uid()).set(Usermodel.getUserInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Util.showToast(UpdateInfoActivity.this, "회원정보 수정을 성공하였습니다.");
                        LoaderLayout.setVisibility(View.GONE);
                        Intent Resultintent = new Intent();
                        Resultintent.putExtra("userModel", UpdateInfoActivity.this.Usermodel);
                        setResult(Activity.RESULT_OK, Resultintent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.showToast(UpdateInfoActivity.this, "회원정보 수정에 실패하였습니다.");
                        LoaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    // 이메일에서 @뒤로 잘라서 닉네임으로 이용
    String extractIDFromEmail(String email){
        String[] parts = email.split("@");
        return parts[0];
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}