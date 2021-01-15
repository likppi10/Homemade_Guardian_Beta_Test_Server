package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.BasicActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter.GalleryAdapter;
import java.util.ArrayList;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util;

// 이미지를 단일 선택하고, 사용자의 프로필 사진의 선택을 할 때에 실행되는 액티비티이다. 초기화 버튼 존재
//      Ex) MemberInitActivity, UpdateInfoActivity에서 사진을 고를 때에 사용된다. <-> GalleryAdapter와 연결된다.

public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setToolbarTitle("");
        checkpermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            // 권한이 주어진 상태라면 이미지들을 담을 RecyclerView 실행 / 아니라면 finish();
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                } else {
                    finish();
                    Util.showToast(GalleryActivity.this, getResources().getString(R.string.please_grant_permission));
                }
            }
        }
    }

    // 권한을 체크하는 함수 : checkSelfPermission 권한 체크, requestPermissions 권한 요구
    public void checkpermission(){
        if (ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }else { Util.showToast(GalleryActivity.this, getResources().getString(R.string.please_grant_permission));}   // part18 : strings에 메시지 관리하기
        } else { recyclerInit(); }
    }

    // 이미지들을 담을 RecyclerView 설정
    private void recyclerInit(){
        final int numberOfColumns = 3;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, GetImagePath(this));
        recyclerView.setAdapter(mAdapter);
    }

    // 아이콘 이미지 생성, return
    public ArrayList<String> GetImagePath(Activity activity) { // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                               // 2. 변수 및 배열
        final ArrayList<String> ListOfImage = new ArrayList<String>();   //PathOfImage의 값을 넣어서 전달하는 ArrayList<String>
        String[] projection;
        String PathOfImage = null;                                  //커서로 선택한 이미지의 Uri를 String의 값으로 받으려는 변수
        int Column_Index_Data;
                                                               // 3. xml 변수
        Button Image_Reset_Button;                                  // 이미지를 선택하고 싶지 않을 때 ImagePath를 비워주는 Reset 버튼
                                                               // 5. 기타 변수
        Cursor CursorEvent;                                         // 이미지를 선택한 행위
        Uri URI;                                                    // 선택한 이미지의 Uri

        // 비디오면 비디오 / 이미지면 이미지 리스트 반환
        Intent ImageIntent = getIntent();
        int Media = ImageIntent.getIntExtra(Util.INTENT_MEDIA, Util.GALLERY_IMAGE);

        // Reset 버튼을 누르면 RESULT_CANCELED로 resultCode를 set하고 finish();
        Image_Reset_Button = findViewById(R.id.Image_Reset_Button);
        Image_Reset_Button.setOnClickListener(onClickListener);

        if(Media == Util.GALLERY_VIDEO){
            URI = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{
            URI = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }

        CursorEvent = activity.getContentResolver().query(URI, projection, null, null, null);
        Column_Index_Data = CursorEvent.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (CursorEvent.moveToNext()) {
            PathOfImage = CursorEvent.getString(Column_Index_Data);
            ListOfImage.add(PathOfImage);
        }
        return ListOfImage;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Image_Reset_Button:
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    break;
            }
        }
    };
}