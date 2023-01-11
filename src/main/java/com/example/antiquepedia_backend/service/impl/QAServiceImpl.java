package com.example.antiquepedia_backend.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.antiquepedia_backend.Entity.Answer;
import com.example.antiquepedia_backend.Entity.Place;
import com.example.antiquepedia_backend.Entity.Question;
import com.example.antiquepedia_backend.service.ManMadeObjectService;
import com.example.antiquepedia_backend.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.antiquepedia_backend.utils.HttpUtil.sendPost;

@Service
public class QAServiceImpl implements QAService {

    @Autowired
    ManMadeObjectService manMadeObjectService;

    @Override
    public Answer answer(Question question) {
        Answer answer = new Answer();

        Map<String, String> standardQuestion = new HashMap<String, String>();
        standardQuestion.put("它的位置是什么", "where");
        standardQuestion.put("它是哪里的", "where");
        standardQuestion.put("它是什么地方的", "where");
        standardQuestion.put("它的名字是什么", "label");
        standardQuestion.put("它是什么时期的", "when");
        standardQuestion.put("它被制作于什么时候", "when");
        standardQuestion.put("它的引文有哪些", "reference");
        standardQuestion.put("它被哪些文章引用", "reference");
        standardQuestion.put("它的图片有哪些", "photo");
        standardQuestion.put("它长什么样子", "photo");
        standardQuestion.put("它是什么样子的", "photo");
        standardQuestion.put("以上都不是", "unknown");

        String uri = question.getEntity_uri();
        String question_content = question.getContent();
        if(standardQuestion.containsKey(question_content)){
            // 命中问题
            String q_type = standardQuestion.get(question_content);
            switch (q_type){
                case "where":
                    Place p = manMadeObjectService.getPlaceByURI(uri);
                    String p_info = "";
                    if(!(p.getLabel()==null||p.getLabel().equals(""))){
                        p_info += ("\n它的位置： " + p.getLabel());
                    }
                    if(!(p.getFallWithin()==null||p.getFallWithin().equals(""))){
                        p_info += ("\n所处城市： " + p.getFallWithin());
                    }
                    if(!(p.getLongitude()==null||p.getLongitude().equals(""))){
                        p_info += ("\n经度： " + p.getLongitude());
                    }
                    if(!(p.getLatitude()==null||p.getLatitude().equals(""))){
                        p_info += ("\n纬度： " + p.getLatitude());
                    }
                    answer.setContent(p_info);
                    break;
                case "label":
                    List<String> labels = manMadeObjectService.getLabelsByURI(uri);
                    String label_info = "";
                    for(String l : labels){
                        label_info += (l + "\n");
                    }
                    answer.setContent(label_info);
                    break;
                case "when":
                    String periodBegin = manMadeObjectService.getPeriodBeginByURI(uri);
                    String periodEnd = manMadeObjectService.getPeriodEndByURI(uri);
                    String period_info = "";
                    if(!(periodBegin==null||periodBegin.equals(""))){
                        period_info = periodBegin;
                    }
                    if(!(periodEnd==null||periodEnd.equals(""))){
                        if(period_info.equals("")){
                            period_info = periodEnd;
                        }else{
                            period_info += (" ~ " + periodEnd);
                        }
                    }
                    answer.setContent(period_info);
                    break;
                case "reference":
                    List<String> refers = manMadeObjectService.getRefersByURI(uri);
                    String refer_info = "";
                    for(String r : refers){
                        refer_info += (r + "\n");
                    }
                    answer.setContent(refer_info);
                    break;
                case "photo":
                    List<String> photos = manMadeObjectService.getRepresentationsByURI(uri);
                    answer.setLinks(photos);
                    break;
                case "unknown":
                    answer.setContent("机器人有点笨，您可以换一种问法!谢谢您理解!");
                    break;
            }
        }else {
            // 需要计算一下和模板问题的相似度 让用户选择一下
            String url = "http://202.120.40.107:8091/qSimilarity";
            JSONObject obj = new JSONObject();

            obj.put("question", question.getContent());
            String q_ls = sendPost(obj,url);
            JSONObject ret_obj = JSONObject.parseObject(q_ls);
            JSONArray ja = (JSONArray) ret_obj.get("msg");
            String ret_str = "您想问的问题是以下这些吗？";
            for(Object q : ja){
                String q_str = (String) q;
                ret_str += ("\n" + q_str);
            }
            ret_str += "\n以上都不是";

            answer.setContent(ret_str);
        }


        return answer;
    }
}
