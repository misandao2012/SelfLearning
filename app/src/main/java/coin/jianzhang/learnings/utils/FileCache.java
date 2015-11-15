package coin.jianzhang.learnings.utils;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;

/**
 * Created by jianzhang on 10/17/15.
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images
        /*if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
        else
            cacheDir = context.getCacheDir();*/

        //use internal storage instead
        cacheDir = new File(context.getFilesDir(), "TTImages_cache");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        //String filename = String.valueOf(url.hashCode());
        String filename = URLEncoder.encode(url);
        File file = new File(cacheDir, filename);
        return file;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File file : files)
            file.delete();
    }
}
