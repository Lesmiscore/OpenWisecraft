package com.nao20010128nao.Wisecraft.activity;

import android.annotation.*;
import android.app.*;
import android.content.Context;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;

import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.remoteServerList.MslServer;

import java.lang.ref.*;
import java.text.*;
import java.util.*;

public class ServerCrawlerConfigActivity extends AppCompatActivity {
    RecyclerView rv;
    ServerCrawlerManager scm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_crawler_config);
        scm = new ServerCrawlerManager(this);
        rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(new LocalAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.addSingle);
        menu.add(0, 1, 1, R.string.reschedule);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:/*Add function*/
                editDialog(null, entry -> {
                    scm.schedule(entry);
                    scm.commit();
                    return true;
                });
                break;
            case 1:
                scm.reschedule();
                return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void editDialog(final Protobufs.ServerCrawlerEntry entry, Predicate<Protobufs.ServerCrawlerEntry> handler) {
        final AlertDialog dialog = new AlertDialog.Builder(this, ThemePatcher.getDefaultDialogStyle(this))
            .setView(R.layout.server_crawler_edit)
            .setPositiveButton(android.R.string.ok, (di, w) -> {
            })
            .setNegativeButton(android.R.string.cancel, (di, w) -> {
            })
            .setCancelable(false)
            .show();

        final ReferencedObject<Protobufs.ServerCrawlerEntry.Builder> editableEntry = new ReferencedObject<>();
        //editableEntry.set(Protobufs.ServerCrawlerEntry.newBuilder());

        final ReferencedObject<EditText> name = new ReferencedObject<>();//dialog.findViewById(R.id.name);
        final ReferencedObject<Button> date = new ReferencedObject<>();//dialog.findViewById(R.id.startDate);
        final ReferencedObject<Button> time = new ReferencedObject<>();//dialog.findViewById(R.id.startTime);
        final ReferencedObject<EditText> interval = new ReferencedObject<>();//dialog.findViewById(R.id.intervalText);
        final ReferencedObject<Switch> enabledState = new ReferencedObject<>();//dialog.findViewById(R.id.enabledSwitch);
        final ReferencedObject<Switch> online = new ReferencedObject<>();//dialog.findViewById(R.id.online);
        final ReferencedObject<Switch> offline = new ReferencedObject<>();//dialog.findViewById(R.id.offline);

        final ReferencedObject<LinearLayout> peFrame = new ReferencedObject<>();
        final ReferencedObject<LinearLayout> pcFrame = new ReferencedObject<>();
        final ReferencedObject<EditText> pe_ip = new ReferencedObject<>();
        final ReferencedObject<EditText> pe_port = new ReferencedObject<>();
        final ReferencedObject<EditText> pc_ip = new ReferencedObject<>();
        final ReferencedObject<CheckBox> split = new ReferencedObject<>();

        Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(interval.checked().getText())) {
                interval.checked().setError(getResources().getString(R.string.cannotBeEmpty));
            } else {
                editableEntry.set(/* set values except for date & time */
                    editableEntry.checked()
                        .setName(name.checked().getText().toString())
                        .setInterval(Long.valueOf(interval.checked().getText().toString()))
                        .setEnabled(enabledState.checked().isChecked())
                        .setNotifyOnline(online.checked().isChecked())
                        .setNotifyOffline(offline.checked().isChecked())
                        .setId(0)
                );
                Server s;
                if (split.checked().isChecked()) {
                    s = Utils.convertServerObject(Collections.singletonList(MslServer.makeServerFromString(pc_ip.checked().getText().toString(), false))).get(0);
                } else {
                    s = new Server();
                    s.ip = pe_ip.checked().getText().toString();
                    s.port = Integer.valueOf(pe_port.checked().getText().toString());
                    s.mode = split.checked().isChecked() ? Protobufs.Server.Mode.PC : Protobufs.Server.Mode.PE;
                }
                s.name = TextUtils.isEmpty(name.checked().getText()) ? "" : name.checked().getText().toString();

                editableEntry.set(
                    editableEntry.checked().setServer(s.toProtobufServer())
                );

                if (handler.process(editableEntry.checked().build()))
                    dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.serverName).setVisibility(View.GONE);

        name.set(dialog.findViewById(R.id.name));
        date.set(dialog.findViewById(R.id.startDate));
        time.set(dialog.findViewById(R.id.startTime));
        interval.set(dialog.findViewById(R.id.intervalText));
        enabledState.set(dialog.findViewById(R.id.enabledSwitch));
        online.set(dialog.findViewById(R.id.online));
        offline.set(dialog.findViewById(R.id.offline));

        peFrame.set(dialog.findViewById(R.id.pe));
        pcFrame.set(dialog.findViewById(R.id.pc));
        pe_ip.set(dialog.findViewById(R.id.pe).findViewById(R.id.serverIp));
        pe_port.set(dialog.findViewById(R.id.pe).findViewById(R.id.serverPort));
        pc_ip.set(dialog.findViewById(R.id.pc).findViewById(R.id.serverIp));
        split.set(dialog.findViewById(R.id.switchFirm));

             /*
              * special case: set date and time separately,
              * because they have special way to set values
              * */
        date.checked().setOnClickListener(v -> {
                /* why is this requires 24 on my IDE!? actual: API 1  */
            DatePickerDialog dpg = new DatePickerDialog(this);
            final Calendar calendar = Utils.toDateTime(editableEntry.checked().getStart());
            dpg.getDatePicker().updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpg.setCancelable(false);
            dpg.setOnDateSetListener((vv, y, m, d) -> {
                calendar.set(y, m, d);
                editableEntry.set(/* set values */
                    editableEntry.checked()
                        .setStart(calendar.getTimeInMillis())
                );
                date.checked().setText(Utils.formatDatePart(editableEntry.checked().getStart()));
            });
            dpg.show();
        });
        time.checked().setOnClickListener(v -> {
            final Calendar calendar = Utils.toDateTime(editableEntry.checked().getStart());
            TimePickerDialog tpd = new TimePickerDialog(
                this,
                (vv, hod, m) -> {
                    calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hod,
                        m,
                                /*calendar.get(Calendar.SECOND)*/0
                    );
                    editableEntry.set(/* set values */
                        editableEntry.checked()
                            .setStart(calendar.getTimeInMillis())
                    );
                    time.checked().setText(Utils.formatTimePart(editableEntry.checked().getStart()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            tpd.show();
        });

        if (entry != null) {
            editableEntry.set(entry.toBuilder());

            name.checked().setText(editableEntry.checked().getName());
            interval.checked().setText(String.valueOf(editableEntry.checked().getInterval()));
            enabledState.checked().setChecked(editableEntry.checked().getEnabled());
            online.checked().setChecked(editableEntry.checked().getNotifyOnline());
            offline.checked().setChecked(editableEntry.checked().getNotifyOffline());
            date.checked().setText(Utils.formatDatePart(editableEntry.checked().getStart()));
            time.checked().setText(Utils.formatTimePart(editableEntry.checked().getStart()));
        } else {
            long start = Utils.cutSecondAndMillis(System.currentTimeMillis());
            editableEntry.set(/* initial values */
                Protobufs.ServerCrawlerEntry.newBuilder()
                    .setEnabled(true)
                    .setNotifyOnline(true)
                    .setNotifyOffline(true)
                    .setStart(start)
            );
            enabledState.checked().setChecked(true);
            online.checked().setChecked(true);
            offline.checked().setChecked(true);
            date.checked().setText(Utils.formatDatePart(start));
            time.checked().setText(Utils.formatTimePart(start));
        }


        if(editableEntry.checked().hasServer()){
            if(editableEntry.checked().getServer().getMode()== Protobufs.Server.Mode.PE){
                pe_ip.checked().setText(editableEntry.checked().getServer().getIp());
                pe_port.checked().setText(String.valueOf(editableEntry.checked().getServer().getPort()));
                split.checked().setChecked(false);
            }else{
                if(editableEntry.checked().getServer().getPort()==25565){
                    pc_ip.checked().setText(editableEntry.checked().getServer().getIp());
                }else{
                    pc_ip.checked().setText(editableEntry.checked().getServer().getIp()+":"+editableEntry.checked().getServer().getPort());
                }
                split.checked().setChecked(true);
            }
        }else{
            pe_ip.checked().setText("localhost");
            pe_port.checked().setText("19132");
            split.checked().setChecked(false);
        }

        split.checked().setOnClickListener(v -> {
            if (split.checked().isChecked()) {
                //PE->PC
                peFrame.checked().setVisibility(View.GONE);
                pcFrame.checked().setVisibility(View.VISIBLE);
                split.checked().setText(R.string.pc);
                StringBuilder result = new StringBuilder();
                result.append(pe_ip.checked().getText());
                int port = Integer.valueOf(pe_port.checked().getText().toString());
                if (!(port == 25565 | port == 19132))
                    result.append(':').append(pe_port.checked().getText());
                pc_ip.checked().setText(result);
            } else {
                //PC->PE
                pcFrame.checked().setVisibility(View.GONE);
                peFrame.checked().setVisibility(View.VISIBLE);
                split.checked().setText(R.string.pe);
                Server s = Utils.convertServerObject(Arrays.asList(MslServer.makeServerFromString(pc_ip.checked().getText().toString(), false))).get(0);
                pe_ip.checked().setText(s.ip);
                pe_port.checked().setText(String.valueOf(s.port));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.VH> {
        List<Protobufs.ServerCrawlerEntry> listBasedOn;

        LocalAdapter(){
            updateList();
        }

        void updateList(){
            boolean wasListNonNull=listBasedOn!=null;
            listBasedOn=new ArrayList<>(scm.getEntries());
            if(wasListNonNull)
                notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.server_crawler_entry, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            final Protobufs.ServerCrawlerEntry entry = listBasedOn.get(position);
            holder.itemView.setTag(R.id.id, entry.getId());
            holder.name.setText(entry.getName());
            holder.start.setText(Utils.formatDate(entry.getStart()));
            holder.interval.setText(Utils.formatTimeSpan(entry.getInterval()));
            holder.enabled.setText(entry.getEnabled() ? R.string.yesWord : R.string.noWord);
            holder.online.setText(entry.getNotifyOnline() ? R.string.yesWord : R.string.noWord);
            holder.offline.setText(entry.getNotifyOffline() ? R.string.yesWord : R.string.noWord);
            holder.edit.setOnClickListener(v -> editDialog(entry, ent -> {
                scm.setEntry(entry.getId(), ent);
                scm.reschedule();
                scm.commit();
                updateList();
                return true;
            }));
        }

        @Override
        public int getItemCount() {
            return listBasedOn.size();
        }

        class VH extends FindableViewHolder {
            TextView name, interval, start, enabled, online, offline;
            ImageButton edit;

            public VH(View v) {
                super(v);
                name = (TextView) findViewById(R.id.name);
                interval = (TextView) findViewById(R.id.intervalText);
                start = (TextView) findViewById(R.id.startTimeText);
                enabled = (TextView) findViewById(R.id.enabledText);
                online = (TextView) findViewById(R.id.online);
                offline = (TextView) findViewById(R.id.offline);
                edit = (ImageButton) findViewById(R.id.edit);
            }
        }
    }
}
