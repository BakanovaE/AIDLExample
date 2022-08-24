package org.martellina.aidl.callback;

import org.martellina.aidl.base.AidlException;
import org.martellina.aidl.base.AidlResult;

interface AsyncCallback {
    void onSuccess(in AidlResult aidlResult);
    void onError(in AidlException aidlException);
}
