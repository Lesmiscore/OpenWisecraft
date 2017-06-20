package com.nao20010128nao.WRcon.misc;

import android.content.*;
import com.ipaulpro.afilechooser.*;
import com.nao20010128nao.Wisecraft.misc.compat.*;

import java.io.*;
import java.security.*;
import java.util.*;


//Wrapper for aFileChooser
public class MainActivityBase1 extends AppCompatListActivity {
    SecureRandom sr = new SecureRandom();
    Map<Integer, FileChooserResult> results = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (results.containsKey(requestCode)) {
            switch (resultCode) {
                case RESULT_OK:
                    results.get(requestCode).onSelected(new File(data.getStringExtra("path")));
                    break;
                case RESULT_CANCELED:
                    results.get(requestCode).onSelectCancelled();
                    break;
            }
            results.remove(requestCode);
        }
    }

    public void startChooseFileForOpen(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (results.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, FileOpenChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    public void startChooseFileForSelect(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (results.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, FileChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    public void startChooseDirectory(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (results.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, DirectoryChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }


    public static interface FileChooserResult {
        public void onSelected(File f);

        public void onSelectCancelled();
    }
}
