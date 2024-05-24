package com.gc.gaoda.common;

import lombok.Data;

/**
 * 审核请求
 */
import java.io.Serializable;
@Data
public class ReviewRequest implements Serializable {
    /**
     *  id
     */

    private Long id;

    /**
     * 状态：
     */
    private Integer reviewStatus;

    /**
     * 审核信息·
     */
    private String reviewMessage;


    private static final long serialVersionUID = 1L;
}
