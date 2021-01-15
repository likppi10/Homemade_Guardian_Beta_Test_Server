package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserModel implements Serializable {
    private String UserModel_ID; //아이디
    private String UserModel_Uid; //uid
    private String UserModel_NickName; //지금은 tnvnfdla1214 -> 번듯한 오크 만약 이용자가 임력을 하지 않을 시 전에꺼로 한다.
    private String UserModel_BirthDay; //생일
    private int UserModel_University; //학교 0 : 홍대 세종 , 1 : 고대세종
    private String UserModel_ProfileImage; //프로필 사진
    private Date UserModel_DateOfManufacture; //생성일자
    private String UserModel_Token;//토큰값

    private ArrayList<HashMap<String, String>> UserModel_Unreview = new ArrayList<HashMap<String, String>>();

    private ArrayList<String> UserModel_UnReViewUserList;  //리뷰를 작성하지 않은 유저리스트
    private ArrayList<String> UserModel_UnReViewPostList;  //리뷰를 작성하지 않은 포스트리스트

    private ArrayList<String> UserModel_kindReviewList;  //리뷰를 작성하지 않은 유저리스트
    private ArrayList<String> UserModel_correctReviewList;  //리뷰를 작성하지 않은 포스트리스트
    private ArrayList<String> UserModel_completeReviewList;  //리뷰를 작성하지 않은 유저리스트
    private ArrayList<String> UserModel_badReviewList;  //리뷰를 작성하지 않은 유저리스트
    private ArrayList<String> UserModel_WritenReviewList;  //리뷰를 작성한 유저리스트
    private ArrayList<String> UserModel_Market_reservationList;  //예약된 마켓포스트 리스트
    private ArrayList<String> UserModel_Market_dealList;  //거래된 마켓포스트 리스트

    public UserModel(String UserModel_NickName, String UserModel_BirthDay, Date UserModel_DateOfManufacture, int UserModel_University , String UserModel_ProfileImage ,ArrayList<String> UserModel_kindReviewList,ArrayList<String> UserModel_correctReviewList,ArrayList<String> UserModel_completeReviewList,ArrayList<String> UserModel_badReviewList,ArrayList<String> UserModel_WritenReviewList,ArrayList<String> UserModel_Market_reservationList,ArrayList<String> UserModel_Market_dealList,String UserModel_Token){     // part5 : 생성자 초기화 (7')
        this.UserModel_NickName = UserModel_NickName;
        this.UserModel_BirthDay = UserModel_BirthDay;
        this.UserModel_University = UserModel_University;
        this.UserModel_ProfileImage = UserModel_ProfileImage;
        this.UserModel_DateOfManufacture = UserModel_DateOfManufacture;    // + : 사용자 리스트 수정 (날짜 정보 추가)
        this.UserModel_kindReviewList = UserModel_kindReviewList;
        this.UserModel_correctReviewList = UserModel_correctReviewList;
        this.UserModel_completeReviewList = UserModel_completeReviewList;
        this.UserModel_badReviewList = UserModel_badReviewList;
        this.UserModel_WritenReviewList = UserModel_WritenReviewList;
        this.UserModel_Market_reservationList = UserModel_Market_reservationList;
        this.UserModel_Market_dealList = UserModel_Market_dealList;
        this.UserModel_Token = UserModel_Token;
    }

    public UserModel(String UserModel_NickName,String UserModel_BirthDay, Date UserModel_DateOfManufacture, int UserModel_University, ArrayList<String> UserModel_kindReviewList,ArrayList<String> UserModel_correctReviewList,ArrayList<String> UserModel_completeReviewList,ArrayList<String> UserModel_badReviewList,ArrayList<String> UserModel_WritenReviewList,ArrayList<String> UserModel_Market_reservationList,ArrayList<String> UserModel_Market_dealList,String UserModel_Token){      // + : 사용자 리스트 수정(날짜 정보 추가)
        this.UserModel_NickName = UserModel_NickName;
        this.UserModel_BirthDay = UserModel_BirthDay;
        this.UserModel_DateOfManufacture = UserModel_DateOfManufacture;
        this.UserModel_University = UserModel_University;
        this.UserModel_kindReviewList = UserModel_kindReviewList;
        this.UserModel_correctReviewList = UserModel_correctReviewList;
        this.UserModel_completeReviewList = UserModel_completeReviewList;
        this.UserModel_badReviewList = UserModel_badReviewList;
        this.UserModel_WritenReviewList = UserModel_WritenReviewList;
        this.UserModel_Market_reservationList = UserModel_Market_reservationList;
        this.UserModel_Market_dealList = UserModel_Market_dealList;
        this.UserModel_Token = UserModel_Token;
    }

    public UserModel(){
    }


    public Map<String, Object> getUserInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("UserModel_ID", UserModel_ID);
        docData.put("UserModel_Uid", UserModel_Uid);
        docData.put("UserModel_NickName", UserModel_NickName);
        docData.put("UserModel_BirthDay", UserModel_BirthDay);
        docData.put("UserModel_University", UserModel_University);
        docData.put("UserModel_ProfileImage", UserModel_ProfileImage);
        docData.put("UserModel_DateOfManufacture", UserModel_DateOfManufacture);
        docData.put("UserModel_kindReviewList", UserModel_kindReviewList);
        docData.put("UserModel_correctReviewList", UserModel_correctReviewList);
        docData.put("UserModel_completeReviewList", UserModel_completeReviewList);
        docData.put("UserModel_badReviewList", UserModel_badReviewList);
        docData.put("UserModel_WritenReviewList", UserModel_WritenReviewList);
        docData.put("UserModel_Market_reservationList", UserModel_Market_reservationList);
        docData.put("UserModel_Market_dealList", UserModel_Market_dealList);
        docData.put("UserModel_Token", UserModel_Token);
        docData.put("UserModel_Unreview", UserModel_Unreview);
        return  docData;
    }


    public String getUserModel_ID() {
        return UserModel_ID;
    }

    public void setUserModel_ID(String userModel_ID) {
        this.UserModel_ID = userModel_ID;
    }

    public String getUserModel_Uid() {
        return UserModel_Uid;
    }

    public void setUserModel_Uid(String userModel_Uid) {
        this.UserModel_Uid = userModel_Uid;
    }

    public String getUserModel_NickName() {
        return UserModel_NickName;
    }

    public void setUserModel_NickName(String userModel_NickName) {
        this.UserModel_NickName = userModel_NickName;
    }

    public String getUserModel_BirthDay(){
        return this.UserModel_BirthDay;
    }

    public void setUserModel_BirthDay(String userModel_BirthDay){
        this.UserModel_BirthDay = userModel_BirthDay;
    }

    public int getUserModel_University(){
        return this.UserModel_University;
    }

    public void setUserModel_University(int UserModel_University){
        this.UserModel_University = UserModel_University;
    }

    public String getUserModel_ProfileImage(){
        return this.UserModel_ProfileImage;
    }

    public void setUserModel_ProfileImage(String photoUrl){
        this.UserModel_ProfileImage = photoUrl;
    }

    public Date getUserModel_DateOfManufacture() { return UserModel_DateOfManufacture; }

    public void setUserModel_DateOfManufacture(Date userModel_DateOfManufacture) { this.UserModel_DateOfManufacture = userModel_DateOfManufacture; }

    public ArrayList<String> getUserModel_kindReviewList() {
        return UserModel_kindReviewList;
    }

    public void setUserModel_kindReviewList(ArrayList<String> UserModel_kindReviewList) {
        UserModel_kindReviewList = UserModel_kindReviewList;
    }

    public ArrayList<String> getUserModel_correctReviewList() {
        return UserModel_correctReviewList;
    }

    public void setUserModel_correctReviewList(ArrayList<String> UserModel_correctReviewList) {
        UserModel_correctReviewList = UserModel_correctReviewList;
    }

    public ArrayList<String> getUserModel_completeReviewList() {
        return UserModel_completeReviewList;
    }

    public void setUserModel_completeReviewList(ArrayList<String> UserModel_completeReviewList) {
        UserModel_completeReviewList = UserModel_completeReviewList;
    }

    public ArrayList<String> getUserModel_badReviewList() {
        return UserModel_badReviewList;
    }
    public void setUserModel_badReviewList(ArrayList<String> UserModel_badReviewList) {
        UserModel_badReviewList = UserModel_badReviewList;
    }
    public ArrayList<String> getUserModel_WritenReviewList() {
        return UserModel_WritenReviewList;
    }
    public void setUserModel_WritenReviewList(ArrayList<String> UserModel_WritenReviewList) {
        UserModel_WritenReviewList = UserModel_WritenReviewList;
    }




    public ArrayList<String> getUserModel_Market_reservationList() {
        return UserModel_Market_reservationList;
    }
    public void setUserModel_Market_reservationList(ArrayList<String> UserModel_Market_reservationList) {
        UserModel_Market_reservationList = UserModel_Market_reservationList;
    }
    public ArrayList<String> getUserModel_Market_dealList() {
        return UserModel_Market_dealList;
    }
    public void setUserModel_Market_dealList(ArrayList<String> UserModel_Market_dealList) {
        UserModel_Market_dealList = UserModel_Market_dealList;
    }

    public String getUserModel_Token() {
        return UserModel_Token;
    }
    public void setUserModel_Token(String UserModel_Token) {
        UserModel_Token = UserModel_Token;
    }

    public ArrayList<HashMap<String, String>> getUserModel_Unreview() {
        return UserModel_Unreview;
    }

    public void setUserModel_Unreview(ArrayList<HashMap<String, String>> userModel_Unreview) {
        UserModel_Unreview = userModel_Unreview;
    }
}