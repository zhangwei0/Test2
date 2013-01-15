package com.wl.magz.downloads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;

import com.wl.magz.utils.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.net.Proxy;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Pair;

public class DownloadThread extends Thread {
    private static final String TAG = "DownloadThread";
    private Context mContext;
    private DownloadInfo mInfo;
    
    public DownloadThread(Context context, DownloadInfo info) {
        mContext = context;
        mInfo = info;
    }
    
    static class State {
        public String mFilename;
        public FileOutputStream mStream;
        public boolean mCountRetry = false;
        public int mRetryAfter = 0;
        public int mRedirectCount = 0;
        public String mNewUri;
        public boolean mGotData = false;
        public String mRequestUri;
        public long mTotalBytes = -1;
        public long mCurrentBytes = 0;
        public String mHeaderETag;
        public boolean mContinuingDownload = false;
        public long mBytesNotified = 0;
        public long mTimeLaseNotification = 0;
        
        
        public State(DownloadInfo info) {
            mRequestUri = info.mUri;
            mFilename = info.mFileName;
            mTotalBytes = info.mTotalBytes;
            mCurrentBytes = info.mCurrentBytes;
        }
    }

    
    private  static class InnerState {
        public String mHeaderContentLength;
        public String mHeaderContentDisposition;
        public String mHeaderContentLocation;
    }
    
    private class RetryDownload extends Throwable {}
    class StopRequestException extends Exception {
        public int mFinalStatus;
        public StopRequestException(int finalStatus, String message) {
            super(message);
            mFinalStatus = finalStatus;
        }
        public StopRequestException(int finalStatus, String message, Throwable throwable) {
            super(message, throwable);
            mFinalStatus = finalStatus;
        }
        public StopRequestException() {
            super("Unknown Exception");
            mFinalStatus = -1;
        }
    }
    
    private static final String USER_AGENT = "";
    private String userAgent() {
        String userAgent = mInfo.mUserAgent;
        if (userAgent == null) {
            userAgent = USER_AGENT;
        }
        return userAgent;
    }
    
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        State state = new State(mInfo);
        AndroidHttpClient client = null;
        PowerManager.WakeLock wakeLock = null;
//        final NetworkPolicyManager netPolicy = NetworkPolicyManager.getSystemService(mContext);
        final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        int finalStatus = Downloads.STATUS_UNKNOWN_ERROR;
        String errorMsg = null;
        try {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire();
//            netPolicy.registerListener(mPolicyListener);
            client = AndroidHttpClient.newInstance(userAgent(), mContext);
            
            boolean finished = false;
            while(!finished) {
                ConnRouteParams.setDefaultProxy(client.getParams(),null);
//                        Proxy.getPreferredHttpHost(mContext, state.mRequestUri));
                HttpGet request = new HttpGet(state.mRequestUri);
                try {
                    executeDownload(state, client, request);
                    finished = true;
                } catch (RetryDownload exc) {
                    
                } finally {
                    request.abort();
                    request = null;
                }
            }
            finalizeDestinationFile(state);
            finalStatus = Downloads.STATUS_SUCCESS;
        } catch (StopRequestException error) {
            errorMsg = error.getMessage();
            finalStatus = error.mFinalStatus;
        } catch (Throwable ex) {
            errorMsg = ex.getMessage();
            finalStatus = Downloads.STATUS_UNKNOWN_ERROR;
        } finally {
            if (client != null) {
                client.close();
                client = null;
            }
            cleanupDestination(state, finalStatus);
            notifyDownloadCompleted(finalStatus, state.mCountRetry, state.mRetryAfter,
                                    state.mGotData, state.mFilename, state.mNewUri, state.mMimeType, errorMsg);
            DownloadManager.getInstance().dequeueDownload(mInfo.mId);
//            netPolicy.unregisterListener(mPolicyListener);
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }
    
    private static final int BUFFER_SIZE = 0;
    private void executeDownload(State state, AndroidHttpClient client, HttpGet request)
            throws StopRequestException, RetryDownload {
        InnerState innerState = new InnerState();
        byte data[] = new byte[BUFFER_SIZE];
        setupDestinationFile(state, innerState);
        addRequestHeaders(state, request);
        if (state.mCurrentBytes == state.mTotalBytes) {
            return;
        }
        checkConnectivity();
        HttpResponse response = sendRequest(state, client, request);
        handleExceptionalStatus(state, innerState, response);
        processResponseHeaders(state, innerState, response);
        InputStream entityStream = openResponseEntity(state, response);
        transferData(state, innerState, data, entityStream);
        //TODO
    }
    
