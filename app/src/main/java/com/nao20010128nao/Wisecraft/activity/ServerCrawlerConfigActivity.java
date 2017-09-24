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
        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(R.layout.server_crawler_edit)
            .setPositiveButton(android.R.string.ok, (di, w) -> {
            })
            .setNegativeButton(android.R.string.cancel, (di, w) -> {
            })
            .setCancelable(false)
            .create();

        final ReferencedObject<Protobufs.ServerCrawlerEntry.Builder> editableEntry = new ReferencedObject<>();

        final ReferencedObject<EditText> name = new ReferencedObject<>();//dialog.findViewById(R.id.name);
        final ReferencedObject<Button> date = new ReferencedObject<>();//dialog.findViewById(R.id.startDate);
        final ReferencedObject<Button> time = new ReferencedObject<>();//dialog.findViewById(R.id.startTime);
        final ReferencedObject<EditText> interval = new ReferencedObject<>();//dialog.findViewById(R.id.intervalText);
        final ReferencedObject<Switch> enabledState = new ReferencedObject<>();//dialog.findViewById(R.id.enabledSwitch);
        final ReferencedObject<Switch> online = new ReferencedObject<>();//dialog.findViewById(R.id.online);
        final ReferencedObject<Switch> offline = new ReferencedObject<>();//dialog.findViewById(R.id.offline);

        dialog.setOnShowListener(naaaa -> {
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
                    );
                    if (handler.process(editableEntry.checked().build()))
                        naaaa.dismiss();
                }
            });

            name.set(dialog.findViewById(R.id.name));
            date.set(dialog.findViewById(R.id.startDate));
            time.set(dialog.findViewById(R.id.startTime));
            interval.set(dialog.findViewById(R.id.intervalText));
            enabledState.set(dialog.findViewById(R.id.enabledSwitch));
            online.set(dialog.findViewById(R.id.online));
            offline.set(dialog.findViewById(R.id.offline));

             /*
              * special case: set date and time separately,
              * because they have special way to set values
              * */
            date.checked().setOnClickListener(v -> {
                /* why is this requires 24 on my IDE!? actual: API 1  */
                DatePickerDialog dpg = new DatePickerDialog(this);
                final Calendar calendar = Utils.toDateTime(entry.getStart());
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
                final Calendar calendar = Utils.toDateTime(entry.getStart());
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
        });

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
            holder.edit.setOnClickListener(v -> editDialog(entry, ent -> {
                scm.setEntry(ent.getId(), ent);
                scm.reschedule();
                return true;
            }));
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
