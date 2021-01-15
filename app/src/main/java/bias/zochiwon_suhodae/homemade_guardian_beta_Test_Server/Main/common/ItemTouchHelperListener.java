package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    void onItemSwipe(int position);
    void onRightClick(int position, RecyclerView.ViewHolder viewHolder);
}
