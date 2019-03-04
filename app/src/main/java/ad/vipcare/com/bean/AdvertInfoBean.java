package ad.vipcare.com.bean;

import java.io.File;

/**
 * Created by zeting
 * Date 19/1/15.
 */

public class AdvertInfoBean {

    private String fileName;//文件名称
    private String fileFormat ;// 文件格式
    private String startDay  ; // 播放开始日期
    private String endDay  ; // 播放结束日期
    private int playType ;// 播放方式 0 时间段，1 次数
    private String startTime  ;// 播放开始时间
    private String endTime  ;// 播放结束时间
    private int showCount ;// 需要显示的总次数


    private int playCount ;// 播放次数
    private File file ; // 文件对象
    private String playTime ;// 播放时间
    private String adId ;// 广告id

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "AdvertInfoBean{" +
                "endDay='" + endDay + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", startDay='" + startDay + '\'' +
                ", playType=" + playType +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", showCount=" + showCount +
                ", playCount=" + playCount +
                ", file=" + file +
                '}';
    }
}
