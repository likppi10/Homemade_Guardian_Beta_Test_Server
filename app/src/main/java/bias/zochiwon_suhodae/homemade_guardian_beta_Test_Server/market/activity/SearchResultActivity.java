package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity;

import android.os.Bundle;
import android.view.MenuItem;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment.Market_Borrow_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment.Market_Food_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment.Market_Quest_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment.Market_Thing_Fragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment.SearchResultFragment;

// SearchActivity에서 버튼을 눌러 넘어 온 액티비티이다.
//      Ex) SearchResultFragment에서 Fragment를 이용하여 결과물을 출력한다. <-> SearchResultAdapter와 연결된다.

public class SearchResultActivity extends BasicActivity {           // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                    // 1. 클래스
    private SearchResultFragment SearchResultFragment;                  // 검색결과의 Fragment        (Info = 0)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);

       // SearchActivity에서 같이 넘어온 Info 값을 getIntent
        String Info = getIntent().getStringExtra("Info");
        String Search = getIntent().getStringExtra("search");

       // 검색결과의 Fragment        (Info = 0)
        if(Info.equals("0")) {
            setToolbarTitle("검색 결과");
            SearchResultFragment = SearchResultFragment.getInstance(Search);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, SearchResultFragment)
                    .commit();
        }

       // 음식교환 카테고리의 Fragment (Info = 1)
        if(Info.equals("1")) {
            Market_Food_Fragment marketFoodFragment;
            marketFoodFragment = new Market_Food_Fragment();
            setToolbarTitle("음식교환");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, marketFoodFragment)
                    .commit();
        }

       // 물건교환 카테고리의 Fragment (Info = 2)
        if(Info.equals("2")) {
            Market_Thing_Fragment marketThingFragment;
            marketThingFragment = new Market_Thing_Fragment();
            setToolbarTitle("물건교환");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, marketThingFragment)
                    .commit();
        }

       //  대여하기 카테고리의 Fragment (Info = 3)
        if(Info.equals("3")) {
            Market_Borrow_Fragment marketBorrowFragment;
            marketBorrowFragment = new Market_Borrow_Fragment();
            setToolbarTitle("대여하기");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, marketBorrowFragment)
                    .commit();
        }

       // 퀘스트 카테고리의 Fragment  (Info = 4)
        if(Info.equals("4")) {
            Market_Quest_Fragment marketQuestFragment;
            marketQuestFragment = new Market_Quest_Fragment();
            setToolbarTitle("퀘스트");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.MyInfo_Post_Fragment, marketQuestFragment)
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
}
