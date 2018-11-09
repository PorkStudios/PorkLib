package net.daporkchop.lib.db.container;

import lombok.*;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOEConsumer;
import net.daporkchop.lib.db.PorkDB;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author DaPorkchop_
 */
public abstract class Container<V, B extends Container.Builder<V, ? extends Container<V, B>>> {
    protected final PorkDB db;
    protected final String name;
    protected final File file;

    public Container(@NonNull B builder) throws IOException {
        this.db = builder.db;
        this.name = builder.name;
        this.file = new File(db.getRoot(), this.name);
        if (this.file.exists()) {
            if (this.usesDirectory()) {
                if (!this.file.isDirectory()) {
                    throw new IllegalStateException(String.format("Not a directory: %s", this.file.getAbsolutePath()));
                }
            } else if (!this.file.isFile()) {
                throw new IllegalStateException(String.format("Not a file: %s", this.file.getAbsolutePath()));
            }
        } else if (this.usesDirectory() && !this.file.mkdirs()) {
            throw new IllegalStateException(String.format("Could not create directory: %s", this.file.getAbsolutePath()));
        } else {
            File parent = this.file.getParentFile();
            if ((parent.exists() && !parent.isDirectory()) || (!parent.exists() && !parent.mkdirs()) || !this.file.createNewFile()) {
                throw new IllegalStateException(String.format("Could not create file: %s", this.file.getAbsolutePath()));
            }
        }
    }

    public abstract V getValue();

    protected boolean usesDirectory() {
        return true;
    }

    public abstract void save() throws IOException;

    protected File getFile(@NonNull String name) throws IOException    {
        return this.getFile(name, null);
    }

    protected File getFile(@NonNull String name, IOEConsumer<DataOut> initializer) throws IOException    {
        if (!this.usesDirectory())  {
            throw new IllegalStateException();
        } else {
            File file = new File(this.file, name);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs())   {
                    throw new IOException(String.format("Could not create file: %s", parent.getAbsolutePath()));
                } else if (parent.exists() && !parent.isDirectory())    {
                    throw new IOException(String.format("Not a directory: %s", parent.getAbsolutePath()));
                } else if (!file.createNewFile())   {
                    throw new IOException(String.format("Could not create file: %s", file.getAbsolutePath()));
                } else if (initializer != null) {
                    try (DataOut out = DataOut.wrap(file))  {
                        initializer.acceptThrowing(out);
                    }
                }
            }
            return file;
        }
    }

    protected DataIn getIn(@NonNull String name) throws IOException {
        return this.getIn(name, null);
    }

    protected DataIn getIn(@NonNull String name, IOEConsumer<DataOut> initializer) throws IOException {
        return DataIn.wrap(this.getFile(name, initializer));
    }

    protected DataOut getOut(@NonNull String name) throws IOException {
        return this.getOut(name, null);
    }

    protected DataOut getOut(@NonNull String name, IOEConsumer<DataOut> initializer) throws IOException {
        return DataOut.wrap(this.getFile(name, initializer));
    }

    protected RandomAccessFile getRAF(@NonNull String name) throws IOException {
        return this.getRAF(name, null);
    }

    protected RandomAccessFile getRAF(@NonNull String name, IOEConsumer<DataOut> initializer) throws IOException {
        return new RandomAccessFile(this.getFile(name, initializer), "rw");
    }

    public void close() throws IOException {
        this.save();
    }

    @Accessors(chain = true)
    @Setter
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder<V, C extends Container<V, ? extends Builder<V, C>>> {
        @NonNull
        protected final PorkDB db;

        @NonNull
        protected final String name;

        public final C buildIfPresent() throws IOException {
            if (new File(this.db.getRoot(), this.name).exists()) {
                return this.buildImpl();
            } else {
                return null;
            }
        }

        public final C build() throws IOException {
            return this.buildImpl();
        }

        protected abstract C buildImpl() throws IOException;
    }
}
