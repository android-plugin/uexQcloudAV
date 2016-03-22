/*
 * Copyright (c) 2016.  The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.zywx.wbpalmstar.plugin.uexqcloudav.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ylt on 16/3/21.
 */
public class StartVO implements Serializable {

    private static final long serialVersionUID = 556023244243639660L;

    private int startSeconds;

    private int index;

    private List<VideoInfoVO> data;


    public int getStartSeconds() {
        return startSeconds;
    }

    public void setStartSeconds(int startSeconds) {
        this.startSeconds = startSeconds;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<VideoInfoVO> getData() {
        return data;
    }

    public void setData(List<VideoInfoVO> data) {
        this.data = data;
    }
}
