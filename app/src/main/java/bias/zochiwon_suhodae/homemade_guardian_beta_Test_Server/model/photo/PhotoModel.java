package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo;

// 이미지의 모델이다.

import java.io.Serializable;

public class PhotoModel implements Serializable {

  private int Photo_Id;
  private String Photo_Path;

  public PhotoModel(int Photo_Id, String Photo_Path) {
    this.Photo_Id = Photo_Id;
    this.Photo_Path = Photo_Path;
  }

  public PhotoModel() { }

  //선택된 이미지와 이벤트를 실행하려는 이미지의 일치 여부를 확인하는 함수
  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PhotoModel)) return false;
    PhotoModel photoModel = (PhotoModel) o;
    return Photo_Id == photoModel.Photo_Id;
  }

  @Override public int hashCode() {
    return Photo_Id;
  }

  public String getPhoto_Path() {
    return Photo_Path;
  }
  public void setPhoto_Path(String photo_Path) {
    this.Photo_Path = photo_Path;
  }
  public int getPhoto_Id() {
    return Photo_Id;
  }
  public void setPhoto_Id(int photo_Id) {
    this.Photo_Id = photo_Id;
  }
}
