package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewModel implements Serializable {
    private String ReviewModel_Uid;             //리뷰의 Uid
    private String ReviewModel_PostUid;         //작성할 리뷰의 포스트 Uid
    private String ReviewModel_To_User_Uid;     //리뷰 작성자의 Uid
    private String ReviewModel_To_User_NickName;     //리뷰 작성자의 닉네임
    private String ReviewModel_To_User_ProfileImage;     //리뷰 작성자의 프로필이미지
    private String ReviewModel_Review;          //리뷰의 내용
    private int ReviewModel_Selected_Review; //선택한 리뷰의 평가 지표
    private Date ReviewModel_DateOfManufacture; //리뷰의 생성일자

    public ReviewModel(String ReviewModel_Uid, String ReviewModel_PostUid, String ReviewModel_To_User_Uid,String ReviewModel_To_User_NickName, String ReviewModel_To_User_ProfileImage,String ReviewModel_Review, int ReviewModel_Selected_Review, Date ReviewModel_DateOfManufacture){     // part5 : 생성자 초기화 (7')
        this.ReviewModel_Uid = ReviewModel_Uid;
        this.ReviewModel_PostUid = ReviewModel_PostUid;
        this.ReviewModel_To_User_Uid = ReviewModel_To_User_Uid;
        this.ReviewModel_To_User_NickName = ReviewModel_To_User_NickName;
        this.ReviewModel_To_User_ProfileImage = ReviewModel_To_User_ProfileImage;
        this.ReviewModel_Review = ReviewModel_Review;
        this.ReviewModel_Selected_Review = ReviewModel_Selected_Review;    // + : 사용자 리스트 수정 (날짜 정보 추가)
        this.ReviewModel_DateOfManufacture = ReviewModel_DateOfManufacture;

    }


    public final Map<String, Object> getReviewModel(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("ReviewModel_Uid", ReviewModel_Uid);
        docData.put("ReviewModel_PostUid", ReviewModel_PostUid);
        docData.put("ReviewModel_To_User_Uid", ReviewModel_To_User_Uid);
        docData.put("ReviewModel_To_User_NickName", ReviewModel_To_User_NickName);
        docData.put("ReviewModel_To_User_ProfileImage", ReviewModel_To_User_ProfileImage);
        docData.put("ReviewModel_Review", ReviewModel_Review);
        docData.put("ReviewModel_Selected_Review", ReviewModel_Selected_Review);
        docData.put("ReviewModel_DateOfManufacture", ReviewModel_DateOfManufacture);
        return  docData;
    }

    public ReviewModel() { }
    public String getReviewModel_Uid() {
        return ReviewModel_Uid;
    }
    public void setReviewModel_Uid(String ReviewModel_Uid) { this.ReviewModel_Uid = ReviewModel_Uid; }

    public String getReviewModel_PostUid() {
        return ReviewModel_PostUid;
    }
    public void setReviewModel_PostUid(String ReviewModel_PostUid) { this.ReviewModel_PostUid = ReviewModel_PostUid; }

    public String getReviewModel_To_User_Uid() {
        return ReviewModel_To_User_Uid;
    }
    public void setReviewModel_To_User_Uid(String ReviewModel_To_User_Uid) { this.ReviewModel_To_User_Uid = ReviewModel_To_User_Uid; }

    public String getReviewModel_To_User_NickName() {
        return ReviewModel_To_User_NickName;
    }
    public void setReviewModel_To_User_NickName(String ReviewModel_To_User_NickName) { this.ReviewModel_To_User_NickName = ReviewModel_To_User_NickName; }

    public String getReviewModel_To_User_ProfileImage() {
        return ReviewModel_To_User_ProfileImage;
    }
    public void setReviewModel_To_User_ProfileImage(String ReviewModel_To_User_ProfileImage) { this.ReviewModel_To_User_ProfileImage = ReviewModel_To_User_ProfileImage; }

    public String getReviewModel_Review(){
        return this.ReviewModel_Review;
    }
    public void setReviewModel_Review(String ReviewModel_Review){ this.ReviewModel_Review = ReviewModel_Review; }

    public int getReviewModel_Selected_Review(){
        return this.ReviewModel_Selected_Review;
    }
    public void setReviewModel_Selected_Review(int ReviewModel_Selected_Review){ this.ReviewModel_Selected_Review = ReviewModel_Selected_Review; }

    public Date getReviewModel_DateOfManufacture() { return ReviewModel_DateOfManufacture; }
    public void setReviewModel_DateOfManufacture(Date ReviewModel_DateOfManufacture) { this.ReviewModel_DateOfManufacture = ReviewModel_DateOfManufacture; }
}