package net.daporkchop.lib.crypto.cipher;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.hash.util.Digest;

import java.util.function.BiFunction;

/**
 * Used for setting a starting IV when initializing a cipher.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public enum CipherInitSide {
    SERVER((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    CLIENT((iv, recv) -> {
        if (recv) {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(b, iv).getHash();
            for (int i = b.length - 1; i >= 0; i--) {
                b[i] = hash[i % hash.length];
            }
            return b;
        } else {
            byte[] b = new byte[iv.length];
            byte[] hash = Digest.SHA3_256.hash(iv, b).getHash();
            for (int i = 0; i < b.length; i++) {
                b[i] = hash[i % hash.length];
            }
            return b;
        }
    }),
    /**
     * Should only be used if data is to be read and written in the same direction (e.g. for encrypting a file)
     */
    ONE_WAY((iv, recv) -> iv.clone());

    @NonNull
    public final BiFunction<byte[], Boolean, byte[]> ivSetter;
}
