package com.deerlive.zhuawawa.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2018/3/28.
 */

public class GameListBean implements Serializable {

    /**
     * code : 200
     * info : [{"id":"2","name":"注意力大挑战","img":"http://test.doll.anwenqianbao.com/data/upload/20180326/5ab8dba322069.png","create_time":"2018-03-26 19:38:20","desc":"这是注意力大挑战","type":"2","status":"1","style":"1","url":null},{"id":"1","name":"加减大师","img":"http://test.doll.anwenqianbao.com/data/upload/20180326/5ab8d6ac1f219.png","create_time":"2018-03-26 19:17:07","desc":"这是加减大","type":"1","status":"1","style":"1","url":null}]
     * descrp : success
     */

    private int code;
    private String descrp;
    private List<InfoBean> info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescrp() {
        return descrp;
    }

    public void setDescrp(String descrp) {
        this.descrp = descrp;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean implements Serializable{
        /**
         * id : 2
         * name : 注意力大挑战
         * img : http://test.doll.anwenqianbao.com/data/upload/20180326/5ab8dba322069.png
         * create_time : 2018-03-26 19:38:20
         * desc : 这是注意力大挑战
         * type : 2
         * status : 1
         * style : 1
         * url : null
         */

        private String id;
        private String name;
        private String img;
        private String create_time;
        private String desc;
        private String type;
        private String status;
        private String style;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
