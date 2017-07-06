package com.nao20010128nao.Wisecraft.misc.debug;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;
import android.support.v7.preference.*;
import com.annimon.stream.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.R;
import com.nao20010128nao.Wisecraft.activity.*;
import com.nao20010128nao.Wisecraft.misc.*;
import com.nao20010128nao.Wisecraft.misc.pref.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import dalvik.system.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import static com.nao20010128nao.Wisecraft.BuildConfig.*;
import static com.nao20010128nao.Wisecraft.misc.compat.BuildConfig.*;

class DebugBridge$Debug2 extends DebugBridge {
	boolean init=false;
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void init(Context ctx) {
        if(init)return;
        
        File groovyDex = new File(ctx.getCacheDir(), "groovy.zip");
        boolean shouldExpandGroovy = true;
        if (groovyDex.exists()) {
            // now we check CRC32 checksum
            CRC32 crc = new CRC32();
            Utils.readBytes(groovyDex, crc::update);
            shouldExpandGroovy = crc.getValue() != GROOVY_CRC32;
        }
        if (shouldExpandGroovy) {
            // copy groovy into local
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(groovyDex);
                Utils.readBytes(ctx.getAssets().open("groovy.zip"), fos::write);
            } catch (Throwable e) {
                WisecraftError.report("DebugBridge", e);
                return;
            } finally {
                Utils.safeClose(fos);
            }
        }
        // now load it
        // TODO: support for newer versions
        try{
            MultiDex.doInstallation(ctx,groovyDex,ctx.getCacheDir(),"groovy-dexes","");
        }catch(Throwable e){
            WisecraftError.report("DebugBridge", e);
            return;
        }
        
        // try loading GroovyObject
        try{
            Class.forName("groovy.lang.GroovyObject");
        }catch(Throwable e){
            WisecraftError.report("DebugBridge", e);
            return;
        }
        init=true;
    }

    @Override
    public void openDebugActivity(Context ctx) {
        Intent intent=new Intent(ctx,DebugList.class);
        if(!(ctx instanceof Activity))intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    
    @Override
    public void addDebugMenus(SextetWalker<Integer, Integer, Consumer<Activity>, Consumer<Activity>, IDrawerItem, UUID> list){
        list.add(new Sextet<>(R.string.dbgMenu,R.drawable.ic_add_black_48dp,this::openDebugActivity,null,null,UUID.fromString("2ee5ea67-99b2-4f75-b7a8-19deaee2e4ed")));
    }
    
    @Override
    public void addDebugInfos(Context ctx,PreferenceScreen preferences){
        Context c = CompatUtils.wrapContextForPreference(ctx);
        List<Preference> buildData = new ArrayList<>();
        buildData.add(new SimplePref(c, "Build ID",    CI_BUILD_ID));
        buildData.add(new SimplePref(c, "Build Ref",   CI_BUILD_REF_NAME));
        buildData.add(new SimplePref(c, "Runner ID",   CI_RUNNER_ID));
        buildData.add(new SimplePref(c, "Build Stage", CI_BUILD_STAGE));
        buildData.add(new SimplePref(c, "Build Name",  CI_BUILD_NAME));
        Stream.of(buildData)
            .peek(preferences::addPreference)
            .forEach(a->a.setVisible(true));
    }
}
// MultiDex
final class MultiDex {

    // we need to prevent confusion: now at DebugBridge
    static final String TAG = "DebugBridge";

    private static final String OLD_SECONDARY_FOLDER_NAME = "secondary-dexes";

    private static final String CODE_CACHE_NAME = "code_cache";

    private static final String CODE_CACHE_SECONDARY_FOLDER_NAME = "secondary-dexes";

    private static final int MAX_SUPPORTED_SDK_VERSION = 20;

    private static final int MIN_SDK_VERSION = 4;

    private static final int VM_WITH_MULTIDEX_VERSION_MAJOR = 2;

    private static final int VM_WITH_MULTIDEX_VERSION_MINOR = 1;

    private static final String NO_KEY_PREFIX = "";

    private static final Set<File> installedApk = new HashSet<File>();

    private static final boolean IS_VM_MULTIDEX_CAPABLE = false;
            //isVMMultidexCapable(System.getProperty("java.vm.version"));

    private MultiDex() {}

