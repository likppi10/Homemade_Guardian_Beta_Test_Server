package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
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

// SearchCommunityActivity (abc 검색) -> SearchCommunityResultActivity (abc 가져옴) (abc 전해줌) -> SearchCommunityResultFragment (abc 가져옴)

public class SearchCommunityResultFragment extends Fragment {           // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                        // 1. 클래스
    private SearchCommunityResultAdapter SearchCommunityresultAdapter;
                                                                        // 2. 변수 및 배열
    private String Search;                                                  // 검색하고자 하는 단어
    private ArrayList<CommunityModel> CommunityList  = new ArrayList<>();   // 해당하는 Community 정보 model

    public SearchCommunityResultFragment() {}

   // SearchCommunityResultActivity에서 검색하려는 단어를 SearchCommunityResultFragment 넘겨주기 위한 함수
    public static final SearchCommunityResultFragment getInstance(String search) {
        SearchCommunityResultFragment f = new SearchCommunityResultFragment();
        Bundle bdl = new Bundle();
        bdl.putString("Communitysearch", search);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_searchcommunity_result, container, false);

       // SearchCommunityResultFragment getInstance로 받은 검색하려는 단어 get
        Search = (String)  getActivity().getIntent().getSerializableExtra("Communitysearch");

       // Search_Market_Result에서 가져온 검색 결과를 나열할 어댑터와 연결한다.
        SearchCommunityresultAdapter = new SearchCommunityResultAdapter(getActivity(), CommunityList);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(SearchCommunityresultAdapter);

       // 검색결과를 가져오는 함수
        CommunityUpdate(true);
        return  view;
    }

   // 검색결과를 가져오는 함수
    private void CommunityUpdate(final boolean clear) {
        Date date = new Date();
        FirebaseFirestore Firebasefirestore;
        Firebasefirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = Firebasefirestore.collection("COMMUNITY");
        collectionReference.orderBy("CommunityModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("CommunityModel_DateOfManufacture", date).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String title = null;
                        if (task.isSuccessful()) {
                            if(clear){
                                CommunityList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                title = document.getData().get("CommunityModel_Title").toString();

                               // if : 제목이 검색한 단어에 포함되어 있는 case 일때만 진행
                                if(title.toLowerCase().contains(Search.toLowerCase())) {
                                    CommunityList.add(new CommunityModel(
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
                            }
                            SearchCommunityresultAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
