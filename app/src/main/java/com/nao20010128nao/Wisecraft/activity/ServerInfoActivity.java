package com.nao20010128nao.Wisecraft.activity;

import android.annotation.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.graphics.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import biz.laenger.android.vpbs.*;
import com.astuetz.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.misc.json.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;
import com.nao20010128nao.Wisecraft.misc.skin_face.*;
import permissions.dispatcher.*;

import java.io.*;
import java.lang.ref.*;
import java.math.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

@RuntimePermissions
abstract class ServerInfoActivityImpl extends ServerInfoActivityBase1 {
    static WeakReference<ServerInfoActivity> instance = new WeakReference(null);
    //public static List<ServerStatus> stat=new ArrayList<>();
    public static Map<String, Bitmap> faces = new HashMap<>();

    public static int DIRT_BRIGHT, DIRT_DARK;
    public static final int BASE64_FLAGS = WisecraftBase64.NO_WRAP | WisecraftBase64.NO_PADDING;

    SharedPreferences pref;

    ServerStatus localStat;
    Bundle keeping;

    String ip;
    int port;
    boolean nonUpd, hidePlayer, hideData, hidePlugins, hideMods, noExport;
    String token;// null if this activity is called from non-SLA activity

    MenuItem updateBtn, seeTitleButton, exportButton;

    InternalPagerAdapter adapter;
    ViewPager tabs;

    List<Bitmap> skinFaceImages;
    SkinFaceFetcher sff;

    View bottomSheet;
    LockableViewPagerBottomSheetBehavior behavior;
    boolean useBottomSheet = false;
    View background;//it is actually CoordinatorLayout

    ServerListStyleLoader slsl;

    ViewGroup snackbarParent;
    FloatingActionButton pin;