    public static void install(Context context) {
        Log.i(TAG, "Installing application");
        if (IS_VM_MULTIDEX_CAPABLE) {
            Log.i(TAG, "VM has multidex support, MultiDex support library is disabled.");
            return;
        }

        if (Build.VERSION.SDK_INT < MIN_SDK_VERSION) {
            throw new RuntimeException("MultiDex installation failed. SDK " + Build.VERSION.SDK_INT
                    + " is unsupported. Min SDK version is " + MIN_SDK_VERSION + ".");
        }

        try {
            ApplicationInfo applicationInfo = getApplicationInfo(context);
            if (applicationInfo == null) {
                Log.i(TAG, "No ApplicationInfo available, i.e. running on a test Context:"
                        + " MultiDex support library is disabled.");
                return;
            }

            doInstallation(context,
                    new File(applicationInfo.sourceDir),
                    new File(applicationInfo.dataDir),
                    CODE_CACHE_SECONDARY_FOLDER_NAME,
                    NO_KEY_PREFIX);

        } catch (Exception e) {
            Log.e(TAG, "MultiDex installation failure", e);
            throw new RuntimeException("MultiDex installation failed (" + e.getMessage() + ").");
        }
        Log.i(TAG, "install done");
    }

    public static void installInstrumentation(Context instrumentationContext,
                                              Context targetContext) {
        Log.i(TAG, "Installing instrumentation");

        if (IS_VM_MULTIDEX_CAPABLE) {
            Log.i(TAG, "VM has multidex support, MultiDex support library is disabled.");
            return;
        }

        if (Build.VERSION.SDK_INT < MIN_SDK_VERSION) {
            throw new RuntimeException("MultiDex installation failed. SDK " + Build.VERSION.SDK_INT
                    + " is unsupported. Min SDK version is " + MIN_SDK_VERSION + ".");
        }
        try {

            ApplicationInfo instrumentationInfo = getApplicationInfo(instrumentationContext);
            if (instrumentationInfo == null) {
                Log.i(TAG, "No ApplicationInfo available for instrumentation, i.e. running on a"
                        + " test Context: MultiDex support library is disabled.");
                return;
            }

            ApplicationInfo applicationInfo = getApplicationInfo(targetContext);
            if (applicationInfo == null) {
                Log.i(TAG, "No ApplicationInfo available, i.e. running on a test Context:"
                        + " MultiDex support library is disabled.");
                return;
            }

            String instrumentationPrefix = instrumentationContext.getPackageName() + ".";

            File dataDir = new File(applicationInfo.dataDir);

            doInstallation(targetContext,
                    new File(instrumentationInfo.sourceDir),
                    dataDir,
                    instrumentationPrefix + CODE_CACHE_SECONDARY_FOLDER_NAME,
                    instrumentationPrefix);

            doInstallation(targetContext,
                    new File(applicationInfo.sourceDir),
                    dataDir,
                    CODE_CACHE_SECONDARY_FOLDER_NAME,
                    NO_KEY_PREFIX);
        } catch (Exception e) {
            Log.e(TAG, "MultiDex installation failure", e);
            throw new RuntimeException("MultiDex installation failed (" + e.getMessage() + ").");
        }
        Log.i(TAG, "Installation done");
    }

    public static void doInstallation(Context mainContext, File sourceApk, File dataDir,
                                       String secondaryFolderName, String prefsKeyPrefix) throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            InvocationTargetException, NoSuchMethodException {
        synchronized (installedApk) {
            if (installedApk.contains(sourceApk)) {
                return;
            }
            installedApk.add(sourceApk);

            if (Build.VERSION.SDK_INT > MAX_SUPPORTED_SDK_VERSION) {
                Log.w(TAG, "MultiDex is not guaranteed to work in SDK version "
                        + Build.VERSION.SDK_INT + ": SDK version higher than "
                        + MAX_SUPPORTED_SDK_VERSION + " should be backed by "
                        + "runtime with built-in multidex capabilty but it's not the "
                        + "case here: java.vm.version=\""
                        + System.getProperty("java.vm.version") + "\"");
            }

            ClassLoader loader;
            try {
                loader = mainContext.getClassLoader();
            } catch (RuntimeException e) {
                Log.w(TAG, "Failure while trying to obtain Context class loader. " +
                        "Must be running in test mode. Skip patching.", e);
                return;
            }
            if (loader == null) {
                Log.e(TAG,
                        "Context class loader is null. Must be running in test mode. "
                                + "Skip patching.");
                return;
            }

            try {
                clearOldDexDir(mainContext);
            } catch (Throwable t) {
                Log.w(TAG, "Something went wrong when trying to clear old MultiDex extraction, "
                        + "continuing without cleaning.", t);
            }

            File dexDir = getDexDir(mainContext, dataDir, secondaryFolderName);
            List<? extends File> files =
                    MultiDexExtractor.load(mainContext, sourceApk, dexDir, prefsKeyPrefix, false);
            installSecondaryDexes(loader, dexDir, files);
        }
    }

