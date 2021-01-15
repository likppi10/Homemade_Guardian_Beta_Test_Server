package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Loding_Dialog;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.PhotoUtil;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

// 게시물 중에 Market 게시물을 작성하는 액티비티이다.

public class WriteMarketActivity extends BasicActivity {                        // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                                // 1. 클래스
    private Loding_Dialog dialog = new Loding_Dialog(this);                 // 로딩 액티비티
                                                                                // 2. 변수 및 배열
    private MarketModel Marketmodel;                                                // Marketmodel 선언
    private int PathCount;                                                          // ArrayList_SelectedPhoto의 count 변수
    private String Category;                                                        // 게시물의 카테고리
    private ArrayList<String> ArrayList_SelectedPhoto = new ArrayList<>();          // 선택한 이미지들이 담기는 리스트
                                                                                // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private ImageView PhotoList0, PhotoList1, PhotoList2, PhotoList3, PhotoList4;   // 수정 화면의 게시물 사진이 보여지는 ImageView
    private ImageView FoodCategory_ImageView;                                       // '음식교환' 카테고리
    private ImageView ThingCategory_ImageView;                                      // '물건교환' 카테고리
    private ImageView BorrowCategory_ImageView;                                     // '대여하기' 카테고리
    private ImageView QuestCategory_ImageView;                                      // '퀘스트' 카테고리
    private TextView Image_Count_TextView;                                          // 선택된 이미지의 개수
    private TextView Food_TextView, Thing_TextView, Borrow_TextView, Quest_TextView;// 카테고리 이미지 아래에 있는 카테고리 이름
                                                                                // 5. 기타 변수
    public final static int REQUEST_CODE = 1;                                       // REQUEST_CODE 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writemarket);
        setToolbarTitle("");

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

       // 카테고리 별 이미지 ImageView find
        Food_TextView = findViewById(R.id.FoodPostText);
        Thing_TextView = findViewById(R.id.LifePostText);
        Borrow_TextView = findViewById(R.id.BorrowPostText);
        Quest_TextView = findViewById(R.id.WorkPostText);

       // Toolbar에 속성된 뒤로가기 Button, 앨범 들어가기 Button, 수정하기 Button, 선택된 이미지 카운트 TextView find
        findViewById(R.id.back_Button).setOnClickListener(onClickListener);
        findViewById(R.id.camera_Button_Layout).setOnClickListener(onClickListener);
        findViewById(R.id.Write_Register_Button).setOnClickListener(onClickListener);
        Image_Count_TextView = (TextView) findViewById(R.id.camera_Select_Text);
    }

