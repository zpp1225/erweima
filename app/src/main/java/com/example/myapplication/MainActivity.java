package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Sao_Yi_Sao;
    private Button Sheng_Btn;
    private ImageView Image;
    private TextView result;
    private final int REQUEST_CODE_SCAN = 1001;
    private String contentEtString = "吕新新是你男神";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Sao_Yi_Sao = (Button) findViewById(R.id.Sao_Yi_Sao);
        Sheng_Btn = (Button) findViewById(R.id.Sheng_Btn);
        Image = (ImageView) findViewById(R.id.Image);
        result = findViewById(R.id.result);
        Sao_Yi_Sao.setOnClickListener(this);
        Sheng_Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Sao_Yi_Sao:
                if (!initPermission()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("没有开启摄像机权限，是否去设置开启？")
                            .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //调用系统内部去开启权限
                                    ApplicationInfo(MainActivity.this);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return;
                }
                //跳到扫一扫页面
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);

                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                 * 也可以不传这个参数
                 * 不传的话  默认都为默认不震动  其他都为true
                 * */

                //ZxingConfig config = new ZxingConfig();
                //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
                //config.setPlayBeep(true);//是否播放提示音
                //config.setShake(true);//是否震动
                //config.setShowAlbum(true);//是否显示相册
                //config.setShowFlashLight(true);//是否显示闪光灯
                //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            case R.id.Sheng_Btn:
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                try {
                    //生成二维码需要用到类是CodeCreator
                    Bitmap bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);
                    Image.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //key值都约束好了
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                result.setText("扫描结果为：" + content);
            }
        }
    }
    //查看是否开启摄像头权限

    private boolean initPermission() {
        //需要在Android里面找到你要开的权限
        String permissions = Manifest.permission.CAMERA;
        boolean ret = false;
        //Android 6.0以上才有动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//permission granted 说明权限开了
            ret = PermissionChecker.checkSelfPermission(MainActivity.this, permissions) == PermissionChecker.PERMISSION_GRANTED;
        }
        return ret;
    }

    /**
     * 应用信息界面
     * 打开自己手机去看权限
     *
     * @param activity
     */
    public static void ApplicationInfo(Activity activity) {

        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
            }
            activity.startActivity(localIntent);
        } catch (Exception e) {
        }
    }
}
