package com.gc.gaoda.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目请求
 *
 * @autho gc
 * @from 
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */

    private Long id;

    /**
     * 题目内容
     */
    private QuestionContentDTO questionContent;

    private static final long serialVersionUID = 1L;
}