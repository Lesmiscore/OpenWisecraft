package com.nao20010128nao.Wisecraft.activity;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.azeesoft.lib.colorpicker.*;
import com.ipaulpro.afilechooser.*;
import com.nao20010128nao.ToolBox.*;
import com.nao20010128nao.Wisecraft.BuildConfig;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import com.nao20010128nao.Wisecraft.widget.*;
import permissions.dispatcher.*;
import uk.co.chrisjenx.calligraphy.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import static com.nao20010128nao.Wisecraft.activity.FragmentSettingsActivityImpl.*;

@RuntimePermissions
abstract class FragmentSettingsActivityImpl extends AppCompatActivity implements SettingsScreen {
    public static final Map<String, Class<? extends Fragment>> FRAGMENT_CLASSES = new HashMap<String, Class<? extends Fragment>>() {{
        put("root", HubPrefFragment.class);
        put("basics", Basics.class);
        put("features", Features.class);
        //put("asfsls",Asfsls.class);
        put("versionInfo", VersionInfoFragmentLocal.class);
    }};
    public static final String DIALOG_FRAGMENT_TAG_PREFIX = "settings@com.nao20010128nao.Wisecraft#";
    public static final List<Quartet<Integer, String, Treatment<SettingsScreen>, Boolean>> MAIN;

    static {
        List<Quartet<Integer, String, Treatment<SettingsScreen>, Boolean>> main = new ArrayList<>();
        main.add(new Quartet<>(R.string.basics, "basics", ss -> ((AppCompatActivity) ss)
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(ss.getIdForFragment(), new Basics())
                .addToBackStack("basics")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit(), true));
        main.add(new Quartet<>(R.string.features, "features", ss -> ((AppCompatActivity) ss)
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(ss.getIdForFragment(), new Features())
                .addToBackStack("features")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit(), true));
        main.add(new Quartet<>(R.string.colorChange, "changeColor", ss -> ((AppCompatActivity) ss)
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(ss.getIdForFragment(), new ColorChanger())
                .addToBackStack("changeColor")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit(), true));
        main.add(new Quartet<>(R.string.osl, "osl", ss -> {
            AppCompatActivity act = (AppCompatActivity) ss;
            act.startActivity(new Intent(act, OpenSourceActivity2.class));
        }, false));
        main.add(new Quartet<>(R.string.aboutApp, "aboutApp", ss -> {
            AppCompatActivity act = (AppCompatActivity) ss;
            act.startActivity(new Intent(act, AboutAppActivity.class));
        }, false));
        main.add(new Quartet<>(R.string.versionInfo, "versionInfo", ss -> {
            VersionInfoFragmentLocal fragment = new VersionInfoFragmentLocal();
            fragment.setShowBuildData(BuildConfig.SHOW_BUILD_DATA_ON_VERSIONS);
            ((AppCompatActivity) ss)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(ss.getIdForFragment(), fragment)
                    .addToBackStack("versionInfo")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }, true));
        MAIN = Collections.unmodifiableList(main);
    }

    int which;
    SharedPreferences pref;
    boolean requireRestart = true;

