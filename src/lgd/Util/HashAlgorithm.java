package lgd.Util;


/**
 * @author lgd
 * @date 2022/3/19 19:38
 */
public final class HashAlgorithm {

    public static long hash(final byte[] buf){
        int offset=0;
        //byte[] bytes = key.getBytes();
        int seed=0x7a43a1e9;
        int m2 = 0x2619cad4;
        MurmurHash3.LongPair out = new MurmurHash3.LongPair();
        MurmurHash3.murmurhash3_x64_128(buf, offset, buf.length, seed, out);
        long hashCode = out.val1+m2*out.val1;
        return hashCode;
    }
}
