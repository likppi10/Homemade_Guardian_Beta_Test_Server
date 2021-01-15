package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.FirebaseHelper;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Loding_Dialog;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.listener.OnPostListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.Community_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.Market_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.PhotoUtil;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 게시물의 수정을 위한 액티비티 이다.
//      Ex) 게시물에서 수정을 눌렀을 때 실행되는 액티비티이다.

public class ModifyMarketActivity extends BasicActivity {                       // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
    Loding_Dialog dialog = new Loding_Dialog(this);                 // 로딩 액티비티
                                                                                // 2. 변수 및 배열
    private MarketModel Marketmodel;                                                // Marketmodel 선언
    public  ArrayList<String> ArrayList_SelectedPhoto = new ArrayList<>();          // 선택한 이미지들이 담기는 리스트
    private int PathCount;                                                          // ArrayList_SelectedPhoto의 count 변수
    private String Category;                                                        // 게시물의 카테고리
    private String ChangeState;                                                     // 앨범에 들어간 적이 있는지
                                                                                // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private RelativeLayout LoaderLayout;                                            // 로딩중을 나타내는 layout 선언
    private ImageView FoodCategory_ImageView;                                       // '음식교환' 카테고리
    private ImageView ThingCategory_ImageView;                                      // '물건교환' 카테고리
    private ImageView BorrowCategory_ImageView;                                     // '대여하기' 카테고리
    private ImageView QuestCategory_ImageView;                                      // '퀘스트' 카테고리
    private ImageView PhotoList0, PhotoList1, PhotoList2, PhotoList3, PhotoList4;   // 수정 화면의 게시물 사진이 보여지는 ImageView
    private TextView Image_Count_TextView;                                          // 선택된 이미지의 개수
    private TextView Food_TextView, Thing_TextView, Borrow_TextView, Quest_TextView;// 카테고리 이미지 아래에 있는 카테고리 이름
    private EditText Title_EditText;                                                // 게시물의 제목
    private EditText TextContents_EditText;                                         // 게시물의 내용
                                                                                // 4. 파이어베이스 관련 선언
    private FirebaseUser CurrentUser;                                               // 현재 사용자
    private StorageReference Storagereference;                                      // 파이어스토리지에 접근하기 위한 선언
    private FirebaseHelper Firebasehelper;                                          // FirebaseHelper 선언
                                                                                // 5.기타 변수
    public final static int REQUEST_CODE = 1;                                       // REQUEST_CODE 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_market);
        setToolbarTitle("");

       // 진행중 레이아웃, 게시물의 내용 EditText, 게시물의 제목 EditText find
        LoaderLayout = findViewById(R.id.Loader_Lyaout);
        Title_EditText = findViewById(R.id.Post_Title_EditText);
        TextContents_EditText = findViewById(R.id.Market_Content_EditText);

       // 선택된 이미지의 ImageView find
        PhotoList0 = (ImageView)findViewById(R.id.PhotoList0);
        PhotoList1 = (ImageView)findViewById(R.id.PhotoList1);
        PhotoList2 = (ImageView)findViewById(R.id.PhotoList2);
        PhotoList3 = (ImageView)findViewById(R.id.PhotoList3);
        PhotoList4 = (ImageView)findViewById(R.id.PhotoList4);

       // 카테고리 별 이미지 ImageView find
        FoodCategory_ImageView = (ImageView) findViewById(R.id.FoodPostbtn);
        ThingCategory_ImageView = (ImageView) findViewById(R.id.LifePostbtn);
        BorrowCategory_ImageView = (ImageView) findViewById(R.id.BorrowPostbtn);
        QuestCategory_ImageView = (ImageView) findViewById(R.id.WorkPostbtn);

       // 카테고리 별 이미지 setOnClickListener
        FoodCategory_ImageView.setOnClickListener(onClickListener);
        ThingCategory_ImageView.setOnClickListener(onClickListener);
        BorrowCategory_ImageView.setOnClickListener(onClickListener);
        QuestCategory_ImageView.setOnClickListener(onClickListener);

       // 카테고리의 이름 TextView find
        Food_TextView = findViewById(R.id.FoodPostText);
        Thing_TextView = findViewById(R.id.LifePostText);
        Borrow_TextView = findViewById(R.id.BorrowPostText);
        Quest_TextView = findViewById(R.id.WorkPostText);

