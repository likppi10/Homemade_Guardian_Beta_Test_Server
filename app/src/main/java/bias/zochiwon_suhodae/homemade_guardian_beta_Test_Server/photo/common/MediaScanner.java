package bias.zochiwon_suhodae.homemade_guardian_beta_Test_Server.photo.common;

import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

// 안쓰는듯?

public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection _msc;
    private File _file;

//    public MediaScanner(Context context, File f) {
//        _file = f;
//        _msc = new MediaScannerConnection(context, this);
//        _msc.connect();
//
//    }

    @Override
    public void onMediaScannerConnected() {
        _msc.scanFile(_file.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        _msc.disconnect();
    }
}