package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity.SearchActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter.MarketAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;

public class Market_BottombarFragment extends Fragment {          // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                        // 2. 변수 및 배열
    private MarketAdapter Marketadapter;                    // MarketAdapter
    private ArrayList<MarketModel> Marketmodel;             // Marketmodel 선언
    private String State;                                   // 카테고리의 현재 상태
    private String FoodMarketbtn_State = "unSelected";      // 카테고리 '음식교환'의 선택 여부
    private String ThingMarketbtn_State = "unSelected";     // 카테고리 '물건교환'의 선택 여부
    private String BorrowMarketbtn_State = "unSelected";    // 카테고리 '대여하기'의 선택 여부
    private String QuestMarketbtn_State = "unSelected";     // 카테고리 '퀘스트'의 선택 여부
    private String HotMarketbtn_State = "unSelected";       // 카테고리 '핫게시물'의 선택 여부
                                                        // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private RecyclerView Recyclerview;                      // 어댑터의 내용을 나열하기 위한 RecyclerView
    private ImageView SearchButton_ImageView;                     // 검색 ImageView
    private ImageView FoodMarket_ImageView;                 // 카테고리 '음식교환' ImageView
    private ImageView ThingMarket_ImageView;                // 카테고리 '물건교환' ImageView
    private ImageView BorrowMarket_ImageView;               // 카테고리 '대여하기' ImageView
    private ImageView QuestMarket_ImageView;                // 카테고리 '퀘스트' ImageView
    private ImageView HotMarket_ImageView;                  // 카테고리 '핫게시물' ImageView
    private TextView FoodText_TextView;                     // 카테고리 '음식교환' Image 아래의 TextView
    private TextView ThingText_TextView;                    // 카테고리 '물건교환' Image 아래의 TextView
    private TextView BorrowText_TextView;                   // 카테고리 '대여하기' Image 아래의 TextView
    private TextView Quest_TextView;                        // 카테고리 '퀘스트' Image 아래의 TextView
    private TextView Hot_TextView;                          // 카테고리 '핫게시물' Image 아래의 TextView
                                                        // 4. 파이어베이스 관련 선언
    private FirebaseFirestore Firebasefirestore;            // 파이어스토어
                                                        // 5. 기타 변수
    private boolean updating;                               // 정보를 받아오는 중인지 분별하는 boolean 변수
    private boolean topScrolled;                            // 상단으로 스크롤한 상태의 boolean 변수

    public Market_BottombarFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

       // 지역선택
        Spinner Market_Category_Spinner = (Spinner)view.findViewById(R.id.Local_Spinner);
        ArrayAdapter Market_Category_Adapter = ArrayAdapter.createFromResource(getContext(), R.array.Local, android.R.layout.simple_spinner_item);
        Market_Category_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Market_Category_Spinner.setAdapter(Market_Category_Adapter);

       // 핫게시물 Button, 카테고리(음식교환, 물건교환, 대여하기, 퀘스트) Button, 검색 Button find
        HotMarket_ImageView = (ImageView) view.findViewById(R.id.HotPostbtn);
        FoodMarket_ImageView = (ImageView) view.findViewById(R.id.FoodPostbtn);
        ThingMarket_ImageView = (ImageView) view.findViewById(R.id.LifePostbtn);
        BorrowMarket_ImageView = (ImageView) view.findViewById(R.id.BorrowPostbtn);
        QuestMarket_ImageView = (ImageView) view.findViewById(R.id.WorkPostbtn);
        SearchButton_ImageView = (ImageView) view.findViewById(R.id.searchbtn);

       // 핫게시물 Button, 카테고리(음식교환, 물건교환, 대여하기, 퀘스트) Button, 검색 Button setOnClickListener
        HotMarket_ImageView.setOnClickListener(onClickListener);
        FoodMarket_ImageView.setOnClickListener(onClickListener);
        ThingMarket_ImageView.setOnClickListener(onClickListener);
        BorrowMarket_ImageView.setOnClickListener(onClickListener);
        QuestMarket_ImageView.setOnClickListener(onClickListener);
        SearchButton_ImageView.setOnClickListener(onClickListener);

       // 핫게시물 Button, 카테고리(음식교환, 물건교환, 대여하기, 퀘스트) Button 아래의 TextView find
        FoodText_TextView = view.findViewById(R.id.FoodPostText);
        ThingText_TextView = view.findViewById(R.id.LifePostText);
        BorrowText_TextView = view.findViewById(R.id.BorrowPostText);
        Quest_TextView = view.findViewById(R.id.WorkPostText);
        Hot_TextView = view.findViewById(R.id.HotPostText);

