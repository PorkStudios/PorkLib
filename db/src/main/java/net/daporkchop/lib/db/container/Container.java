package net.daporkchop.lib.db.container;

import lombok.*;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.PorkDB;

import java.io.File;
import java.io.IOException;

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
        } else if (!this.file.getParentFile().mkdirs() && this.file.createNewFile())    {
            throw new IllegalStateException(String.format("Could not create file: %s", this.file.getAbsolutePath()));
        }
    }

    public abstract V getValue();

    protected boolean usesDirectory() {
        return true;
    }

    public abstract void save() throws IOException;

    public void close() throws IOException  {
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

        public final C buildIfPresent() throws IOException   {
            if (new File(this.db.getRoot(), this.name).exists())    {
                return this.buildImpl();
            } else {
                return null;
            }
        }

        public final C build() throws IOException    {
            return this.buildImpl();
        }

        protected abstract C buildImpl() throws IOException;
    }
}
