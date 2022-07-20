package encrypt.pck;

import com.spoon.pass.encrypt.Encrypt;
import com.spoon.pass.encrypt.EncryptField;


@Encrypt(randomPsw = true)
public class JiaMi {
    @EncryptField(src = "wBoUPtps7bLwU5xddHu9qMTzdsScLZ3MI9EwyECTk0ZLxKMBVdpTTPBosSTFDrbuzxb3-4CmK5Fo-QR8DNl-15")
    public static String applovin_sdk_key;

    @EncryptField(src = "https://firmusic.oss-us-west-1.aliyuncs.com/")
    public static String configBaseUrl;

    @EncryptField(src = "cn-hk-sg-in-es-ru-jp-kr-tur-uk-tr-jb-ind-chn-fr-fra-gb")
    public static String ban;

    @EncryptField(src = "hi-ta-te-mr-pa-ml-ja-ko-ru-tr")
    public static String speciallanguage;



}
