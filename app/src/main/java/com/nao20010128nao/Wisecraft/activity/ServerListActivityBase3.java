package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.os.*;
import com.ipaulpro.afilechooser.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.misc.*;
import permissions.dispatcher.*;

import java.io.*;


//Wrapper for aFileChooser
@RuntimePermissions
abstract class ServerListActivityBase3 extends ServerListActivityBase4 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addActivityResultReceiver((requestCode, resultCode, data, consumed) -> {
            if (localFileSelectResults.containsKey(requestCode)) {
                switch (resultCode) {
                    case RESULT_OK:
                        localFileSelectResults.get(requestCode).onSelected(new File(data.getStringExtra("path")));
                        break;
                    case RESULT_CANCELED:
                        localFileSelectResults.get(requestCode).onSelectCancelled();
                        break;
                }
                localFileSelectResults.remove(requestCode);
                return true;
            }
            return false;
        });
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseFileForOpen(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (localFileSelectResults.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, FileOpenChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        localFileSelectResults.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseFileForSelect(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (localFileSelectResults.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, FileChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        localFileSelectResults.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseDirectory(File startDir, FileChooserResult result) {
        int call = Math.abs(sr.nextInt()) & 0xf;
        while (localFileSelectResults.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xf;
        }
        Intent intent = new Intent(this, DirectoryChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        localFileSelectResults.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @OnShowRationale({"android.permission.WRITE_EXTERNAL_STORAGE"})
    @Deprecated
    public void _startExtChooseFileRationale(PermissionRequest req) {
        Utils.describeForPermissionRequired(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, req, R.string.permissionsRequiredReasonSelectFile);
    }

    @OnPermissionDenied({"android.permission.WRITE_EXTERNAL_STORAGE"})
    @Deprecated
    public void _startExtChooseFileError() {
        Utils.showPermissionError(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, R.string.permissionsRequiredReasonSelectFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ServerListActivityBase3PermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