    FrameLayout misc;
    ViewPager pager;
    ServerListFragment slf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = Utils.getPreferences(this);
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings_with_preview);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.preference, new HubPrefFragment())
                    .addToBackStack("root")
                    .commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        TheApplication.instance.initForActivities();
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    @Override
    public void onBackPressed() {
        FragmentManager sfm = getSupportFragmentManager();
        if (sfm.getBackStackEntryCount() < 2) {
            finish();
            return;
        }
        sfm.popBackStack();
    }

    @Override
    public void finish() {
        if (requireRestart) {
            if (ServerListActivityImpl.instance.get() != null)
                ServerListActivityImpl.instance.get().finish();
            startActivity(new Intent(this, ServerListActivity.class));
        }
        super.finish();
    }

    @Override
    public int getIdForFragment() {
        return R.id.preference;
    }

    @Override
    public void excecuteWithGpsPermission(Runnable e) {
        FragmentSettingsActivityImplPermissionsDispatcher.excecuteWithGpsPermissionImplWithCheck(this, e);
    }

    @NeedsPermission({"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"})
    public void excecuteWithGpsPermissionImpl(Runnable r) {
        if (r != null) r.run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FragmentSettingsActivityImplPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    public static class HubPrefFragment extends SettingsBaseFragment {
        SharedPreferences pref;

        @Override
        public void onCreatePreferences(Bundle p1, String p2) {
            addPreferencesFromResource(R.xml.settings_parent_compat);
            for (Trio<Integer, String, Treatment<SettingsScreen>> t : MAIN) {
                Context c = Utils.wrapContextForPreference(getActivity());
                StartPrefCompat pref = new StartPrefCompat(c);
                pref.setKey(t.getB());
                pref.setTitle(t.getA());
                final Treatment<SettingsScreen> proc = t.getC();
                pref.setOnClickListener((HandledPreference.OnClickListener) (a, b, c1) -> proc.process((SettingsScreen) getActivity()));
                getPreferenceScreen().addPreference(pref);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            super.onCreate(savedInstanceState);
            ((SetTextColor) findPreference("settingsAttention")).setTextColor(ContextCompat.getColor(getContext(), R.color.color888));
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.settings);
        }
    }

    public static class Basics extends SettingsBaseFragment {
        public static final String PARALLELS_DIALOG_FRAGMENT_TAG = DIALOG_FRAGMENT_TAG_PREFIX + "parallels-dialog";
        int which;

        @Override
        public void onCreatePreferences(Bundle p1, String p2) {
            addPreferencesFromResource(R.xml.settings_basic_compat);
            sH("parallels", (a, b, c) -> PreferenceUtils.showEditTextDialog(getActivity(), findPreference("parallels"), "6", v -> {
                EditText text = (EditText) v.findViewById(android.R.id.edit);
                text.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_TEXT_VARIATION_NORMAL |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            }));
            sH("serverListStyle", (a, b, c) -> new AlertDialog.Builder(getActivity(), ThemePatcher.getDefaultDialogStyle(getContext()))
                    .setTitle(R.string.serverListStyle)
                    .setSingleChoiceItems(getResources().getStringArray(R.array.serverListStyles), which = pref.getInt("serverListStyle2", 0), (di, w) -> which = w)
                    .setPositiveButton(android.R.string.ok, (di, w) -> pref.edit().putInt("serverListStyle2", which).commit())
                    .setNegativeButton(android.R.string.cancel, (di, w) -> {

                    })
                    .show());
            sH("selectFont", new HandledPreference.OnClickListener() {
                public void onClick(String a, String b, String c) {
                    String[] choice = getFontChoices();
                    String[] display = TheApplication.instance.getDisplayFontNames(choice);
                    final List<String> choiceList = Arrays.asList(choice);
                    new AlertDialog.Builder(getActivity(), ThemePatcher.getDefaultDialogStyle(getContext()))
                            .setSingleChoiceItems(display, choiceList.indexOf(TheApplication.instance.getFontFieldName())
                                    , (di, w) -> {
                                        di.cancel();
                                        TheApplication.instance.setFontFieldName(choiceList.get(w));
                                        //Toast.makeText(getContext(),R.string.saved_fonts,Toast.LENGTH_LONG).show();
                                    })
                            .show();
                }

                String[] getFontChoices() {
                    return getResources().getStringArray(R.array.fontNamesOrder);
                }
            });
            sH("changeDpi", (a, b, c) -> PreferenceUtils.showEditTextDialog(getActivity(), findPreference("changeDpi"), "1.0", v -> ((EditText) v.findViewById(android.R.id.edit)).setInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_TEXT_VARIATION_NORMAL |
                    InputType.TYPE_NUMBER_FLAG_DECIMAL)));
            sH("addLessRows", (a, b, c) -> {
                View v = getLayoutInflater(null).inflate(R.layout.quick_seekbar, null);
                final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekbar);
                ((TextView) v.findViewById(R.id.max)).setText("5");
                ((TextView) v.findViewById(R.id.min)).setText("-5");
                final TextView value = (TextView) v.findViewById(R.id.value);
                seekBar.setMax(10);
                seekBar.setProgress(pref.getInt("addLessRows", 0) + 5);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar p11, int p21, boolean p3) {
                        value.setText((seekBar.getProgress() - 5) + "");
                    }

                    public void onStartTrackingTouch(SeekBar p11) {

                    }

                    public void onStopTrackingTouch(SeekBar p11) {

                    }

                    {
                        onProgressChanged(seekBar, 0, false);
                    }
                });
                new AlertDialog.Builder(getActivity(), ThemePatcher.getDefaultDialogStyle(getContext()))
                        .setTitle(R.string.addLessRows)
                        .setView(v)
                        .setPositiveButton(android.R.string.ok, (di, w) -> pref.edit().putInt("addLessRows", seekBar.getProgress() - 5).commit())
                        .setNegativeButton(android.R.string.cancel, (di, w) -> {

                        })
                        .show();
            });
            sH("serverListLooks", (a, b, c) -> startActivity(new Intent(getActivity(), FragmentSettingsActivity.ServerListStyleEditor.class)));
            sH("widgetEditor", (a, b, c) -> startActivity(new Intent(getActivity(), WidgetsEditorActivity.class)));
            sH("4.0themeMode", (a, b, c) -> {
                final boolean[] gpsRequired = Utils.getBooleanArray(getContext(), R.array.themeMode_GpsRequired);
                new AlertDialog.Builder(getActivity(), ThemePatcher.getDefaultDialogStyle(getContext()))
                        .setTitle(R.string.themeMode)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.themeMode), which = pref.getInt("4.0themeMode", ThemePatcher.THEME_MODE_LIGHT), (di, w) -> which = w)
                        .setPositiveButton(android.R.string.ok, (di, w) -> {
                            if (gpsRequired[which]) {
                                new AlertDialog.Builder(getActivity(), ThemePatcher.getDefaultDialogStyle(getContext()))
                                        .setMessage(getResources().getString(R.string.gpsRequiredForDayNight).replace("{NO}", getResources().getString(android.R.string.no)))
                                        .setPositiveButton(android.R.string.yes, (di1, w1) -> {
//pref.edit().putInt("4.0themeMode",which).commit();
                                            getSettingsScreen().excecuteWithGpsPermission(() -> pref.edit().putInt("4.0themeMode", which).commit());
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            } else {
                                pref.edit().putInt("4.0themeMode", which).commit();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            });
            sH("retryIteration", (a, b, c) -> PreferenceUtils.showEditTextDialog(getActivity(), findPreference("retryIteration"), "10", v -> {
                EditText text = (EditText) v.findViewById(android.R.id.edit);
                text.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_TEXT_VARIATION_NORMAL |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            }));
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.basics);
        }
    }


    public static class Features extends SettingsBaseFragment {
        int which;

        @Override
        public void onCreatePreferences(Bundle p1, String p2) {
            addPreferencesFromResource(R.xml.settings_features_compat);
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.features);
        }
    }

    public static class ColorChanger extends SettingsBaseFragment {

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.colorChange);
        }

        @Override
        public void onCreatePreferences(Bundle p1, String p2) {
            addPreferencesFromResource(R.xml.settings_color_changer_compat);
        }
    }

    public static class VersionInfoFragmentLocal extends VersionInfoFragment {

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.versionInfo);
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            return super.onCreateRecyclerView(Utils.fixLayoutInflaterIfNeeded(CalligraphyContextWrapper.wrap(inflater.getContext()), getActivity()),
                    parent,
                    savedInstanceState);
        }

        @Override
        public void onModifyPreferenceViewHolder(PreferenceViewHolder viewHolder, Preference pref) {
            PreferenceUtils.onBindViewHolder(getActivity(), pref, viewHolder);
        }
    }


    public abstract static class SettingsBaseFragment extends SHablePreferenceFragment {
        protected SharedPreferences pref;

        LinearLayout miscContent;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            super.onCreate(savedInstanceState);
        }

        @Override
        public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
            return getActivity().getLayoutInflater().cloneInContext(super.getLayoutInflater(savedInstanceState).getContext());
        }

        @Override
        public Context getContext() {
            return TheApplication.injectContextSpecial(super.getContext());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(getActivity().getLayoutInflater(), container, savedInstanceState);
            miscContent = (LinearLayout) v.findViewById(R.id.misc);
            if (miscContent != null) onMiscPartAvailable(miscContent);
            return v;
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            return super.onCreateRecyclerView(Utils.fixLayoutInflaterIfNeeded(CalligraphyContextWrapper.wrap(inflater.getContext()), getActivity()),
                    parent,
                    savedInstanceState);
        }

        @Override
        public void onModifyPreferenceViewHolder(PreferenceViewHolder viewHolder, Preference pref) {
            PreferenceUtils.onBindViewHolder(getActivity(), pref, viewHolder);
        }

        public int getIdForFragment() {
            return getSettingsScreen().getIdForFragment();
        }

        public SettingsScreen getSettingsScreen() {
            return (SettingsScreen) getActivity();
        }


        protected void onMiscPartAvailable(LinearLayout misc) {
        }

        public LinearLayout getMiscContent() {
            return miscContent;
        }
    }
}

