package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat;

import java.io.Serializable;

public class ChatRoomListModel implements Serializable {
    private String ChatRoomListModel_RoomUid; //room의 uid
    private String ChatRoomListModel_Title; //room의 제목 <- 이거 근데 상대방 닉네임인데 고민해야 할듯
    private String ChatRoomListModel_ProfileImage; //room의 상대방 이미지 <- 이것도 상대방 프로필 사진
    private String ChatRoomListModel_LastMessage; //마지막 메세지
    private String ChatRoomListModel_MessageLastDateTime; //마지막 메세지 날짜
    private Integer ChatRoomListModel_NumberOfUser; //채팅방에 몇명 들어갔는지
    private Integer ChatRoomListModel_UnreadCheck; //채팅방을 읽었는지 안익었는지
    private String ChatRoomListModel_ToUserUid; // 상대방 user의 uid 가져오기
    //추가
    private String ChatRoomListModel_PostUid;  //현재 어떤 포스트의 uid인지 가져오기


    public String getChatRoomListModel_PostUid() {
        return ChatRoomListModel_PostUid;
    }

    public void setChatRoomListModel_PostUid(String ChatRoomListModel_PostUid) {
        this.ChatRoomListModel_PostUid = ChatRoomListModel_PostUid;
    }

    public String getChatRoomListModel_ToUserUid() {
        return ChatRoomListModel_ToUserUid;
    }

    public void setChatRoomListModel_ToUserUid(String ChatRoomListModel_ToUserUid) {
        this.ChatRoomListModel_ToUserUid = ChatRoomListModel_ToUserUid;
    }

    public String getChatRoomListModel_RoomUid() {
        return ChatRoomListModel_RoomUid;
    }

    public void setChatRoomListModel_RoomUid(String chatRoomListModel_RoomUid) {
        this.ChatRoomListModel_RoomUid = chatRoomListModel_RoomUid;
    }

    public String getChatRoomListModel_Title() {
        return ChatRoomListModel_Title;
    }

    public void setChatRoomListModel_Title(String chatRoomListModel_Title) {
        this.ChatRoomListModel_Title = chatRoomListModel_Title;
    }

    public String getChatRoomListModel_ProfileImage() {
        return ChatRoomListModel_ProfileImage;
    }

    public void setChatRoomListModel_ProfileImage(String chatRoomListModel_ProfileImage) {
        this.ChatRoomListModel_ProfileImage = chatRoomListModel_ProfileImage;
    }

    public String getChatRoomListModel_LastMessage() {
        return ChatRoomListModel_LastMessage;
    }

    public void setChatRoomListModel_LastMessage(String chatRoomListModel_LastMessage) {
        this.ChatRoomListModel_LastMessage = chatRoomListModel_LastMessage;
    }

    public String getChatRoomListModel_MessageLastDateTime() {
        return ChatRoomListModel_MessageLastDateTime;
    }

    public void setChatRoomListModel_MessageLastDateTime(String chatRoomListModel_MessageLastDateTime) {
        this.ChatRoomListModel_MessageLastDateTime = chatRoomListModel_MessageLastDateTime;
    }

    public Integer getChatRoomListModel_NumberOfUser() {
        return ChatRoomListModel_NumberOfUser;
    }

    public void setChatRoomListModel_NumberOfUser(Integer chatRoomListModel_NumberOfUser) {
        this.ChatRoomListModel_NumberOfUser = chatRoomListModel_NumberOfUser;
    }

    public Integer getChatRoomListModel_UnreadCheck() {
        return ChatRoomListModel_UnreadCheck;
    }

    public void setChatRoomListModel_UnreadCheck(Integer chatRoomListModel_UnreadCheck) {
        this.ChatRoomListModel_UnreadCheck = chatRoomListModel_UnreadCheck;
    }
}