    private void setupDestinationFile(State state, InnerState innerState)
            throws StopRequestException {
        if (!TextUtils.isEmpty(state.mFilename)) {
            File f = new File(state.mFilename);
            if (f.exists()) {
                long fileLength = f.length();
                if (fileLength == 0) {
                    f.delete();
                    state.mFilename = null;
                } else if (mInfo.mETag == null && !mInfo.mNoIntegrity) {
                    f.delete();
                    throw new StopRequestException(Downloads.STATUS_CANNOT_RESUME, "");
                } else {
                    try {
                        state.mStream = new FileOutputStream(state.mFilename, true);
                    } catch (FileNotFoundException exc) {
                        throw new StopRequestException(Downloads.STATUS_FILE_ERROR, "");
                    }
                    state.mCurrentBytes = (int) fileLength;
                    if (mInfo.mTotalBytes != -1) {
                        innerState.mHeaderContentLength = Long.toString(mInfo.mTotalBytes);
                    }
                    state.mHeaderETag = mInfo.mETag;
                    state.mContinuingDownload = true;
                }
            }
        }
    }
    
    private void addRequestHeaders(State state, HttpGet request) {
        for (Pair<String, String> header : mInfo.getHeaders()) {
            request.addHeader(header.first, header.second);
        }
        if (state.mContinuingDownload) {
            if (state.mHeaderETag != null) {
                request.addHeader("If-Match", state.mHeaderETag);
            }
            request.addHeader("Range", "bytes=" + state.mCurrentBytes + "-";)
        }
    }
    
