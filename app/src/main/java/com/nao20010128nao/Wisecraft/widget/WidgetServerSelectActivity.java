package com.nao20010128nao.Wisecraft.widget;

import android.appwidget.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.google.gson.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.serverList.*;

import java.util.*;

abstract class WidgetServerSelectActivityImpl extends AppCompatActivity {
    RecyclerView rv;
    Adapter a;
    Gson gson = new Gson();
    SharedPreferences pref, widgetPref;
    int wid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_content);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rv = (RecyclerView) findViewById(android.R.id.list);
        Intent values = getIntent();
        if (values.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            wid = values.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        } else if (values.hasExtra("wid")) {
            wid = values.getIntExtra("wid", 0);
        } else {
            finish();
            return;
        }
        widgetPref = getSharedPreferences("widgets", Context.MODE_PRIVATE);
        //We won't apply any style here, because this is only to select.
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(a = new Adapter());
        Server[] servers = gson.fromJson(pref.getString("servers", "[]"), Server[].class);
        a.addAll(servers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, R.string.typeServer);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                View dialog = getLayoutInflater().inflate(R.layout.server_add_dialog_new, null);
                final LinearLayout peFrame = (LinearLayout) dialog.findViewById(R.id.pe);
                final LinearLayout pcFrame = (LinearLayout) dialog.findViewById(R.id.pc);
                final EditText pe_ip = (EditText) dialog.findViewById(R.id.pe).findViewById(R.id.serverIp);
                final EditText pe_port = (EditText) dialog.findViewById(R.id.pe).findViewById(R.id.serverPort);
                final EditText pc_ip = (EditText) dialog.findViewById(R.id.pc).findViewById(R.id.serverIp);
                final CheckBox split = (CheckBox) dialog.findViewById(R.id.switchFirm);
                final EditText serverName = (EditText) dialog.findViewById(R.id.serverName);
                serverName.setVisibility(View.GONE);

                pe_ip.setText("localhost");
                pe_port.setText("19132");
                split.setChecked(false);

                split.setOnClickListener(v -> {
                    if (split.isChecked()) {
                        //PE->PC
                        peFrame.setVisibility(View.GONE);
                        pcFrame.setVisibility(View.VISIBLE);
                        split.setText(R.string.pc);
                        StringBuilder result = new StringBuilder();
                        result.append(pe_ip.getText());
                        int port = Integer.valueOf(pe_port.getText().toString()).intValue();
                        if (!(port == 25565 | port == 19132))
                            result.append(':').append(pe_port.getText());
                        pc_ip.setText(result);
                    } else {
                        //PC->PE
                        pcFrame.setVisibility(View.GONE);
                        peFrame.setVisibility(View.VISIBLE);
                        split.setText(R.string.pe);
                        Server s = Utils.convertServerObject(Arrays.asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
                        pe_ip.setText(s.ip);
                        pe_port.setText(s.port + "");
                    }
                });

                new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this)).
                        setView(dialog).
                        setPositiveButton(android.R.string.yes, (d, sel) -> {
                            Server s;
                            if (split.isChecked()) {
                                s = Utils.convertServerObject(Arrays.asList(MslServer.makeServerFromString(pc_ip.getText().toString(), false))).get(0);
                            } else {
                                s = new Server();
                                s.ip = pe_ip.getText().toString();
                                s.port = Integer.valueOf(pe_port.getText().toString());
                                s.mode = Protobufs.Server.Mode.PE;
                            }

                            widgetPref.edit().putString(wid + "", gson.toJson(s)).commit();
                            sendBroadcast(new Intent(WidgetServerSelectActivityImpl.this, PingWidget.PingHandler.class).putExtra("wid", wid));
                            setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wid));
                            finish();
                        }).
                        setNegativeButton(android.R.string.no, (d, sel) -> {

                        }).
                        show();
                return true;
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        TheApplication.instance.initForActivities();
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    class Adapter extends ListRecyclerViewAdapter<FindableViewHolder, Server> {

        @Override
        public void onBindViewHolder(FindableViewHolder parent, int offset) {
            ((TextView) parent.findViewById(android.R.id.text1)).setText(makeServerTitle(getItem(offset)));
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
            parent.itemView.setBackground(ta.getDrawable(0));
            ta.recycle();
            parent.itemView.setTag(getItem(offset));
            Utils.applyHandlersForViewTree(parent.itemView, new OnClickListener(offset));
        }

        @Override
        public FindableViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            return new FindableViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        String makeServerTitle(Server sv) {
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(sv.name) || sv.toString().equals(sv.name)) {
                sb.append(sv).append(" ");
            } else {
                sb.append(sv.name).append(" (").append(sv).append(") ");
            }
            sb.append(sv.mode == Protobufs.Server.Mode.PE ? "PE" : "PC");
            return sb.toString();
        }

        class OnClickListener implements View.OnClickListener {
            int ofs;

            public OnClickListener(int i) {
                ofs = i;
            }

            @Override
            public void onClick(View p1) {
                Server s = getItem(ofs).cloneAsServer();
                s.name = null;
                widgetPref.edit().putString(wid + "", gson.toJson(s)).commit();
                PingWidget.setWidgetData(WidgetServerSelectActivityImpl.this, wid, newWidgetDataInstance());
                sendBroadcast(new Intent(WidgetServerSelectActivityImpl.this, PingWidget.PingHandler.class).putExtra("wid", wid));
                setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, wid));
                finish();
            }
        }
    }

    public abstract PingWidget.WidgetData newWidgetDataInstance();


}

public abstract class WidgetServerSelectActivity extends WidgetServerSelectActivityImpl {
    public static class Type1 extends WidgetServerSelectActivity {

        @Override
        public PingWidget.WidgetData newWidgetDataInstance() {
            PingWidget.WidgetData wd = new PingWidget.WidgetData();
            wd.style = 0;
            return wd;
        }
    }

    public static class Type2 extends WidgetServerSelectActivity {

        @Override
        public PingWidget.WidgetData newWidgetDataInstance() {
            PingWidget.WidgetData data = new PingWidget.WidgetData();
            data.style = 1;
            return data;
        }
    }

    public static class Type3 extends WidgetServerSelectActivity {

        @Override
        public PingWidget.WidgetData newWidgetDataInstance() {
            PingWidget.WidgetData data = new PingWidget.WidgetData();
            data.style = 2;
            return data;
        }
    }
}

