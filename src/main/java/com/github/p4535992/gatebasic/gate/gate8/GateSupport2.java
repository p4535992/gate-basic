package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.*;

import java.util.*;

/**
 * Created by 4535992 on 25/06/2015.
 * @author 4535992
 * @version 2015-11-12
 */
@SuppressWarnings("unused")
public class GateSupport2 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateSupport2.class);

    private MapCorpus mapCorpus;
    private MapAnnotationSet mapAnnotationSet;
    private MapAnnotation mapAnnotation;
    private MapDocument mapDocs;

    private List<MapAnnotationSet> listMapAnnotationSet;
    private List<MapAnnotation> listMapAnnotation;
    private List<MapDocument> listMapDocs;
    private List<MapContent> listMapContent;

    private String content;

    private static GateSupport2 instance = null;

    protected GateSupport2(){
        setIfNull();
    }

    protected GateSupport2(MapDocument mapDocs) {
        setIfNull();
        this.mapCorpus.add(mapDocs);
        this.mapDocs = mapDocs;
        //this.listMapAnnotationSet = mapDocs.getListAnnotationSets();
    }

    protected GateSupport2(MapCorpus mapCorpus,String nameDocument) {
        setIfNull();
        this.mapCorpus = mapCorpus;
        this.listMapDocs = mapCorpus.get(nameDocument);
      /*  this.listMapAnnotationSet =
        this.listMapAnnotation = mapAnnotationSet.getListAnnotations();
        this.listMapContent*/
        //this.mapAnnotationSet = mapDocs.getMapDocs().getFirst(nameDocument);
        //this.mapAnnotation = mapAnnotationSet.getMapAnnotationSets().getFirst(nameAnnotationSet)
    }

    public static GateSupport2 getInstance(MapDocument mapDocs){
        if(instance == null)  instance = new GateSupport2(mapDocs);
        return instance;
    }

    public static GateSupport2 getInstance(MapDocument mapDocs, boolean isNull){
        if(isNull) instance = null;
        return  getInstance(mapDocs);
    }

    public static GateSupport2 getInstance(MapCorpus mapCorpus,String nameDocument){
        if(instance == null) instance = new GateSupport2(mapCorpus,nameDocument);
        return instance;
    }

    public static GateSupport2 getInstance(MapCorpus mapCorpus,String nameDocument, boolean isNull){
        if(isNull) instance = null;
        return  getInstance(mapCorpus,nameDocument);
    }

    private void setIfNull(){
        if(mapCorpus == null) mapCorpus = new MapCorpus();
        if(mapDocs == null) mapDocs = new MapDocument();
        if(mapAnnotationSet == null) mapAnnotationSet = new MapAnnotationSet();
        if(mapAnnotation == null) mapAnnotation = new MapAnnotation();
        if(listMapContent == null) listMapContent = new ArrayList<>();
        if(listMapAnnotation == null) listMapAnnotation = new ArrayList<>();
        if(listMapAnnotationSet == null) listMapAnnotationSet = new ArrayList<>();
        if(listMapDocs == null) listMapDocs = new ArrayList<>();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET CORPUS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MapDocument> getCorpus(){
        if(mapCorpus!=null && mapCorpus.size() >0) {
            this.listMapDocs = mapCorpus.getListDocument();
            return listMapDocs;
        }else{
            logger.warn("The MapDocument is NULL or EMPTY, set up a valid MapDocument for extract something useful");
            return new ArrayList<>();
        }
    }

    public List<MapDocument> getCorpus(Integer index,MapCorpus mapCorpus){
        this.mapCorpus = mapCorpus;
        return getCorpus(index);
    }

    public List<MapDocument> getCorpus(Integer index){
        if(index > mapCorpus.getMapCorpus().size()){
            logger.warn("The index:" + index + " on the map of the documents you try to get not " +
                    "exists on this map of the result of GATE, return NULL");
            return null;
        }
        this.listMapDocs = mapCorpus.get(index);
        return listMapDocs;
        //return mapCorpus.get(index);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET DOCUMENT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MapAnnotationSet> getDocument(){
        if(mapDocs!=null && mapDocs.size() >0) {
            this.listMapAnnotationSet = mapDocs.getListAnnotationSets();
            return listMapAnnotationSet;
            //return mapDocs.getListAnnotationSets();
        }else{
            logger.warn("The MapDocument is NULL or EMPTY, set up a valid MapDocument for extract something useful");
            return new ArrayList<>();
        }
    }

    public List<MapAnnotationSet> getDocument(Integer index,MapDocument mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(index);
    }

    public List<MapAnnotationSet> getDocument(Integer index){
        if(mapDocs.isEmpty()){
            mapDocs = listMapDocs.get(0);
        }
        if(index > mapDocs.size()){
            logger.warn("The index:" + index + " on the map of the documents you try to get not " +
                    "exists on this map of the result of GATE, return NULL");
             return null;
        }
        this.listMapAnnotationSet = mapDocs.get(index);
        return listMapAnnotationSet;
        //return mapDocs.get(index);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument,MapDocument mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(nameDocument,false);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument){
        return getDocument(nameDocument,false);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument,boolean ignorecase){
        if(mapDocs == null || mapDocs.isEmpty()){
            mapDocs = new MapDocument(listMapAnnotationSet);
        }

        for(Map.Entry<String,List<MapAnnotationSet>> entryDocs: mapDocs.entrySet()){
            if(ignorecase){
                if(entryDocs.getKey().equalsIgnoreCase(nameDocument) ||
                        entryDocs.getKey().toLowerCase().contains(nameDocument.toLowerCase())){
                    return entryDocs.getValue();
                }
            }else{
                if(entryDocs.getKey().equals(nameDocument) || entryDocs.getKey().contains(nameDocument)){
                    return entryDocs.getValue();
                }
            }
        }
        logger.warn("The document with the name:" + nameDocument + " not exists on this map of " +
                "the result of GATE, return NULL");
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET ANNOTATIONSET
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MapAnnotation> getAnnotationSet(){
        if(mapDocs!=null && mapDocs.size() >0) {
            List<MapAnnotation> list = new ArrayList<>();
            for(MapAnnotationSet entry: mapDocs.getListAnnotationSets()){
                list.addAll(entry.getListAnnotations());
            }
            //return list;
            this.listMapAnnotation = list;
            return listMapAnnotation;

        }else{
            logger.warn("The MapDocument is NULL or EMPTY, set up a valid MapDocument for extract something useful");
            return new ArrayList<>();
        }
    }

    public List<MapAnnotation> getAnnotationSet(Integer index,MapAnnotationSet mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(index);
    }

    public List<MapAnnotation> getAnnotationSet(Integer index){
        if(mapAnnotationSet.isEmpty()){
            mapAnnotationSet = listMapAnnotationSet.get(0);
        }
        if(index > mapAnnotationSet.size()){
            logger.warn("The index on the map of the annotationSets you try to get not " +
                    "exists on this map of the result of GATE, return NULL");
            return null;
        }
        //List<MapAnnotation> list = new ArrayList<>(mapAnnotationSet.values());
        //this.mapAnnotation = list.get(index);
        //return list.get(index);
        return mapAnnotationSet.get(index);
    }


    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet,MapAnnotationSet mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet){
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet,boolean ignorecase){
        if(mapAnnotationSet == null || mapAnnotationSet.isEmpty()){
            mapAnnotationSet = new MapAnnotationSet(listMapAnnotation);
        }

        for(Map.Entry<String,List<MapAnnotation>> entryAnnSet: mapAnnotationSet.entrySet()){
            if(ignorecase){
                if(entryAnnSet.getKey().equalsIgnoreCase(nameAnnotationSet)
                        || entryAnnSet.getKey().toLowerCase().contains(nameAnnotationSet.toLowerCase())){
                    return entryAnnSet.getValue();
                }
            }else{
                if(entryAnnSet.getKey().equals(nameAnnotationSet) || entryAnnSet.getKey().contains(nameAnnotationSet)){
                    return entryAnnSet.getValue();
                }
            }
        }
        logger.warn("The annotationSet with the name:" + nameAnnotationSet + " not exists on this map of " +
                "the result of GATE, return NULL");
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET ANNOTATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MapContent> getAnnotation(){
        if(mapDocs!=null && mapDocs.size() >0) {
            List<MapContent> list = new ArrayList<>();
            for(MapAnnotationSet entry: mapDocs.getListAnnotationSets()){
                for(MapAnnotation entry2: entry.getListAnnotations()) {
                    list.addAll(entry2.getListContents());
                }
            }
            //return list;
            this.listMapContent = list;
            return listMapContent;
        }else{
            logger.warn("The MapDocument is NULL or EMPTY, set up a valid MapDocument for extract something useful");
            return new ArrayList<>();
        }
    }

    public List<MapContent> getAnnotation(Integer index, MapAnnotation mapAnnotation ){
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(index);
    }

    public List<MapContent> getAnnotation(Integer index){
        if(mapAnnotation.isEmpty()){
            mapAnnotation = listMapAnnotation.get(0);
        }
        if(index > mapAnnotation.size()){
            logger.warn("The index:" + index + " on the map of the annotations you try to get not exists" +
                    " on this map of the result of GATE, return NULL");
            return null;
        }
        return mapAnnotation.get(index);
    }

    public List<MapContent> getAnnotation(String nameAnnotation,MapAnnotation mapAnnotation ) {
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(nameAnnotation,false);
    }

    public List<MapContent> getAnnotation(String nameAnnotation) {
        return getAnnotation(nameAnnotation,false);
    }

    public List<MapContent> getAnnotation(String nameAnnotation,boolean ignorecase){
        if(mapAnnotation == null || mapAnnotation.isEmpty()){
            mapAnnotation = new MapAnnotation(listMapContent);
        }

        for(Map.Entry<String,List<MapContent>> entryAnn: mapAnnotation.entrySet()){
            if(ignorecase){
                if(entryAnn.getKey().equalsIgnoreCase(nameAnnotation) ||
                        entryAnn.getKey().toLowerCase().contains(nameAnnotation.toLowerCase())){
                    return entryAnn.getValue();
                }
            }else{
                if(entryAnn.getKey().equals(nameAnnotation) || entryAnn.getKey().contains(nameAnnotation)){
                    return entryAnn.getValue();
                }
            }
        }
        logger.error("The annotation with the name:" + nameAnnotation + " not exists " +
                "on this map of the result of GATE, return NULL");
        return null;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET CONTENT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<String> getContent(){
        if(mapDocs!=null && mapDocs.size() >0) {
            List<String> list = new ArrayList<>();
            for(MapAnnotationSet entry: mapDocs.getListAnnotationSets()){
                for(MapAnnotation entry2: entry.getListAnnotations()) {
                    for(MapContent entry3: entry2.getListContents()) {
                        list.add(entry3.getContent());
                    }
                }
            }
            return list;
        }else{
            logger.warn("The MapDocument is NULL or EMPTY, set up a valid MapDocument for extract something useful");
            return new ArrayList<>();
        }
    }

    public List<String> getContent(String nameDocument,String nameAnnotationSet,String nameAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(nameDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(nameDocument,nameAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Name:"+
                        nameAnnotationSet+ " for the Document with Name "+nameDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Name "+nameAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation:"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String",ne);
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,String nameAnnotationSet,String nameAnnotation){
        try{
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,nameAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Name:"+
                        nameAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Name "+nameAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Name"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,Integer indexAnnotationSet,String nameAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,indexAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Index:"+
                        indexAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Index "+indexAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Name"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,Integer indexAnnotationSet,Integer indexAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,indexAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Index:"+
                        indexAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(indexAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Index:"+
                        indexAnnotation+ " for the AnnotationSet with Index "+indexAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(indexAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Index"+indexAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET SINGLE CONTENT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getSingleContent(){
        return getContent().get(0);
    }

    public String getSingleContent(Integer indexContent){
        return getContent().get(indexContent);
    }

    public String getSingleContent(String nameDocument,String nameAnnotationSet,String nameAnnotation){
        return getContent(nameDocument,nameAnnotationSet,nameAnnotation).get(0);
    }

    public String getSingleContent(Integer indexDocument,String nameAnnotationSet,String nameAnnotation){
        return getContent(indexDocument,nameAnnotationSet,nameAnnotation).get(0);
    }

    public String getSingleContent(Integer indexDocument,Integer indexAnnotationSet,String nameAnnotation){
       return getContent(indexDocument,indexAnnotationSet,nameAnnotation).get(0);
    }

    public String getSingleContent(Integer indexDocument,Integer indexAnnotationSet,Integer indexAnnotation){
        return getContent(indexDocument,indexAnnotationSet,indexAnnotation).get(0);
    }

    public String getSingleContent(String nameDocument,String nameAnnotationSet,String nameAnnotation,Integer indexContent){
        return getContent(nameDocument,nameAnnotationSet,nameAnnotation).get(indexContent);
    }

    public String getSingleContent(Integer indexDocument,String nameAnnotationSet,String nameAnnotation,Integer indexContent){
        return getContent(indexDocument,nameAnnotationSet,nameAnnotation).get(indexContent);
    }

    public String getSingleContent(Integer indexDocument,Integer indexAnnotationSet,String nameAnnotation,Integer indexContent){
        return getContent(indexDocument,indexAnnotationSet,nameAnnotation).get(indexContent);
    }

    public String getSingleContent(Integer indexDocument,Integer indexAnnotationSet,Integer indexAnnotation,Integer indexContent){
        return getContent(indexDocument,indexAnnotationSet,indexAnnotation).get(indexContent);
    }

}