    public static ApplicationInfo getApplicationInfo(Context context) {
        try {
            return context.getApplicationInfo();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure while trying to obtain ApplicationInfo from Context. " +
                    "Must be running in test mode. Skip patching.", e);
            return null;
        }
    }

    static boolean isVMMultidexCapable(String versionString) {
        boolean isMultidexCapable = false;
        if (versionString != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
            if (matcher.matches()) {
                try {
                    int major = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    isMultidexCapable = (major > VM_WITH_MULTIDEX_VERSION_MAJOR)
                            || ((major == VM_WITH_MULTIDEX_VERSION_MAJOR)
                            && (minor >= VM_WITH_MULTIDEX_VERSION_MINOR));
                } catch (NumberFormatException e) {
                }
            }
        }
        Log.i(TAG, "VM with version " + versionString +
                (isMultidexCapable ?
                        " has multidex support" :
                        " does not have multidex support"));
        return isMultidexCapable;
    }

    private static void installSecondaryDexes(ClassLoader loader, File dexDir,
                                              List<? extends File> files)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            InvocationTargetException, NoSuchMethodException, IOException {
        if (!files.isEmpty()) {
            if (Type1.isApplicable(loader)) {
                Type1.install(loader, files, dexDir);
            } else if (Build.VERSION.SDK_INT >= /*19*/23) {
                V19.install(loader, files, dexDir);
            } else if (Build.VERSION.SDK_INT >= 14) {
                V14.install(loader, files, dexDir);
            } else {
                V4.install(loader, files);
            }
        }
    }

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);


                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);


                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " +
                Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }
    
    private static void expandFieldArray(Object instance, String fieldName,
                                         Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field jlrField = findField(instance, fieldName);
        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(
                original.getClass().getComponentType(), original.length + extraElements.length);
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);
        jlrField.set(instance, combined);
    }

    private static void clearOldDexDir(Context context) throws Exception {
        File dexDir = new File(context.getFilesDir(), OLD_SECONDARY_FOLDER_NAME);
        if (dexDir.isDirectory()) {
            Log.i(TAG, "Clearing old secondary dex dir (" + dexDir.getPath() + ").");
            File[] files = dexDir.listFiles();
            if (files == null) {
                Log.w(TAG, "Failed to list secondary dex dir content (" + dexDir.getPath() + ").");
                return;
            }
            for (File oldFile : files) {
                Log.i(TAG, "Trying to delete old file " + oldFile.getPath() + " of size "
                        + oldFile.length());
                if (!oldFile.delete()) {
                    Log.w(TAG, "Failed to delete old file " + oldFile.getPath());
                } else {
                    Log.i(TAG, "Deleted old file " + oldFile.getPath());
                }
            }
            if (!dexDir.delete()) {
                Log.w(TAG, "Failed to delete secondary dex dir " + dexDir.getPath());
            } else {
                Log.i(TAG, "Deleted old secondary dex dir " + dexDir.getPath());
            }
        }
    }

    private static File getDexDir(Context context, File dataDir, String secondaryFolderName)
            throws IOException {
        File cache = new File(dataDir, CODE_CACHE_NAME);
        try {
            mkdirChecked(cache);
        } catch (IOException e) {
            cache = new File(context.getFilesDir(), CODE_CACHE_NAME);
            mkdirChecked(cache);
        }
        File dexDir = new File(cache, secondaryFolderName);
        mkdirChecked(dexDir);
        return dexDir;
    }

    private static void mkdirChecked(File dir) throws IOException {
        dir.mkdir();
        if (!dir.isDirectory()) {
            File parent = dir.getParentFile();
            if (parent == null) {
                Log.e(TAG, "Failed to create dir " + dir.getPath() + ". Parent file is null.");
            } else {
                Log.e(TAG, "Failed to create dir " + dir.getPath() +
                        ". parent file is a dir " + parent.isDirectory() +
                        ", a file " + parent.isFile() +
                        ", exists " + parent.exists() +
                        ", readable " + parent.canRead() +
                        ", writable " + parent.canWrite());
            }
            throw new IOException("Failed to create directory " + dir.getPath());
        }
    }

    // https://android.googlesource.com/platform/libcore/+/51cba155feefe3e47f99fbae2e166a796f5a388c/dalvik/src/main/java/dalvik/system/DexPathList.java
    private static final class Type1{
        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            Method addDexPath = findMethod(dexPathList, "addDexPath", String.class, File.class);

            for(File f:additionalClassPathEntries)
                addDexPath.invoke(dexPathList, f, optimizedDirectory);
        }
        
        private static boolean isApplicable(ClassLoader loader)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            try{
                Method addDexPath = findMethod(dexPathList, "addDexPath", String.class, File.class);
                return addDexPath!=null;
            }catch(Throwable e){
                return false;
            }
        }
    }

    private static final class V19 {

        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                }
                Field suppressedExceptionsField =
                        findField(dexPathList, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions =
                        (IOException[]) suppressedExceptionsField.get(dexPathList);

                if (dexElementsSuppressedExceptions == null) {
                    dexElementsSuppressedExceptions =
                            suppressedExceptions.toArray(
                                    new IOException[suppressedExceptions.size()]);
                } else {
                    IOException[] combined =
                            new IOException[suppressedExceptions.size() +
                                    dexElementsSuppressedExceptions.length];
                    suppressedExceptions.toArray(combined);
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined,
                            suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }

                suppressedExceptionsField.set(dexPathList, dexElementsSuppressedExceptions);
            }
        }

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class,
                            ArrayList.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory,
                    suppressedExceptions);
        }
    }

    private static final class V14 {

        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {

            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));
        }

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }
    }

    @Deprecated
    private static final class V4 {
        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, IOException {
            int extraSize = additionalClassPathEntries.size();

            Field pathField = findField(loader, "path");

            StringBuilder path = new StringBuilder((String) pathField.get(loader));
            String[] extraPaths = new String[extraSize];
            File[] extraFiles = new File[extraSize];
            ZipFile[] extraZips = new ZipFile[extraSize];
            DexFile[] extraDexs = new DexFile[extraSize];
            for (ListIterator<? extends File> iterator = additionalClassPathEntries.listIterator();
                 iterator.hasNext();) {
                File additionalEntry = iterator.next();
                String entryPath = additionalEntry.getAbsolutePath();
                path.append(':').append(entryPath);
                int index = iterator.previousIndex();
                extraPaths[index] = entryPath;
                extraFiles[index] = additionalEntry;
                extraZips[index] = new ZipFile(additionalEntry);
                extraDexs[index] = DexFile.loadDex(entryPath, entryPath + ".dex", 0);
            }

            pathField.set(loader, path.toString());
            expandFieldArray(loader, "mPaths", extraPaths);
            expandFieldArray(loader, "mFiles", extraFiles);
            expandFieldArray(loader, "mZips", extraZips);
            expandFieldArray(loader, "mDexs", extraDexs);
        }
    }
}
final class MultiDexExtractor {

