package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.contextwrappers.extender.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pc.*;
import com.nao20010128nao.Wisecraft.misc.ping.methods.pe.*;
import com.nao20010128nao.Wisecraft.services.*;

import java.lang.ref.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.misc.Utils.*;

@ShowsServerList
abstract class ServerFinderActivityImpl extends AppCompatActivity implements ServerListActivityInterface {
    ServerList sl;
    List<ServerStatus> list;
    String ip;
    Protobufs.Server.Mode mode;
    View dialog, dialog2;
    SharedPreferences pref;
    RecyclerView rv;
    ServerListStyleLoader slsl;
    ServiceConnection lastConnection;
    String tag;
    ServerFinderService.InternalBinder bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_content);
        sl = new ServerList(this);
        rv = findViewById(android.R.id.list);
        switch (pref.getInt("serverListStyle2", 0)) {
            case 0:
            default:
                rv.setLayoutManager(new LinearLayoutManager(this));
                break;
            case 1:
                GridLayoutManager glm = new GridLayoutManager(this, calculateRows(this));
                rv.setLayoutManager(glm);
                break;
            case 2:
                StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(calculateRows(this), StaggeredGridLayoutManager.VERTICAL);
                rv.setLayoutManager(sglm);
                break;
        }
        rv.setAdapter(sl);

        if (getIntent().hasExtra("tag")) {
            tag = getIntent().getStringExtra("tag");
            launchService();
        } else {
            ip = getIntent().getStringExtra("ip");
            mode = (Protobufs.Server.Mode) getIntent().getSerializableExtra("mode");
            new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
                .setTitle(R.string.serverFinder)
                .setView(dialog = getLayoutInflater().inflate(R.layout.server_finder_start, null, false))
                .setPositiveButton(android.R.string.ok, (di, w) -> {
                    String ip = ((EditText) dialog.findViewById(R.id.ip)).getText().toString();
                    int startPort = Integer.valueOf(((EditText) dialog.findViewById(R.id.startPort)).getText().toString());
                    int endPort = Integer.valueOf(((EditText) dialog.findViewById(R.id.endPort)).getText().toString());
                    Protobufs.Server.Mode mode = ((CheckBox) dialog.findViewById(R.id.pc)).isChecked() ? Protobufs.Server.Mode.PC : Protobufs.Server.Mode.PE;
                    launchService(ip, Math.min(startPort, endPort), Math.max(startPort, endPort), mode);
                })
                .setNegativeButton(android.R.string.cancel, (di, w) -> finish())
                .setOnCancelListener(di -> finish())
                .show();
            if (ip != null) ((EditText) dialog.findViewById(R.id.ip)).setText(ip);
            ((CheckBox) dialog.findViewById(R.id.pc)).setChecked(mode != Protobufs.Server.Mode.PE);
        }
        slsl = (ServerListStyleLoader) getSystemService(ContextWrappingExtender.SERVER_LIST_STYLE_LOADER);

        ViewCompat.setBackground(findViewById(android.R.id.content), slsl.load());
    }

    private void launchService(final String ip, final int startPort, final int endPort, final Protobufs.Server.Mode mode) {
        bindService(new Intent(this, ServerFinderService.class), new ServiceConnection() {
            public void onServiceConnected(android.content.ComponentName p1, android.os.IBinder p2) {
                lastConnection = this;
                tag = (bound = (ServerFinderService.InternalBinder) p2).startExploration(ip, mode, startPort, endPort);
                startIntervalUpdate();
            }

            public void onServiceDisconnected(android.content.ComponentName p1) {
                finish();
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void launchService() {
        bindService(new Intent(this, ServerFinderService.class), new ServiceConnection() {
            public void onServiceConnected(android.content.ComponentName p1, android.os.IBinder p2) {
                lastConnection = this;
                bound = (ServerFinderService.InternalBinder) p2;
                startIntervalUpdate();
            }

            public void onServiceDisconnected(android.content.ComponentName p1) {
                finish();
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void startIntervalUpdate() {
        ServerFinderService.State state = bound.getState(tag);
        setTitle(state.ip + ":(" + state.start + "~" + state.end + ")");
        state.activityClosed = false;
        updateList();
    }

    private void updateList() {
        ServerFinderService.State state = bound.getState(tag);
        if (state.finished) return;
        new Handler().postDelayed(this::updateList, 1000);
        sl.clear();
        sl.addAll(state.detected.values());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lastConnection != null) {
            ServerFinderService.State state = bound.getState(tag);
            state.activityClosed = true;
            ServerFinderService.checkDead(tag);
            unbindService(lastConnection);
        }
    }

    @Override
    public void addIntoList(Server s) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ServerList extends ListRecyclerViewAdapter<ServerStatusWrapperViewHolder, ServerStatus> implements AdapterView.OnItemClickListener {
        ServerFinderActivityImpl sta;

        public ServerList(ServerFinderActivityImpl parent) {
            super(parent.list = new ArrayList<>());
            sta = parent;
        }

        @Override
        @ServerInfoParser
        public void onBindViewHolder(ServerStatusWrapperViewHolder viewHolder, final int offset) {
            sta.slsl.applyTextColorTo(viewHolder);
            ServerStatus s = getItem(offset);

            final CharSequence title;
            if (s.response instanceof RawJsonReply) {//PC (Obfuscated)
                RawJsonReply rep = (RawJsonReply) s.response;
                if (!rep.json.has("description")) {
                    title = s.toString();
                } else {
                    title = Utils.parseMinecraftDescriptionJson(rep.json.get("description"));
                }
            } else if (s.response instanceof UnconnectedPing.UnconnectedPingResult) {
                title = ((UnconnectedPing.UnconnectedPingResult) s.response).getServerName();
            } else {//Unreachable
                title = s.toString();
            }
            if (sta.pref.getBoolean("serverListColorFormattedText", false)) {
                if (title instanceof String) {
                    viewHolder.setServerName(parseMinecraftFormattingCode(title.toString()));
                } else {
                    viewHolder.setServerName(title);
                }
            } else {
                if (title instanceof String) {
                    viewHolder.setServerName(deleteDecorations(title.toString()));
                } else {
                    viewHolder.setServerName(title.toString());
                }
            }
            viewHolder
                .setStatColor(ContextCompat.getColor(sta, R.color.stat_ok))
                .hideServerPlayers()
                .setTag(s)
                .setPingMillis(s.ping)
                .setServer(s)
                .setServerAddress(s.port + "")
                .hideServerTitle();
            applyHandlersForViewTree(viewHolder.itemView,
                v -> {
                    onItemClick(null, v, offset, Long.MIN_VALUE);
                }
            );
        }

        @Override
        public ServerStatusWrapperViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            switch (sta.pref.getInt("serverListStyle2", 0)) {
                case 0:
                default:
                    return new ServerStatusWrapperViewHolder(sta, false, parent);
                case 1:
                case 2:
                    return new ServerStatusWrapperViewHolder(sta, true, parent);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Server s = getItem(position);
            if (s != null) {
                new AlertDialog.Builder(ServerFinderActivityImpl.this, ThemePatcher.getDefaultDialogStyle(ServerFinderActivityImpl.this))
                    .setTitle(s.toString())
                    .setItems(R.array.serverFinderMenu, (di, w) -> {
                        switch (w) {
                            case 0:
                                if (!ServerListActivityImpl.instance.get().list.contains(s)) {
                                    ServerListActivityImpl.instance.get().sl.add(s.cloneAsServer());
                                    ServerListActivityImpl.instance.get().dryUpdate(s, true);
                                }
                                break;
                        }
                    })
                    .show();
            }
        }
    }
}

public class ServerFinderActivity extends ServerFinderActivityImpl {
    public static WeakReference<ServerFinderActivity> instance = new WeakReference(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = new WeakReference(this);
        super.onCreate(savedInstanceState);
    }
}
