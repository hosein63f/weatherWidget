package weather.widget.database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyDB {

    private Context context;

    public CopyDB(Context context) {
        this.context = context;
    }

    public void checkingExistDB() {
        File dbFile = context.getDatabasePath(WeatherDB.DB_NAME);
        if (!dbFile.exists()) {
            try {
                copyDBFile(dbFile);

            } catch (IOException e) {
                Log.e("db", e.getLocalizedMessage());
            }
        }
    }

    private void copyDBFile(File dbFile) throws IOException {

        InputStream is = context.getAssets().open(WeatherDB.DB_NAME);
        dbFile.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(dbFile);

        int len = 0;
        byte[] buff = new byte[1024];

        while ((len = is.read(buff)) > 0) {
            os.write(buff, 0, len);
        }
        is.close();
        os.flush();
        os.close();
    }
}
