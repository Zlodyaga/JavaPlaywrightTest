package com.demo.data;

import lombok.Getter;

@Getter
public class HookContext extends SiteContext {
    String hookName;
    int patternNumber;

    public HookContext(String hookName, int patternNumber, int segmentNumber, int brandNumber) {
        super(segmentNumber, brandNumber);
        this.hookName = hookName;
        this.patternNumber = patternNumber;
    }
}
