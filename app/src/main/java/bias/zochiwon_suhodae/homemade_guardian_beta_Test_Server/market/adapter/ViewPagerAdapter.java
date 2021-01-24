package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity.EnlargeImageActivity;
import java.util.ArrayList;

// 뷰페이져에서 이미지리스트의 string으로 저장된 이미지들을 imageList에 넣어서 MarketActivity에서 슬라이드하여 이미지들을 볼 수 있게 해준다.

public class ViewPagerAdapter extends PagerAdapter {                            // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
    // 2. 변수 및 배열
    private ArrayList<String> ArrayList_ImageList = new ArrayList<String>();             // 받아온 이미지리스트
    private String ViewpagerState;                                                  // ViewpagerState를 enable 시켜서 EnlargeImageActivity로 이동할 것인가를 정해주는 상태 변수
    private String uses;
    // 5.기타 변수
    private Context Context;

    public ViewPagerAdapter(Context Context, ArrayList<String> ArrayList_ImageList,String ViewpagerState, String uses)
    {
        this.Context = Context;
        this.ArrayList_ImageList = ArrayList_ImageList;
        this.ViewpagerState = ViewpagerState;
        this.uses = uses;
    }

    // 로그를 찍어본다면 이미 이미지의 정보를 ArrayList_ImageList에 갖고 왔으므로 Glide로 생성만 해준다.
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater Inflater = (LayoutInflater) Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(ViewpagerState.equals("Enable")){
            view = Inflater.inflate(R.layout.post_silder, null);

            // 뷰페이저를 클릭한다면 EnlargeImageActivity로 이동시킨다.
            view.setOnClickListener( new View.OnClickListener() {
                public void onClick(View m) {
                    Intent Intent_ViewPagerViewer = new Intent(Context, EnlargeImageActivity.class);
                    Intent_ViewPagerViewer.putExtra("marketImage",ArrayList_ImageList);
                    Context.startActivity(Intent_ViewPagerViewer);
                }
            });
            ImageView Market_ImageView = view.findViewById(R.id.PostActivity_Post_ImageView);
            Glide.with(view).load(ArrayList_ImageList.get(position)).centerCrop().override(1000).thumbnail(0.1f).into(Market_ImageView);
            container.addView(view);
        }else{
            view = Inflater.inflate(R.layout.post_slider_enlarge, null);
            PhotoView Market_ImageView = view.findViewById(R.id.PostActivity_Post_EnlargeImageView);
            Glide.with(view).load(ArrayList_ImageList.get(position)).override(1000).thumbnail(0.1f).into(Market_ImageView);
            container.addView(view);
        }
        return view;
    }

    @Override
    public int getCount() { return ArrayList_ImageList.size(); }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) { container.removeView((View)object); }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) { return (view == (View)o); }

}