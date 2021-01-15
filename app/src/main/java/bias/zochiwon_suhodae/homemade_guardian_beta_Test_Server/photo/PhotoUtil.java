package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity;

// 형태는 조금 다르나 다른 package에서 쓰이는 Utill과 기능이 같다.

public class PhotoUtil extends Intent implements Parcelable {

  private PhotoUtil() { }

  public PhotoUtil(Context packageContext) { super(packageContext, PhotoPickerActivity.class); }

  public void setMaxSelectCount(int photoCount) { this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount); }

  public void setShowCamera(boolean showCamera) { this.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera); }

  public void setShowGif(boolean showGif) { this.putExtra(PhotoPickerActivity.EXTRA_SHOW_GIF, showGif); }

  public void setMaxGrideItemCount(int grideCount){ this.putExtra(PhotoPickerActivity.EXTRA_MAX_GRIDE_ITEM_COUNT, grideCount); }

  public void setSelectCheckBox(boolean isCheckbox){ this.putExtra(PhotoPickerActivity.EXTRA_CHECK_BOX_ONLY, isCheckbox); }

}
