package com.tbossgroup.scale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.util.Vector;

/**
 * Created by Administrator on 2018/4/16.
 */

public class PrintContent {




    /**
     * 标签打印测试页
     * @return
     */
    public static Vector<Byte> getLabel() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addSize(40, 60);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(1);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        // 绘制简体中文
        tsc.addText(10, 0, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "欢迎使用Printer");
        //打印繁体
//            tsc.addUnicodeText(10,32, LabelCommand.FONTTYPE.TRADITIONAL_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"BIG5碼繁體中文字元","BIG5");
        //打印韩文
//            tsc.addUnicodeText(10,60, LabelCommand.FONTTYPE.KOREAN, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"Korean 지아보 하성","EUC_KR");
//            Bitmap b = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.gprinter);
        // 绘制图片
//            tsc.addBitmap(10, 80, LabelCommand.BITMAP_MODE.OVERWRITE, 300, b);
        //绘制二维码
        tsc.addQRCode(10,10, LabelCommand.EEC.LEVEL_L, 10, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc");
        // 绘制一维条码
//            tsc.add1DBarcode(10, 500, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);
        //开启钱箱
//            tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        return  datas;
    }

    /**
     * 标签打印长图
     *
     * @param bitmap
     * @return
     */
    public static Vector<Byte> printViewPhoto(Bitmap bitmap){
        LabelCommand labelCommand=new LabelCommand();
        /**
         * 参数说明
         * 0：打印图片x轴
         * 0：打印图片Y轴
         * 576：打印图片宽度  纸张可打印宽度  72 *8
         * bitmap:图片
         */
        labelCommand.addZLibNoTrembleBitmapheight(0,0,576,bitmap);
        return labelCommand.getCommand();
    }


    /**
     * 获取图片
     * @param mcontext
     * @return
     */
    public static Bitmap getBitmap(Context mcontext) {
        View v = View.inflate(App.getContext(), R.layout.pj, null);
        TableLayout tableLayout = (TableLayout) v.findViewById(R.id.li);
        TextView jine = (TextView) v.findViewById(R.id.jine);
        TextView pep = (TextView) v.findViewById(R.id.pep);
        tableLayout.addView(ctv(mcontext, "红茶\n加热\n加糖", 8, 3));
        tableLayout.addView(ctv(mcontext, "绿茶", 109, 899));
        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
        jine.setText("998");
        pep.setText("张三");
        final Bitmap bitmap = convertViewToBitmap(v);
        return bitmap;
    }
    /**
     * mxl转bitmap图片
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static TableRow ctv(Context context, String name, int k, int n){
        TableRow tb=new TableRow(context);
        tb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT ,TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv1=new TextView(context);
        tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv1.setText(name);
        tv1.setTextColor(Color.BLACK);
        tv1.setTextSize(30);
        tb.addView(tv1);
        TextView tv2=new TextView(context);
        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setText(k+"");
        tv2.setTextColor(Color.BLACK);
        tv2.setTextSize(30);
        tb.addView(tv2);
        TextView tv3=new TextView(context);
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setText(n+"");
        tv3.setTextColor(Color.BLACK);
        tv3.setTextSize(30);
        tb.addView(tv3);
        return tb;
    }
    /**
     * 打印矩阵二维码
     * @return
     */
    public static Vector<Byte> getNewCommandToPrintQrcode() {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸，按照实际尺寸设置
        tsc.addSize(80, 80);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addGap(0);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 设置原点坐标
        tsc.addReference(0, 0);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        //添加矩阵打印二维码  旋转
        /**
         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  ROTATION：旋转   content：内容
         */
        tsc.addDMATRIX(10,10,400,400, LabelCommand.ROTATION.ROTATION_90,"DMATRIX EXAMPLE 1");
        /**
         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  content：内容
         */
        tsc.addDMATRIX(110,10,200,200,"DMATRIX EXAMPLE 1");
        /**
         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  Xzoom：放大倍数   content：内容
         */
        tsc.addDMATRIX(210,10,400,400, 6,"DMATRIX EXAMPLE 2");
        /**
         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  c：ASCLL码  Xzomm：放大倍数 content：内容
         */
        tsc.addDMATRIX(10,200,100,100,126,6,"~1010465011125193621Gsz9YC24xBbQD~12406404~191ffd0~192Ypg+oU9uLHdR9J5ms0UlqzSPEW7wYQbknUrwOehbz+s+a+Nfxk8JlwVhgItknQEZyfG4Al26Rs/Ncj60ubNCWg==");
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        return datas;
    }
}

