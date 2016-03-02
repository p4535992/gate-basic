package com.github.p4535992.gatebasic.object;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapAnnotation {

    //private MapContent mapContent;
    private List<MapContent> listContents;
    private MultiValueMap<String, MapContent> mapAnnotations;
    private MultiValueMap<String, String> mapStringAnnotations;
    private String annotationName;
    private int index;

    public MapAnnotation(){
        this.index = 0;
        this.listContents = new ArrayList<>();
        this.annotationName = setUpDefaultName();
        this.mapAnnotations = new LinkedMultiValueMap<>();
        this.mapStringAnnotations = new LinkedMultiValueMap<>();
    }

    public MapAnnotation(MapContent mapContent){
        this.index = 0;
        this.listContents = Collections.singletonList(mapContent);
        this.annotationName = setUpDefaultName();
        this.mapAnnotations = setUpMapAnnotations();
        this.mapStringAnnotations = setUpMapStringAnnotations();
    }

    public MapAnnotation(List<MapContent> listContents){
        this.index = 0;
        this.listContents = listContents;
        this.annotationName = setUpDefaultName();
        this.mapAnnotations = setUpMapAnnotations();
        this.mapStringAnnotations = setUpMapStringAnnotations();
    }

    public MapAnnotation(String annotationName,MapContent mapContent){
        this.index = 0;
        this.listContents = Collections.singletonList(mapContent);
        this.annotationName = annotationName;
        this.mapAnnotations = setUpMapAnnotations();
        this.mapStringAnnotations = setUpMapStringAnnotations();
    }

    public MapAnnotation(String annotationName,List<MapContent> listContents){
        this.index = 0;
        this.listContents = listContents;
        this.annotationName = annotationName;
        this.mapAnnotations = setUpMapAnnotations();
        this.mapStringAnnotations = setUpMapStringAnnotations();
    }

    //GETTER AND SETTER

    public List<MapContent> getListContents() {
        return listContents;
    }

    public MultiValueMap<String, MapContent> getMapAnnotations() {
        return mapAnnotations;
    }

    public MultiValueMap<String, String> getMapStringAnnotations() {
        return mapStringAnnotations;
    }

    public Map<String,List<String>> getMap(){
        if(mapAnnotations== null)return new LinkedHashMap<>();
        Map<String, List<String>> result = new LinkedHashMap<>(mapAnnotations.size());
        for (Map.Entry<String, List<MapContent>> entry : mapAnnotations.entrySet()) {
            List<String> list = new ArrayList<>();
            for(MapContent mapContent : entry.getValue()) {
               list.add(mapContent.getContent());
            }
            result.put(entry.getKey(), list);
        }
        return result;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "Annotation#"+index;
    }

    private MultiValueMap<String,MapContent> setUpMapAnnotations(){
        MultiValueMap<String,MapContent> map = new LinkedMultiValueMap<>();
        for(MapContent mapContent : listContents){
            map.add(annotationName,mapContent);
        }
        return map;
    }

    private MultiValueMap<String,String> setUpMapStringAnnotations(){
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        for(MapContent mapContent : listContents){
            map.add(annotationName,mapContent.getContent());
        }
        return map;
    }

    public void add(MapContent mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        //re-set values
        this.mapAnnotations.add(setUpDefaultName(),mapContent);
        this.mapStringAnnotations.add(setUpDefaultName(),mapContent.getContent());
    }

    public void add(String annotationName,MapContent mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        //re-set values
        this.mapAnnotations.add(annotationName,mapContent);
        this.mapStringAnnotations.add(annotationName,mapContent.getContent());
    }

    public void add(MapAnnotation mapAnnotation){
        setIfNull();
        this.listContents.addAll( mapAnnotation.getListContents());
        addAll(mapAnnotation);
        addAllString(mapAnnotation);
    }

    private void addAll(MapAnnotation mapAnnotation){
        for(Map.Entry<String,List<MapContent>> entry: mapAnnotation.getMapAnnotations().entrySet()){
            for(MapContent mapContent: entry.getValue()) {
                this.mapAnnotations.add(entry.getKey(),mapContent);
            }
        }
    }

    private void addAllString(MapAnnotation mapAnnotation){
        for(Map.Entry<String,List<String>> entry: mapAnnotation.getMap().entrySet()){
            for(String entry2 : entry.getValue()){
                this.mapStringAnnotations.add(entry.getKey(),entry2);
            }
        }
    }

    public void put(MapContent mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        //re-set values
        this.mapAnnotations.put(setUpDefaultName(),Collections.singletonList(mapContent));
        this.mapStringAnnotations.put(setUpDefaultName(),Collections.singletonList(mapContent.getContent()));
    }

    public void put(String annotationName,MapContent mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        //re-set values
        this.mapAnnotations.put(annotationName,Collections.singletonList(mapContent));
        this.mapStringAnnotations.put(annotationName,Collections.singletonList(mapContent.getContent()));
    }

    public MultiValueMap<String, String> filterByString(MultiValueMap<String, String> map,Pattern pattern){
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys, Collections.reverseOrder());
        for(Map.Entry<String,List<String>> entry : map.entrySet()){
            for(String value : entry.getValue()) {
                if (pattern.matcher(value).matches() || pattern.matcher(value).find()) {
                    result.add(entry.getKey(),value);
                }
            }

        }
        return result;
    }

    public MultiValueMap<String,MapContent> filter(MultiValueMap<String, MapContent> map,Pattern pattern) {
        MultiValueMap<String, MapContent> result = new LinkedMultiValueMap<>();
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys, Collections.reverseOrder());
        for(Map.Entry<String,List<MapContent>> entry : map.entrySet()){
            for(MapContent value : entry.getValue()) {
                if (pattern.matcher(value.getContent()).matches() || pattern.matcher(value.getContent()).find()) {
                    result.add(entry.getKey(),value);
                }
            }
        }
        return result;
    }

    private void setIfNull(){
        if(listContents == null || listContents.isEmpty()){
            listContents = new ArrayList<>();
        }
        if(mapAnnotations == null || mapAnnotations.isEmpty()){
            mapAnnotations = new LinkedMultiValueMap<>();
        }
        if(mapStringAnnotations == null || mapStringAnnotations.isEmpty()){
            mapStringAnnotations = new LinkedMultiValueMap<>();
        }
    }

    public void clear(){
        listContents.clear();
        mapAnnotations.clear();
        mapStringAnnotations.clear();
    }

    public int size(){
        return mapAnnotations.size();
    }

    public boolean isEmpty(){
        return mapAnnotations.isEmpty();
        //return values().size();
    }

    public List<MapContent> values(){
        Collection<List<MapContent>> coll = mapAnnotations.values();
        List<MapContent> list = new ArrayList<>();
        for(List<MapContent> annCon : coll){
            list.addAll(annCon);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapContent>>> entrySet(){
        return mapAnnotations.entrySet();
    }


    public List<MapContent> get(String annotationName){
        for(Map.Entry<String,List<MapContent>> entry: mapAnnotations.entrySet()){
            if(entry.getKey().toLowerCase().contains(annotationName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<MapContent> get(Integer indexAnnotation){
        return new ArrayList<>(mapAnnotations.values()).get(indexAnnotation);
    }

    //SUPPORT METHOD

    public List<String> getString(String annotationName){
        List<MapContent> list = get(annotationName);
        List<String> list2 = new ArrayList<>();
        for(MapContent mapContent: list){
            list2.add(mapContent.getContent());
        }
        return list2;
    }

    public List<String> getString(Integer indexAnnotation){
        List<MapContent> list = get(indexAnnotation);
        List<String> list2 = new ArrayList<>();
        for(MapContent mapContent: list){
            list2.add(mapContent.getContent());
        }
        return list2;
    }

    public MapContent find(String nameAnnotation,Integer indexAnnotation){
        MapContent theMapContent = null;
        int i = 0;
        for(MapContent content : get(nameAnnotation)){
            if(i == indexAnnotation){
                if (content == null || content.isEmpty()) {
                    theMapContent = new MapContent("");
                    break;
                }else{
                    theMapContent = content;
                    break;
                }
            }
            i++;
        }
        return theMapContent;
    }

    public MapContent find(Integer indexAnnotation,Integer indexContent){
        MapContent theMapContent = null;
        int i = 0;
        for(MapContent content : get(indexAnnotation)){
            if(i == indexContent){
                if (content == null || content.isEmpty()) {
                    theMapContent = new MapContent("");
                    break;
                }else{
                    theMapContent = content;
                    break;
                }
            }
            i++;
        }
        return theMapContent;
    }

    public MapContent find(String nameAnnotation){
        MapContent theMapContent = null;
        for(Map.Entry<String,List<MapContent>> mapAnn : mapAnnotations.entrySet()){
            for(MapContent content: mapAnn.getValue()) {
                if (content != null && !content.isEmpty()) {
                    theMapContent = content;
                    break;
                }
            }
            if (theMapContent != null)break;
        }
        return theMapContent;
    }

    public String getName(Integer indexAnnotation) {
        int i = 0;
        for (Map.Entry<String, List<MapContent>> entry : mapAnnotations.entrySet()) {
            if (i == indexAnnotation) {
                return entry.getKey();
            }
            i++;
        }
        return null;
    }

    public String getName(String nameAnnotation) {
        for (Map.Entry<String, List<MapContent>> entry : mapAnnotations.entrySet()) {
            if (Objects.equals(entry.getKey(), nameAnnotation)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getName() {
        return annotationName;
    }

    @Override
    public String toString() {
        return "MapAnnotation{" +
                "mapAnnotations=" + mapAnnotations +
                '}';
    }
}
