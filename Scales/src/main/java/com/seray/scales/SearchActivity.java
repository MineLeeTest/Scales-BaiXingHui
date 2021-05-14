package com.seray.scales;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.device.ProductADB;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    //每页数量
    private static final int MAX_PROS_PAGE = 27;

    private TextView pagecount, search_product_title;
    private List<ProductADB> proListFromDb;

    private void initViews() {
        pagecount = findViewById(R.id.pagenum);
        search_product_title = findViewById(R.id.search_product_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filppage);
        //初始化界面控件
        initViews();
        //加载界面数据
        showPros(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void postResult(ProductADB p) {
        if (p != null) {
            EventBus.getDefault().post(p);
            finish();
        }
    }

    private List<Button> getAllBtns() {
        LinearLayout llPro = findViewById(R.id.LLProPage);
        List<Button> btnList = new ArrayList<Button>();
        for (int i = 0; i < llPro.getChildCount(); i++) {
            View view = llPro.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout ll = ((LinearLayout) view);
                for (int m = 0; m < ll.getChildCount(); m++) {
                    View btn = ll.getChildAt(m);
                    if (btn instanceof Button) {
                        if (R.id.finishactivity != btn.getId()) {
                            Button b = (Button) btn;
                            b.setText("");
                            b.setTag(null);
                            btnList.add(b);
                        }
                    }
                }
            }
        }
        return btnList;
    }

    /**
     * 查找数据库商品表中的所有数据
     */
    private void showPros(int nowPageNum) {
        if (nowPageNum < 1) {
            showMessage("已经是首页了！");
            return;
        }
        proListFromDb = AppDatabase.getInstance().getProductDao().loadAll();
        if (!proListFromDb.isEmpty()) {
            int allCount = proListFromDb.size();
            search_product_title.setText(String.format("共有商品【%s】件", allCount));
            int allPageNum = 1;//全部页数
            allPageNum = (allCount / MAX_PROS_PAGE) + 1;
            if (nowPageNum > allPageNum) {
                showMessage("已经是最后一页了！");
                return;
            }
            pagecount.setText(String.format("%s/%s", nowPageNum, allPageNum));
            pagecount.setTag(nowPageNum);

            List<Button> btns = getAllBtns();
            int locS = (nowPageNum - 1) * MAX_PROS_PAGE;
            for (int i = locS; i < proListFromDb.size(); i++) {
                if ((i - locS) < btns.size()) {
                    ProductADB productADB = proListFromDb.get(i);
                    Button btn = btns.get(i - locS);
                    btn.setText(productADB.getPro_name());
                    btn.setTag(productADB);
                    btn.setOnClickListener(btnOnclick);
                }
            }

        }
    }

    private Button.OnClickListener btnOnclick = v -> {
        mMisc.beep();
        Button button = (Button) v;
        ProductADB productADB = (ProductADB) button.getTag();
        postResult(productADB);
    };

    public void return_clik(View view) {
        this.finish();
    }

    public void last_clik(View view) {
        mMisc.beep();
        int pageNum = (Integer) pagecount.getTag();
        showPros(pageNum - 1);
    }

    public void next_clik(View view) {
        mMisc.beep();
        int pageNum = (Integer) pagecount.getTag();
        showPros(pageNum + 1);
    }
}