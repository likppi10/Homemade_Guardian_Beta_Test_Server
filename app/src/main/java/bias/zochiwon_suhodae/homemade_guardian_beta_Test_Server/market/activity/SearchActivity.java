package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter.SearchResultAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;

// 검색을 실행하려 하고 검색하고자 하는 단어를 입력 받는 액티비티이다. / 카테고리별 첫번째 게시물과 핫게시물이 존재한다.
//      Ex) 메인프레그먼트에서 검색버튼을 눌러 넘어온다.
//      Ex) 단어를 입력한 후 버튼을 누르면 SearchResultActivity로 넘어가게 된다.

public class SearchActivity extends BasicActivity {                                             // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                                                // 1. 클래스
    private SearchResultAdapter SearchresultAdapter;                                                // 핫게시물의 나열을 위한 어댑터
                                                                                                // 2. 변수 및 배열
    private ArrayList<MarketModel> Marketmodel;                                                     // 핫게시물의 정보를 담고 있는 Marketmodel
                                                                                                // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private EditText Search_Market_EditText;                                                        // 검색하고자 하는 단어 입력 받는 EditText

                                                                                                // 4. 파이어베이스 관련 선언
    private FirebaseFirestore Firebasefirestore;                                                    // 파이어스토어

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       // 검색창 EditText, 검색 ImageView find
        Search_Market_EditText = findViewById(R.id.Search_PostTitle);
        ImageView Search_ImageView;
        Search_ImageView = findViewById(R.id.searchbtn);
        ImageView Back_ImageView;
        Back_ImageView = findViewById(R.id.backbtn);


        LinearLayout Food_Layout, Thing_Layout, Borrow_Layout, Quest_Layout;
        Food_Layout = findViewById(R.id.food_layout);
        Thing_Layout = findViewById(R.id.thing_layout);
        Borrow_Layout = findViewById(R.id.borrow_layout);
        Quest_Layout = findViewById(R.id.quest_layout);

       // 검색 ImageView, 카테고리 별 게시물 영역 Layout setOnClickListener
        Search_ImageView.setOnClickListener(onClickListener);
        Back_ImageView.setOnClickListener(onClickListener);
        Food_Layout.setOnClickListener(onClickListener);
        Thing_Layout.setOnClickListener(onClickListener);
        Borrow_Layout.setOnClickListener(onClickListener);
        Quest_Layout.setOnClickListener(onClickListener);

       // 파이어스토어 getInstance
        Firebasefirestore = FirebaseFirestore.getInstance();

       // Marketmodel 초기화
        Marketmodel = new ArrayList<>();

       // 핫게시물을 위한 어댑터 연결
        SearchresultAdapter = new SearchResultAdapter(SearchActivity.this, Marketmodel);
        final RecyclerView recyclerView = findViewById(R.id.HotrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.setAdapter(SearchresultAdapter);

       // 검색창에서도 검색(돋보기) 버튼을 누를 수 있도록 listener를 설정
        Search_Market_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                        String Search = Search_Market_EditText.getText().toString();
                        myStartActivity(SearchResultActivity.class,Search,"0");
                        break;
                }
                return true;
            }
        });

       // 카테고리 별 첫 게시물 제목을 받는 함수
        //CategoryMarket();

       // 최근 핫게시물 3개를 받는 함수
        HotMarket(true);
    }


   // 최근 핫게시물 2개를 받는 함수
    private void HotMarket(final boolean clear) {
        Date date = new Date() ;
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");
       // limit(2)로 날짜 순으로 두개만 받는다.
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_HotMarket","O").limit(3).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                Marketmodel.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Marketmodel.add(new MarketModel(
                                        document.getData().get("MarketModel_Title").toString(),
                                        document.getData().get("MarketModel_Text").toString(),
                                        (ArrayList<String>) document.getData().get("MarketModel_ImageList"),
                                        new Date(document.getDate("MarketModel_DateOfManufacture").getTime()),
                                        document.getData().get("MarketModel_Host_Uid").toString(),
                                        document.getId(),
                                        document.getData().get("MarketModel_Category").toString(),
                                        (ArrayList<String>) document.getData().get("MarketModel_LikeList"),
                                        document.getData().get("MarketModel_HotMarket").toString(),
                                        document.getData().get("MarketModel_reservation").toString(),
                                        document.getData().get("MarketModel_deal").toString(),
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))
                                ));
                            }
                            SearchresultAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

   // 검색 ImageView, 카테고리 별 게시물 영역의 OnClickListener / Info라는 밧을 가지고 전부 같은 SearchResultActivity로 이동한다.
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.searchbtn:
                    String Search = Search_Market_EditText.getText().toString();
                    myStartActivity(SearchResultActivity.class,Search,"0");
                    break;
                case R.id.backbtn:
                    finish();
                    break;
                case R.id.food_layout:
                    myStartActivity(SearchResultActivity.class,null,"1");
                    break;
                case R.id.thing_layout:
                    myStartActivity(SearchResultActivity.class,null,"2");
                    break;
                case R.id.borrow_layout:
                    myStartActivity(SearchResultActivity.class,null,"3");
                    break;
                case R.id.quest_layout:
                    myStartActivity(SearchResultActivity.class,null,"4");
                    break;
            }
        }
    };

    private void myStartActivity(Class c, String search, String info) {
        Intent Intent_Search_Words = new Intent(SearchActivity.this, c);
        Intent_Search_Words.putExtra("search", search);
        Intent_Search_Words.putExtra("Info", info);
        startActivityForResult(Intent_Search_Words, 0);
    }
}