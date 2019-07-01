package com.xwc.wxvoicetest;

import android.Manifest;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: xuweichao
 * date: 2019/6/24 17:12
 * desc:
 */
public class MainActivity extends Activity {

    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<Recorder>();

    private AudioRecorderButton mAudioRecorderButton;

    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();

        mListView = findViewById(R.id.list_recorder);
        mAudioRecorderButton = findViewById(R.id.btn_recorder);
        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);
            }
        });

        mAdapter = new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.mipmap.adj);
                    mAnimView = null;
                }
                // 播放动画
                mAnimView = view.findViewById(R.id.recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                final AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                // 播放音频
                MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });
    }

    private void getPermission() {
        if (PermissionUtils.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {

        } else {
            PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.MICROPHONE)
                    .callback(new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(List<String> permissionsGranted) {

                        }

                        @Override
                        public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                            Toast.makeText(MainActivity.this, "没有权限不能使用", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    class Recorder {
        float time;
        String filePath;

        public Recorder(float time, String filePath) {
            super();
            this.time = time;
            this.filePath = filePath;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}

