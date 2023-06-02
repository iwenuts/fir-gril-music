package music.mp3.song.app.song.music.tube.ztools;


import music.mp3.song.app.song.music.tube.bean.BaseBean;
import music.mp3.song.app.song.music.tube.referrer.ReferrerStream;

public final class Config extends BaseBean {
    //    public Integer feedbackRate = 0;
    public int dl_pop = 10; //ad弹出的可能性为 1/4，listen pop
    public int se_pop = 0; //ad弹出的可能性为 1/4，search pop

    public String uVer = "";
    public boolean uForce = false;
    public String uId = "";
    public String uInfo = "";
    public String webappurl = "";

    public String moreApps = "";
    public String cnx = "-0"; //country final
    //    public String ban = "cn-hk-sg-in-es-ru-jp-kr-tur-uk-tr-gb-ind-chn";
    public String ban = "cn-hk-in-jp-ru-sg-kr-es";
    public String speciallanguage = "";

    public String onlist = "3.0.0"; //上架开关
    public boolean xm = false; // xiami
    public boolean sc = false; // sound
    public boolean yt = true; // youtube
    public boolean jm = true; // jamendo
    public boolean mp3juice = false; // mp3juice

    public boolean free_mp3 = true;
    public boolean qq = true;
    public boolean wyy = true;
    public boolean nhac = true;
    public boolean archive = true;


    //    public boolean n2 = true; // normal 2tab
    public boolean ad = true;
    public int level = 0;

    public boolean showBanner1 = true;
    public boolean showBanner2 = false;
    public boolean showNative = true;
    public boolean showDaily = true;
    public boolean showGenre = true;

    public int fbArea = 0;
    public int big = 2;

