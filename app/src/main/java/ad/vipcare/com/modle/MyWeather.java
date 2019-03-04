package ad.vipcare.com.modle;

import java.util.List;

/**
 * 返回城市天气
 */

public class MyWeather {

    private List<HeWeather6> HeWeather6 ;

    public List<MyWeather.HeWeather6> getHeWeather6() {
        return HeWeather6;
    }

    public void setHeWeather6(List<MyWeather.HeWeather6> heWeather6) {
        HeWeather6 = heWeather6;
    }

    public class HeWeather6{
        private List<Basic> basic ;
        private String status ;

        public List<Basic> getBasic() {
            return basic;
        }

        public void setBasic(List<Basic> basic) {
            this.basic = basic;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "HeWeather6{" +
                    "basic=" + basic +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public class Basic {
        private String cid ;// ":"CN101010100",
        private String location ;// ":"北京",
        private String parent_city ;// ":"北京",
        private String admin_area ;// ":"北京",
        private String cnty ;// ":"中国",
        private String lat ;// ":"39.90498734",
        private String lon ;// ":"116.4052887",
        private String tz ;// ":"+8.00",
        private String type ;// ":"city"

        public String getAdmin_area() {
            return admin_area;
        }

        public void setAdmin_area(String admin_area) {
            this.admin_area = admin_area;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getCnty() {
            return cnty;
        }

        public void setCnty(String cnty) {
            this.cnty = cnty;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getParent_city() {
            return parent_city;
        }

        public void setParent_city(String parent_city) {
            this.parent_city = parent_city;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTz() {
            return tz;
        }

        public void setTz(String tz) {
            this.tz = tz;
        }

        @Override
        public String toString() {
            return "Basic{" +
                    "admin_area='" + admin_area + '\'' +
                    ", cid='" + cid + '\'' +
                    ", location='" + location + '\'' +
                    ", parent_city='" + parent_city + '\'' +
                    ", cnty='" + cnty + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lon='" + lon + '\'' +
                    ", tz='" + tz + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyWeather{" +
                "HeWeather6=" + HeWeather6 +
                '}';
    }
}
