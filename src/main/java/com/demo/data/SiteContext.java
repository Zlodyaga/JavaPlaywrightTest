package com.demo.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SiteContext {
    int segmentNumber;
    int brandNumber;

    public SiteContext(int segmentNumber, int brandNumber) {
        this.segmentNumber = segmentNumber;
        this.brandNumber = brandNumber;
    }
}