//    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
//        Bitmap output = Bitmap.createBitmap(70,70, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(70, 70, 70, 70);
//        final RectF rectF = new RectF(rect);
//        final float roundPx = 12;
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        return output;
//    }
//    public static Bitmap StringToBitmap(String encodedString) {
//        try {
//            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
//
//    /*
//     * Bitmap을 String형으로 변환
//     * */
//    public static String BitmapToString(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
//        byte[] bytes = baos.toByteArray();
//        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
//        return temp;
//    }



    public static BitmapImageViewTarget getRoundedImageTarget(@NonNull final Context context, @NonNull final ImageView imageView, final float radius) {
        return new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(final Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(radius);
                imageView.setImageDrawable(circularBitmapDrawable);
                }
            };
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

           // PhotoPickerActivity에서 받은 data를 photos에 넣겠다.
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            if (photos != null || ArrayList_SelectedPhoto !=null) {
                ArrayList_SelectedPhoto.addAll(photos);
                for(int i=0;i<photos.size();i++){
                    switch (i){
                        case 0 :
                            Glide.with(this).load(photos.get(0)).apply(requestOptions).into(PhotoList0);
                           break;
                        case 1 :
                            Glide.with(this).load(photos.get(1)).apply(requestOptions).into(PhotoList1);
                            break;
                        case 2 :
                            Glide.with(this).load(photos.get(2)).apply(requestOptions).into(PhotoList2);
                            break;
                        case 3 :
                            Glide.with(this).load(photos.get(3)).apply(requestOptions).into(PhotoList3);
                            break;
                        case 4 :
                            Glide.with(this).load(photos.get(4)).apply(requestOptions).into(PhotoList4);
                            break;
                    }
                }
               // 선택된 이미지의 개수를 size로 count
                Image_Count_TextView.setText(photos.size()+"/5");
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
                    finish();
                    break;

               // 앨범으로 이동
                case R.id.camera_Button_Layout:
                    ArrayList_SelectedPhoto = new ArrayList<>();
                    PhotoUtil intent2 = new PhotoUtil(getApplicationContext());
                    intent2.setMaxSelectCount(5);
                    intent2.setShowCamera(true);
                    intent2.setShowGif(true);
                    intent2.setSelectCheckBox(false);
                    intent2.setMaxGrideItemCount(3);
                    startActivityForResult(intent2, REQUEST_CODE);
                    break;

               // 작성 완료 버튼  : 작성 내용 등록 함수로 이동
                case R.id.Write_Register_Button:
                    storageUpload();
                    break;
            }
        }
    };

   // 작성 내용 등록 함수 : 스토리지에 사진 등록
    private void storageUpload() {

       // 게시물의 제목, 게시물의 내용, 좋아요 리스트, 핫게시물의 유무, 예약 유무, 거래완료 유무, 댓글 개수, 작성자 Uid 설정
        final String Market_Title_EditText = ((EditText) findViewById(R.id.Market_Title_EditText)).getText().toString();
        final String Market_Content_EditText = ((EditText) findViewById(R.id.Market_Content_EditText)).getText().toString();
        final ArrayList<String> LikeList = new ArrayList<>();
        final String HotMarket = "X";
        final String MarketModel_reservation = "X";
        final String MarketModel_deal = "X";
        final int market_commentcount = 0;
        final String CurrentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

       // if : 제목이 작성되거나 카테고리가 선택되었다면, 진행
        if (Market_Title_EditText.length() > 0 && Category != null) {
            String MarketID = null;

           // 등록이 시작되면 다른 이벤트를 방지하기 위해서 Dialog를 활성화한다.
            dialog.callDialog();

           // 이미지의 수정사항이 있을 수 있으므로 이미지 리스트를 초기화한다.
            final ArrayList<String> Image_List = new ArrayList<>();

           // 파이어베이스 관련
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

           // Market의 Uid를 임의로 발생시켜 저장한다.
            MarketID = firebaseFirestore.collection("MARKETS").document().getId();
            final DocumentReference documentReference =firebaseFirestore.collection("MARKETS").document(MarketID);
            final Date date = new Date();
            for (int i = 0; i < ArrayList_SelectedPhoto.size(); i++) {

               // 스토리지의 경로 설정
                String path = ArrayList_SelectedPhoto.get(PathCount);
                Image_List.add(path);
                String[] pathArray = path.split("\\.");
                final StorageReference mountainImagesRef = storageRef.child("MARKETS/" + documentReference.getId() + "/" + PathCount + "." + pathArray[pathArray.length - 1]);
                try {
                   // 스토리지에 등록
                    InputStream stream = new FileInputStream(new File(ArrayList_SelectedPhoto.get(PathCount)));
                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (Image_List.size() - 1)).build();
                    UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);

                   // Market의 기존 Uid 받음
                    final String newMarketID = MarketID;

                   // 몇번째 이미지까지 등록되었나 지표 / 등록이 다 되었다면 스토어 추가로 이동된다.
                    final int finalI = i;
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) { }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final int index =  Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                            mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Image_List.set(index, uri.toString());

                                   // marketModel 구성 후 스토어 추가로 이동한다.
                                    MarketModel marketModel = new MarketModel(Market_Title_EditText, Market_Content_EditText, Image_List,
                                            date, CurrentUid, newMarketID, Category, LikeList, HotMarket,MarketModel_reservation,MarketModel_deal, market_commentcount);
                                    marketModel.setMarketModel_Market_Uid(newMarketID);
                                    if(finalI == ArrayList_SelectedPhoto.size()-1){
                                        storeUpload(documentReference, marketModel);
                                    }
                                }
                            });
                        }
                    });
                } catch (FileNotFoundException e) {
                }
               // 다음 이미지로 넘어가기 위한 PathCount 증가
                PathCount++;
            }
           // 이미지가 없고 스토어 추가로 이동한다.
            if (ArrayList_SelectedPhoto.size() == 0) {
                Marketmodel = new MarketModel(Market_Title_EditText, Market_Content_EditText, date, CurrentUid, MarketID, Category, LikeList,
                        HotMarket,MarketModel_reservation,MarketModel_deal, market_commentcount);
                Marketmodel.setMarketModel_Market_Uid(MarketID);
                storeUpload(documentReference, Marketmodel);
            }
        }
       // 제목이 없거나 카테고리가 정해지지 않았을 경우
        else {
            if(Market_Title_EditText.length() == 0){
                Toast.makeText(this.getApplicationContext(), "제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }else if(Category == null){
                Toast.makeText(this.getApplicationContext(), "카테고리를 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

   // 파이어스토어에 바뀐 정보들을 Market에 넣는다.
    private void storeUpload(DocumentReference documentReference, final MarketModel marketModel) {
        documentReference.set(marketModel.getMarketInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.calldismiss();
                        finish();
                        dialog.calldismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { }
                });
    }

   // 뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}