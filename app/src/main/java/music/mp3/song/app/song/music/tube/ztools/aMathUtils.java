package music.mp3.song.app.song.music.tube.ztools;

public class aMathUtils {
    public static final String appKey = "Ob6PH5HuA8"; //如果放在app里，必须放在new conofig上面，否则调解码时appkey没有初始化

    private aMathUtils() {
    }

//    private static String base64Encode(byte[] data) {
//        return Base64.encodeToString(data, Base64.DEFAULT);
//    }
//
//    private static byte[] base64Decode(String data) throws IOException {
//        return Base64.decode(data.getBytes(), Base64.DEFAULT);
//    }
//
//    private static byte[] base64Decode(byte[] data) throws IOException {
//        return Base64.decode(data, Base64.DEFAULT);
//    }
//
//    /**
//     * Encrypt data with key.
//     *
//     * @param data
//     * @param key
//     * @return
//     */
//    public static byte[] encrypt(byte[] data, byte[] key) {
//        if (data.length == 0) {
//            return data;
//        }
//        return toByteArray(encrypt(toIntArray(data, true), toIntArray(key, false)), false);
//    }
//
//    public static String encryptWithBase64(byte[] data, byte[] key) {
//        return base64Encode(encrypt(data, key));
//    }
//
//    public static String encryptWithBase64(String data, byte[] key, String encoding)
//            throws UnsupportedEncodingException {
//        return base64Encode(encrypt(data.getBytes(encoding), key));
//    }
//
//    /**
//     * Decrypt data with key.
//     *
//     * @param data
//     * @param key
//     * @return
//     */
//    public static byte[] decrypt(byte[] data, byte[] key) {
//        if (data.length == 0) {
//            return data;
//        }
//        return toByteArray(decrypt(toIntArray(data, false), toIntArray(key, false)), true);
//    }
//
//    /**
//     * @param data
//     * @param key
//     * @return
//     * @throws IOException
//     */
//    public static String decryptWithBase64(String data, String key) {
//        try {
//            if (null == data || data.length() == 0) {
//                return "";
//            }
//            return new String(decrypt(base64Decode(data), key.getBytes()));
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    /**
//     * @param data
//     * @param key
//     * @return
//     * @throws IOException
//     */
//    public static byte[] decryptWithBase64(byte[] data, byte[] key) throws IOException {
//        if (data.length == 0) {
//            return data;
//        }
//        return decrypt(base64Decode(data), key);
//    }
//
//    /**
//     * Encrypt data with key.
//     *
//     * @param v
//     * @param k
//     * @return
//     */
//    public static int[] encrypt(int[] v, int[] k) {
//        int n = v.length - 1;
//
//        if (n < 1) {
//            return v;
//        }
//        if (k.length < 4) {
//            int[] key = new int[4];
//
//            System.arraycopy(k, 0, key, 0, k.length);
//            k = key;
//        }
//        int z = v[n], y = v[0], delta = 0x9E3779B9, sum = 0, e;
//        int p, q = 6 + 52 / (n + 1);
//
//        while (q-- > 0) {
//            sum = sum + delta;
//            e = sum >>> 2 & 3;
//            for (p = 0; p < n; p++) {
//                y = v[p + 1];
//                z = v[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
//            }
//            y = v[0];
//            z = v[n] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
//        }
//        return v;
//    }
//
//    /**
//     * Decrypt data with key.
//     *
//     * @param v
//     * @param k
//     * @return
//     */
//    public static int[] decrypt(int[] v, int[] k) {
//        int n = v.length - 1;
//
//        if (n < 1) {
//            return v;
//        }
//        if (k.length < 4) {
//            int[] key = new int[4];
//
//            System.arraycopy(k, 0, key, 0, k.length);
//            k = key;
//        }
//        int z = v[n], y = v[0], delta = 0x9E3779B9, sum, e;
//        int p, q = 6 + 52 / (n + 1);
//
//        sum = q * delta;
//        while (sum != 0) {
//            e = sum >>> 2 & 3;
//            for (p = n; p > 0; p--) {
//                z = v[p - 1];
//                y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
//            }
//            z = v[n];
//            y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
//            sum = sum - delta;
//        }
//        return v;
//    }
//
//    /**
//     * Convert byte array to int array.
//     *
//     * @param data
//     * @param includeLength
//     * @return
//     */
//    private static int[] toIntArray(byte[] data, boolean includeLength) {
//        int n = (((data.length & 3) == 0) ? (data.length >>> 2) : ((data.length >>> 2) + 1));
//        int[] result;
//
//        if (includeLength) {
//            result = new int[n + 1];
//            result[n] = data.length;
//        } else {
//            result = new int[n];
//        }
//        n = data.length;
//        for (int i = 0; i < n; i++) {
//            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
//        }
//        return result;
//    }
//
//    /**
//     * Convert int array to byte array.
//     *
//     * @param data
//     * @param includeLength
//     * @return
//     */
//    private static byte[] toByteArray(int[] data, boolean includeLength) {
//        int n = data.length << 2;
//
//        ;
//        if (includeLength) {
//            int m = data[data.length - 1];
//
//            if (m > n) {
//                return null;
//            } else {
//                n = m;
//            }
//        }
//        byte[] result = new byte[n];
//
//        for (int i = 0; i < n; i++) {
//            result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
//        }
//        return result;
//    }

}
