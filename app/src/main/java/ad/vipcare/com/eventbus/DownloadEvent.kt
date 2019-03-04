package ad.vipcare.com.eventbus

/**
 * Created by zeting
 * Date 19/1/10.
 */

class DownloadEvent {
    var type: Int = 0// 1,开始显示对话框，2，更新对话框，3，结束对话框并且解析文件，4，拷贝文件失败 , 5, 结束对话框

    var progress: Int = 0
    var title: String = ""

    constructor(type: Int , progress: Int ) {
        this.progress = progress
        this.type = type
    }

    constructor(type: Int) {
        this.type = type
    }

    constructor(type: Int , title: String) {
        this.title = title
        this.type = type
    }

    constructor(type: Int , progress: Int, title: String ) {
        this.progress = progress
        this.title = title
        this.type = type
    }


}
