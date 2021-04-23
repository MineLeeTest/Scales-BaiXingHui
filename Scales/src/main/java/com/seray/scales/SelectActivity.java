//package com.seray.scales;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.GridLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.seray.message.LocalFileTag;
//import com.seray.sjc.adapter.CategoryAdapter;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.entity.device.ProductADB;
//import com.seray.sjc.view.CommonPopupWindow;
//import com.seray.util.FileHelp;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 商品管理页面
// */
//public class SelectActivity extends BaseActivity {
//
//    /**
//     * 一页按钮数目
//     */
//    private static final int PLU_AMOUNT = 26;
//
//    private static final int MSG_UPDATE_VIEW = 3;
//    /**
//     * 确定按钮事件ID(确保大于 PUL_AMOUT)
//     */
//    private static final int BTN_OK_FLAG = 50;
//    /**
//     * 上一页按钮事件ID
//     */
//    private static final int BTN_LATEST_FLAG = 51;
//    /**
//     * 下一页按钮事件ID
//     */
//    private static final int BTN_NEXT_FLAG = 52;
//    /**
//     * 返回按钮事件ID
//     */
//    private static final int BTN_BACK_FLAG = 53;
//    /**
//     * 主页面布局容器
//     */
//    private GridLayout mGridLayout;
//
//    private TextView mTitleText;
//
//    /**
//     * 全部商品集合
//     */
//    private List<ProductADB> mProList = new ArrayList<>();
//    /**
//     * 选中商品集合
//     */
//    private ArrayList<ProductADB> selectedList = null;
//
//    private int pageNumber = 0;
//    private int curPageNumber = 1;
//
//    private SelectHandler mSelectHandler = new SelectHandler(new WeakReference<>(this));
//
//    private void _handleDataInit() {
//        curPageNumber = 1;
//        pageNumber = getPageNumber(mProList);
//        addBtnToGridLayout();
//        checkView();
//    }
//
//    /**
//     * 添加元素到布局中
//     */
//    protected void addBtnToGridLayout() {
//
//        for (int i = 0; i < 5; i++) {
//
//            for (int j = 0; j < 6; j++) {
//
//                if (i == 4 && j > 1) {
//                    if (j == 2) {
//                        createNavigation(2, R.string.select_last_page, BTN_LATEST_FLAG);
//                    } else if (j == 3) {
//                        createNavigation(3, R.string.select_next_page, BTN_NEXT_FLAG);
//
//                    } else if (j == 4) {
//                        createNavigation(4, R.string.reprint_ok, BTN_OK_FLAG);
//                    } else {
//                        createNavigation(5, R.string.operation_back, BTN_BACK_FLAG);
//                    }
//                    continue;
//                }
//
//                GridLayout.Spec rowSpec = GridLayout.spec(i, 1f);
//                GridLayout.Spec columnSpec = GridLayout.spec(j, 1f);
//                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
//                params.height = 0;
//                params.width = 0;
//                params.setMargins(3, 3, 3, 3);
//
//                Button btn = new Button(SelectActivity.this);
//
//                btn.setBackgroundResource(R.drawable.bg_btn_product);
//
//                btn.setTextColor(Color.parseColor("#000000"));
//
//                int datePosition = (i * 5) + j;
//
//                btn.setTag(R.id.tag_first, datePosition);
//
//                if (datePosition < mProList.size()) {
//
//                    ProductADB bean = mProList.get(datePosition);
//
//                    String content = bean.getPro_name();
//
//                    btn.setTag(R.id.tag_second, bean);
//
//                    int textLength = content == null ? 0 : content.length();
//
//                    int textSize = getAutoTextSize(textLength);
//
//                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
//
//                    btn.setText(content);
//
//                } else {
//
//                    btn.setTag(R.id.tag_second, null);
//
//                }
//
//                btn.setOnClickListener(SelectActivity.this);
//
//                mGridLayout.addView(btn, params);
//            }
//        }
//    }
//
//    private int getAutoTextSize(int textLength) {
//        int maxSize = (int) getResources().getDimension(R.dimen.pluTextSize);
//        int minSize = (int) getResources().getDimension(R.dimen.pluMinTextSize);
//        if (textLength <= 3) return maxSize;
//        int size = maxSize - (textLength - 3) * 4;
//        if (size < minSize) return minSize;
//        else return size;
//    }
//
//    /**
//     * 比对选中的商品
//     */
//    protected void checkView() {
//
//        int count = mGridLayout.getChildCount();
//
//        for (int i = 0; i < count; i++) {
//
//            Button btn = (Button) mGridLayout.getChildAt(i);
//
//            ProductADB bean = (ProductADB) btn.getTag(R.id.tag_second);
//
//            if (bean == null) {
//                continue;
//            }
//
//            if (isContained(bean)) {
//
//                btn.setBackgroundResource(R.drawable.shapethr);
//
//                btn.setTextColor(Color.parseColor("#ffffff"));
//
//                bean.setSelected(true);
//
//            }
//        }
//    }
//
//    private boolean isContained(ProductADB bean) {
//        for (int i = 0; i < selectedList.size(); i++) {
//            ProductADB product = selectedList.get(i);
//            if (product.getPro_name().equals(bean.getPro_name())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 底部导航按钮
//     */
//    protected void createNavigation(int columnId, int strId, int flag) {
//        GridLayout.Spec rowSpec = GridLayout.spec(4, 1f);
//        GridLayout.Spec columnSpec = GridLayout.spec(columnId, 1f);
//        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
//        params.height = 0;
//        params.width = 0;
//        params.setMargins(3, 3, 3, 3);
//        Button button = new Button(SelectActivity.this);
//        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
//        String content = getResources().getString(strId);
//        button.setText(content);
//        button.setTag(R.id.tag_first, flag);
//        if (flag == BTN_NEXT_FLAG | flag == BTN_LATEST_FLAG) {
//            button.setBackgroundResource(R.drawable.bg_btn_origin);
//            button.setTextColor(Color.parseColor("#ffffff"));
//        } else if (flag == BTN_OK_FLAG | flag == BTN_BACK_FLAG) {
//            button.setBackgroundResource(R.drawable.bg_btn_red);
//            button.setTextColor(Color.parseColor("#ffffff"));
//        }
//        button.setOnClickListener(SelectActivity.this);
//        mGridLayout.addView(button, params);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.select_layout);
//        initView();
//        initData();
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
//        contentView.findViewById(R.id.category_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SelectActivity.super.onClick(v);
//                mCategoryPopWindow.dismiss();
//            }
//        });
//        mCategoryAdapter = new CategoryAdapter(null);
//        mCategoryRecycler.setAdapter(mCategoryAdapter);
//        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                SelectActivity.super.onClick(view);
////                SjcCategory category = mCategoryAdapter.getData().get(position);
//                final String categoryId = "";
//                final String name = "";
//                sqlQueryThread.submit(new Runnable() {
//
//                    @SuppressWarnings("unchecked")
//                    @Override
//                    public void run() {
////                        mProList = AppDatabase.getInstance().getProductDao().loadProductByCategoryId(categoryId);
//                        pageNumber = getPageNumber(mProList);
//                        mSelectHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateView(1);
//                                mCategoryPopWindow.dismiss();
//                                mTitleText.setText(String.format("选择商品分类(%s)", name));
//                            }
//                        });
//                    }
//                });
//            }
//        });
//        mCategoryRecycler.setLayoutManager(new GridLayoutManager(this, 4));
//        mCategoryPopWindow.showAtLocation(findViewById(R.id.select_content), Gravity.CENTER, 0, 0);
//        loadCategory();
//    }
//
//    private void loadCategory() {
//        sqlQueryThread.submit(new Runnable() {
//            @Override
//            public void run() {
////                List<SjcCategory> categories = AppDatabase.getInstance().getCategoryDao().loadAll();
//                mSelectHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mCategoryAdapter.setNewData(null);
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mSelectHandler.removeCallbacksAndMessages(null);
//        mProList.clear();
//        mProList = null;
//        mGridLayout = null;
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        Button button = (Button) v;
//        int tag = (int) button.getTag(R.id.tag_first);
//        switch (tag) {
//            case BTN_OK_FLAG:
//                saveProducts();
//                break;
//            case BTN_LATEST_FLAG:
//                curPageNumber -= 1;
//                if (curPageNumber < 1) {
//                    showMessage(R.string.select_first_page);
//                    curPageNumber = 1;
//                } else {
//                    updateView(curPageNumber);
//                }
//                break;
//            case BTN_NEXT_FLAG:
//                curPageNumber += 1;
//                if (curPageNumber > pageNumber) {
//                    showMessage(R.string.select_final_page);
//                    curPageNumber = pageNumber;
//                } else {
//                    updateView(curPageNumber);
//                }
//                break;
//            case BTN_BACK_FLAG:
//                finish();
//                break;
//            default:
//                ProductADB bean = (ProductADB) v.getTag(R.id.tag_second);
//                boolean isSelected;
//                if (bean != null) {
//                    isSelected = true;
//                    if (isSelected) {
//                        // 如果是被选中的再次点击应取消选中
//                        button.setBackgroundResource(R.drawable.shapeone);
//
//                        button.setTextColor(Color.parseColor("#000000"));
//
//                    } else {
//
//                        // 如果没被选中再次点击应选中
//                        button.setBackgroundResource(R.drawable.shapethr);
//
//                        button.setTextColor(Color.parseColor("#ffffff"));
//                        selectedList.add(bean);
//                    }
//                }
//                break;
//        }
//    }
//
//    private void deleteSelectedProduct(SjcProduct product) {
//        String goodsName = product.getGoodsName();
//        for (int i = 0; i < selectedList.size(); i++) {
//            String name = selectedList.get(i).getGoodsName();
//            if (name.equals(goodsName)) {
//                selectedList.remove(i);
//                break;
//            }
//        }
//    }
//
//    /**
//     * 保存已选中的商品至本地文件
//     */
//    private void saveProducts() {
//        LocalFileTag tag = FileHelp.writeToProducts(selectedList);
//        if (tag != null && tag.isSuccess()) {
//            EventBus.getDefault().post(selectedList);
//            finish();
//        } else {
//            String content = tag == null ? "" : tag.getContent();
//            String error = getString(R.string.select_save_warnning) + ":" + content;
//            showMessage(error);
//            finish();
//        }
//    }
//
//    /**
//     * 切换页码刷新显示
//     */
//    protected void updateView(int curPage) {
//        for (int i = 0; i < PLU_AMOUNT; i++) {// 初始化当前页面的元素
//            Button btn = (Button) mGridLayout.getChildAt(i);
//            btn.setTag(R.id.tag_first, null);
//            btn.setTag(R.id.tag_second, null);
//            btn.setText("");
//            btn.setTextColor(Color.parseColor("#000000"));
//            btn.setBackgroundResource(R.drawable.shapeone);
//        }
//
//        for (int i = 0; i < PLU_AMOUNT; i++) {
//
//            int index = (curPage - 1) * PLU_AMOUNT + i;
//
//            Button btn = (Button) mGridLayout.getChildAt(i);
//
//            btn.setTag(R.id.tag_first, i);
//
//            if (index < mProList.size()) {
//
//                ProductADB bean = mProList.get(index);
//
//                String content = bean.getPro_name();
//
//                btn.setTag(R.id.tag_second, bean);
//
//                int textLength = content == null ? 0 : content.length();
//
//                int textSize = getAutoTextSize(textLength);
//
//                btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
//
//                btn.setText(content);
//
//            } else {
//
//                btn.setTag(R.id.tag_second, null);
//
//            }
//
//            btn.setBackgroundResource(R.drawable.shapeone);
//
//            btn.setTextColor(Color.parseColor("#000000"));
//
//        }
//        checkView();
//    }
//
//    private void initView() {
//        mGridLayout = findViewById(R.id.select_grid_layout);
//        mTitleText = findViewById(R.id.search_product_title);
//        mTitleText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SelectActivity.super.onClick(v);
//                showCategorySelectWindow();
//            }
//        });
//    }
//
//    private void initData() {
//        sqlQueryThread.submit(new Runnable() {
//
//            @Override
//            public void run() {
//                mProList = AppDatabase.getInstance().getProductDao().loadAll();
//                selectedList = checkSelectedData(mProList);
//                mSelectHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
//            }
//        });
//    }
//
//    /**
//     * 获取加载页数
//     */
//    protected int getPageNumber(List<ProductADB> list) {
//
//        int pageNumber = list.size() / PLU_AMOUNT;
//
//        int lessNumber = list.size() % PLU_AMOUNT;
//
//        if (lessNumber != 0) {
//
//            pageNumber += 1;
//        }
//        return pageNumber;
//    }
//
//    private static class SelectHandler extends Handler {
//
//        WeakReference<SelectActivity> mWeakReference;
//
//        SelectHandler(WeakReference<SelectActivity> weakReference) {
//            this.mWeakReference = weakReference;
//        }
//
//        @Override
//        public void handleMessage(Message msgs) {
//            super.handleMessage(msgs);
//            SelectActivity activity = mWeakReference.get();
//            if (activity != null && msgs.what == MSG_UPDATE_VIEW) {
//                activity._handleDataInit();
//            }
//        }
//    }
//}
