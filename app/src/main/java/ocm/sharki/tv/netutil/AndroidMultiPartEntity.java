package ocm.sharki.tv.netutil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class AndroidMultiPartEntity extends MultipartEntity {
    private final ProgressListener listener;

    public static class CountingOutputStream extends FilterOutputStream {
        private final ProgressListener listener;
        private long transferred = 0;

        public CountingOutputStream(OutputStream out, ProgressListener listener) {
            super(out);
            this.listener = listener;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
            this.transferred += (long) len;
            this.listener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            this.out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);
        }
    }

    public interface ProgressListener {
        void transferred(long j);
    }

    public AndroidMultiPartEntity(ProgressListener listener) {
        this.listener = listener;
    }

    public AndroidMultiPartEntity(HttpMultipartMode mode, ProgressListener listener) {
        super(mode);
        this.listener = listener;
    }

    public AndroidMultiPartEntity(HttpMultipartMode mode, String boundary, Charset charset, ProgressListener listener) {
        super(mode, boundary, charset);
        this.listener = listener;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream, this.listener));
    }
}
