package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CommunityModel implements Serializable {                                                         // part10 : 게시물 정보 (21'30") // part17 : Serializable(29')
    private String CommunityModel_Title;                 //게시물 제목
    private String CommunityModel_Host_Uid;              //게시물 작성자의 Uid
    private String CommunityModel_Community_Uid;              //게시물의 Uid
    private Date CommunityModel_DateOfManufacture;       //게시물의 작성 시간
    private ArrayList<String> CommunityModel_ImageList;  //게시물의 사진 리스트
    private String CommunityModel_Text;                //게시물의 글
    private ArrayList<String> CommunityModel_LikeList;  //게시물의 좋아요 리스트
    private String CommunityModel_HotCommunity;
    private int CommunityModel_CommentCount;

    public CommunityModel(String CommunityModel_Title, String CommunityModel_Text, ArrayList<String> CommunityModel_ImageList, Date CommunityModel_DateOfManufacture, String CommunityModel_Host_Uid, String CommunityModel_Community_Uid, ArrayList<String> CommunityModel_LikeList, String CommunityModel_HotCommunity, int CommunityModel_CommentCount){
        this.CommunityModel_Title = CommunityModel_Title;
        this.CommunityModel_Text = CommunityModel_Text;
        this.CommunityModel_ImageList = CommunityModel_ImageList;
        this.CommunityModel_DateOfManufacture = CommunityModel_DateOfManufacture;
        this.CommunityModel_Host_Uid = CommunityModel_Host_Uid;
        this.CommunityModel_Community_Uid = CommunityModel_Community_Uid;
        this.CommunityModel_LikeList = CommunityModel_LikeList;
        this.CommunityModel_HotCommunity = CommunityModel_HotCommunity;
        this.CommunityModel_CommentCount = CommunityModel_CommentCount;
    }
    public CommunityModel(String CommunityModel_Title, String CommunityModel_Text, Date CommunityModel_DateOfManufacture, String CommunityModel_Host_Uid, String CommunityModel_Community_Uid, ArrayList<String> CommunityModel_LikeList, String CommunityModel_HotCommunity, int CommunityModel_CommentCount){
        this.CommunityModel_Title = CommunityModel_Title;
        this.CommunityModel_Text = CommunityModel_Text;
        this.CommunityModel_DateOfManufacture = CommunityModel_DateOfManufacture;
        this.CommunityModel_Host_Uid = CommunityModel_Host_Uid;
        this.CommunityModel_Community_Uid = CommunityModel_Community_Uid;
        this.CommunityModel_LikeList = CommunityModel_LikeList;
        this.CommunityModel_HotCommunity = CommunityModel_HotCommunity;
        this.CommunityModel_CommentCount = CommunityModel_CommentCount;
    }

    public Map<String, Object> getCommunityInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("CommunityModel_Title", CommunityModel_Title);
        docData.put("CommunityModel_Text", CommunityModel_Text);
        docData.put("CommunityModel_ImageList", CommunityModel_ImageList);
        docData.put("CommunityModel_Host_Uid", CommunityModel_Host_Uid);
        docData.put("CommunityModel_DateOfManufacture", CommunityModel_DateOfManufacture);
        docData.put("CommunityModel_Community_Uid", CommunityModel_Community_Uid);
        docData.put("CommunityModel_LikeList", CommunityModel_LikeList);
        docData.put("CommunityModel_HotCommunity", CommunityModel_HotCommunity);
        docData.put("CommunityModel_CommentCount", CommunityModel_CommentCount);
        return  docData;
    }

    public CommunityModel(){ }
    public String getCommunityModel_Title(){
        return this.CommunityModel_Title;
    }
    public void setCommunityModel_Title(String CommunityModel_Title){ this.CommunityModel_Title = CommunityModel_Title; }
    public ArrayList<String> getCommunityModel_ImageList(){
        return this.CommunityModel_ImageList;
    }
    public void setCommunityModel_ImageList(ArrayList<String> CommunityModel_ImageList){ this.CommunityModel_ImageList = CommunityModel_ImageList; }
    public String getCommunityModel_Host_Uid(){
        return this.CommunityModel_Host_Uid;
    }
    public void setCommunityModel_Host_Uid(String CommunityModel_Host_Uid){ this.CommunityModel_Host_Uid = CommunityModel_Host_Uid; }
    public Date getCommunityModel_DateOfManufacture(){ return this.CommunityModel_DateOfManufacture; }
    public void setCommunityModel_DateOfManufacture(Date CommunityModel_DateOfManufacture){ this.CommunityModel_DateOfManufacture = CommunityModel_DateOfManufacture; }
    public String getCommunityModel_Community_Uid(){
        return this.CommunityModel_Community_Uid;
    }
    public void setCommunityModel_Community_Uid(String CommunityModel_Community_Uid){ this.CommunityModel_Community_Uid = CommunityModel_Community_Uid; }
    public String getCommunityModel_Text(){return this.CommunityModel_Text;}
    public void setCommunityModel_Text(String CommunityModel_Text){this.CommunityModel_Text = CommunityModel_Text;}
    public ArrayList<String> getCommunityModel_LikeList(){
        return this.CommunityModel_LikeList;
    }
    public void setCommunityModel_LikeList(ArrayList<String> CommunityModel_LikeList){ this.CommunityModel_LikeList = CommunityModel_LikeList; }
    public String getCommunityModel_HotCommunity(){
        return this.CommunityModel_HotCommunity;
    }
    public void setCommunityModel_HotCommunity(String CommunityModel_HotCommunity){ this.CommunityModel_HotCommunity = CommunityModel_HotCommunity; }
    public int getCommunityModel_CommentCount() { return CommunityModel_CommentCount; }
    public void setCommunityModel_CommentCount(int CommunityModel_CommentCount) { this.CommunityModel_CommentCount = CommunityModel_CommentCount; }
}
