package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.Main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.R;
import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.model.user.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.chat.common.ChatUtil;

public class LoginActivity extends AppCompatActivity {
    private SessionCallback sessionCallback;

    private FirebaseAuth Firebaseauth =null;
    private FirebaseUser currentUser=null;

    private GoogleSignInClient mGoogleSignInClient;                         //구글 로그인 진짜 버튼
    private SignInButton local_google_login;                                //카카오 로그인 진짜 버튼

    private ImageView login_google;                                         //구글 로그인 버튼
    private ImageView login_kakao;                                          //카카오 로그인 버튼

    String KakaoPassword = "1234567890";                                    //카카오 패스워드

    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        local_google_login = findViewById(R.id.local_google_login);

        Firebaseauth = FirebaseAuth.getInstance();

        login_google = findViewById(R.id.login_google);
        login_kakao = findViewById(R.id.login_kakao);

        User_login_Check(); //유저 로그인 되어있는지 체크하는 함수
        FirebaseAuthgoogle(); //구글 로그인 메인 함수(onCreate안의 함수)

        login_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KaKaoLoginSession(); //카카오 세션 함수
            }
        });

        //KaKaoLoginSession(); //카카오 세션 함수
        //FirebaseAuthgoogle(); //구글 로그인 메인 함수(onCreate안의 함수)

        ImageView charactor = (ImageView) findViewById(R.id.charactor);
        Glide.with(this).load(R.drawable.character_v2).into(new DrawableImageViewTarget(charactor));

    }

    //유저 로그인 되어있는지 체크하는 함수
    public void User_login_Check(){
        if (currentUser != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //카카오 세션 함수
    public void KaKaoLoginSession(){
        sessionCallback = new SessionCallback(); //세션콜백 초기화
        Session.getCurrentSession().addCallback(sessionCallback);  //현재 세션에 콜백 붙임
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL,this);
        /////////////Session.getCurrentSession().checkAndImplicitOpen();  //자동 로그인
    }

    //카카오 로그인
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);  //현재 액티비티 제거시 콜백도 같이 제거
    }

    //카카오 로그인 SessionCallback
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            //로그인 세션이 열렸을때
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) { //로그인에 실패했을때.인터넷 연결이 불안정해도 이경우에 해당됨
                    int result = errorResult.getErrorCode();
                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) { //로그인 도중 세션이 비정상적인 이유로 닫혔을때
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.d("석ㄱ","000");
                    FirebaseAuthkakaologin(result.getKakaoAccount().getEmail(), KakaoPassword);
                    Log.d("석ㄱ","000");
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) { //로그인 세션이 정상적으로 열리지 않았을 때
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //카카오 로그인
    public void FirebaseAuthkakaologin(final String email, String password) {
        Log.d("석ㄱ","0");
        Firebaseauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("석ㄱ","1");
                            currentUser = Firebaseauth.getCurrentUser();
                            Log.d("석ㄱ","2");
                            updateUI(currentUser);
                            Log.d("석ㄱ","3");
                        } else {
                            String Email = email;
                            FirebaseAuthkakaosignup(Email, KakaoPassword);
                        }
                    }
                });

    }

    //파이어베이스 카카오 가입 함수
    public void FirebaseAuthkakaosignup(String email, String password){
        Firebaseauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()) {
                            Log.d("태그","카카오 로그인(가입) 실패");
                        }else{
                            currentUser = Firebaseauth.getCurrentUser();
                            updateUI(currentUser);
                        }
                    }
                });
    }

    //구글 정보 기입 성공시 실행
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }


    //구글 로그인 메인 함수(onCreate안의 함수)
    public void FirebaseAuthgoogle(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuthgoogle(); //구글 로그인 메인 함수(onCreate안의 함수)
                googlesignIn();
            }
        });
    }

    // 구글 회원가입
    private void googlesignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //구글 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Firebaseauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //추가 -가입할때 uid,id,userm,usermsg 만들기
                            currentUser = Firebaseauth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            // 로그인이 실패하면 사용자에게 메시지를 표시하기
                            updateUI(null);
                            ChatUtil.showMessage(getApplicationContext(), task.getException().getMessage());
                        }
                    }
                });
    }

    //이동 함수 + token 확인
    private void updateUI(final FirebaseUser Curruntuser_uid) { //update ui code here
        if (Curruntuser_uid != null) {

            final String CurrentUid =Curruntuser_uid.getUid();
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.d("민규", "토큰 못 가져옴");
                        return;
                    }
                    //토큰 확인 이거 보안에 문제있을 수도있음 계속 로그가 그런거뜸뜸
                   final String token = task.getResult().getToken();
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUid);

                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserModel Usermodel = documentSnapshot.toObject(UserModel.class);
                            if (Usermodel != null) {
                                if (!token.equals(Usermodel.getUserModel_Token())) {
                                    Usermodel.setUserModel_Token(token);
                                    DocumentReference documentReferencesetUser = FirebaseFirestore.getInstance().collection("USERS").document(CurrentUid);
                                    documentReferencesetUser
                                            .update("UserModel_Token", token)
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
                        }
                    });

                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

