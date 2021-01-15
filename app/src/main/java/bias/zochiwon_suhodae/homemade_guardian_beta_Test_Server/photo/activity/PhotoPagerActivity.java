//package com.example.homemade_guardian_beta.photo.activity;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.viewpager.widget.ViewPager;
//
//import com.example.homemade_guardian_beta.R;
//import com.example.homemade_guardian_beta.photo.fragment.ImagePagerFragment;
//import com.google.android.material.snackbar.Snackbar;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//// 안 쓰는듯
//public class PhotoPagerActivity extends AppCompatActivity {
//
//  private ImagePagerFragment pagerFragment;
//
//  private static final String FOLDER_NAME = "y_photopicker";
//
//  public final static String EXTRA_CURRENT_ITEM = "current_item";
//  public final static String EXTRA_PHOTOS = "photos";
//
//  private ActionBar actionBar;
//
//  private TextView tv_count;
//  private ImageView iv_back_btn;
//
//  private int mSaveEditPostion = 0;
//
//  private String PostModel_Post_Uid;
//
//  @Override protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    setContentView(R.layout.util_activity_photo_pager);
//
//    int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
//    List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
//
//    tv_count = (TextView) findViewById(R.id.tv_count);
//    iv_back_btn = (ImageView) findViewById(R.id.iv_back_btn);
//    iv_back_btn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        onBackPressed();
//      }
//    });
//
//    pagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
//    pagerFragment.setPhotos(paths, currentItem);
//
//    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//    setSupportActionBar(mToolbar);
//
//    actionBar = getSupportActionBar();
//
//    actionBar.setDisplayHomeAsUpEnabled(true);
//    updateActionBarTitle();
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//      actionBar.setElevation(25);
//    }
//
//
//    pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        updateActionBarTitle();
//      }
//
//      @Override public void onPageSelected(int i) {
//
//      }
//
//      @Override public void onPageScrollStateChanged(int i) {
//
//      }
//    });
//
//    PostModel_Post_Uid = getIntent().getStringExtra("PostModel_Post_Uid");
//
//  }
//
//
//  @Override public boolean onCreateOptionsMenu(Menu menu) {
//    getMenuInflater().inflate(R.menu.menu_preview, menu);
//    return true;
//  }
//
//
//  @Override public void onBackPressed() {
//
//    Intent intent = new Intent();
//    intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPathList());
//    setResult(RESULT_OK, intent);
//    finish();
//
//    super.onBackPressed();
//  }
//
//
//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    //      if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
////        final Uri resultUri = UCrop.getOutput(data);
////
////        Toast.makeText(PhotoPagerActivity.this , "Saved Image ["+ resultUri + "]" , Toast.LENGTH_LONG).show();
////
////      } else if (resultCode == UCrop.RESULT_ERROR) {
////        final Throwable cropError = UCrop.getError(data);
////        Toast.makeText(PhotoPagerActivity.this , "[Error]"+cropError , Toast.LENGTH_SHORT).show();
////      }
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//
//    if (item.getItemId() == android.R.id.home) {
//      onBackPressed();
//      return true;
//    }
//
//    if (item.getItemId() == R.id.Comment_Delete_Button) {
//      final int index = pagerFragment.getCurrentItem();
//
//      final String deletedPath =  pagerFragment.getPathList().get(index);
//
//      Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.y_photopicker_deleted_a_photo, Snackbar.LENGTH_LONG);
//      if (pagerFragment.getPathList().size() <= 1) {
//
//        // show confirm dialog
//        new AlertDialog.Builder(this)
//            .setTitle(R.string.y_photopicker_confirm_to_delete)
//            .setPositiveButton(R.string.y_photopicker_yes, new DialogInterface.OnClickListener() {
//              @Override public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                setResult(RESULT_OK);
//                finish();
//              }
//            })
//            .setNegativeButton(R.string.y_photopicker_cancel, new DialogInterface.OnClickListener() {
//              @Override public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//              }
//            })
//            .show();
//
//      } else {
//
//        snackbar.show();
//
//        pagerFragment.getPathList().remove(index);
//        pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//      }
//
//      snackbar.setAction(R.string.y_photopicker_undo, new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//          if (pagerFragment.getPathList().size() > 0) {
//            pagerFragment.getPathList().add(index, deletedPath);
//          } else {
//            pagerFragment.getPathList().add(deletedPath);
//          }
//          pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
//          pagerFragment.getViewPager().setCurrentItem(index, true);
//        }
//      });
//
//      return true;
//    }
//
//    return super.onOptionsItemSelected(item);
//  }
//
//  public void updateActionBarTitle() {
//    tv_count.setText("" + (pagerFragment.getViewPager().getCurrentItem() + 1) + "/" + pagerFragment.getPathList().size());
//  }
//
//  /**
//   * create Image save Folder
//   * @return
//   */
//  private File createFolders() {
//    File baseDir;
//
//    if (Build.VERSION.SDK_INT < 8) {
//      baseDir = Environment.getExternalStorageDirectory();
//    } else {
//      baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//    }
//
//    if (baseDir == null) {
//      return Environment.getExternalStorageDirectory();
//    }
//
//    File aviaryFolder = new File(baseDir, FOLDER_NAME);
//
//    if (aviaryFolder.exists()) {
//      return aviaryFolder;
//    }
//    if (aviaryFolder.mkdirs()) {
//      return aviaryFolder;
//    }
//
//    return Environment.getExternalStorageDirectory();
//  }
//
//  /**
//   * get Uri Fimename
//   * @param context
//   * @param uri
//   * @return
//   */
//  public static String getFileNameByUri(Context context, Uri uri)
//  {
//    String fileName="unknown";//default fileName
//    Uri filePathUri = uri;
//    if (uri.getScheme().toString().compareTo("content")==0)
//    {
//      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//      if (cursor.moveToFirst())
//      {
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
//        filePathUri = Uri.parse(cursor.getString(column_index));
//        fileName = filePathUri.getLastPathSegment().toString();
//      }
//    }
//    else if (uri.getScheme().compareTo("file")==0)
//    {
//      fileName = filePathUri.getLastPathSegment().toString();
//    }
//    else
//    {
//      fileName = fileName+"_"+filePathUri.getLastPathSegment();
//    }
//    return fileName;
//  }
//
//}
