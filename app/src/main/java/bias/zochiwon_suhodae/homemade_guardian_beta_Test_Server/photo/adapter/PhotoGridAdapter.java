package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.PhotoModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.DirectoryModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event.OnItemCheckListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.event.OnPhotoClickListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common.MediaStoreHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// PhotoPickerFragment와 연결되어 제일 첫번째에는 카메라 기능을 연결할 버튼을 배치하고 그 다음부터는 이미지들을 나열한다.
//    (PhotoPickerActivity) -> (PhotoPickerFragment) -> PhotoGridAdapter
//                        ↘ (ImagePagerFragment)  -> (PhotoPagerAdapter)

public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

  private LayoutInflater Inflater;
  private Context Context;

  private OnItemCheckListener onItemCheckListener    = null;  //이미지의 체크박스가 체크되었는지
  private OnPhotoClickListener onPhotoClickListener  = null;  //이미지가 클릭되었는지
  private View.OnClickListener onCameraClickListener = null;  //카메라를 이용하는 버튼이 클릭 되었는지

  public final static int ITEM_TYPE_CAMERA = 100;
  public final static int ITEM_TYPE_PHOTO  = 101;
  private boolean HasCamera = true;
  private boolean IsCheckBoxOnly = false;

  public PhotoGridAdapter(Context Context, List<DirectoryModel> PhotoDirectories , boolean IsCheckBoxOnly) {
    this.directory_Model_List = PhotoDirectories;
    this.Context = Context;
    this.IsCheckBoxOnly = IsCheckBoxOnly;
    Inflater = LayoutInflater.from(Context);
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = Inflater.inflate(R.layout.util_item_photo, parent, false);
    PhotoViewHolder holder = new PhotoViewHolder(itemView);
    if (viewType == ITEM_TYPE_CAMERA) {
      holder.Selectedview.setVisibility(View.GONE);
      holder.IvPhoto.setScaleType(ImageView.ScaleType.CENTER);
      holder.IvPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onCameraClickListener != null) {
            onCameraClickListener.onClick(view);
          }
        }
      });
    }
    return holder;
  }

  //이미지들을 받는 Holder
  @Override public void onBindViewHolder(final PhotoViewHolder Holder, int Position) {
    if (getItemViewType(Position) == ITEM_TYPE_PHOTO) {
      List<PhotoModel> photoModelList = getCurrentPhotos();
      final PhotoModel PhotoModel;
      if (showCamera()) {
        PhotoModel = photoModelList.get(Position - 1);
      } else {
        PhotoModel = photoModelList.get(Position);
      }
            Uri URI = FileProvider.getUriForFile(Context, "bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.provider", new File(PhotoModel.getPhoto_Path()));
      Glide.with(Context)
              .load(URI)
              .apply(new RequestOptions()
                      .placeholder(R.color.img_loding_placeholder)
                      .error(R.color.image_loading_error_color)
                      .centerCrop())
              .into(Holder.IvPhoto);

      final boolean IsChecked = isSelected(PhotoModel);
      Holder.Selectedview.setSelected(IsChecked);
      Holder.IvPhoto.setSelected(IsChecked);
      Holder.IvPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if(IsCheckBoxOnly){
            boolean IsEnable = true;
            if (onItemCheckListener != null) {
              IsEnable = onItemCheckListener.OnItemCheck(Holder.getAdapterPosition(), PhotoModel, IsChecked, getSelectedPhoto_Model_List().size());
            }
            if (IsEnable) {
              toggleSelection(PhotoModel);
              notifyItemChanged(Holder.getAdapterPosition());
            }
          }else{
            if (onPhotoClickListener != null) {
              onPhotoClickListener.onClick(view, Holder.getAdapterPosition(), showCamera());
            }
          }
        }
      });

      Holder.Selectedview.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {

          boolean isEnable = true;

          if (onItemCheckListener != null) {
            isEnable = onItemCheckListener.OnItemCheck(Holder.getAdapterPosition(), PhotoModel, IsChecked, getSelectedPhoto_Model_List().size());
          }
          if (isEnable) {
            toggleSelection(PhotoModel);
            notifyItemChanged(Holder.getAdapterPosition());
          }
        }
      });
    } else {
      Holder.IvPhoto.setImageResource(R.drawable.camera);
    }
  }

  //디렉토리의 이미지 개수
  @Override public int getItemCount() {
    int photosCount = directory_Model_List.size() == 0 ? 0 : getCurrentPhotos().size();
    if (showCamera()) { return photosCount + 1; }
    return photosCount;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView IvPhoto;
    private View Selectedview;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      IvPhoto = (ImageView) itemView.findViewById(R.id.Arrange_Image);
      Selectedview = itemView.findViewById(R.id.Check_Selected);
    }
  }

  @Override
  public int getItemViewType(int position) { return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO; }

  public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) { this.onItemCheckListener = onItemCheckListener; }

  public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) { this.onPhotoClickListener = onPhotoClickListener; }

  public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) { this.onCameraClickListener = onCameraClickListener; }

  public ArrayList<String> getSelectedPhotoPaths() {
    ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());
    for (PhotoModel photoModel : selectedPhoto_Model_List) { selectedPhotoPaths.add(photoModel.getPhoto_Path()); }
    return selectedPhotoPaths;
  }

  public void setShowCamera(boolean hasCamera) {
    this.HasCamera = hasCamera;
  }

  public boolean showCamera() { return (HasCamera && CurrentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS); }
}