    /*Only for PC servers*/
    Drawable serverIconObj;
    Bitmap serverIconBmp;
    CharSequence serverNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        instance = new WeakReference(this);
        slsl = (ServerListStyleLoader) getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);

        String stat = getIntent().getStringExtra("stat");
        if (stat == null) {
            finish();
            return;
        }
        byte[] statData = WisecraftBase64.decode(stat, BASE64_FLAGS);
        localStat = PingSerializeProvider.loadFromServerDumpFile(statData);

        if (localStat == null) {
            finish();
            return;
        }

        keeping = getIntent().getBundleExtra("object");
        useBottomSheet = getIntent().getBooleanExtra("bottomSheet", true) & !pref.getBoolean("noScrollServerInfo", false);

        if (useBottomSheet) {
            setContentView(R.layout.server_info_pager);
            snackbarParent = (ViewGroup) findViewById(R.id.coordinator);
            pin = (FloatingActionButton) findViewById(R.id.pin);
        } else {
            setContentView(R.layout.server_info_pager_nobs);
            snackbarParent = (ViewGroup) findViewById(android.R.id.content);
        }
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        tabs = (ViewPager) findViewById(R.id.pager);
        tabs.setAdapter(adapter = new InternalPagerAdapter());
        tabs.setPageTransformer(true, new ZoomOutPageTransformer());
        PagerSlidingTabStrip psts = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        psts.setViewPager(tabs);

        hideData = getIntent().getBooleanExtra("nonDetails", false);
        hidePlayer = getIntent().getBooleanExtra("nonPlayers", false);
        hidePlugins = getIntent().getBooleanExtra("nonPlugins", false);
        hideMods = getIntent().getBooleanExtra("nonMods", false);

        if (!hidePlayer) {
            if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
                adapter.addTab(UcpInfoFragment.class, getResources().getString(R.string.players));
            } else {
                adapter.addTab(PlayersFragment.class, getResources().getString(R.string.players));
            }
        }

        if (!hideData) {
            if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
                adapter.addTab(UcpDetailsFragment.class, getResources().getString(R.string.data));
            } else {
                switch (localStat.mode) {
                    case PE:
                        adapter.addTab(DataFragmentPE.class, getResources().getString(R.string.data));
                        break;
                    case PC:
                        adapter.addTab(DataFragmentPC.class, getResources().getString(R.string.data));
                        break;
                }
            }
        }

        if (!(hidePlugins | localStat.mode == Protobufs.Server.Mode.PC)) {
            if (localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
                adapter.addTab(UcpInfoFragment.class, getResources().getString(R.string.plugins));
            } else {
                adapter.addTab(PluginsFragment.class, getResources().getString(R.string.plugins));
            }
        }

        nonUpd = getIntent().getBooleanExtra("nonUpd", false);
        noExport = getIntent().getBooleanExtra("noExport", false);

        ip = localStat.ip;
        port = localStat.port;

        update(localStat.response);

        psts.setIndicatorColor(slsl.getTextColor());
        psts.setTextColor(slsl.getTextColor());
        psts.setOnPageChangeListener(new ViewPagerChangeListenerObserver(
            new ColorUpdater(slsl.getTextColor(), translucent(slsl.getTextColor()), tabs, psts),
            new PstsTextStyleChanger(Typeface.BOLD, Typeface.NORMAL, tabs, psts)
        ));

        ViewCompat.setBackground(findViewById(R.id.appbar), slsl.load());

        int offset = getIntent().getIntExtra("offset", 0);
        if (adapter.getCount() >= 2 & offset == 0) tabs.setCurrentItem(1);
        tabs.setCurrentItem(offset);

        if (useBottomSheet) {
            BottomSheetUtils.setupViewPager(tabs);
            bottomSheet = findViewById(R.id.serverInfoFragment);
            behavior = (LockableViewPagerBottomSheetBehavior) ViewPagerBottomSheetBehavior.from(bottomSheet);
            behavior.setHideable(true);
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);

            if (Build.VERSION.SDK_INT >= 21) {
                behavior.setBottomSheetCallback(new ColorUpdateCallback());
            } else {
                behavior.setBottomSheetCallback(new UpdateCallback());
            }

            background = findViewById(R.id.background);
            background.setOnClickListener(v -> {
                if (behavior.getAllowUserDragging()) {
                    behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
                }
            });
            background.setBackgroundColor(slsl.getBackgroundSimpleColor());
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(0);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(slsl.getBackgroundSimpleColor());
            }
        }
        TypedArray ta = ThemePatcher.getStyledContext(this).obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        ViewCompat.setBackground(tabs, ta.getDrawable(0));
        ta.recycle();

        if (useBottomSheet) {
            pin.setVisibility(View.GONE);
            behavior.setAllowUserDragging(true);
            pin.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_lock_open_black_48dp, Color.WHITE));//pinned
            pin.setOnClickListener(v -> {
                behavior.setAllowUserDragging(!behavior.getAllowUserDragging());
                if (behavior.getAllowUserDragging()) {
                    pin.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_lock_open_black_48dp, Color.WHITE));//not pinned
                } else {
                    pin.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_lock_black_48dp, Color.WHITE));//pinned
                }
            });
            if (getIntent().getBooleanExtra("bottomSheetPinned", false)) {
                pin.setImageDrawable(TheApplication.instance.getTintedDrawable(R.drawable.ic_lock_black_48dp, Color.WHITE));//pinned
                behavior.setAllowUserDragging(false);
                new Handler().post(() -> {
                    behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                    pin.show();
                });
            }
        }

        new Handler().post(() -> {
            TextView tv = Utils.getActionBarTextView(Utils.getToolbar(ServerInfoActivityImpl.this));
            if (tv != null) tv.setTextColor(slsl.getTextColor());
        });

        token = getIntent().getStringExtra("token");
    }

    public void onBackPressed() {
        if (useBottomSheet) {
            if (behavior.getAllowUserDragging()) {
                switch (behavior.getState()) {
                    case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                        behavior.setState(ViewPagerBottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                        behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
                        break;
                }
            }
        } else {
            supportFinishAfterTransition();
        }
    }

    public void scheduleFinish() {
        if (useBottomSheet) {
            behavior.setState(ViewPagerBottomSheetBehavior.STATE_HIDDEN);
        } else {
            supportFinishAfterTransition();
        }
    }

    @ServerInfoParser
    public synchronized void update(final ServerPingResult resp) {
        if (resp instanceof FullStat) {
            FullStat fs = (FullStat) resp;
            final String title;
            Map<String, String> m = fs.getDataAsMap();
            if (m.containsKey("hostname")) {
                title = m.get("hostname");
            } else if (m.containsKey("motd")) {
                title = m.get("motd");
            } else {
                title = ip + ":" + port;
            }
            setTitle(title);
        } else if (resp instanceof RawJsonReply) {
            RawJsonReply rep = (RawJsonReply) resp;
            CharSequence title;
            if (!rep.json.has("description")) {
                title = localStat.toString();
            } else {
                title = Utils.parseMinecraftDescriptionJson(rep.json.get("description"));
            }
            setTitle(title);
        } else if (resp instanceof SprPair) {
            SprPair p = (SprPair) resp;
            update(p.getA());
            update(p.getB());
        } else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp != localStat.response) {
            setTitle((((UnconnectedPing.UnconnectedPingResult) resp).getServerName()));
            addUcpDetailsTab();
        } else if (resp instanceof UnconnectedPing.UnconnectedPingResult & resp == localStat.response) {
            setTitle((((UnconnectedPing.UnconnectedPingResult) resp).getServerName()));
        }
        Utils.getToolbar(this).setSubtitle(localStat.toString());


        updateTaskDesc(resp);
    }

    @ServerInfoParser
    public void updateTaskDesc(ServerPingResult resp) {
        if (Build.VERSION.SDK_INT >= 21) {
            int color = ThemePatcher.getMainColor(this);
            if (resp instanceof RawJsonReply) {
                WisecraftJsonObject rep = ((RawJsonReply) resp).json;
                if (rep.has("favicon")) {
                    byte[] image = WisecraftBase64.decode(rep.get("favicon").getAsString().split("\\,")[1], WisecraftBase64.NO_WRAP);
                    serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                    serverIconObj = new BitmapDrawable(getResources(), serverIconBmp);
                } else {
                    serverIconObj = null;
                }
            }
            CompatTaskDescription td;
            switch (localStat.mode) {
                case PC:
                    td = new CompatTaskDescription(
                        getTitle().toString(),
                        serverIconBmp != null ?
                            serverIconBmp :
                            BitmapFactory.decodeResource(getResources(), R.drawable.wisecraft_icon),
                        color
                    );
                    break;
                default:
                    td = new CompatTaskDescription(
                        getTitle().toString(),
                        BitmapFactory.decodeResource(getResources(), R.drawable.wisecraft_icon),
                        color
                    );
                    break;
            }
            setTaskDescription(td);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!noExport) {
            exportButton = menu.add(Menu.NONE, 0, 0, R.string.exportPing);
            exportButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_file_upload_black_48dp, slsl.getTextColor()));
            MenuItemCompat.setShowAsAction(exportButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        seeTitleButton = menu.add(Menu.NONE, 1, 1, R.string.seeTitle);
        seeTitleButton.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_open_in_new_black_48dp, slsl.getTextColor()));
        MenuItemCompat.setShowAsAction(seeTitleButton, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        if (!nonUpd) {
            updateBtn = menu.add(Menu.NONE, 2, 2, R.string.update);
            updateBtn.setIcon(TheApplication.instance.getTintedDrawable(com.nao20010128nao.MaterialIcons.R.drawable.ic_refresh_black_48dp, slsl.getTextColor()));
            MenuItemCompat.setShowAsAction(updateBtn, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0://Export this ping result
                View dialogView_ = getLayoutInflater().inflate(R.layout.server_list_imp_exp, null);
                final EditText et_ = (EditText) dialogView_.findViewById(R.id.filePath);
                et_.setText(new File(Environment.getExternalStorageDirectory(), "/Wisecraft/pingresult.wisecraft-ping").toString());
                dialogView_.findViewById(R.id.selectFile).setOnClickListener(v -> {
                    File f = new File(et_.getText().toString());
                    if ((!f.exists()) | f.isFile()) f = f.getParentFile();
                    ServerInfoActivityBase1PermissionsDispatcher.startChooseFileForOpenWithCheck(ServerInfoActivityImpl.this, f, new FileChooserResult() {
                        public void onSelected(File f) {
                            et_.setText(f.toString());
                        }

                        public void onSelectCancelled() {/*No-op*/}
                    });
                });
                new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
                    .setTitle(R.string.export_typepath_simple)
                    .setView(dialogView_)
                    .setPositiveButton(android.R.string.ok, (di, w) -> ServerInfoActivityImplPermissionsDispatcher.exportCurrentServerStatusWithCheck(ServerInfoActivityImpl.this, et_.getText().toString()))
                    .show();
                break;
            case 2://Update
                if (useBottomSheet) {
                    setResultInstead(Constant.ACTIVITY_RESULT_UPDATE, new Intent().putExtra("offset", tabs.getCurrentItem()).putExtra("bottomSheetPinned", !behavior.getAllowUserDragging()));
                } else {
                    setResultInstead(Constant.ACTIVITY_RESULT_UPDATE, new Intent().putExtra("offset", tabs.getCurrentItem()));
                }
                scheduleFinish();//ServerListActivity updates the stat
                return true;
            case 1://See the title for all
                AlertDialog.Builder ab = new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this));
                LinearLayout ll;
                boolean dark;
                dark = pref.getBoolean("colorFormattedText", false) && pref.getBoolean("darkBackgroundForServerName", false);
            {
                ll = (LinearLayout) TheApplication.instance.getLayoutInflater().inflate(
                    dark ? R.layout.server_info_show_title_dark : R.layout.server_info_show_title, null
                );
                ViewCompat.setBackground(ll, slsl.load());
            }
            TextView serverNameView = (TextView) ll.findViewById(R.id.serverName);
            serverNameView.setText(getTitle());
            serverNameView.setTextColor(slsl.getTextColor());
            ll.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ab.setView(ll).show();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @NeedsPermission("android.permission.WRITE_EXTERNAL_STORAGE")
    public void exportCurrentServerStatus(String fn) {
        Utils.makeSB(snackbarParent, R.string.exporting, Snackbar.LENGTH_LONG).show();
        new AsyncTask<String, Void, File>() {
            public File doInBackground(String... texts) {
                File f;
                byte[] data = PingSerializeProvider.doRawDumpForFile(localStat.response);
                if (writeToFileByBytes(f = new File(texts[0]), data))
                    return f;
                else
                    return null;
            }

            public void onPostExecute(File f) {
                if (f != null) {
                    Utils.makeSB(snackbarParent, getResources().getString(R.string.export_complete).replace("[PATH]", f + ""), Snackbar.LENGTH_LONG).show();
                } else {
                    Utils.makeSB(snackbarParent, getResources().getString(R.string.export_failed), Snackbar.LENGTH_LONG).show();
                }
            }
        }.execute(fn);
    }

    public void setResultInstead(int resultCode, Intent data) {
        setResult(resultCode, data.putExtra("object", keeping).putExtra("token", token));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title == null) {
            Log.d("ServerInfoActivity", "title == null");
            if (pref.getBoolean("serverListColorFormattedText", false)) {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(localStat.toString());
                ssb.setSpan(new ForegroundColorSpan(slsl.getTextColor()), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                super.setTitle(ssb);
            } else {
                super.setTitle(localStat.toString());
            }
        } else {
            Log.d("ServerInfoActivity", "title != null");
            Log.d("ServerInfoActivity", "length: " + title.length());
            Log.d("ServerInfoActivity", title.toString());
            if (pref.getBoolean("serverListColorFormattedText", false)) {
                if (title instanceof String) {
                    super.setTitle(Utils.parseMinecraftFormattingCode(title.toString()));
                } else {
                    super.setTitle(title);
                }
            } else {
                if (title instanceof String) {
                    super.setTitle(Utils.deleteDecorations(title.toString()));
                } else {
                    super.setTitle(title.toString());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(() -> pref.edit().putString("pcuseruuids", Utils.newGson().toJson(TheApplication.pcUserUUIDs)).commit()).start();
        if (serverIconBmp != null) serverIconBmp.recycle();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ServerInfoActivityImplPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void addModsTab() {
        if ((!hideMods) | localStat.mode == Protobufs.Server.Mode.PC) {
            adapter.addTab(ModsFragment.class, getResources().getString(R.string.mods));
        }
    }

    public void addUcpDetailsTab() {
        adapter.addTab(UcpDetailsFragment.class, getResources().getString(R.string.data_ucp));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    class PCUserFaceAdapter extends PlayerNamesListAdapter {
        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new VH(getLayoutInflater().inflate(R.layout.simple_list_item_with_image, parent, false));
        }

        @Override
        public void onBindViewHolder(FindableViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position);
            View convertView = holder.itemView;
            String playerName = getItem(position);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(playerName);
            ImageView iv = (ImageView) convertView.findViewById(R.id.image);
            if (faces.containsKey(playerName)) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageBitmap(faces.get(playerName));
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                iv.setVisibility(View.GONE);
                String uuid = TheApplication.pcUserUUIDs.get(playerName);
                sff.requestLoadSkin(playerName, uuid, new Handler());
                iv.setImageBitmap(null);
            }
        }

        public class VH extends FindableViewHolder {
            public VH(View w) {
                super(w);
            }
        }

        class Handler implements SkinFetcher.SkinFetchListener {
            @Override
            public void onError(String player) {
                Log.d("face", "err:" + player);
            }

            @Override
            public void onSuccess(final Bitmap bmp, final String player) {
                skinFaceImages.add(bmp);
                new AsyncTask<Bitmap, Void, Bitmap>() {
                    public Bitmap doInBackground(Bitmap... datas) {
                        Bitmap toProc = datas[0];
                        int clSiz = getResources().getDimensionPixelSize(R.dimen.list_height) / 8 + 1;
                        return ImageResizer.resizeBitmapPixel(toProc, clSiz, Bitmap.Config.ARGB_4444);
                    }

                    public void onPostExecute(final Bitmap bmp) {
                        skinFaceImages.add(bmp);
                        faces.put(player, bmp);
                        runOnUiThread(() -> {
                            notifyItemChanged(indexOf(player));
                            Log.d("face", "ok:" + player);
                        });
                    }
                }.execute(bmp);
            }
        }
    }

    class ModInfoListAdapter extends ListRecyclerViewAdapter<FindableViewHolder, Object> {
        @Override
        public int getItemCount() {
            return super.getItemCount() + 1;
        }

        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            switch (type) {
                case 0:
                    return new VH(getLayoutInflater().inflate(R.layout.void_view, parent, false));
                case 1:
                    return new VH(getLayoutInflater().inflate(R.layout.mod_info_content, parent, false));
                default:
                    return null;
            }
        }

        @Override
        @ServerInfoParser
        public void onBindViewHolder(FindableViewHolder parent, int offset) {
            if (offset == 0) return;
            int position = offset - 1;
            View v = parent.itemView;
            Object o = getItem(position);
            if (o instanceof WisecraftJsonObject) {
                WisecraftJsonObject mlc = (WisecraftJsonObject) o;
                ((TextView) v.findViewById(R.id.modName)).setText(mlc.get("modid").getAsString());
                ((TextView) v.findViewById(R.id.modVersion)).setText(mlc.get("version").getAsString());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return 0;
            else return 1;
        }

        public class VH extends FindableViewHolder {
            public VH(View w) {
                super(w);
            }
        }
    }

    class PlayerNamesListAdapter extends ListRecyclerViewAdapter<FindableViewHolder, String> {
        boolean pcMode;

        public PlayerNamesListAdapter() {
            super(new ArrayList<>());
        }

        @Override
        public String getItem(int position) {
            String s = super.getItem(position);
            if (pref.getBoolean("deleteDecoPlayerName", false))
                s = deleteDecorations(s);
            return s;
        }

        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new VH(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(FindableViewHolder parent, int offset) {
            final String name = getItem(offset);
            ((TextView) parent.findViewById(android.R.id.text1)).setText(name);
            if (pcMode) {
                TypedArray ta = obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
                ViewCompat.setBackground(parent.itemView, ta.getDrawable(0));
                ta.recycle();
                Utils.applyHandlersForViewTree(parent.itemView, v -> {
                    MCPlayerInfoDialog dialog = new MCPlayerInfoDialog(ServerInfoActivityImpl.this);
                    dialog.setPlayer(name);
                    dialog.show();
                }, null);
            } else {
                ViewCompat.setBackground(parent.itemView, null);
                Utils.applyHandlersForViewTree(parent.itemView, null, null);
            }
        }

        public void setPcMode(boolean pcMode) {
            this.pcMode = pcMode;
            notifyItemRangeChanged(0, size());
        }

        public boolean isPcMode() {
            return pcMode;
        }

        public class VH extends FindableViewHolder {
            public VH(View w) {
                super(w);
            }
        }
    }


    class InternalPagerAdapter extends UsefulPagerAdapter {
        public InternalPagerAdapter() {
            super(getSupportFragmentManager());
        }
    }


    public static class PlayersFragment extends SiaBaseFragment {
        RecyclerView lv;
        PlayerNamesListAdapter player;

        @Override
        @ServerInfoParser
        public void onResume() {
            super.onResume();
            try {
                lv = (RecyclerView) getChildClassReturnedView().findViewById(R.id.players);
                lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                lv.setHasFixedSize(false);


                ServerStatus localStat = getParentActivity().localStat;
                ServerPingResult resp = localStat.response;
                if (pref.getBoolean("showPcUserFace", false) & localStat.mode == Protobufs.Server.Mode.PC & canInflateSkinFaceList()) {
                    getParentActivity().skinFaceImages = new ArrayList<>();
                    getParentActivity().sff = new SkinFaceFetcher(new SkinFetcher());
                    player = getParentActivity().new PCUserFaceAdapter();
                    Log.d("ServerInfoActivity", "face on");
                } else {
                    player = getParentActivity().new PlayerNamesListAdapter();
                    Log.d("ServerInfoActivity", "face off");
                }

                player.setHasStableIds(false);

                if (resp instanceof FullStat | resp instanceof SprPair) {
                    FullStat fs = null;
                    if (resp instanceof FullStat)
                        fs = (FullStat) resp;
                    else if (resp instanceof SprPair)
                        fs = (FullStat) ((SprPair) resp).getA();
                    final ArrayList<String> sort = new ArrayList<>(fs.getPlayerList());
                    if (pref.getBoolean("sortPlayerNames", true))
                        Collections.sort(sort);
                    player.clear();
                    player.addAll(sort);
                    player.setPcMode(false);
                } else if (resp instanceof RawJsonReply) {
                    WisecraftJsonObject rep = ((RawJsonReply) resp).json;
                    if (rep.has("players") && rep.get("players").has("sample")) {
                        final ArrayList<String> sort = new ArrayList<>();
                        for (WisecraftJsonObject o : rep.get("players").get("sample")) {
                            sort.add(o.get("name").getAsString());
                            TheApplication.pcUserUUIDs.put(o.get("name").getAsString(), o.get("id").getAsString());
                        }
                        if (pref.getBoolean("sortPlayerNames", true))
                            Collections.sort(sort);
                        player.clear();
                        player.addAll(sort);
                    } else {
                        player.clear();
                    }
                    player.setPcMode(true);
                }

                lv.setAdapter(player);
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.PlayersFragment", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            return inflater.inflate(R.layout.players_tab, container);
        }

        public boolean canInflateSkinFaceList() {
            try {
                LayoutInflater.from(getParentActivity()).inflate(R.layout.simple_list_item_with_image, null, false);
                return true;
            } catch (Throwable e) {
                return false;
            }
        }
    }

    public static class DataFragmentPE extends SiaBaseFragment {
        RecyclerView data;
        KVRecyclerAdapter<String, String> infos;

        @Override
        @ServerInfoParser
        public void onResume() {
            super.onResume();
            try {
                data = (RecyclerView) getChildClassReturnedView().findViewById(R.id.data);
                data.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                data.setHasFixedSize(false);

                infos = new KVRecyclerAdapter<>(getParentActivity());
                infos.setHasStableIds(false);
                data.setAdapter(infos);
                ServerStatus localStat = getParentActivity().localStat;
                ServerPingResult resp = localStat.response;
                if (resp instanceof FullStat | resp instanceof SprPair) {
                    FullStat fs = null;
                    if (resp instanceof FullStat)
                        fs = (FullStat) resp;
                    else if (resp instanceof SprPair)
                        fs = (FullStat) ((SprPair) resp).getA();
                    infos.clear();
                    infos.addAll(fs.getData());
                }
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.DataFragmentPE", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            View v = inflater.inflate(R.layout.data_tab, container);
            ((RecyclerView) v.findViewById(R.id.data)).addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            return v;
        }
    }

    public static class DataFragmentPC extends SiaBaseFragment {
        ImageView serverIcon;
        TextView serverName;
        Drawable serverIconObj;
        Bitmap serverIconBmp;
        RecyclerView data;
        CharSequence serverNameStr;
        KVRecyclerAdapter<String, String> infos;

        @Override
        @ServerInfoParser
        public void onResume() {
            super.onResume();
            try {
                serverIcon = (ImageView) getChildClassReturnedView().findViewById(R.id.serverIcon);
                serverName = (TextView) getChildClassReturnedView().findViewById(R.id.serverTitle);
                data = (RecyclerView) getChildClassReturnedView().findViewById(R.id.data);
                data.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                data.setHasFixedSize(false);


                infos = new KVRecyclerAdapter<>(getParentActivity());
                infos.setHasStableIds(false);
                data.setAdapter(infos);
                ServerStatus localStat = getParentActivity().localStat;
                ServerPingResult resp = localStat.response;
                if (resp instanceof RawJsonReply) {
                    WisecraftJsonObject rep = ((RawJsonReply) resp).json;
                    CharSequence text;
                    text = Utils.parseMinecraftDescriptionJson(rep.get("description"));
                    if (pref.getBoolean("serverListColorFormattedText", false)) {
                        serverNameStr = text;
                    } else {
                        serverNameStr = text.toString();
                    }

                    WisecraftJsonObject players = rep.get("players");
                    WisecraftJsonObject version = rep.get("version");

                    infos.clear();
                    List<Map.Entry<String, String>> data = new ArrayList<>();
                    data.add(new KVP<>(getString(R.string.pc_maxPlayers), players.get("max").getAsInt() + ""));
                    data.add(new KVP<>(getString(R.string.pc_nowPlayers), players.get("online").getAsInt() + ""));
                    data.add(new KVP<>(getString(R.string.pc_softwareVersion), version.get("name").getAsString()));
                    data.add(new KVP<>(getString(R.string.pc_protocolVersion), version.get("protocol").getAsInt() + ""));
                    infos.addAll(data);

                    if (rep.has("favicon")) {
                        byte[] image = WisecraftBase64.decode(rep.get("favicon").getAsString().split("\\,")[1], WisecraftBase64.NO_WRAP);
                        serverIconBmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                        serverIconObj = new BitmapDrawable(getResources(), serverIconBmp);
                    } else {
                        serverIconObj = null;
                    }
                }
                serverName.setTextColor(getParentActivity().slsl.getTextColor());
                serverName.setText(serverNameStr);
                serverIcon.setImageDrawable(serverIconObj);
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.DataFragmentPC", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            View lv = inflater.inflate(R.layout.data_tab_pc, container);
            ViewCompat.setBackground(lv.findViewById(R.id.serverImageAndName), getParentActivity().slsl.load());
            ((RecyclerView) lv.findViewById(R.id.data)).addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            return lv;
        }
    }

    public static class PluginsFragment extends SiaBaseFragment {
        SimpleRecyclerAdapter<String> pluginNames;
        RecyclerView lv;

        @Override
        public void onResume() {
            super.onResume();
            try {
                lv = (RecyclerView) getChildClassReturnedView().findViewById(R.id.players);
                lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                lv.setHasFixedSize(false);


                pluginNames = new SimpleRecyclerAdapter<>(getParentActivity());
                pluginNames.setHasStableIds(false);
                lv.setAdapter(pluginNames);
                ServerStatus localStat = getParentActivity().localStat;
                ServerPingResult resp = localStat.response;
                if (resp instanceof FullStat | resp instanceof SprPair) {
                    FullStat fs = null;
                    if (resp instanceof FullStat)
                        fs = (FullStat) resp;
                    else if (resp instanceof SprPair)
                        fs = (FullStat) ((SprPair) resp).getA();
                    pluginNames.clear();
                    if (fs.getDataAsMap().containsKey("plugins")) {
                        String[] data = fs.getDataAsMap().get("plugins").split("\\: ");
                        if (data.length >= 2) {
                            ArrayList<String> plugins = new ArrayList<>(Arrays.asList(data[1].split("\\; ")));
                            if (pref.getBoolean("sortPluginNames", false))
                                Collections.sort(plugins);
                            pluginNames.addAll(plugins);
                        }
                    }
                }
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.PluginsFragment", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            return inflater.inflate(R.layout.players_tab, container);
        }
    }

    public static class ModsFragment extends SiaBaseFragment {
        String modLoaderTypeName;
        TextView modLoader;
        ListRecyclerViewAdapter<FindableViewHolder, Object> modInfos;
        RecyclerView mods;

        @Override
        @ServerInfoParser
        public void onResume() {
            super.onResume();
            try {
                mods = (RecyclerView) getChildClassReturnedView().findViewById(R.id.players);
                modLoader = (TextView) getChildClassReturnedView().findViewById(R.id.modLoaderType);
                mods.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                mods.setHasFixedSize(false);


                modInfos = getParentActivity().new ModInfoListAdapter();
                modInfos.setHasStableIds(false);
                mods.setAdapter(modInfos);
                ServerStatus localStat = getParentActivity().localStat;
                ServerPingResult resp = localStat.response;
                if (resp instanceof RawJsonReply) {
                    WisecraftJsonObject rep = ((RawJsonReply) resp).json;
                    if (rep.has("modinfo")) {
                        WisecraftJsonObject modInfo = rep.get("modinfo");
                        modInfos.addAll(Utils.iterableToCollection(modInfo.get("modList")));
                        modLoaderTypeName = modInfo.get("type").getAsString();
                    }
                }
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.ModsFragment", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            return inflater.inflate(R.layout.mods_tab, container);
        }
    }

    public static class UcpInfoFragment extends SiaBaseFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            return inflater.inflate(R.layout.server_info_no_details_fragment, container);
        }
    }

    public static class UcpDetailsFragment extends SiaBaseFragment {

        @Override
        public void onResume() {
            super.onResume();
            try {
                UnconnectedPing.UnconnectedPingResult result;
                if (getParentActivity().localStat.response instanceof UnconnectedPing.UnconnectedPingResult) {
                    result = (UnconnectedPing.UnconnectedPingResult) getParentActivity().localStat.response;
                } else {
                    result = (UnconnectedPing.UnconnectedPingResult) ((SprPair) getParentActivity().localStat.response).getB();
                }
                RecyclerView lv = (RecyclerView) getChildClassReturnedView().findViewById(R.id.data);
                lv.setLayoutManager(new HPLinearLayoutManager(getActivity()));
                lv.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                lv.setHasFixedSize(false);

                KVRecyclerAdapter<String, String> adap = new KVRecyclerAdapter<>(getActivity());
                adap.setHasStableIds(false);
                lv.setAdapter(adap);
                List<Map.Entry<String, String>> otm = new ArrayList<>();
                String[] values = result.getRaw().split("\\;");
                otm.add(new KVP<>(getString(R.string.ucp_serverName), values[1]));
                otm.add(new KVP<>(getString(R.string.ucp_protocolVersion), values[2]));
                otm.add(new KVP<>(getString(R.string.ucp_mcpeVersion), values[3]));
                otm.add(new KVP<>(getString(R.string.ucp_nowPlayers), values[4]));
                otm.add(new KVP<>(getString(R.string.ucp_maxPlayers), values[5]));
                adap.addAll(otm);
            } catch (Throwable e) {
                WisecraftError.report("ServerInfoActivity.UcpDetailsFragment", e);
                indicateError();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container) {
            View v = inflater.inflate(R.layout.server_info_ucp_details, container);
            return v;
        }
    }

    static abstract class SiaBaseFragment extends BaseFragment<ServerInfoActivity> {
        private View childClassReturnedView, base;

        @Override
        public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            base = inflater.inflate(R.layout.server_info_fragments_base, container, false);
            ViewGroup childBase = (ViewGroup) base.findViewById(R.id.childBase);
            childClassReturnedView = onCreateView(inflater, childBase);
            if (childBase.getChildCount() == 0) {
                childBase.addView(childClassReturnedView);
            }
            return base;
        }

        public abstract View onCreateView(LayoutInflater inflater, ViewGroup container);

        public View getChildClassReturnedView() {
            return childClassReturnedView;
        }

        public void indicateError() {
            base.findViewById(R.id.childBase).setVisibility(View.GONE);
            base.findViewById(R.id.error).setVisibility(View.VISIBLE);
        }
    }


    class CallbackBase extends ViewPagerBottomSheetBehavior.BottomSheetCallback {
        @Override
        public void onStateChanged(View bottomSheet, int newState) {
        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
        }
    }

    class UpdateCallback extends CallbackBase {
        @Override
        public void onStateChanged(View bottomSheet, int newState) {
            switch (newState) {
                case ViewPagerBottomSheetBehavior.STATE_DRAGGING:
                    if (!behavior.getAllowUserDragging()) {
                        behavior.setState(ViewPagerBottomSheetBehavior.STATE_EXPANDED);
                        break;
                    }
                case ViewPagerBottomSheetBehavior.STATE_SETTLING:
                case ViewPagerBottomSheetBehavior.STATE_COLLAPSED:
                    pin.hide();
                    break;
                case ViewPagerBottomSheetBehavior.STATE_EXPANDED:
                    pin.show();
                    break;
                case ViewPagerBottomSheetBehavior.STATE_HIDDEN:
                    finish();
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class ColorUpdateCallback extends UpdateCallback {
        int r, g, b;

        public ColorUpdateCallback() {
            int color = slsl.getBackgroundSimpleColor();
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
            BigDecimal val = slideOffset < 0 ? BigDecimal.ZERO : new BigDecimal(slideOffset);
            ViewCompat.setAlpha(background, val.floatValue());
            int alpha = val.multiply(new BigDecimal(255)).intValue();
            int status = Color.argb(alpha, r, g, b);
            getWindow().setStatusBarColor(status);
        }
    }


    static {
        int base = 0xff3a2a1d;
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(base), Color.green(base), Color.blue(base), hsv);
        float v = hsv[2];
        hsv[2] = v + 0.25f;//V+20
        DIRT_BRIGHT = Color.HSVToColor(hsv);
        hsv[2] = v - 0.05f;//V-10
        DIRT_DARK = Color.HSVToColor(hsv);

        Log.d("DirtBright", Integer.toHexString(DIRT_BRIGHT));
        Log.d("DirtDark", Integer.toHexString(DIRT_DARK));
    }

    public static int translucent(int palePrimary) {
        int r = Color.red(palePrimary);
        int g = Color.green(palePrimary);
        int b = Color.blue(palePrimary);
        int a = new BigDecimal(0xff).multiply(new BigDecimal("0.3")).intValue();

        return Color.argb(a, r, g, b);
    }

    public static int darker(int base) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(base), Color.green(base), Color.blue(base), hsv);
        hsv[2] -= 0.20f;
        return Color.HSVToColor(hsv);
    }
}

public class ServerInfoActivity extends ServerInfoActivityImpl {

}
