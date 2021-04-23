package com.seray.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ToggleButton;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.ConfigADB;

public abstract class BaseConfigFragment extends Fragment implements View.OnClickListener {

    Misc mMisc;
    ConfigDao mManager = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
        mManager = AppDatabase.getInstance().getConfigDao();
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        ToggleButton button = (ToggleButton) v;
        Object tag = button.getTag();
        if (tag != null) {
            String key = tag.toString();
            boolean checked = button.isChecked();
            updateConfig(key, checked ? "1" : "0");
        }
    }

    private void updateConfig(final String key, final String value) {
        AppExecutors.getInstance().insertIO().submit(new Runnable() {
            @Override
            public void run() {
                ConfigADB configADB = new ConfigADB(key, value);
                mManager.save(configADB);
            }
        });
    }

    public abstract void recoverChecked(View parent);

}
