package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event;


import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;

import java.util.List;

//이미지를 선택 할 수 있는지 아닌지를 구별하는 interface

public interface Selectable {

  /**
   * Indicates if the item at position position is selected
   *
   * @param photoModel PhotoModel of the item to check
   * @return true if the item is selected, false otherwise
   */
  boolean isSelected(PhotoModel photoModel);
  /**
   * Toggle the selection status of the item at a given position
   *
   * @param photoModel PhotoModel of the item to toggle the selection status for
   */
  void toggleSelection(PhotoModel photoModel);
  /**
   * Clear the selection status for all items
   */
  void clearSelection();
  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  int getSelectedItemCount();
  /**
   * Indicates the list of selected photos
   *
   * @return List of selected photos
   */
  List<PhotoModel> getSelectedPhoto_Model_List();
}
