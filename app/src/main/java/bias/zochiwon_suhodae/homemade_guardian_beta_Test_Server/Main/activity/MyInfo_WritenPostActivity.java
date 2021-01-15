package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment.My_Writen_Community_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment.My_Writen_Market_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyInfo_WritenPostActivity extends AppCompatActivity {
    private My_Writen_Market_Fragment My_Writen_Market_Fragment;
    private My_Writen_Community_Fragment My_Writen_Community_Fragment;
    private String currentUser_Uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_writenpost);

        currentUser_Uid = getIntent().getStringExtra("CurrentUid");

        init();
    }

    private void init() {
        My_Writen_Market_Fragment = My_Writen_Market_Fragment.getInstance(currentUser_Uid);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, My_Writen_Market_Fragment).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.TopNavigationView);            // part22 : 바텀 네비게이션바  설정 (47'20")
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Market:
                        My_Writen_Market_Fragment = My_Writen_Market_Fragment.getInstance(currentUser_Uid);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, My_Writen_Market_Fragment).commit();
                        return true;
                    case R.id.Community:
                        My_Writen_Community_Fragment = My_Writen_Community_Fragment.getInstance(currentUser_Uid);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, My_Writen_Community_Fragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

}
