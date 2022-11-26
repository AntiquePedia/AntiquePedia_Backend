package com.example.antiquepedia_backend.component;

import lombok.Data;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb2.TDB2Factory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TDBClient {
    @Value("${tdb.directory}")
    private String datasetName;

    @Value("${tdb.model}")
    private  String modelName;

    private ThreadLocal<QueryExecution> executionThreadLocal = new ThreadLocal<>();

    private ThreadLocal<Dataset> datasetThreadLocal = new ThreadLocal<>();

    public ResultSet queryURIByLabel(String label, String serachV) {
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{?%s <http://www.w3.org/2000/01/rdf-schema#label> \"%s\"}", serachV, serachV, label);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryLabelByURI(String URI, String serachV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://www.w3.org/2000/01/rdf-schema#label> ?%s}", serachV, URI, serachV);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryRepresentationByURI(String URI, String serachV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P138i_has_representation> ?%s}", serachV, URI, serachV);
        return this.query(modelName, queryStr);
    }

    public ResultSet queryReferToURIBySubURI(String subURI, String serachV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P67i_is_referred_to_by> ?%s}", serachV, subURI, serachV);
        return this.query(modelName,queryStr);
    }

    public ResultSet queryPlaceURIBySubURI(String subURI, String serachV){
        String queryStr = String.format("SELECT DISTINCT ?%s WHERE{<%s> <http://purl.org/NET/crm-owl#P53_has_former_or_current_location> ?%s}", serachV, subURI, serachV);
        return this.query(modelName,queryStr);
    }


    private ResultSet query(String modelName, String queryStr) {
        Dataset dataset = TDB2Factory.connectDataset(this.datasetName);
        this.datasetThreadLocal.set(dataset);
        dataset.begin(ReadWrite.READ);
        Model model = dataset.getNamedModel(modelName);
        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        this.executionThreadLocal.set(qexec);
        return qexec.execSelect();
    }

    public void close() {
        this.executionThreadLocal.get().close();
        this.datasetThreadLocal.get().end();
        this.getDatasetThreadLocal().get().close();
    }
}
