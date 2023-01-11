package com.example.antiquepedia_backend.component;

import com.example.antiquepedia_backend.Entity.ManMadeObject;
import com.example.antiquepedia_backend.Entity.Place;
import lombok.Data;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.reasoner.rulesys.*;
import org.apache.jena.tdb2.TDB2Factory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class TDBClient {
    @Value("${tdb.directory}")
    private String datasetName;

    @Value("${tdb.model}")
    private  String modelName;

    private ThreadLocal<QueryExecution> executionThreadLocal = new ThreadLocal<>();

    private ThreadLocal<Dataset> datasetThreadLocal = new ThreadLocal<>();

    // 根据label模糊查询URI
    public ResultSet queryURILikeLabel(String label, String searchV) {
        String quertStr = String.format("SELECT DISTINCT ?%s WHERE{?%s <http://www.w3.org/2000/01/rdf-schema#label> ?l . FILTER REGEX(?l, \"%s\")}", searchV, searchV, label);
        return this.query(modelName, quertStr);
    }

    public ResultSet queryURIByLabel(String label, String searchV) {
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{?%s <http://www.w3.org/2000/01/rdf-schema#label> \"%s\"}", searchV, searchV, label);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryLabelByURI(String URI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", searchV, URI, searchV);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryRepresentationByURI(String URI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P138i_has_representation> ?%s}", searchV, URI, searchV);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryReferToURIBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P67i_is_referred_to_by> ?ref ." +
                "?ref <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", searchV, subURI, searchV);
        return this.query(modelName,queryStr);
    }

    // 获取组成材料的URI
    public ResultSet queryMaterialBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P45_consists_of> ?material ." +
                "?material <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", searchV, subURI, searchV);
        return this.query(modelName, queryStr);
    }

    // 获取 has_type URI
    public ResultSet queryHasTypeBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P2_has_type> ?type ." +
                "?type <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    public ResultSet queryPeriodBeginBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P108i_was_produced_by> ?production ." +
                "?production <http://purl.org/NET/crm-owl#P4_has_time-span> ?period ." +
                "?period <http://purl.org/NET/crm-owl#P82_at_some_time_within> ?time ." +
                "?time <http://purl.org/NET/Claros/vocab#period_begin> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    public ResultSet queryPeriodEndBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P108i_was_produced_by> ?production ." +
                "?production <http://purl.org/NET/crm-owl#P4_has_time-span> ?period ." +
                "?period <http://purl.org/NET/crm-owl#P82_at_some_time_within> ?time ." +
                "?time <http://purl.org/NET/Claros/vocab#period_end> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    public ResultSet queryPlaceURIBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P53_has_former_or_current_location> ?%s}", searchV, subURI, searchV);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryFallWithinBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P89_falls_within> ?falls ." +
                "?falls <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    // 注意 传进来的 uri 是 manMadeObject 的 URI
    public ResultSet queryLongitudeBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/Claros/vocab#coordinates-current> ?coordinate ." +
                "?coordinate <http://purl.org/NET/Claros/vocab#has_geoObject> ?geo ." +
                "?geo <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    public ResultSet queryLatitudeBySubURI(String subURI, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/Claros/vocab#coordinates-current> ?coordinate ." +
                "?coordinate <http://purl.org/NET/Claros/vocab#has_geoObject> ?geo ." +
                "?geo <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?%s}", searchV, subURI, searchV);

        return this.query(modelName, queryStr);
    }

    public ResultSet queryObjectURIByRefer(String refer, String searchV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{ ?%s <http://purl.org/NET/crm-owl#P67i_is_referred_to_by> ?ref ." +
                "?ref <http://www.w3.org/2000/01/rdf-schema#label> \"%s\" }", searchV, searchV, refer);

        return this.query(modelName, queryStr);
    }

    // 尝试统计下 manMadeObject 数量
    public ResultSet queryManMadeObjectCount(){
        String queryStr = "SELECT ( count ( ?s ) AS ?total ) where { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/NET/crm-owl#E22_Man-Made_Object> }";

        return this.query(modelName, queryStr);
    }


    private ResultSet query(String modelName, String queryStr) {
        Dataset dataset = TDB2Factory.connectDataset(this.datasetName);
        this.datasetThreadLocal.set(dataset);
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getNamedModel(modelName);
//        Model model = dataset.getDefaultModel();
        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        this.executionThreadLocal.set(qexec);
        return qexec.execSelect();
    }


    /**
     *
     * @param rules 自定义推理规则
     * @param builtin 自定义筛选函数 （也是用于规则推理）
     * @param targetSub
     * @param targetProp
     * @param targetObj
     *
     * @return 推理后 符合 （targetSub, targetProp, targetObj） 条件 的 所有 答案（就是三元组中的主语或宾语 目前只是这样考虑的）
     */
    public List<String> infer(String rules, Builtin builtin, Node targetSub, Node targetProp, Node targetObj){
        List<String> answers = new ArrayList<String>();

        // 注册 builtin
        if(builtin != null){
            BuiltinRegistry.theRegistry.register(builtin);
        }
        // 开启读事务
        Dataset dataset = TDB2Factory.connectDataset(datasetName);
        this.datasetThreadLocal.set(dataset);
        dataset.begin(ReadWrite.READ);
        // 获取对应模型
        Model model = dataset.getNamedModel("testCulture");
        // 根据规则 生成 推理机
        GenericRuleReasoner reasoner = (GenericRuleReasoner) GenericRuleReasonerFactory.theInstance().create(null);
        reasoner.setRules(Rule.parseRules(rules));
        reasoner.setMode(GenericRuleReasoner.HYBRID);
        // 绑定模型与推理机
        InfGraph infgraph = reasoner.bind(model.getGraph());
        infgraph.setDerivationLogging(true);
        // 根据条件获取所有符合条件的三元组
        Iterator<Triple> tripleIterator = infgraph.find(targetSub, targetProp , targetObj);
        // 遍历所有三元组 获取结果集
        while (tripleIterator.hasNext()){
            Triple triple = tripleIterator.next();
            if(targetSub != null){
                // 答案是宾语
                answers.add(triple.getObject().toString());
            }else{
                answers.add(triple.getSubject().toString());
            }
        }

        return answers;
    }

    // 添加文物实体
    public void addManMadeObject(ManMadeObject manMadeObject){
        List<String> labels = manMadeObject.getLabels();
        // URI 自己生成吧
        String URI = "http://sam/manMadeObject/" + labels.get(0);
        List<String> representations = manMadeObject.getRepresentations();
        List<String> refers = manMadeObject.getRefers();
        List<String> materials = manMadeObject.getMaterials();
        String period_begin = manMadeObject.getPeriod_begin();
        String period_end = manMadeObject.getPeriod_end();
        List<String> has_type = manMadeObject.getHas_type();
        Place place = manMadeObject.getPlace();

        String place_label = place.getLabel();
        String fallWith = place.getFallWithin();
        String longitude = place.getLongitude();
        String latitude = place.getLatitude();

        Dataset dataset = TDB2Factory.connectDataset(datasetName);
        this.datasetThreadLocal.set(dataset);
        dataset.begin(ReadWrite.WRITE);

        Model model = dataset.getNamedModel("testCulture");

        Resource newManMadeObject = model.createResource(URI);
        Property type_prop = model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource temp_manMadeObject = model.getResource("http://purl.org/NET/crm-owl#E22_Man-Made_Object");
        newManMadeObject.addProperty(type_prop, temp_manMadeObject);

        // 加 Label
        Property label_prop = model.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
        for(String label : labels){
            newManMadeObject.addProperty(label_prop, label);
        }
        // 加入representations
        Property img_prop = model.getProperty("http://purl.org/NET/crm-owl#P138i_has_representation");
        for(String representation : representations){
            Resource temp_img = model.createResource(representation);
            temp_img.addProperty(type_prop, "http://sam/type#image");
            newManMadeObject.addProperty(img_prop,temp_img);
        }

        // 加入 refers
        Property ref_prop = model.getProperty("http://purl.org/NET/crm-owl#P67i_is_referred_to_by");
        for(String refer : refers){
            Resource temp_refer = model.createResource();
            temp_refer.addProperty(label_prop, refer);
            newManMadeObject.addProperty(ref_prop, temp_refer);
        }

        // 加入 materials
        Property material_prop = model.getProperty("http://purl.org/NET/crm-owl#P45_consists_of");
        for(String material : materials){
            Resource temp_material = model.createResource();
            temp_material.addProperty(label_prop, material);
            newManMadeObject.addProperty(material_prop, temp_material);
        }

        // 加 begin end
        Property t1 = model.getProperty("http://purl.org/NET/crm-owl#P108i_was_produced_by");
        Property t2 = model.getProperty("http://purl.org/NET/crm-owl#P4_has_time-span");
        Property t3 = model.getProperty("http://purl.org/NET/crm-owl#P82_at_some_time_within");
        Property t4 = model.getProperty("http://purl.org/NET/Claros/vocab#period_begin");
        Property t5 = model.getProperty("http://purl.org/NET/Claros/vocab#period_end");
        newManMadeObject.addProperty(t1,model.createResource().
                addProperty(t2,model.createResource().
                        addProperty(t3,model.createResource().
                                addProperty(t4,period_begin).addProperty(t5,period_end))));

        // 加 has_type
        Property has_type_prop = model.getProperty("http://purl.org/NET/crm-owl#P2_has_type");
        for(String hp : has_type){
            Resource temp_hp = model.createResource();
            temp_hp.addProperty(label_prop, hp);
            newManMadeObject.addProperty(has_type_prop, temp_hp);
        }

        // 加 Place
        Property p1 = model.getProperty("http://purl.org/NET/crm-owl#P53_has_former_or_current_location");
        Property p2 = model.getProperty("http://purl.org/NET/crm-owl#P89_falls_within");
        Property p3 = model.getProperty("http://purl.org/NET/Claros/vocab#coordinates-current");
        Property p4 = model.getProperty("http://purl.org/NET/Claros/vocab#has_geoObject");
        Property p5 = model.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
        Property p6 = model.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
        newManMadeObject.addProperty(p1, model.createResource("http://sam/place/" + place_label).addProperty(label_prop, place_label).addProperty(p2, model.createResource("http://sam/fallwith/" + fallWith).addProperty(label_prop, fallWith))).addProperty(p3,model.createResource().addProperty(p4,model.createResource().addProperty(p5, longitude).addProperty(p6, latitude)));

        dataset.commit();
    }

    // 获取一个文物的所有 Statements
    public void getAllStatements(List<Statement> ls, Set<String> memo, Resource sub){
        StmtIterator iter = sub.listProperties();
        while (iter.hasNext()){
            Statement st = iter.nextStatement();
            ls.add(st);
            RDFNode ob = st.getObject();
            if(ob.isResource()){
                Resource obResource = ob.asResource();
                if(!memo.contains(obResource.toString())){
                    memo.add(obResource.toString());
                    getAllStatements(ls, memo, obResource);
                }
            }
        }
    }

    public void deleteManMadeObject(String URI){
        Dataset dataset = TDB2Factory.connectDataset(datasetName);
        this.datasetThreadLocal.set(dataset);
        dataset.begin(ReadWrite.WRITE);
        Model model = dataset.getNamedModel("testCulture");
        List<Statement> deleteLS = new ArrayList<Statement>();
        Set<String> memo = new HashSet<String>();
        Resource sub = model.getResource(URI);

        getAllStatements(deleteLS, memo, sub);

        model.remove(deleteLS);

        dataset.commit();

    }

    public void close() {
        if (this.executionThreadLocal.get()!=null) {
            this.executionThreadLocal.get().close();
        }
        this.datasetThreadLocal.get().end();
        this.datasetThreadLocal.get().close();
    }
}
