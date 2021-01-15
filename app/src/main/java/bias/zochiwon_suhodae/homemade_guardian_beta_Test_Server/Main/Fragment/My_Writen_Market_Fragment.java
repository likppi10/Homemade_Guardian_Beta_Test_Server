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

public class My_Writen_Market_Fragment extends Fragment {
    private String CurrentUid;
    private ArrayList<MarketModel> MarketList;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchResultFragment";
    private SearchResultAdapter searchResultAdapter;
    MarketModel marketModel;

    public My_Writen_Market_Fragment() {

    }
    public static final My_Writen_Market_Fragment getInstance(String CurrentUid) {
        My_Writen_Market_Fragment f = new My_Writen_Market_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_searchresult, container, false);
        CurrentUid =  getArguments().getString("CurrentUid");
        Log.d("asd","CurrentUid : " + CurrentUid);

        firebaseFirestore = FirebaseFirestore.getInstance();
        MarketList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(getActivity(), MarketList);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchResultAdapter);
        MarketUpdate(true);
        return  view;
    }
    private void MarketUpdate(final boolean clear) {
// updating = true;

        Date date = MarketList.size() == 0 || clear ? new Date() : MarketList.get(MarketList.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = firebaseFirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        //아래꺼는 색인이 없으면 안뜨는듯
        collectionReference.whereEqualTo("MarketModel_Host_Uid",CurrentUid).orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).limit(10).get()
        //collectionReference.whereEqualTo("MarketModel_Host_Uid",CurrentUid).limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("asd","asd1");
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                MarketList.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MarketList.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                            searchResultAdapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        //updating = false;
                    }
                });

    }
}
