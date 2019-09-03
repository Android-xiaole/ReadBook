package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;
import com.jj.comics.adapter.mine.VIPAdapter;

import java.util.List;

public class SelectionWithGood extends AbstractExpandableItem<GoodsPriceModel> implements IModel, MultiItemEntity {
    private Selection section;
    private List<GoodsPriceModel> goodsPricelList;

    public Selection getSection() {
        return section;
    }

    public void setSection(Selection section) {
        this.section = section;
    }

    public List<GoodsPriceModel> getGoodsPricelList() {
        return goodsPricelList;
    }

    public void setGoodsPricelList(List<GoodsPriceModel> goodsPricelList) {
        this.goodsPricelList = goodsPricelList;
    }

    @Override
    public NetError error() {
        return null;
    }

    @Override
    public int getItemType() {
        return VIPAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
