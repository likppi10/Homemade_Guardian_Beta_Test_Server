package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.common.ChatUtil;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.SendNotification;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.RoomModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.ChatimageModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.MessageModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;


import static android.app.Activity.RESULT_OK;

//채팅룸 안의 프레그먼트
//채팅에서 어떻게 배열되는지 설정되는 기능(Chat_RecyclerView_Adapter 함수)
public class ChatFragment extends Fragment {
    private ListenerRegistration listenerRegistration;                                            //생성일자인터페이스(찾고 싶을때 사용하는듯)
    private LinearLayoutManager linearLayoutManager;                                              //리니얼레이아웃 매니져
    private RecyclerViewAdapter Chat_RecyclerView_Adapter;                                        //채팅 리사이클러뷰 어텝터 함수

    private Button Chat_Send_Button;                                                              //채팅 보내기 버튼
    private EditText Chat_Message_Input_EditText;                                                 //채팅 edittext
    private RecyclerView Chat_RecyclerView;                                                       //채팅 리사이클러뷰

    private String MarketModel_Market_Uid;
    private String ChatRoomListModel_RoomUid;                                                     //룸의 uid (확실 x)
    private String currentUser_Uid;                                                                         //나의 uid
    private String To_User_Uid;                                                                         //상대방의 uid
    private Map<String, UserModel> UserModel_UserList = new HashMap<>();                                    //유저 리스트
    private ProgressDialog progressDialog = null;                                                 //진행 표시(Loding창)
    private Integer NumberOfUser = 0;                                                            //현 채팅방의 들어와 있는 유저


    private FirebaseFirestore Firestore =null;
    private StorageReference StorageReference;

    private SimpleDateFormat DateFormatDay = new SimpleDateFormat("yyyy-MM-dd");           //년/월일/ 변수
    private SimpleDateFormat DateFormatHour = new SimpleDateFormat("aa hh:mm");            //시/분/초 변수
    private static final int PICK_FROM_ALBUM = 1;                                                  //앨범 선택
    private static final int PICK_FROM_FILE = 2;                                                   //이미지 선택
    private static String rootPath = ChatUtil.getRootPath()+"/homemade_guardian_beta/";            //경로 설정

    int Int_RoomModel_ImageCount;                                                               //string형을 int로 형변환
    String String_RoomModel_ImageCount;                                                         //int를 string으로 변환

    private RelativeLayout loaderLayout;

    private RoomUidSetListener roomUidSetListener;

    //public Loding_Dialog dialog =null;                 // 로딩 액티비티
    Dialog dialog = null;
    ////Loding_Dialog dialog;                 // 로딩 액티비티

    public ChatFragment() {
    }

    public static final ChatFragment getInstance(String To_User_Uid, String RoomUiD,String MarketModel_Market_Uid,String currentUser_Uid) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("To_User_Uid", To_User_Uid);
        bundle.putString("RoomUid", RoomUiD);
        bundle.putString("MarketModel_Market_Uid", MarketModel_Market_Uid);
        bundle.putString("currentUser_Uid", currentUser_Uid);
        chatFragment.setArguments(bundle);
        return chatFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Chat_RecyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        Chat_RecyclerView.setLayoutManager(linearLayoutManager);
        Chat_Send_Button = view.findViewById(R.id.Chat_Send_Button);
        Chat_Message_Input_EditText = view.findViewById(R.id.Chat_Message_Input_EditText);
        loaderLayout = (RelativeLayout)view.findViewById(R.id.Loader_Lyaout);

