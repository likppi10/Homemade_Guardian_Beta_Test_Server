package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

// SearchResultActivity에서 Info = 2을 가지고 이동된 Fragment : 카테고리가 '물건교환'인  Market 나열 Fragment

public class Market_Thing_Fragment extends Fragment {           // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                // 1. 클래스
    private SearchResultAdapter SearchresultAdapter;
                                                                // 2. 변수 및 배열
    private ArrayList<MarketModel> Marketmodel = new ArrayList<>(); // 해당하는 Market 정보 model
                                                                // 5. 기타 변수
    private boolean updating;                                       // 정보를 받아오는 중인지 분별하는 boolean 변수
    private boolean topScrolled;                                    // 상단으로 스크롤한 상태의 boolean 변수

    public Market_Thing_Fragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_searchcommunity_result, container, false);

       // Search_Market_Result에서 가져온 검색 결과를 나열할 어댑터와 연결한다.
        SearchresultAdapter = new SearchResultAdapter(getActivity(), Marketmodel);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(SearchresultAdapter);

       // 제일 위에서 스크롤 되는 경우
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    MarketUpdate(true);
                    topScrolled = false;
                }
            }

           // 불러들인 10개의 게시물 중에 스크롤하여 8번째가 보이면 업데이트 하는 경우
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                if(totalItemCount - 8 <= lastVisibleItemPosition && !updating){
                    MarketUpdate(false);
                }
                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

       // Market 게시물 불러들이는 함수
        MarketUpdate(false);
        return  view;
    }

   // Market 게시물 불러들이는 함수
    private void MarketUpdate(final boolean clear) {

       // 현재  MarketUpdate()가 진행 중임을 알림
        updating = true;
        FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_Category","물건교환").limit(10).get()
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
                       // 현재  MarketUpdate()가 끝났음을 알림
                        updating = false;
                    }
                });
    }
}