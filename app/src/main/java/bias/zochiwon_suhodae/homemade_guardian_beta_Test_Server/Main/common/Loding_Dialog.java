package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;

// 게시물 작성이나, 수정 등 액티비티 간의 로딩 부분을 채워주는 Dialog

public class Loding_Dialog {

    private Context context;
    public Loding_Dialog(Context context)
    {
        this.context = context;
    }
    public void callDialog()
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // setCancelable : Dialog 뒷 부분의 터치나 뒤로가기를 허용하지 않는 코드
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_loading);
        ImageView Loding_charactor = (ImageView) dialog.findViewById(R.id.Loding_charactor);
        Glide.with(Loding_charactor.getContext()).load(R.drawable.loading).into(new DrawableImageViewTarget(Loding_charactor));
        dialog.show();
    }

    public void calldismiss(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_loading);
        dialog.dismiss();
    }
}