package com.xlh.hexadecimalstatesample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xlh.hexadecimalstatesample.utils.BitOperationUtil;

public class MainActivity extends AppCompatActivity {

    TextView tvOrderState;
    Button btnFreeze;
    Button btnPaySuccess;
    Button btnRefundAll;
    Button btnRefundPart;
    Button btnRole1;
    Button btnRole2;
    Button btnRole3;
    Button btnPrintOpen;
    Button btnPrintClose;
    TextView tvRefundState;
    TextView tvPrintState;
    TextView tvSetOrderState;
    TextView tvSetRole;
    TextView tvSetPrint;
    Button btnReset;

    public static final String TAG = "TEST";

    // 当前状态
    private int STATUSES;// 0

    // 订单状态
    // 冻结，可理解为顾客付押金给商家
    private final int STATUS_FREEZE = 0x0001;// 1
    // 付款成功
    private final int STATUS_PAY_SUCCESS = 0x0002;// 2
    // 全部退款
    private final int STATUS_REFUND_ALL = 0x0004;// 4
    // 部分退款
    private final int STATUS_REFUND_PART = 0x0008;// 8

    // 角色状态。决定退款权限：商户店长有权限，店员无权限
    // 商户
    private final int STATUS_ROLE_1 = 0x0010;// 16
    // 店长
    private final int STATUS_ROLE_2 = 0x0020;// 32
    // 店员
    private final int STATUS_ROLE_3 = 0x0040;// 64

    // 打印状态
    // 开启
    private final int STATUS_PRINT_OPEN = 0x0100;// 256
    // 关闭
    private final int STATUS_PRINT_CLOSE = 0x0200;// 512


    //  不会有0值，进入时就要设置订单、角色、打印等初始状态。
//    private final int MODE_INIT = 0;

    // 默认设置：冻结中，商户，关闭打印
    private final int MODE_INIT = STATUS_FREEZE | STATUS_ROLE_1 | STATUS_PRINT_CLOSE;//  529 = 1+16+512

    // 冻结且开启打印
    private final int MODE_FREEZE_PRINT = STATUS_FREEZE | STATUS_PRINT_OPEN;// 257 = 1+256
    // 付款成功且开启打印
    private final int MODE_SUCCESS_PRINT = STATUS_PAY_SUCCESS | STATUS_PRINT_OPEN;// 258 = 2+256
    // 全部退款或部分退款且开启打印
    private final int MODE_REFUND_PRINT = STATUS_REFUND_ALL | STATUS_REFUND_PART | STATUS_PRINT_OPEN;// 268 = 4+8+256
    // 全部退款且开启打印
    private final int MODE_REFUND_ALL_PRINT = STATUS_REFUND_ALL | STATUS_PRINT_OPEN;
    // 部分退款且开启打印
    private final int MODE_REFUND_PART_PRINT = STATUS_REFUND_PART | STATUS_PRINT_OPEN;


    // 付款成功或部分退款，商户或店长
    private final int MODE_REFUND_AVAILABLE = (STATUS_PAY_SUCCESS | STATUS_REFUND_PART) & (STATUS_ROLE_1 | STATUS_ROLE_2);
    // 付款成功且是商户
    private final int MODE_PAY_SUCCESS_ROLE1 = STATUS_PAY_SUCCESS | STATUS_ROLE_1;
    // 部分退款且是商户
    private final int MODE_REFUND_PART_ROLE1 = STATUS_REFUND_PART | STATUS_ROLE_1;
    // 付款成功且是店长
    private final int MODE_PAY_SUCCESS_ROLE2 = STATUS_PAY_SUCCESS | STATUS_ROLE_2;
    // 部分退款且是店长
    private final int MODE_REFUND_PART_ROLE2 = STATUS_REFUND_PART | STATUS_ROLE_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initStatus();

