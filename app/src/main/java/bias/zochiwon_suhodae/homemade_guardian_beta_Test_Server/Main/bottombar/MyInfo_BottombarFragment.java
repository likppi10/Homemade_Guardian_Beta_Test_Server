package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.MainActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.MyInfoPostActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.UpdateInfoActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyInfo_BottombarFragment extends Fragment {
    ImageView Myinfo_profileImage;
    TextView Myinfo_profileNickName, Myinfo_profileUniversity;
    LinearLayout Proceeding_Post,Deal_Complete_Post,My_Writen_Post;
    TextView Kind_Count,Complete_Count,Correct_Count,Bad_Count;
    LinearLayout LogOut_Button,To_Reviews_written;
    Button Update_InfoBtn;
    private String CurrentUid;
    private FirebaseUser CurrentUser;
    UserModel userModel = new UserModel();


    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,@Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        Toolbar myToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("내 정보");
        }


        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CurrentUid =CurrentUser.getUid();

        Myinfo_profileImage = (ImageView) view.findViewById(R.id.Myinfo_profileImage);
        Myinfo_profileNickName = (TextView) view.findViewById(R.id.Myinfo_profileNickName);
        Myinfo_profileUniversity = (TextView) view.findViewById(R.id.Myinfo_profileUniversity);

        Proceeding_Post = view.findViewById(R.id.Proceeding_Post);
        Proceeding_Post.setOnClickListener(onClickListener);
        Deal_Complete_Post = view.findViewById(R.id.Deal_Complete_Post);
        Deal_Complete_Post.setOnClickListener(onClickListener);
        My_Writen_Post = view.findViewById(R.id.My_Writen_Post);
        My_Writen_Post.setOnClickListener(onClickListener);

        Kind_Count = (TextView) view.findViewById(R.id.Kind_Count);
        Complete_Count = (TextView) view.findViewById(R.id.Complete_Count);
        Correct_Count = (TextView) view.findViewById(R.id.Correct_Count);
        Bad_Count = (TextView) view.findViewById(R.id.Bad_Count);

        LogOut_Button = view.findViewById(R.id.LogOut_Button);
        LogOut_Button.setOnClickListener(onClickListener);
        To_Reviews_written = view.findViewById(R.id.To_Reviews_written);
        To_Reviews_written.setOnClickListener(onClickListener);

        Update_InfoBtn = view.findViewById(R.id.Update_Info_Buutton);
        Update_InfoBtn.setOnClickListener(onClickListener);
        getUserModel(CurrentUid);

        return view;
    }

    public void getUserModel(String CurrentUid){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUid);
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
                case R.id.Update_Info_Buutton:
                    myStartActivity(UpdateInfoActivity.class,"4");
                    break;
                case R.id.LogOut_Button:
                    ((MainActivity)getActivity()).Logout_dialog();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                getUserModel(CurrentUid);
            }
        }
    }

    public void Profile_Info(UserModel Usermodel){
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
        Kind_Count.setText("X"+Usermodel.getUserModel_kindReviewList().size());
        Complete_Count.setText("X"+Usermodel.getUserModel_completeReviewList().size());
        Correct_Count.setText("X"+Usermodel.getUserModel_correctReviewList().size());
        Bad_Count.setText("X"+Usermodel.getUserModel_badReviewList().size());

    }

    private void myStartActivity(Class c,String Info) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("Info",Info);
        intent.putExtra("CurrentUid",CurrentUid);
        intent.putExtra("userModel",userModel);
        startActivityForResult(intent, 0);
    }


}