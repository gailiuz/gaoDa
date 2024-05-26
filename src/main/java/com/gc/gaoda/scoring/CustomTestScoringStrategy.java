package com.gc.gaoda.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gc.gaoda.model.dto.question.QuestionContentDTO;
import com.gc.gaoda.model.entity.App;
import com.gc.gaoda.model.entity.Question;
import com.gc.gaoda.model.entity.ScoringResult;
import com.gc.gaoda.model.entity.UserAnswer;
import com.gc.gaoda.model.vo.QuestionVO;
import com.gc.gaoda.service.QuestionService;
import com.gc.gaoda.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 自定义测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;


    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //1.根据id查询到的题目和题目结果信息
        Long appId = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultsList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
        );

        //2.根据用户选择的答案和题目结果信息，计算得分
        HashMap<String, Integer> optionCount = new HashMap<>();//统计：空间换时间
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        //遍历题目列表
        for(QuestionContentDTO questionContentDTO : questionContent){
            //遍历答案列表
            for(String answer : choices){
                //遍历题目中的选项
                for(QuestionContentDTO.Option option : questionContentDTO.getOptions()){
                    //如果答案和选择的key匹配
                    if(option.getKey().equals(answer)){
                        //获取选项的result
                        String result = option.getResult();
                        //如果result属性不在optionCount中，初始化为0
                        if(!optionCount.containsKey(result)){
                            optionCount.put(result, 0);
                        }

                        //将result对应的值加1
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }

        //3.计算每个属性对应的得分
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultsList.get(0);
        //遍历评分结果列表
        for(ScoringResult scoringResult : scoringResultsList){
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            //计算当前评分结果分数：[I,F]=>[10,5]=>[15]
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                    .sum();

            //如果分数高于当前最高分数，则更新
            if(score > maxScore){
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }



        //4.构造返回值，填充答案对象属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());


        return userAnswer;
    }
}
