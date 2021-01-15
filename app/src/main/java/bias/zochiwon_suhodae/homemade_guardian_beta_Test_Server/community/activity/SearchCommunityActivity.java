package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.adapter.SearchCommunityResultAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

// 검색을 실행하려 하고 검색하고자 하는 단어를 입력 받는 액티비티이다.
//      Ex) 메인프레그먼트에서 검색버튼을 눌러 넘어온다.
//      Ex) 단어를 입력한 후 버튼을 누르면 SearchCommunityResultActivity로 넘어가게 된다.

public class SearchCommunityActivity extends BasicActivity {            // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                        // 1. 클래스
    private SearchCommunityResultAdapter SearchCommunityresultAdapter;      // 핫게시물의 나열을 위한 어댑터
                                                                        // 2. 변수 및 배열
    private ArrayList<CommunityModel> Communitymodel;                       // 핫게시물의 정보를 담고 있는 Communitymodel
                                                                        // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private EditText Search_Community_EditText;                             // 검색하고자 하는 단어 입력 받는 EditText
                                                                        // 4. 파이어베이스 관련 선언
    private FirebaseFirestore Firebasefirestore;                            // 파이어스토어

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_search);

        ImageView Search_ImageView;
        ImageView Back_ImageView;
        Back_ImageView = findViewById(R.id.backbtn);

       // 검색창 EditText, 검색 ImageView find
        Search_Community_EditText = findViewById(R.id.Search_PostTitle);
        Search_ImageView = findViewById(R.id.searchbtn);

       // 검색 ImageView setOnClickListener
        Search_ImageView.setOnClickListener(onClickListener);
        Back_ImageView.setOnClickListener(onClickListener);

       // 파이어스토어 getInstance
        Firebasefirestore = FirebaseFirestore.getInstance();

       // Communitymodel 초기화
        Communitymodel = new ArrayList<>();

       // 핫게시물을 위한 어댑터 연결
        SearchCommunityresultAdapter = new SearchCommunityResultAdapter(SearchCommunityActivity.this, Communitymodel);
        final RecyclerView recyclerView = findViewById(R.id.HotrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchCommunityActivity.this));
        recyclerView.setAdapter(SearchCommunityresultAdapter);

       // 검색창에서도 검색(돋보기) 버튼을 누를 수 있도록 listener를 설정
        Search_Community_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case IME_ACTION_SEARCH :
                        String Search = Search_Community_EditText.getText().toString();
                        myStartActivity(SearchCommunityResultActivity.class,Search);
                        break;
                }
                return true;
            }
        });

       // 최근 핫게시물 4개를 받는 함수
        HotCommunity(true);
    }

   // 최근 핫게시물 2개를 받는 함수
    private void HotCommunity(final boolean clear) {
        Date date = new Date();
        CollectionReference collectionReference = Firebasefirestore.collection("COMMUNITY");
       // limit(2)로 날짜 순으로 두개만 받는다.
        collectionReference.orderBy("CommunityModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("CommunityModel_DateOfManufacture", date).whereEqualTo("CommunityModel_HotCommunity","O").limit(4).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) { if (task.isSuccessful()) {
                                if(clear){
                                    Communitymodel.clear();
                                }
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                        Communitymodel.add(new CommunityModel(
                                                document.getData().get("CommunityModel_Title").toString(),
                                                document.getData().get("CommunityModel_Text").toString(),
                                                (ArrayList<String>) document.getData().get("CommunityModel_ImageList"),
                                                new Date(document.getDate("CommunityModel_DateOfManufacture").getTime()),
                                                document.getData().get("CommunityModel_Host_Uid").toString(),
                                                document.getId(),
                                                (ArrayList<String>) document.getData().get("CommunityModel_LikeList"),
                                                document.getData().get("CommunityModel_HotCommunity").toString(),
                                                Integer.parseInt(String.valueOf(document.getData().get("CommunityModel_CommentCount")))
                                        ));
                                }
                                SearchCommunityresultAdapter.notifyDataSetChanged();
                            }
                        }
                    });
    }

   // 검색 ImageView OnClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.searchbtn:
                    String Search = Search_Community_EditText.getText().toString();
                    myStartActivity(SearchCommunityResultActivity.class,Search);
                    break;
                case R.id.backbtn:
                    finish();
                    break;
            }
        }
    };

    private void myStartActivity(Class c, String search) {
        Intent Intent_Search_Words = new Intent(SearchCommunityActivity.this, c);
        Intent_Search_Words.putExtra("Communitysearch", search);
        startActivityForResult(Intent_Search_Words, 0);
    }
}