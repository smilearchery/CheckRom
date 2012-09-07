
package com.dianxinos.checkromdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.pm.FeatureInfo;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.os.Environment;
import android.os.Process;
import android.util.SparseArray;
import android.util.Xml;

import com.android.internal.util.XmlUtils;

public class CheckPackage {
    int[] mGlobalGids;
    public static final SparseArray<HashSet<String>> mSystemPermissions = new SparseArray<HashSet<String>>();
    public static final HashMap<String, String> mSharedLibraries = new HashMap<String, String>();
    public static final HashMap<String, FeatureInfo> mAvailableFeatures = new HashMap<String, FeatureInfo>();
    public static final List<FeatureInfo> mFeatures = new ArrayList<FeatureInfo>();
    public static final List<String> mLibraries = new ArrayList<String>();

    public CheckPackage() {
        //readPermissions();
    }

    void readPermissions() {
        // Read permissions from .../etc/permission directory.
        File libraryDir = new File(Environment.getRootDirectory(), "etc/permissions");
        if (!libraryDir.exists() || !libraryDir.isDirectory()) {
            // Slog.w(TAG, "No directory " + libraryDir + ", skipping");
            return;
        }
        if (!libraryDir.canRead()) {
            // Slog.w(TAG, "Directory " + libraryDir + " cannot be read");
            return;
        }

        // Iterate over the files in the directory and scan .xml files
        for (File f : libraryDir.listFiles()) {
            // We'll read platform.xml last
            if (f.getPath().endsWith("etc/permissions/platform.xml")) {
                continue;
            }

            if (!f.getPath().endsWith(".xml")) {
                // Slog.i(TAG, "Non-xml file " + f + " in " + libraryDir +
                // " directory, ignoring");
                continue;
            }
            if (!f.canRead()) {
                // Slog.w(TAG, "Permissions library file " + f +
                // " cannot be read");
                continue;
            }

            readPermissionsFromXml(f);
        }

        // Read permissions from .../etc/permissions/platform.xml last so it
        // will take precedence
        final File permFile = new File(Environment.getRootDirectory(),
                "etc/permissions/platform.xml");
        readPermissionsFromXml(permFile);
    }

