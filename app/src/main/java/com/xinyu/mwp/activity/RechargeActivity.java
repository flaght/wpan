package com.xinyu.mwp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.unionpay.UPPayAssistEx;
import com.xinyu.mwp.R;
import com.xinyu.mwp.activity.base.BaseRefreshActivity;
import com.xinyu.mwp.activity.unionpay.APKActivity;
import com.xinyu.mwp.activity.unionpay.JARActivity;
import com.xinyu.mwp.application.MyApplication;
import com.xinyu.mwp.constant.Constant;
import com.xinyu.mwp.entity.EventBusMessage;
import com.xinyu.mwp.entity.UnionPayReturnEntity;
import com.xinyu.mwp.entity.WXPayResultEntity;
import com.xinyu.mwp.entity.WXPayReturnEntity;
import com.xinyu.mwp.listener.OnAPIListener;
import com.xinyu.mwp.listener.OnRefreshListener;
import com.xinyu.mwp.listener.OnSuccessListener;
import com.xinyu.mwp.networkapi.NetworkAPIFactoryImpl;
import com.xinyu.mwp.networkapi.socketapi.SocketReqeust.SocketAPINettyHandler;
import com.xinyu.mwp.networkapi.socketapi.SocketReqeust.SocketAPIResponse;
import com.xinyu.mwp.networkapi.socketapi.SocketReqeust.SocketDataPacket;
import com.xinyu.mwp.user.UserManager;
import com.xinyu.mwp.util.ErrorCodeUtil;
import com.xinyu.mwp.util.LogUtil;
import com.xinyu.mwp.util.NumberUtils;
import com.xinyu.mwp.util.TestDataUtil;
import com.xinyu.mwp.util.ToastUtils;
import com.xinyu.mwp.util.Utils;
import com.xinyu.mwp.view.CellView;
import com.xinyu.mwp.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import in.srain.cube.views.ptr.PtrFrameLayout;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Benjamin on 17/1/13.
 */

public class RechargeActivity extends BaseRefreshActivity {
    @ViewInject(R.id.refreshFrameLayout)
    private PtrFrameLayout refreshFrameLayout;
    @ViewInject(R.id.account)
    private CellView account;
    @ViewInject(R.id.money)
    private CellView money;
    //    @ViewInject(R.id.myBankCard)
//    private CellView myBankCard;
    @ViewInject(R.id.rechargeMoney)
    private EditText rechargeMoney;
    @ViewInject(R.id.rechargeType)
    private CellView rechargeType;
    @ViewInject(R.id.iv_bannerview)
    private ImageView bannerView;
    private int choice = 0;
    //    private IWXAPI api;
    private WXPayReturnEntity wxPayEntity;

    @Override
    protected int getContentView() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("充值");
//        bannerView.centerDot();
//        bannerView.setRefreshLayout(refreshFrameLayout);
//        bannerView.update(TestDataUtil.getIndexBanners(3));
//        requestBalance();
        rightText.setText("充值记录");
        rightText.setVisibility(View.VISIBLE);
        Utils.closeSoftKeyboard(rechargeMoney);
        NumberUtils.setEditTextPoint(rechargeMoney, 2);  //设置充值金额的小数位数
//        api = WXAPIFactory.createWXAPI(context, null);
//        api.registerApp(Constant.APP_ID);

