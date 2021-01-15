package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RoomModel {
    private Date RoomModel_DateOfManufacture;           //룸모델 날짜
    private String RoomModel_FileName;                  //룸 파일 이름
    private String RoomModel_FileSize;                  //룸 파일 사이즈
    private String RoomModel_ImageCount;                // 룸 이미지 개수
    private String RoomModel_Message;                   // 룸 메세지 내용
    private String RoomModel_PostUid;                   // 룸 포스트 uid
    private String RoomModel_Title;                     // 룸 제목
    private String RoomModel_UserUid;                   // 룸 마지막에 보낸 유저의 uid
    private String RoomModel_MessageType;               // 룸 메세지 타입(0일경우 글,1일경우 사진)
    private Map<String, Integer> RoomModel_USERS;       // 룸에 들어와 있는 유저(읽었는지 확인)
    private Map<String, Integer> RoomModel_USER_OUT;    // 룸에 들어와 있는 유저가 나갔는지 안나갔는지 확인하는 모델

    //추가
    private String RoomModel_GuestUser;               // 룸의 게스트 uid

    public RoomModel(){}

    public RoomModel(Date RoomModel_DateOfManufacture, String RoomModel_FileName, String RoomModel_FileSize, String RoomModel_ImageCount, String RoomModel_Message, String RoomModel_PostUid, String RoomModel_Title, String RoomModel_UserUid, String RoomModel_MessageType, Map<String, Integer> RoomModel_USERS, Map<String, Integer> RoomModel_USER_OUT,String RoomModel_GuestUser){
        this.RoomModel_DateOfManufacture = RoomModel_DateOfManufacture;
        this.RoomModel_FileName = RoomModel_FileName;
        this.RoomModel_FileSize = RoomModel_FileSize;
        this.RoomModel_ImageCount = RoomModel_ImageCount;
        this.RoomModel_Message = RoomModel_Message;
        this.RoomModel_PostUid = RoomModel_PostUid;
        this.RoomModel_Title = RoomModel_Title;
        this.RoomModel_UserUid = RoomModel_UserUid;
        this.RoomModel_MessageType = RoomModel_MessageType;
        this.RoomModel_USERS = RoomModel_USERS;
        this.RoomModel_USER_OUT = RoomModel_USER_OUT;
        this.RoomModel_GuestUser = RoomModel_GuestUser;
    }

    public Map<String, Object> getRoomInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("RoomModel_DateOfManufacture", RoomModel_DateOfManufacture);
        docData.put("RoomModel_FileName", RoomModel_FileName);
        docData.put("RoomModel_FileSize", RoomModel_FileSize);
        docData.put("RoomModel_ImageCount", RoomModel_ImageCount);
        docData.put("RoomModel_Message", RoomModel_Message);
        docData.put("RoomModel_PostUid", RoomModel_PostUid);
        docData.put("RoomModel_Title", RoomModel_Title);
        docData.put("RoomModel_UserUid", RoomModel_UserUid);
        docData.put("RoomModel_MessageType", RoomModel_MessageType);
        docData.put("RoomModel_USERS", RoomModel_USERS);
        docData.put("RoomModel_USER_OUT", RoomModel_USER_OUT);
        docData.put("RoomModel_GuestUser", RoomModel_GuestUser);
        return  docData;
    }
    public Date getRoomModel_DateOfManufacture() {
        return RoomModel_DateOfManufacture;
    }

    public void setRoomModel_DateOfManufacture(Date roomModel_DateOfManufacture) {
        RoomModel_DateOfManufacture = roomModel_DateOfManufacture;
    }

    public String getRoomModel_FileName() {
        return RoomModel_FileName;
    }

    public void setRoomModel_FileName(String roomModel_FileName) {
        RoomModel_FileName = roomModel_FileName;
    }

    public String getRoomModel_FileSize() {
        return RoomModel_FileSize;
    }

    public void setRoomModel_FileSize(String roomModel_FileSize) {
        RoomModel_FileSize = roomModel_FileSize;
    }

    public String getRoomModel_ImageCount() {
        return RoomModel_ImageCount;
    }

    public void setRoomModel_ImageCount(String roomModel_ImageCount) {
        RoomModel_ImageCount = roomModel_ImageCount;
    }

    public String getRoomModel_Message() {
        return RoomModel_Message;
    }

    public void setRoomModel_Message(String roomModel_Message) {
        RoomModel_Message = roomModel_Message;
    }

    public String getRoomModel_PostUid() {
        return RoomModel_PostUid;
    }

    public void setRoomModel_PostUid(String roomModel_PostUid) {
        RoomModel_PostUid = roomModel_PostUid;
    }

    public String getRoomModel_Title() {
        return RoomModel_Title;
    }

    public void setRoomModel_Title(String roomModel_Title) {
        RoomModel_Title = roomModel_Title;
    }

    public String getRoomModel_UserUid() {
        return RoomModel_UserUid;
    }

    public void setRoomModel_UserUid(String roomModel_UserUid) {
        RoomModel_UserUid = roomModel_UserUid;
    }

    public String getRoomModel_MessageType() {
        return RoomModel_MessageType;
    }

    public void setRoomModel_MessageType(String roomModel_MessageType) {
        RoomModel_MessageType = roomModel_MessageType;
    }

    public Map<String, Integer> getRoomModel_USERS() {
        return RoomModel_USERS;
    }

    public void setRoomModel_USERS(Map<String, Integer> roomModel_USERS) {
        this.RoomModel_USERS = roomModel_USERS;
    }

    public Map<String, Integer> getRoomModel_USER_OUT() {
        return RoomModel_USER_OUT;
    }

    public void setRoomModel_USER_OUT(Map<String, Integer> roomModel_USER_OUT) {
        this.RoomModel_USER_OUT = roomModel_USER_OUT;
    }

    public String getRoomModel_GuestUser() {
        return RoomModel_GuestUser;
    }

    public void setRoomModel_GuestUser(String RoomModel_GuestUser) {
        this.RoomModel_GuestUser = RoomModel_GuestUser;
    }
}