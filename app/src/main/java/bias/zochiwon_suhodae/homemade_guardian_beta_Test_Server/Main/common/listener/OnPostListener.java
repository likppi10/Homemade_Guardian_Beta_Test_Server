package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.listener;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.Community_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.Market_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;

//삭제를 할 때에 FirebaseHelper와 연결되어 사용되는 Interface이다.

public interface OnPostListener {
    void onDelete(MarketModel marketModel);
    void oncommentDelete(Market_CommentModel market_commentModel);
    void oncommunityDelete(CommunityModel communityModel);
    void oncommnitycommentDelete(Community_CommentModel community_commentModel);
    public void onModify();

}
