package com.nao20010128nao.Wisecraft.asfsls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

abstract class ServerGetActivityImpl extends CompatWebViewActivity {
    public static List<String> addForServerList;
    String domain;
    String[] serverList;
    Snackbar downloading;
    BottomSheetBehavior bottomSheet;
    RecyclerView loadedServerListRv;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(com.nao20010128nao.Wisecraft.Icons.App.R.layout.bottomsheet_base);
        getLayoutInflater().inflate(com.nao20010128nao.Wisecraft.Icons.App.R.layout.only_toolbar_cood, (ViewGroup) findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.main));
        getLayoutInflater().inflate(com.nao20010128nao.Wisecraft.Icons.App.R.layout.webview_activity_compat, (ViewGroup) findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.toolbarCoordinator).findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.frame));

        getLayoutInflater().inflate(com.nao20010128nao.Wisecraft.Icons.App.R.layout.yes_no, (ViewGroup) findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.bottomSheet));
        getLayoutInflater().inflate(com.nao20010128nao.Wisecraft.Icons.App.R.layout.recycler_view_content, (ViewGroup) findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.ynDecor).findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.frame));
        scanWebView();
        setSupportActionBar((Toolbar) findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.bottomSheet).setVisibility(View.GONE);
        new Handler().post(() -> {
            Toolbar tb = Utils.getToolbar(ServerGetActivityImpl.this);
            TextView tv = Utils.getActionBarTextView(tb);
            if (tv != null) {
                tv.setGravity(Gravity.CENTER);
            }
        });

        bottomSheet = BottomSheetBehavior.from(findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.bottomSheet));
        bottomSheet.setHideable(true);
        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            public void onStateChanged(View p1, int p2) {
                switch (p2) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        adapter.deleteAll();
                        findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.bottomSheet).setVisibility(View.GONE);
                        break;
                }
            }

            public void onSlide(View p1, float p2) {

            }
        });

        loadedServerListRv = (RecyclerView) findViewById(android.R.id.list);
        loadedServerListRv.setLayoutManager(new LinearLayoutManager(this));
        loadedServerListRv.setAdapter(adapter = new Adapter());

        findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.yes).setOnClickListener(v -> {
            Stream.of(adapter.getSelection()).forEach(ServerListActivity.instance.get()::addIntoList);
            bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.no).setOnClickListener(v -> bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN));

        if (!Utils.isOnline(this)) {
            new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
                .setMessage(com.nao20010128nao.Wisecraft.Icons.App.R.string.offline)
                .setTitle(com.nao20010128nao.Wisecraft.Icons.App.R.string.error)
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
        new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
            .setItems(serverList, (di, w) -> {
                di.dismiss();
                loadUrl("http://" + (domain = serverList[w]) + "/");
            })
            .setTitle(com.nao20010128nao.Wisecraft.Icons.App.R.string.selectWebSite)
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
        downloading = Snackbar.make(findViewById(android.R.id.content), com.nao20010128nao.Wisecraft.Icons.App.R.string.serverGetFetch, Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem find = menu.add(Menu.NONE, 0, 0, com.nao20010128nao.Wisecraft.Icons.App.R.string.findServers).setIcon(TheApplication.getTintedDrawable(com.nao20010128nao.Wisecraft.Icons.App.R.drawable.ic_search_black_48dp, Utils.getMenuTintColor(this), this));
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
                            mbsd.setContentView(com.nao20010128nao.Wisecraft.Icons.App.R.layout.server_get_recycler);
                            final RecyclerView loadedServerListRv = (RecyclerView) mbsd.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.servers);
                            loadedServerListRv.setLayoutManager(new LinearLayoutManager(ServerGetActivityImpl.this));
                            loadedServerListRv.setAdapter(adapter);
                            adapter.addAll(serv);
                            mbsd.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.yes).setOnClickListener(v -> {
                                for (Server s : adapter.getSelection())
                                    ServerListActivity.instance.get().addIntoList(s);
                                mbsd.dismiss();
                            });
                            mbsd.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.no).setOnClickListener(v -> mbsd.dismiss());
                            mbsd.show();
                        } else {
                            //Throwable
                            String msg = ((Throwable) o).getMessage();
                            String dialogMsg = msg;
                            if (msg.startsWith("This website is not supported")) {
                                dialogMsg = getResources().getString(com.nao20010128nao.Wisecraft.Icons.App.R.string.msl_websiteNotSupported) + url;
                            }
                            if (msg.startsWith("Unsupported webpage")) {
                                dialogMsg = getResources().getString(com.nao20010128nao.Wisecraft.Icons.App.R.string.msl_unsupportedWebpage) + url;
                            }

                            new AlertDialog.Builder(ServerGetActivityImpl.this, ThemePatcher.getDefaultDialogStyle(ServerGetActivityImpl.this))
                                .setTitle(com.nao20010128nao.Wisecraft.Icons.App.R.string.error)
                                .setMessage(dialogMsg)
                                .setPositiveButton(android.R.string.ok, Constant.BLANK_DIALOG_CLICK_LISTENER)
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
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(getResources().getStringArray(com.nao20010128nao.Wisecraft.Icons.App.R.array.serverListSites)));
        if (addForServerList != null) result.addAll(addForServerList);
        return Factories.strArray(result);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    public int getCheckColor() {
        TypedArray ta = obtainStyledAttributes(com.nao20010128nao.Wisecraft.Icons.App.R.styleable.ServerGetActivity);
        int color = Color.BLACK;
        if (ta.hasValue(com.nao20010128nao.Wisecraft.Icons.App.R.styleable.ServerGetActivity_wcMenuTintColor)) {
            color = ta.getColor(com.nao20010128nao.Wisecraft.Icons.App.R.styleable.ServerGetActivity_wcMenuTintColor, 0);
        }
        if (ta.hasValue(com.nao20010128nao.Wisecraft.Icons.App.R.styleable.ServerGetActivity_wcSgaCheckColor)) {
            color = ta.getColor(com.nao20010128nao.Wisecraft.Icons.App.R.styleable.ServerGetActivity_wcSgaCheckColor, 0);
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
            Utils.applyHandlersForViewTree(parent.itemView, new OnClickListener(offset));
            if (selected.get(getItem(offset))) {
                parent.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.check).setVisibility(View.VISIBLE);
            } else {
                parent.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.check).setVisibility(View.GONE);
            }
            ((ImageView) parent.findViewById(com.nao20010128nao.Wisecraft.Icons.App.R.id.check)).setImageDrawable(TheApplication.instance.getTintedDrawable(com.nao20010128nao.Wisecraft.Icons.App.R.drawable.ic_check_black_48dp, getCheckColor()));
        }

        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new FindableViewHolder(getLayoutInflater().inflate(com.nao20010128nao.Wisecraft.Icons.App.R.layout.checkable_list_item, parent, false));
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
            List<MslServer> result = new ArrayList<>();
            for (MslServer srv : new ArrayList<>(this))
                if (selected.get(srv))
                    result.add(srv);
            return Utils.convertServerObject(result);
        }

        String makeServerTitle(MslServer sv) {
            StringBuilder sb = new StringBuilder();
            sb.append(sv.ip).append(':').append(sv.port).append(" ");
            sb.append(sv.isPE ? "PE" : "PC");
            return sb.toString();
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
