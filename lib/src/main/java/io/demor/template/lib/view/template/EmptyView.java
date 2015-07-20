package io.demor.template.lib.view.template;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.demor.template.lib.xml.nodes.BaseViewNode;
import io.demor.template.lib.xml.nodes.Text;

public final class EmptyView extends BaseViewRender<BaseViewNode> {

    View mView;

    protected EmptyView(final Context context, final Text node, final ViewGroup parentView) {
        super(context, node, parentView);
    }

    @Override
    protected void setData(final String data) {
    }

    @Override
    protected String getData() {
        return null;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    protected void initView(final Context context, BaseViewNode node) {
    }

    @Override
    protected void onCreateView(final Context context) {
        mView = new View(context);
    }
}
