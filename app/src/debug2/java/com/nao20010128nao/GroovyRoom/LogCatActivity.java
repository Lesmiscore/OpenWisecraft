package com.nao20010128nao.GroovyRoom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.common.io.CharStreams;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.WisecraftError;
import com.nao20010128nao.Wisecraft.misc.Utils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lesmi on 17/07/13.
 */
public class LogCatActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_cat);
        TextView log= (TextView) findViewById(R.id.logcat);
        ScrollView scroll= (ScrollView) log.getParent();
        
        log.setText("");
        new Thread(()->{
            Process process=Utils.barrier(new ProcessBuilder()
                .command("logcat")
                .redirectErrorStream(true)
                ::start);
            Utils.readLines(process.getInputStream(),line->runOnUiThread(()-> log.append(line+"\n")));
            scroll.fullScroll(View.FOCUS_DOWN);
        }).start();
    }
}
