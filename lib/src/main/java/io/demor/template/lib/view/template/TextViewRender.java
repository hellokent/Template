package io.demor.template.lib.view.template;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import io.demor.template.lib.L;
import io.demor.template.lib.view.ViewNodeClass;
import io.demor.template.lib.xml.nodes.Text;

@ViewNodeClass(Text.class)
public class TextViewRender extends BaseViewRender<Text> {

    TextView mTextView;

    public TextViewRender(final Context context, final Text node, final ViewGroup parentView) {
        super(context, node, parentView);
    }

    @Override
	protected void setData(final String data) {
		mTextView.setText(data);
	}

    @Override
    protected String getData() {
        return mTextView.getText().toString();
    }

    @Override
    public TextView getView() {
        return mTextView;
    }

    @Override
    protected void initView(final Context context, Text node) {
        super.initView(context, node);
        L.v("set textView with data:%s", node.getValue());
        if (!TextUtils.isEmpty(node.textColor)){
            mTextView.setTextColor(Color.parseColor(node.textColor));
        }
        mTextView.setText(node.getValue());
    }

    @Override
    protected void onCreateView(final Context context) {
        mTextView = new TextView(context);
    }
}