       // Toolbar에 속성된 뒤로가기 Button, 앨범 들어가기 Button, 수정하기 Button, 선택된 이미지 카운트 TextView find
        findViewById(R.id.back_Button).setOnClickListener(onClickListener);
        findViewById(R.id.camera_Button_Layout).setOnClickListener(onClickListener);
        findViewById(R.id.Write_Register_Button).setOnClickListener(onClickListener);
        Image_Count_TextView = (TextView) findViewById(R.id.camera_Select_Text);

       // 파이어베이스 스토리지 권한
        FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();
        Storagereference = Firebasestorage.getReference();

       // 현재 게시물의  Marketmodel getIntent
        Marketmodel = (MarketModel) getIntent().getSerializableExtra("marketInfo");

        Firebasehelper = new FirebaseHelper(this);
        Firebasehelper.setOnpostlistener(onPostListener);

       // 수정하려는 게시물의 내용 구성
        MarketInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = null;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.circleCropTransform();
        requestOptions.transforms( new CenterCrop(),new RoundedCorners(25));

       // if : 설정한 resultcode와 requestCode라면 PhotoList에 선택된 이미지를 넣겠다.
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // 기존 이미지 지우기
            PhotoList0.setImageResource(0);
            PhotoList1.setImageResource(0);
            PhotoList2.setImageResource(0);
            PhotoList3.setImageResource(0);
            PhotoList4.setImageResource(0);
            ArrayList_SelectedPhoto = new ArrayList<>();
            ChangeState = "앨범O";

