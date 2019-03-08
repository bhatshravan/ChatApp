package inc.bs.chataroo.misc;

import android.util.Log;

/**
 * Created by Shravan on 18-01-2018.
 */

public class Loger {
    boolean build=true;
    public void log(String n){
        if(build)
            Log.e("Loger",n);
    }
    public void log(String n,char n2){
        if(build) {
            switch (n2) {
                case 'w':
                    Log.w("Loger",n);
                    break;
                case 'e':
                    Log.e("Loger",n);
                    break;
                case 'i':
                    Log.i("Loger",n);
                    break;
                case 'v':
                    Log.v("Loger",n);
                    break;
                case 'd':
                default:
                    Log.d("Loger", n);
            }
        }
    }


}
