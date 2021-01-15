package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;

// 리뷰창에서 후기를 입력하는 부분

public class WriteReviewActivity extends BasicActivity {    // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
                                                            // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private TextView Writen_Review_TextView;                    // 작성한 후기 TextView
                                                            // 5. 기타 변수
    private Context context;

    public WriteReviewActivity() { }

    public WriteReviewActivity(Context context, TextView WriteReview_EditText) {
        this.context = context;
        this.Writen_Review_TextView = Writen_Review_TextView;
    }
    public void callFunction(final TextView Writen_Review_TextView) {

       // Dialog 생성
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(R.layout.activity_writereview);
        dlg.show();

       // 작성 Button, 후기 입력 EditText find
        final Button Review_Write_Button;
        final EditText WriteReview_EditText;
        Review_Write_Button = (Button) dlg.findViewById(R.id.Review_Write_Button);
        WriteReview_EditText = (EditText) dlg.findViewById(R.id.WriteReview_EditText);

       // 이전에 입력한 것이 있다면 setText, 확인 Button setOnClickListener
        WriteReview_EditText.setText(Writen_Review_TextView.getText());
        Review_Write_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Writen_Review_TextView.setText(WriteReview_EditText.getText());
                dlg.dismiss();
            }
        });
    }
}