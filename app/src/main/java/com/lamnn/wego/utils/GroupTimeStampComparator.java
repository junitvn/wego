package com.lamnn.wego.utils;

import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.UserMessage;

import java.util.Comparator;

public class GroupTimeStampComparator implements Comparator<GroupMessage> {
    @Override
    public int compare(GroupMessage o1, GroupMessage o2) {
        if (Integer.parseInt(o1.getTimeStamp().getSeconds()) == Integer.parseInt(o2.getTimeStamp().getSeconds())) {
            return 0;
        } else if (Integer.parseInt(o1.getTimeStamp().getSeconds()) > Integer.parseInt(o2.getTimeStamp().getSeconds())) {
            return 1;
        } else
            return -1;
    }
}
