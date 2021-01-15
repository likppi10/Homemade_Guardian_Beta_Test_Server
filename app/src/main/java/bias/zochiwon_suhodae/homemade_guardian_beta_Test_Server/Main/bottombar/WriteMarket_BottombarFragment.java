package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.bottombar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class WriteMarket_BottombarFragment extends Fragment {
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private EditText selectedEditText;
    private EditText titleEditText;
    private MarketModel marketModel;
    private int pathCount;
    public final static int REQUEST_CODE = 1;
    public  ArrayList<String> selectedPhotos = new ArrayList<>();
    ////////////////////////
    private ImageView Selected_ImageView;
    private Button Select_Market_Image_Button;
    private ArrayList<String> ImageList;            //게시물의 이미지 리스트
    private ViewPager Viewpager;                    //이미지들을 보여주기 위한 ViewPager 선언
    private ImageView FoodMarketbtn;
    private ImageView LifeMarketbtn;
    private ImageView BorrowMarketbtn;
    private ImageView WorkMarketbtn;
    private String Category = null;

    private String title;
    private String textcontents;

    /////////////////////////////////////////////////////
    private ImageView imagelist0, imagelist1, imagelist2, imagelist3, imagelist4;
    //////////////////////////////////////////////////////


    private RelativeLayout writefragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_writemarket, container, false);
        Toolbar myToolbar = view.findViewById(R.id.toolbar_title_write);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        writefragment = view.findViewById(R.id.writefragment);

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }


}