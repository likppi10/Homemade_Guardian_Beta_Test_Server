package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event.OnItemCheckListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.fragment.ImagePagerFragment;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.fragment.PhotoPickerFragment;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

//사진을 다중 선택하는 이벤트의 최초로 도달하는 액티비티이다.
// 주된 기능은 앨범, 카메라,스토리지에 대한 접근 및 카메라 실행 / 사진 각 장마다의 setOnClickListener (ImagePagerFragment) / 접근한 경로의 이미지들을 배열하는 것 (PhotoPickerFragment) 이렇게 3가지이다.
//    PhotoPickerActivity -> (PhotoPickerFragment) -> (PhotoGridAdapter)
//                        ↘ (ImagePagerFragment)  -> (PhotoPagerAdapter)

public class PhotoPickerActivity extends AppCompatActivity {

  private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7;
  private final int MY_PERMISSIONS_REQUEST_CAMERA = 8;

  private PhotoPickerFragment PickerFragment;
  private ImagePagerFragment ImagePagerFragment;

  public final static String EXTRA_MAX_COUNT     = "MAX_COUNT";
  public final static String EXTRA_SHOW_CAMERA   = "SHOW_CAMERA";
  public final static String EXTRA_SHOW_GIF      = "SHOW_GIF";
  public final static String EXTRA_CHECK_BOX_ONLY       = "CHECK_BOX_ONLY";
  public final static String KEY_SELECTED_PHOTOS        = "SELECTED_PHOTOS";
  public final static String EXTRA_MAX_GRIDE_ITEM_COUNT = "MAX_GRIDE_IMAGE_COUNT";
  private boolean ShowCamera = true;


  private MenuItem MenuDoneItem;
  public final static int DEFAULT_MAX_COUNT = 9;
  public final static int DEFAULT_MAX_GRIDE_ITEM_COUNT = 3;

  private int MaxCount = DEFAULT_MAX_COUNT;
  public int MaxGrideItemCount = DEFAULT_MAX_GRIDE_ITEM_COUNT;
  public boolean IsCheckBoxOnly = false;

  /** to prevent multiple calls to inflate menu */
  private boolean MenuIsInflated = false;
  private boolean ShowGif = false;
  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkExternalStoragePermission();
  }

  @Override
  protected void onResume() {
    super.onResume();
//    Log.d("민규","3333333");
//    checkExternalStoragePermission();
//    Log.d("민규","333333");
  }

  //저장경로의 접근권한 요청
  private void checkExternalStoragePermission(){
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
          checkCameraPermission();
        }
        else{
          finish();
        }
      }
    }else {
      checkCameraPermission();
    }
  }

  //카메라의 접근권한 요청
  private void checkCameraPermission(){
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      }
    }else {
      init();
    }
  }

  //접근권한의 결과처리
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          checkCameraPermission();
        } else {
          Toast.makeText(PhotoPickerActivity.this , "You do not have read permissions." , Toast.LENGTH_LONG ).show();
          finish();
        }
        return;
      }

      case MY_PERMISSIONS_REQUEST_CAMERA: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          init();
        } else {
          Toast.makeText(PhotoPickerActivity.this , "There is no camera permissions." , Toast.LENGTH_LONG ).show();
        }
        return;
      }
    }
  }

  //접근권한 요청 후 화면 구성
  private void init(){
    setContentView(R.layout.util_activity_photo_picker);
    ShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    ShowGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
    IsCheckBoxOnly = getIntent().getBooleanExtra(EXTRA_CHECK_BOX_ONLY, false);
    MaxGrideItemCount = getIntent().getIntExtra(EXTRA_MAX_GRIDE_ITEM_COUNT , DEFAULT_MAX_GRIDE_ITEM_COUNT);

    setShowGif(ShowGif);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle(R.string.y_photopicker_image_select_title);
    setSupportActionBar(mToolbar);

    ActionBar ActionBar = getSupportActionBar();

    assert ActionBar != null;
    ActionBar.setDisplayHomeAsUpEnabled(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ActionBar.setElevation(25);
    }
    MaxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
    setPickerFragment();
  }

  //PhotoPickerFragment로 연결
  private void setPickerFragment(){
    if(PickerFragment == null){
      PickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.PhotoPickerFragment);
      PickerFragment.getPhotogridAdapter().setShowCamera(ShowCamera);
      PickerFragment.getPhotogridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
        @Override public boolean OnItemCheck(int position, PhotoModel photoModel, final boolean isCheck, int selectedItemCount) {
          int total = selectedItemCount + (isCheck ? -1 : 1);
          MenuDoneItem.setEnabled(total > 0);
          if (MaxCount <= 1) {
            List<PhotoModel> photoModelList = PickerFragment.getPhotogridAdapter().getSelectedPhoto_Model_List();
            if (!photoModelList.contains(photoModel)) {
              photoModelList.clear();
              PickerFragment.getPhotogridAdapter().notifyDataSetChanged();
            }
            return true;
          }
          if (total > MaxCount) {
            Toast.makeText(getActivity(), getString(R.string.y_photopicker_over_max_count_tips, MaxCount),
                    LENGTH_LONG).show();
            return false;
          }
          MenuDoneItem.setTitle(getString(R.string.y_photopicker_done_with_count, total, MaxCount));
          return true;
        }
      });
    }else{
      PickerFragment.getPhotogridAdapter().notifyDataSetChanged();
    }
  }

  /**
   * Overriding this method allows us to run our exit animation first, then exiting
   * the activity when it complete.
   */

  //BackPressed의 이벤트 처리
  @Override public void onBackPressed() {
    if (ImagePagerFragment != null && ImagePagerFragment.isVisible()) {
      ImagePagerFragment.runExitAnimation(new Runnable() {
        public void run() {
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
          }
        }
      });
    } else {
      super.onBackPressed();
    }
  }

  // 앨범 화면에서 사진을 클릭하여 크게 볼때
  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
    this.ImagePagerFragment = imagePagerFragment;
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.Photo_Container, this.ImagePagerFragment)
            .addToBackStack(null)
            .commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (!MenuIsInflated) {
      getMenuInflater().inflate(R.menu.menu_picker, menu);
      MenuDoneItem = menu.findItem(R.id.done);
      MenuDoneItem.setEnabled(true);                                                                  //완료버튼
      MenuIsInflated = true;
      return true;
    }
    return false;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    if (item.getItemId() == R.id.done) {
      Intent intent = new Intent();
      ArrayList<String> SelectedPhotoList = PickerFragment.getPhotogridAdapter().getSelectedPhotoPaths();
      intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, SelectedPhotoList);
      setResult(RESULT_OK, intent);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public PhotoPickerActivity getActivity() {
    return this;
  }

  public boolean isShowGif() {
    return ShowGif;
  }

  public void setShowGif(boolean showGif) {
    this.ShowGif = showGif;
  }

}