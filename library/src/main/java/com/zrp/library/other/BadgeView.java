/*
 * Copyright (C) 2016 sanchi3 Inc.
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
package com.zrp.library.other;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class BadgeView extends TextView {

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * set default params
     */
    private void init() {
    }

    /**
     * converts dip to px
     */
    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * set the badge as a point
     */
    public void setPoint() {
        setTextColor(Color.TRANSPARENT);
        getLayoutParams().height = dip2Px(9);
        getLayoutParams().width = dip2Px(9);
        this.setLayoutParams(getLayoutParams());
    }
}