        setListener();
    }

    private void initView() {
        tvOrderState = findViewById(R.id.tv_order_state);
        btnFreeze = findViewById(R.id.btn_freeze);
        btnPaySuccess = findViewById(R.id.btn_pay_success);
        btnRefundAll = findViewById(R.id.btn_refund_all);
        btnRefundPart = findViewById(R.id.btn_refund_part);
        btnRole1 = findViewById(R.id.btn_role_1);
        btnRole2 = findViewById(R.id.btn_role_2);
        btnRole3 = findViewById(R.id.btn_role_3);
        btnPrintOpen = findViewById(R.id.btn_print_open);
        btnPrintClose = findViewById(R.id.btn_print_close);
        tvRefundState = findViewById(R.id.tv_refund_state);
        tvPrintState = findViewById(R.id.tv_print_state);
        tvSetOrderState = findViewById(R.id.tv_set_order_state);
        tvSetRole = findViewById(R.id.tv_set_role);
        tvSetPrint = findViewById(R.id.tv_set_print);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void initStatus() {
        STATUSES = MODE_INIT;
        Log.e(TAG, "initStatus--STATUSES：" + STATUSES);
        refreshState();
    }

    private void resetPay() {
        Log.e(TAG, "resetPay前--STATUSES：" + STATUSES);

        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_FREEZE);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_PAY_SUCCESS);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_REFUND_ALL);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_REFUND_PART);

        Log.e(TAG, "resetPay后--STATUSES：" + STATUSES);
    }

    private void resetRole() {
        Log.e(TAG, "resetRole前--STATUSES：" + STATUSES);

        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_ROLE_1);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_ROLE_2);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_ROLE_3);

        Log.e(TAG, "resetRole后--STATUSES：" + STATUSES);
    }

    private void resetPrint() {
        Log.e(TAG, "resetPrint前--STATUSES：" + STATUSES);

        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_PRINT_OPEN);
        STATUSES = BitOperationUtil.removeMark(STATUSES, STATUS_PRINT_CLOSE);

        Log.e(TAG, "resetPrint后--STATUSES：" + STATUSES);
    }

    private void refreshState() {

        Log.e(TAG, "refreshState--STATUSES：" + STATUSES);

        /**
         * 设置订单状态
         */
        if (BitOperationUtil.hasMark(STATUSES, STATUS_FREEZE)) {
            tvOrderState.setText("冻结");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_PAY_SUCCESS)) {
            tvOrderState.setText("付款成功");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_REFUND_ALL)) {
            tvOrderState.setText("全部退款");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_REFUND_PART)) {
            tvOrderState.setText("部分退款");
        }

        /**
         * 设置退款状态
         * 先判断订单状态，再判断角色状态
         */
        if (BitOperationUtil.hasMark(STATUSES, STATUS_FREEZE)) {
            tvRefundState.setText("冻结中，不可退");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_REFUND_ALL)) {
            tvRefundState.setText("已全部退款，不可退");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_ROLE_3)) {
            tvRefundState.setText("店员，不可退");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_PAY_SUCCESS_ROLE1) || BitOperationUtil.hasMark(STATUSES, MODE_REFUND_PART_ROLE1)) {
            tvRefundState.setText("商户，可退");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_PAY_SUCCESS_ROLE2) || BitOperationUtil.hasMark(STATUSES, MODE_REFUND_PART_ROLE2)) {
            tvRefundState.setText("店长，可退");
        }

        /**
         * 设置打印状态
         */
        if (BitOperationUtil.hasMark(STATUSES, STATUS_PRINT_CLOSE)) {
            tvPrintState.setText("打印关闭，请开启");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_FREEZE_PRINT)) {
            // MODE_FREEZE_PRINT
            tvPrintState.setText("冻结打印");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_SUCCESS_PRINT)) {
            // MODE_SUCCESS_PRINT
            tvPrintState.setText("付款成功打印");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_REFUND_ALL_PRINT)) {
            // MODE_REFUND_ALL_PRINT
            tvPrintState.setText("全部退款打印");
        } else if (BitOperationUtil.hasMark(STATUSES, MODE_REFUND_PART_PRINT)) {
            // MODE_REFUND_PART_PRINT
            tvPrintState.setText("部分退款打印");
        } else if (BitOperationUtil.hasMark(STATUSES, STATUS_PRINT_OPEN)) {
            tvPrintState.setText("打印开启");
        }

    }


    private void setListener() {
        btnFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置付款状态
                resetPay();
                // 添加冻结
                Log.e(TAG, "点击btn_freeze：" + STATUS_FREEZE);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_FREEZE);
                Log.e(TAG, "点击btn_freeze后STATUSES：" + STATUSES);
                tvSetOrderState.setText("冻结");
                refreshState();
            }
        });

        btnPaySuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置付款状态
                resetPay();
                // 添加付款成功
                Log.e(TAG, "点击btn_pay_success：" + STATUS_PAY_SUCCESS);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_PAY_SUCCESS);
                Log.e(TAG, "点击btn_pay_success后STATUSES：" + STATUSES);
                tvSetOrderState.setText("付款成功");
                refreshState();
            }
        });

        btnRefundAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置付款状态
                resetPay();
                // 添加全部退款
                Log.e(TAG, "点击btn_refund_all：" + STATUS_REFUND_ALL);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_REFUND_ALL);
                Log.e(TAG, "点击btn_refund_all后STATUSES：" + STATUSES);
                tvSetOrderState.setText("全部退款");
                refreshState();
            }
        });

        btnRefundPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置付款状态
                resetPay();
                // 添加部分退款
                Log.e(TAG, "点击btn_refund_part：" + STATUS_REFUND_PART);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_REFUND_PART);
                Log.e(TAG, "点击btn_refund_part后STATUSES：" + STATUSES);
                tvSetOrderState.setText("部分退款");
                refreshState();
            }
        });

        btnRole1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置角色
                resetRole();
                // 添加商户
                Log.e(TAG, "点击btn_role_1：" + STATUS_ROLE_1);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_ROLE_1);
                Log.e(TAG, "点击btn_role_1后STATUSES：" + STATUSES);
                tvSetRole.setText("商户");
                refreshState();
            }
        });

        btnRole2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置角色
                resetRole();
                // 添加店长
                Log.e(TAG, "点击btn_role_2：" + STATUS_ROLE_2);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_ROLE_2);
                Log.e(TAG, "点击btn_role_2后STATUSES：" + STATUSES);
                tvSetRole.setText("店长");
                refreshState();
            }
        });

        btnRole3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置角色
                resetRole();
                // 添加店员
                Log.e(TAG, "点击btn_role_3：" + STATUS_ROLE_3);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_ROLE_3);
                Log.e(TAG, "点击btn_role_3后STATUSES：" + STATUSES);
                tvSetRole.setText("店员");
                refreshState();
            }
        });

        btnPrintOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置打印
                resetPrint();
                // 添加开启打印
                Log.e(TAG, "点击btn_print_open：" + STATUS_PRINT_OPEN);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_PRINT_OPEN);
                Log.e(TAG, "点击btn_print_open后STATUSES：" + STATUSES);
                tvSetPrint.setText("开启打印");
                refreshState();
            }
        });

        btnPrintClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置打印
                resetPrint();
                // 添加关闭打印
                Log.e(TAG, "点击btn_print_close：" + STATUS_PRINT_CLOSE);
                STATUSES = BitOperationUtil.addMark(STATUSES, STATUS_PRINT_CLOSE);
                Log.e(TAG, "点击btn_print_close后STATUSES：" + STATUSES);
                tvSetPrint.setText("关闭打印");
                refreshState();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initStatus();

                // 恢复成初始状态
                tvSetOrderState.setText("冻结");
                tvSetRole.setText("商户");
                tvSetPrint.setText("关闭打印");

            }
        });

    }

}
