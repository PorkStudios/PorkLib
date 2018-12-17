/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import net.daporkchop.lib.hash.util.Digest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class HashTest {
    private static final Map<Digest, String> EMPTY_HASHES = new HashMap<Digest, String>(){
        {
            this.put(Digest.MD5, "d41d8cd98f00b204e9800998ecf8427e");
            this.put(Digest.SHA1, "da39a3ee5e6b4b0d3255bfef95601890afd80709");
            this.put(Digest.SHA_256, "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
            this.put(Digest.SHA512, "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
            this.put(Digest.SHA3_256, "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a");
            this.put(Digest.SHA3_512, "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26");
            this.put(Digest.WHIRLPOOL, "19fa61d75522a4669b44e39c1d2e1726c530232130d407f89afee0964997f7a73e83be698b288febcf88e3e03c4f0757ea8964e59b63d93708b138cc42a66eb3");
        }
    };

    private static final Map<Digest, String> QUICK_BROWN_FOX = new HashMap<Digest, String>(){
        {
            this.put(Digest.MD5, "9e107d9d372bb6826bd81d3542a419d6");
            this.put(Digest.SHA1, "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");
            this.put(Digest.SHA_256, "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592");
            this.put(Digest.SHA512, "07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6");
            this.put(Digest.SHA3_256, "69070dda01975c8c120c3aada1b282394e7f032fa9cf32f4cb2259a0897dfc04");
            this.put(Digest.SHA3_512, "01dedd5de4ef14642445ba5f5b97c15e47b9ad931326e4b0727cd94cefc44fff23f07bf543139939b49128caf436dc1bdee54fcb24023a08d9403f9b4bf0d450");
            this.put(Digest.WHIRLPOOL, "b97de512e91e3828b40d2b0fdce9ceb3c4a71f9bea8d88e75c4fa854df36725fd2b52eb6544edcacd6f8beddfea403cb55ae31f03ad62a5ef54e42ee82c3fb35");
        }
    };

    @Test
    public void test()  {
        EMPTY_HASHES.forEach((digest, emptyHash) -> {
            String computed = digest.hash().toHex();
            if (!emptyHash.equals(computed))    {
                throw new IllegalStateException(String.format("Invalid empty hash! Expected %s but calculated %s", emptyHash, computed));
            }
        });
        QUICK_BROWN_FOX.forEach((digest, emptyHash) -> {
            String computed = digest.hash("The quick brown fox jumps over the lazy dog".getBytes()).toHex();
            if (!emptyHash.equals(computed))    {
                throw new IllegalStateException(String.format("Invalid hash for \"The quick brown fox...\"! Expected %s but calculated %s", emptyHash, computed));
            }
        });
    }
}
