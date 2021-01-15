package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment.Deal_Complete_Post_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment.Proceeding_Post_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment.To_Review_Writen_Fragment;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;

//SearchActivity에서 버튼을 눌러 넘어 온 액티비티이다.
//      Ex) SearchResultFragment에서 Fragment를 이용하여 결과물을 출력한다. <-> SearchResultAdapter와 연결된다.

public class MyInfoPostActivity extends BasicActivity {
    private Deal_Complete_Post_Fragment Deal_Complete_Post_Fragment;
    private Proceeding_Post_Fragment Proceeding_Post_Fragment;
    private To_Review_Writen_Fragment To_Review_Writen_Fragment;
    UserModel userModel = new UserModel();
    private String CurrentUid;

    /*
    private String CurrentUid;
    private FirebaseUser CurrentUser;
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        String Info = getIntent().getStringExtra("Info");               //선택한 것의 넘버
        CurrentUid = getIntent().getStringExtra("CurrentUid");   //현재 자신의 UID
        userModel = (UserModel) getIntent().getSerializableExtra("userModel");
        Log.d("userModel", "userModel: " + userModel.getUserModel_NickName());


        if(Info.equals("0")) {
            // Proceeding_Post_Fragment
            setToolbarTitle("진행중인 포스트");
            Proceeding_Post_Fragment = Proceeding_Post_Fragment.getInstance(CurrentUid);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, Proceeding_Post_Fragment)
                    .commit();
        }
        if(Info.equals("1")) {
            // Deal_Complete_Post_Fragment
            setToolbarTitle("거래 완료");
            Deal_Complete_Post_Fragment = Deal_Complete_Post_Fragment.getInstance(CurrentUid);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, Deal_Complete_Post_Fragment)
                    .commit();
        }
        if(Info.equals("2")) {
            // My_Writen_Post_Fragment
            myStartFinishActivity(MyInfo_WritenPostActivity.class);
        }
        if(Info.equals("3")) {
            // To_Review_Writen_Fragment
            setToolbarTitle("나에게 작성한 리뷰");
            To_Review_Writen_Fragment = To_Review_Writen_Fragment.getInstance(CurrentUid);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, To_Review_Writen_Fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void myStartFinishActivity(Class c) {                                                             // part22 : c에다가 이동하려는 클래스를 받고 requestcode는 둘다 1로 준다.
        Intent intent = new Intent(this, c);
        intent.putExtra("CurrentUid",CurrentUid);
        startActivityForResult(intent, 1);
        finish();
    }
}