        if (flag) {
            EventBus.getDefault().register(this); // EventBus注册广播()
            flag = false;//更改标记,使其不会再进行多次注册
        }
    }

    @Event(value = {R.id.commit, R.id.rechargeType, R.id.myBankCard, R.id.rightText})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.commit:
                commitPay();
                break;

            case R.id.rechargeType:
                ToastUtils.show(context, "充值方式");
                choice = 0;
                createDialog();
                break;

            case R.id.myBankCard:
                next(BindBankCardActivity.class);
                break;

            case R.id.rightText:
                next(RechargeRecordActivity.class);
                break;
        }
    }

    private void commitPay() {
        String title = "微盘-余额充值";
        if (TextUtils.isEmpty(rechargeMoney.getEditableText().toString().trim())) {
            ToastUtils.show(context, "输入不能为空");
            return;
        }
        double price = Double.parseDouble(rechargeMoney.getEditableText().toString().trim());
        if (price <= 0) {
            ToastUtils.show(context, "输入的金额有误,请重新输入");
            return;
        }

        if (choice == 0) {
            ToastUtils.show(context, "微信支付");
            requestWXPay(title, price);
        } else {
            // showToast("银联支付");
            //requestUnionPay(title, price);
            requestPayMent((long) price);  //第三方

        }
    }

    /**
     * 请求微信支付
     *
     * @param title title
     * @param price 金额
     */
    private void requestWXPay(String title, double price) {
        NetworkAPIFactoryImpl.getDealAPI().weixinPay(title, price, new OnAPIListener<WXPayReturnEntity>() {
            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
            }

            @Override
            public void onSuccess(WXPayReturnEntity wxPayReturnEntity) {
                wxPayEntity = wxPayReturnEntity;
                PayReq request = new PayReq();
                request.appId = wxPayReturnEntity.getAppid();
                request.partnerId = wxPayReturnEntity.getPartnerid();
                request.prepayId = wxPayReturnEntity.getPrepayid();
//                    request.packageValue = wxPayReturnEntity.getPackage();
                request.packageValue = "Sign=WXPay";
                request.nonceStr = wxPayReturnEntity.getNoncestr();
                request.timeStamp = wxPayReturnEntity.getTimestamp();
                request.sign = wxPayReturnEntity.getSign();
                MyApplication.api.sendReq(request);
                //模拟请求回调
//                    requestResult();
            }
        });
    }

    /**
     * 请求第三方支付
     *
     * @param price 金额
     */
    private void requestPayMent(long price) {
        String outTradeNo = ""; //订单号
        final String content = "";  //描述
        NetworkAPIFactoryImpl.getDealAPI().payment(outTradeNo, price, content, Constant.payType.H5_ONLINE_BANK_PAY,
                new OnAPIListener<UnionPayReturnEntity>() {
            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
                LogUtil.d("调用第三方失败");
                //模拟进入百度页面
                Intent intent = new Intent(context, PayMentActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSuccess(UnionPayReturnEntity unionPayReturnEntity) {
                Intent intent = new Intent(context, PayMentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("payment", unionPayReturnEntity);
                intent.putExtra("pay", bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * 请求银联支付
     *
     * @param title title
     * @param price 金额
     */
    private void requestUnionPay(String title, double price) {
        NetworkAPIFactoryImpl.getDealAPI().unionPay(title, price, new OnAPIListener<Object>() {
            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
                LogUtil.d("银联支付请求失败");
                ErrorCodeUtil.showEeorMsg(context, ex);
            }

            @Override
            public void onSuccess(Object o) {
                LogUtil.d("银联支付请求成功" + o.toString());
            }
        });
        if (UPPayAssistEx.checkInstalled(this)) {
            //当判断用户手机上已安装银联Apk，商户客户端可以做相应个性化处理
            next(APKActivity.class);//APK接入
            LogUtil.d("已经安装了apk客户端");
        } else {
            next(JARActivity.class);//JAR接入
            LogUtil.d("没有安装apk客户端,jar接入");
        }
        //tn是交易流水号
//               UPPayAssistEx.startPay(activity, null, null, tn, mode);

        //处理支付结果
        //onActivityResult方法
    }

    private void requestResult() {
        NetworkAPIFactoryImpl.getDealAPI().wxpayResult(wxPayEntity.getRid(), 1, new OnAPIListener<WXPayResultEntity>() {
            @Override
            public void onError(Throwable ex) {
                ex.printStackTrace();
                LogUtil.d("接收支付回调失败了");
            }

            @Override
            public void onSuccess(WXPayResultEntity wxPayResultEntity) {
                LogUtil.d("接收到了支付成功的消息:" + wxPayResultEntity.toString());

            }
        });
    }

    private void createDialog() {
        new AlertDialog.Builder(this).setTitle("选择支付方式").setIcon(
                R.mipmap.icon_rechargetype).setSingleChoiceItems(Constant.rechargeType, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        choice = which;
                        dialog.dismiss();
                        LogUtil.d("点击的是第" + which + "个条目");
                        rechargeType.updateContentLeft(Constant.rechargeType[choice]);
                    }
                }).setNegativeButton("取消", null).show();

    }

    @Override
    protected void initListener() {
        super.initListener();
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (UserManager.getInstance().getUserEntity() != null) {
                            account.updateContentLeft(UserManager.getInstance().getUserEntity().getMobile());
                            money.updateContentLeft(NumberUtils.halfAdjust2(UserManager.getInstance().getUserEntity().getBalance()) + "元");
                        }
                        rechargeType.updateContentLeft(Constant.rechargeType[choice]);
                        getRefreshController().getContentView().setVisibility(View.VISIBLE);
                        getRefreshController().refreshComplete();
                    }
                }, 200);
            }
        });

        OnSuccessListener listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
