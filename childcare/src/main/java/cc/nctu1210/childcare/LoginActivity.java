package cc.nctu1210.childcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cc.nctu1210.entity.ChildProfile;
import cc.nctu1210.tool.ApplicationContext;
import cc.nctu1210.tool.CallBack;
import cc.nctu1210.tool.CallBackContent;

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button btLogin,btNewGarden;
    private Spinner login_type;
    private int type = 0;  // 0: master,  1: teacher , 2: parent , 3: gateway
    private EditText edtAccount,edtPassword;
    private String [] LoginType= {"admin","teacher","parent","gateway"};
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btLogin = (Button) findViewById(R.id.bt_logIn);
        btNewGarden = (Button) findViewById(R.id.bt_new_garden);
        btLogin.setOnClickListener(this);
        btNewGarden.setOnClickListener(this);
        login_type = (Spinner)findViewById(R.id.login_type);
        login_type.setOnItemSelectedListener(spnOnItemSelected);
    }

    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            type = position;
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    };
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_logIn:
                String account=edtAccount.getText().toString();
                String password=edtPassword.getText().toString();
                final Intent intent_login = new Intent();
                if(type == 0) {
                    intent_login.setClass(LoginActivity.this, MasterLoginTabViewActivity.class);
                    ApplicationContext.login_admin(LoginType[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.gids = content.getGids();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.pids = content.getPids();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == 1)
                {
                    intent_login.setClass(LoginActivity.this, TeacherLoginActivity.class);
                    ApplicationContext.login_teacher(LoginType[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == 2)
                {
                    intent_login.setClass(LoginActivity.this, ParentLoginActivity.class);
                    ApplicationContext.login_parent(LoginType[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mPid = content.getmPid();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(type == 3)
                {
                    intent_login.setClass(LoginActivity.this, GatewayLoginActivity.class);
                    ApplicationContext.login_gateway(LoginType[type], account, password, new CallBack() {
                        @Override
                        public void done(CallBackContent content) {
                            if (content != null) {
                                ApplicationContext.login_mid = content.getMid();
                                ApplicationContext.cids = content.getCids();
                                ApplicationContext.mGid = content.getmGid();
                                ApplicationContext.mIsLogin = true;
                                ApplicationContext.mListChildren.clear();
                                ApplicationContext.mMapChildren.clear();
                                startActivity(intent_login);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fail !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                break;
            case R.id.bt_new_garden:
                Intent intent_new_garden = new Intent();
                intent_new_garden.setClass(LoginActivity.this, MasterAccountCreateActivity.class);
                startActivity(intent_new_garden);
                break;
        }

    }
}
