package com.nao20010128nao.Wisecraft.activity;

import android.annotation.*;
import android.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;

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
        rv = (RecyclerView) findViewById(R.id.list);
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
                editDialog(null,entry->{
                    scm.schedule(entry);
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
    private void editDialog(final Protobufs.ServerCrawlerEntry entry, Predicate<Protobufs.ServerCrawlerEntry> handler){
        final AlertDialog dialog=new AlertDialog.Builder(this)
                .setView(R.layout.server_crawler_edit)
                .setPositiveButton(android.R.string.ok,(di,w)->{
                })
                .setNegativeButton(android.R.string.cancel,(di,w)->{})
                .setCancelable(false)
                .create();

        final ReferencedObject<Protobufs.ServerCrawlerEntry> editableEntry=new ReferencedObject<>();

        final EditText name= (EditText) dialog.findViewById(R.id.name);
        final Button date= (Button) dialog.findViewById(R.id.startDate);
        final Button time= (Button) dialog.findViewById(R.id.startTime);
        final EditText interval= (EditText) dialog.findViewById(R.id.intervalText);
        final Switch enabledState= (Switch) dialog.findViewById(R.id.enabledSwitch);
        final Switch online= (Switch) dialog.findViewById(R.id.online);
        final Switch offline= (Switch) dialog.findViewById(R.id.offline);

        dialog.setOnShowListener(d->{
            Button btn=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(v->{
                if(TextUtils.isEmpty(interval.getText())){
                    interval.setError(getResources().getString(R.string.cannotBeEmpty));
                }else{
                    editableEntry.set(/* set values except for date & time */
                            editableEntry.get().toBuilder()
                                    .setName(name.getText().toString())
                                    .setInterval(Long.valueOf(interval.getText().toString()))
                                    .setEnabled(enabledState.isChecked())
                                    .setNotifyOnline(online.isChecked())
                                    .setNotifyOffline(offline.isChecked())
                                    .build()
                    );
                    if(handler.process(editableEntry.get()))
                        d.dismiss();
                }
            });
        });

        /*
         * special case: set date and time separately,
         * because they have special way to set values
         * */
        date.setOnClickListener(v->{
            /* why is this requires 24 on my IDE!? actual: API 1  */
            DatePickerDialog dpg=new DatePickerDialog(this);
            final Calendar calendar=Utils.toDateTime(entry.getStart());
            dpg.getDatePicker().updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpg.setCancelable(false);
            dpg.setOnDateSetListener((vv,y,m,d)->{
                calendar.set(y,m,d);
                editableEntry.set(/* set values */
                        editableEntry.get().toBuilder()
                                .setStart(calendar.getTimeInMillis())
                                .build()
                );
                date.setText(Utils.formatDatePart(editableEntry.get().getStart()));
            });
            dpg.show();
        });
        time.setOnClickListener(v->{
            final Calendar calendar=Utils.toDateTime(entry.getStart());
            TimePickerDialog tpd=new TimePickerDialog(
                    this,
                    (vv,hod,m)->{
                        calendar.set(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                hod,
                                m,
                                /*calendar.get(Calendar.SECOND)*/0
                        );
                        editableEntry.set(/* set values */
                                editableEntry.get().toBuilder()
                                        .setStart(calendar.getTimeInMillis())
                                        .build()
                        );
                        time.setText(Utils.formatTimePart(editableEntry.get().getStart()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            tpd.show();
        });

        if(entry!=null){
            editableEntry.set(entry);

            name.setText(editableEntry.get().getName());
            interval.setText(String.valueOf(editableEntry.get().getInterval()));
            enabledState.setChecked(editableEntry.get().getEnabled());
            online.setChecked(editableEntry.get().getNotifyOnline());
            offline.setChecked(editableEntry.get().getNotifyOffline());
            date.setText(Utils.formatDatePart(editableEntry.get().getStart()));
            time.setText(Utils.formatTimePart(editableEntry.get().getStart()));
        }else{
            long start=Utils.cutSecondAndMillis(System.currentTimeMillis());
            editableEntry.set(/* initial values */
                    Protobufs.ServerCrawlerEntry.newBuilder()
                            .setEnabled(true)
                            .setNotifyOnline(true)
                            .setNotifyOffline(true)
                            .setStart(start)
                            .build()
            );
            enabledState.setChecked(true);
            online.setChecked(true);
            offline.setChecked(true);
            date.setText(Utils.formatDatePart(start));
            time.setText(Utils.formatTimePart(start));
        }

        dialog.show();
    }

    class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.server_crawler_entry, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            final Protobufs.ServerCrawlerEntry entry = scm.getEntries().get(position);
            holder.itemView.setTag(R.id.id, entry.getId());
            holder.name.setText(entry.getName());
            holder.start.setText(Utils.formatDate(entry.getStart()));
            holder.interval.setText(Utils.formatTimeSpan(entry.getInterval()));
            holder.enabled.setText(entry.getEnabled() ? R.string.yesWord : R.string.noWord);
            holder.online.setText(entry.getNotifyOnline() ? R.string.yesWord : R.string.noWord);
            holder.offline.setText(entry.getNotifyOffline() ? R.string.yesWord : R.string.noWord);
            holder.edit.setOnClickListener(v->{
                editDialog(entry,ent->{
                	return true;
                });
            });
        }

        @Override
        public int getItemCount() {
            return scm.getEntries().size();
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
