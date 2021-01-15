package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.market.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import java.util.ArrayList;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util;

// GalleryActivity와 연결되어 사용되는 어댑터이다.

public class GalleryAdapter  extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
                                                        // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5.기타 변수
                                                        // 2. 변수 및 배열
    private ArrayList<String> ArrayList_ImageList;          //GalleryActivity에서 return 한 listOfImage를 받는 ArrayList<String>
                                                        // 5.기타 변수
    private Activity Activity;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView Cardview;
        GalleryViewHolder(CardView v) {
            super(v);
            Cardview = v;
        }
    }

    public GalleryAdapter(Activity activity, ArrayList<String> myDataset) {
        ArrayList_ImageList = myDataset;
        this.Activity = activity;
    }

   //앨범에서 불러온 사진을 화면에 나타내는 Holder
    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView Cardview = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        final GalleryViewHolder Galleryviewholder = new GalleryViewHolder(Cardview);
        Cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Resultintent = new Intent();
                Resultintent.putExtra(Util.INTENT_PATH, ArrayList_ImageList.get(Galleryviewholder.getAdapterPosition()));
                Activity.setResult(Activity.RESULT_OK, Resultintent);
                Activity.finish();
            }
        });
        return Galleryviewholder;
    }

   // 보여지는 적용사항
    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView Cardview = holder.Cardview;
        ImageView Imageview = Cardview.findViewById(R.id.Directory_Image);
        Glide.with(Activity).load(ArrayList_ImageList.get(position)).centerCrop().override(500).into(Imageview);
    }

    @Override
    public int getItemCount() {
        return ArrayList_ImageList.size();
    }
}