@RuntimePermissions
abstract class MasterDetailSettingsImpl extends MasterDetailSupportActivity implements SettingsScreen {
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = Utils.getPreferences(this);
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        MAIN.get(0).getC().process(this);
    }

    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecyclerAdapter());
    }

    public int getIdForFragment() {
        return R.id.item_detail_container;
    }

    @Override
    public void excecuteWithGpsPermission(Runnable e) {
        MasterDetailSettingsImplPermissionsDispatcher.excecuteWithGpsPermissionImplWithCheck(this, e);
    }

    @NeedsPermission({"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"})
    public void excecuteWithGpsPermissionImpl(Runnable r) {
        if (r != null) r.run();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MasterDetailSettingsImplPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    final class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.vh> {
        int selected = 0;
        int selectedBg, selectedText, unselectedText;

        public RecyclerAdapter() {
            selectedBg = ThemePatcher.getMainColor(MasterDetailSettingsImpl.this);
            TypedArray ta = obtainStyledAttributes(R.styleable.MasterDetailSettings);
            selectedText = ta.getColor(R.styleable.MasterDetailSettings_wcSelectedTextColor, Color.BLACK);
            unselectedText = ta.getColor(R.styleable.MasterDetailSettings_wcUnselectedTextColor, Color.BLACK);
            ta.recycle();
        }

        @Override
        public vh onCreateViewHolder(ViewGroup p1, int p2) {
            return new vh(p1);
        }

        @Override
        public void onBindViewHolder(vh p1, final int p2) {
            p1.setTitle(MAIN.get(p2).getA());
            p1.setSelected(p2 == selected);
            Utils.applyHandlersForViewTree(p1.itemView, v -> {
                if (p2 == selected) return;
                MAIN.get(p2).getC().process(MasterDetailSettingsImpl.this);
                if (MAIN.get(p2).getD())
                    setSelected(p2);
            });
        }

        @Override
        public int getItemCount() {
            return MAIN.size();
        }

        public void setSelected(int a) {
            notifyItemChanged(a);
            notifyItemChanged(selected);
            selected = a;
        }


        final class vh extends FindableViewHolder {
            public vh(ViewGroup p1) {
                super(getLayoutInflater().inflate(R.layout.item_list_content, p1, false));
            }

            public void setTitle(CharSequence cs) {
                ((TextView) findViewById(R.id.id)).setText(cs);
            }

            public void setTitle(int cs) {
                ((TextView) findViewById(R.id.id)).setText(cs);
            }

            public void setSelected(boolean value) {
                ViewCompat.setBackground(findViewById(R.id.background), value ? new ColorDrawable(selectedBg) : null);
                ((TextView) findViewById(R.id.id)).setTextColor(value ? selectedText : unselectedText);
            }
        }
    }
}

