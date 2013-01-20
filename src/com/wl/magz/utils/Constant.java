package com.wl.magz.utils;

public class Constant {

    public static class DBConstant {
        public static final String ID = "_id";
        public static final String PRIVATE_KEY = "private_key";
        public static final String NAME = "name";
        public static final String PATH = "path";
        public static final String DOWNLOAD_COMPLETE = "download_complete";
        public static final String PROGRESS = "progress";
        public static final String READ_TIME = "read_time";
        


    }
    

    public static class Downloads {
       
        //Download status
        public static final int STATUS_UNKNOWN_ERROR = 0;
        public static final int STATUS_WAITING_FOR_NETWORK = 1;
        public static final int STATUS_QUEUED_FOR_WIFI = 2;
        public static final int STATUS_BLOCKED = 3;
        public static final int STATUS_SUCCESS = 4;
        public static final int STATUS_PAUSED_BY_APP = 5;
        public static final int STATUS_CANCELED = 6;
        public static final int STATUS_FILE_ERROR = 7;
        public static final int STATUS_CANNOT_RESUME = 8;
        public static final int STATUS_NOT_ACCEPTABLE = 9;
        public static final int STATUS_HTTP_DATA_ERROR = 10;
        public static final int STATUS_UNHANDLED_REDIRECT = 11;
        public static final int STATUS_UNHANDLED_HTTP_CODE = 12;
        public static final int STATUS_TOO_MANY_REDIRECTS = 13;
        public static final int STATUS_WAITING_TO_RETRY = 14;
        
        public static final int STATUS_RUNNING = 101;
        public static final int STATUS_PENDING = 102;
        public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 103;
        
        //User Control
        public static final int CONTROL_PAUSED = 201;
        public static final int CONTROL_RUN = 202;
        
        //DB Download
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CURRENT_BYTES = "current_bytes";
        public static final String COLUMN_TOTAL_BYTES = "total_bytes";
        public static final String COLUMN_URI= "uri";
        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_NUM_FAILED = "num_failed";
        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_ETAG = "etag";
        public static final String COLUMN_USER_AGENT = "user_agent";
        public static final String COLUMN_FAILED_CONNECTIONS = "failed_connections";
        public static final String COLUMN_RETRY_AFTER_X_REDIRECT_COUNT = "retry_after";
        public static final String COLUMN_ERROR_MSG = "error_msg";
        public static final String COLUMN_CONTROL = "control";
        
        public static final String COLUMN_HEADER = "header";
        public static final String COLUMN_VALUE = "value";
        
        
        public static final int MIN_PROGRESS_STEP = 4096;
        public static final long MIN_PROGRESS_TIME = 1500;
        
    }
}
