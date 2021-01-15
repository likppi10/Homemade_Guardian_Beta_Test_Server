package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.activity.MarketActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

// SearchResultFragment와 연결된 어댑터이다. onBindViewHolder로 카드뷰에 검색된 게시물의 정보들을 담는 역할을 한다. !검색된 결과의 게시물 나열!

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MainViewHolder> {
                                                            // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                            // 2. 변수 및 배열
    private ArrayList<MarketModel> ArrayList_MarketModel;       // 게시물에 담을 MarketModel
                                                            // 5.기타 변수
    private Activity Activity;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView Cardview;
        MainViewHolder(CardView v) {
            super(v);
            Cardview = v;
        }
    }

    public SearchResultAdapter(Activity activity, ArrayList<MarketModel> arrayList_Marketmodel) {
        this.ArrayList_MarketModel = arrayList_Marketmodel;
        this.Activity = activity;
    }

   // 검색한 결과를 Market에서 불러온 게시물 내용으로 화면에 나타내는 Holder
    @NonNull
    @Override
    public SearchResultAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView Cardview = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent, false);
        final MainViewHolder Mainviewholder = new MainViewHolder(Cardview);
        Cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent_MarketActivity = new Intent(Activity, MarketActivity.class);
                Intent_MarketActivity.putExtra("marketInfo", ArrayList_MarketModel.get(Mainviewholder.getAdapterPosition()));
                Activity.startActivity(Intent_MarketActivity);
            }
        });
        return Mainviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {

        CardView Contents_CardView = holder.Cardview;

       // 제목, 내용, 카테고리, 좋아요 개수, 댓글 개수, 작성일을 나타낼 TextView find
        TextView Title_TextView = Contents_CardView.findViewById(R.id.Post_Title_TextView);
        TextView Contents_TextView = Contents_CardView.findViewById(R.id.Post_Contents_TextView);
        TextView Market_Category = Contents_CardView.findViewById(R.id.Post_Category);
        TextView Market_LikeCount = Contents_CardView.findViewById(R.id.Post_LikeCount);
        TextView Market_CommentCount = Contents_CardView.findViewById(R.id.comment_count_text);
        TextView DateOfManufacture_TextView = Contents_CardView.findViewById(R.id.Post_DateOfManufacture);

       // 어떤 Marketmodel? : 해당 position의 Marketmodel
        MarketModel marketModel = ArrayList_MarketModel.get(position);

       // 제목, 내용, 카테고리, 좋아요 개수, 댓글 개수, 작성일을 나타낼 TextView set
        Title_TextView.setText(marketModel.getMarketModel_Title());
        Contents_TextView.setText(marketModel.getMarketModel_Text());
        Market_Category.setText(marketModel.getMarketModel_Category());
        Market_LikeCount.setText(String.valueOf(marketModel.getMarketModel_LikeList().size()));
        Market_CommentCount.setText(String.valueOf(marketModel.getMarketModel_CommentCount()));
        DateOfManufacture_TextView.setText(new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(marketModel.getMarketModel_DateOfManufacture()));

       // if : 게시물에 이미지가 존재한다면 첫번째 사진을 썸네일 사진으로 설정한다. / 없으면 ImageView 자체를 GONE
        ArrayList<String> ArrayList_ImageList = marketModel.getMarketModel_ImageList();
        ImageView Thumbnail_ImageView = Contents_CardView.findViewById(R.id.Post_ImageView);
        if(ArrayList_ImageList != null) {
            Thumbnail_ImageView.setVisibility(View.VISIBLE);
            String Image = ArrayList_ImageList.get(0);
            Glide.with(Activity).load(Image).centerCrop().override(500).thumbnail(0.1f).into(Thumbnail_ImageView);
        }else {
            Thumbnail_ImageView.setVisibility(View.GONE);
        }

       // 게시물의 거래 상태 (거래 전(표시X), 예약중, 거래완료)
        TextView market_state = Contents_CardView.findViewById(R.id.market_state);
        market_state.setText("");
        GradientDrawable state_none= (GradientDrawable) ContextCompat.getDrawable(this.Activity, R.drawable.state_none);
        market_state.setBackground(state_none);
        market_state.setVisibility(View.INVISIBLE);
        if(!marketModel.getMarketModel_reservation().equals("X")){
            market_state.setVisibility(View.VISIBLE);
            if(!marketModel.getMarketModel_deal().equals("X")){
                market_state.setText("거래완료");
                GradientDrawable state_deal= (GradientDrawable) ContextCompat.getDrawable(this.Activity, R.drawable.state_deal);
                market_state.setBackground(state_deal);
            }else{
                market_state.setText("예약중");
                GradientDrawable state_reserved= (GradientDrawable) ContextCompat.getDrawable(this.Activity, R.drawable.state_reserved);
                market_state.setBackground(state_reserved);
            }
        }
    }

    @Override
    public int getItemViewType(int position){ return position; }

    @Override
    public int getItemCount() {
        return ArrayList_MarketModel.size();
    }
}