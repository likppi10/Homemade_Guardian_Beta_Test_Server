package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.activity.PhotoPickerActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// ImagePagerFragment와 연결되어 있고, 이미지를 크게 보기 위한 데에 필요한 Adapter이다.
//    (PhotoPickerActivity) -> (PhotoPickerFragment) -> (PhotoGridAdapter)
//                        ↘ (ImagePagerFragment)  -> PhotoPagerAdapter

public class PhotoPagerAdapter extends PagerAdapter {

  private List<String> PathList = new ArrayList<>();  //확대 된 상태에서도 뷰페이저로 슬라이드하여 다른 이미지들을 볼 수 있으므로 보여지는 단일 이미지만 받는 것이 아닌 리스트로 받아온다.
  private Context Context;
  private LayoutInflater LayoutInflater;

  public PhotoPagerAdapter(Context MContext, List<String> PathList) {
    this.Context = MContext;
    this.PathList = PathList;
    LayoutInflater = LayoutInflater.from(MContext);
  }

  @Override
  public Object instantiateItem(ViewGroup Container, int Position) {
    View Itemview = LayoutInflater.inflate(R.layout.util_item_pager, Container, false);
    ImageView Imageview = (ImageView) Itemview.findViewById(R.id.Enlarge_Pager);
    final String Path = PathList.get(Position);
    final Uri URI;
    if (Path.startsWith("http")) {
      URI = Uri.parse(Path);
    } else {
      URI = FileProvider.getUriForFile(Context, "bias.zochiwon_suhodae.homemade_guardian_beta.provider", new File(Path));
    }
    Glide.with(Context)
            .load(URI)
            .thumbnail(0.4f)
            .apply(new RequestOptions()
                    .placeholder(R.color.img_loding_placeholder)
                    .error(R.drawable.ic_place_holder))
            .into(Imageview);

    //이미지를 클릭하여 크게 보았을 때 한번더 누르면 백프레스의 기능을 한다.
    Imageview.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Context instanceof PhotoPickerActivity) {
          if (!((Activity) Context).isFinishing()) {
            ((Activity) Context).onBackPressed();
          }
        }
      }
    });
    Container.addView(Itemview);
    return Itemview;
  }

  @Override
  public int getCount() {
    return PathList.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) { container.removeView((View) object); }

  @Override
  public int getItemPosition (Object object) { return POSITION_NONE; }

}