//                ToastUtils.show(context, "接收到成功的信息" + o.toString());
                LogUtil.d("接收到成功的信息" + o.toString());
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        TestDataUtil.requestBalance();
    }

    private boolean flag = true;

    /**
     * EventBus接收消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void ReciveMessage(EventBusMessage eventBusMessage) {
        switch (eventBusMessage.Message) {
            case 0:  //成功
                ToastUtils.show(context, "支付成功");  //1-成功 2-取消支付
                LogUtil.d("接收到成功是0");
                TestDataUtil.requestBalance();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        money.updateContentLeft(NumberUtils.halfAdjust2(UserManager.getInstance().getUserEntity().getBalance()) + "元");
                    }
                }, 500);
                next(RechargeRecordActivity.class);
                break;
            case -2:  //取消支付
                ToastUtils.show(context, "用户取消支付");
                LogUtil.d("接收到取消支付是-2");
                break;

            case -10:  //取消支付
                createCancelPayDialog();
                LogUtil.d("接收到取消支付是-10");
                break;
        }
    }

    /**
     * 取消支付的弹窗
     */
    private void createCancelPayDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(context, Constant.TYPE_INSUFFICIENT_BALANCE);
        builder.setTitle(getResources().getString(R.string.pay_state))
                .setMessage(getResources().getString(R.string.cancel_pay_msg))
                .setPositiveButton(getResources().getString(R.string.pay_complete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        next(RechargeRecordActivity.class);
                    }
                }).setNegativeButton(getResources().getString(R.string.pay_problem), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Intent intent = new Intent(RechargeActivity.this, MainFragmentActivity.class);
//                startActivity(intent);
                finish();
            }
        }).create().show();
    }

    private SocketAPINettyHandler handler = new SocketAPINettyHandler() {
        @Override
        protected void messageReceived(ChannelHandlerContext ctx, SocketDataPacket socketDataPacket) throws Exception {
            super.messageReceived(ctx, socketDataPacket);
            ToastUtils.show(context, "接收到了message的消息了");
            LogUtil.d("接收到了message的消息了");
            if (socketDataPacket != null) {

                SocketAPIResponse socketAPIResponse = new SocketAPIResponse(socketDataPacket);
                int statusCode = socketAPIResponse.statusCode();
                if (statusCode == 0) {
//                        socketAPIRequest.onSuccess(socketAPIResponse);
                    LogUtil.d("jsonResponse:" + socketAPIResponse.jsonObject());
                    ToastUtils.show(context, "接收到了消息ssss:" + socketAPIResponse.jsonObject());
                }
//                    else {
////                        socketAPIRequest.onErrorCode(statusCode);
//                    }
            }
        }
    };


    @Override
    protected void onDestroy() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        LogUtil.d("onDestroy--------------------执行");
        super.onDestroy();
    }
}
