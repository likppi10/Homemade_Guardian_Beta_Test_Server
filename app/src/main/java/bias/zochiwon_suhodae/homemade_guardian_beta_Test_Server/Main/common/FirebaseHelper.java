package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.listener.OnPostListener;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.activity.ChatActivity;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.chat.RoomModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.CommunityModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.community.Community_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.Market_CommentModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.market.MarketModel;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Map;

import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util.isCommunityStorageUrl;
import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util.isMarketStorageUrl;
import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util.showCommunityToast;
import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util.showMarketToast;
import static bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.common.Util.storageUrlToName;

//파이어베이스에서 파이어스토어,파이어스토리지의 삭제에 관여한다.

public class FirebaseHelper {                                                                           // part19 : Firevasehelper로 이동 (61')
    private Activity Activity;
    private OnPostListener Onpostlistener;
    private int SuccessCount;
    int Java_MessageModel_ImageCount;                         //string형을 int로 형변환
    UserModel userModel;
    ArrayList<String> Market_reservationList = new ArrayList<>();
    ArrayList<String> Market_dealList = new ArrayList<>();

    public FirebaseHelper(Activity Activity) {
        this.Activity = Activity;
    }

    public void setOnpostlistener(OnPostListener Onpostlistener){ this.Onpostlistener = Onpostlistener; }