    //    public String s = "r5ELVSy3RkcjX7ilaL7n2v1Z8irA9SL8";
    public int jamAppVersion = 79005380;
    public String so = "";
    public String sx = "";
    public int rewardpop = 5;
    //promote: weight%%super%%packageId%%title%%desc%%imgUrl
    public String promId = "";
    //            new String(Base64.decode("MjAlJTAlJWNvbS5wbGF5dHViZS52aWRlb3R1YmUudHViZXZpZGVvJSVUdWJlIFBsYXllciAtIEZsb2F0IFR1YmUlJVZpZGVvIFR1YmUsIE11c2ljIFR1YmUsIGEgZnJlZSBsaXRlIHRoaXJkIHBhcnR5IGNsaWVudCBmb3IgWW91VHViZSwgZWFzaWx5IGZpbmQgZ3JlYXQgdmlkZW9zIGFuZCBmcmVlIG11c2ljJSVodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vOGRvamd4T0RfTWFEbkUyZFBHY3Q5QjhGY1pVUjQ3SDdjbFpIWGdEYlhrQWotM3NBVlF3VDlMUWo3b0duYWlYRHJB".getBytes(), Base64.DEFAULT)) + //"20%%0%%com.playtube.videotube.tubevideo%%Tube Player - YouTube Player%%Float Tube, Video Tube, Music Tube is a free lite third party client for YouTube, easily find great videos and free music%%https://lh3.googleusercontent.com/8dojgxOD_MaDnE2dPGct9B8FcZUR47H7clZHXgDbXkAj-3sAVQwT9LQj7oGnaiXDrA" +
//                    new String(Base64.decode("fHwyMCUlMCUlY29tLmZyZWUubW92aWUudmlkZW8ucGxheWVyJSVIRCBNb3ZpZXMgRnJlZSAyMDE5JSVXYXRjaCBCZXN0IE1vdmllcyBvZiAyMDE5LCBTaG93Qm94ICYgTW92aWVCb3gsIERvd25sb2FkIG1vdmllcyBmYXN0IGFuZCBwbGF5IG9mZmxpbmUlJWh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9jWHNNcTN2NWVTTmV1LUtpd0ZyN2l1N2ZMQWRqNTVRZTA2Y0ZCa29id0VmdnQwYWxZYWREeGZHZzBLZE1YUEpjV1hZ".getBytes(), Base64.DEFAULT)) + //"||20%%0%%com.free.movie.video.player%%HD Movies Free 2019%%Watch Best Movies of 2019, ShowBox & MovieBox, Download movies fast and play offline%%https://lh3.googleusercontent.com/cXsMq3v5eSNeu-KiwFr7iu7fLAdj55Qe06cFBkobwEfvt0alYadDxfGg0KdMXPJcWXY" +
//                    new String(Base64.decode("fHwxMCUlMCUlZnJlZS5tcDMubXVzaWMuZG93bmxvYWQucmluZ3RvbmUubWFrZXIuY2FsbC5mbGFzaCUlRnJlZSBNdXNpYyBSaW5ndG9uZSAyMDE5JSVXYW50IHRvIG1ha2UgeW91ciBwaG9uZSBDb29sPyB0cnkgbWFrZSB3b25kZXJmdWwgbXVzaWMgcmluZ3RvbmVzIGZvciBmcmVlJSVodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vTkFlSm90M2xaWDByeVBFQ3NBOFI2Q2hkSEhuU1lYX3p2SGtxMF83SmtkaVFLMUNmeV9mdDFBTXZxU0RGdTJPQk52NA==".getBytes(), Base64.DEFAULT)) + //"||10%%0%%free.mp3.music.download.ringtone.maker.call.flash%%Free Music Ringtone 2019%%Want to make your phone Cool? try wonderful music ringtones free%%https://lh3.googleusercontent.com/NAeJot3lZX0ryPECsA8R6ChdHHnSYX_zvHkq0_7JkdiQK1Cfy_ft1AMvqSDFu2OBNv4" +
//                    new String(Base64.decode("fHwzMCUlMCUlY29tLmFuaW1lLmdpcmwubGl2ZS5oZC53YWxscGFwZXIuYXBwJSVBbGwgQW5pbWUgV2FsbHBhcGVyIEhEJSUxLDAwMCwwMDArIEhEIEFuaW1lIFdhbGxwYXBlcnMgRnJlZWx5LlNlYXJjaCwgRG93bmxvYWQsIFNldCBBbmltZSBHaXJsIFdhbGxwYXBlciUlaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2lfQlg4TU1jZWZoa184TVpJM0VCeElDbTFjUmVJWjFNNUZVMmhJbGFhN3J0UnFyeWFvYU45blVmMnhSbGkzUTNLaWdy".getBytes(), Base64.DEFAULT)) + //"||30%%0%%com.anime.girl.live.hd.wallpaper.app%%All Anime Wallpaper HD%%1,000,000+ HD Anime Wallpapers Freely.Search, Download, Set Anime Girl Wallpaper%%https://lh3.googleusercontent.com/i_BX8MMcefhk_8MZI3EBxICm1cReIZ1M5FU2hIlaa7rtRqryaoaN9nUf2xRli3Q3Kigr" +
//                    new String(Base64.decode("fHwxMCUlMCUlbmV3LmZyZWUubXVzaWMubXAzLmRvd25sb2FkLmdhbWUuc3R1ZGlvJSVVbmxpbWl0ZWQgTXAzIE11c2ljIERvd25sb2FkZXIlJUZyZWUgTXVzaWMgRG93bmxvYWQsIGVhc3kgdG8gZmluZCwgbGlzdGVuIHRvIGFuZCBkb3dubG9hZCBzb25ncyUlaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL3Y5MEV4c3NTWGdaOGF6WnpzaXRrSlFHRk1ZX3pnVXpoTVJTaWduSFFMNmR2S09LVXNnSXBuSDlIUDU5aUYwY0NibDA=".getBytes(), Base64.DEFAULT)); //"||10%%0%%new.free.music.mp3.download.game.studio%%Unlimited Mp3 Music Downloader%%Free Music Download, easy to find, listen to and download songs%%https://lh3.googleusercontent.com/v90ExssSXgZ8azZzsitkJQGFMY_zgUzhMRSignHQL6dvKOKUsgIpnH9HP59iF0cCbl0";
    public String tags = "Blinding Lights|The Box|Lean On Me|Don't Start Now|Circles|Life Is Good|Adore You|Say So|Intentions|everything i wanted|Toosie Slide|Someone You Loved|Lovely Day";//search tag

    public String devToken = "";
    public String linkId = "";
    public String pipeua = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0";
    public boolean isIpDeal = false;
    public boolean isBanDeal = false;
    public boolean isLocalSearchDeal = false;

    public String freemp3url = "";

    public ReferrerStream referrer;
//    public int playerinterval;

    public boolean toPlayer = true;
    public String wayp = "none";
    public int sni = 3;

    public ReferrerStream getUseReferrer() {
        return referrer;
    }
}
