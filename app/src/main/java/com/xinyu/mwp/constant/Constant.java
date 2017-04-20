package com.xinyu.mwp.constant;

public interface Constant {

    String[] rechargeType = new String[]{"微信支付", "银联支付"};
    String[] handleText = new String[]{" 未操作 ", " 双倍返还 ", " 货运 ", " 退舱 "};
    String APP_ID = "wx9dc39aec13ee3158";
    String SECRET = "1e372a173a248fb1b5c88d24236ef3b3";

    String USER_ENTITY = "user_entity";

    interface IntentKey {
        String CHOOSE_IMGS_SCAN = "CHOOSE_IMGS_SCAN";
        String CHOOSE_IMGS_RES = "CHOOSE_IMGS_RES";
        String IMGS_BIG = "IMGS_BIG";
        String IMGS_LIST = "IMGS_LIST";
        String IMGS_POSITION = "IMGS_POSITION";
    }

    int TYPE_BUY_MINUS = 0; //买跌
    int TYPE_BUY_PLUS = 1; //买涨
    int TYPE_INSUFFICIENT_BALANCE = 2; //余额不足

    //    60-1分钟K线，300-5分K线，900-15分K线，1800-30分K线，3600-60分K线，5-日K线
    int MIN_LINE1 = 0;
    int MIN_LINE5 = 300;
    int MIN_LINE15 = 900;
    int MIN_LINE30 = 1800;
    int MIN_LINE60 = 3600;

    int ACTION_NONE = 0;
    int ACTION_DOUBLE = 1;
    int ACTION_FREIGHT = 2;  //货运
    int ACTION_RETURN = 3;

}
