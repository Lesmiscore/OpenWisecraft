package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

public class GenerateWisecraftOpenLinkActivity extends AppCompatActivity {
    static final String PREFIX = "wisecraft://";

    static final String ADD_SERVER = "addserver";
    static final String SERVER_DETAILS = "info";

    static final String PC = "java";
    static final String PE = "mobile";

    EditText ip, port;
    RadioGroup action, mode;
    Button generate, copy;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_wisecraft_open_link);

        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);

        action = findViewById(R.id.action);
        mode = findViewById(R.id.mode);

        generate = findViewById(R.id.generate);
        copy = findViewById(R.id.copy);
        result = findViewById(R.id.result);

        Intent data = getIntent();
        if (data.hasExtra("ip") & data.hasExtra("port") & data.hasExtra("mode")) {
            ip.setText(data.getStringExtra("ip"));
            port.setText(data.getIntExtra("port", 19132) + "");
            mode.check(data.getIntExtra("mode", 0) == 0 ? R.id.pe : R.id.pc);
        }

        generate.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            switch (action.getCheckedRadioButtonId()) {
                case R.id.addServer:
                    sb.append(ADD_SERVER);
                    break;
                case R.id.serverDetails:
                    sb.append(SERVER_DETAILS);
                    break;
            }
            sb.append('/').append(ip.getText()).append('/').append(port.getText()).append('/');
            switch (mode.getCheckedRadioButtonId()) {
                case R.id.pc:
                    sb.append(PC);
                    break;
                case R.id.pe:
                    sb.append(PE);
                    break;
            }
            result.setVisibility(View.VISIBLE);
            result.setText(sb);
            copy.setVisibility(View.VISIBLE);
        });
        copy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setText(result.getText().toString());
            Snackbar.make(copy, R.string.copied, Snackbar.LENGTH_LONG).show();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }
}