    private static class ExtractedDex extends File {
        public long crc = NO_VALUE;

        public ExtractedDex(File dexDir, String fileName) {
            super(dexDir, fileName);
        }
    }

    private static final String TAG = MultiDex.TAG;

    private static final String DEX_PREFIX = "classes";
    private static final String DEX_SUFFIX = ".dex";

    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";
    private static final int MAX_EXTRACT_ATTEMPTS = 3;

    private static final String PREFS_FILE = "debug-bridge";
    private static final String KEY_TIME_STAMP = "timestamp";
    private static final String KEY_CRC = "crc";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final String KEY_DEX_CRC = "dex.crc.";
    private static final String KEY_DEX_TIME = "dex.time.";

    private static final int BUFFER_SIZE = 0x4000;
    private static final long NO_VALUE = -1L;

    private static final String LOCK_FILENAME = "DebugBridge.lock";

    static List<? extends File> load(Context context, File sourceApk, File dexDir,
                                     String prefsKeyPrefix,
                                     boolean forceReload) throws IOException {
        Log.i(TAG, "MultiDexExtractor.load(" + sourceApk.getPath() + ", " + forceReload + ", " +
                prefsKeyPrefix + ")");

        long currentCrc = getZipCrc(sourceApk);

        File lockFile = new File(dexDir, LOCK_FILENAME);
        RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
        FileChannel lockChannel = null;
        FileLock cacheLock = null;
        List<ExtractedDex> files;
        IOException releaseLockException = null;
        try {
            lockChannel = lockRaf.getChannel();
            Log.i(TAG, "Blocking on lock " + lockFile.getPath());
            cacheLock = lockChannel.lock();
            Log.i(TAG, lockFile.getPath() + " locked");

            if (!forceReload && !isModified(context, sourceApk, currentCrc, prefsKeyPrefix)) {
                try {
                    files = loadExistingExtractions(context, sourceApk, dexDir, prefsKeyPrefix);
                } catch (IOException ioe) {
                    Log.w(TAG, "Failed to reload existing extracted secondary dex files,"
                            + " falling back to fresh extraction", ioe);
                    files = performExtractions(sourceApk, dexDir);
                    putStoredApkInfo(context, prefsKeyPrefix, getTimeStamp(sourceApk), currentCrc,
                            files);
                }
            } else {
                Log.i(TAG, "Detected that extraction must be performed.");
                files = performExtractions(sourceApk, dexDir);
                putStoredApkInfo(context, prefsKeyPrefix, getTimeStamp(sourceApk), currentCrc,
                        files);
            }
        } finally {
            if (cacheLock != null) {
                try {
                    cacheLock.release();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to release lock on " + lockFile.getPath());
                    releaseLockException = e;
                }
            }
            if (lockChannel != null) {
                closeQuietly(lockChannel);
            }
            closeQuietly(lockRaf);
        }

        if (releaseLockException != null) {
            throw releaseLockException;
        }

        Log.i(TAG, "load found " + files.size() + " secondary dex files");
        return files;
    }
    
