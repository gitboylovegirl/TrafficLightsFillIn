package com.fred.trafficlightsfillin.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Describe index:相对于List集合的位置,position:表示在adapter中的位置
 * @Author : zhaolu
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<  BaseViewHolder> {

    public static final View inflaterView(@LayoutRes int id, RecyclerView recyclerView) {
        return LayoutInflater.from(recyclerView.getContext())
                .inflate(id, recyclerView, false);
    }

    private static final int HEADER_VIEW_TYPE = -10000;
    private static final int FOOTER_VIEW_TYPE = -20000;
    private final List<View> mHeaders = new ArrayList<View>();
    private final List<View> mFooters = new ArrayList<View>();
    private List<T> dataList = new ArrayList<T>();
    protected RecyclerView attachedRecyclerView;

    public List<T> getData() {
        return dataList;
    }

    public int getDataSize() {
        return getData().size();
    }

    public boolean isDataEmpty() {
        return getDataSize() <= 0;
    }

    public int getHeaderCount() {
        return mHeaders.size();
    }


    public int getFooterCount() {
        return mFooters.size();
    }

    public BaseRecyclerAdapter(@NonNull List<T> data) {
        this.dataList = data == null ? new ArrayList<T>() : data;
    }

    public BaseRecyclerAdapter() {
        this(new ArrayList<T>());
    }

    @Nullable
    @CheckResult
    public View addHeader(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null header!");
        }
        mHeaders.add(view);
        notifyDataSetChanged();
        return view;
    }

    @Nullable
    @CheckResult
    public View addHeader(@LayoutRes int id, @NonNull RecyclerView recyclerView) {
        return addHeader(inflaterView(id, recyclerView));
    }

    @Nullable
    @CheckResult
    public View addFooter(@NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("You can't have a null footer!");
        }
        mFooters.add(view);
        notifyDataSetChanged();
        return view;
    }

    @Nullable
    @CheckResult
    public View addFooter(@LayoutRes int id, @NonNull RecyclerView recyclerView) {
        return addFooter(inflaterView(id, recyclerView));
    }

    @Nullable
    @CheckResult
    public View getHeader(int index) {
        return index < mHeaders.size() ? mHeaders.get(index) : null;
    }

    @Nullable
    @CheckResult
    public View getFooter(int index) {
        return index < mFooters.size() ? mFooters.get(index) : null;
    }

    private boolean isHeader(int viewType) {
        return viewType >= HEADER_VIEW_TYPE && viewType < (HEADER_VIEW_TYPE + mHeaders.size());
    }

    public boolean isHeader(View view) {
        return mHeaders.contains(view);
    }

    public boolean removeHeader(View view) {
        if (getHeaderCount() > 0) {
            if (mHeaders.contains(view)) {
                boolean remove = mFooters.remove(view);
                notifyDataSetChanged();
                return remove;
            }
        }
        return false;
    }

    public boolean removeFooter(View view) {
        if (getFooterCount() > 0) {
            if (mFooters.contains(view)) {
                boolean remove = mFooters.remove(view);
                notifyDataSetChanged();
                return remove;
            }
        }
        return false;
    }

    private boolean isFooter(int viewType) {
        return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + mFooters.size());
    }


    /**
     * @param isRefresh 是否下拉刷新
     * @param datas
     * @return
     */
    public boolean bindData(boolean isRefresh, @NonNull List<T> datas) {
        if (isRefresh) {
            //下拉刷新
            getData().clear();
            if (checkList(datas)) {
                getData().addAll(datas);
            }
            notifyDataSetChanged();
            return true;
        } else {
            //上拉加载 不能为空,并且不包含
            if (checkList(datas)
                    && !getData().contains(datas)) {
                getData().addAll(datas);
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public void clearData() {
        getData().clear();
        notifyDataSetChanged();
    }

    /**
     * 检查index是否有效
     *
     * @param index
     * @return true 有效 false 无效
     */
    private boolean checkIndex(@IntRange(from = 0) int index) {
        return index >= 0 && index < dataList.size();
    }

    /**
     * 检查add index是否有效
     *
     * @param addIndex
     * @return true 有效 false 无效
     */
    private boolean checkAddIndex(@IntRange(from = 0) int addIndex) {
        return addIndex >= 0 && addIndex <= dataList.size();
    }

    /**
     * 检查list是否有效
     *
     * @param datas
     * @return true 不空
     */
    private boolean checkList(List<? extends T> datas) {
        return datas != null && !datas.isEmpty();
    }

    /**
     * 检查对象是否有效
     *
     * @return true 不空
     */
    private boolean checkItem(T t) {
        return t != null;
    }

    /**
     * @param index 相对于List的位置
     * @return
     */
    public T getItem(@IntRange(from = 0) int index) {
        if (checkIndex(index)) {
            return getData().get(index);
        }
        return null;
    }

    /**
     * 获取角标
     *
     * @param t
     * @return
     */
    public int getIndex(@NonNull T t) {
        return getData().indexOf(t);
    }

    /**
     * @param index 相对于List的位置
     * @param t
     * @return
     */
    public final boolean addItem(@IntRange(from = 0) int index, @Nullable T t) {
        if (checkAddIndex(index)
                && checkItem(t)
                && !getData().contains(t)) {
            getData().add(index, t);
            int internalPosition = index + getHeaderCount();
            notifyItemInserted(internalPosition);
            return true;
        }
        return false;
    }

    public final boolean addItems(@IntRange(from = 0) int index, @NonNull List<? extends T> datas) {
        if (checkList(datas)
                && checkAddIndex(index)
                && !getData().containsAll(datas)) {
            if (getData().addAll(index, datas)) {
                int internalPosition = index + getHeaderCount();
                notifyItemRangeInserted(internalPosition, datas.size());
                return true;
            }
        }
        return false;
    }

    public final boolean addItems(@NonNull List<? extends T> datas) {
        if (checkList(datas)
                && !getData().containsAll(datas)) {
            int internalPosition = getData().size() + getHeaderCount();
            if (getData().addAll(datas)) {
                notifyItemRangeInserted(internalPosition, datas.size());
                return true;
            }
        }
        return false;
    }

    public final boolean addItem(@NonNull T t) {
        if (checkItem(t)
                && !getData().contains(t)) {
            int internalPosition = getData().size() + getHeaderCount();
            if (getData().add(t)) {
                notifyItemInserted(internalPosition);
            }
        }
        return false;
    }

    /**
     * 更新item
     *
     * @param t
     * @return
     */
    public final boolean updateItem(@NonNull T t) {
        if (checkItem(t)) {
            int index = getIndex(t);
            if (index >= 0) {
                getData().set(index, t);
                int internalPosition = index + getHeaderCount();
                notifyItemChanged(internalPosition);
                return true;
            }
        }
        return false;
    }

    /**
     * @param index 相对于List的位置
     * @return
     */
    public final boolean removeItem(@IntRange(from = 0) int index) {
        if (checkIndex(index)) {
            getData().remove(index);
            int internalPosition = index + getHeaderCount();
            notifyItemRemoved(internalPosition);
            return true;
        }
        return false;
    }


    /**
     * 移除item
     *
     * @param t
     * @return
     */
    public final boolean removeItem(@NonNull T t) {
        return removeItem(getIndex(t));
    }


    /**
     * 初始化布局
     *
     * @param viewType
     * @return
     */
    @LayoutRes
    public abstract int bindView(int viewType);

    /**
     * 初始化item
     *
     * @param holder
     * @param t
     * @param index  相对于List的位置
     */
    public abstract void onBindHolder(BaseViewHolder holder, @Nullable T t, int index);

    @Override
    public final   BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE);
            View headerView = mHeaders.get(whichHeader);
            return new   BaseViewHolder(this, headerView, false);
        } else if (isFooter(viewType)) {
            int whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE);
            View footerView = mFooters.get(whichFooter);
            return new   BaseViewHolder(this, footerView, false);
        } else {
            return onCreateHolder(viewGroup, viewType);
        }
    }

    /**
     * 创建viewHolder
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    public   BaseViewHolder onCreateHolder(ViewGroup viewGroup, int viewType) {
          BaseViewHolder viewHolder = new   BaseViewHolder(this, LayoutInflater.from(viewGroup.getContext())
                .inflate(bindView(viewType), viewGroup, false), true);
        return viewHolder;
    }


    /**
     * 每条布局的type
     *
     * @param index 相对于List的位置
     * @return
     */
    public int getViewType(int index) {
        return 0;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return HEADER_VIEW_TYPE + position;
        } else if (position < (getHeaderCount() + getData().size())) {
            return getViewType(position - getHeaderCount());
        } else {
            return FOOTER_VIEW_TYPE + position - getHeaderCount() - getData().size();
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getDataSize() + getFooterCount();
    }

    @Override
    public final void onBindViewHolder(  BaseViewHolder holder, int position) {
        if (position >= getHeaderCount() && position < getHeaderCount() + getData().size()) {
            int index = position - getHeaderCount();
            onBindHolder(holder, getItem(index), index);
        }
    }


    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected OnItemChildClickListener onItemChildClickListener;
    protected OnItemChildLongClickListener onItemChildLongClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.onItemLongClickListener = l;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener l) {
        this.onItemChildLongClickListener = l;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener l) {
        this.onItemChildClickListener = l;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        attachedRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        attachedRecyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    @Nullable
    public Context getContext() {
        return attachedRecyclerView != null ? attachedRecyclerView.getContext() : null;
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @return
     */
    @NonNull
    public CharSequence getContextString(@StringRes int resId) {
        return getContextString(resId, "");
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @param formatArgs
     * @return
     */
    public CharSequence getContextString(@StringRes int resId, Object... formatArgs) {
        return getContextString(resId, "", formatArgs);
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @param defaultStr
     * @return
     */
    public CharSequence getContextString(@StringRes int resId, CharSequence defaultStr) {
        try {
            return getContext().getString(resId);
        } catch (android.content.res.Resources.NotFoundException e) {
        }
        return defaultStr;
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @param defaultStr
     * @param formatArgs
     * @return
     */
    public CharSequence getContextString(@StringRes int resId, CharSequence defaultStr, Object... formatArgs) {
        try {
            return getContext().getString(resId, formatArgs);
        } catch (android.content.res.Resources.NotFoundException e) {
        }
        return defaultStr;
    }

    /**
     * ContextCompat.getColor() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     * 默认返回值为黑色
     *
     * @param id
     */
    @ColorInt
    public int getContextColor(@ColorRes int id) {
        return getContextColor(id, Color.BLACK);
    }

    /**
     * ContextCompat.getColor() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     *
     * @param id 如果不存在返回默认值 does not exist.
     */
    @ColorInt
    public int getContextColor(@ColorRes int id, @ColorInt int defaultColor) {
        try {
            return ContextCompat.getColor(getContext(), id);
        } catch (android.content.res.Resources.NotFoundException e) {
        }
        return defaultColor;
    }


    /**
     * ContextCompat.getDrawable() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     * 默认返回值为黑色
     *
     * @param id
     */
    public Drawable getContextDrawable(@DrawableRes int id) {
        return getContextDrawable(id, R.mipmap.ic_launcher);
    }

    /**
     * ContextCompat.getDrawable() 将抛出异常,也没判断contexts是否为空
     * android.content.res.Resources.NotFoundException
     *
     * @param id 如果不存在返回默认值 does not exist.
     */
    @Nullable
    public Drawable getContextDrawable(@DrawableRes int id, @DrawableRes int defaultDrawable) {
        Drawable drawable = null;
        try {
            drawable = ContextCompat.getDrawable(getContext(), id);
        } catch (android.content.res.Resources.NotFoundException e) {
            try {
                drawable = ContextCompat.getDrawable(getContext(), defaultDrawable);
            } catch (android.content.res.Resources.NotFoundException e1) {
            }
        }
        return drawable;
    }

    public interface OnItemClickListener {

        /**
         * @param adapter
         * @param holder
         * @param itemView
         * @param index    相对于List容器的位置
         */
        void onItemClick(  BaseRecyclerAdapter adapter,   BaseViewHolder holder, View itemView, int index);
    }

    public interface OnItemLongClickListener {

        /**
         * @param adapter
         * @param holder
         * @param itemView
         * @param index    相对于List容器的位置
         * @return
         */
        boolean onItemLongClick(  BaseRecyclerAdapter adapter,   BaseViewHolder holder, View itemView, int index);
    }

    public interface OnItemChildClickListener {

        /**
         * @param adapter
         * @param holder
         * @param childView
         * @param index     相对于List容器的位置
         */
        void onItemChildClick(  BaseRecyclerAdapter adapter,   BaseViewHolder holder, View childView, int index);
    }

    public interface OnItemChildLongClickListener {
        /**
         * @param adapter
         * @param holder
         * @param childView
         * @param index     相对于List容器的位置
         * @return
         */
        boolean onItemChildLongClick(  BaseRecyclerAdapter adapter,   BaseViewHolder holder, View childView, int index);
    }
}