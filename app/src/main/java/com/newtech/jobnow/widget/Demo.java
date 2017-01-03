package com.newtech.jobnow.widget;

import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtech.jobnow.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by manhi on 9/8/2016.
 */
public enum Demo {


    CUSTOM_TAB_ICONS1(R.string.tab, R.layout.custom_tab) {
        @Override
        public int[] tabs() {
            return new int[]{
                    R.string.myProfile,
                    R.string.experience,
                    R.string.skills
            };
        }

        @Override
        public void setup(SmartTabLayout layout) {
            super.setup(layout);

            final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
            final Resources res = layout.getContext().getResources();

            layout.setCustomTabView(new SmartTabLayout.TabProvider() {
                @Override
                public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                    View view = inflater.inflate(R.layout.custom_tab, container,
                            false);
                    ImageView icon = (ImageView) view.findViewById(R.id.ic_tab);
                    TextView title = (TextView) view.findViewById(R.id.custom_text);
                    switch (position) {
                        case 0:
                            icon.setImageDrawable(res.getDrawable(R.mipmap.ic_profile_tab));
                            title.setText(tabs()[0]);
                            break;
                        case 1:
                            icon.setImageDrawable(res.getDrawable(R.mipmap.ic_exprience));
                            title.setText(tabs()[1]);
                            break;
                        case 2:
                            icon.setImageDrawable(res.getDrawable(R.mipmap.ic_skill));
                            title.setText(tabs()[2]);
                            break;
                        default:
                            throw new IllegalStateException("Invalid position: " + position);
                    }
                    return icon;
                }
            });
        }
    };

    public final int titleResId;
    public final int layoutResId;

    Demo(int titleResId, int layoutResId) {
        this.titleResId = titleResId;
        this.layoutResId = layoutResId;
    }

//    public void startActivity(Context context) {
//        DemoActivity.startActivity(context, this);
//    }

    public void setup(final SmartTabLayout layout) {
        //Do nothing.
    }

    public static int[] tab10() {
        return new int[]{
                R.string.myProfile,
                R.string.experience,
                R.string.skills
        };
    }

    public int[] tabs() {
        return tab10();
    }
}
