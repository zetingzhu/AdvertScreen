package ad.vipcare.com.eventbus;

/**
 * Created by zeting
 * Date 19/1/10.
 */

public class HideMenuEvent {
    private int type ;

    public HideMenuEvent(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
