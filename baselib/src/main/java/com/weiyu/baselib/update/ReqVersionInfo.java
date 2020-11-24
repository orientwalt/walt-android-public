package com.weiyu.baselib.update;

public class ReqVersionInfo {


    /**
     * num : 200
     * msg : 请求成功
     * dataT : {"version":"1.0","url":"http://192.168.10.252:9999/tigase-patch/imgupload/app-release.apk"}
     */

    private int num;
    private String msg;
    private DataTBean dataT;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataTBean getDataT() {
        return dataT;
    }

    public void setDataT(DataTBean dataT) {
        this.dataT = dataT;
    }

    public static class DataTBean {
        /**
         * isCompellentUpdate: 1
         * updateContent: "1. 更新了群聊"
         * version : 1.0
         * url : http://192.168.10.252:9999/tigase-patch/imgupload/app-release.apk
         */

        private int isCompellentUpdate;//是否强制更新  0：否 1：是
        private String updateContent;//更新日志
        private String version;
        private String url;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getIsCompellentUpdate() {
            return isCompellentUpdate;
        }

        public void setIsCompellentUpdate(int isCompellentUpdate) {
            this.isCompellentUpdate = isCompellentUpdate;
        }

        public String getUpdateContent() {
            return updateContent;
        }

        public void setUpdateContent(String updateContent) {
            this.updateContent = updateContent;
        }
    }
}
