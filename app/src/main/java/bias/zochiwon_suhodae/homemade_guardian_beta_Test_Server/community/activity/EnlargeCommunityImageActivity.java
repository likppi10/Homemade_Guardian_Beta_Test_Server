package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.adapter.CommunityViewPagerAdapter;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

// CommunityActivity에서 이미지가 있다면 클릭하여 큰 Image의 뷰페이저 형식으로 볼 수 있음

public class EnlargeCommunityImageActivity extends BasicActivity {         // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                            // 2. 변수 및 배열
    private ArrayList<String> ImageList = new ArrayList<>();                    // 게시물의 이미지 리스트
                                                                            // 3. xml 변수
    private ViewPager Viewpager;                                                // 이미지들을 보여주기 위한 ViewPager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_imagelist);

       // marketActivity에서 받아온 Image들을 ImageList로 저장
        ImageList = (ArrayList<String>) getIntent().getSerializableExtra("communityImage");

        Viewpager = findViewById(R.id.ViewPager);
        Viewpager.setAdapter(new CommunityViewPagerAdapter(this, ImageList, "Disable","Enlarge"));

       // 이미지의 개수 대로 이미지 하단에 CircleIndicator를 생성
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(Viewpager);
    }
}