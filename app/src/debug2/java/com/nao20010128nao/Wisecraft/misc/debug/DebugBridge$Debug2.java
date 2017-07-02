package com.nao20010128nao.Wisecraft.misc.debug;

import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.util.*;
import com.nao20010128nao.Wisecraft.*;
import com.nao20010128nao.Wisecraft.misc.*;
import dalvik.system.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

class DebugBridge$Debug2 extends DebugBridge {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void init(Context ctx) {
        File groovyDex = new File(ctx.getCacheDir(), "groovy.zip");
        boolean shouldExpandGroovy = true;
        if (groovyDex.exists()) {
            // now we check CRC32 checksum
            CRC32 crc = new CRC32();
            Utils.readBytes(groovyDex, crc::update);
            shouldExpandGroovy = crc.getValue() != BuildConfig.GROOVY_CRC32;
        }
        if (shouldExpandGroovy) {
            // copy groovy into local
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(groovyDex);
                Utils.readBytes(ctx.getAssets().open("groovy.zip"), fos::write);
            } catch (Throwable e) {
                WisecraftError.report("DebugBridge", e);
            } finally {
                Utils.safeClose(fos);
            }
        }
        // now load it
    }

    @Override
    public void openDebugActivity(Context ctx) {

    }
}

final class MultiDex {

    static final String TAG = "MultiDex";

    private static final String OLD_SECONDARY_FOLDER_NAME = "secondary-dexes";

    private static final String CODE_CACHE_NAME = "code_cache";

    private static final String CODE_CACHE_SECONDARY_FOLDER_NAME = "secondary-dexes";

    private static final int MAX_SUPPORTED_SDK_VERSION = 20;

    private static final int MIN_SDK_VERSION = 4;

    private static final int VM_WITH_MULTIDEX_VERSION_MAJOR = 2;

    private static final int VM_WITH_MULTIDEX_VERSION_MINOR = 1;

    private static final String NO_KEY_PREFIX = "";

    private static final Set<File> installedApk = new HashSet<File>();

    private static final boolean IS_VM_MULTIDEX_CAPABLE =
            isVMMultidexCapable(System.getProperty("java.vm.version"));

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