    private static List<ExtractedDex> loadExistingExtractions(
            Context context, File sourceApk, File dexDir,
            String prefsKeyPrefix)
            throws IOException {
        Log.i(TAG, "loading existing secondary dex files");

        final String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        int totalDexNumber = multiDexPreferences.getInt(prefsKeyPrefix + KEY_DEX_NUMBER, 1);
        final List<ExtractedDex> files = new ArrayList<ExtractedDex>(totalDexNumber - 1);

        for (int secondaryNumber = 1; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            String fileName = extractedFilePrefix + (secondaryNumber==1?"":secondaryNumber) + EXTRACTED_SUFFIX;
            ExtractedDex extractedFile = new ExtractedDex(dexDir, fileName);
            if (extractedFile.isFile()) {
                extractedFile.crc = getZipCrc(extractedFile);
                long expectedCrc = multiDexPreferences.getLong(
                        prefsKeyPrefix + KEY_DEX_CRC + secondaryNumber, NO_VALUE);
                long expectedModTime = multiDexPreferences.getLong(
                        prefsKeyPrefix + KEY_DEX_TIME + secondaryNumber, NO_VALUE);
                long lastModified = extractedFile.lastModified();
                if ((expectedModTime != lastModified)
                        || (expectedCrc != extractedFile.crc)) {
                    throw new IOException("Invalid extracted dex: " + extractedFile +
                            " (key \"" + prefsKeyPrefix + "\"), expected modification time: "
                            + expectedModTime + ", modification time: "
                            + lastModified + ", expected crc: "
                            + expectedCrc + ", file crc: " + extractedFile.crc);
                }
                files.add(extractedFile);
            } else {
                throw new IOException("Missing extracted secondary dex file '" +
                        extractedFile.getPath() + "'");
            }
        }

        return files;
    }
    
    private static boolean isModified(Context context, File archive, long currentCrc,
                                      String prefsKeyPrefix) {
        SharedPreferences prefs = getMultiDexPreferences(context);
        return (prefs.getLong(prefsKeyPrefix + KEY_TIME_STAMP, NO_VALUE) != getTimeStamp(archive))
                || (prefs.getLong(prefsKeyPrefix + KEY_CRC, NO_VALUE) != currentCrc);
    }

    private static long getTimeStamp(File archive) {
        long timeStamp = archive.lastModified();
        if (timeStamp == NO_VALUE) {
            // never return NO_VALUE
            timeStamp--;
        }
        return timeStamp;
    }


