package org.martellina.aidl;

import org.martellina.aidl.callback.AsyncCallback;

interface ReturningData {
    double calcPrimitive(int first, int second, int action);
    void calcObject(int first, int second, int action, AsyncCallback callback);
}