       // 필터의 초기화는 '전체'로 핫게시물, 카테고리 상관없이 나열한다.
        State = "전체";

       // 파이어스토어
        Firebasefirestore = FirebaseFirestore.getInstance();

       // Marketmodel 초기화, 어댑터에 연결
        Marketmodel = new ArrayList<>();
        Marketadapter = new MarketAdapter(getActivity(), Marketmodel);
        Recyclerview = view.findViewById(R.id.recyclerView);
        Recyclerview.setHasFixedSize(true);
        Recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        Recyclerview.setAdapter(Marketadapter);
       // 스크롤 listener
        Recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                if(newState == 1 && firstVisibleItemPosition == 0){topScrolled = true; }
                if(newState == 0 && topScrolled){
                    JudgeState(true);
                    topScrolled = false;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                if(totalItemCount - 8 <= lastVisibleItemPosition && !updating){ JudgeState(false); }
                if(0 < firstVisibleItemPosition){ topScrolled = false; }
            }
        });

        All_MarketUpdate(false);

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
            All_MarketUpdate(clear);

        }else if(State == "핫게시판"){
            Hot_MarketUpdate(clear);
        }else if(State == "음식교환"){
            Food_MarketUpdate(clear);
        }else if(State == "물건교환"){
            Thing_MarketUpdate(clear);
        }else if(State == "대여하기"){
            Borrow_MarketUpdate(clear);
        }else if(State == "퀘스트"){
            Work_MarketUpdate(clear);
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.searchbtn:
                    myStartActivity(SearchActivity.class);
                    break;
                case R.id.HotPostbtn:
                    if(HotMarketbtn_State.equals("unSelected")){
                    HotMarket_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    Hot_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    FoodMarket_ImageView.setColorFilter(null);
                    FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                    ThingMarket_ImageView.setColorFilter(null);
                    ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                    BorrowMarket_ImageView.setColorFilter(null);
                    BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                    QuestMarket_ImageView.setColorFilter(null);
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    Marketmodel.clear();
                    Hot_MarketUpdate(true);
                    State = "핫게시판";
                    HotMarketbtn_State = "Selected";
                    }else if(HotMarketbtn_State.equals("Selected")){
                        FoodMarket_ImageView.setColorFilter(null);
                        FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        All_MarketUpdate(true);
                        State = "전체";
                        HotMarketbtn_State = "unSelected";
                    }
                    break;
                case R.id.FoodPostbtn:
                    //ImageView FoodPostbtn = (ImageView) view.findViewById(R.id.FoodPostbtn);
                    if(FoodMarketbtn_State.equals("unSelected")){
                        FoodMarket_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                        FoodText_TextView.setTextColor(Color.parseColor("#2fd8df"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        Food_MarketUpdate(true);
                        State = "음식교환";
                        FoodMarketbtn_State = "Selected";
                    }else if(FoodMarketbtn_State.equals("Selected")){
                        FoodMarket_ImageView.setColorFilter(null);
                        FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        All_MarketUpdate(true);
                        State = "전체";
                        FoodMarketbtn_State = "unSelected";
                    }

                    break;
                case R.id.LifePostbtn:
                    if(ThingMarketbtn_State.equals("unSelected")){
                    ThingMarket_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    ThingText_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    FoodMarket_ImageView.setColorFilter(null);
                    FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                    BorrowMarket_ImageView.setColorFilter(null);
                    BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                    QuestMarket_ImageView.setColorFilter(null);
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    HotMarket_ImageView.setColorFilter(null);
                    Hot_TextView.setTextColor(Color.parseColor("#000000"));
                    Marketmodel.clear();
                    Thing_MarketUpdate(true);
                    State = "물건교환";
                    ThingMarketbtn_State = "Selected";
                    } else if(ThingMarketbtn_State.equals("Selected")){
                        FoodMarket_ImageView.setColorFilter(null);
                        FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        All_MarketUpdate(true);
                        State = "전체";
                        ThingMarketbtn_State = "unSelected";
                    }
                    break;
                case R.id.BorrowPostbtn:
                    if(BorrowMarketbtn_State.equals("unSelected")){
                    BorrowMarket_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    BorrowText_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    FoodMarket_ImageView.setColorFilter(null);
                    FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                    ThingMarket_ImageView.setColorFilter(null);
                    ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                    QuestMarket_ImageView.setColorFilter(null);
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    HotMarket_ImageView.setColorFilter(null);
                    Hot_TextView.setTextColor(Color.parseColor("#000000"));
                    Marketmodel.clear();
                    Borrow_MarketUpdate(true);
                    State = "대여하기";
                    BorrowMarketbtn_State = "Selected";
                    } else if(BorrowMarketbtn_State.equals("Selected")){
                        FoodMarket_ImageView.setColorFilter(null);
                        FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        All_MarketUpdate(true);
                        State = "전체";
                        BorrowMarketbtn_State = "unSelected";
                    }
                    break;
                case R.id.WorkPostbtn:
                    if(QuestMarketbtn_State.equals("unSelected")){
                    QuestMarket_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    Quest_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    FoodMarket_ImageView.setColorFilter(null);
                    FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                    ThingMarket_ImageView.setColorFilter(null);
                    ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                    BorrowMarket_ImageView.setColorFilter(null);
                    BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                    HotMarket_ImageView.setColorFilter(null);
                    Hot_TextView.setTextColor(Color.parseColor("#000000"));
                    Marketmodel.clear();
                    Work_MarketUpdate(true);
                    State = "퀘스트";
                    QuestMarketbtn_State = "Selected";
                    } else if(QuestMarketbtn_State.equals("Selected")){
                        FoodMarket_ImageView.setColorFilter(null);
                        FoodText_TextView.setTextColor(Color.parseColor("#000000"));
                        ThingMarket_ImageView.setColorFilter(null);
                        ThingText_TextView.setTextColor(Color.parseColor("#000000"));
                        BorrowMarket_ImageView.setColorFilter(null);
                        BorrowText_TextView.setTextColor(Color.parseColor("#000000"));
                        QuestMarket_ImageView.setColorFilter(null);
                        Quest_TextView.setTextColor(Color.parseColor("#000000"));
                        HotMarket_ImageView.setColorFilter(null);
                        Hot_TextView.setTextColor(Color.parseColor("#000000"));
                        Marketmodel.clear();
                        All_MarketUpdate(true);
                        State = "전체";
                        QuestMarketbtn_State = "unSelected";
                    }
                    break;
            }
        }
    };

    private void All_MarketUpdate(final boolean clear) {
        updating = true;
        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).limit(10).get()        // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                        )
                                );
                        }
                            Marketadapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Hot_MarketUpdate(final boolean clear) {
        updating = true;

        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_HotMarket","O").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int HotMarketCut;
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                HotMarketCut = ((ArrayList<String>) document.getData().get("MarketModel_LikeList")).size();
                                if(HotMarketCut>0) {
                                    Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                            Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                    ));
                                }
                                HotMarketCut = 0;
                            }
                            Marketadapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Food_MarketUpdate(final boolean clear) {
        updating = true;

        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_Category","음식교환").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                ));
                            }
                            Marketadapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Thing_MarketUpdate(final boolean clear) {
        updating = true;

        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_Category","물건교환").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String Thing;
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                ));
                            }
                            Marketadapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Borrow_MarketUpdate(final boolean clear) {
        updating = true;

        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_Category","대여하기").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String Borrow;
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                ));
                            }
                            Marketadapter.notifyDataSetChanged();
                        } else {
                            //Log.d("로그","실패?");
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void Work_MarketUpdate(final boolean clear) {
        updating = true;

        Date date = Marketmodel.size() == 0 || clear ? new Date() : Marketmodel.get(Marketmodel.size() - 1).getMarketModel_DateOfManufacture();  //part21 : 사이즈가 없으면 현재 날짜 아니면 최근 말짜의 getCreatedAt로 지정 (27'40")
        CollectionReference collectionReference = Firebasefirestore.collection("MARKETS");                // 파이어베이스의 posts에서
        collectionReference.orderBy("MarketModel_DateOfManufacture", Query.Direction.DESCENDING).whereLessThan("MarketModel_DateOfManufacture", date).whereEqualTo("MarketModel_Category","퀘스트").limit(10).get()  // post14: 게시물을 날짜 기준으로 순서대로 나열 (23'40") // part21 : 날짜기준으로 10개  collectionReference.whereGreaterThanOrEqualTo("title",  search).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String Work;
                        if (task.isSuccessful()) {
                            if(clear){                      //part22 : clear를 boolean으로 써서 업데이트 도중에 게시물 클릭시 발생하는 오류 해결 (3'30")   // part15 : MainAdapter에서 setOnClickListener에서 시작 (35'30")
                                Marketmodel.clear();                                                           // part16 : List 안의 데이터 초기화
                            }                                                                               // part16 : postsUpdate로 이동 (15'50")
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Marketmodel.add(new MarketModel(                                                          //postList로 데이터를 넣는다.
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
                                        Integer.parseInt(String.valueOf(document.getData().get("MarketModel_CommentCount")))                                ));
                            }
                            Marketadapter.notifyDataSetChanged();
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

