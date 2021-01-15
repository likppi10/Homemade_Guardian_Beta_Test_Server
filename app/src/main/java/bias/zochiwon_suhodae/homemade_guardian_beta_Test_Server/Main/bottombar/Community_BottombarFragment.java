package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity.SearchCommunityActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.adapter.CommunityAdapter;
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

//커뮤니티 페이지 바텀바 프레그먼트
public class Community_BottombarFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore firebaseFirestore;
    private CommunityAdapter CommunityAdapter;
    private ArrayList<CommunityModel> CommunityList;
    private boolean updating;
    private boolean topScrolled;
    private String State;
    private RecyclerView recyclerView;
    private ImageView HotCommunitybtn;
    private String HotCommunitybtn_State = "unSelected";
    private ImageView searchbtn;

    public Community_BottombarFragment() {                                                                                 // part22 : 프레그먼트로 내용 이전 (21'40")
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        //지역선택
        Spinner Community_Category_Spinner = (Spinner)view.findViewById(R.id.Local_Spinner);
        ArrayAdapter Community_Category_Adapter = ArrayAdapter.createFromResource(getContext(), R.array.Local, android.R.layout.simple_spinner_item);
        Community_Category_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Community_Category_Spinner.setAdapter(Community_Category_Adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        CommunityList = new ArrayList<>();
        CommunityAdapter = new CommunityAdapter(getActivity(), CommunityList);
        //homeAdapter.setOnPostListener(onPostListener);

        recyclerView = view.findViewById(R.id.recyclerView);
        //view.findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        view.findViewById(R.id.searchbtn).setOnClickListener(onClickListener);

        //view.findViewById(R.id.AllPostbtn).setOnClickListener(onClickListener);
        HotCommunitybtn = (ImageView) view.findViewById(R.id.HotPostbtn);
        HotCommunitybtn.setOnClickListener(onClickListener);
        searchbtn = (ImageView) view.findViewById(R.id.searchbtn);
        searchbtn.setOnClickListener(onClickListener);
        //searchbtn.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);

        State = "전체";
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(CommunityAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {                          // part21 : 스크롤로 새로고침 (29'10")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {        //part21 : 스크롤 손을 뗏을때(31')
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){                                      // part21 : 위로 새로고침 (39'40")
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    JudgeState(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){                          //part21 : 스크롤 되는 내내(31')
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                if(totalItemCount - 8 <= lastVisibleItemPosition && !updating){                         // part21 : 아래에서 3번쩨 일때 && 로딩중일 때는 이벤트 작용 안하게 (35'10")
                    JudgeState(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        All_CommunityUpdate(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
//        homeAdapter.playerStop();                                                                       //part21 : 앱정지시 비디오 정지 실행문 (15'10")
    }

    public void JudgeState(Boolean clear){
        if(State == "전체"){
            All_CommunityUpdate(clear);

        }else if(State == "핫게시판"){
            Hot_CommunityUpdate(clear);
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.searchbtn:
                    myStartActivity(SearchCommunityActivity.class);
                    break;
                case R.id.HotPostbtn:
                    if(HotCommunitybtn_State.equals("unSelected")){
                    HotCommunitybtn.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                        CommunityList.clear();
                    Hot_CommunityUpdate(true);
                    State = "핫게시판";
                        HotCommunitybtn_State = "Selected";
                    }else if(HotCommunitybtn_State.equals("Selected")){
                        HotCommunitybtn.setColorFilter(null);
                        CommunityList.clear();
                        All_CommunityUpdate(true);
                        State = "전체";
                        HotCommunitybtn_State = "unSelected";
                    }
                    break;
            }
        }
    };

    private void All_CommunityUpdate(final boolean clear) {
        updating = true;
        Date date = CommunityList.size() == 0 || clear ? new Date() : CommunityList.get(CommunityList.size() - 1).getCommunityModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = firebaseFirestore.collection("COMMUNITY");                // 파이어베이스의 posts에서
        Log.d("로그","스크롤 333");
        collectionReference.orderBy("CommunityModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("CommunityModel_DateOfManufacture", date).limit(10).get()        // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                CommunityList.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            Log.d("로그","스크롤 555");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d("로그","스크롤 3333");
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
                            CommunityAdapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Hot_CommunityUpdate(final boolean clear) {
        updating = true;

        Date date = CommunityList.size() == 0 || clear ? new Date() : CommunityList.get(CommunityList.size() - 1).getCommunityModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = firebaseFirestore.collection("COMMUNITY");                // 파이어베이스의 posts에서
        collectionReference.orderBy("CommunityModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("CommunityModel_DateOfManufacture", date).whereEqualTo("CommunityModel_HotCommunity","O").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int HotCommunityCut;
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                CommunityList.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                HotCommunityCut = ((ArrayList<String>) document.getData().get("CommunityModel_LikeList")).size();
                                if(HotCommunityCut>0) {
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
                                HotCommunityCut = 0;
                            }
                            CommunityAdapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }




    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }
}

