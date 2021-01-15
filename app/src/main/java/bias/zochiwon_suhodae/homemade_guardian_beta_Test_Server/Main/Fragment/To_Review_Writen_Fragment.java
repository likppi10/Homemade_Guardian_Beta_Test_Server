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

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.ReviewResultAdapter;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.ReviewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

//나에게 작성된 리뷰
public class To_Review_Writen_Fragment extends Fragment {
    private String CurrentUid;
    private ArrayList<ReviewModel> ReviewList;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SearchResultFragment";
    private ReviewResultAdapter ReviewResultAdapter;

    public To_Review_Writen_Fragment() {

    }
    public static final To_Review_Writen_Fragment getInstance(String CurrentUid) {
        To_Review_Writen_Fragment f = new To_Review_Writen_Fragment();
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
        CurrentUid = getArguments().getString("CurrentUid");

        firebaseFirestore = FirebaseFirestore.getInstance();
        ReviewList = new ArrayList<>();
        ReviewResultAdapter = new ReviewResultAdapter(getActivity(), ReviewList);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(ReviewResultAdapter);
        ReviewUpdate(true);
        return  view;
    }
    private void ReviewUpdate(final boolean clear) {

        Date date =  new Date();
        CollectionReference collectionReference = firebaseFirestore.collection("USERS").document(CurrentUid).collection("REVIEW");
        collectionReference.orderBy("ReviewModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("ReviewModel_DateOfManufacture", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                        ReviewList.clear();                                                           // part16 : List 안의 데이터 초기화
                    }                                                                               // part16 : postsUpdate로 이동 (15'50")
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ReviewModel reviewModel = document.toObject(ReviewModel.class);
                        ReviewList.add(reviewModel);
                    }
                    ReviewResultAdapter.notifyDataSetChanged();
                } else {
                    Log.d("민규다다","없음");
                }
                //updating = false;
            }
        });

    }
}
