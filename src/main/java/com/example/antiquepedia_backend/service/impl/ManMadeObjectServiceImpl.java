package com.example.antiquepedia_backend.service.impl;

import com.example.antiquepedia_backend.Entity.ManMadeObject;
import com.example.antiquepedia_backend.Entity.Place;
import com.example.antiquepedia_backend.component.TDBClient;
import com.example.antiquepedia_backend.service.ManMadeObjectService;
import com.example.antiquepedia_backend.utils.SemanticSimilarityBuiltin;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.reasoner.rulesys.Builtin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManMadeObjectServiceImpl implements ManMadeObjectService {

    @Autowired
    private TDBClient tdbClient;

    @Override
    public Place getPlaceByURI(String URI) {
        Place place = new Place();
        String placeURI = "";
        String variableSearch = "p_uri";
        ResultSet rsPlaceURI = tdbClient.queryPlaceURIBySubURI(URI, variableSearch);
        while (rsPlaceURI.hasNext()){
            QuerySolution solution = rsPlaceURI.nextSolution();
            RDFNode placeNode = solution.get(variableSearch);
            placeURI = placeNode.toString();
        }
        tdbClient.close();

        place.setUri(placeURI);

        // 获取 label （第一个不为空的）
        List<String> labels = getLabelsByURI(placeURI);
        for(String l : labels){
            if(l.length()!=0){
                place.setLabel(l);
                break;
            }
        }

        // 获取 fallWithin
        String fallWithin = getFallWithinByURI(placeURI);
        place.setFallWithin(fallWithin);
        // 获取 longitude
        String longitude = getLongitudeByURI(URI);
        place.setLongitude(longitude);
        // 获取 latitude
        String latitude = getLatitudeByURI(URI);
        place.setLatitude(latitude);

        return place;
    }

    @Override
    public String getFallWithinByURI(String URI) {
        String fallWithin = "";
        String variableSearch = "fall_uri";
        ResultSet rsFallWithin = tdbClient.queryFallWithinBySubURI(URI, variableSearch);
        while (rsFallWithin.hasNext()){
            QuerySolution solution = rsFallWithin.nextSolution();
            RDFNode fallWithinNode = solution.get(variableSearch);
            fallWithin = fallWithinNode.toString();
        }

        tdbClient.close();

        return fallWithin;
    }

    @Override
    public String getLongitudeByURI(String URI) {
        String longitude = "";
        String variableSearch = "l_uri";
        ResultSet rsLongitude = tdbClient.queryLongitudeBySubURI(URI, variableSearch);
        while (rsLongitude.hasNext()){
            QuerySolution solution = rsLongitude.nextSolution();
            RDFNode longitudeNode = solution.get(variableSearch);
            longitude = longitudeNode.toString();
        }

        tdbClient.close();

        return longitude;
    }

    @Override
    public String getLatitudeByURI(String URI) {
        String latitude = "";
        String variableSearch = "l_uri";
        ResultSet rslatitude = tdbClient.queryLatitudeBySubURI(URI, variableSearch);
        while (rslatitude.hasNext()){
            QuerySolution solution = rslatitude.nextSolution();
            RDFNode latitudeNode = solution.get(variableSearch);
            latitude = latitudeNode.toString();
        }

        tdbClient.close();

        return latitude;
    }

    @Override
    public String getTotalCount() {
        String total = "";
        ResultSet rs = tdbClient.queryManMadeObjectCount();
        while (rs.hasNext()){
            QuerySolution solution = rs.nextSolution();
            RDFNode totalNode = solution.get("total");
            total = totalNode.toString();
        }

        tdbClient.close();
        return total;
    }

    @Override
    public List<String> recommendByReference(String URI) {
        Builtin builtin = null;

        Node targetSub = NodeFactory.createURI(URI);
        String propURI = "http://www.test.com/ws/#sameReference";
        Node targetProp = NodeFactory.createURI(propURI);
        Node targetObj = null;
        String rules = "[ruleHoldShare: (?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s <http://purl.org/NET/crm-owl#P67i_is_referred_to_by> ?d) " +
                "(?s2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s2 <http://purl.org/NET/crm-owl#P67i_is_referred_to_by> ?d) -> (?s <" + propURI + "> ?s2)] \n"
                + "-> tableAll().";

        List<String> ret = tdbClient.infer(rules, builtin, targetSub, targetProp, targetObj);

        tdbClient.close();

        return ret;
    }

    @Override
    public List<String> recommendByShowFeature(String URI) {
        Builtin builtin = null;

        Node targetSub = NodeFactory.createURI(URI);
        String propURI = "http://www.test.com/ws/#shareShowFeature";
        Node targetProp = NodeFactory.createURI(propURI);
        Node targetObj = null;

        String rules = "[ruleHoldShare1: (?s_p <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s_1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) "+
                "(?s_p <http://purl.org/NET/crm-owl#P130_shows_features_of> ?s_1) -> (?s_p <http://www.test.com/ws/#relation> ?s_1) (?s_1 <http://www.test.com/ws/#relation> ?s_p)] \n" +
                "[ruleHoldShare2: (?s_p <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s_1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) "+
                "(?s_p <http://purl.org/NET/crm-owl#P130_shows_features_of> ?s_1) "+
                "(?s_2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s_p <http://purl.org/NET/crm-owl#P130_shows_features_of> ?s_2) -> (?s_1 <" + propURI + "> ?s_2)] \n"
                + "-> tableAll().";

        List<String> ret = tdbClient.infer(rules, builtin, targetSub, targetProp, targetObj);

        tdbClient.close();
        return ret;
    }

    @Override
    public List<String> recommendByLocation(String URI) {
        Builtin builtin = null;

        Node targetSub = NodeFactory.createURI(URI);
        String propURI = "http://www.test.com/ws/#sameLocation";
        Node targetProp = NodeFactory.createURI(propURI);
        Node targetObj = null;

        String rules = "[ruleHoldShare: (?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s http://purl.org/NET/crm-owl#P53_has_former_or_current_location ?d) " +
                "(?s2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s2 <http://purl.org/NET/crm-owl#P53_has_former_or_current_location> ?d) -> (?s <" + propURI + "> ?s2)] \n"
                + "-> tableAll().";

        List<String> ret = tdbClient.infer(rules, builtin, targetSub, targetProp, targetObj);
        tdbClient.close();
        return ret;
    }

    @Override
    public List<String> recommendByLabelSimilarity(String URI) {
        Builtin builtin = new SemanticSimilarityBuiltin();

        Node targetSub = NodeFactory.createURI(URI);
        String propURI = "http://www.test.com/ws/#similarLabel";
        Node targetProp = NodeFactory.createURI(propURI);
        Node targetObj = null;

        String rules = "[ruleHoldShare: (?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s <http://www.w3.org/2000/01/rdf-schema#label> ?l) " +
                "(?s2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object>) " +
                "(?s2 <http://www.w3.org/2000/01/rdf-schema#label> ?l2) notEqual(?s,?s2) semsim(?l,?l2,0.8)-> (?s <" + propURI + "> ?s2)] \n"
                + "-> tableAll().";

        List<String> ret = tdbClient.infer(rules, builtin, targetSub, targetProp, targetObj);
        tdbClient.close();
        return ret;
    }

    @Override
    public String addManMadeObject(ManMadeObject manMadeObject) {
        String retStr = "Success";
        try{
            tdbClient.addManMadeObject(manMadeObject);
        }catch (Exception e){
            retStr = e.toString();
        }
        tdbClient.close();
        return retStr;
    }

    @Override
    public String deleteManMadeObject(String objectURI) {
        String retStr = "Success";
        try{
            tdbClient.deleteManMadeObject(objectURI);
        }catch (Exception e){
            retStr = e.toString();
        }

        tdbClient.close();

        return retStr;
    }

    @Override
    public List<ManMadeObject> getManMadeObjectsByRefer(String refer) {
        List<ManMadeObject> ret_objects = new ArrayList<ManMadeObject>();
        String searchV = "obj";
        ResultSet rs = tdbClient.queryObjectURIByRefer(refer, searchV);
        List<String> uris = new ArrayList<String>();
        while(rs.hasNext()){
            QuerySolution solution = rs.nextSolution();
            RDFNode object_uri = solution.get(searchV);
            uris.add(object_uri.toString());
        }
        tdbClient.close();

        for(String uri : uris){
            ret_objects.add(getManMadeObjectByURI(uri));
        }

        return ret_objects;
    }

    @Override
    public ManMadeObject getManMadeObjectByURI(String URI) {
        ManMadeObject manMadeObject = new ManMadeObject();
        manMadeObject.setURI(URI);
        // 根据URI 获取所有Label
        List<String> labels = getLabelsByURI(URI);
        manMadeObject.setLabels(labels);
        // 根据URI 获取所有Representations
        List<String> representations = getRepresentationsByURI(URI);
        manMadeObject.setRepresentations(representations);
        // 根据URI 获取所有refers
        List<String> refers = getRefersByURI(URI);
        manMadeObject.setRefers(refers);

        // 获取 period_begin
        String periodBegin = getPeriodBeginByURI(URI);
        manMadeObject.setPeriod_begin(periodBegin);

        // 获取 period_end
        String periodEnd = getPeriodEndByURI(URI);
        manMadeObject.setPeriod_end(periodEnd);

        // 获取 材料
        List<String> materials = getMaterialsByURI(URI);
        manMadeObject.setMaterials(materials);

        // 获取 has_type
        List<String> hasTypes = getHasTypeByURI(URI);
        manMadeObject.setHas_type(hasTypes);

        // 获取 place
        Place place = getPlaceByURI(URI);
        manMadeObject.setPlace(place);

        return manMadeObject;
    }

    @Override
    public ManMadeObject getManMadeObjectByLabel(String label) {
        ManMadeObject manMadeObject = new ManMadeObject();
        // 查URI 唯一
        String objectURI = "";
        String variableSearch = "s_uri";
        ResultSet rsObjectURI = tdbClient.queryURIByLabel(label, variableSearch);
        while(rsObjectURI.hasNext()){
            QuerySolution solution = rsObjectURI.nextSolution();
            RDFNode objectNode = solution.get(variableSearch);
            objectURI = objectNode.toString();
        }
        tdbClient.close();
        if(objectURI.length() != 0){
            // 说明存在
            manMadeObject.setURI(objectURI);
            // 根据URI 获取所有Label
            List<String> labels = getLabelsByURI(objectURI);
            manMadeObject.setLabels(labels);
            // 根据URI 获取所有Representations
            List<String> representations = getRepresentationsByURI(objectURI);
            manMadeObject.setRepresentations(representations);
            // 根据URI 获取所有refers
            List<String> refers = getRefersByURI(objectURI);
            manMadeObject.setRefers(refers);

            // 获取 period_begin
            String periodBegin = getPeriodBeginByURI(objectURI);
            manMadeObject.setPeriod_begin(periodBegin);

            // 获取 period_end
            String periodEnd = getPeriodEndByURI(objectURI);
            manMadeObject.setPeriod_end(periodEnd);

            // 获取 材料
            List<String> materials = getMaterialsByURI(objectURI);
            manMadeObject.setMaterials(materials);

            // 获取 has_type
            List<String> hasTypes = getHasTypeByURI(objectURI);
            manMadeObject.setHas_type(hasTypes);

            // 获取 place
            Place place = getPlaceByURI(objectURI);
            manMadeObject.setPlace(place);

        }

        return manMadeObject;
    }


    @Override
    public List<String> getLabelsByURI(String URI) {
        // 根据URI 获取所有Label
        List<String> labels = new ArrayList<String>();
        String variableSearch = "o_label";
        ResultSet rsLabel = tdbClient.queryLabelByURI(URI, variableSearch);
        while(rsLabel.hasNext()){
            QuerySolution solution = rsLabel.nextSolution();
            RDFNode labelNode = solution.get(variableSearch);
            labels.add(labelNode.toString());
        }
        tdbClient.close();

        return labels;
    }

    @Override
    public String getPeriodBeginByURI(String URI) {
        String periodBegin = "";
        String variableSearch = "o_periodBegin";
        ResultSet rsPeriodBegin = tdbClient.queryPeriodBeginBySubURI(URI, variableSearch);
        while (rsPeriodBegin.hasNext()){
            QuerySolution solution = rsPeriodBegin.nextSolution();
            RDFNode periodBeginNode = solution.get(variableSearch);
            periodBegin = periodBeginNode.toString();
            System.out.println("Begin: " + periodBegin);
        }

        tdbClient.close();

        return periodBegin;
    }

    @Override
    public List<String> getRefersByURI(String URI) {
        List<String> referLabels = new ArrayList<String>();
        // 先获取所有 refer 的 URI
        String variableSearch = "o_refer";
        ResultSet rsReferURI = tdbClient.queryReferToURIBySubURI(URI, variableSearch);
        while(rsReferURI.hasNext()){
            QuerySolution solution = rsReferURI.nextSolution();
            RDFNode refNode = solution.get(variableSearch);
            referLabels.add(refNode.toString());
        }
        tdbClient.close();

        return referLabels;
    }

    // 需要测一测
    @Override
    public List<String> getMaterialsByURI(String URI) {
        List<String> materialLabels = new ArrayList<String>();
        String variableSearch = "o_material";
        ResultSet rsMaterialURI = tdbClient.queryMaterialBySubURI(URI, variableSearch);
        while(rsMaterialURI.hasNext()){
            QuerySolution solution = rsMaterialURI.nextSolution();
            RDFNode materialNode = solution.get(variableSearch);
            materialLabels.add(materialNode.toString());
        }
        tdbClient.close();

        return materialLabels;

    }

    @Override
    public List<String> getHasTypeByURI(String URI) {
        List<String> hasTypeLabels = new ArrayList<String>();
        String variableSearch = "o_hasType";
        ResultSet rshasTypeURI = tdbClient.queryHasTypeBySubURI(URI, variableSearch);
        while(rshasTypeURI.hasNext()){
            QuerySolution solution = rshasTypeURI.nextSolution();
            RDFNode hasTypeNode = solution.get(variableSearch);
            hasTypeLabels.add(hasTypeNode.toString());
        }
        tdbClient.close();
        return hasTypeLabels;
    }



    @Override
    public String getPeriodEndByURI(String URI) {
        String periodEnd = "";
        String variableSearch = "o_periodEnd";
        ResultSet rsperiodEnd = tdbClient.queryPeriodEndBySubURI(URI, variableSearch);
        while (rsperiodEnd.hasNext()){
            QuerySolution solution = rsperiodEnd.nextSolution();
            RDFNode periodEndNode = solution.get(variableSearch);
            periodEnd = periodEndNode.toString();
            System.out.println("Begin: " + periodEnd);
        }

        tdbClient.close();

        return periodEnd;
    }

    @Override
    public List<String> getRepresentationsByURI(String URI) {
        List<String> representations = new ArrayList<String>();
        String variableSearch = "o_representation";
        ResultSet rsRepresentation = tdbClient.queryRepresentationByURI(URI, variableSearch);
        while (rsRepresentation.hasNext()){
            QuerySolution solution = rsRepresentation.nextSolution();
            RDFNode repNode = solution.get(variableSearch);
            representations.add(repNode.toString());
        }

        tdbClient.close();
        return representations;
    }

    @Override
    public List<ManMadeObject> getManObjectLikeLabel(String label) {
        List<ManMadeObject> manMadeObjects = new ArrayList<ManMadeObject>();

        List<String> objectURIs = new ArrayList<String>();
        String variableSearch = "s_uri";
        ResultSet rsObjectURI = tdbClient.queryURILikeLabel(label, variableSearch);
        while(rsObjectURI.hasNext()){
            QuerySolution solution = rsObjectURI.nextSolution();
            RDFNode objectNode = solution.get(variableSearch);
            objectURIs.add(objectNode.toString());
        }
        tdbClient.close();
        for(String objectURI : objectURIs){
            ManMadeObject manMadeObject = new ManMadeObject();
            // 说明存在
            manMadeObject.setURI(objectURI);
            // 根据URI 获取所有Label
            List<String> labels = getLabelsByURI(objectURI);
            manMadeObject.setLabels(labels);
            // 根据URI 获取所有Representations
            List<String> representations = getRepresentationsByURI(objectURI);
            manMadeObject.setRepresentations(representations);
            // 根据URI 获取所有refers
            List<String> refers = getRefersByURI(objectURI);
            manMadeObject.setRefers(refers);

            // 获取 period_begin
            String periodBegin = getPeriodBeginByURI(objectURI);
            manMadeObject.setPeriod_begin(periodBegin);

            // 获取 period_end
            String periodEnd = getPeriodEndByURI(objectURI);
            manMadeObject.setPeriod_end(periodEnd);

            // 获取材料
            List<String> materials = getMaterialsByURI(objectURI);
            manMadeObject.setMaterials(materials);

            // 获取has_type
            List<String> hasTypes = getHasTypeByURI(objectURI);
            manMadeObject.setHas_type(hasTypes);

            // 获取 place
            Place place = getPlaceByURI(objectURI);
            manMadeObject.setPlace(place);

            manMadeObjects.add(manMadeObject);
        }

        return manMadeObjects;
    }
}