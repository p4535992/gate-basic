package com.github.p4535992.gatebasic.object;

import gate.Annotation;
import gate.FeatureMap;
import gate.Node;
import gate.annotation.AnnotationImpl;
import gate.annotation.NodeImpl;
import gate.event.AnnotationEvent;
import gate.event.AnnotationListener;
import gate.event.FeatureMapListener;
import gate.util.AbstractFeatureBearer;
import gate.util.FeatureBearer;
import gate.util.SimpleFeatureMapImpl;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
@SuppressWarnings("unused")
public class MapAnnotation extends AnnotationImpl implements Annotation{
    static final long serialVersionUID = -4448993256574857725L;
    private List<String> listContents;
    private MultiValueMap<String, String> mapAnnotations;
    private String annotationName;
    private int index;

   /* Integer id;
    String type;
    FeatureMap features;
    protected Node start;
    protected Node end;*/

    public MapAnnotation(){
        this(new ArrayList<String>(),new SimpleFeatureMapImpl());
    }

    public MapAnnotation(String content){
        this(Collections.singletonList(content),new SimpleFeatureMapImpl());
    }

    public MapAnnotation(List<String> listContents){
        this(listContents,new SimpleFeatureMapImpl());
    }

    //TODO to implement
    public MapAnnotation(List<String> listContents,FeatureMap featureMap){
        super(new AtomicInteger().getAndIncrement(),new NodeImpl(0),new NodeImpl(0),"",featureMap);
        //New variables
        this.index = 0;
        this.listContents = new ArrayList<>();
        for(String s :listContents){
            this.listContents.add(setContent(s));
        }
        this.annotationName = setUpDefaultName();
        this.mapAnnotations = setUpMapAnnotations();
    }

    //GETTER AND SETTER

    public List<String> getListContents() {
        return listContents;
    }

    public MultiValueMap<String, String> getMapAnnotations() {
        return mapAnnotations;
    }

