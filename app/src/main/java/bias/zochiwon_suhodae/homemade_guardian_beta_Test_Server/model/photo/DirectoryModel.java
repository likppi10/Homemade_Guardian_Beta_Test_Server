package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 디렉토리의 모델이다.

public class DirectoryModel implements Serializable {

  private String PhotoDirectory_Id;
  private String PhotoDirectory_CoverPath;
  private String PhotoDirectory_Name;
  private long PhotoDirectory_Dateadded;
  private List<PhotoModel> photoModelList = new ArrayList<>();

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DirectoryModel)) return false;

    DirectoryModel directory = (DirectoryModel) o;

    if (!PhotoDirectory_Id.equals(directory.PhotoDirectory_Id)) return false;
    return PhotoDirectory_Name.equals(directory.PhotoDirectory_Name);
  }

  @Override public int hashCode() {
    int result = PhotoDirectory_Id.hashCode();
    result = 31 * result + PhotoDirectory_Name.hashCode();
    return result;
  }

  public String getPhotoDirectory_Id() {
    return PhotoDirectory_Id;
  }
  public void setPhotoDirectory_Id(String photoDirectory_Id) { this.PhotoDirectory_Id = photoDirectory_Id; }
  public String getPhotoDirectory_CoverPath() {
    return PhotoDirectory_CoverPath;
  }
  public void setPhotoDirectory_CoverPath(String photoDirectory_CoverPath) { this.PhotoDirectory_CoverPath = photoDirectory_CoverPath; }
  public String getPhotoDirectory_Name() {
    return PhotoDirectory_Name;
  }
  public void setPhotoDirectory_Name(String photoDirectory_Name) { this.PhotoDirectory_Name = photoDirectory_Name; }
  public long getPhotoDirectory_Dateadded() {
    return PhotoDirectory_Dateadded;
  }
  public void setPhotoDirectory_Dateadded(long photoDirectory_Dateadded) { this.PhotoDirectory_Dateadded = photoDirectory_Dateadded; }
  public List<PhotoModel> getPhotoModelList() {
    return photoModelList;
  }
  public void setPhotoModelList(List<PhotoModel> photoModelList) {
    this.photoModelList = photoModelList;
  }
  public List<String> getPhotoPaths() {
    List<String> paths = new ArrayList<>(photoModelList.size());
    for (PhotoModel photoModel : photoModelList) {
      paths.add(photoModel.getPhoto_Path());
    }
    return paths;
  }
  public void addPhoto(int id, String path) {
    photoModelList.add(new PhotoModel(id, path));
  }
}
