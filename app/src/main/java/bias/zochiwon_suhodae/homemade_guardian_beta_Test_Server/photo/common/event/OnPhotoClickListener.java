package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event;

import android.view.View;

//이미지들이 나열되어 있는 PhotoPickerFragment와 연결되어 있는 PhotoGridAdapter에서 사진이 클릭 되었을 때 발생하는 이벤트를 interface로 선언한 것이다.

public interface OnPhotoClickListener { void onClick(View v, int position, boolean showCamera);}
