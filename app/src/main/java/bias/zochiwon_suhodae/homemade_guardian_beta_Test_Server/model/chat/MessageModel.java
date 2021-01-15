package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModel implements Serializable {
    private String MessageModel_UserUid; //메세지 보낸 유저의 uid
    private String MessageModel_Message; //메세지 내용
    private String MessageModel_MessageType;          // 0: msg, 1: image, 2: file
    private Date MessageModel_DateOfManufacture; //메세지 보냔 낳짜
    private List<String> MessageModel_ReadUser = new ArrayList<>(); //메세지 읽음 체크 <- 근데 보낸유저의 uid는 무조건 0, 반댄 1인듯 ??
    private String MessageModel_FileName; //메세지 파일 이름 <- 뺴야할듯
    private String MessageModel_FileSize; //메세지 파일 사이즈 <- 빼야할듯

    public MessageModel(){}

    public MessageModel(String MessageModel_UserUid, String MessageModel_Message, String MessageModel_MessageType, Date MessageModel_DateOfManufacture){
        this.MessageModel_UserUid = MessageModel_UserUid;
        this.MessageModel_Message = MessageModel_Message;
        this.MessageModel_MessageType = MessageModel_MessageType;
        this.MessageModel_DateOfManufacture = MessageModel_DateOfManufacture;
    }

    public MessageModel(String MessageModel_UserUid, String MessageModel_Message, String MessageModel_MessageType, Date MessageModel_DateOfManufacture, String MessageModel_FileName, String MessageModel_FileSize){
        this.MessageModel_UserUid = MessageModel_UserUid;
        this.MessageModel_Message = MessageModel_Message;
        this.MessageModel_MessageType = MessageModel_MessageType;
        this.MessageModel_DateOfManufacture = MessageModel_DateOfManufacture;
        this.MessageModel_FileName = MessageModel_FileName;
        this.MessageModel_FileSize = MessageModel_FileSize;
    }

    public Map<String, Object> getMeesageInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("MessageModel_UserUid", MessageModel_UserUid);
        docData.put("MessageModel_Message", MessageModel_Message);
        docData.put("MessageModel_MessageType", MessageModel_MessageType);
        docData.put("MessageModel_DateOfManufacture", MessageModel_DateOfManufacture);
        docData.put("MessageModel_ReadUser", MessageModel_ReadUser);
        docData.put("MessageModel_FileName", MessageModel_FileName);
        docData.put("MessageModel_FileSize", MessageModel_FileSize);
        return  docData;
    }




    public String getMessageModel_UserUid() {
        return MessageModel_UserUid;
    }

    public void setMessageModel_UserUid(String messageModel_UserUid) {
        this.MessageModel_UserUid = messageModel_UserUid;
    }

    public String getMessageModel_Message() {
        return MessageModel_Message;
    }

    public void setMessageModel_Message(String messageModel_Message) {
        this.MessageModel_Message = messageModel_Message;
    }

    public String getMessageModel_MessageType() {
        return MessageModel_MessageType;
    }

    public void setMessageModel_MessageType(String messageModel_MessageType) {
        this.MessageModel_MessageType = messageModel_MessageType;
    }

    public Date getMessageModel_DateOfManufacture() {
        return MessageModel_DateOfManufacture;
    }

    public void setMessageModel_DateOfManufacture(Date messageModel_DateOfManufacture) {
        this.MessageModel_DateOfManufacture = messageModel_DateOfManufacture;
    }

    public List<String> getMessageModel_ReadUser() {
        return MessageModel_ReadUser;
    }

    public void setMessageModel_ReadUser(List<String> messageModel_ReadUser) {
        this.MessageModel_ReadUser = messageModel_ReadUser;
    }

    public String getMessageModel_FileName() {
        return MessageModel_FileName;
    }

    public void setMessageModel_FileName(String messageModel_FileName) {
        this.MessageModel_FileName = messageModel_FileName;
    }

    public String getMessageModel_FileSize() {
        return MessageModel_FileSize;
    }

    public void setMessageModel_FileSize(String messageModel_FileSize) {
        this.MessageModel_FileSize = messageModel_FileSize;
    }

}
