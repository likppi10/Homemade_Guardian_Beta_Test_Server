package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class MarketModel implements Serializable {                                                         // part10 : 게시물 정보 (21'30") // part17 : Serializable(29')
    private String MarketModel_Title;                   //게시물 제목
    private String MarketModel_Host_Uid;                //게시물 작성자의 Uid
    private String MarketModel_Market_Uid;              //게시물의 Uid
    private Date MarketModel_DateOfManufacture;         //게시물의 작성 시간
    private ArrayList<String> MarketModel_ImageList;    //게시물의 사진 리스트
    private String MarketModel_Text;                    //게시물의 글
    private String MarketModel_Category;                //게시물의 카테고리
    private ArrayList<String> MarketModel_LikeList;     //게시물의 좋아요 리스트
    private String MarketModel_HotMarket;               //게시물의 핫게시물 유무
    private String MarketModel_reservation;             //게시자가 예약을 할때 안했을때는 0 했다면 1
    private String MarketModel_deal;                    //게시물이 거래 안했을때 0, 했으면 1
    private int MarketModel_CommentCount;               //게시물의 댓글 개수

    public MarketModel(String MarketModel_Title, String MarketModel_Text, ArrayList<String> MarketModel_ImageList, Date MarketModel_DateOfManufacture, String MarketModel_Host_Uid, String MarketModel_Market_Uid, String MarketModel_Category, ArrayList<String> MarketModel_LikeList, String MarketModel_HotMarket, String MarketModel_reservation, String MarketModel_deal, int MarketModel_CommentCount){
        this.MarketModel_Title = MarketModel_Title;
        this.MarketModel_Text = MarketModel_Text;
        this.MarketModel_ImageList = MarketModel_ImageList;
        this.MarketModel_DateOfManufacture = MarketModel_DateOfManufacture;
        this.MarketModel_Host_Uid = MarketModel_Host_Uid;
        this.MarketModel_Market_Uid = MarketModel_Market_Uid;
        this.MarketModel_Category = MarketModel_Category;
        this.MarketModel_LikeList = MarketModel_LikeList;
        this.MarketModel_HotMarket = MarketModel_HotMarket;
        this.MarketModel_reservation = MarketModel_reservation;
        this.MarketModel_deal = MarketModel_deal;
        this.MarketModel_CommentCount = MarketModel_CommentCount;
    }
    public MarketModel(String MarketModel_Title, String MarketModel_Text, Date MarketModel_DateOfManufacture, String MarketModel_Host_Uid, String MarketModel_Market_Uid, String MarketModel_Category, ArrayList<String> MarketModel_LikeList, String MarketModel_HotMarket, String MarketModel_reservation, String MarketModel_deal, int MarketModel_CommentCount){
        this.MarketModel_Title = MarketModel_Title;
        this.MarketModel_Text = MarketModel_Text;
        this.MarketModel_DateOfManufacture = MarketModel_DateOfManufacture;
        this.MarketModel_Host_Uid = MarketModel_Host_Uid;
        this.MarketModel_Market_Uid = MarketModel_Market_Uid;
        this.MarketModel_Category = MarketModel_Category;
        this.MarketModel_LikeList = MarketModel_LikeList;
        this.MarketModel_HotMarket = MarketModel_HotMarket;
        this.MarketModel_reservation = MarketModel_reservation;
        this.MarketModel_deal = MarketModel_deal;
        this.MarketModel_CommentCount = MarketModel_CommentCount;
    }

    public Map<String, Object> getMarketInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("MarketModel_Title", MarketModel_Title);
        docData.put("MarketModel_Text", MarketModel_Text);
        docData.put("MarketModel_ImageList", MarketModel_ImageList);
        docData.put("MarketModel_Host_Uid", MarketModel_Host_Uid);
        docData.put("MarketModel_DateOfManufacture", MarketModel_DateOfManufacture);
        docData.put("MarketModel_Market_Uid", MarketModel_Market_Uid);
        docData.put("MarketModel_Category", MarketModel_Category);
        docData.put("MarketModel_LikeList", MarketModel_LikeList);
        docData.put("MarketModel_HotMarket", MarketModel_HotMarket);
        docData.put("MarketModel_reservation", MarketModel_reservation);
        docData.put("MarketModel_deal", MarketModel_deal);
        docData.put("MarketModel_CommentCount", MarketModel_CommentCount);
        return  docData;
    }

    public MarketModel(){ }
    public String getMarketModel_Title(){
        return this.MarketModel_Title;
    }
    public void setMarketModel_Title(String MarketModel_Title){ this.MarketModel_Title = MarketModel_Title; }
    public ArrayList<String> getMarketModel_ImageList(){
        return this.MarketModel_ImageList;
    }
    public void setMarketModel_ImageList(ArrayList<String> MarketModel_ImageList){ this.MarketModel_ImageList = MarketModel_ImageList; }
    public String getMarketModel_Host_Uid(){
        return this.MarketModel_Host_Uid;
    }
    public void setMarketModel_Host_Uid(String MarketModel_Host_Uid){ this.MarketModel_Host_Uid = MarketModel_Host_Uid; }
    public Date getMarketModel_DateOfManufacture(){
        return this.MarketModel_DateOfManufacture;
    }
    public void setMarketModel_DateOfManufacture(Date MarketModel_DateOfManufacture){ this.MarketModel_DateOfManufacture = MarketModel_DateOfManufacture; }
    public String getMarketModel_Market_Uid(){
        return this.MarketModel_Market_Uid;
    }
    public void setMarketModel_Market_Uid(String MarketModel_Market_Uid){ this.MarketModel_Market_Uid = MarketModel_Market_Uid; }
    public String getMarketModel_Text(){return this.MarketModel_Text;}
    public void setMarketModel_Text(String MarketModel_Text){this.MarketModel_Text = MarketModel_Text;}
    public String getMarketModel_Category(){return this.MarketModel_Category;}
    public void setMarketModel_Category(String MarketModel_Category){this.MarketModel_Category = MarketModel_Category;}
    public ArrayList<String> getMarketModel_LikeList(){
        return this.MarketModel_LikeList;
    }
    public void setMarketModel_LikeList(ArrayList<String> MarketModel_LikeList){ this.MarketModel_LikeList = MarketModel_LikeList; }
    public String getMarketModel_HotMarket(){
        return this.MarketModel_HotMarket;
    }
    public void setMarketModel_HotMarket(String MarketModel_HotMarket){ this.MarketModel_HotMarket = MarketModel_HotMarket; }
    public String getMarketModel_reservation(){
        return this.MarketModel_reservation;
    }
    public void setMarketModel_reservation(String MarketModel_reservation){ this.MarketModel_reservation = MarketModel_reservation; }
    public String getMarketModel_deal(){
        return this.MarketModel_deal;
    }
    public void setMarketModel_deal(String MarketModel_deal){ this.MarketModel_deal = MarketModel_deal; }
    public int getMarketModel_CommentCount() { return MarketModel_CommentCount; }
    public void setMarketModel_CommentCount(int MarketModel_CommentCount) { this.MarketModel_CommentCount = MarketModel_CommentCount; }
}
