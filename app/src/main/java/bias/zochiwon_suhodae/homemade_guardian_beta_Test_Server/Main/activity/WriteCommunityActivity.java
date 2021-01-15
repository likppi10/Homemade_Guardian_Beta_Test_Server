package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Loding_Dialog;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
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

// 게시물 중에 Community 게시물을 작성하는 액티비티이다.

public class WriteCommunityActivity extends BasicActivity {                     // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                                                // 1. 클래스
    Loding_Dialog dialog = new Loding_Dialog(this);                         // 로딩 액티비티
                                                                                // 2. 변수 및 배열
    private CommunityModel Communitymodel;                                          // Communitymodel 선언
    private int PathCount;                                                          // ArrayList_SelectedPhoto의 count 변수
    private ArrayList<String> ArrayList_SelectedPhoto = new ArrayList<>();          // 선택한 이미지들이 담기는 리스트
                                                                                // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private ImageView PhotoList0, PhotoList1, PhotoList2, PhotoList3, PhotoList4;   // 수정 화면의 게시물 사진이 보여지는 ImageView
    private TextView Image_Count_TextView;                                          // 선택된 이미지의 개수
                                                                                // 5. 기타 변수
    public final static int REQUEST_CODE = 1;                                       // REQUEST_CODE 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecommunity);
        setToolbarTitle("");

       // 선택된 이미지의 ImageView find
        PhotoList0 = (ImageView)findViewById(R.id.PhotoList0);
        PhotoList1 = (ImageView)findViewById(R.id.PhotoList1);
        PhotoList2 = (ImageView)findViewById(R.id.PhotoList2);
        PhotoList3 = (ImageView)findViewById(R.id.PhotoList3);
        PhotoList4 = (ImageView)findViewById(R.id.PhotoList4);

       // Toolbar에 속성된 뒤로가기 Button, 앨범 들어가기 Button, 수정하기 Button, 선택된 이미지 카운트 TextView find
        findViewById(R.id.back_Button).setOnClickListener(onClickListener);
        findViewById(R.id.camera_Button_Layout).setOnClickListener(onClickListener);
        findViewById(R.id.Write_Register_Button).setOnClickListener(onClickListener);
        Image_Count_TextView = (TextView) findViewById(R.id.camera_Select_Text);
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

       // 게시물의 제목, 게시물의 내용, 좋아요 리스트, 핫게시물의 유무, 댓글 개수, 작성자 Uid 설정
        final String Community_Title_EditText = ((EditText) findViewById(R.id.Community_Title_EditText)).getText().toString();
        final String Community_Content_EditText = ((EditText) findViewById(R.id.Community_Content_EditText)).getText().toString();
        final ArrayList<String> LikeList = new ArrayList<>();
        final String HotCommunity = "X";
        final int community_commentcount = 0;
        final String CurrentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

       // if : 제목이 작성되었다면, 진행
        if (Community_Title_EditText.length() > 0) {
            String CommunityID = null;

           // 등록이 시작되면 다른 이벤트를 방지하기 위해서 Dialog를 활성화한다.
            dialog.callDialog();

           // 이미지의 수정사항이 있을 수 있으므로 이미지 리스트를 초기화한다.
            final ArrayList<String> Image_List = new ArrayList<>();

           // 파이어베이스 관련
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

           // Community의 Uid를 임의로 발생시켜 저장한다.
            CommunityID = firebaseFirestore.collection("COMMUNITY").document().getId();

            final DocumentReference documentReference =firebaseFirestore.collection("COMMUNITY").document(CommunityID);     //postInfo가 null이면 그냥 추가 되고 아니면 해당 아게시물 아이디에 해당하는 것으로 추가
            final Date date = new Date();
            for (int i = 0; i < ArrayList_SelectedPhoto.size(); i++) {

               // 스토리지의 경로 설정
                String path = ArrayList_SelectedPhoto.get(PathCount);
                Image_List.add(path);
                String[] pathArray = path.split("\\.");

                final StorageReference mountainImagesRef = storageRef.child("COMMUNITY/" + documentReference.getId() + "/" + PathCount + "." + pathArray[pathArray.length - 1]);
                try {
                   // 스토리지에 등록
                    InputStream stream = new FileInputStream(new File(ArrayList_SelectedPhoto.get(PathCount)));
                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (Image_List.size() - 1)).build();
                    UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);

                   // Community의 기존 Uid 받음
                    final String newCommunityID = CommunityID;

                   // 몇번째 이미지까지 등록되었나 지표 / 등록이 다 되었다면 스토어 추가로 이동된다.
                    final int finalI = i;
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                            mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Image_List.set(index, uri.toString());

                                   // communityModel 구성 후 스토어 추가로 이동한다.
                                    CommunityModel communityModel = new CommunityModel(Community_Title_EditText, Community_Content_EditText, Image_List,  date, CurrentUid, newCommunityID, LikeList, HotCommunity, community_commentcount);
                                    communityModel.setCommunityModel_Community_Uid(newCommunityID);
                                    if(finalI == ArrayList_SelectedPhoto.size()-1) {
                                        storeUpload(documentReference, communityModel);
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
                Communitymodel = new CommunityModel(Community_Title_EditText, Community_Content_EditText,  date, CurrentUid, CommunityID, LikeList, HotCommunity, community_commentcount);
                Communitymodel.setCommunityModel_Community_Uid(CommunityID);
                storeUpload(documentReference, Communitymodel);
            }
        }
       // 제목이 없는 경우
        else {
            if(Community_Title_EditText.length() == 0){
                Toast.makeText(this.getApplicationContext(), "제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

   // 파이어스토어에 바뀐 정보들을 Community에 넣는다.
    private void storeUpload(DocumentReference documentReference, final CommunityModel communityModel) {
        documentReference.set(communityModel.getCommunityInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.calldismiss();
                        finish();
                        dialog.calldismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.calldismiss();
                    }
                });
    }

   // 뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}