package com.example.homemade_guardian_beta.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import com.example.homemade_guardian_beta.R;
import com.example.homemade_guardian_beta.chat.common.FirestoreAdapter;
import com.example.homemade_guardian_beta.chat.common.ChatUtil;
import com.example.homemade_guardian_beta.chat.model.UserModel;


//그룹채팅에 쓰이는 액티비티
//그룹 채팅방 할때 쓰는거 지워야함
public class SelectUserActivity extends AppCompatActivity {
    private String roomID;
    private Map<String, String> selectedUsers = new HashMap<>();
    private FirestoreAdapter firestoreAdapter;

    @Override
    public void onStart() {
        super.onStart();
        if (firestoreAdapter != null) {
            firestoreAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreAdapter != null) {
            firestoreAdapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        roomID = getIntent().getStringExtra("roomID");

        firestoreAdapter = new RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("USERS").orderBy("userModel_NickName"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager( new LinearLayoutManager((this)));
        recyclerView.setAdapter(firestoreAdapter);

        Button makeRoomBtn = findViewById(R.id.makeRoomBtn);
        if (roomID==null)
             makeRoomBtn.setOnClickListener(makeRoomClickListener);
        else makeRoomBtn.setOnClickListener(addRoomUserClickListener);
    }

    //룸이 없으면 생성버튼
    Button.OnClickListener makeRoomClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (selectedUsers.size() <2) {
                ChatUtil.showMessage(getApplicationContext(), "Please select 2 or more user");
                return;
            }

            selectedUsers.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "");

            DocumentReference newRoom = FirebaseFirestore.getInstance().collection("rooms").document();
            CreateChattingRoom(newRoom);
        }
    };

    //있는 룸 버튼
    Button.OnClickListener addRoomUserClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (selectedUsers.size() <1) {
                ChatUtil.showMessage(getApplicationContext(), "Please select 1 or more user");
                return;
            }
            CreateChattingRoom(FirebaseFirestore.getInstance().collection("rooms").document(roomID) );
        }
    };

    //처음생성 룸 함수
    //말건사람이 1, 받는사람이 0
    public void CreateChattingRoom(final DocumentReference room) {
        //내꺼 uid
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Integer> users = new HashMap<>();
        String title = "";
        for( String key : selectedUsers.keySet()) {
            users.put(key, 0);
            if (title.length() < 20 & !key.equals(uid)) {
                title += selectedUsers.get(key) + ", ";
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("title", title.substring(0, title.length() - 2));
        data.put("users", users);

        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SelectUserActivity.this, ChatActivity.class);
                    intent.putExtra("roomID", room.getId());
                    startActivity(intent);
                    SelectUserActivity.this.finish();
                }
            }
        });
    }

    class RecyclerViewAdapter extends FirestoreAdapter<CustomViewHolder> {

        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        private StorageReference storageReference;
        private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        RecyclerViewAdapter(Query query) {
            super(query);
            storageReference  = FirebaseStorage.getInstance().getReference();
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_user, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder viewHolder, int position) {
            DocumentSnapshot documentSnapshot = getSnapshot(position);
            final UserModel userModel = documentSnapshot.toObject(UserModel.class);

            if (myUid.equals(userModel.getUserModel_Uid())) {
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.itemView.getLayoutParams().height = 0;
                return;
            }

            //닉네임 부분
            viewHolder.user_name.setText(userModel.getUserModel_NickName());

            //그룹 채팅 프로필 사진 부분
            if (userModel.getphotoUrl()!=null) {
                Glide.with(getApplicationContext()).load(userModel.getphotoUrl()).centerCrop().override(500).into(viewHolder.user_photo);
            } else{
                Glide.with(getApplicationContext()).load(R.drawable.user).into(viewHolder.user_photo);
            }


            viewHolder.userChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedUsers.put(userModel.getUserModel_Uid(), userModel.getUserModel_NickName());
                    } else {
                        selectedUsers.remove(userModel.getUserModel_Uid());
                    }
                }
            });
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView user_name;
        public CheckBox userChk;

        CustomViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            user_name = view.findViewById(R.id.user_name);
            userChk = view.findViewById(R.id.userChk);
        }
    }

}
