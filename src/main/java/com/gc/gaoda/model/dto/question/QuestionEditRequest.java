package com.gc.gaoda.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑题目请求
 *
 * @autho gc
 * @from 
 */
@Data
public class QuestionEditRequest implements Serializable {
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