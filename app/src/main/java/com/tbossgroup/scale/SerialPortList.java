package com.tbossgroup.scale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by Administrator
 *
 * @author 猿史森林
 *         Date: 2017/10/14
 *         Class description:
 */
public class SerialPortList extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        portconnect();
    }


    public void portconnect(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.SERIALPORTPATH, "/dev/ttyXRM0");
        bundle.putInt(Constant.SERIALPORTBAUDRATE, 9600);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
