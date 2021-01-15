package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.photo.DirectoryModel;
import java.util.ArrayList;
import java.util.List;

// PhotoPickerFragment에서 나타낸 하단부에 이미지들의 디렉토리를 고를 수 있다. 다른 경로의 이미지들을 선택할 수 있게 해준다.

public class PopupDirectoryListAdapter extends BaseAdapter {

  private Context Context;
  private List<DirectoryModel> photodirectory_List = new ArrayList<>();
  private LayoutInflater LayoutInflater;

  public PopupDirectoryListAdapter(Context Context, List<DirectoryModel> photodirectory_List) {
    this.Context = Context;
    this.photodirectory_List = photodirectory_List;
    LayoutInflater = LayoutInflater.from(Context);
  }

  @Override
  public int getCount() {
    return photodirectory_List.size();
  }

  @Override
  public DirectoryModel getItem(int position) {
    return photodirectory_List.get(position);
  }

  @Override
  public long getItemId(int position) {
    return photodirectory_List.get(position).hashCode();
  }

  @Override
  public View getView(int position, View Convertview, ViewGroup Parent) {
    ViewHolder Holder;
    if (Convertview == null) {
      Convertview = LayoutInflater.inflate(R.layout.util_item_directory, Parent, false);
      Holder = new ViewHolder(Convertview);
      Convertview.setTag(Holder);
    } else {
      Holder = (ViewHolder) Convertview.getTag();
    }
    Holder.bindData(photodirectory_List.get(position));

    return Convertview;
  }

  private class ViewHolder {
    public ImageView Directory_Cover_Image_ImageView;
    public TextView Directory_Cover_Name_TextView;
    public TextView Directory_Cover_ImageCount_TextView;

    public ViewHolder(View Rootview) {
      Directory_Cover_Image_ImageView = (ImageView) Rootview.findViewById(R.id.Directory_Cover_Image);
      Directory_Cover_Name_TextView = (TextView)  Rootview.findViewById(R.id.Directory_Cover_Name);
      Directory_Cover_ImageCount_TextView = (TextView)  Rootview.findViewById(R.id.Directory_Cover_ImageCount);
    }

    //디렉토리의 정보 (커버사진,이름,이미지개수 등)를 set
    public void bindData(DirectoryModel Directory) {
      if (Context instanceof Activity && ((Activity) Context).isFinishing()) { return; }
      Glide.with(Context)
          .load(Directory.getPhotoDirectory_CoverPath())
          .thumbnail(0.1f)
          .into(Directory_Cover_Image_ImageView);
      Directory_Cover_Name_TextView.setText(Directory.getPhotoDirectory_Name());
      Directory_Cover_ImageCount_TextView.setText(Context.getString(R.string.y_photopicker_image_count, Directory.getPhotoModelList().size()));
    }
  }
}