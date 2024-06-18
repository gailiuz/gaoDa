package com.gc.gaoda.manager;

import com.gc.gaoda.common.ErrorCode;
import com.gc.gaoda.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*
* 通用AI调用
* */
@Component
public class AiManager {

    @Resource
    private ClientV4 clientV4;

    //稳定随机数
    private static final float STABLE_TEMPERATURE = 0.5f;
    //不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;


    /**
     * 同步请求（答案不稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnStableRequest(String systemMessage ,String userMessage){
        return doRequest(systemMessage,userMessage,Boolean.FALSE,UNSTABLE_TEMPERATURE);
    }
    /**
     * 同步请求（答案稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage ,String userMessage){
        return doRequest(systemMessage,userMessage,Boolean.FALSE,STABLE_TEMPERATURE);
    }
    /**
     * 同步请求
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage ,String userMessage,Float temperature){
        return doRequest(systemMessage,userMessage,Boolean.FALSE,temperature);
    }

    /**
     * 通用请求(简化信息传递)
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String systemMessage ,String userMessage,Boolean stream,Float temperature){

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        chatMessages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));
        return doRequest(chatMessages,stream,temperature);
    }


    /**
     * 通用请求
     * @param messages
     * @param stream
     * @param temperature
     * @return
     */

    public String doRequest(List<ChatMessage> messages,Boolean stream,Float temperature){


        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }

    }

}
