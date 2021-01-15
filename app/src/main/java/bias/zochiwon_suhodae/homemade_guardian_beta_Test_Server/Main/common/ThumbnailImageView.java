package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;

import java.util.ArrayList;

//MainFragment에서 이미지를 썸네일로써 한장만 보여주고 2장 이상일경우 "더보기"로 구현

public class ThumbnailImageView extends LinearLayout {
    private LayoutInflater Layoutinflater;
    private Context Context;

    private int moreIndex = -1;             //썸네일에 사진 1장 만을 만들기 위한 인덱스

    public ThumbnailImageView(Context Context) {
        super(Context);
        this.Context = Context;
        initView();
    }

    public ThumbnailImageView(Context Context, @Nullable AttributeSet attributeSet) {
        super(Context, attributeSet);
        this.Context = Context;
        initView();
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        Layoutinflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Layoutinflater.inflate(R.layout.view_market, this, true);                                                 // part19 : view_post는 게시물 클릭시 페이지, item_post는 메인에서 나열된 게시물
    }

    public void setMoreIndex(int moreIndex){ this.moreIndex = moreIndex;}                                                                          // part19 : 어댑터에서도 쓸 수 있게 (46'50") }

    //구체적으로 썸네일을 설정하는 부분 i가 moreIndex 같다면 "더보기"를 게시물하단부에 출력시키고 종료한다.
    public void Set_Post_Thumbnail(MarketModel marketModel){

        LinearLayout Thumbnail_Layout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> ArrayList_ImageList = marketModel.getMarketModel_ImageList();
        if(ArrayList_ImageList != null) {
            for (int i = 0; i < ArrayList_ImageList.size(); i++) {                                                 // part17 : 더보기기능 추가
                if (i == moreIndex) {
                    TextView Textview = new TextView(Context);
                    Textview.setLayoutParams(layoutParams);
                    Textview.setText("더보기...");
                    Thumbnail_Layout.addView(Textview);
                    break;
                }
                String Image = ArrayList_ImageList.get(i);                                                                    // part20 : 컨텐츠의 정보에 따라 다르게 만듦 (12')
                ImageView Imageview = (ImageView) Layoutinflater.inflate(R.layout.view_contents_image, this, false);
                Thumbnail_Layout.addView(Imageview);
                Glide.with(this).load(Image).override(1000).thumbnail(0.1f).into(Imageview);         // 흐릿하게 로딩하기
            }
        }else{
            Thumbnail_Layout.setVisibility(GONE);
        }
    } 
}
