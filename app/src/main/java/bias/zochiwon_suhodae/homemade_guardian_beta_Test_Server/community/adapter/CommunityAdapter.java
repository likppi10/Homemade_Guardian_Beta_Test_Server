package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.community.activity.CommunityActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

// CommunityFragment와 연결된 어댑터이다. onBindViewHolder로 카드뷰에 게시물의 정보들을 담는 역할을 한다.

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MainViewHolder> {
                                                                // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                                // 2. 변수 및 배열
    private ArrayList<CommunityModel> ArrayList_CommunityModel;       // Community에 담을 CommunityModel
                                                                // 5.기타 변수
    private Activity Activity;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView Cardview;
        MainViewHolder(CardView v) {
            super(v);
            Cardview = v;
        }
    }

    public CommunityAdapter(Activity activity, ArrayList<CommunityModel> arrayList_CommunityModel) {
        this.ArrayList_CommunityModel = arrayList_CommunityModel;
        this.Activity = activity;
    }

    // Community에서 불러온 게시물 내용을 화면에 나타내는 Holder
    @NonNull
    @Override
    public CommunityAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView Cardview = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        final MainViewHolder Mainviewholder = new MainViewHolder(Cardview);
        Cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent_CommunityActivity = new Intent(Activity, CommunityActivity.class);
                Intent_CommunityActivity.putExtra("communityInfo", ArrayList_CommunityModel.get(Mainviewholder.getAdapterPosition()));
                Activity.startActivity(Intent_CommunityActivity);
            }
        });
        return Mainviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {

        CardView Contents_CardView = holder.Cardview;

       // 제목, 내용, 좋아요 개수, 댓글 개수, 작성일을 나타낼 TextView find
        TextView Title_TextView = Contents_CardView.findViewById(R.id.Post_Title_TextView);
        TextView Contents_TextView = Contents_CardView.findViewById(R.id.Post_Contents_TextView);
        TextView Community_LikeCount = Contents_CardView.findViewById(R.id.Post_LikeCount);
        TextView Community_CommentCount = Contents_CardView.findViewById(R.id.comment_count_text);
        TextView DateOfManufacture_TextView = Contents_CardView.findViewById(R.id.Post_DateOfManufacture);

       // 어떤 CommunityModel? : 해당 position의 CommunityModel
        CommunityModel communityModel = ArrayList_CommunityModel.get(position);

       // 제목, 내용, 좋아요 개수, 댓글 개수, 작성일을 나타낼 TextView set
        Title_TextView.setText(communityModel.getCommunityModel_Title());
        Contents_TextView.setText(communityModel.getCommunityModel_Text());
        Community_LikeCount.setText(String.valueOf(communityModel.getCommunityModel_LikeList().size()));
        Community_CommentCount.setText(String.valueOf(communityModel.getCommunityModel_CommentCount()));
        DateOfManufacture_TextView.setText(new SimpleDateFormat("MM/dd hh:mm",
                Locale.getDefault()).format(communityModel.getCommunityModel_DateOfManufacture()));

       // if : 게시물에 이미지가 존재한다면 첫번째 사진을 썸네일 사진으로 설정한다. / 없으면 ImageView 자체를 GONE
        ArrayList<String> ArrayList_ImageList = communityModel.getCommunityModel_ImageList();
        ImageView Thumbnail_ImageView = Contents_CardView.findViewById(R.id.Post_ImageView);
        if(ArrayList_ImageList != null) {
            Thumbnail_ImageView.setVisibility(View.VISIBLE);
            String Image = ArrayList_ImageList.get(0);
            Glide.with(Activity).load(Image).centerCrop().override(500).thumbnail(0.1f).into(Thumbnail_ImageView);
        }else {
            Thumbnail_ImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position){ return position; }

    @Override
    public int getItemCount() {
        return ArrayList_CommunityModel.size();
    }
}