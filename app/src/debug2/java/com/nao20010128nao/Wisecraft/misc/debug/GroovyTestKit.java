package com.nao20010128nao.Wisecraft.misc.debug;

import android.graphics.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.widget.*;
import com.annimon.stream.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import groovy.lang.*;
import me.champeau.groovydroid.*;
import org.codehaus.groovy.runtime.*;

import java.io.*;
import java.util.*;

/**
 * Created by lesmi on 17/07/14.
 */
public class GroovyTestKit extends AppCompatActivity{
    LinearLayout console;
    EditText script;
    Button enter;
    GTKBinding binding=new GTKBinding();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groovy_test_kit);
        binding.pinned.put("context",this);
        binding.pinned.put("application",TheApplication.instance);
        binding.pinned.put("serverListActivity", ServerListActivity.instance.get());

        console= (LinearLayout) findViewById(R.id.console);
        script= (EditText) findViewById(R.id.script);
        enter= (Button) findViewById(R.id.enter);

        enter.setOnClickListener(v->{
            String scpt;
            if(TextUtils.isEmpty(script.getText())){
                return;
            }
            scpt=script.getText().toString();
            new Thread(()->{
                GrooidShell gs=new GrooidShell(getCacheDir(),getClassLoader());
                GrooidShell.EvalResult result=Debug2Utils.barrier(gs::evaluateWithConfig,scpt,null,cc->{
                    cc.setScriptBaseClass(GTKScript.class.getName());
                });
                if(result==null){
                    runOnUiThread(()->{
                        // nothing to run, treat as null
                        TextView first=createUserTypedTextView();
                        first.setText("> "+scpt);
                        TextView second=createResultTextView();
                        second.setText("--> null");

                        Stream.of(first,second).forEach(console::addView);
                    });
                    return;
                }
                ByteArrayOutputStream dish=new ByteArrayOutputStream();
                PrintStream receiver=new PrintStream(dish);
                binding.setProperty("out",receiver);
                Script script=result.script;
                script.setBinding(binding);
                final ReferencedObject<Throwable> thrown=new ReferencedObject<>();
                final ReferencedObject<Object> returned=new ReferencedObject<>();
                try {
                    returned.set(script.run());
                } catch (Exception e) {
                    thrown.set(e);
                }
                runOnUiThread(()->{
                    // execution finished
                    TextView first=createUserTypedTextView();
                    first.setText("> "+scpt);
                    TextView second;
                    if(thrown.get()==null){
                        second=createResultTextView();
                        second.setText("--> "+InvokerHelper.toString(returned.get()));
                    }else{
                        second=createErrorTextView();
                        ByteArrayOutputStream errDish=new ByteArrayOutputStream();
                        PrintStream errReceiver=new PrintStream(errDish);
                        thrown.checked().printStackTrace(errReceiver);
                        errReceiver.flush();
                        second.setText(errDish.toString());
                    }

                    Stream.of(first,second).forEach(console::addView);
                });
            }).start();
        });
    }

    class GTKBinding extends Binding{
        private Map<String,Object> pinned= Maps.newHashMap();

        @Override
        public void setProperty(String property, Object newValue) {
            if(pinned.containsKey(property)){
                throw new RuntimeException("Cannot set "+property+" into a Script.");
            }
            super.setProperty(property, newValue);
        }

        @Override
        public Object getProperty(String property) {
            if(pinned.containsKey(property)){
                return pinned.get(property);
            }
            return super.getProperty(property);
        }
    }

    @NonNull TextView createBaseTextView(){
        return new AppCompatTextView(this);
    }

    @NonNull TextView createUserTypedTextView(){
        TextView tv=createBaseTextView();
        tv.setTextColor(Color.BLACK);
        ViewCompat.setAlpha(tv,.8f);
        return tv;
    }
    @NonNull TextView createResultTextView(){
        TextView tv=createBaseTextView();
        tv.setTextColor(Color.BLACK);
        return tv;
    }

    @NonNull TextView createErrorTextView(){
        TextView tv=createBaseTextView();
        tv.setTextColor(Color.RED);
        return tv;
    }

    public abstract static class GTKScript extends Script{

    }
}
