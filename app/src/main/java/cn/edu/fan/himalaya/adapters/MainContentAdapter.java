package cn.edu.fan.himalaya.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cn.edu.fan.himalaya.utils.FragmentCreator;

//显示主内容的适配器
public class MainContentAdapter extends FragmentPagerAdapter {
    public MainContentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentCreator.getFragment(position); // 返回要显示的fragment
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