        view.findViewById(R.id.Chat_Send_Button).setOnClickListener(Chat_Send_Button_ClickListener);
        view.findViewById(R.id.Chat_Image_Send_Button).setOnClickListener(Chat_Image_Send_Button_ClickListener);
        view.findViewById(R.id.Chat_Message_Input_EditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    ChatUtil.hideKeyboard(getActivity());
                }
            }
        });

        //dialog = new Dialog(getContext());
        ////dialog = new Loding_Dialog((ChatFragment.this).getContext());

        Chat_Message_Input_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().getBytes().length >= 150) {
                    Log.d("민규","150자 이상입니다.");
                }
            }
        });

        Firestore = FirebaseFirestore.getInstance();
        StorageReference = FirebaseStorage.getInstance().getReference();
        currentUser_Uid = getArguments().getString("currentUser_Uid");

        Chat_User_Check();                    //유저의 uid와 room의 uid 체크 함수
        KroreaTime();                         //한국 시간 가져오기 함수
        Chat_RecyclerView_Arrangement();      // Chat_RecyclerView의 배열 정리에 관한 함수

        return view;
    }


    // Chat_RecyclerView의 배열 정리에 관한 함수
    public void Chat_RecyclerView_Arrangement(){
        Chat_RecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (Chat_RecyclerView_Adapter !=null & bottom < oldBottom) {
                    final int lastAdapterItem = Chat_RecyclerView_Adapter.getItemCount() - 1;
                    Chat_RecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int recyclerViewPositionOffset = -1000000;
                            View bottomView = linearLayoutManager.findViewByPosition(lastAdapterItem);
                            if (bottomView != null) {
                                recyclerViewPositionOffset = 0 - bottomView.getHeight();
                            }
                            linearLayoutManager.scrollToPositionWithOffset(lastAdapterItem, recyclerViewPositionOffset);
                        }
                    });
                }
            }
        });
    }

    //한국 시간 가져오기 함수
    public void KroreaTime(){
        DateFormatDay.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        DateFormatHour.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

    //유저의 uid와 room를 해당 변수에 넣는 함수,또한 현재 roomuid와 touid가 맞는지 확인 하는 함수
    public void Chat_User_Check(){
        if (getArguments() != null) {
            ChatRoomListModel_RoomUid = getArguments().getString("RoomUid");
            To_User_Uid = getArguments().getString("To_User_Uid");
            MarketModel_Market_Uid = getArguments().getString("MarketModel_Market_Uid");
            currentUser_Uid = getArguments().getString("currentUser_Uid");
        }

        //마켓에서 넘어왔다면 여기로 들어옴
        if (ChatRoomListModel_RoomUid ==null) {                                         // new room for two user
            currentUser_Uid = getArguments().getString("currentUser_Uid");
            To_User_Uid = getArguments().getString("To_User_Uid");
            MarketModel_Market_Uid = getArguments().getString("MarketModel_Market_Uid");
            getUserInfoFromServer(currentUser_Uid);
            getUserInfoFromServer(To_User_Uid);
            NumberOfUser = 2;
            findChatRoom2(currentUser_Uid,To_User_Uid,MarketModel_Market_Uid);
        }

        if (!"".equals(To_User_Uid) && To_User_Uid !=null && !"".equals(MarketModel_Market_Uid) && MarketModel_Market_Uid !=null) {
            findChatRoom(currentUser_Uid,To_User_Uid,MarketModel_Market_Uid);
        }
        if (!"".equals(ChatRoomListModel_RoomUid) && ChatRoomListModel_RoomUid !=null) {                   // existing room (multi user)
            setChatRoom(ChatRoomListModel_RoomUid);
        }
    }


    // 프래그먼트 객체 자체는 사라지지 않고 메모리에 남아있는 함수
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Chat_RecyclerView_Adapter != null) {
            Chat_RecyclerView_Adapter.stopListening();
        }
    }

    // chat에 들어와있는 user의 uid로 정보를 가져와 userlist에 담는 함수
    void getUserInfoFromServer(String UserModel_Uid){
        Firestore = FirebaseFirestore.getInstance();
        Firestore.collection("USERS").document(UserModel_Uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                UserModel_UserList.put(userModel.getUserModel_Uid(), userModel);
                if (ChatRoomListModel_RoomUid != null & NumberOfUser == UserModel_UserList.size()) {
                    Chat_RecyclerView_Adapter = new RecyclerViewAdapter();
                    Chat_RecyclerView.setAdapter(Chat_RecyclerView_Adapter);
                }
            }
        });
    }

    // Room에서 MessageModel_PostUid로 찾는다.
    void findChatRoom2(final String currentUser_Uid,final String toUid,final String MarketModel_Market_Uid){
        Firestore = FirebaseFirestore.getInstance();
        Firestore.collection("ROOMS").whereEqualTo("RoomModel_PostUid",MarketModel_Market_Uid).whereEqualTo("RoomModel_GuestUser", currentUser_Uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {return;}
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Long> users = (Map<String, Long>) document.get("RoomModel_USERS");
                            if (users.size()==2 & users.get(toUid)!=null){
                                setChatRoom(document.getId());
                                break;
                            }
                        }
                    }
                });
    }


    // Room에서 MessageModel_PostUid로 찾는다.
    void findChatRoom(final String currentUser_Uid,final String toUid,final String MarketModel_Market_Uid){
        Firestore = FirebaseFirestore.getInstance();
        Firestore.collection("ROOMS").whereEqualTo("RoomModel_PostUid",MarketModel_Market_Uid).whereGreaterThanOrEqualTo("RoomModel_USERS."+currentUser_Uid, 0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {return;}
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Long> users = (Map<String, Long>) document.get("RoomModel_USERS");
                            if (users.size()==2 & users.get(toUid)!=null){
                                setChatRoom(document.getId());
                                break;
                            }
                        }
                    }
                });
    }

    // 채팅방에서 사용자 목록을 가져오는 함수
    void setChatRoom(String RoomUid) {
        ChatRoomListModel_RoomUid = RoomUid;
        Firestore = FirebaseFirestore.getInstance();
        Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {return;}
                DocumentSnapshot document = task.getResult();
                Map<String, Long> users = (Map<String, Long>) document.get("RoomModel_USERS");

                for( String key : users.keySet() ){
                    getUserInfoFromServer(key);
                }
                NumberOfUser = users.size();
            }
        });
    }

    //유저가 읽었다면
    void setUnreadRead() {
        if (ChatRoomListModel_RoomUid ==null) return;
        Firestore = FirebaseFirestore.getInstance();
        Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {return;}
                DocumentSnapshot document = task.getResult();
                Map<String, Integer> users = (Map<String, Integer>) document.get("RoomModel_USERS");

                users.put(currentUser_Uid, 0);
                document.getReference().update("RoomModel_USERS", users);
            }
        });
    }

    //채팅(글) 보내기 버튼 함수
    Button.OnClickListener Chat_Send_Button_ClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            String msg = Chat_Message_Input_EditText.getText().toString();
            sendMessage(msg, "0", null, MarketModel_Market_Uid,ChatRoomListModel_RoomUid);
            Chat_Message_Input_EditText.setText("");
        }
    };

    // 이미지 보내기 버튼 함수
    Button.OnClickListener Chat_Image_Send_Button_ClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            if(ChatRoomListModel_RoomUid!=null){
                //여기서 처음이면 0 다음부터는 +1씩 증가시킨다. 그후 이름을 해당 에 맞추어 이름을 지어 배열로 가진다.
                DocumentReference docRefe_ROOMS_CurrentUid = FirebaseFirestore.getInstance().collection("ROOMS").document(ChatRoomListModel_RoomUid);
                docRefe_ROOMS_CurrentUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        RoomModel roomModel = new RoomModel();
                        roomModel = documentSnapshot.toObject(RoomModel.class);
                        Int_RoomModel_ImageCount = Integer.parseInt(roomModel.getRoomModel_ImageCount());
                    }
                });
            }
            else{
                //이렇게 되면 첫채팅에서 사진 누르고 다시 취소하면 아무 채팅없이 방이 하나 파질듯
                ChatRoomListModel_RoomUid = Firestore.collection("ROOMS").document().getId();
                CreateChattingRoom( Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid) );

                //여기서 처음이면 0 다음부터는 +1씩 증가시킨다. 그후 이름을 해당 에 맞추어 이름을 지어 배열로 가진다.
                DocumentReference docRefe_ROOMS_CurrentUid = FirebaseFirestore.getInstance().collection("ROOMS").document(ChatRoomListModel_RoomUid);
                docRefe_ROOMS_CurrentUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        RoomModel roomModel = new RoomModel();
                        roomModel = documentSnapshot.toObject(RoomModel.class);
                        Int_RoomModel_ImageCount = Integer.parseInt(roomModel.getRoomModel_ImageCount());
                    }
                });
            }
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };

    //최초 채팅룸 만들기,1을 0으로 변경하는 함수
    public void CreateChattingRoom(final DocumentReference room) {
        //유저의 uid와 읽었는지 않읽었는지 확인 하는 정보
        Map<String, Integer> USERS = new HashMap<>();
        for( String key : UserModel_UserList.keySet() ){
            USERS.put(key, 1);
        }
        //유저의 uid와 유저가 나갔는지 안나갓는지 확인하는 정보 <- 1이면 안나간거 0이면 나간거
        Map<String, Integer> USERS_OUT = new HashMap<>();
        for( String key : UserModel_UserList.keySet() ){
            USERS_OUT.put(key, 1);
        }

        RoomModel roomModel = new RoomModel();
        roomModel.setRoomModel_Title(null);
        roomModel.setRoomModel_USERS(USERS);

        roomModel.setRoomModel_USER_OUT(USERS_OUT);
        roomModel.setRoomModel_ImageCount("0");
        roomModel.setRoomModel_PostUid(MarketModel_Market_Uid);
        roomModel.setRoomModel_GuestUser(currentUser_Uid);

        room.set(roomModel.getRoomInfo()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Chat_RecyclerView_Adapter = new RecyclerViewAdapter();
                    Chat_RecyclerView.setAdapter(Chat_RecyclerView_Adapter);
                }
            }
        });
    }





    //메세지 보내기 함수
    private void sendMessage(final String MessageModel_Message, final String Message_MessageType, final ChatimageModel.FileInfo fileinfo, final String MarketModel_Market_Uid, String RoomUid) {
        Chat_Send_Button.setEnabled(false);

        //최초 룸 만들기
        if(RoomUid ==null) {             // create chatting room for two user
            ChatRoomListModel_RoomUid = Firestore.collection("ROOMS").document().getId();
            CreateChattingRoom( Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid) );
            roomUidSetListener.RoomUidSet(ChatRoomListModel_RoomUid, To_User_Uid);

        }

        final MessageModel messageModel = new MessageModel();
        if (fileinfo==null){
            messageModel.setMessageModel_UserUid(currentUser_Uid);
            messageModel.setMessageModel_Message(MessageModel_Message);
            messageModel.setMessageModel_MessageType(Message_MessageType);
            messageModel.setMessageModel_DateOfManufacture(new Date());
        }
        else{
            messageModel.setMessageModel_UserUid(currentUser_Uid);
            messageModel.setMessageModel_Message(MessageModel_Message);
            messageModel.setMessageModel_MessageType(Message_MessageType);
            messageModel.setMessageModel_DateOfManufacture(new Date());
            messageModel.setMessageModel_FileName(fileinfo.FileName);
            messageModel.setMessageModel_FileSize(fileinfo.FileSize);
        }

        //처음 채팅을 시작할때 -> 텍스트만 들어옴
        if(RoomUid ==null){
            final DocumentReference docRef = Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //MESSAGE의 MessageModel_ReadUser에 uid를 넣는다.
                    //ROOMS의 USERS의 해당 uid의 1->0으로 바꾼다.
                    if (!task.isSuccessful()) {return;}
                    WriteBatch batch = Firestore.batch();
                    //마지막 메세지 저장
                    DocumentSnapshot document2 = task.getResult();
                    RoomModel roomModel = document2.toObject(RoomModel.class);
                    roomModel.setRoomModel_DateOfManufacture(new Date());
                    roomModel.setRoomModel_MessageType(Message_MessageType);
                    roomModel.setRoomModel_Message(MessageModel_Message);
                    roomModel.setRoomModel_UserUid(currentUser_Uid);
                    if (fileinfo!=null){
                        roomModel.setRoomModel_FileName(fileinfo.FileName);
                        roomModel.setRoomModel_FileSize(fileinfo.FileSize);
                    }
                    roomModel.setRoomModel_PostUid(MarketModel_Market_Uid);
                    batch.set(docRef, roomModel.getRoomInfo(), SetOptions.merge());

                    List<String> ReadUsers = new ArrayList();
                    ReadUsers.add(currentUser_Uid);
                    messageModel.setMessageModel_ReadUser(ReadUsers);
                    batch.set(docRef.collection("MESSAGE").document(), messageModel.getMeesageInfo());

                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendGson(MessageModel_Message);
                                Chat_Send_Button.setEnabled(true);
                                //dialog.dismiss();
                            }
                        }
                    });
                }

            });
        }
        else{
            //룸이 있다면`
            final DocumentReference docRef = Firestore.collection("ROOMS").document(RoomUid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful()) {return;}
                    WriteBatch batch = Firestore.batch();
                    // save last message
                    DocumentSnapshot document2 = task.getResult();
                    RoomModel roomModel = document2.toObject(RoomModel.class);
                    roomModel.setRoomModel_DateOfManufacture(new Date());
                    roomModel.setRoomModel_MessageType(Message_MessageType);
                    roomModel.setRoomModel_Message(MessageModel_Message);
                    roomModel.setRoomModel_UserUid(currentUser_Uid);
                    if (fileinfo!=null){
                        roomModel.setRoomModel_FileName(fileinfo.FileName);
                        roomModel.setRoomModel_FileSize(fileinfo.FileSize);
                    }
                    Map<String, Integer> users = roomModel.getRoomModel_USERS();
                    for( String key : users.keySet() ){
                        if (!currentUser_Uid.equals(key)) users.put(key, users.get(key)+1);
                    }
                    roomModel.setRoomModel_USERS(users);
                    batch.set(docRef, roomModel.getRoomInfo(), SetOptions.merge());

                    List<String> ReadUsers = new ArrayList();
                    ReadUsers.add(currentUser_Uid);
                    //MessageModel.put("MessageModel_ReadUser", ReadUsers);
                    messageModel.setMessageModel_ReadUser(ReadUsers);
                    batch.set(docRef.collection("MESSAGE").document(), messageModel.getMeesageInfo());
                    // inc unread message count

                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //sendGCM();
                                sendGson(MessageModel_Message);
                                Chat_Send_Button.setEnabled(true);
                                //dialog.dismiss();
                            }
                        }
                    });
                }

            });
        }
    }


    // 이미지나 파일을 로딩하는 결과 함수 (대략 10초가 넘게 걸림 , 스토리지에 넣는것 9초 사진 띄우기 1초)
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode!= RESULT_OK) { return;}
        Uri fileUri = data.getData(); //해당 사진
        final ChatimageModel.FileInfo fileinfo  = getFileDetailFromUri(getContext(), fileUri); //chatmodel.fileinfo에 넣기
        ////dialog.callDialog();
        callDialog();
        //showProgressDialog("잠시만 기다려 주세요^^");
        Int_RoomModel_ImageCount = Int_RoomModel_ImageCount +1;
        String_RoomModel_ImageCount =String.valueOf(Int_RoomModel_ImageCount);
        StorageReference.child("ROOMS/"+ChatRoomListModel_RoomUid + "/" + String_RoomModel_ImageCount).putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //추가 ()
                DocumentReference docRefe_ROOMS_CurrentUid = FirebaseFirestore.getInstance().collection("ROOMS").document(ChatRoomListModel_RoomUid);
                docRefe_ROOMS_CurrentUid.update("RoomModel_ImageCount", String_RoomModel_ImageCount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendMessage(String_RoomModel_ImageCount, Integer.toString(requestCode), fileinfo, MarketModel_Market_Uid,ChatRoomListModel_RoomUid);
                                ////dialog.calldismiss();
                                //hideProgressDialog();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
        if (requestCode != PICK_FROM_ALBUM) { return;}
        // small image
        Glide.with(getContext())
                .asBitmap()
                .load(fileUri)
                .apply(new RequestOptions().override(150, 150))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        StorageReference.child("ROOMS/"+ChatRoomListModel_RoomUid + "/" + String_RoomModel_ImageCount).putBytes(data);
                    }
                });
        //dialog.dismiss();
    }

    // Uri에서 파일 이름 및 크기 가져오기 함수
    public static ChatimageModel.FileInfo getFileDetailFromUri(final Context context, final Uri uri) {
        if (uri == null) { return null; }

        ChatimageModel.FileInfo fileDetail = new ChatimageModel.FileInfo();
        // File Scheme.
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            fileDetail.FileName = file.getName();
            fileDetail.FileSize = ChatUtil.size2String(file.length());
        }
        // Content Scheme.
        else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor returnCursor =
                    context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null && returnCursor.moveToFirst()) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                fileDetail.FileName = returnCursor.getString(nameIndex);
                fileDetail.FileSize = ChatUtil.size2String(returnCursor.getLong(sizeIndex));
                returnCursor.close();
            }
        }

        return fileDetail;
    }

    public void showProgressDialog(String title ) {
        if (progressDialog==null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(title);
        progressDialog.setMessage("사진을 보내는 중입니다..!!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void setProgressDialog(int value) {
        progressDialog.setProgress(value);
    }
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }


    private void sendGson(final String MessageModel_Message) {
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(currentUser_Uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //데이터의 존재여부
                            final UserModel userModel = document.toObject(UserModel.class);
                            final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(To_User_Uid);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {  //데이터의 존재여부
                                                UserModel TouserModel = document.toObject(UserModel.class);
                                                //SendNotification.sendNotification(TouserModel.getUserModel_Token(), userModel.getUserModel_NickName(), MessageModel_Message);
                                                SendNotification.sendCommentNotification(TouserModel.getUserModel_Token(), userModel.getUserModel_NickName(), "메세지가 도착했습니다!",ChatRoomListModel_RoomUid);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }



    //채팅 RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        List<MessageModel> MessageModel_List= new ArrayList<MessageModel>(); //메세지 리스트


        //파일 경로 설정
        RecyclerViewAdapter() {
            File dir = new File(rootPath);
            if (!dir.exists()) {
                if (!ChatUtil.isPermissionGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    return;
                }
                dir.mkdirs();
            }

            //setUnread2Read();
            startListening();
            setUnreadRead();
        }

        //시작 세팅 함수
        public void startListening() {
            MessageModel_List.clear();
            Firestore = FirebaseFirestore.getInstance();
            CollectionReference roomRef = Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).collection("MESSAGE");
            // my chatting room information
            listenerRegistration = roomRef.orderBy("MessageModel_DateOfManufacture").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {return;}
                    MessageModel messageModel;
                    for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                messageModel = change.getDocument().toObject(MessageModel.class);
                                //if (message.msg !=null & message.timestamp == null) {continue;} // FieldValue.serverTimestamp is so late
                                if (messageModel.getMessageModel_ReadUser().indexOf(currentUser_Uid) == -1) {
                                    messageModel.getMessageModel_ReadUser().add(currentUser_Uid);
                                    change.getDocument().getReference().update("MessageModel_ReadUser", messageModel.getMessageModel_ReadUser());
                                }
                                MessageModel_List.add(messageModel);
                                notifyItemInserted(change.getNewIndex());
                                break;
                            case MODIFIED:
                                messageModel = change.getDocument().toObject(MessageModel.class);
                                MessageModel_List.set(change.getOldIndex(), messageModel);
                                notifyItemChanged(change.getOldIndex());
                                break;
                            case REMOVED:
                                MessageModel_List.remove(change.getOldIndex());
                                notifyItemRemoved(change.getOldIndex());
                                break;
                        }
                    }
                    Chat_RecyclerView.scrollToPosition(MessageModel_List.size() - 1);
                }
            });
        }

        public void stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
                listenerRegistration = null;
            }

            MessageModel_List.clear();
            notifyDataSetChanged();
        }

        //케이스에 따른 이미지 띄우기 함수
        @Override
        public int getItemViewType(int position) {
            MessageModel messageModel = MessageModel_List.get(position);
            if (currentUser_Uid.equals(messageModel.getMessageModel_UserUid()) ) {
                switch(messageModel.getMessageModel_MessageType()){
                    case "1": return R.layout.item_chatimage_right;
                    case "2": return R.layout.item_chatfile_right;
                    default:  return R.layout.item_chatmsg_right;
                }
            } else {
                switch(messageModel.getMessageModel_MessageType()){
                    case "1": return R.layout.item_chatimage_left;
                    case "2": return R.layout.item_chatfile_left;
                    default:  return R.layout.item_chatmsg_left;
                }
            }
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            final MessageModel messageModel = MessageModel_List.get(position);
            setReadCounter(messageModel, messageViewHolder.Read_Check);

            //변경
            if ("0".equals(messageModel.getMessageModel_MessageType())) {                                      // text message
                messageViewHolder.Message_TextView.setText(messageModel.getMessageModel_Message());
            }
            //사진 보이기
            else if ("1".equals(messageModel.getMessageModel_MessageType())) {                                      // file transfer
                messageViewHolder.realname = messageModel.getMessageModel_Message();
                Glide.with(getContext())
                        .load(StorageReference.child("ROOMS/"+ChatRoomListModel_RoomUid + "/" + messageModel.getMessageModel_Message()))
                        .apply(new RequestOptions().override(1000, 1000))
                        .into(messageViewHolder.Message_Image_ImageView);
            }
            ////

            //상대방 이름
            if (! currentUser_Uid.equals(messageModel.getMessageModel_UserUid())) {
                UserModel userModel = UserModel_UserList.get(messageModel.getMessageModel_UserUid());
                messageViewHolder.Message_NickName.setText(userModel.getUserModel_NickName());
                //상대방 프로필사진으로 바꾸기
                if (userModel.getUserModel_ProfileImage() != null) {
                    Glide.with(getContext()).load(userModel.getUserModel_ProfileImage()).centerCrop().override(500).into(messageViewHolder.User_Profile_Imalge);
                } else{
                    Glide.with(getContext()).load(R.drawable.none_profile_user).into(messageViewHolder.User_Profile_Imalge);
                }
            }

            messageViewHolder.Message_Divider.setVisibility(View.INVISIBLE);
            messageViewHolder.Message_Divider.getLayoutParams().height = 0;
            messageViewHolder.Message_DateOfManufacture.setText("");
            if (messageModel.getMessageModel_DateOfManufacture()==null) {return;}

            String day = DateFormatDay.format( messageModel.getMessageModel_DateOfManufacture());
            String timestamp = DateFormatHour.format( messageModel.getMessageModel_DateOfManufacture());
            messageViewHolder.Message_DateOfManufacture.setText(timestamp);

            if (position==0) {
                messageViewHolder.Message_Divider_Date.setText(day);
                messageViewHolder.Message_Divider.setVisibility(View.VISIBLE);
                messageViewHolder.Message_Divider.getLayoutParams().height = 60;
            } else {
                MessageModel beforeMsg = MessageModel_List.get(position - 1);
                String beforeDay = DateFormatDay.format( beforeMsg.getMessageModel_DateOfManufacture() );

                if (!day.equals(beforeDay) && beforeDay != null) {
                    messageViewHolder.Message_Divider_Date.setText(day);
                    messageViewHolder.Message_Divider.setVisibility(View.VISIBLE);
                    messageViewHolder.Message_Divider.getLayoutParams().height = 60;
                }
            }
        }

        //상대방 유저가 읽었는지 확인한다. -> 채팅방 안의 1이랑 사라지는거는 MESSAGE의 MessageModel_ReadUser의 수로 파악
        void setReadCounter (MessageModel messageModel, final TextView textView) {
            int cnt = NumberOfUser - messageModel.getMessageModel_ReadUser().size();
            if (cnt > 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(cnt));
            } else {
                textView.setVisibility(View.INVISIBLE);

                //현재 상대방 유저가 들어와있다면 상대방 룸 리스트의 현재 채팅방을 읽음 표시로 바꾼다. - 추가
                Firestore = FirebaseFirestore.getInstance();
                Firestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {return;}
                        DocumentSnapshot document = task.getResult();
                        Map<String, Long> users = (Map<String, Long>) document.get("RoomModel_USERS");

                        users.put(currentUser_Uid, (long) 0);
                        document.getReference().update("RoomModel_USERS", users);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return MessageModel_List.size();
        }


    }

    //메세지보낼때 프로필 사진, 뭐라고 보냈는지, 언제 보냈는지 등을 묶어주는 함수
    //파일도 없앨거임
    private class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView User_Profile_Imalge;  //프로필 사진
        public TextView Message_TextView;//채팅 메세지 텍스트
        public ImageView Message_Image_ImageView;          //채팅 메세지 이미지
        public TextView Message_NickName; //채팅 메세지 이름
        public TextView Message_DateOfManufacture; //메세지 보낸 시간
        public TextView Read_Check; //읽었는지 확인
        public LinearLayout Message_Divider; //메세지 전체 틀
        public TextView Message_Divider_Date; //메세지 보낸 날짜
        public TextView button_item;            // only item_chatfile_<-이거 뭔지 정확히 체크
        public LinearLayout msgLine_item;       // only item_chatfile_ <-이거 뭔지 정확히 체크
        public String filename;
        public String realname;                 //이거 뭔지 모르겠음

        public MessageViewHolder(View view) {
            super(view);
            User_Profile_Imalge = view.findViewById(R.id.User_Profile_Imalge);
            Message_TextView = view.findViewById(R.id.Message_TextView);
            Message_Image_ImageView = view.findViewById(R.id.Message_Image_ImageView);
            Message_DateOfManufacture = view.findViewById(R.id.Message_DateOfManufacture);
            Message_NickName = (TextView) view.findViewById(R.id.Message_NickName);
            Read_Check = view.findViewById(R.id.Read_Check);
            Message_Divider = view.findViewById(R.id.Message_Divider);
            Message_Divider_Date = view.findViewById(R.id.Message_Divider_Date);
            button_item = view.findViewById(R.id.button_item);
            msgLine_item = view.findViewById(R.id.msgLine_item);        // for file
            if (msgLine_item!=null) {
                msgLine_item.setOnClickListener(downloadClickListener);
            }
        }
        // 파일 다운로드및 열기 버튼 함수
        Button.OnClickListener downloadClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                if ("Download".equals(button_item.getText())) {
                    download();
                } else {
                    openWith();
                }
            }
            public void download() {
                if (!ChatUtil.isPermissionGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    return ;
                }
                //showProgressDialog("Downloading File.");

                final File localFile = new File(rootPath, filename);

                StorageReference.child("files/"+realname).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        button_item.setText("Open File");
                        //hideProgressDialog();
                        Log.e("DirectTalk9 ","local file created " +localFile.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("DirectTalk9 ","local file not created  " +exception.toString());
                    }
                });
            }

            public void openWith() {
                File newFile = new File(rootPath + filename);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext = newFile.getName().substring(newFile.getName().lastIndexOf(".") + 1);
                String type = mime.getMimeTypeFromExtension(ext);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", newFile);

                    List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }else {
                    uri = Uri.fromFile(newFile);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, type);//"application/vnd.android.package-archive");
                startActivity(Intent.createChooser(intent, "Your title"));
            }
        };

    }

    public void backPressed() {
    }


    //Room_Uid를 넘겨주는 interface
    public interface RoomUidSetListener{
        void RoomUidSet(String RoomUid,String ToUid);
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof RoomUidSetListener){
            roomUidSetListener = (RoomUidSetListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement RoomUidSetListener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        roomUidSetListener = null;
    }

    //유저 나갈때 나갔습니다. 텍스트 띄우기 함수
    public void User_GoOut(String current_Uid, final String Market_Uid,final String Room_uid){
        Firestore.collection("USERS").document(current_Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                DocumentSnapshot document = task.getResult();
                String NickName = (String)document.get("UserModel_NickName");
                String msg = "("+ NickName + ")" + "  님이 나갔습니다.";
                sendMessage(msg, "0", null, Market_Uid,Room_uid);
            }
        });
    }

    public void callDialog(){
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_loading);
        ImageView Loding_charactor = (ImageView) dialog.findViewById(R.id.Loding_charactor);
        Glide.with(Loding_charactor.getContext()).load(R.drawable.loading).into(new DrawableImageViewTarget(Loding_charactor));
        dialog.show();
    }
}