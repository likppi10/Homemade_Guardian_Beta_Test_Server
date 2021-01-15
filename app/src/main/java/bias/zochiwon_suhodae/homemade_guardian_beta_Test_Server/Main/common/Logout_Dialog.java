package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.LoginActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity.MainActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import com.google.firebase.auth.FirebaseAuth;

public class Logout_Dialog {

    private Context context;

    public Logout_Dialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

       // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

       // requestWindowFeature : 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

       // setContentView : 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_logout);

       // setCancelable : Dialog의 뒷 부분 터치나 뒤로가기 잠금
        dlg.setCancelable(false);

       // 커스텀 다이얼로그를 노출한다.
        dlg.show();

       // 커스텀 다이얼로그의 각 위젯들을 정의한다. : 로그아웃, 취소
        final LinearLayout LogOut_Button = (LinearLayout) dlg.findViewById(R.id.LogOut_Button);
        final LinearLayout Cancel_Button = (LinearLayout) dlg.findViewById(R.id.Cancel_Button);

        LogOut_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                dlg.dismiss();
                ((MainActivity)context).myStartFinishActivity(LoginActivity.class);
            }
        });

        Cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
    }
}