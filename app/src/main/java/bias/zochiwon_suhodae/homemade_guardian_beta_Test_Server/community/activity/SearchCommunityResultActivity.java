package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity;

import android.os.Bundle;
import android.view.MenuItem;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.fragment.SearchCommunityResultFragment;

// SearchCommunityActivity에서 버튼을 눌러 넘어 온 액티비티이다.
//      Ex) SearchCommunityResultFragment에서 Fragment를 이용하여 결과물을 출력한다. <-> SearchCommunityResultAdapter와 연결된다.

public class SearchCommunityResultActivity extends BasicActivity {          // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                            // 1. 클래스
    private SearchCommunityResultFragment SearchCommunityResultFragment;        // 검색결과의 Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchcommunity_result);
        setToolbarTitle("검색 결과");

       // SearchCommunityActivity 같이 넘어온 "Communitysearch" 값을 getIntent
        String Search = getIntent().getStringExtra("Communitysearch");

        SearchCommunityResultFragment = SearchCommunityResultFragment.getInstance(Search);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.SearchCommunityResultFragment, SearchCommunityResultFragment)
                .commit();
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
}
