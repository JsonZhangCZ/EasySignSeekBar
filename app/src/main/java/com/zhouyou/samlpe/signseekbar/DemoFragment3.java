package com.zhouyou.samlpe.signseekbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.ArrayList;

public class DemoFragment3 extends Fragment {

    public static DemoFragment3 newInstance() {
        return new DemoFragment3();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_3, container, false);
        SignSeekBar signSeekBar1 = (SignSeekBar) view.findViewById(R.id.demo_3_seek_bar_1);
        SignSeekBar signSeekBar2 = (SignSeekBar) view.findViewById(R.id.demo_3_seek_bar_2);
        SignSeekBar signSeekBar3 = (SignSeekBar) view.findViewById(R.id.demo_3_seek_bar_3);
        SignSeekBar signSeekBar4 = (SignSeekBar) view.findViewById(R.id.demo_3_seek_bar_4);
        SignSeekBar signSeekBar5 = (SignSeekBar) view.findViewById(R.id.demo_3_seek_bar_5);

        signSeekBar1.getConfigBuilder()
                .min(0)
                .max(50)
                .progress(20)
                .sectionCount(5)
                .trackColor(ContextCompat.getColor(getContext(), R.color.color_gray))
                .secondTrackColor(ContextCompat.getColor(getContext(), R.color.color_blue))
                .thumbColor(ContextCompat.getColor(getContext(), R.color.color_blue))
                .showSectionText()
                .sectionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .sectionTextSize(18)
                .showThumbText()
                .thumbTextColor(ContextCompat.getColor(getContext(), R.color.color_red))
                .thumbTextSize(18)
                //.signColor(ContextCompat.getColor(getContext(), R.color.color_green))
                //.signTextSize(18)
                .showSectionMark()
                .seekBySection()
                .autoAdjustSectionMark()
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        signSeekBar2.getConfigBuilder()
                .min(-50)
                .max(50)
                .sectionCount(10)
                .sectionTextInterval(2)
                .trackColor(ContextCompat.getColor(getContext(), R.color.color_red_light))
                .secondTrackColor(ContextCompat.getColor(getContext(), R.color.color_red))
                .seekBySection()
                .showSectionText()
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        signSeekBar3.getConfigBuilder()
                .min(1)
                .max(1.5f)
                .floatType()
                .sectionCount(10)
                .secondTrackColor(ContextCompat.getColor(getContext(), R.color.color_green))
                .showSectionText()
                .showThumbText()
                .build();

        signSeekBar4.getConfigBuilder()
                .min(-0.4f)
                .max(0.4f)
                .progress(0)
                .floatType()
                .sectionCount(10)
                .sectionTextInterval(2)
                .showSectionText()
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .autoAdjustSectionMark()
                .build();
        ArrayList<Float> arr = new ArrayList<Float>();
        arr.add(1000f);
        arr.add(18000f);
        arr.add(16000f);
        arr.add(11000f);
        arr.add(13000f);
        arr.add(19000f);
        signSeekBar5.setMax(28254);
        signSeekBar5.setCacheProgress(28000);
        signSeekBar5.getConfigBuilder()
                .customSectionArrayFloat(arr)
                .build();

        return view;
    }

}
