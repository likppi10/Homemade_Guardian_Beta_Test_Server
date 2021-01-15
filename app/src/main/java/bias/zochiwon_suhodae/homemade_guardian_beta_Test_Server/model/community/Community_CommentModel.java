package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community;

import java.io.Serializable;
import java.util.Date;

public class Community_CommentModel implements Serializable {
    private String Community_CommentModel_Comment;    //댓글 내용
    private String Community_CommentModel_Host_Image; //댓글 작성자 프로필
    private String Community_CommentModel_Host_Uid;   //댓글 작성자 Uid
    private String Community_CommentModel_Host_Name;  //댓글 작성자 이름
    private String Community_CommentModel_Market_Uid;   //댓글이 쓰여진 게시물의 Uid
    private String Community_CommentModel_Comment_Uid;//댓글의 Uid
    private Date Community_CommentModel_DateOfManufacture;    //댓글 작성 시간

    public Community_CommentModel(String Community_CommentModel_Host_Uid, String Community_CommentModel_Comment, Date Community_CommentModel_DateOfManufacture, String Community_CommentModel_Host_Name, String Community_CommentModel_Comment_Uid, String Community_CommentModel_Market_Uid, String Community_CommentModel_Host_Image){
        this.Community_CommentModel_Host_Uid = Community_CommentModel_Host_Uid;
        this.Community_CommentModel_Comment = Community_CommentModel_Comment;
        this.Community_CommentModel_DateOfManufacture = Community_CommentModel_DateOfManufacture;
        this.Community_CommentModel_Host_Name = Community_CommentModel_Host_Name;
        this.Community_CommentModel_Comment_Uid = Community_CommentModel_Comment_Uid;
        this.Community_CommentModel_Market_Uid = Community_CommentModel_Market_Uid;
        this.Community_CommentModel_Host_Image = Community_CommentModel_Host_Image;
    }

    public Community_CommentModel(){ }
    public String getCommunity_CommentModel_Host_Uid() { return Community_CommentModel_Host_Uid; }
    public void setCommunity_CommentModel_Host_Uid(String Community_CommentModel_Host_Uid) { this.Community_CommentModel_Host_Uid = Community_CommentModel_Host_Uid; }
    public String getCommunity_CommentModel_Comment() { return Community_CommentModel_Comment; }
    public void setCommunity_CommentModel_Comment(String Community_CommentModel_Comment) { this.Community_CommentModel_Comment = Community_CommentModel_Comment; }
    public Date getCommunity_CommentModel_DateOfManufacture() { return Community_CommentModel_DateOfManufacture; }
    public void setCommunity_CommentModel_DateOfManufacture(Date Community_CommentModel_DateOfManufacture) { this.Community_CommentModel_DateOfManufacture = Community_CommentModel_DateOfManufacture; }
    public String getCommunity_CommentModel_Host_Name() { return Community_CommentModel_Host_Name; }
    public void setCommunity_CommentModel_Host_Name(String Community_CommentModel_Host_Name) { this.Community_CommentModel_Host_Name = Community_CommentModel_Host_Name; }
    public String getCommunity_CommentModel_Comment_Uid() { return Community_CommentModel_Comment_Uid; }
    public void setCommunity_CommentModel_Comment_Uid(String Community_CommentModel_Comment_Uid) { this.Community_CommentModel_Comment_Uid = Community_CommentModel_Comment_Uid; }
    public String getCommunity_CommentModel_Market_Uid() { return Community_CommentModel_Market_Uid; }
    public void setCommunity_CommentModel_Market_Uid(String Community_CommentModel_Market_Uid) { this.Community_CommentModel_Market_Uid = Community_CommentModel_Market_Uid; }
    public String getCommunity_CommentModel_Host_Image(){ return this.Community_CommentModel_Host_Image; }
    public void setCommunity_CommentModel_Host_Image(String Community_CommentModel_Host_Image){ this.Community_CommentModel_Host_Image = Community_CommentModel_Host_Image; }

}