    private void readPermissionsFromXml(File permFile) {
        FileReader permReader = null;
        try {
            permReader = new FileReader(permFile);
        } catch (FileNotFoundException e) {
            // Slog.w(TAG, "Couldn't find or open permissions file " +
            // permFile);
            return;
        }

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(permReader);

            XmlUtils.beginDocument(parser, "permissions");

            while (true) {
                XmlUtils.nextElement(parser);
                if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
                    break;
                }

                String name = parser.getName();
                if ("group".equals(name)) {
                    String gidStr = parser.getAttributeValue(null, "gid");
                    if (gidStr != null) {
                        int gid = Integer.parseInt(gidStr);
                        mGlobalGids = appendInt(mGlobalGids, gid);
                    } else {
                        /*
                         * Slog.w(TAG, "<group> without gid at " +
                         * parser.getPositionDescription());
                         */
                    }

                    XmlUtils.skipCurrentTag(parser);
                    continue;
                } else if ("permission".equals(name)) {
                    String perm = parser.getAttributeValue(null, "name");
                    if (perm == null) {
                        /*
                         * Slog.w(TAG, "<permission> without name at " +
                         * parser.getPositionDescription());
                         */
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    perm = perm.intern();
                    readPermission(parser, perm);

                } else if ("assign-permission".equals(name)) {
                    String perm = parser.getAttributeValue(null, "name");
                    if (perm == null) {
                        /*
                         * Slog.w(TAG, "<assign-permission> without name at " +
                         * parser.getPositionDescription());
                         */
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    String uidStr = parser.getAttributeValue(null, "uid");
                    if (uidStr == null) {
                        /*
                         * Slog.w(TAG, "<assign-permission> without uid at " +
                         * parser.getPositionDescription());
                         */
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    int uid = Process.getUidForName(uidStr);
                    if (uid < 0) {
                        /*
                         * Slog.w(TAG, "<assign-permission> with unknown uid \""
                         * + uidStr + "\" at " +
                         * parser.getPositionDescription());
                         */
                        XmlUtils.skipCurrentTag(parser);
                        continue;
                    }
                    perm = perm.intern();
                    HashSet<String> perms = mSystemPermissions.get(uid);
                    if (perms == null) {
                        perms = new HashSet<String>();
                        mSystemPermissions.put(uid, perms);
                    }
                    perms.add(perm);
                    XmlUtils.skipCurrentTag(parser);

                } else if ("library".equals(name)) {
                    String lname = parser.getAttributeValue(null, "name");
                    String lfile = parser.getAttributeValue(null, "file");
                    if (lname == null) {
                        /*
                         * Slog.w(TAG, "<library> without name at " +
                         * parser.getPositionDescription());
                         */
                    } else if (lfile == null) {
                        /*
                         * Slog.w(TAG, "<library> without file at " +
                         * parser.getPositionDescription());
                         */
                    } else {
                        // Log.i(TAG, "Got library " + lname + " in " + lfile);
                        mSharedLibraries.put(lname, lfile);
                        mLibraries.add(lname);
                    }
                    XmlUtils.skipCurrentTag(parser);
                    continue;

                } else if ("feature".equals(name)) {
                    String fname = parser.getAttributeValue(null, "name");
                    if (fname == null) {
                        /*
                         * Slog.w(TAG, "<feature> without name at " +
                         * parser.getPositionDescription());
                         */
                    } else {
                        // Log.i(TAG, "Got feature " + fname);
                        FeatureInfo fi = new FeatureInfo();
                        fi.name = fname;
                        mAvailableFeatures.put(fname, fi);
                        mFeatures.add(fi);
                    }
                    XmlUtils.skipCurrentTag(parser);
                    continue;

                } else {
                    XmlUtils.skipCurrentTag(parser);
                    continue;
                }

            }
            permReader.close();
        } catch (XmlPullParserException e) {
            // Slog.w(TAG, "Got execption parsing permissions.", e);
        } catch (IOException e) {
            // Slog.w(TAG, "Got execption parsing permissions.", e);
        }
    }

    void readPermission(XmlPullParser parser, String name)
            throws IOException, XmlPullParserException {

        name = name.intern();

        /*
         * BasePermission bp = mSettings.mPermissions.get(name); if (bp == null)
         * { bp = new BasePermission(name, null, BasePermission.TYPE_BUILTIN);
         * mSettings.mPermissions.put(name, bp); }
         */
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG
                || parser.getDepth() > outerDepth)) {
            if (type == XmlPullParser.END_TAG
                    || type == XmlPullParser.TEXT) {
                continue;
            }

            String tagName = parser.getName();
            if ("group".equals(tagName)) {
                String gidStr = parser.getAttributeValue(null, "gid");
                if (gidStr != null) {
                    int gid = Process.getGidForName(gidStr);
                    // bp.gids = appendInt(bp.gids, gid);
                } else {
                    /*
                     * Slog.w(TAG, "<group> without gid at " +
                     * parser.getPositionDescription());
                     */
                }
            }
            XmlUtils.skipCurrentTag(parser);
        }
    }

    static int[] appendInt(int[] cur, int val) {
        if (cur == null) {
            return new int[] {
                    val
            };
        }
        final int N = cur.length;
        for (int i = 0; i < N; i++) {
            if (cur[i] == val) {
                return cur;
            }
        }
        int[] ret = new int[N + 1];
        System.arraycopy(cur, 0, ret, 0, N);
        ret[N] = val;
        return ret;
    }

    static int[] appendInts(int[] cur, int[] add) {
        if (add == null)
            return cur;
        if (cur == null)
            return add;
        final int N = add.length;
        for (int i = 0; i < N; i++) {
            cur = appendInt(cur, add[i]);
        }
        return cur;
    }

    static int[] removeInt(int[] cur, int val) {
        if (cur == null) {
            return null;
        }
        final int N = cur.length;
        for (int i = 0; i < N; i++) {
            if (cur[i] == val) {
                int[] ret = new int[N - 1];
                if (i > 0) {
                    System.arraycopy(cur, 0, ret, 0, i);
                }
                if (i < (N - 1)) {
                    System.arraycopy(cur, i + 1, ret, i, N - i - 1);
                }
                return ret;
            }
        }
        return cur;
    }

    static int[] removeInts(int[] cur, int[] rem) {
        if (rem == null)
            return cur;
        if (cur == null)
            return cur;
        final int N = rem.length;
        for (int i = 0; i < N; i++) {
            cur = removeInt(cur, rem[i]);
        }
        return cur;
    }

}
