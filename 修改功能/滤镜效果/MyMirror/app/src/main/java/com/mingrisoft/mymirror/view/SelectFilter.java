/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mingrisoft.mymirror.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.LinkedList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

public class SelectFilter {
    /**
     * 显示弹窗
     * @param context 上下文用于创建弹窗
     * @param listener 用于返回设置的滤镜的类型
     */
    public static void showDialog(final Context context,
                                  final OnFilterChooseListener listener) {
        final FilterList filters = new FilterList();
        filters.addFilter("反相", FilterType.反相);//添加数据
        filters.addFilter("正常", FilterType.正常);//添加数据
        filters.addFilter("复古", FilterType.复古);//添加数据
        filters.addFilter("浮雕", FilterType.浮雕);//添加数据
        filters.addFilter("模糊", FilterType.模糊);//添加数据
        filters.addFilter("黑白", FilterType.黑白);//添加数据
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择滤镜的效果");//设置标题
        //设置Item点击事件
        builder.setItems(filters.names.toArray(new String[filters.names.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.OnFilterChooseListener(
                                createFilterForType(filters.filters.get(which)));
                    }
                });
        builder.create().show();//显示弹窗
    }

    /**
     * 用于判断弹窗中Item的点击事件
     * @param type
     * @return
     */
    private static GPUImageFilter createFilterForType(final FilterType type) {
        switch (type) {
            case 反相:
                return new GPUImageColorInvertFilter();
            case 正常:
                return new GPUImagePixelationFilter();
            case 复古:
                return new GPUImageSepiaFilter();
            case 浮雕:
                return new GPUImageEmbossFilter();
            case 模糊:
                return new GPUImagePosterizeFilter();
            case 黑白:
                return new GPUImageGrayscaleFilter();
            default:
                throw new IllegalStateException("没有该类型的滤镜！");
        }

    }

    /**
     * 回调接口，用于获取滤镜
     */
    public interface OnFilterChooseListener {
        void OnFilterChooseListener(GPUImageFilter filter);
    }

    /**
     * 枚举设置滤镜的类型
     */
    private enum FilterType {
        反相, 正常, 复古, 浮雕, 模糊,黑白
    }

    /**
     * 该类用于储存弹窗中将要展示滤镜的信息
     */
    private static class FilterList {
        public List<String> names = new LinkedList();//滤镜名称
        public List<FilterType> filters = new LinkedList();//滤镜类型

        /**
         * 设置滤镜名称和滤镜的类型
         * @param name
         * @param filter
         */
        public void addFilter(final String name, final FilterType filter) {
            names.add(name);
            filters.add(filter);
        }
    }
}
