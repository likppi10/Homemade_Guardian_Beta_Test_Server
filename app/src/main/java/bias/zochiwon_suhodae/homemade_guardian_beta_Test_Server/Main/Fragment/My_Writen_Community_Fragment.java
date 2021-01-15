package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment;

import android.os.Bundle;
import android.util.Log;
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

public class My_Writen_Community_Fragment extends Fragment {
    private String CurrentUid;
    private ArrayList<CommunityModel> CommunityList;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchResultFragment";
    private SearchCommunityResultAdapter searchCommunityResultAdapter;
    MarketModel marketModel;

    public My_Writen_Community_Fragment() {

    }
    public static final My_Writen_Community_Fragment getInstance(String CurrentUid) {
        My_Writen_Community_Fragment f = new My_Writen_Community_Fragment();
        Bundle bdl = new Bundle();
        bdl.putString("CurrentUid", CurrentUid);
        f.setArguments(bdl);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchcommunity_result, container, false);
        CurrentUid =  getArguments().getString("CurrentUid");
        Log.d("asd","CurrentUid : " + CurrentUid);

        firebaseFirestore = FirebaseFirestore.getInstance();
        CommunityList = new ArrayList<>();
        searchCommunityResultAdapter = new SearchCommunityResultAdapter(getActivity(), CommunityList);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchCommunityResultAdapter);
        MarketUpdate(true);
        return  view;
    }
    private void MarketUpdate(final boolean clear) {
// updating = true;

        Date date = CommunityList.size() == 0 || clear ? new Date() : CommunityList.get(CommunityList.size() - 1).getCommunityModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = firebaseFirestore.collection("COMMUNITY");                // 파이어베이스의 posts에서
        //아래꺼는 색인이 없으면 안뜨는듯
        collectionReference.whereEqualTo("CommunityModel_Host_Uid",CurrentUid).orderBy("CommunityModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("CommunityModel_DateOfManufacture", date).get()                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                CommunityList.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CommunityList.add(new CommunityModel(                                                          //postList로 데이터를 넣는다.
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
                            searchCommunityResultAdapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //updating = false;
                    }
                });

    }
}
