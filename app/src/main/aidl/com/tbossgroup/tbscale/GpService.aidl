// GpService.aidl
package com.tbossgroup.tbscale;

// Declare any non-default types here with import statements

interface GpService {

    void openPortConfigurationDialog();  //打开端口连接对话框
    int printeTestPage(int PrinterId);  // 打印测试页
    int queryPrinterStatus(int PrinterId); //查询打印机状态
    int getPrinterCommandType(int PrinterId); //查询打印机指令类型
    int sendEscCommand(int PrinterId, String b64);//发送ESC指令
    int sendTscCommand(int PrinterId, String b64); //发送TSC指令
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}