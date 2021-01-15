package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// 사용자의 정보를 보여주는 액티비티이다.
//      Ex) 현재는 PostActivity에서 프로필 이미지를 눌렀을 때 사용되고 있지만, 내정보Activity를 구현하는 데에도 사용될 것이다.

public class HostModelActivity extends BasicActivity {

    ImageView Myinfo_profileImage,Menu;
    TextView Myinfo_profileNickName, Myinfo_profileUniversity, Hosts_Review;
    LinearLayout Proceeding_Post,Deal_Complete_Post,My_Writen_Post;
    TextView Kind_Count,Complete_Count,Correct_Count,Bad_Count;
    LinearLayout To_Reviews_written;
    UserModel userModel = new UserModel();
    private String WriterUid;
    private String CurrentUid;                                  // 현재 사용자의 Uid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_info);


       // 게시물 작성자의 Uid를 받아옴
        WriterUid = getIntent().getStringExtra("toUid");

        Myinfo_profileImage = findViewById(R.id.Users_Profile_ImageView);
        Myinfo_profileNickName = findViewById(R.id.Users_Usernm);
        Myinfo_profileUniversity = findViewById(R.id.Myinfo_profileUniversity);
        Hosts_Review = findViewById(R.id.Hosts_Review);

        Proceeding_Post = findViewById(R.id.Proceeding_Post);
        Proceeding_Post.setOnClickListener(onClickListener);
        Deal_Complete_Post = findViewById(R.id.Deal_Complete_Post);
        Deal_Complete_Post.setOnClickListener(onClickListener);
        My_Writen_Post = findViewById(R.id.My_Writen_Post);
        My_Writen_Post.setOnClickListener(onClickListener);

        Kind_Count = findViewById(R.id.Kind_Count);
        Complete_Count = findViewById(R.id.Complete_Count);
        Correct_Count = findViewById(R.id.Correct_Count);
        Bad_Count = findViewById(R.id.Bad_Count);

        To_Reviews_written = findViewById(R.id.To_Reviews_written);
        To_Reviews_written.setOnClickListener(onClickListener);


        // 현재 사용자 설정
        CurrentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 메뉴 버튼 활성화
        Menu = findViewById(R.id.menu);
        Menu.setOnClickListener(onClickListener);
        if(CurrentUid.equals(WriterUid)){
            Menu.setVisibility(View.GONE);
        }
        getUserModel(WriterUid);

    }

   // CurrentUid로 현재 사용자의 정보를 가져온다.
    public void getUserModel(String WriterUid){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(WriterUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //데이터의 존재여부
                            userModel = document.toObject(UserModel.class);
                            Profile_Info(userModel);
                        }
                    }
                }
            }
        });
        return;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Proceeding_Post:
                    myStartActivity(MyInfoPostActivity.class,"0");
                    break;
                case R.id.Deal_Complete_Post:
                    myStartActivity(MyInfoPostActivity.class,"1");
                    break;
                case R.id.My_Writen_Post:
                    myStartActivity(MyInfoPostActivity.class,"2");
                    break;
                case R.id.To_Reviews_written:
                    myStartActivity(MyInfoPostActivity.class,"3");
                    break;
                // 메뉴 버튼
                case R.id.menu:
                    showPopup(v);
                    break;
            }
        }
    };

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(HostModelActivity.this, v);
        getMenuInflater().inflate(R.menu.writer_menu, popup.getMenu());

        // 메뉴에서의 MenuItem이 클릭 되었을 때의 Listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.Post_Report_Button:
                        Toast.makeText(getApplicationContext(), "신고 되었습니다.", Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
    public void Profile_Info(UserModel Usermodel){
        setToolbarTitle(userModel.getUserModel_NickName()+"님의 회원정보");
        Myinfo_profileNickName.setText(Usermodel.getUserModel_NickName());
        if(Usermodel.getUserModel_University() == 0){
            Myinfo_profileUniversity.setText("홍익대학교");
        }
        else {
            Myinfo_profileUniversity.setText("고려대학교");
        }
        //post의 이미지 섬네일 띄우기
        if(Usermodel.getUserModel_ProfileImage() != null){
            Glide.with(this).load(Usermodel.getUserModel_ProfileImage()).centerCrop().into(Myinfo_profileImage);
        }
        else{
            //런타임 에러에 걸리는 곳
            Glide.with(this).load(R.drawable.none_profile_user).centerCrop().into(Myinfo_profileImage);
        }
        Hosts_Review.setText(userModel.getUserModel_NickName()+"님의 리뷰..!");
        Kind_Count.setText("X"+Usermodel.getUserModel_kindReviewList().size());
        Complete_Count.setText("X"+Usermodel.getUserModel_completeReviewList().size());
        Correct_Count.setText("X"+Usermodel.getUserModel_correctReviewList().size());
        Bad_Count.setText("X"+Usermodel.getUserModel_badReviewList().size());

    }

    private void myStartActivity(Class c,String Info) {
        Intent intent = new Intent(HostModelActivity.this, c);
        intent.putExtra("Info",Info);
        intent.putExtra("CurrentUid",WriterUid);
        intent.putExtra("userModel",userModel);
        startActivityForResult(intent, 0);
    }
}