    private static long getZipCrc(File archive) throws IOException {
        long computedValue = ZipUtil.getZipCrc(archive);
        if (computedValue == NO_VALUE) {
            computedValue--;
        }
        return computedValue;
    }

    private static List<ExtractedDex> performExtractions(File sourceApk, File dexDir)
            throws IOException {

        final String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

        prepareDexDir(dexDir, extractedFilePrefix);

        List<ExtractedDex> files = new ArrayList<ExtractedDex>();

        final ZipFile apk = new ZipFile(sourceApk);
        try {

            int secondaryNumber = 1;

            ZipEntry dexFile = apk.getEntry(DEX_PREFIX + DEX_SUFFIX);
            while (dexFile != null) {
                String fileName = extractedFilePrefix + (secondaryNumber==1?"":secondaryNumber) + EXTRACTED_SUFFIX;
                ExtractedDex extractedFile = new ExtractedDex(dexDir, fileName);
                files.add(extractedFile);

                Log.i(TAG, "Extraction is needed for file " + extractedFile);
                int numAttempts = 0;
                boolean isExtractionSuccessful = false;
                while (numAttempts < MAX_EXTRACT_ATTEMPTS && !isExtractionSuccessful) {
                    numAttempts++;

                    extract(apk, dexFile, extractedFile, extractedFilePrefix, secondaryNumber==1);

                    try {
                        extractedFile.crc = getZipCrc(extractedFile);
                        isExtractionSuccessful = true;
                    } catch (IOException e) {
                        isExtractionSuccessful = false;
                        Log.w(TAG, "Failed to read crc from " + extractedFile.getAbsolutePath(), e);
                    }

                    Log.i(TAG, "Extraction " + (isExtractionSuccessful ? "succeeded" : "failed") +
                            " - length " + extractedFile.getAbsolutePath() + ": " +
                            extractedFile.length() + " - crc: " + extractedFile.crc);
                    if (!isExtractionSuccessful) {
                        extractedFile.delete();
                        if (extractedFile.exists()) {
                            Log.w(TAG, "Failed to delete corrupted secondary dex '" +
                                    extractedFile.getPath() + "'");
                        }
                    }
                }
                if (!isExtractionSuccessful) {
                    throw new IOException("Could not create zip file " +
                            extractedFile.getAbsolutePath() + " for secondary dex (" +
                            secondaryNumber + ")");
                }
                secondaryNumber++;
                dexFile = apk.getEntry(DEX_PREFIX + secondaryNumber + DEX_SUFFIX);
            }
        } finally {
            try {
                apk.close();
            } catch (IOException e) {
                Log.w(TAG, "Failed to close resource", e);
            }
        }

        return files;
    }