    //게시물의 경우에는 이미지가 파이어스토리지에 있기 때문에 파이어스토리지 또한 삭제해주어야한다.
    public void Market_Storagedelete(final MarketModel marketModel,final String Info){                                                 // part16: 스토리지의 삭제 (13')
        FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();                                        // part17 : 스토리지 삭제 (문서) (19'50")
        StorageReference Storagereference = Firebasestorage.getReference();
        final String Market_Uid = marketModel.getMarketModel_Market_Uid();
        ArrayList<String> Market_ImageList = marketModel.getMarketModel_ImageList();
        if(Market_ImageList != null) {
            for (int i = 0; i < Market_ImageList.size(); i++) {
                String Image = Market_ImageList.get(i);
                if (isMarketStorageUrl(Image)) {
                    SuccessCount++;                                                                             // part17 : 사진의 개수가 여러개인 게시물의 경우 (23'35")
                    StorageReference desertRef_MARKETS_MarketUid = Storagereference.child("MARKETS/" + Market_Uid + "/" + storageUrlToName(Image));    // part17: (((파이어베이스에서 삭제))) 파이에베이스 스토리지는 폴더가 없다, 하나하나가 객체로서 저장 (13'30")
                    desertRef_MARKETS_MarketUid.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SuccessCount--;
                            if(Info.equals("delete")){
                                Market_Storedelete(Market_Uid, marketModel);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showMarketToast(Activity, "Error");
                        }
                    });
                }
            }
        }else{
            if(Info.equals("delete")){
                Market_Storedelete(Market_Uid, marketModel);
            }
        }
    }

    //파이어스토리지에서의 삭제가 끝난 후 파이어스토어에 있는 게시물의 데이터를 삭제한다., 댓글은 하위 컬렉션이기 때문에 미리삭제하고 게시물 삭제로 이동한다.
    private void Market_Storedelete(final String Market_Uid, final MarketModel marketModel) {                                     // part15 : (((DB에서 삭제))) 스토리지에서는 삭제 x
        final FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        final ArrayList<String> CommentList = new ArrayList<>();
        if(SuccessCount == 0){

            // market이 삭제되면, 상대방의 USER에서 진행중이거나 거래완료된 market의 uid삭제
            final String reserved_Uid = marketModel.getMarketModel_reservation();
            final DocumentReference documentReferenceToUser = FirebaseFirestore.getInstance().collection("USERS").document(reserved_Uid);
            documentReferenceToUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    if(userModel != null){
                        if(userModel.getUserModel_Market_reservationList() != null){
                            Market_reservationList = userModel.getUserModel_Market_reservationList();
                            Market_reservationList.remove(marketModel.getMarketModel_Market_Uid());
                            userModel.setUserModel_Market_reservationList(Market_reservationList);
                        }
                        if(userModel.getUserModel_Market_dealList() != null){
                            Market_dealList = userModel.getUserModel_Market_dealList();
                            Market_dealList.remove(marketModel.getMarketModel_Market_Uid());
                            userModel.setUserModel_Market_dealList(Market_dealList);
                        }
                        final DocumentReference documentReferencesetToUser = FirebaseFirestore.getInstance().collection("USERS").document(reserved_Uid);
                        documentReferencesetToUser.set(userModel.getUserInfo())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }

                }
            });
            // market이 삭제되면, 나의 USER에서 진행중이거나 거래완료된 market의 uid삭제
            final DocumentReference documentReferenceCurrentUser = FirebaseFirestore.getInstance().collection("USERS").document(marketModel.getMarketModel_Host_Uid());
            documentReferenceCurrentUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    Market_reservationList = userModel.getUserModel_Market_reservationList();
                    Market_reservationList.remove(marketModel.getMarketModel_Market_Uid());
                    userModel.setUserModel_Market_reservationList(Market_reservationList);
                    Market_dealList = userModel.getUserModel_Market_dealList();
                    Market_dealList.remove(marketModel.getMarketModel_Market_Uid());
                    userModel.setUserModel_Market_dealList(Market_dealList);

                    final DocumentReference documentReferencesetCurrentUser = FirebaseFirestore.getInstance().collection("USERS").document(marketModel.getMarketModel_Host_Uid());
                    documentReferencesetCurrentUser.set(userModel.getUserInfo())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }
            });

            FirebaseFirestore.getInstance().collection("MARKETS").document(marketModel.getMarketModel_Market_Uid()).collection("COMMENT")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommentList.add(document.getId());
                        }
                    }
                    else {
                        Log.d("태그", "Error getting documents: ", task.getException());
                    }
                    for(int i = 0; i < CommentList.size(); i++){
                        Firebasefirestore.collection("MARKETS").document(Market_Uid).collection("COMMENT").document(CommentList.get(i))
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {}
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {}
                                });
                    }
                }
            });

            Firebasefirestore.collection("MARKETS").document(Market_Uid)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMarketToast(Activity, "게시글을 삭제하였습니다.");
                            Onpostlistener.onDelete(marketModel);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { showMarketToast(Activity, "게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }


    //현재 사용자는 룸을 나간다. 룸의 USERS_OUT의 필드값에 해당 uid를 찾아 1 ->0으로 만들어 준다. 해당 유저들의 값이 전부 0일 경우 ROOMS_Storagedelete를 실행 시켜준다.
    public void ROOMS_USERS_OUT_CHECK(final String ChatRoomListModel_RoomUid , final String Current_My_user, final String To_User_Uid){
        FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        Firebasefirestore = FirebaseFirestore.getInstance();
        Firebasefirestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                DocumentSnapshot document = task.getResult();
                Map<String, Long> USERS_OUT = (Map<String, Long>) document.get("RoomModel_USER_OUT");
                if (USERS_OUT.get(To_User_Uid)==0){
                    ROOMS_Storagedelete(ChatRoomListModel_RoomUid);
                }
                else{
                    USERS_OUT.put(Current_My_user, (long) 0);
                    document.getReference().update("RoomModel_USER_OUT", USERS_OUT);
                    ((ChatActivity)ChatActivity.mcontext).ChatFragment_User_GoOut(ChatRoomListModel_RoomUid);
                }
            }
        });
    }


    //룸의 경우에는 이미지가 파이어스토리지에 있기 때문에 파이어스토리지 또한 삭제해주어야한다.
    public void ROOMS_Storagedelete(final String ChatRoomListModel_RoomUid) {                                                 // part16: 스토리지의 삭제 (13')
        FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();                                        // part17 : 스토리지 삭제 (문서) (19'50")
        final StorageReference Storagereference = Firebasestorage.getReference();
        DocumentReference docRefe_ROOMS_CurrentUid = FirebaseFirestore.getInstance().collection("ROOMS").document(ChatRoomListModel_RoomUid);
        docRefe_ROOMS_CurrentUid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RoomModel roomModel = new RoomModel();
                roomModel = documentSnapshot.toObject(RoomModel.class);
                Java_MessageModel_ImageCount = Integer.parseInt(roomModel.getRoomModel_ImageCount());
                if(Java_MessageModel_ImageCount > 0){
                    for (int i = 1; i <= Java_MessageModel_ImageCount; i++) {
                        StorageReference desertRef_ROOMS_ChatRoomListModel_RoomUid = Storagereference.child("ROOMS/" + ChatRoomListModel_RoomUid + "/" + String.valueOf(i));
                        // part17: (((파이어베이스에서 삭제))) 파이에베이스 스토리지는 폴더가 없다, 하나하나가 객체로서 저장 (13'30")
                        desertRef_ROOMS_ChatRoomListModel_RoomUid.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("민규1", "스토리지 삭제 성공");
                                //ROOMS_Storedelete(ChatRoomListModel_RoomUid);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("민규1", "스토리지 삭제 실패");
                            }
                        });
                    }
                }
                ROOMS_Storedelete(ChatRoomListModel_RoomUid);
            }
        });
        //((ChatActivity)ChatActivity.mcontext).ChatFragment_User_GoOut(ChatRoomListModel_RoomUid);
    }


    //파이어스토리지에서의 삭제가 끝난 후 파이어스토어에 있는 채팅의 데이터를 삭제한다., 메세지는 하위 컬렉션이기 때문에 미리삭제하고 Room 삭제로 이동한다.
    private void ROOMS_Storedelete(final String ChatRoomListModel_RoomUid) {
        //Room의 Message 지우기
        final FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        final ArrayList<String> MessageList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("ROOMS").document(ChatRoomListModel_RoomUid).collection("MESSAGE")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MessageList.add(document.getId());
                    }
                }
                else {
                    Log.d("태그", "Error getting documents: ", task.getException());
                }
                for(int i = 0; i < MessageList.size(); i++){
                    Firebasefirestore.collection("ROOMS").document(ChatRoomListModel_RoomUid).collection("MESSAGE").document(MessageList.get(i))
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {}
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {}
                            });
                }
            }
        });
        Firebasefirestore.collection("ROOMS").document(ChatRoomListModel_RoomUid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("민규1", "파이어스토어 삭제 성공");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("민규1", "파이어스토어 삭제 실패", e);
                    }
                });
    }


    //댓글은 파이어스토리지를 이용하지 않으므로 파이어스토어 삭제만 진행한다.
    public void Market_Comment_Storedelete(final Market_CommentModel commentmodel,final MarketModel marketmodel){                                                 // part16: 스토리지의 삭제 (13')
        final String Comment_Uid = commentmodel.getMarket_CommentModel_Comment_Uid();
        FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        Firebasefirestore.collection("MARKETS").document(commentmodel.getMarket_CommentModel_Market_Uid()).collection("COMMENT").document(Comment_Uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMarketToast(Activity, "댓글을 삭제하였습니다.");
                        Onpostlistener.oncommentDelete(commentmodel);

                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        int commentcount;
                        final DocumentReference documentReference = firebaseFirestore.collection("MARKETS").document(marketmodel.getMarketModel_Market_Uid());
                        commentcount = marketmodel.getMarketModel_CommentCount();
                        commentcount--;
                        marketmodel.setMarketModel_CommentCount(commentcount);
                        documentReference.set(marketmodel.getMarketInfo())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { showMarketToast(Activity, "댓글을 삭제하지 못하였습니다.");
                    }
                });
    }


    public void Community_Storagedelete(final CommunityModel communityModel,final String Info){                                                 // part16: 스토리지의 삭제 (13')
        FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();                                        // part17 : 스토리지 삭제 (문서) (19'50")
        StorageReference Storagereference = Firebasestorage.getReference();
        final String Community_Uid = communityModel.getCommunityModel_Community_Uid();
        ArrayList<String> Community_ImageList = communityModel.getCommunityModel_ImageList();
        if(Community_ImageList != null) {
            for (int i = 0; i < Community_ImageList.size(); i++) {
                String Image = Community_ImageList.get(i);
                if (isCommunityStorageUrl(Image)) {
                    SuccessCount++;                                                                             // part17 : 사진의 개수가 여러개인 게시물의 경우 (23'35")
                    StorageReference desertRef_COMMUNITY_CommunityUid = Storagereference.child("COMMUNITY/" + Community_Uid + "/" + storageUrlToName(Image));    // part17: (((파이어베이스에서 삭제))) 파이에베이스 스토리지는 폴더가 없다, 하나하나가 객체로서 저장 (13'30")
                    desertRef_COMMUNITY_CommunityUid.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SuccessCount--;
                            if(Info.equals("delete")){
                                Community_Storedelete(Community_Uid, communityModel);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showCommunityToast(Activity, "Error");
                        }
                    });
                }
            }
        }else{
            if(Info.equals("delete")){
                Community_Storedelete(Community_Uid, communityModel);
            }
        }
    }

    //파이어스토리지에서의 삭제가 끝난 후 파이어스토어에 있는 게시물의 데이터를 삭제한다., 댓글은 하위 컬렉션이기 때문에 미리삭제하고 게시물 삭제로 이동한다.
    private void Community_Storedelete(final String Community_Uid, final CommunityModel communityModel) {                                     // part15 : (((DB에서 삭제))) 스토리지에서는 삭제 x
        final FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        final ArrayList<String> CommentList = new ArrayList<>();
        if(SuccessCount == 0){
            FirebaseFirestore.getInstance().collection("COMMUNITY").document(communityModel.getCommunityModel_Community_Uid()).collection("COMMENT")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CommentList.add(document.getId());
                        }
                    }
                    else {
                        Log.d("태그", "Error getting documents: ", task.getException());
                    }
                    for(int i = 0; i < CommentList.size(); i++){
                        Firebasefirestore.collection("COMMUNITY").document(Community_Uid).collection("COMMENT").document(CommentList.get(i))
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {}
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {}
                                });
                    }
                }
            });

            Firebasefirestore.collection("COMMUNITY").document(Community_Uid)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showCommunityToast(Activity, "게시글을 삭제하였습니다.");
                            Onpostlistener.oncommunityDelete(communityModel);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { showCommunityToast(Activity, "게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    public void Community_Comment_Storedelete(final Community_CommentModel commentmodel,final CommunityModel communityModel){                                                 // part16: 스토리지의 삭제 (13')
        final String Comment_Uid = commentmodel.getCommunity_CommentModel_Comment_Uid();
        FirebaseFirestore Firebasefirestore = FirebaseFirestore.getInstance();
        Firebasefirestore.collection("COMMUNITY").document(commentmodel.getCommunity_CommentModel_Market_Uid()).collection("COMMENT").document(Comment_Uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showCommunityToast(Activity, "댓글을 삭제하였습니다.");
                        Onpostlistener.oncommnitycommentDelete(commentmodel);
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        int commentcount;
                        final DocumentReference documentReference = firebaseFirestore.collection("COMMUNITY").document(communityModel.getCommunityModel_Community_Uid());
                        commentcount = communityModel.getCommunityModel_CommentCount();
                        commentcount--;
                        communityModel.setCommunityModel_CommentCount(commentcount);
                        documentReference.set(communityModel.getCommunityInfo())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { showCommunityToast(Activity, "댓글을 삭제하지 못하였습니다.");
                    }
                });
    }

}