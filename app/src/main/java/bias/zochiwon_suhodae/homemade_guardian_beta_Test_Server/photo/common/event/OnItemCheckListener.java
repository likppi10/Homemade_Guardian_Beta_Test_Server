package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;

// 이미지들이 나열되어 있는 PhotoPickerFragment와 연결되어 있는 PhotoGridAdapter에서 사진들이 체크되었을 때 발생하는 이벤트를 interface로 선언한 것이다.

public interface OnItemCheckListener {
  /***
   *
   * @param position Select Image postion
   * @param path     Select image path
   *@param isCheck   Image status
   * @param selectedItemCount  Select image count
   * @return enable check
   */
  boolean OnItemCheck(int position, PhotoModel path, boolean isCheck, int selectedItemCount);
}
