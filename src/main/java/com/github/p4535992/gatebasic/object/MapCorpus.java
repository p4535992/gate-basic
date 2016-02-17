package com.github.p4535992.gatebasic.object;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapCorpus{

    private List<MapDocument> listDocument;
    private MultiValueMap<String,MapDocument> mapCorpus;
    private String docName;
    private int index;


    public MapCorpus(){
        this.index = 0;
        this.listDocument = new ArrayList<>();
        this.docName = setUpDefaultName();
        this.mapCorpus = new LinkedMultiValueMap<>();
        //this.mapStringDocs = new LinkedMultiValueMap<>();
    }

    public MapCorpus(MapDocument mapDocument){
        this.index = 0;
        this.listDocument = Collections.singletonList(mapDocument);
        this.docName = setUpDefaultName();
        this.mapCorpus = setUpMapCorpus();
        //this.mapStringDocs = setUpMapStringDocs();
    }

    public MapCorpus(List<MapDocument> listDocument){
        this.index = 0;
        this.listDocument = listDocument;
        this.docName = setUpDefaultName();
        this.mapCorpus = setUpMapCorpus();
        //this.mapStringDocs = setUpMapStringDocs();
    }

    public MapCorpus(String nameDoc, MapDocument mapDocument){
        this.index = 0;
        this.listDocument = Collections.singletonList(mapDocument);
        this.docName = nameDoc;
        this.mapCorpus = setUpMapCorpus();
        //this.mapCorpus = setUpMapStringDocs();
    }

    public MapCorpus(String nameDoc, List<MapDocument> listDocument){
        this.index = 0;
        this.listDocument = listDocument;
        this.docName = nameDoc;
        this.mapCorpus = setUpMapCorpus();
        //this.mapStringDocs = setUpMapStringDocs();
    }

    //Getter and setter

    public MapCorpus(MultiValueMap<String,MapDocument> mapCorpus){
        this.mapCorpus = mapCorpus;
    }

    public MultiValueMap<String,MapDocument> getMapCorpus() {
        return mapCorpus;
    }

    public void setMapCorpus(MultiValueMap<String,MapDocument> mapCorpus) {
        this.mapCorpus = mapCorpus;
    }

    public List<MapDocument> getListDocument() {
        return listDocument;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "Corpus#"+index;
    }

    private MultiValueMap<String,MapDocument> setUpMapCorpus(){
        MultiValueMap<String,MapDocument> map = new LinkedMultiValueMap<>();
        for(MapDocument mapDocument : listDocument){
            map.add(docName,mapDocument);
        }
        return map;
    }

   /* private MultiValueMap<String,Map<String,Map<String,List<String>>>> setUpMapStringDocs(){
        MultiValueMap<String,Map<String,Map<String,List<String>>>> map = new LinkedMultiValueMap<>();
        for(MapDocument mapDocument :  listDocument){
            map.add(docName,mapDocument.getMap());
        }
        return map;
    }*/

    public void add(MapDocument mapDocument){
        setIfNull();
        this.listDocument.add(mapDocument);
        //re-set values
        this.mapCorpus.add(setUpDefaultName(),mapDocument);
        //this.mapStringDocs.add(setUpDefaultName(),mapDocument.getMap());
    }

    public void add(String annotationSetName,MapDocument mapDocument){
        setIfNull();
        this.listDocument.add(mapDocument);
        //re-set values
        this.mapCorpus.add(annotationSetName,mapDocument);
        //this.mapStringDocs.add(annotationSetName,mapDocument.getMap());
    }

    public void add(MapCorpus mapCorpus){
        setIfNull();
        this.listDocument.addAll(mapCorpus.getListDocument());
        addAll(mapCorpus);
        //addAllString(mapDocument);
    }

    private void addAll(MapCorpus mapCorpus){
        for(Map.Entry<String,List<MapDocument>> entry: mapCorpus.getMapCorpus().entrySet()){
            for(MapDocument mapDocument: entry.getValue()) {
                this.mapCorpus.add(entry.getKey(),mapDocument);
            }
        }
    }

    /*private void addAllString(MapDocument mapDocument) {
        for (Map.Entry<String, Map<String, Map<String, List<String>>>> entry : mapDocument.getMap().entrySet()) {
            for (Map.Entry<String, Map<String, List<String>>> entry2 : entry.getValue().entrySet()) {
                this.mapStringDocs.add(entry.getKey(), entry.getValue());
            }
        }
    }*/

    public void put(MapDocument mapDocument){
        setIfNull();
        this.listDocument.add(mapDocument);
        //re-set values
        this.mapCorpus.put(setUpDefaultName(),Collections.singletonList(mapDocument));
        //this.mapStringDocs.put(setUpDefaultName(),Collections.singletonList(mapDocument.getMap()));
    }

    public void put(String annotationSetName,MapDocument mapDocument){
        setIfNull();
        this.listDocument.add(mapDocument);
        //re-set values
        this.mapCorpus.put(annotationSetName,Collections.singletonList(mapDocument));
        //this.mapStringDocs.put(annotationSetName,Collections.singletonList(mapDocument.getMap()));
    }

    private void setIfNull(){
        if(listDocument == null || listDocument.isEmpty()){
            listDocument = new ArrayList<>();
        }
        if(mapCorpus == null || mapCorpus.isEmpty()){
            mapCorpus = new LinkedMultiValueMap<>();
        }
       /* if(mapStringDocs == null || mapStringDocs.isEmpty()){
            mapStringDocs = new LinkedMultiValueMap<>();
        }*/
    }

    public void clear(){
        listDocument.clear();
        mapCorpus.clear();
        //mapStringDocs.clear();
    }

    public int size(){
        return mapCorpus.size();
        //return values().size();
    }

    public boolean isEmpty(){
        return mapCorpus.isEmpty();
        //return values().size();
    }

    public List<MapDocument> values(){
        Collection<List<MapDocument>> coll = mapCorpus.values();
        List<MapDocument> list = new ArrayList<>();
        for(List<MapDocument> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapDocument>>> entrySet(){
        return mapCorpus.entrySet();
    }

    public List<MapDocument> get(String documentName){
        for(Map.Entry<String,List<MapDocument>> entry: mapCorpus.entrySet()){
            if(entry.getKey().toLowerCase().contains(documentName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<MapDocument> get(Integer indexDocument){
        //List<List<MapDocument>> l = new ArrayList<>(mapCorpus.values());
        return new ArrayList<>(mapCorpus.values()).get(indexDocument);
    }

    public boolean hasValue(String key){
        List<MapDocument> value = mapCorpus.get(key);
        return value != null && !value.isEmpty();
    }

    public boolean hasKey(MapDocument value){
        for (Map.Entry<String,List<MapDocument>> entry : mapCorpus.entrySet()) {
            for(MapDocument annSet: entry.getValue()) {
                if (Objects.equals(value, annSet)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MapCorpus{" +
                "mapCorpus=" + mapCorpus +
                '}';
    }
}
