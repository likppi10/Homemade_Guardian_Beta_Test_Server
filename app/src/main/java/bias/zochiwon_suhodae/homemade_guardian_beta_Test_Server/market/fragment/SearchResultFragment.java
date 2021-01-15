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
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter.SearchResultAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

// SearchResultActivity에서 Info = 0을 가지고 이동된 Fragment : 검색된  Market 나열 Fragment

public class SearchResultFragment extends Fragment {            // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                // 1. 클래스
    private SearchResultAdapter SearchresultAdapter;
                                                                // 2. 변수 및 배열
    private String Search;                                          // 검색하고자 하는 단어
    private ArrayList<MarketModel> Marketmodel= new ArrayList<>();  // 해당하는 Market 정보 model

    public SearchResultFragment() {}

   // SearchResultActivity에서 검색하려는 단어를 SearchResultFragment로 넘겨주기 위한 함수
    public static final SearchResultFragment getInstance(String search) {
        SearchResultFragment f = new SearchResultFragment();
        Bundle bdl = new Bundle();
        bdl.putString("search", search);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_searchresult, container, false);

       // SearchResultFragment getInstance로 받은 검색하려는 단어 get
        Search = (String)  getActivity().getIntent().getSerializableExtra("search");

       // Search_Market_Result에서 가져온 검색 결과를 나열할 어댑터와 연결한다.
        SearchresultAdapter = new SearchResultAdapter(getActivity(), Marketmodel);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(SearchresultAdapter);

       // 검색결과를 가져오는 함수
        Search_Market_Result(true);
        return  view;
    }

    // 검색결과를 가져오는 함수
    private void Search_Market_Result(final boolean clear) {
        Date date = new Date();
        FirebaseFirestore Firebasefirestore;
        Firebasefirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String title= null;
                        if (task.isSuccessful()) {
                            if(clear){
                                Marketmodel.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                title = document.getData().get("MarketModel_Title").toString();

                               // if : 제목이 검색한 단어에 포함되어 있는 case 일때만 진행
                                if(title.toLowerCase().contains(Search.toLowerCase())) {
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
                            }
                            SearchresultAdapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
    }
}
