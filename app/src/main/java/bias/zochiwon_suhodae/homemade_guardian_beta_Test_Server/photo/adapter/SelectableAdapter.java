package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter;

import androidx.recyclerview.widget.RecyclerView;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.DirectoryModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event.Selectable;
import java.util.ArrayList;
import java.util.List;

//RecyclerView에 Utill처럼 PhotoModel package에서만 사용 할 기능을 추가하여 쓰기 위해 만든 추상 클래스이다.

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> implements Selectable {

  private static final String TAG = SelectableAdapter.class.getSimpleName();

  protected List<DirectoryModel> directory_Model_List;
  protected List<PhotoModel> selectedPhoto_Model_List;
  public int CurrentDirectoryIndex = 0;

  public SelectableAdapter() {
    directory_Model_List = new ArrayList<>();
    selectedPhoto_Model_List = new ArrayList<>();
  }

  /**
   * Indicates if the item at position position is selected
   * @param photoModel PhotoModel of the item to check
   * @return true if the item is selected, false otherwise
   */
  @Override public boolean isSelected(PhotoModel photoModel) { return getSelectedPhoto_Model_List().contains(photoModel); }

  /**
   * Toggle the selection status of the item at a given position
   *
   * @param photoModel PhotoModel of the item to toggle the selection status for
   */
  @Override public void toggleSelection(PhotoModel photoModel) {
    if (selectedPhoto_Model_List.contains(photoModel)) {
      selectedPhoto_Model_List.remove(photoModel);
    } else {
      selectedPhoto_Model_List.add(photoModel);
    }
  }

  /**
   * Clear the selection status for all items
   */
  @Override public void clearSelection() {
    selectedPhoto_Model_List.clear();
  }


  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  @Override public int getSelectedItemCount() {
    return selectedPhoto_Model_List.size();
  }

  public void setCurrentDirectoryIndex(int currentDirectoryIndex) { this.CurrentDirectoryIndex = currentDirectoryIndex; }

  public List<PhotoModel> getCurrentPhotos() { return directory_Model_List.get(CurrentDirectoryIndex).getPhotoModelList(); }

  public List<String> getCurrentPhotoPaths() {
    List<String> currentPhotoPaths = new ArrayList<>(getCurrentPhotos().size());
    for (PhotoModel photoModel : getCurrentPhotos()) {
      currentPhotoPaths.add(photoModel.getPhoto_Path());
    }
    return currentPhotoPaths;
  }

  @Override public List<PhotoModel> getSelectedPhoto_Model_List() {
    return selectedPhoto_Model_List;
  }
}