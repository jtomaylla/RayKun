package com.ecandle.raykun.helpers

/**
 * Created by juantomaylla on 16/04/18.
 */
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionDetector(private val _context: Context) {

    /**
     * Checking for all possible internet providers
     */
    //NetworkInfo[] info = connectivity.ge.getAllNetworkInfo();
    //                  for (int i = 0; i < info.length; i++)
    //                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
    //                      {
    //                          return true;
    //                      }
    val isConnectingToInternet: Boolean
        get() {
            val connectivity = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.activeNetworkInfo
                if (info != null)
                    if (info.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }

            }
            return false
        }
}
