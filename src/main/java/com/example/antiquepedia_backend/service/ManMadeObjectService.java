package com.example.antiquepedia_backend.service;

import com.example.antiquepedia_backend.Entity.ManMadeObject;
import com.example.antiquepedia_backend.Entity.Place;

import java.util.List;

public interface ManMadeObjectService {

    public List<ManMadeObject> getManMadeObjectsByRefer(String refer);

    public ManMadeObject getManMadeObjectByURI(String URI);

    public ManMadeObject getManMadeObjectByLabel(String label);

    public List<String> getLabelsByURI(String URI);

    public List<String> getRepresentationsByURI(String URI);

    public List<String> getRefersByURI(String URI);

    public List<ManMadeObject> getManObjectLikeLabel(String label);

    public List<String> getMaterialsByURI(String URI);

    public List<String> getHasTypeByURI(String URI);

    public String getPeriodBeginByURI(String URI);

    public String getPeriodEndByURI(String URI);

    public Place getPlaceByURI(String URI);

    public String getFallWithinByURI(String URI);

    public String getLongitudeByURI(String URI);

    public String getLatitudeByURI(String URI);

    public String getTotalCount();

    // 推荐先默认都返回 URI列表 看前端需要啥 再给啥吧
    // 根据Reference推荐
    public List<String> recommendByReference(String URI);
    // 根据ShowFeature推荐
    public List<String> recommendByShowFeature(String URI);
    // 根据地区推荐
    public List<String> recommendByLocation(String URI);
    // 根据Label相似度推荐
    public List<String> recommendByLabelSimilarity(String URI);

    public String addManMadeObject(ManMadeObject manMadeObject);

    public String deleteManMadeObject(String objectURI);
}
