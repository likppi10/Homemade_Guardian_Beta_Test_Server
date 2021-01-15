package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity.ChatActivity;

// 채팅방은 있는데 안에 있는 market이 삭제 되었을 때
public class Nonepost_chat_MarketInfoFragment extends Fragment {

    TextView Chat_MarketInfo_Title;
    TextView Chat_MarketInfo_Text;
    ChatActivity chatActivity;


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
        final ViewGroup View = (ViewGroup) inflater.inflate(R.layout.fragment_nonepost_chat_marketinfo, container, false);
        Chat_MarketInfo_Title = (TextView) View.findViewById(R.id.Chat_PostInfo_Title);
        Chat_MarketInfo_Text = (TextView) View.findViewById(R.id.Chat_PostInfo_Text);

        Chat_MarketInfo_Title.setText("삭제된 게시물 입니다.");
        Chat_MarketInfo_Text.setText("내용이 없습니다.");

        return  View;
    }
}
