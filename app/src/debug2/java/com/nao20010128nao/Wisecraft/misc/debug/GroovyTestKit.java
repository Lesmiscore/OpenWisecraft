package com.nao20010128nao.Wisecraft.misc.debug;

import android.graphics.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
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
import java.util.Objects;

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
                receiver.flush();
                runOnUiThread(()->{
                    // execution finished
                    TextView first=createUserTypedTextView();
                    first.setText("> "+scpt);
                    TextView second;
                    if(dish.size()!=0) {
                        second = createUserTypedTextView();
                        second.setText(dish.toString());
                    }else{
                        second=null;
                    }
                    TextView third;
                    if(thrown.get()==null){
                        third=createResultTextView();
                        third.setText("--> "+InvokerHelper.toString(returned.get()));
                    }else{
                        third=createErrorTextView();
                        ByteArrayOutputStream errDish=new ByteArrayOutputStream();
                        PrintStream errReceiver=new PrintStream(errDish);
                        thrown.checked().printStackTrace(errReceiver);
                        errReceiver.flush();
                        third.setText(errDish.toString());
                    }

                    Stream.of(first,second,third).filter(Utils::nonNull).forEach(console::addView);
                });
            }).start();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"Predefined variables");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                Map<String,String> descriptions=Maps.newLinkedHashMap();
                descriptions.put("context","GroovyTestKit activity");
                descriptions.put("application","TheApplication object");
                descriptions.put("serverListActivity","ServerListActivity activity");

                new AlertDialog.Builder(this)
                    .setTitle("Predefined variables")
                    .setMessage(
                        Stream.of(descriptions)
                            .map(a-> "§0§l"+a.getKey()+" §o"+a.getValue())
                            .map(Utils::parseMinecraftFormattingCode)
                            .reduce(
                                new SpannableStringBuilder(),
                                (a,b)->a.append(b).append("\n")
                            )
                    )
                    .show();
                return true;
        }
        return false;
    }

    class GTKBinding extends Binding{
        private final Map<String,Object> pinned= Maps.newHashMap();

        @Override
        public void setVariable(String property, Object newValue) {
            if(pinned.containsKey(property)){
                throw new RuntimeException("Cannot set "+property+" into a Script.");
            }
            super.setVariable(property, newValue);
        }

        @Override
        public Object getVariable(String property) {
            if(pinned.containsKey(property)){
                return pinned.get(property);
            }
            return super.getVariable(property);
        }
    }

    @NonNull TextView createBaseTextView(){
        TextView tv=new AppCompatTextView(this);
        tv.setTypeface(Typeface.MONOSPACE);
        return tv;
    }

    @NonNull TextView createUserTypedTextView(){
        TextView tv=createBaseTextView();
        tv.setTextColor(Color.BLACK);
        ViewCompat.setAlpha(tv,.4f);
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
