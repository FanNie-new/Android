package cn.edu.fan.himalaya.utils;

import java.util.HashMap;
import java.util.Map;

import cn.edu.fan.himalaya.base.BaseFragment;
import cn.edu.fan.himalaya.fragments.AlbumFragment;
import cn.edu.fan.himalaya.fragments.HistoryFragment;
import cn.edu.fan.himalaya.fragments.SubscriptionFragment;

//缓存
public class FragmentCreator {

    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTION = 1;
    public final static int INDEX_HISTORY = 2;

    public final static int PAGE_COUNT = 3;
    //用来缓存已经创建过的fragment
    private static Map<Integer, BaseFragment> sCashe = new HashMap<>();

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCashe.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }

        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new AlbumFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }

        sCashe.put(index,baseFragment);
        return baseFragment;
    }
}
