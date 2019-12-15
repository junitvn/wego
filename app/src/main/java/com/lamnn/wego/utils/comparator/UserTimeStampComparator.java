package com.lamnn.wego.utils.comparator;

import com.lamnn.wego.data.model.UserMessage;

import java.util.Comparator;

public class UserTimeStampComparator implements Comparator<UserMessage> {
    @Override
    public int compare(UserMessage o1, UserMessage o2) {
        if (Integer.parseInt(o1.getTimeStamp().getSeconds()) == Integer.parseInt(o2.getTimeStamp().getSeconds())) {
            return 0;
        } else if (Integer.parseInt(o1.getTimeStamp().getSeconds()) > Integer.parseInt(o2.getTimeStamp().getSeconds())) {
            return 1;
        } else
            return -1;
    }
}
