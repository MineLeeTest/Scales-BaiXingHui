//package com.seray.scales;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.seray.sjc.adapter.CategoryAdapter;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.entity.product.SjcCategory;
//import com.seray.sjc.entity.product.SjcProduct;
//import com.seray.sjc.view.CommonPopupWindow;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SearchActivity extends BaseActivity {
//
//    private ImageView backpage, nextpage;
//    private TextView pagecount, mTitleText;
//
//    private int pageAmount = 0;
//
//    private List<SjcProduct> proListFromDb;
//    private List<SjcProduct> mData = new ArrayList<>();
//
//    private SearchHandler mSearchHandler = new SearchHandler(new WeakReference<>(this));
//    private int count = 1;
//    private List<Button> mPluButtonList = new ArrayList<>();
//
//    private void _handleDataInit() {
//        pluAdaptation();
//    }
//
//    private void pluAdaptation() {
//        clearPlu();
//        int dataSize = proListFromDb.size();
//        pageAmount = dataSize / 27;
//        int free = dataSize % 27;
//        if (dataSize == 0)
//            pageAmount = 1;
//        if (free != 0)
//            pageAmount += 1;
//        pagecount.setText(String.format("%s/%s", count, pageAmount));
//        int startIndex = (count - 1) * 27;
//        for (int i = startIndex; i < dataSize; i++) {
//            if (i < 27 * count) {
//                int btnIndex = i % 27;
//                String proName = proListFromDb.get(i).getGoodsName();
//                Button button = mPluButtonList.get(btnIndex);
//                button.setText(proName);
//            }
//        }
//    }
//
//    private void clearPlu() {
//        for (Button button : mPluButtonList) {
//            button.setText("");
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.filppage);
//        EventBus.getDefault().register(this);
//        initViews();
//        initData();
//        initListeners();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        mSearchHandler.removeCallbacksAndMessages(null);
//        mData.clear();
//        mData = null;
//        if (proListFromDb != null) {
//            proListFromDb.clear();
//            proListFromDb = null;
//        }
//        mPluButtonList.clear();
//        mPluButtonList = null;
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.backpage:
//                count -= 1;
//                if (count < 1) {
//                    showMessage(R.string.select_first_page);
//                    count = 1;
//                } else {
//                    pluAdaptation();
//                }
//                break;
//            case R.id.nextpage:
//                count += 1;
//                if (count > pageAmount) {
//                    showMessage(R.string.select_final_page);
//                    count = pageAmount;
//                } else {
//                    pluAdaptation();
//                }
//                break;
//            case R.id.finishactivity:
//                finish();
//                break;
//        }
//    }
//
//    private void postResult(SjcProduct p) {
//        if (p != null) {
//            EventBus.getDefault().post(p);
//            finish();
//        }
//    }
//
//    private CategoryAdapter mCategoryAdapter;
//
//    private CommonPopupWindow mCategoryPopWindow;
//
//    private void showCategorySelectWindow() {
//        mCategoryPopWindow = new CommonPopupWindow.Builder(this)
//                .setView(R.layout.pop_category_layout)
//                .setAnimationStyle(R.anim.dialog_enter)
//                .setBackGroundLevel(.2f)
//                .setWidthAndHeight(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
//                .create();
//        View contentView = mCategoryPopWindow.getContentView();
//        RecyclerView mCategoryRecycler = contentView.findViewById(R.id.category_recycler);
//        contentView.findViewById(R.id.category_cancel).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchActivity.super.onClick(v);
//                mCategoryPopWindow.dismiss();
//            }
//        });
//        mCategoryAdapter = new CategoryAdapter(null);
//        mCategoryRecycler.setAdapter(mCategoryAdapter);
//        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                SearchActivity.super.onClick(view);
//                SjcCategory category = mCategoryAdapter.getData().get(position);
//                final String categoryId = category.getCategoryId();
//                final String name = category.getCategoryName();
//                sqlQueryThread.submit(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        proListFromDb = AppDatabase.getInstance().getProductDao().loadProductByCategoryId(categoryId);
//                        List<SjcProduct> result = checkSelectedData(proListFromDb);
//                        if (!result.isEmpty()) {
//                            proListFromDb.clear();
//                            proListFromDb.addAll(result);
//                        }
//                        mSearchHandler.sendEmptyMessage(3);
//                        mSearchHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTitleText.setText(String.format("选择商品分类(%s)", name));
//                            }
//                        });
//                    }
//                });
//            }
//        });
//        mCategoryRecycler.setLayoutManager(new GridLayoutManager(this, 4));
//        mCategoryPopWindow.showAtLocation(findViewById(R.id.search_content), Gravity.CENTER, 0, 0);
//        loadCategory();
//    }
//
//    private void loadCategory() {
//        sqlQueryThread.submit(new Runnable() {
//            @Override
//            public void run() {
//                List<SjcCategory> categories = AppDatabase.getInstance().getCategoryDao().loadAll();
//                mSearchHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCategoryAdapter.setNewData(categories);
//                    }
//                });
//            }
//        });
//    }
//
//    private void initViews() {
//        pluButton();
//        mTitleText = findViewById(R.id.search_product_title);
//        pagecount = (TextView) findViewById(R.id.pagenum);
//        backpage = (ImageView) findViewById(R.id.backpage);
//        nextpage = (ImageView) findViewById(R.id.nextpage);
//    }
//
//    /**
//     * 查找数据库商品表中的所有数据
//     */
//    private void initData() {
//        sqlQueryThread.submit(new Runnable() {
//
//            @Override
//            public void run() {
//                proListFromDb = AppDatabase.getInstance().getProductDao().loadAll();
//                List<SjcProduct> result = checkSelectedData(proListFromDb);
//                if (!result.isEmpty()) {
//                    proListFromDb.clear();
//                    proListFromDb.addAll(result);
//                }
//                mSearchHandler.sendEmptyMessage(3);
//            }
//        });
//    }
//
//    private void initListeners() {
//        MyProductChangeListener productChangeListener = new MyProductChangeListener();
//        for (Button button : mPluButtonList) {
//            button.setOnClickListener(productChangeListener);
//        }
//        findViewById(R.id.finishactivity).setOnClickListener(this);
//        backpage.setOnClickListener(this);
//        nextpage.setOnClickListener(this);
//        mTitleText.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchActivity.super.onClick(v);
//                showCategorySelectWindow();
//            }
//        });
//    }
//
//    private void pluButton() {
//        Button PLU1 = (Button) findViewById(R.id.plu1);
//        Button PLU2 = (Button) findViewById(R.id.plu2);
//        Button PLU3 = (Button) findViewById(R.id.plu3);
//        Button PLU4 = (Button) findViewById(R.id.plu4);
//        Button PLU5 = (Button) findViewById(R.id.plu5);
//        Button PLU6 = (Button) findViewById(R.id.plu6);
//        Button PLU7 = (Button) findViewById(R.id.plu7);
//        Button PLU8 = (Button) findViewById(R.id.plu8);
//        Button PLU9 = (Button) findViewById(R.id.plu9);
//        Button PLU10 = (Button) findViewById(R.id.plu10);
//        Button PLU11 = (Button) findViewById(R.id.plu11);
//        Button PLU12 = (Button) findViewById(R.id.plu12);
//        Button PLU13 = (Button) findViewById(R.id.plu13);
//        Button PLU14 = (Button) findViewById(R.id.plu14);
//        Button PLU15 = (Button) findViewById(R.id.plu15);
//        Button PLU16 = (Button) findViewById(R.id.plu16);
//        Button PLU17 = (Button) findViewById(R.id.plu17);
//        Button PLU18 = (Button) findViewById(R.id.plu18);
//        Button PLU19 = (Button) findViewById(R.id.plu19);
//        Button PLU20 = (Button) findViewById(R.id.plu20);
//        Button PLU21 = (Button) findViewById(R.id.plu21);
//        Button PLU22 = (Button) findViewById(R.id.plu22);
//        Button PLU23 = (Button) findViewById(R.id.plu23);
//        Button PLU24 = (Button) findViewById(R.id.plu24);
//        Button PLU25 = (Button) findViewById(R.id.plu25);
//        Button PLU26 = (Button) findViewById(R.id.plu26);
//        Button PLU27 = (Button) findViewById(R.id.plu27);
//        mPluButtonList.add(PLU1);
//        mPluButtonList.add(PLU2);
//        mPluButtonList.add(PLU3);
//        mPluButtonList.add(PLU4);
//        mPluButtonList.add(PLU5);
//        mPluButtonList.add(PLU6);
//        mPluButtonList.add(PLU7);
//        mPluButtonList.add(PLU8);
//        mPluButtonList.add(PLU9);
//        mPluButtonList.add(PLU10);
//        mPluButtonList.add(PLU11);
//        mPluButtonList.add(PLU12);
//        mPluButtonList.add(PLU13);
//        mPluButtonList.add(PLU14);
//        mPluButtonList.add(PLU15);
//        mPluButtonList.add(PLU16);
//        mPluButtonList.add(PLU17);
//        mPluButtonList.add(PLU18);
//        mPluButtonList.add(PLU19);
//        mPluButtonList.add(PLU20);
//        mPluButtonList.add(PLU21);
//        mPluButtonList.add(PLU22);
//        mPluButtonList.add(PLU23);
//        mPluButtonList.add(PLU24);
//        mPluButtonList.add(PLU25);
//        mPluButtonList.add(PLU26);
//        mPluButtonList.add(PLU27);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receiveSelectPLU(List<SjcProduct> selected) {
//        if (selected != null && !selected.isEmpty()) {
//            proListFromDb.clear();
//            proListFromDb.addAll(selected);
//            pluAdaptation();
//        }
//    }
//
//    private static class SearchHandler extends Handler {
//
//        WeakReference<SearchActivity> mWeakReference;
//
//        SearchHandler(WeakReference<SearchActivity> weakReference) {
//            this.mWeakReference = weakReference;
//        }
//
//        @Override
//        public void handleMessage(Message msgs) {
//            super.handleMessage(msgs);
//            SearchActivity activity = mWeakReference.get();
//            if (activity != null) {
//                if (msgs.what == 3) {
//                    if (activity.mCategoryPopWindow != null) {
//                        activity.mCategoryPopWindow.dismiss();
//                    }
//                    activity._handleDataInit();
//                }
//            }
//        }
//    }
//
//    private class MyProductChangeListener implements OnClickListener {
//        @Override
//        public void onClick(View v) {
//            mMisc.beep();
//            Button button = (Button) v;
//            String proName = button.getText().toString();
//            if (!TextUtils.isEmpty(proName)) {
//                int tagNum = Integer.parseInt(v.getTag().toString());
//                int index;
//                if (count == 1) {
//                    index = tagNum;
//                } else {
//                    index = tagNum + (count - 1) * 27;
//                }
//                SjcProduct p = proListFromDb.get(index);
//                postResult(p);
//            }
//        }
//    }
//}