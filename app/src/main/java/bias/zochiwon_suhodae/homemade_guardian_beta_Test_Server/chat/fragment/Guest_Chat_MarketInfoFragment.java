package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity.ChatActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//게스트 포스트 정보 프레그먼트
public class Guest_Chat_MarketInfoFragment extends Fragment {

    String MarketModel_Market_Uid;
    String CurrentUserUid;
    MarketModel marketModel;
    TextView Chat_MarketInfo_Title;
    TextView Chat_MarketInfo_Text;
    ImageView Chat_MarketInfo_Image;
    CardView Chat_PostInfo_Card;


    ChatActivity chatActivity;

    TextView Chat_MarketInfo_dealText;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        chatActivity = (ChatActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        chatActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        final ViewGroup View = (ViewGroup) inflater.inflate(R.layout.fragment_guest_chat_marketinfo, container, false);
        Chat_MarketInfo_Title = (TextView)View.findViewById(R.id.Chat_PostInfo_Title);
        Chat_MarketInfo_Text = (TextView)View.findViewById(R.id.Chat_PostInfo_Text);
        Chat_MarketInfo_Image = (ImageView) View.findViewById(R.id.Chat_PostInfo_Image);
        Chat_MarketInfo_dealText = (TextView)View.findViewById(R.id.Chat_PostInfo_dealText);
        Chat_PostInfo_Card = View.findViewById(R.id.Chat_PostInfo_Card);

        CurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle Marketbundle = getArguments();
        MarketModel_Market_Uid = Marketbundle.getString("MarketModel_Market_Uid");

        MarketInfo(MarketModel_Market_Uid);
        return View;
    }

    //마켓 정보
    void MarketInfo(String RoomUid){
        DocumentReference docRef_USERS_HostUid = FirebaseFirestore.getInstance().collection("MARKETS").document(MarketModel_Market_Uid);
        docRef_USERS_HostUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                marketModel = documentSnapshot.toObject(MarketModel.class);
                Chat_MarketInfo_Title.setText(marketModel.getMarketModel_Title());
                Chat_MarketInfo_Text.setText(marketModel.getMarketModel_Text());
                //post의 이미지 섬네일 띄우기
                if(marketModel.getMarketModel_ImageList() != null){
                    Chat_PostInfo_Card.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(marketModel.getMarketModel_ImageList().get(0)).centerCrop().override(500).into(Chat_MarketInfo_Image);
                }
                else{
                    Chat_MarketInfo_Image.setVisibility(View.GONE);
                }
                //X : 예약이나 거래완료 사항이 없음 !X : [호스트]의 상대방 Uid
                if(!marketModel.getMarketModel_reservation().equals("X")){
                    if(!marketModel.getMarketModel_deal().equals("X")) {
                        Chat_MarketInfo_dealText.setText("거래완료");
                        Chat_MarketInfo_dealText.setTextColor(Color.parseColor("#505050"));
                    }
                    else{
                        Chat_MarketInfo_dealText.setText("예약중");
                        Chat_MarketInfo_dealText.setTextColor(Color.parseColor("#e65d5d"));
                    }
                }
                else{
                    Chat_MarketInfo_dealText.setText("");
                }

            }
        });
    }
}