    private static void putStoredApkInfo(Context context, String keyPrefix, long timeStamp,
                                         long crc, List<ExtractedDex> extractedDexes) {
        SharedPreferences prefs = getMultiDexPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(keyPrefix + KEY_TIME_STAMP, timeStamp);
        edit.putLong(keyPrefix + KEY_CRC, crc);
        edit.putInt(keyPrefix + KEY_DEX_NUMBER, extractedDexes.size() + 1);

        int extractedDexId = 2;
        for (ExtractedDex dex : extractedDexes) {
            edit.putLong(keyPrefix + KEY_DEX_CRC + extractedDexId, dex.crc);
            edit.putLong(keyPrefix + KEY_DEX_TIME + extractedDexId, dex.lastModified());
            extractedDexId++;
        }
        edit.commit();
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE,
                Build.VERSION.SDK_INT < 11 /* Build.VERSION_CODES.HONEYCOMB */
                        ? Context.MODE_PRIVATE
                        : Context.MODE_PRIVATE | 0x0004 /* Context.MODE_MULTI_PROCESS */);
    }

    private static void prepareDexDir(File dexDir, final String extractedFilePrefix) {
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return !(name.startsWith(extractedFilePrefix)
                        || name.equals(LOCK_FILENAME));
            }
        };
        File[] files = dexDir.listFiles(filter);
        if (files == null) {
            Log.w(TAG, "Failed to list secondary dex dir content (" + dexDir.getPath() + ").");
            return;
        }
        for (File oldFile : files) {
            Log.i(TAG, "Trying to delete old file " + oldFile.getPath() + " of size " +
                    oldFile.length());
            if (!oldFile.delete()) {
                Log.w(TAG, "Failed to delete old file " + oldFile.getPath());
            } else {
                Log.i(TAG, "Deleted old file " + oldFile.getPath());
            }
        }
    }

    //TODO: support for resources
    private static void extract(ZipFile apk, ZipEntry dexFile, File extractTo,
                                String extractedFilePrefix,
                                boolean withResources) throws IOException, FileNotFoundException {

        InputStream in = apk.getInputStream(dexFile);
        ZipOutputStream out = null;
        File tmp = File.createTempFile("tmp-" + extractedFilePrefix, EXTRACTED_SUFFIX,
                extractTo.getParentFile());
        Log.i(TAG, "Extracting " + tmp.getPath());
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)));
            try {
                ZipEntry classesDex = new ZipEntry("classes.dex");
                classesDex.setTime(dexFile.getTime());
                out.putNextEntry(classesDex);

                /*byte[] buffer = new byte[BUFFER_SIZE];
                int length = in.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = in.read(buffer);
                }*/
                Utils.readBytes(in,out::write);
                out.closeEntry();
                if(withResources){
                    Stream.of(Collections.list(apk.entries()))
                        .filter(ze->!ze.getName().matches("classes[0-9]+\\.dex"))
                        .forEach(ze->{
                            try{
                                out.putNextEntry(ze);
                                Utils.readBytes(apk.getInputStream(ze),out::write);
                                out.closeEntry();
                            }catch(Throwable e){
                                WisecraftError.report("DebugBridge",e);
                            }
                        });
                }
            } finally {
                out.close();
            }
            if (!tmp.setReadOnly()) {
                throw new IOException("Failed to mark readonly \"" + tmp.getAbsolutePath() +
                        "\" (tmp of \"" + extractTo.getAbsolutePath() + "\")");
            }
            Log.i(TAG, "Renaming to " + extractTo.getPath());
            if (!tmp.renameTo(extractTo)) {
                throw new IOException("Failed to rename \"" + tmp.getAbsolutePath() +
                        "\" to \"" + extractTo.getAbsolutePath() + "\"");
            }
        } finally {
            closeQuietly(in);
            tmp.delete(); // return status ignored
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w(TAG, "Failed to close resource", e);
        }
    }
}
final class ZipUtil {
    static class CentralDirectory {
        long offset;
        long size;
    }

    private static final int ENDHDR = 22;
    private static final int ENDSIG = 0x6054b50;

    private static final int BUFFER_SIZE = 0x4000;

    static long getZipCrc(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);

            return computeCrcOfCentralDir(raf, dir);
        } finally {
            raf.close();
        }
    }

    static CentralDirectory findCentralDirectory(RandomAccessFile raf) throws IOException,
            ZipException {
        long scanOffset = raf.length() - ENDHDR;
        if (scanOffset < 0) {
            throw new ZipException("File too short to be a zip file: " + raf.length());
        }

        long stopOffset = scanOffset - 0x10000 /* ".ZIP file comment"'s max length */;
        if (stopOffset < 0) {
            stopOffset = 0;
        }

        int endSig = Integer.reverseBytes(ENDSIG);
        while (true) {
            raf.seek(scanOffset);
            if (raf.readInt() == endSig) {
                break;
            }

            scanOffset--;
            if (scanOffset < stopOffset) {
                throw new ZipException("End Of Central Directory signature not found");
            }
        }

        raf.skipBytes(2);
        raf.skipBytes(2);
        raf.skipBytes(2);
        raf.skipBytes(2);
        CentralDirectory dir = new CentralDirectory();
        dir.size = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        dir.offset = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        return dir;
    }

    static long computeCrcOfCentralDir(RandomAccessFile raf, CentralDirectory dir)
            throws IOException {
        CRC32 crc = new CRC32();
        long stillToRead = dir.size;
        raf.seek(dir.offset);
        int length = (int) Math.min(BUFFER_SIZE, stillToRead);
        byte[] buffer = new byte[BUFFER_SIZE];
        length = raf.read(buffer, 0, length);
        while (length != -1) {
            crc.update(buffer, 0, length);
            stillToRead -= length;
            if (stillToRead == 0) {
                break;
            }
            length = (int) Math.min(BUFFER_SIZE, stillToRead);
            length = raf.read(buffer, 0, length);
        }
        return crc.getValue();
    }
}
