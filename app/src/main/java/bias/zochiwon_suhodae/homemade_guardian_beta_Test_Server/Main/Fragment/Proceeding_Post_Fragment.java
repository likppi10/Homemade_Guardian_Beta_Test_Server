package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter.SearchResultAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.util.ArrayList;

public class Proceeding_Post_Fragment extends Fragment {
    private String CurrentUid;
    private ArrayList<MarketModel> MarketList;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchResultFragment";
    private SearchResultAdapter searchResultAdapter;

    MarketModel marketModel;
    UserModel userModel;
    ArrayList<String> Market_reservationList = new ArrayList<>();

    int i=0;

    public Proceeding_Post_Fragment() {

    }
    public static final Proceeding_Post_Fragment getInstance(String CurrentUid) {
        Proceeding_Post_Fragment f = new Proceeding_Post_Fragment();
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
        final DocumentReference documentReferenceMyUser = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUid);
        documentReferenceMyUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Market_reservationList = userModel.getUserModel_Market_reservationList();
                for(i =0; i< Market_reservationList.size(); i++){
                    final DocumentReference documentReferenceMyUser = FirebaseFirestore.getInstance().collection("MARKETS").document(Market_reservationList.get(i));
                    documentReferenceMyUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
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
                            searchResultAdapter.notifyDataSetChanged();
                        }
                    });
                }
                //searchResultAdapter.notifyDataSetChanged();

            }
        });

    }
}