@RuntimePermissions
abstract class ServerListStyleEditorImpl extends AppCompatActivity {
    ServerListStyleLoader slsl;
    RadioGroup rdGrp;
    ImageView color, image, textColor;
    Button selectColor, selectImage, apply, selectTextColor;
    Bitmap loadedBitmap;
    int selectedColor = Color.BLACK, selectedTextColor;
    boolean didOnceColorSelected = false;


    //FSF
    Map<Integer, ServerListActivityBase5.ChooserResult> results = new HashMap<>();
    SecureRandom sr = new SecureRandom();
    Object lastResult = null;

    Button select;
    ImageButton fileLocal, fileProvided;
    EditText path;
    LinearLayout pathForm, modeForm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemePatcher.applyThemeForActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_server_list_style_editor);
        slsl = new ServerListStyleLoader(this);
        rdGrp = (RadioGroup) findViewById(R.id.checkGroup);
        color = (ImageView) findViewById(R.id.singleColorIndicate);
        image = (ImageView) findViewById(R.id.imagePreview);
        textColor = (ImageView) findViewById(R.id.textColorIndicate);
        selectColor = (Button) findViewById(R.id.selectColor);
        selectImage = (Button) findViewById(R.id.selectImage);
        selectTextColor = (Button) findViewById(R.id.selectTextColor);
        apply = (Button) findViewById(R.id.apply);

        switch (slsl.getBgId()) {
            case ServerListStyleLoader.BACKGROUND_WHITE:
                rdGrp.check(R.id.white);
                break;
            case ServerListStyleLoader.BACKGROUND_BLACK:
                rdGrp.check(R.id.black);
                break;
            case ServerListStyleLoader.BACKGROUND_DIRT:
                rdGrp.check(R.id.dirt);
                break;
            case ServerListStyleLoader.BACKGROUND_SINGLE_COLOR:
                rdGrp.check(R.id.singleColor);
                color.setImageDrawable(slsl.load());
                selectedColor = slsl.getBackgroundSimpleColor();
                break;
            case ServerListStyleLoader.BACKGROUND_IMAGE:
                rdGrp.check(R.id.image);
                loadedBitmap = slsl.getImageBgBitmap();
                image.setImageBitmap(loadedBitmap);
                break;
        }
        selectedTextColor = slsl.getTextColor();
        textColor.setImageDrawable(new ColorDrawable(selectedTextColor));

        rdGrp.setOnCheckedChangeListener((p1, p2) -> {
            switch (rdGrp.getCheckedRadioButtonId()) {
                case R.id.white:
                case R.id.black:
                case R.id.dirt:
                case R.id.singleColor:
                    apply.setEnabled(true);
                    break;
                case R.id.image:
                    apply.setEnabled(loadedBitmap != null);
                    break;
            }
        });
        selectColor.setOnClickListener(v -> {
            ColorPickerDialog cpd = ColorPickerDialog.createColorPickerDialog(ServerListStyleEditorImpl.this, ColorPickerDialog.LIGHT_THEME);
            cpd.setLastColor(selectedColor);
            cpd.setOnColorPickedListener((c, hex) -> {
                selectedColor = c;
                didOnceColorSelected = true;
                color.setImageDrawable(new ColorDrawable(selectedColor));
            });
            cpd.setOnClosedListener(() -> {
            });
            cpd.show();
        });
        selectImage.setOnClickListener(v -> new AsyncTask<Object, Void, Bitmap>() {
            public Bitmap doInBackground(Object... a) {
                Log.d("slse image", a[0] + "");
                String path = Utils.toString(a[0]);
                InputStream is = null;
                try {
                    is = tryOpen(path);
                    return BitmapFactory.decodeStream(is);
                } catch (Throwable e) {
                    WisecraftError.report("slse image", e);
                    DebugWriter.writeToE("slse image", e);
                    return null;
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        WisecraftError.report("slse image", e);
                    }
                }
            }

            public void onPostExecute(Bitmap bmp) {
                loadedBitmap = bmp;
                image.setImageBitmap(loadedBitmap);
                apply.setEnabled(loadedBitmap != null);
                Log.d("slse image", "loaded:" + bmp);
            }
        }.execute(getResult()));
        selectTextColor.setOnClickListener(v -> {
            ColorPickerDialog cpd = ColorPickerDialog.createColorPickerDialog(ServerListStyleEditorImpl.this, ColorPickerDialog.LIGHT_THEME);
            cpd.setLastColor(selectedColor);
            cpd.setOnColorPickedListener((c, hex) -> {
                selectedTextColor = c;
                textColor.setImageDrawable(new ColorDrawable(selectedTextColor));
            });
            cpd.setOnClosedListener(() -> {
            });
            cpd.show();
        });
        apply.setOnClickListener(v -> {
            switch (rdGrp.getCheckedRadioButtonId()) {
                case R.id.white:
                    slsl.setWhiteBg();
                    break;
                case R.id.black:
                    slsl.setBlackBg();
                    break;
                case R.id.dirt:
                    slsl.setDirtBg();
                    break;
                case R.id.singleColor:
                    slsl.setSingleColorBg(selectedColor);
                    break;
                case R.id.image:
                    if (loadedBitmap != null)
                        slsl.setImageBg(loadedBitmap);
                    else return;
                    break;
            }
            slsl.setTextColor(selectedTextColor);
            finish();
        });

        //FSF
        getLayoutInflater().inflate(R.layout.server_list_imp_exp, (ViewGroup) findViewById(R.id.fileSelectFrg));
        select = (Button) findViewById(R.id.selectFile);
        fileLocal = (ImageButton) findViewById(R.id.openLocalChooser);
        fileProvided = (ImageButton) findViewById(R.id.openProvidedChooser);
        path = (EditText) findViewById(R.id.filePath);
        pathForm = (LinearLayout) findViewById(R.id.pathForm);
        modeForm = (LinearLayout) findViewById(R.id.modeForm);

        select.setOnClickListener(v -> {
            pathForm.setVisibility(View.GONE);
            modeForm.setVisibility(View.VISIBLE);
        });
        fileLocal.setOnClickListener(v -> {
            modeForm.setVisibility(View.GONE);
            pathForm.setVisibility(View.VISIBLE);

            File f = new File(path.getText().toString());
            if ((!f.exists()) | f.isFile()) f = f.getParentFile();
            ServerListStyleEditorImplPermissionsDispatcher.startChooseFileForSelectWithCheck(ServerListStyleEditorImpl.this, f, new ServerListActivityBase5.FileChooserResult() {
                public void onSelected(File f) {
                    path.setText(f.toString());
                    path.setEnabled(true);
                }

                public void onSelectCancelled() {/*No-op*/}
            });
        });
        fileProvided.setOnClickListener(v -> {
            modeForm.setVisibility(View.GONE);
            pathForm.setVisibility(View.VISIBLE);
            ServerListStyleEditorImplPermissionsDispatcher.startExtChooseFileWithCheck(ServerListStyleEditorImpl.this, new ServerListActivityBase5.UriFileChooserResult() {
                public void onSelected(Uri f) {
                    path.setText("");
                    path.setEnabled(false);
                }

                public void onSelectCancelled() {/*No-op*/}
            });
        });
        path.setOnTouchListener((v, ev) -> {
            if (ev.getAction() != MotionEvent.ACTION_UP) return false;
            if (!v.isEnabled()) {
                v.setEnabled(true);
                path.setText(Environment.getExternalStorageDirectory().toString());
                lastResult = null;
            }
            return false;
        });
        path.setText(Environment.getExternalStorageDirectory().toString());
        fileLocal.setImageDrawable(TheApplication.getTintedDrawable(R.drawable.ic_file, Color.WHITE, this));
        fileProvided.setImageDrawable(TheApplication.getTintedDrawable(R.drawable.ic_launch_black_36dp, Color.WHITE, this));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TheApplication.injectContextSpecial(newBase));
    }

    public InputStream tryOpen(String uri) throws IOException {
        Log.d("dbg", "tryOpen:" + uri);
        if (uri.startsWith("content://")) {
            return getContentResolver().openInputStream(Uri.parse(uri));
        } else if (uri.startsWith("/")) {
            return new FileInputStream(uri);
        } else {
            return URI.create(uri).toURL().openConnection().getInputStream();
        }
    }

    public OutputStream trySave(String uri) throws IOException {
        Log.d("dbg", "trySave:" + uri);
        if (uri.startsWith("content://")) {
            return getContentResolver().openOutputStream(Uri.parse(uri));
        } else if (uri.startsWith("/")) {
            return new FileOutputStream(uri);
        } else {
            return URI.create(uri).toURL().openConnection().getOutputStream();
        }
    }

    public static String toUri(Object o) throws IOException, URISyntaxException {
        if (o instanceof File)
            return toUri(((File) o).toURL());
        else if (o instanceof Uri)
            return ((Uri) o).toString();
        else if (o instanceof URL)
            return toUri(((URL) o).toURI());
        else if (o instanceof URI)
            return ((URI) o).toString();
        else
            return null;
    }

    //FSF
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (results.containsKey(requestCode)) {
            switch (resultCode) {
                case RESULT_OK:
                    if (results.get(requestCode) instanceof ServerListActivity.FileChooserResult) {
                        ((ServerListActivity.FileChooserResult) results.get(requestCode))
                                .onSelected((File) (lastResult = new File(data.getStringExtra("path"))));
                    } else if (results.get(requestCode) instanceof ServerListActivity.UriFileChooserResult) {
                        ((ServerListActivity.UriFileChooserResult) results.get(requestCode))
                                .onSelected((Uri) (lastResult = data.getData()));
                    }
                    Log.d("slse", "select:" + lastResult);
                    break;
                case RESULT_CANCELED:
                    results.get(requestCode).onSelectCancelled();
                    break;
            }
            results.remove(requestCode);
        }
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseFileForOpen(File startDir, ServerListActivity.FileChooserResult result) {
        int call = nextCallId();
        Intent intent = new Intent(this, FileOpenChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseFileForSelect(File startDir, ServerListActivity.FileChooserResult result) {
        int call = nextCallId();
        Intent intent = new Intent(this, FileChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startChooseDirectory(File startDir, ServerListActivity.FileChooserResult result) {
        int call = nextCallId();
        Intent intent = new Intent(this, DirectoryChooserActivity.class);
        if (startDir != null) {
            intent.putExtra("path", startDir.toString());
        }
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void startExtChooseFile(ServerListActivity.UriFileChooserResult result) {
        int call = nextCallId();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        results.put(call, Utils.requireNonNull(result));
        startActivityForResult(intent, call);
    }

    private int nextCallId() {
        int call = Math.abs(sr.nextInt()) & 0xff;
        while (results.containsKey(call)) {
            call = Math.abs(sr.nextInt()) & 0xff;
        }
        return call;
    }

    public Object getLastResult() {
        return lastResult;
    }

    public Object getResult() {
        if (lastResult == null) {
            //no choose, so file
            return new File(path.getText().toString());
        } else if (lastResult instanceof File) {
            //file choosen, so file
            return new File(path.getText().toString());
        } else {
            //uri retrived, return last result
            return lastResult;
        }
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
        ServerListStyleEditorImplPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}

interface SettingsScreen {
    int getIdForFragment();

    void excecuteWithGpsPermission(Runnable e);
}


public class FragmentSettingsActivity extends FragmentSettingsActivityImpl {
    public static class ServerListStyleEditor extends ServerListStyleEditorImpl {

    }

    public static class MasterDetailSettings extends MasterDetailSettingsImpl {

    }
}