           // PhotoPickerActivity에서 받은 data를 photos에 넣겠다.
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            if (photos != null || ArrayList_SelectedPhoto !=null) {
                ArrayList_SelectedPhoto.addAll(photos);
                for(int i=0;i<photos.size();i++) {
                    switch (i) {
                        case 0:
                            Glide.with(this).load(photos.get(0)).apply(requestOptions).into(PhotoList0);
                            break;
                        case 1:
                            Glide.with(this).load(photos.get(1)).apply(requestOptions).into(PhotoList1);
                            break;
                        case 2:
                            Glide.with(this).load(photos.get(2)).apply(requestOptions).into(PhotoList2);
                            break;
                        case 3:
                            Glide.with(this).load(photos.get(3)).apply(requestOptions).into(PhotoList3);
                            break;
                        case 4:
                            Glide.with(this).load(photos.get(4)).apply(requestOptions).into(PhotoList4);
                            break;
                    }
                }
               // 선택된 이미지의 개수를 size로 count
                Image_Count_TextView.setText(photos.size()+"/5");
            }
            if(photos.size() == 0){
                ArrayList_SelectedPhoto = null;
                Image_Count_TextView.setText(photos.size()+"/5");
            }
        }
    }

   // 수정하고자 하는 게시물의 현재 정보 구성
    private void MarketInit() {

       // Marketmodel이 null이 아니어야 함
        if (Marketmodel != null) {
            ChangeState = "앨범X";
           // 게시물의 제목, 내용 set
            Title_EditText.setText(Marketmodel.getMarketModel_Title());
            TextContents_EditText.setText(Marketmodel.getMarketModel_Text());

           // 게시물에 등록 되어 있던 이미지 set
            ArrayList_SelectedPhoto = Marketmodel.getMarketModel_ImageList();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.circleCropTransform();
            requestOptions.transforms( new CenterCrop(),new RoundedCorners(25));

            GradientDrawable drawable= (GradientDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.round);
            if (ArrayList_SelectedPhoto != null) {
                for(int i=0;i<ArrayList_SelectedPhoto.size();i++){
                    switch (i){
                        case 0 :
                            Glide.with(this).load(ArrayList_SelectedPhoto.get(0)).apply(requestOptions).into(PhotoList0);
                            break;
                        case 1 :
                            Glide.with(this).load(ArrayList_SelectedPhoto.get(1)).apply(requestOptions).into(PhotoList1);
                            break;
                        case 2 :
                            Glide.with(this).load(ArrayList_SelectedPhoto.get(2)).apply(requestOptions).into(PhotoList2);
                            break;
                        case 3 :
                            Glide.with(this).load(ArrayList_SelectedPhoto.get(3)).apply(requestOptions).into(PhotoList3);
                            break;
                        case 4 :
                            Glide.with(this).load(ArrayList_SelectedPhoto.get(4)).apply(requestOptions).into(PhotoList4);
                            break;
                    }
                }
               // 현재 이미지의 개수 set
                Image_Count_TextView.setText(ArrayList_SelectedPhoto.size()+"/5");
            }

            // 게시물의 카테고리를 가져와 해당 카테고리 이미지와 텍스트에 setcolorfilter
            Category = Marketmodel.getMarketModel_Category();
            if(Category.equals("음식교환")){
                FoodCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                Food_TextView.setTextColor(Color.parseColor("#2fd8df"));
            }else if(Category.equals("물건교환")){
                ThingCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                Thing_TextView.setTextColor(Color.parseColor("#2fd8df"));
            }else if(Category.equals("대여하기")){
                BorrowCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                Borrow_TextView.setTextColor(Color.parseColor("#2fd8df"));
            }else if(Category.equals("퀘스트")){
                QuestCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                Quest_TextView.setTextColor(Color.parseColor("#2fd8df"));
            }
        }
    }

   // Activity에서 사용하는 버튼들의 OnClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

               // 클릭시 : 클릭한 카테고리 ImageView, Text ColorFilter 설정 다른 카테고리는 초기화 / Category = 현재 카테고리로
                case R.id.FoodPostbtn:
                    FoodCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    ThingCategory_ImageView.setColorFilter(null);
                    BorrowCategory_ImageView.setColorFilter(null);
                    QuestCategory_ImageView.setColorFilter(null);
                    Food_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    Thing_TextView.setTextColor(Color.parseColor("#000000"));
                    Borrow_TextView.setTextColor(Color.parseColor("#000000"));
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    Category = "음식교환";
                    break;

               // 클릭시 : 클릭한 카테고리 ImageView, Text ColorFilter 설정 다른 카테고리는 초기화 / Category = 현재 카테고리로
                case R.id.LifePostbtn:
                    ThingCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    FoodCategory_ImageView.setColorFilter(null);
                    BorrowCategory_ImageView.setColorFilter(null);
                    QuestCategory_ImageView.setColorFilter(null);
                    Food_TextView.setTextColor(Color.parseColor("#000000"));
                    Thing_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    Borrow_TextView.setTextColor(Color.parseColor("#000000"));
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    Category = "물건교환";
                    break;

                // 클릭시 : 클릭한 카테고리 ImageView, Text ColorFilter 설정 다른 카테고리는 초기화 / Category = 현재 카테고리로
                case R.id.BorrowPostbtn:
                    BorrowCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    FoodCategory_ImageView.setColorFilter(null);
                    ThingCategory_ImageView.setColorFilter(null);
                    QuestCategory_ImageView.setColorFilter(null);
                    Food_TextView.setTextColor(Color.parseColor("#000000"));
                    Thing_TextView.setTextColor(Color.parseColor("#000000"));
                    Borrow_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    Quest_TextView.setTextColor(Color.parseColor("#000000"));
                    Category = "대여하기";
                    break;

                // 클릭시 : 클릭한 카테고리 ImageView, Text ColorFilter 설정 다른 카테고리는 초기화 / Category = 현재 카테고리로
                case R.id.WorkPostbtn:
                    QuestCategory_ImageView.setColorFilter(Color.parseColor("#2fd8df"), PorterDuff.Mode.SRC_IN);
                    FoodCategory_ImageView.setColorFilter(null);
                    ThingCategory_ImageView.setColorFilter(null);
                    BorrowCategory_ImageView.setColorFilter(null);
                    Food_TextView.setTextColor(Color.parseColor("#000000"));
                    Thing_TextView.setTextColor(Color.parseColor("#000000"));
                    Borrow_TextView.setTextColor(Color.parseColor("#000000"));
                    Quest_TextView.setTextColor(Color.parseColor("#2fd8df"));
                    Category = "퀘스트";
                    break;

               // 뒤로가기 누르면 게시물 화면으로 이동
                case R.id.back_Button:
                    myStartActivity(MarketActivity.class, Marketmodel);
                    break;

               // 앨범으로 이동
                case R.id.camera_Button_Layout:

                    PhotoUtil intent2 = new PhotoUtil(getApplicationContext());
                    intent2.setMaxSelectCount(5);
                    intent2.setShowCamera(true);
                    intent2.setShowGif(true);
                    intent2.setSelectCheckBox(false);
                    intent2.setMaxGrideItemCount(3);
                    startActivityForResult(intent2, REQUEST_CODE);
                    break;

               // 수정 완료 버튼  : 수정사항 적용하는 함수로 이동
                case R.id.Write_Register_Button:
                    Modify_Storage_Upload();
                    break;
            }
        }
    };

   // WriteMarketActivity와 비슷한 형태로 차이점이 있다면, docRef_POSTS_PostUid에 새로운 Uid를 생성 받는 것이 아니라 수정하고자하는 게시물의 Uid를 받아서 쓴다.
    private void Modify_Storage_Upload() {

       // 게시물의 제목, 게시물의 내용, 게시물의 Uid, 좋아요 list, 핫게시물의 유무, 예약 유무, 거래완료 유무 설정
        final String Title = ((EditText) findViewById(R.id.Post_Title_EditText)).getText().toString();
        final String TextContents = ((EditText) findViewById(R.id.Market_Content_EditText)).getText().toString();
        String Market_Uid = Marketmodel.getMarketModel_Market_Uid();
        final ArrayList<String> LikeList = Marketmodel.getMarketModel_LikeList();
        final String HotMarket = Marketmodel.getMarketModel_HotMarket();
        final String MarketModel_reservation = Marketmodel.getMarketModel_reservation();
        final String MarketModel_deal = Marketmodel.getMarketModel_deal();

       // if : 제목이 작성되었다면, 진행
        if (Title.length() > 0) {

            // 등록이 시작되면 다른 이벤트를 방지하기 위해서 Dialog를 활성화한다.
            dialog.callDialog();

           // 파이어베이스 관련
            FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();
            Storagereference = Firebasestorage.getReference();
            FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();

           // 작성자의 Uid
           // Market의 기존 Uid 받아놓음
           // Market의 기존 작성일 받아놓음 : 날짜를 새로 받지 않아야 기존의 게시물보다 위로 올라가지 않게 할수 있다.
           // Market의 기존 댓글의 개수를 받아놓음
            CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            final DocumentReference docRef_MARKETS_MarketUid = Firebasefirestore.collection("MARKETS").document(Market_Uid);
            final Date DateOfManufacture = Marketmodel.getMarketModel_DateOfManufacture();
            final int commentcount = Marketmodel.getMarketModel_CommentCount();
           // 이미지의 수정사항이 있을 수 있으므로 이미지 리스트를 초기화한다.
            final ArrayList<String> Modify_Image_List = new ArrayList<>();

            // if : 이미지가 있다. / else : 이미지가 없다.
            //1. 원래 사진 O, 앨범 들어간 적 O, [[사진 O]] +뒤로가기 or 완료
            //2. 원래 사진 O, 앨범 들어간 적 X, [[사진 O]]
            //3. 원래 사진 X, 앨범 들어간 적 O, [[사진 O]]
            if (ArrayList_SelectedPhoto != null) {
                //2. 원래 사진 O, 앨범 들어간 적 X, [[사진 O]]
                if(ChangeState.equals("앨범X")){
                    MarketModel marketModel = new MarketModel(Title, TextContents,ArrayList_SelectedPhoto, DateOfManufacture, CurrentUser.getUid(), Market_Uid, Category, LikeList, HotMarket, MarketModel_reservation, MarketModel_deal, commentcount);
                    marketModel.setMarketModel_Title(Title);
                    marketModel.setMarketModel_Text(TextContents);
                    marketModel.setMarketModel_Category(Category);
                    Modify_Store_Upload(docRef_MARKETS_MarketUid, marketModel);
                }
                //1. 원래 사진 O, 앨범 들어간 적 O, [[사진 O]] +뒤로가기
                //3. 원래 사진 X, 앨범 들어간 적 O, [[사진 O]]
                else{
                    for (int i = 0; i < ArrayList_SelectedPhoto.size(); i++) {
                        // 스토리지의 경로 설정
                        String path = ArrayList_SelectedPhoto.get(PathCount);
                        Modify_Image_List.add(path);
                        String[] pathArray = path.split("\\.");
                        final StorageReference ImagesRef_MARKETS_Uid_PathCount = Storagereference.child("MARKETS/" + docRef_MARKETS_MarketUid.getId() + "/" + PathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            // 스토리지에 등록
                            InputStream Stream = new FileInputStream(new File(ArrayList_SelectedPhoto.get(PathCount)));
                            StorageMetadata Metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (Modify_Image_List.size() - 1)).build();
                            UploadTask Uploadtask = ImagesRef_MARKETS_Uid_PathCount.putStream(Stream, Metadata);

                            // Market의 기존 Uid 받아오고, ImageList 초기화
                            final String Get_MarketUid = Market_Uid;
                            Marketmodel.setMarketModel_ImageList(new ArrayList<String>());

                            // 몇번째 이미지까지 등록되었나 지표 / 등록이 다 되었다면 스토어 추가로 이동된다.
                            final int finalI = i;
                            Uploadtask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    ImagesRef_MARKETS_Uid_PathCount.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            // 새로운 Image 및 marketModel 구성 후 스토어 추가로 이동한다.
                                            Modify_Image_List.set(index, uri.toString());
                                            MarketModel marketModel = new MarketModel(Title, TextContents, Modify_Image_List, DateOfManufacture,
                                                    CurrentUser.getUid(), Get_MarketUid, Category, LikeList, HotMarket, MarketModel_reservation, MarketModel_deal, commentcount);
                                            marketModel.setMarketModel_Market_Uid(Get_MarketUid);
                                            if (finalI == ArrayList_SelectedPhoto.size() - 1) {
                                                Modify_Store_Upload(docRef_MARKETS_MarketUid, marketModel);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        // 다음 이미지로 넘어가기 위한 PathCount 증가
                        PathCount++;
                    }
                }
            }
            //1. 원래 사진 O, 앨범 들어간 적 O, [[사진 X]]
            //2. 원래 사진 X, 앨범 들어간 적 O, [[사진 X]] +뒤로가기 or 완료
            //3. 원래 사진 X, 앨범 들어간 적 X, [[사진 X]]
            else {
                MarketModel marketModel = new MarketModel(Title, TextContents,null, DateOfManufacture, CurrentUser.getUid(), Market_Uid, Category, LikeList, HotMarket, MarketModel_reservation, MarketModel_deal, commentcount);
                marketModel.setMarketModel_Market_Uid(Market_Uid);
                Firebasehelper.Market_Storagedelete(Marketmodel,"modify");
                Modify_Store_Upload(docRef_MARKETS_MarketUid, marketModel);
            }
        } else {
           // 제목이 없을 경우
            if(Title.length() == 0){
                Toast.makeText(this, "제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

   // 파이어스토어에 바뀐 정보들을 Market에 넣는다. WriteMaerketActivity에 있는 것과는 차이가 없다.
    private void Modify_Store_Upload(DocumentReference docRef_MARKETS_MarketUid, final MarketModel marketmodel) {
        docRef_MARKETS_MarketUid.set(marketmodel.getMarketInfo())
               // data set이 성공한다면, LoaderLayout 사라지게 하고, finish()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        LoaderLayout.setVisibility(View.GONE);
                        Intent Resultintent = new Intent();
                        Resultintent.putExtra("marketInfo", marketmodel);
                        setResult(Activity.RESULT_OK, Resultintent);
                        dialog.calldismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.calldismiss();
                    }
                });
    }

    // 파이어베이스 헬퍼의 listener
    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(MarketModel marketModel) {
            Log.e("로그 ","삭제 성공");
        }
        @Override
        public void oncommentDelete(Market_CommentModel market_commentModel) { Log.e("로그 ","댓글 삭제 성공"); }
        @Override
        public void oncommunityDelete(CommunityModel communityModel) { }
        @Override
        public void oncommnitycommentDelete(Community_CommentModel community_commentModel) { }
        @Override
        public void onModify() {
            Log.e("로그 ","수정 성공");
        }
    };

    private void myStartActivity(Class c, MarketModel marketModel) {
        Intent Intent_Market_Data = new Intent(this, c);
        Intent_Market_Data.putExtra("marketInfo", marketModel);
        startActivityForResult(Intent_Market_Data, 0);
        finish();
    }
}