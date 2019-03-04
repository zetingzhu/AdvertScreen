package ad.vipcare.com.bean;

import java.util.List;

/**
 * Created by zeting
 * Date 19/1/15.
 */

public class AdvertsListBean {

    private boolean isVodeo = false ;//是否有视频文件
    private String anqiMd5 = "" ;//是否有视频文件
    List<AdvertInfoBean> adList ;

    public List<AdvertInfoBean> getAdverList() {
        return adList;
    }

    public void setAdverList(List<AdvertInfoBean> adverList) {
        this.adList = adverList;
    }

    public String getAnqiMd5() {
        return anqiMd5;
    }

    public void setAnqiMd5(String anqiMd5) {
        this.anqiMd5 = anqiMd5;
    }

    public boolean isVodeo() {
        return isVodeo;
    }

    public void setVodeo(boolean vodeo) {
        isVodeo = vodeo;
    }

    @Override
    public String toString() {
        return "AdvertsListBean{" +
                "adList=" + adList +
                ", isVodeo=" + isVodeo +
                ", anqiMd5='" + anqiMd5 + '\'' +
                '}';
    }
}