    public Map<String,List<String>> getMap(){
        if(mapAnnotations== null)return new LinkedHashMap<>();
        Map<String, List<String>> result = new LinkedHashMap<>(mapAnnotations.size());
        for (Map.Entry<String, List<String>> entry : mapAnnotations.entrySet()) {
            List<String> list = new ArrayList<>();
            for(String mapContent : entry.getValue()) {
               list.add(mapContent);
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

    private MultiValueMap<String,String> setUpMapAnnotations(){
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        for(String mapContent : listContents){
            map.add(annotationName,mapContent);
        }
        return map;
    }

    public void add(String mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        this.mapAnnotations.add(setUpDefaultName(),mapContent);
    }

    public void add(String annotationName,String mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        this.mapAnnotations.add(annotationName,mapContent);
    }

    public void add(MapAnnotation mapAnnotation){
        setIfNull();
        this.listContents.addAll( mapAnnotation.getListContents());
        addAll(mapAnnotation);
    }

    private void addAll(MapAnnotation mapAnnotation){
        for(Map.Entry<String,List<String>> entry: mapAnnotation.getMap().entrySet()){
            for(String entry2 : entry.getValue()){
                this.mapAnnotations.add(entry.getKey(),entry2);
            }
        }
    }

    public void put(String mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        this.mapAnnotations.put(setUpDefaultName(),Collections.singletonList(mapContent));
    }

    public void put(String annotationName,String mapContent){
        setIfNull();
        this.listContents.add(mapContent);
        this.mapAnnotations.put(annotationName,Collections.singletonList(mapContent));
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

    private void setIfNull(){
        if(listContents == null || listContents.isEmpty()){
            listContents = new ArrayList<>();
        }
        if(mapAnnotations == null || mapAnnotations.isEmpty()){
            mapAnnotations = new LinkedMultiValueMap<>();
        }
    }

    public void clear(){
        listContents.clear();
        mapAnnotations.clear();
    }

    public int size(){
        return mapAnnotations.size();
    }

    public boolean isEmpty(){
        return mapAnnotations.isEmpty();
        //return values().size();
    }

    public List<String> values(){
        Collection<List<String>> coll = mapAnnotations.values();
        List<String> list = new ArrayList<>();
        for(List<String> annCon : coll){
            list.addAll(annCon);
        }
        return list;
    }

    public Set<Map.Entry<String,List<String>>> entrySet(){
        return mapAnnotations.entrySet();
    }

    public List<String> getList(String annotationName){
        for(Map.Entry<String,List<String>> entry: mapAnnotations.entrySet()){
            if(entry.getKey().toLowerCase().contains(annotationName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<String> getList(Integer indexAnnotation){
        return new ArrayList<>(mapAnnotations.values()).get(indexAnnotation);
    }

    //SUPPORT METHOD

    public List<String> getString(String annotationName){
        List<String> list = getList(annotationName);
        List<String> list2 = new ArrayList<>();
        for(String mapContent: list){
            list2.add(mapContent);
        }
        return list2;
    }

    public List<String> getString(Integer indexAnnotation){
        List<String> list = getList(indexAnnotation);
        List<String> list2 = new ArrayList<>();
        for(String mapContent: list){
            list2.add(mapContent);
        }
        return list2;
    }

    public String find(String nameAnnotation,Integer indexAnnotation){
        String theMapContent = null;
        int i = 0;
        for(String content : getList(nameAnnotation)){
            if(i == indexAnnotation){
                if (content == null || content.isEmpty()) {
                    theMapContent = "";
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

    public String find(Integer indexAnnotation,Integer indexContent){
        String theMapContent = null;
        int i = 0;
        for(String content : getList(indexAnnotation)){
            if(i == indexContent){
                if (content == null || content.isEmpty()) {
                    theMapContent = "";
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

    public String find(String nameAnnotation){
        String theMapContent = null;
        for(Map.Entry<String,List<String>> mapAnn : mapAnnotations.entrySet()){
            for(String content: mapAnn.getValue()) {
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
        for (Map.Entry<String, List<String>> entry : mapAnnotations.entrySet()) {
            if (i == indexAnnotation) {
                return entry.getKey();
            }
            i++;
        }
        return null;
    }

    public String getName(String nameAnnotation) {
        for (Map.Entry<String, List<String>> entry : mapAnnotations.entrySet()) {
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
        return "MapAnnotation{" + "id=" + super.getId() + "; type=" + super.getType() + "; features=" + super.getFeatures() + "; start=" + super.getStartNode().toString() + "; end=" + super.getEndNode().toString() + System.getProperty("line.separator") +
                "mapAnnotations=" + mapAnnotations +
                '}';
    }

    //==============================================================================================
    // OTHER
    //==============================================================================================4

    private String cleanText(String text){
        return text.replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", " ").trim();
    }

    private Boolean isEmpty(String content){
        return (content == null)  || content.isEmpty() || content.trim().isEmpty() || content.equals("");
    }

    private String setContent(String content){
        return cleanText(new String(content.getBytes(), StandardCharsets.UTF_8));
    }

    private String setContent(String content,Charset charset){
        return cleanText(new String(content.getBytes(), charset));
    }

    //==============================================================================================
    //IMPLEMENT INTERFACE ANNOTATION OF GATE
    //==============================================================================================
   /* @Override
    public boolean isCompatible(Annotation annotation) {
        if(annotation == null) {
            return false;
        } else {
            if(this.coextensive(annotation)) {
                if(annotation.getFeatures() == null) {
                    return true;
                }
                if(annotation.getFeatures().subsumes(this.getFeatures())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean isCompatible(Annotation annotation, Set<?> set) {
        if(set == null) {
            return this.isCompatible(annotation);
        } else if(annotation == null) {
            return false;
        } else {
            if(this.coextensive(annotation)) {
                if(annotation.getFeatures() == null) {
                    return true;
                }
                if(annotation.getFeatures().subsumes(this.getFeatures(), set)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean isPartiallyCompatible(Annotation annotation) {
        if(annotation == null) {
            return false;
        } else {
            if(this.overlaps(annotation)) {
                if(annotation.getFeatures() == null) {
                    return true;
                }

                if(annotation.getFeatures().subsumes(this.getFeatures())) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean isPartiallyCompatible(Annotation annotation, Set<?> set) {
        if(set == null) {
            return this.isPartiallyCompatible(annotation);
        } else if(annotation == null) {
            return false;
        } else {
            if(this.overlaps(annotation)) {
                if(annotation.getFeatures() == null) {
                    return true;
                }

                if(annotation.getFeatures().subsumes(this.getFeatures(), set)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean coextensive(Annotation annotation) {
        if(annotation.getStartNode() == null ^ this.getStartNode() == null) {
            return false;
        } else {
            if(annotation.getStartNode() != null) {
                if(annotation.getStartNode().getOffset() == null ^ this.getStartNode().getOffset() == null) {
                    return false;
                }

                if(annotation.getStartNode().getOffset() != null && !annotation.getStartNode().getOffset().equals(this.getStartNode().getOffset())) {
                    return false;
                }
            }

            if(annotation.getEndNode() == null ^ this.getEndNode() == null) {
                return false;
            } else {
                if(annotation.getEndNode() != null) {
                    if(annotation.getEndNode().getOffset() == null ^ this.getEndNode().getOffset() == null) {
                        return false;
                    }

                    if(annotation.getEndNode().getOffset() != null && !annotation.getEndNode().getOffset().equals(this.getEndNode().getOffset())) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    @Override
    public boolean overlaps(Annotation annotation) {
        return annotation != null && ((annotation.getStartNode() != null && annotation.getEndNode() != null && annotation.getStartNode().getOffset() != null && annotation.getEndNode().getOffset() != null) && (annotation.getEndNode().getOffset() > this.getStartNode().getOffset() && annotation.getStartNode().getOffset() < this.getEndNode().getOffset()));
    }

    @Override
    public boolean withinSpanOf(Annotation annotation) {
        return annotation != null && ((annotation.getStartNode() != null && annotation.getEndNode() != null && annotation.getStartNode().getOffset() != null && annotation.getEndNode().getOffset() != null) && (annotation.getEndNode().getOffset() >= this.getEndNode().getOffset() && annotation.getStartNode().getOffset() <= this.getStartNode().getOffset()));
    }

    @Override
    public synchronized void removeAnnotationListener(AnnotationListener annotationListener) {
        if(this.annotationListeners != null && this.annotationListeners.contains(annotationListener)) {
            Vector<AnnotationListener> vector = (Vector<AnnotationListener>)this.annotationListeners.clone();
            vector.removeElement(annotationListener);
            this.annotationListeners = vector;
        }

    }

    @Override
    public synchronized void addAnnotationListener(AnnotationListener annotationListener) {
        Vector<AnnotationListener> vector = this.annotationListeners == null?new Vector<AnnotationListener>(2):(Vector)this.annotationListeners.clone();
        if(vector.isEmpty()) {
            FeatureMap var3 = this.getFeatures();
            if(this.eventHandler == null) {
                this.eventHandler = new MapAnnotation.EventsHandler();
            }

            var3.addFeatureMapListener(this.eventHandler);
        }

        if(!vector.contains(annotationListener)) {
            vector.addElement(annotationListener);
            this.annotationListeners = vector;
        }

    }

    @Override
    public int compareTo(Object o) {
        Annotation var2 = (Annotation)o;
        return this.id.compareTo(var2.getId());
    }

    @Override
    public FeatureMap getFeatures() {
        return this.features;
    }

    @Override
    public void setFeatures(FeatureMap featureMap) {
        if(this.eventHandler != null) {
            this.features.removeFeatureMapListener(this.eventHandler);
        }

        this.features = featureMap;
        if(this.annotationListeners != null && !this.annotationListeners.isEmpty()) {
            this.features.addFeatureMapListener(this.eventHandler);
        }

        this.fireAnnotationUpdated(new AnnotationEvent(this, 701));
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Node getStartNode() {
        return this.start;
    }

    @Override
    public Node getEndNode() {
        return this.end;
    }

    protected void fireAnnotationUpdated(AnnotationEvent annEvent) {
        if(this.annotationListeners != null) {
            Vector annotationListeners = this.annotationListeners;
            int size = annotationListeners.size();

            for(int i = 0; i < size; ++i) {
                ((AnnotationListener)annotationListeners.elementAt(i)).annotationUpdated(annEvent);
            }
        }

    }

    class EventsHandler implements FeatureMapListener, Serializable {
        static final long serialVersionUID = 4448156420244752907L;

        EventsHandler() {
        }

        public void featureMapUpdated() {
            MapAnnotation.this.fireAnnotationUpdated(new AnnotationEvent(MapAnnotation.this, 701));
        }
    }*/

}