    /**
     * @param mainContext context used to get filesDir, to save preference and to get the
     * classloader to patch.
     * @param sourceApk Apk file.
     * @param dataDir data directory to use for code cache simulation.
     * @param secondaryFolderName name of the folder for storing extractions.
     * @param prefsKeyPrefix prefix of all stored preference keys.
     */
    private static void doInstallation(Context mainContext, File sourceApk, File dataDir,
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

            /* The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
            ClassLoader loader;
            try {
                loader = mainContext.getClassLoader();
            } catch (RuntimeException e) {
                /* Ignore those exceptions so that we don't break tests relying on Context like
                 * a android.test.mock.MockContext or a android.content.ContextWrapper with a
                 * null base Context.
                 */
                Log.w(TAG, "Failure while trying to obtain Context class loader. " +
                        "Must be running in test mode. Skip patching.", e);
                return;
            }
            if (loader == null) {
                // Note, the context class loader is null when running Robolectric tests.
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

    private static ApplicationInfo getApplicationInfo(Context context) {
        try {
            /* Due to package install races it is possible for a process to be started from an old
             * apk even though that apk has been replaced. Querying for ApplicationInfo by package
             * name may return information for the new apk, leading to a runtime with the old main
             * dex file and new secondary dex files. This leads to various problems like
             * ClassNotFoundExceptions. Using context.getApplicationInfo() should result in the
             * process having a consistent view of the world (even if it is of the old world). The
             * package install races are eventually resolved and old processes are killed.
             */
            return context.getApplicationInfo();
        } catch (RuntimeException e) {
            /* Ignore those exceptions so that we don't break tests relying on Context like
             * a android.test.mock.MockContext or a android.content.ContextWrapper with a null
             * base Context.
             */
            Log.w(TAG, "Failure while trying to obtain ApplicationInfo from Context. " +
                    "Must be running in test mode. Skip patching.", e);
            return null;
        }
    }

    /**
     * Identifies if the current VM has a native support for multidex, meaning there is no need for
     * additional installation by this library.
     * @return true if the VM handles multidex
     */
    /* package visible for test */
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
                    // let isMultidexCapable be false
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
            if (Build.VERSION.SDK_INT >= 19) {
                V19.install(loader, files, dexDir);
            } else if (Build.VERSION.SDK_INT >= 14) {
                V14.install(loader, files, dexDir);
            } else {
                V4.install(loader, files);
            }
        }
    }

    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the field into.
     * @param name field name
     * @return a field object
     * @throws NoSuchFieldException if the field cannot be located
     */
    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);


                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     *
     * @param instance an object to search the method into.
     * @param name method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
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
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " +
                Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    /**
     * Replace the value of a field containing a non null array, by a new array containing the
     * elements of the original array plus the elements of extraElements.
     * @param instance the instance whose field is to be modified.
     * @param fieldName the field to modify.
     * @param extraElements elements to append at the end of the array.
     */
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
            /* If we can't emulate code_cache, then store to filesDir. This means abandoning useless
             * files on disk if the device ever updates to android 5+. But since this seems to
             * happen only on some devices running android 2, this should cause no pollution.
             */
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

    /**
     * Installer for platform versions 19.
     */
    private static final class V19 {

        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            /* The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
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

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         */
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

    /**
     * Installer for platform versions 14, 15, 16, 17 and 18.
     */
    private static final class V14 {

        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            /* The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList,
                    new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         */
        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }
    }

    /**
     * Installer for platform versions 4 to 13.
     */
    private static final class V4 {
        private static void install(ClassLoader loader,
                                    List<? extends File> additionalClassPathEntries)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, IOException {
            /* The patched class loader is expected to be a descendant of
             * dalvik.system.DexClassLoader. We modify its
             * fields mPaths, mFiles, mZips and mDexs to append additional DEX
             * file entries.
             */
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

    /**
     * Zip file containing one secondary dex file.
     */
    private static class ExtractedDex extends File {
        public long crc = NO_VALUE;

        public ExtractedDex(File dexDir, String fileName) {
            super(dexDir, fileName);
        }
    }

    private static final String TAG = MultiDex.TAG;

    /**
     * We look for additional dex files named {@code classes2.dex},
     * {@code classes3.dex}, etc.
     */
    private static final String DEX_PREFIX = "classes";
    private static final String DEX_SUFFIX = ".dex";

    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";
    private static final int MAX_EXTRACT_ATTEMPTS = 3;

    private static final String PREFS_FILE = "multidex.version";
    private static final String KEY_TIME_STAMP = "timestamp";
    private static final String KEY_CRC = "crc";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final String KEY_DEX_CRC = "dex.crc.";
    private static final String KEY_DEX_TIME = "dex.time.";

    /**
     * Size of reading buffers.
     */
    private static final int BUFFER_SIZE = 0x4000;
    /* Keep value away from 0 because it is a too probable time stamp value */
    private static final long NO_VALUE = -1L;

    private static final String LOCK_FILENAME = "MultiDex.lock";

    /**
     * Extracts application secondary dexes into files in the application data
     * directory.
     *
     * @return a list of files that were created. The list may be empty if there
     * are no secondary dex files. Never return null.
     * @throws IOException if encounters a problem while reading or writing
     *                     secondary dex files
     */
    static List<? extends File> load(Context context, File sourceApk, File dexDir,
                                     String prefsKeyPrefix,
                                     boolean forceReload) throws IOException {
        Log.i(TAG, "MultiDexExtractor.load(" + sourceApk.getPath() + ", " + forceReload + ", " +
                prefsKeyPrefix + ")");

        long currentCrc = getZipCrc(sourceApk);

        // Validity check and extraction must be done only while the lock file has been taken.
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
                    // Exception while releasing the lock is bad, we want to report it, but not at
                    // the price of overriding any already pending exception.
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

    /**
     * Load previously extracted secondary dex files. Should be called only while owning the lock on
     * {@link #LOCK_FILENAME}.
     */
    private static List<ExtractedDex> loadExistingExtractions(
            Context context, File sourceApk, File dexDir,
            String prefsKeyPrefix)
            throws IOException {
        Log.i(TAG, "loading existing secondary dex files");

        final String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        int totalDexNumber = multiDexPreferences.getInt(prefsKeyPrefix + KEY_DEX_NUMBER, 1);
        final List<ExtractedDex> files = new ArrayList<ExtractedDex>(totalDexNumber - 1);

        for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
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


    /**
     * Compare current archive and crc with values stored in {@link SharedPreferences}. Should be
     * called only while owning the lock on {@link #LOCK_FILENAME}.
     */
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
            // never return NO_VALUE
            computedValue--;
        }
        return computedValue;
    }

    private static List<ExtractedDex> performExtractions(File sourceApk, File dexDir)
            throws IOException {

        final String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

        // Ensure that whatever deletions happen in prepareDexDir only happen if the zip that
        // contains a secondary dex file in there is not consistent with the latest apk.  Otherwise,
        // multi-process race conditions can cause a crash loop where one process deletes the zip
        // while another had created it.
        prepareDexDir(dexDir, extractedFilePrefix);

        List<ExtractedDex> files = new ArrayList<ExtractedDex>();

        final ZipFile apk = new ZipFile(sourceApk);
        try {

            int secondaryNumber = 2;

            ZipEntry dexFile = apk.getEntry(DEX_PREFIX + secondaryNumber + DEX_SUFFIX);
            while (dexFile != null) {
                String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
                ExtractedDex extractedFile = new ExtractedDex(dexDir, fileName);
                files.add(extractedFile);

                Log.i(TAG, "Extraction is needed for file " + extractedFile);
                int numAttempts = 0;
                boolean isExtractionSuccessful = false;
                while (numAttempts < MAX_EXTRACT_ATTEMPTS && !isExtractionSuccessful) {
                    numAttempts++;

                    // Create a zip file (extractedFile) containing only the secondary dex file
                    // (dexFile) from the apk.
                    extract(apk, dexFile, extractedFile, extractedFilePrefix);

                    // Read zip crc of extracted dex
                    try {
                        extractedFile.crc = getZipCrc(extractedFile);
                        isExtractionSuccessful = true;
                    } catch (IOException e) {
                        isExtractionSuccessful = false;
                        Log.w(TAG, "Failed to read crc from " + extractedFile.getAbsolutePath(), e);
                    }

                    // Log size and crc of the extracted zip file
                    Log.i(TAG, "Extraction " + (isExtractionSuccessful ? "succeeded" : "failed") +
                            " - length " + extractedFile.getAbsolutePath() + ": " +
                            extractedFile.length() + " - crc: " + extractedFile.crc);
                    if (!isExtractionSuccessful) {
                        // Delete the extracted file
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

    /**
     * Save {@link SharedPreferences}. Should be called only while owning the lock on
     * {@link #LOCK_FILENAME}.
     */
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
        /* Use commit() and not apply() as advised by the doc because we need synchronous writing of
         * the editor content and apply is doing an "asynchronous commit to disk".
         */
        edit.commit();
    }

    /**
     * Get the MuliDex {@link SharedPreferences} for the current application. Should be called only
     * while owning the lock on {@link #LOCK_FILENAME}.
     */
    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE,
                Build.VERSION.SDK_INT < 11 /* Build.VERSION_CODES.HONEYCOMB */
                        ? Context.MODE_PRIVATE
                        : Context.MODE_PRIVATE | 0x0004 /* Context.MODE_MULTI_PROCESS */);
    }

    /**
     * This removes old files.
     */
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

    private static void extract(ZipFile apk, ZipEntry dexFile, File extractTo,
                                String extractedFilePrefix) throws IOException, FileNotFoundException {

        InputStream in = apk.getInputStream(dexFile);
        ZipOutputStream out = null;
        // Temp files must not start with extractedFilePrefix to get cleaned up in prepareDexDir()
        File tmp = File.createTempFile("tmp-" + extractedFilePrefix, EXTRACTED_SUFFIX,
                extractTo.getParentFile());
        Log.i(TAG, "Extracting " + tmp.getPath());
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)));
            try {
                ZipEntry classesDex = new ZipEntry("classes.dex");
                // keep zip entry time since it is the criteria used by Dalvik
                classesDex.setTime(dexFile.getTime());
                out.putNextEntry(classesDex);

                byte[] buffer = new byte[BUFFER_SIZE];
                int length = in.read(buffer);
                while (length != -1) {
                    out.write(buffer, 0, length);
                    length = in.read(buffer);
                }
                out.closeEntry();
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

    /**
     * Closes the given {@code Closeable}. Suppresses any IO exceptions.
     */
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

    /* redefine those constant here because of bug 13721174 preventing to compile using the
     * constants defined in ZipFile */
    private static final int ENDHDR = 22;
    private static final int ENDSIG = 0x6054b50;

    /**
     * Size of reading buffers.
     */
    private static final int BUFFER_SIZE = 0x4000;

    /**
     * Compute crc32 of the central directory of an apk. The central directory contains
     * the crc32 of each entries in the zip so the computed result is considered valid for the whole
     * zip file. Does not support zip64 nor multidisk but it should be OK for now since ZipFile does
     * not either.
     */
    static long getZipCrc(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);

            return computeCrcOfCentralDir(raf, dir);
        } finally {
            raf.close();
        }
    }

    /* Package visible for testing */
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
        // Read the End Of Central Directory. ENDHDR includes the signature
        // bytes,
        // which we've already read.

        // Pull out the information we need.
        raf.skipBytes(2); // diskNumber
        raf.skipBytes(2); // diskWithCentralDir
        raf.skipBytes(2); // numEntries
        raf.skipBytes(2); // totalNumEntries
        CentralDirectory dir = new CentralDirectory();
        dir.size = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        dir.offset = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        return dir;
    }

    /* Package visible for testing */
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