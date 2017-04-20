package com.xinyu.mwp.networkapi;

import com.xinyu.mwp.entity.BankCardEntity;
import com.xinyu.mwp.entity.BankInfoEntity;
import com.xinyu.mwp.entity.CurrentPositionEntity;
import com.xinyu.mwp.entity.CurrentPositionListReturnEntity;
import com.xinyu.mwp.entity.CurrentPriceReturnEntity;
import com.xinyu.mwp.entity.HistoryPositionEntity;
import com.xinyu.mwp.entity.HistoryPositionListReturnEntity;
import com.xinyu.mwp.entity.OpenPositionReturnEntity;
import com.xinyu.mwp.entity.RechargeRecordItemEntity;
import com.xinyu.mwp.entity.SymbolInfosEntity;
import com.xinyu.mwp.entity.CurrentTimeLineReturnEntity;
import com.xinyu.mwp.entity.ProductEntity;
import com.xinyu.mwp.entity.TotalDealInfoEntity;
import com.xinyu.mwp.entity.WXPayResultEntity;
import com.xinyu.mwp.entity.WXPayReturnEntity;
import com.xinyu.mwp.entity.WithDrawCashReturnEntity;
import com.xinyu.mwp.listener.OnAPIListener;

import java.util.List;

/**
 * Created by yaowang on 2017/2/20.
 * 交易 行情相关接口
 */

public interface DealAPI {
    void products(OnAPIListener<List<ProductEntity>> listener);

    //当前分时数据
    void timeline(String exchangeName, String platformName, String symbol, int aType, OnAPIListener<List<CurrentTimeLineReturnEntity>> listener);
//    void timeline(String exchangeName, String platformName, String symbol, int aType, OnAPIListener<CurrentTimeLineReturnEntity> listener);

    //当前报价
    void currentPrice(List<SymbolInfosEntity> symbolInfos, OnAPIListener<List<CurrentPriceReturnEntity>> listener);

    //加载Kchart
    void kchart(String exchangeName, String platformName, String symbol, int aType, int chartType, OnAPIListener<List<CurrentTimeLineReturnEntity>> listener);

    //建仓
    void openPosition(long codeId, int buySell, double amount, double price, boolean isDeferred, OnAPIListener<CurrentPositionListReturnEntity> listener);

    //当前仓位列表
    void currentPositionList(int start, int count, OnAPIListener<List<CurrentPositionListReturnEntity>> listener);

    //历史记录(处理/已处理)
    void historyPositionList(int start, int count, OnAPIListener<List<HistoryPositionListReturnEntity>> listener);

    //交易明细--历史记录(根据symbol)
    void historyDealList(int start, int count, String symbol, OnAPIListener<List<HistoryPositionListReturnEntity>> listener);

    //交易总概况
    void totalDealInfo(OnAPIListener<TotalDealInfoEntity> listener);

    //微信支付
    void weixinPay(String title, double price, OnAPIListener<WXPayReturnEntity> listener);

    //提现
    void cash(double money, long cardId, String pwd, OnAPIListener<WithDrawCashReturnEntity> listener);

    //提现列表
    void cashList(String status, int startPos, int count, OnAPIListener<List<WithDrawCashReturnEntity>> listener);

    //提现列表
    void currentPosition(double pid, OnAPIListener<CurrentPositionEntity> listener);

    //提现列表
    void profit(long tid, int handle, OnAPIListener<HistoryPositionListReturnEntity> listener);

    void wxpayResult(String rid, int payResult, OnAPIListener<WXPayResultEntity> listener);//支付结果

    void bankCardList(OnAPIListener<List<BankCardEntity>> listener);//银行卡列表

    void bankName(String cardNo, OnAPIListener<BankInfoEntity> listener);//获取银行账户信息

    void bindCard(long bankId, String bankName, String branchBank, String cardNo, String name, OnAPIListener<BankInfoEntity> listener);//获取银行账户信息

    void unBindCard(long bankCardId, String verCode, OnAPIListener<Object> listener);//解绑操作

    void rechargeList(int startPos, int count, OnAPIListener<List<RechargeRecordItemEntity>> listener);//解绑操作
}
