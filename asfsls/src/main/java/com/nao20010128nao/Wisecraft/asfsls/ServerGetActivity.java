package com.nao20010128nao.Wisecraft.asfsls;

import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.nao20010128nao.Wisecraft.asfsls.misc.AsfslsUtils;
import com.nao20010128nao.Wisecraft.asfsls.misc.FindableViewHolder;
import com.nao20010128nao.Wisecraft.asfsls.misc.ListRecyclerViewAdapter;
import com.nao20010128nao.Wisecraft.asfsls.misc.NonNullableMap;
import com.nao20010128nao.Wisecraft.asfsls.misc.Server;
import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.MslServer;
import com.nao20010128nao.Wisecraft.asfsls.misc.serverList.ServerAddressFetcher;
import com.nao20010128nao.Wisecraft.misc.CompatUtils;
import com.nao20010128nao.Wisecraft.misc.DebugWriter;
import com.nao20010128nao.Wisecraft.misc.ModifiedBottomSheetDialog;
import com.nao20010128nao.Wisecraft.misc.compat.CompatWebViewActivity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class ServerGetActivityImpl extends CompatWebViewActivity {
    public static List<String> addForServerList= Collections.emptyList();
    String domain;
    String[] serverList;
    Snackbar downloading;
    BottomSheetBehavior bottomSheet;
    RecyclerView loadedServerListRv;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // require Wisecraft or WRcon(it doesn't!!) to be called from
        Log.d("SGA-isolated","Caller: "+getCallingPackage());
        /*if(getPackageManager().checkSignatures(getCallingPackage(),getPackageName())!= PackageManager.SIGNATURE_MATCH){
            finish();
            return;
        }*/

        setContentView(R.layout.bottomsheet_base);
        getLayoutInflater().inflate(R.layout.only_toolbar_cood, findViewById(R.id.main));
        getLayoutInflater().inflate(R.layout.webview_activity_compat, findViewById(R.id.toolbarCoordinator).findViewById(R.id.frame));

        getLayoutInflater().inflate(R.layout.yes_no, findViewById(R.id.bottomSheet));
        getLayoutInflater().inflate(R.layout.recycler_view_content, findViewById(R.id.ynDecor).findViewById(R.id.frame));
        scanWebView();
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findViewById(R.id.bottomSheet).setVisibility(View.GONE);
        new Handler().post(() -> {
            Toolbar tb = CompatUtils.getToolbar(ServerGetActivityImpl.this);
            TextView tv = CompatUtils.getActionBarTextView(tb);
            if (tv != null) {
                tv.setGravity(Gravity.CENTER);
            }
        });

        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        bottomSheet.setHideable(true);
        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            public void onStateChanged(View p1, int p2) {
                switch (p2) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        adapter.deleteAll();
                        findViewById(R.id.bottomSheet).setVisibility(View.GONE);
                        break;
                }
            }

            public void onSlide(View p1, float p2) {

            }
        });

        loadedServerListRv = findViewById(android.R.id.list);
        loadedServerListRv.setLayoutManager(new LinearLayoutManager(this));
        loadedServerListRv.setAdapter(adapter = new Adapter());

        findViewById(R.id.yes).setOnClickListener(v -> {
            startActivity(AsfslsUtils.makeMagicSpell(adapter.getSelection()));
            bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(R.id.no).setOnClickListener(v -> bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN));

        if (!AsfslsUtils.isOnline(this)) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.offline)
                .setTitle(R.string.error)
                .setOnCancelListener(di -> {
                    finish();
                    Log.d("SGA", "cancel");
                })
                .setOnDismissListener(di -> {
                    //finish();
                    Log.d("SGA", "dismiss");
                })
                .show();
            return;
        }
        serverList = createServerListDomains();
        new AlertDialog.Builder(this)
            .setItems(serverList, (di, w) -> {
                di.dismiss();
                loadUrl("http://" + (domain = serverList[w]) + "/");
            })
            .setTitle(R.string.selectWebSite)
            .setOnCancelListener(di -> {
                finish();
                Log.d("SGA", "cancel");
            })
            .setOnDismissListener(di -> {
                //finish();
                Log.d("SGA", "dismiss");
            })
            .show();
        getWebView().setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView wv, String url) {
                setTitle(wv.getTitle());
                getSupportActionBar().setSubtitle(wv.getUrl());
            }
        });
        downloading = Snackbar.make(findViewById(android.R.id.content), R.string.serverGetFetch, Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem find = menu.add(Menu.NONE, 0, 0, R.string.findServers).setIcon(TheApplication.getTintedDrawable(R.drawable.ic_search_black_48dp, AsfslsUtils.getMenuTintColor(this), this));
        MenuItemCompat.setShowAsAction(find, MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //List<MslServer>
                downloading.show();
                new AsyncTask<String, Void, Object>() {
                    String url;
                    boolean[] selections;

                    public Object doInBackground(String... a) {
                        try {
                            return ServerAddressFetcher.findServersInWebpage(new URL(url = a[0]));
                        } catch (Throwable e) {
                            DebugWriter.writeToD("ServerGetActivity.gettingServer#" + url, e);
                            return e;
                        }
                    }

                    public void onPostExecute(Object o) {
                        downloading.dismiss();
                        if (o instanceof List) {
                            //Server list
                            final List<MslServer> serv = (List<MslServer>) o;
                            /*
                            adapter.deleteAll();
							adapter.addAll(serv);
							bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
							findViewById(R.id.bottomSheet).setVisibility(View.VISIBLE);
							*/
                            final ModifiedBottomSheetDialog mbsd = new ModifiedBottomSheetDialog(ServerGetActivityImpl.this);
                            final Adapter adapter = new Adapter();
                            mbsd.setContentView(R.layout.server_get_recycler);
                            final RecyclerView loadedServerListRv = mbsd.findViewById(R.id.servers);
                            loadedServerListRv.setLayoutManager(new LinearLayoutManager(ServerGetActivityImpl.this));
                            loadedServerListRv.setAdapter(adapter);
                            adapter.addAll(serv);
                            mbsd.findViewById(R.id.yes).setOnClickListener(v -> {
                                startActivity(AsfslsUtils.makeMagicSpell(adapter.getSelection()));
                                mbsd.dismiss();
                            });
                            mbsd.findViewById(R.id.no).setOnClickListener(v -> mbsd.dismiss());
                            mbsd.show();
                        } else {
                            //Throwable
                            String msg = ((Throwable) o).getMessage();
                            String dialogMsg = msg;
                            if (msg.startsWith("This website is not supported")) {
                                dialogMsg = getResources().getString(R.string.msl_websiteNotSupported) + url;
                            }
                            if (msg.startsWith("Unsupported webpage")) {
                                dialogMsg = getResources().getString(R.string.msl_unsupportedWebpage) + url;
                            }

                            new AlertDialog.Builder(ServerGetActivityImpl.this)
                                .setTitle(R.string.error)
                                .setMessage(dialogMsg)
                                .setPositiveButton(android.R.string.ok, (i,w)->{})
                                .show();
                        }
                    }

                    public List<MslServer> getServers(List<MslServer> all, boolean[] balues) {
                        List<MslServer> lst = new ArrayList<>();
                        for (int i = 0; i < balues.length; i++) {
                            if (balues[i]) {
                                lst.add(all.get(i));
                            }
                        }
                        return lst;
                    }
                }.execute(getWebView().getUrl());
                break;
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        switch (bottomSheet.getState()) {
            case BottomSheetBehavior.STATE_EXPANDED:
                bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            default:
                if (getWebView().canGoBack()) {
                    getWebView().goBack();
                } else {
                    finish();
                }
                break;
        }
    }

    public String[] createServerListDomains() {
        return Stream.concat(
            Stream.of(getResources().getStringArray(R.array.serverListSites)),
            Stream.of(addForServerList)
        ).toArray(String[]::new);
    }

    public int getCheckColor() {
        TypedArray ta = obtainStyledAttributes(R.styleable.ServerGetActivity);
        int color = Color.BLACK;
        if (ta.hasValue(R.styleable.ServerGetActivity_wcMenuTintColor)) {
            color = ta.getColor(R.styleable.ServerGetActivity_wcMenuTintColor, 0);
        }
        if (ta.hasValue(R.styleable.ServerGetActivity_wcSgaCheckColor)) {
            color = ta.getColor(R.styleable.ServerGetActivity_wcSgaCheckColor, 0);
        }
        ta.recycle();
        return color;
    }

    class Adapter extends ListRecyclerViewAdapter<FindableViewHolder, MslServer> {
        Map<MslServer, Boolean> selected = new NonNullableMap<>();

        @Override
        public void onBindViewHolder(FindableViewHolder parent, int offset) {
            ((TextView) parent.findViewById(android.R.id.text1)).setText(makeServerTitle(getItem(offset)));
            parent.itemView.setTag(getItem(offset));
            CompatUtils.applyHandlersForViewTree(parent.itemView, new OnClickListener(offset));
            parent.findViewById(R.id.check).setVisibility(selected.get(getItem(offset)) ? View.VISIBLE : View.GONE);
            ((ImageView) parent.findViewById(R.id.check)).setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_check_black_48dp, getCheckColor()));
        }

        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new FindableViewHolder(getLayoutInflater().inflate(R.layout.checkable_list_item, parent, false));
        }

        public void clearSelectedState() {
            selected.clear();
            notifyItemRangeChanged(0, size());
        }

        public void deleteAll() {
            clear();
            selected.clear();
        }

        public List<Server> getSelection() {
            return AsfslsUtils.convertServerObject(Stream.of(this).filter(selected::get).toList());
        }

        String makeServerTitle(MslServer sv) {
            return sv.ip + ':' + sv.port + " " +
                (sv.isPE ? "PE" : "PC");
        }

        class OnClickListener implements View.OnClickListener {
            int ofs;

            public OnClickListener(int i) {
                ofs = i;
            }

            @Override
            public void onClick(View p1) {
                MslServer s = getItem(ofs);
                selected.put(s, !selected.get(s));
                notifyItemChanged(ofs);
            }
        }
    }
}

public class ServerGetActivity extends ServerGetActivityImpl {

}