    private void checkConnectivity() throws StopRequestException {
//        mPolicyDirty = false;
        int networkUsable = mInfo.checkCanUseNetwork();
        if (networkUsable != DownloadInfo.NETWORK_OK) {
            int status = Downloads.STATUS_WAITING_FOR_NETWORK;
            if (networkUsable == DownloadInfo.NETWORK_UNUSABLE_DUE_TO_SIZE) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
                mInfo.notifyPauseDueToSize(true);
            } else if (networkUsable == DownloadInfo.NETWORK_RECOMMENDED_UNUSABLE) {
                status = Downloads.STATUS_QUEUED_FOR_WIFI;
                mInfo.notifyPauseDueToSize(false);
            } else if (networkUsable == DownloadInfo.NETWORK_BLOCKED) {
                status = Downloads.STATUS_BLOCKED;
            }
            throw new StopRequestException(status,"");
        }
    }
    
    private HttpResponse sendRequest(State state, AndroidHttpClient client, HttpGet request) 
            throws StopRequestException {
        try {
            return client.execute(request);
        } catch (IllegalArgumentException ex) {
            throw new StopRequestException(Downloads.STATUS_HTTP_DATA_ERROR, "");
        } catch (IOException ex) {
            throw new StopRequestException();
        }
    }
    
    private void handleExceptionalStatus(State state, InnerState innerState, HttpResponse response)
            throws StopRequestException, RetryDownload {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 503 && mInfo.mNumFailed < MAX_RETRIES) {
            handleServiceUnavailable(state, response);
        }
        if (statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307) {
            handleRedirect(state, response, statusCode);
        }
        int expectedStatus = state.mContinuingDownload ? 206 : STATUS_SUCCESS;
        if (statusCode != expectedStatus) {
            handleOtherStatus(state, innerState, statusCode);
        }
    }
    
    private void handleOtherStatus(State state, InnerState innerState, int statusCode)
            throws StopRequestException {
        if (statusCode == 416) {
            throw new IllegalStateException();
        }
        int finalStatus;
        if (statusCode >= 400 && statusCode < 600) {
            finalStatus = statusCode;
        } else if (statusCode >= 300 && statusCode < 400) {
            finalStatus = Downloads.STATUS_UNHANDLED_REDIRECT;
        } else if (state.mContinuingDownload && statusCode == Downloads.STATUS_SUCCESS) {
            finalStatus = Downloads.STATUS_CANNOT_RESUME;
        } else {
            finalStatus = Downloads.STATUS_UNHANDLED_HTTP_CODE;
        }
        throw new StopRequestException();
    }
    
    private void processResponseHeaders(State state, InnerState innerState, HttpResponse response)
            throws StopRequestException {
        if (state.mContinuingDownload) {
            return;
        }
        readResponseHeaders(state, innerState, response);
        
        state.mFileName =; //TODO
        try {
            state.mStream = new FileOutputStream(state.mFilename);
        } catch (FileNotFoundException exc) {
            throw new StopRequestException(Downloads.STATUS_FILE_ERROR, "");
        }
        
        updateDatabaseFromHeaders(state, innerState);
        checkConnectivity();
    }
    
    private void updateDatabaseFromHeaders(State state, InnerState innerState) {
        ContentValues values = new ContentValues();
        values.put(_DATA, state.mFilename);
        if (state.mHeaderETag != null) {
            values.put(ETAG, state.mHeaderETag);
        }
        values.put(COLUMN_TOTAL_BYTES, mInfo.mTotalBytes);
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
    }
    
    private void readResponseHeaders(State state, InnerState innerState, HttpResponse response)
            throws StopRequestException {
        Header header = response.getFirstHeader("Content-Disposition");
        if (header != null) {
            innerState.mHeaderContentDisposition = header.getValue();
        }
        header = response.getFirstHeader("Content-Location");
        if (header != null) {
            innerState.mHeaderContentLocation = header.getValue();
        }
        header = response.getFirstHeader("ETag");
        if (header != null) {
            state.mHeaderETag = header.getValue();
        }
        String headerTransferEncoding = null;
        header = response.getFirstHeader("Transfer-Encoding");
        if (header != null) {
            headerTransferEncoding = header.getValue();
        }
        if (headerTransferEncoding == null) {
            header = response.getFirstHeader("Content-Length");
            if (header != null) {
                innerState.mHeaderContentLength = header.getValue();
                state.mTotalBytes = mInfo.mTotalBytes =
                        Long.parseLong(innerState.mHeaderContentLength);
            }
        }
        boolean noSizeInfo = innerState.mHeaderContentLength == null
                && (headerTransferEncoding == null
                        || !headerTransferEncoding.equalsIgnoreCase("chunked"));
        if (!mInfo.mNoIntegrity && noSizeInfo) {
            throw new StopRequestException(Downloads.STATUS_HTTP_DATA_ERROR, "");
        }
    }
    
    private InputStream openResponseEntity(State state, HttpResponse response)
            throws StopRequestException {
        try {
            return response.getEntity().getContent();
        } catch (IOException exc) {
            throw new StopRequestException();
        }
    }
    
    private void transferData(State state, InnerState innerState, byte[] data, InputStream entityStream)
            throws StopRequestException {
        for (;;) {
            int bytesRead = readFromResponse(state, innerState, data, entityStream);
            if (bytesRead == -1) {
                handleEndOfStream(state, innerState);
                return;
            }
            
            state.mGotData = true;
            writeDataToDestination(state, data, bytesRead);
            state.mCurrentBytes += bytesRead;
            reportProgress(state, innerState);
            checkPausedOrCanceled(state);
        }
    }
    
    private void finalizeDestinationFile(State state) throws StopRequestException {
        if (state.mFilename != null) {
            syncDestination(state);
        }
    }
    
    private void syncDestination(State state) {
        FileOutputStream downloadedFileStream = null;
        try {
            downloadedFileStream = new FileOutputStream(state.mFilename, true);
            downloadedFileStream.getFD().sync();
        } catch (Exception e) {
            
        } finally {
            if (downloadedFileStream != null) {
                try {
                    downloadedFileStream.close();
                } catch (Exception e) {
                    
                }
            }
        }
    }
    
    private void cleanupDestination(State state, int finalStatus) {
        closeDestination(state);
        if (state.mFilename != null && isStatusError(finalStatus)) {
            new File(state.mFilename).delete();
            state.mFilename = null;
        }
    }
    
    private boolean isStatusError(int status) {
        return (status >= 400 && status < 600);
    }
    
    private void closeDestination(State state) {
        try {
            if (state.mStream != null) {
                state.mStream.close();
                state.mStream = null;
            }
        } catch (Exception e) {
            
        }
    }
    
    private void handleEndofStream(State state, InnerState innerState)
            throws StopRequestException {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_BYTES, state.mCurrentBytes);
        if (innerState.mHeaderContentLength == null) {
            values.put(COLUMN_TOTAL_BYTES, state.mCurrentBytes);
        }
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
        boolean lengthMismatched = (innerState.mHeaderContentLength != null)
                && (state.mCurrentBytes != Integer.parseInt(innerState.mHeaderContentLength));
        if (lengthMismatched) {
            if (cannotResume(state)) {
                throw new StopRequestException(Downloads.STATUS_CANNOT_RESUME, "");
            } else {
                throw new StopRequestException();
            }
        }
    }
    
    private boolean cannotResume(State state) {
        return state.mCurrentBytes > 0 && !mInfo.mNoIntegrity && state.mHeaderETag == null);
    }
    
    private void writeDataToDestination(State state, byte[] data, int bytesRead)
            throws StopReuqestException {
        for (;;) {
            try {
                if (state.mStream == null) {
                    state.mStream = new FileOutputStream(state.mFilename);
                }
                state.mStream.write(data, 0, bytesRead);
                return;
            } catch (IOException ex) {
            } finally {
                closeDestination(state);
            }
            
        }
    }
    

    private void reportProgress(State state, InnerState innerState) {
        long now = System.currentTimeMillis();
        if (state.mCurrentBytes = state.mBytesNotified > Constant.MIN_PROGRESS_STEP
                && now - state.mTimeLastNotification > Constant.MIN_PROGRESS_TIME) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CURRENT_BYTES, state.mCurrentBytes);
            mContext.getContentResolver().update(mInfo.getAllDOwnloadsUri(), values, null, null);
            state.mBytesNotified = state.mCurrentBytes;
            state.mTimeLastNotification = now;
        }
    }
    
    private void checkPausedOrCanceled(State state) throws StopRequestException {
        synchronized(mInfo) {
            if (mInfo.mControl == Downloads.CONTROL_PAUSED) {
                throw new StopRequestException(Downloads.STATUS_PAUSED_BY_APP, "");
            }
            if (mInfo.mStatus == DonwloadStatus.STATUS_CANCELED) {
                throw new StopRequestException(Downloads.STATUS_CANCELED, "");
            }
        }
    }
    
    private int readFromResponse(State state, InnerState innerState, byte[] data,
                InputStream entityStream) throws StopRequestException {
        try {
            return entityStream.read(data);
        } catch (IOException ex) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CURRENT_BYTES, state.mCurrentBytes);
            if (cannotResume(state)) {
                throw new StopRequestException(Downloads.STATUS_CANNOT_RESUME, "");
            } else {
                throw new StopRequestException();
            }
        }
    }
    
    private void notifyDownloadCompleted(
            int status, boolean countRetry, int retryAfter, boolean gotData,
            String filename, String uri, String mimeType, String errorMsg) {
        notifyThroughDatabase(
                status, countRetry, retryAfter, gotData, filename, uri, mimeType,
                errorMsg);
        if (Downloads.Impl.isStatusCompleted(status)) {
            mInfo.sendIntentIfRequested();
        }
    }

    private void notifyThroughDatabase(
            int status, boolean countRetry, int retryAfter, boolean gotData,
            String filename, String uri, String mimeType, String errorMsg) {
        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_STATUS, status);
        values.put(Downloads.Impl._DATA, filename);
        if (uri != null) {
            values.put(Downloads.Impl.COLUMN_URI, uri);
        }
        values.put(Downloads.Impl.COLUMN_MIME_TYPE, mimeType);
        values.put(Downloads.Impl.COLUMN_LAST_MODIFICATION, mSystemFacade.currentTimeMillis());
        values.put(Constants.RETRY_AFTER_X_REDIRECT_COUNT, retryAfter);
        if (!countRetry) {
            values.put(Constants.FAILED_CONNECTIONS, 0);
        } else if (gotData) {
            values.put(Constants.FAILED_CONNECTIONS, 1);
        } else {
            values.put(Constants.FAILED_CONNECTIONS, mInfo.mNumFailed + 1);
        }
        // save the error message. could be useful to developers.
        if (!TextUtils.isEmpty(errorMsg)) {
            values.put(Downloads.Impl.COLUMN_ERROR_MSG, errorMsg);
        }
        mContext.getContentResolver().update(mInfo.getAllDownloadsUri(), values, null, null);
    }
    
//    private INetworkPolicyListener mPolicyListener = new INetworkPolicyListener.Stub() {
//        @Override
//        public void onUidRulesChanged(int uid, int uidRules) {
//            // only someone like NPMS should only be calling us
//            mContext.enforceCallingOrSelfPermission(MANAGE_NETWORK_POLICY, Constants.TAG);
//
//            if (uid == mInfo.mUid) {
//                mPolicyDirty = true;
//            }
//        }
//
//        @Override
//        public void onMeteredIfacesChanged(String[] meteredIfaces) {
//            // only someone like NPMS should only be calling us
//            mContext.enforceCallingOrSelfPermission(MANAGE_NETWORK_POLICY, Constants.TAG);
//
//            mPolicyDirty = true;
//        }
//    };


}
