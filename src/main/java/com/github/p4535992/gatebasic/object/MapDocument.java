package com.github.p4535992.gatebasic.object;

import com.github.p4535992.gatebasic.gate.gate8.GateUtils;
import gate.*;
import gate.annotation.AnnotationSetImpl;
import gate.corpora.*;
import gate.creole.AbstractLanguageResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.*;
import gate.event.*;
import gate.persist.PersistenceException;
import gate.util.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
@CreoleResource(
        name = "GATE MapDocument",
        interfaceName = "gate.Document",
        comment = "GATE transient document.",
        icon = "document",
        helpURL = "http://gate.ac.uk/userguide/sec:developer:documents"
)
public class MapDocument extends DocumentImpl implements Document{
    static final long serialVersionUID = -4446893608311510260L;
    private List<MapAnnotationSet> listAnnotationSets;
    private MultiValueMap<String, MapAnnotationSet> mapDocs;
    private MultiValueMap<String, Map<String,Map<String,List<String>>>> mapStringDocs;
    //private String name;
    private int index;

    public MapDocument(){
        this("",new ArrayList<>(),null);
    }

    public MapDocument(MapAnnotationSet mapAnnotationSet){
        this("",Collections.singletonList(mapAnnotationSet),null);
    }

    public MapDocument(List<MapAnnotationSet> listAnnotationSets){
        this("",listAnnotationSets,null);
    }

    public MapDocument(String nameDoc, MapAnnotationSet mapAnnotationSet){
        this(nameDoc,Collections.singletonList(mapAnnotationSet),null);
    }

    public MapDocument(URL url){

    }

    public MapDocument(String nameDoc, List<MapAnnotationSet> listAnnotationSets,URL url){
        super();
        if(url != null){
            super.setSourceUrl(url);
            FeatureMap fmap = Factory.newFeatureMap();
            fmap.put("sourceUrl", url);
            super.setFeatures(fmap);
        }
        super.setName(Objects.equals(nameDoc, "") ?setUpDefaultName():nameDoc);
        //New variables
        this.index = 0;

        this.listAnnotationSets = listAnnotationSets;
        this.mapDocs = setUpMapDocs();
        this.mapStringDocs = setUpMapStringDocs();
    }

    //SETTER AND GETTER

    public List<MapAnnotationSet> getListAnnotationSets() {
        return listAnnotationSets;
    }

    public MultiValueMap<String, MapAnnotationSet> getMapDocs() {
        return mapDocs;
    }

    public MultiValueMap<String, Map<String, Map<String, List<String>>>> getMapStringDocs() {
        return mapStringDocs;
    }

    public Map<String,Map<String,Map<String,List<String>>>> getMap(){
        if(mapDocs== null)return new LinkedHashMap<>();
        Map<String,Map<String,Map<String,List<String>>>> result = new LinkedHashMap<>(mapDocs.size());
        for (Map.Entry<String, List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            for(MapAnnotationSet mapAnnotationSet : entry.getValue()) {
                result.put(entry.getKey(),mapAnnotationSet.getMap());
            }
        }
        return result;
    }

    @Deprecated
         //Map<String,Map<String,Map<String,String>>>
    public Map<String,Map<String,Map<String,String>>> getMap2(){
        if(mapDocs== null)return new LinkedHashMap<>();
        Map<String,Map<String,Map<String,Map<String,String>>>> map = new LinkedHashMap<>(mapDocs.size());
        Map<String,Map<String,Map<String,String>>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            for(MapAnnotationSet mapAnnotationSet : entry.getValue()) {
                Map<String,Map<String,String>> result2 = new LinkedHashMap<>();
                for (Map.Entry<String, List<MapAnnotation>> entry2 : mapAnnotationSet.entrySet()) {
                    for(MapAnnotation mapAnnotation : entry2.getValue()) {
                        Map<String,String> result3 = new LinkedHashMap<>();
                        for(Map.Entry<String,List<String>> entry3: mapAnnotation.entrySet()){
                            for(String mapContent: entry3.getValue()){
                                result3.put(entry3.getKey(),mapContent);
                            }
                        }
                        result2.put(entry2.getKey(),result3);
                    }
                }
                result.put(entry.getKey(),result2);
            }
            //map.put(entry.getKey(),result);
        }
        return result;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "Docs#"+index;
    }

    private MultiValueMap<String,MapAnnotationSet> setUpMapDocs(){
        MultiValueMap<String,MapAnnotationSet> map = new LinkedMultiValueMap<>();
        for(MapAnnotationSet mapAnnotationSet : listAnnotationSets){
            map.add(name,mapAnnotationSet);
        }
        return map;
    }

    private MultiValueMap<String,Map<String,Map<String,List<String>>>> setUpMapStringDocs(){
        MultiValueMap<String,Map<String,Map<String,List<String>>>> map = new LinkedMultiValueMap<>();
        for(MapAnnotationSet mapAnnotationSet :  listAnnotationSets){
            map.add(name,mapAnnotationSet.getMap());
        }
        return map;
    }

    public void add(MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.add(setUpDefaultName(),mapAnnotationSet);
        this.mapStringDocs.add(setUpDefaultName(),mapAnnotationSet.getMap());
    }

    public void add(String annotationSetName,MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.add(annotationSetName,mapAnnotationSet);
        this.mapStringDocs.add(annotationSetName,mapAnnotationSet.getMap());
    }

    public void add(MapDocument mapDocument){
        setIfNull();
        this.listAnnotationSets.addAll(mapDocument.getListAnnotationSets());
        addAll(mapDocument);
        addAllString(mapDocument);
    }

    private void addAll(MapDocument mapDocument){
        for(Map.Entry<String,List<MapAnnotationSet>> entry: mapDocument.getMapDocs().entrySet()){
            for(MapAnnotationSet mapAnnotationSet: entry.getValue()) {
                this.mapDocs.add(entry.getKey(),mapAnnotationSet);
            }
        }
    }

    private void addAllString(MapDocument mapDocument){
        for(Map.Entry<String,Map<String,Map<String,List<String>>>> entry: mapDocument.getMap().entrySet()){
          for(Map.Entry<String,Map<String,List<String>>> entry2 : entry.getValue().entrySet()){
              this.mapStringDocs.add(entry.getKey(),entry.getValue());
          }
        }
    }

    public void put(MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.put(setUpDefaultName(),Collections.singletonList(mapAnnotationSet));
        this.mapStringDocs.put(setUpDefaultName(),Collections.singletonList(mapAnnotationSet.getMap()));
    }

    public void put(String annotationSetName,MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.put(annotationSetName,Collections.singletonList(mapAnnotationSet));
        this.mapStringDocs.put(annotationSetName,Collections.singletonList(mapAnnotationSet.getMap()));
    }

    private void setIfNull(){
        if(listAnnotationSets == null || listAnnotationSets.isEmpty()){
            listAnnotationSets = new ArrayList<>();
        }
        if(mapDocs == null || mapDocs.isEmpty()){
            mapDocs = new LinkedMultiValueMap<>();
        }
        if(mapStringDocs == null || mapStringDocs.isEmpty()){
            mapStringDocs = new LinkedMultiValueMap<>();
        }
    }

    public void clear(){
        listAnnotationSets.clear();
        mapDocs.clear();
        mapStringDocs.clear();
    }

    public int size(){
       return mapDocs.size();
        //return values().size();
    }

    public boolean isEmpty(){
        return mapDocs.isEmpty();
        //return values().size();
    }

    public List<MapAnnotationSet> values(){
        Collection<List<MapAnnotationSet>> coll = mapDocs.values();
        List<MapAnnotationSet> list = new ArrayList<>();
        for(List<MapAnnotationSet> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapAnnotationSet>>> entrySet(){
        return mapDocs.entrySet();
    }

    public List<MapAnnotationSet> getList(String documentName){
        for(Map.Entry<String,List<MapAnnotationSet>> entry: mapDocs.entrySet()){
            if(entry.getKey().toLowerCase().contains(documentName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<MapAnnotationSet> getList(Integer indexDocument){
        //List<List<MapAnnotationSet>> l = new ArrayList<>(mapDocs.values());
        return new ArrayList<>(mapDocs.values()).get(indexDocument);
    }

    public boolean hasValue(String key){
        List<MapAnnotationSet> value = mapDocs.get(key);
        return value != null && !value.isEmpty();
    }

    public boolean hasKey(MapAnnotationSet value){
        for (Map.Entry<String,List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            for(MapAnnotationSet annSet: entry.getValue()) {
                if (Objects.equals(value, annSet)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MapAnnotationSet find(String nameDocument,String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet =null;
        List<MapAnnotationSet> list = getList(nameDocument);
        if(list != null && list.size() > 0) {
            for (MapAnnotationSet annSet : list) {
                for (Map.Entry<String, List<MapAnnotation>> entry : annSet.entrySet()) {
                    if (entry.getKey().equals(nameAnnotationSet)) {
                        theMapAnnotationSet = annSet;
                        break;
                    }
                }
                if (theMapAnnotationSet != null) break;
            }
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(String nameDocument,Integer indexAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : getList(nameDocument)){
            if(i == indexAnnotationSet){
                theMapAnnotationSet = annSet;
                break;
            }
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(Integer indexDocument,String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : getList(indexDocument)){
            for(Map.Entry<String,List<MapAnnotation>> entry : annSet.entrySet()){
                if(entry.getKey().equals(nameAnnotationSet)){
                    theMapAnnotationSet = annSet;
                    break;
                }
            }
            if(theMapAnnotationSet!=null)break;
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(Integer indexDocument,Integer indexAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : getList(indexDocument)){
            if(i == indexAnnotationSet){
                theMapAnnotationSet = annSet;
                break;
            }
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        for(Map.Entry<String,List<MapAnnotationSet>> mapAnnSet : mapDocs.entrySet()){
            for(MapAnnotationSet annSet: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<MapAnnotation>> entry : annSet.entrySet()) {
                    if (entry.getKey().equals(nameAnnotationSet)) {
                        theMapAnnotationSet = annSet;
                        break;
                    }
                }
                if (theMapAnnotationSet != null)break;
            }
            if (theMapAnnotationSet != null)break;
        }
        return theMapAnnotationSet;
    }

    public String getName(Integer indexDocument) {
        int i = 0;
        for (Map.Entry<String, List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            if (i == indexDocument) {
                return entry.getKey();
            }
            i++;
        }
        return null;
    }

    public String getName(String nameAnnotationSet) {
        for (Map.Entry<String, List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            if (Objects.equals(entry.getKey(), nameAnnotationSet)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void setName(String s) {

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MapDocument{" +
                "mapDocs=" + mapDocs +
                ", index=" + index +
                '}';
    }

    //==============================================================================================
    /*public FeatureMap getFeatures() {
        if(this.features == null) {
            this.features = new SimpleFeatureMapImpl();
        }

        return this.features;
    }

    public Resource init() throws ResourceInstantiationException {
        if(this.sourceUrl == null) {
            if(this.stringContent == null) {
                throw new ResourceInstantiationException("The sourceURL and document\'s content were null.");
            }

            this.content = new DocumentContentImpl(this.stringContent);
            this.getFeatures().put("gate.SourceURL", "created from String");
        } else {
            try {
                this.content = new DocumentContentImpl(this.sourceUrl, this.getEncoding(), this.sourceUrlStartOffset, this.sourceUrlEndOffset);
                this.getFeatures().put("gate.SourceURL", this.sourceUrl.toExternalForm());
            } catch (IOException var7) {
                throw new ResourceInstantiationException("MapDocument.init: " + var7);
            }
        }

        String var1;
        if(this.preserveOriginalContent.booleanValue() && this.content != null) {
            var1 = new String(((DocumentContentImpl)this.content).getOriginalContent());
            this.getFeatures().put("Original_document_content_on_load", var1);
        }

        if(this.getMarkupAware().booleanValue()) {
            var1 = null;
            DocumentFormat var9;
            if(this.mimeType != null && this.mimeType.length() > 0) {
                MimeType var2 = DocumentFormat.getMimeTypeForString(this.mimeType);
                if(var2 == null) {
                    throw new ResourceInstantiationException("MIME type \"" + this.mimeType + " has no registered DocumentFormat");
                }

                var9 = DocumentFormat.getDocumentFormat(this, var2);
            } else {
                var9 = DocumentFormat.getDocumentFormat(this, this.sourceUrl);
            }

            try {
                if(var9 != null) {
                    StatusListener var10 = (StatusListener)Gate.getListeners().get("gate.event.StatusListener");
                    if(var10 != null) {
                        var9.addStatusListener(var10);
                    }

                    var9.setShouldCollectRepositioning(this.collectRepositioningInfo);
                    if(var9.getShouldCollectRepositioning().booleanValue()) {
                        RepositioningInfo var3 = new RepositioningInfo();
                        String var4 = (String)this.getFeatures().get("Original_document_content_on_load");
                        RepositioningInfo var5 = new RepositioningInfo();
                        if(var4 != null) {
                            boolean var6 = var9 instanceof XmlDocumentFormat;
                            this.collectInformationForAmpCodding(var4, var5, var6);
                            if(var9.getMimeType().equals(new MimeType("text", "html"))) {
                                this.collectInformationForWS(var4, var5);
                            }
                        }

                        var9.unpackMarkup(this, var3, var5);
                        if(var4 != null && var9 instanceof XmlDocumentFormat) {
                            this.correctRepositioningForCRLFInXML(var4, var3);
                        }

                        this.getFeatures().put("Document_repositioning_info", var3);
                    } else {
                        var9.unpackMarkup(this);
                    }

                    var9.removeStatusListener(var10);
                }
            } catch (DocumentFormatException var8) {
                throw new ResourceInstantiationException("Couldn\'t unpack markup in document " + (this.sourceUrl != null?this.sourceUrl.toExternalForm():"") + "!", var8);
            }
        }

        return this;
    }

    private void correctRepositioningForCRLFInXML(String var1, RepositioningInfo var2) {
        int var3 = -1;

        do {
            var3 = var1.indexOf("\r\n", var3 + 1);
            if(var3 != -1) {
                var2.correctInformationOriginalMove((long)var3, 1L);
            }
        } while(var3 != -1);

    }

    private void collectInformationForAmpCodding(String var1, RepositioningInfo var2, boolean var3) {
        if(var1 != null && var2 != null) {
            int var4 = -1;

            int var6;
            do {
                var4 = var1.indexOf(38, var4 + 1);
                if(var4 != -1) {
                    int var5 = var1.indexOf(59, var4 + 1);
                    if(var5 != -1 && var5 - var4 < 8) {
                        var2.addPositionInfo((long)var4, (long)(var5 - var4 + 1), 0L, 1L);
                    } else {
                        var6 = Math.min(var4 + 8, var1.length());
                        String var7 = var1.substring(var4, var6);
                        int var8 = this.analyseAmpCodding(var7);
                        if(var8 != -1) {
                            var2.addPositionInfo((long)var4, (long)var8, 0L, 1L);
                        }
                    }
                }
            } while(var4 != -1);

            var6 = -1;
            if(var3) {
                do {
                    var6 = var1.indexOf("\r\n", var6 + 1);
                    if(var6 != -1) {
                        var2.correctInformationOriginalMove((long)var6, -1L);
                    }
                } while(var6 != -1);
            }

        }
    }

    private int analyseAmpCodding(String var1) {
        int var2 = -1;

        try {
            char var3 = var1.charAt(1);
            switch(var3) {
                case '#':
                    int var4 = 2;
                    boolean var5 = false;
                    if(var1.charAt(2) == 120 || var1.charAt(2) == 88) {
                        ++var4;
                        var5 = true;
                    }

                    while(var4 < 8 && this.isNumber(var1.charAt(var4), var5)) {
                        ++var4;
                    }

                    var2 = var4;
                    break;
                case 'A':
                case 'a':
                    if(var1.substring(2, 4).equalsIgnoreCase("mp")) {
                        var2 = 4;
                    }
                    break;
                case 'G':
                case 'g':
                    if(var1.charAt(2) == 116 || var1.charAt(2) == 84) {
                        var2 = 3;
                    }
                    break;
                case 'L':
                case 'l':
                    if(var1.charAt(2) == 116 || var1.charAt(2) == 84) {
                        var2 = 3;
                    }
                    break;
                case 'Q':
                case 'q':
                    if(var1.substring(2, 5).equalsIgnoreCase("uot")) {
                        var2 = 5;
                    }
            }
        } catch (StringIndexOutOfBoundsException var6) {
            ;
        }

        return var2;
    }

    private boolean isNumber(char var1, boolean var2) {
        if(var1 >= 48 && var1 <= 57) {
            return true;
        } else {
            if(var2) {
                if(var1 >= 65 && var1 <= 70) {
                    return true;
                }

                if(var1 >= 97 && var1 <= 102) {
                    return true;
                }
            }

            return false;
        }
    }

    private void collectInformationForWS(String var1, RepositioningInfo var2) {
        if(var1 != null && var2 != null) {
            int var5 = -1;
            int var4 = -1;
            int var6 = var1.length();

            for(int var7 = 0; var7 < var6; ++var7) {
                char var3 = var1.charAt(var7);
                if(var3 <= 32) {
                    if(var4 == -1) {
                        var4 = var7;
                    }

                    var5 = var7;
                } else {
                    if(var5 - var4 > 0) {
                        var2.addPositionInfo((long)var4, (long)(var5 - var4 + 1), 0L, 1L);
                    }

                    var5 = -1;
                    var4 = -1;
                }
            }

        }
    }

    public void cleanup() {
        this.defaultAnnots = null;
        if(this.namedAnnotSets != null && !this.namedAnnotSets.isEmpty()) {
            this.namedAnnotSets.clear();
        }

        if(this.lrPersistentId != null) {
            Gate.getCreoleRegister().removeCreoleListener(this);
        }

        if(this.getDataStore() != null) {
            this.getDataStore().removeDatastoreListener(this);
        }

    }

    public String getMimeType() {
        return this.mimeType;
    }

    @gate.creole.metadata.Optional
    @CreoleParameter(
            comment = "MIME type of the document.  If unspecified it will be inferred from the file extension, etc."
    )
    public void setMimeType(String var1) {
        this.mimeType = var1;
    }

    public URL getSourceUrl() {
        return this.sourceUrl;
    }

    @CreoleParameter(
            disjunction = "source",
            priority = 1,
            comment = "Source URL",
            suffixes = "txt;text;xml;xhtm;xhtml;html;htm;sgml;sgm;mail;email;eml;rtf;pdf;doc;ppt;pptx;docx;xls;xlsx;ods;odt;odp;iob;conll"
    )
    public void setSourceUrl(URL var1) {
        this.sourceUrl = var1;
    }

    public Long[] getSourceUrlOffsets() {
        Long[] var1 = new Long[]{this.sourceUrlStartOffset, this.sourceUrlEndOffset};
        return var1;
    }

    @CreoleParameter(
            comment = "Should the document preserve the original content?",
            defaultValue = "false"
    )
    public void setPreserveOriginalContent(Boolean var1) {
        this.preserveOriginalContent = var1;
    }

    public Boolean getPreserveOriginalContent() {
        return this.preserveOriginalContent;
    }

    @CreoleParameter(
            defaultValue = "false",
            comment = "Should the document collect repositioning information"
    )
    public void setCollectRepositioningInfo(Boolean var1) {
        this.collectRepositioningInfo = var1;
    }

    public Boolean getCollectRepositioningInfo() {
        return this.collectRepositioningInfo;
    }

    public Long getSourceUrlStartOffset() {
        return this.sourceUrlStartOffset;
    }

    @gate.creole.metadata.Optional
    @CreoleParameter(
            comment = "Start offset for documents based on ranges"
    )
    public void setSourceUrlStartOffset(Long var1) {
        this.sourceUrlStartOffset = var1;
    }

    public Long getSourceUrlEndOffset() {
        return this.sourceUrlEndOffset;
    }

    @gate.creole.metadata.Optional
    @CreoleParameter(
            comment = "End offset for documents based on ranges"
    )
    public void setSourceUrlEndOffset(Long var1) {
        this.sourceUrlEndOffset = var1;
    }

    public DocumentContent getContent() {
        return this.content;
    }

    public void setContent(DocumentContent var1) {
        this.content = var1;
    }

    public String getEncoding() {
        if(this.encoding == null || this.encoding.trim().length() == 0) {
            this.encoding = Charset.forName(System.getProperty("file.encoding")).name();
        }

        return this.encoding;
    }

    @gate.creole.metadata.Optional
    @CreoleParameter(
            comment = "Encoding"
    )
    public void setEncoding(String var1) {
        this.encoding = var1;
    }

    public AnnotationSet getAnnotations() {
        if(this.defaultAnnots == null) {
            this.defaultAnnots = new AnnotationSetImpl(this);
            this.fireAnnotationSetAdded(new DocumentEvent(this, 101, (String)null));
        }

        return this.defaultAnnots;
    }

    public AnnotationSet getAnnotations(String var1) {
        if(var1 != null && !"".equals(var1)) {
            if(this.namedAnnotSets == null) {
                this.namedAnnotSets = new HashMap();
            }

            AnnotationSet var2 = (AnnotationSet)this.namedAnnotSets.get(var1);
            if(var2 == null) {
                var2 = new AnnotationSetImpl(this, var1);
                this.namedAnnotSets.put(var1, var2);
                DocumentEvent var3 = new DocumentEvent(this, 101, var1);
                this.fireAnnotationSetAdded(var3);
            }

            return (AnnotationSet)var2;
        } else {
            return this.getAnnotations();
        }
    }

    @CreoleParameter(
            defaultValue = "true",
            comment = "Should the document read the original markup?"
    )
    public void setMarkupAware(Boolean var1) {
        this.markupAware = var1;
    }

    public Boolean getMarkupAware() {
        return this.markupAware;
    }

    public String toXml(Set<Annotation> var1) {
        return this.toXml(var1, true);
    }

    public String toXml(Set<Annotation> var1, boolean var2) {
        if(this.hasOriginalContentFeatures()) {
            return this.saveAnnotationSetAsXmlInOrig(var1, var2);
        } else {
            AnnotationSet var3 = this.getAnnotations("Original markups");
            ArrayList var4 = new ArrayList(var3.size());
            StatusListener var5 = (StatusListener)Gate.getListeners().get("gate.event.StatusListener");
            if(var5 != null) {
                var5.statusChanged("Constructing the dumping annotation set.");
            }

            var4.addAll(var3);
            if(var1 != null) {
                Iterator var6 = var1.iterator();

                while(var6.hasNext()) {
                    Annotation var7 = (Annotation)var6.next();
                    if(this.insertsSafety((List)var4, var7)) {
                        var4.add(var7);
                    } else if(this.crossedOverAnnotation != null) {
                        ;
                    }
                }
            }

            Collections.sort(var4, new OffsetComparator());
            if(var5 != null) {
                var5.statusChanged("Dumping annotations as XML");
            }

            StringBuffer var9 = new StringBuffer(40 * this.getContent().size().intValue());
            String var10 = this.getFeatures() == null?null:(String)this.getFeatures().get("MimeType");
            boolean var8 = var10 != null && var10.equalsIgnoreCase("text/xml");
            if(var8) {
                var9.append("<?xml version=\"1.0\" encoding=\"");
                var9.append(this.getEncoding());
                var9.append("\" ?>");
                var9.append(Strings.getNl());
            }

            this.theRootAnnotation = this.identifyTheRootAnnotation((List)var4);
            if(this.theRootAnnotation != null) {
                var4.remove(this.theRootAnnotation);
                var9.append(this.writeStartTag(this.theRootAnnotation, var2));
            }

            var9.append(this.saveAnnotationSetAsXml((List)var4, var2));
            if(this.theRootAnnotation != null) {
                var9.append(this.writeEndTag(this.theRootAnnotation));
            }

            if(var5 != null) {
                var5.statusChanged("Done.");
            }

            return var9.toString();
        }
    }

    private boolean insertsSafety(AnnotationSet var1, Annotation var2) {
        if(var1 != null && var2 != null) {
            if(var2.getStartNode() != null && var2.getStartNode().getOffset() != null) {
                if(var2.getEndNode() != null && var2.getEndNode().getOffset() != null) {
                    Long var3 = var2.getStartNode().getOffset();
                    Long var4 = var2.getEndNode().getOffset();
                    long var5 = var3.longValue();
                    long var7 = var4.longValue();
                    AnnotationSet var9 = var1.get(var3, var4);
                    Iterator var10 = var9.iterator();

                    Annotation var11;
                    long var12;
                    long var14;
                    do {
                        if(!var10.hasNext()) {
                            return true;
                        }

                        var11 = (Annotation)var10.next();
                        var12 = var11.getStartNode().getOffset().longValue();
                        var14 = var11.getEndNode().getOffset().longValue();
                        if(var12 < var5 && var5 < var14 && var14 < var7) {
                            this.crossedOverAnnotation = var11;
                            return false;
                        }
                    } while(var5 >= var12 || var12 >= var7 || var7 >= var14);

                    this.crossedOverAnnotation = var11;
                    return false;
                } else {
                    this.crossedOverAnnotation = null;
                    return false;
                }
            } else {
                this.crossedOverAnnotation = null;
                return false;
            }
        } else {
            this.crossedOverAnnotation = null;
            return false;
        }
    }

    private boolean insertsSafety(List<Annotation> var1, Annotation var2) {
        if(var1 != null && var2 != null) {
            if(var2.getStartNode() != null && var2.getStartNode().getOffset() != null) {
                if(var2.getEndNode() != null && var2.getEndNode().getOffset() != null) {
                    Long var3 = var2.getStartNode().getOffset();
                    Long var4 = var2.getEndNode().getOffset();
                    long var5 = var3.longValue();
                    long var7 = var4.longValue();
                    ArrayList var9 = new ArrayList();

                    Annotation var11;
                    for(int var10 = 0; var10 < var1.size(); ++var10) {
                        var11 = (Annotation)var1.get(var10);
                        if(var11.getStartNode().getOffset().longValue() >= var5 && var11.getStartNode().getOffset().longValue() <= var7) {
                            var9.add(var11);
                        } else if(var11.getEndNode().getOffset().longValue() >= var5 && var11.getEndNode().getOffset().longValue() <= var7) {
                            var9.add(var11);
                        }
                    }

                    Iterator var16 = var9.iterator();

                    long var12;
                    long var14;
                    do {
                        if(!var16.hasNext()) {
                            return true;
                        }

                        var11 = (Annotation)var16.next();
                        var12 = var11.getStartNode().getOffset().longValue();
                        var14 = var11.getEndNode().getOffset().longValue();
                        if(var12 < var5 && var5 < var14 && var14 < var7) {
                            this.crossedOverAnnotation = var11;
                            return false;
                        }
                    } while(var5 >= var12 || var12 >= var7 || var7 >= var14);

                    this.crossedOverAnnotation = var11;
                    return false;
                } else {
                    this.crossedOverAnnotation = null;
                    return false;
                }
            } else {
                this.crossedOverAnnotation = null;
                return false;
            }
        } else {
            this.crossedOverAnnotation = null;
            return false;
        }
    }

    private String saveAnnotationSetAsXml(AnnotationSet var1, boolean var2) {
        String var3 = null;
        if(this.getContent() == null) {
            var3 = new String("");
        } else {
            var3 = this.getContent().toString();
        }

        StringBuffer var4 = DocumentXmlUtils.filterNonXmlChars(new StringBuffer(var3));
        if(var1 == null) {
            return var4.toString();
        } else {
            TreeMap var5 = new TreeMap();
            if(this.getContent().size().longValue() != 0L) {
                this.buildEntityMapFromString(var3, var5);
            }

            TreeSet var6 = new TreeSet();
            Iterator var7 = var1.iterator();

            while(var7.hasNext()) {
                Annotation var8 = (Annotation)var7.next();
                var6.add(var8.getStartNode().getOffset());
                var6.add(var8.getEndNode().getOffset());
            }

            StringBuffer var10;
            Long var15;
            label115:
            for(; !var6.isEmpty(); var4.insert(var15.intValue(), var10.toString())) {
                var15 = (Long)var6.last();
                var6.remove(var15);
                List var9 = this.getAnnotationsForOffset((Set)var1, var15);
                var10 = new StringBuffer(3 * this.getContent().size().intValue());
                Stack var11 = new Stack();
                Iterator var12 = var9.iterator();

                while(true) {
                    while(true) {
                        while(true) {
                            Annotation var13;
                            while(var12.hasNext()) {
                                var13 = (Annotation)var12.next();
                                var12.remove();
                                Annotation var14;
                                if(var15.equals(var13.getEndNode().getOffset())) {
                                    if(var15.equals(var13.getStartNode().getOffset())) {
                                        if(null != var13.getFeatures().get("isEmptyAndSpan") && "true".equals(var13.getFeatures().get("isEmptyAndSpan"))) {
                                            var10.append(this.writeStartTag(var13, var2));
                                            var11.push(var13);
                                        } else {
                                            var10.append(this.writeEmptyTag(var13));
                                            var1.remove(var13);
                                        }
                                    } else {
                                        if(!var11.isEmpty()) {
                                            while(!var11.isEmpty()) {
                                                var14 = (Annotation)var11.pop();
                                                var10.append(this.writeEndTag(var14));
                                            }
                                        }

                                        var10.append(this.writeEndTag(var13));
                                    }
                                } else if(var15.equals(var13.getStartNode().getOffset())) {
                                    if(!var11.isEmpty()) {
                                        while(!var11.isEmpty()) {
                                            var14 = (Annotation)var11.pop();
                                            var10.append(this.writeEndTag(var14));
                                        }
                                    }

                                    var10.append(this.writeStartTag(var13, var2));
                                    var1.remove(var13);
                                }
                            }

                            if(!var11.isEmpty()) {
                                while(!var11.isEmpty()) {
                                    var13 = (Annotation)var11.pop();
                                    var10.append(this.writeEndTag(var13));
                                }
                            }

                            if(!var5.isEmpty()) {
                                Long var16 = (Long)var5.lastKey();

                                while(!var5.isEmpty() && var16.intValue() >= var15.intValue()) {
                                    var4.replace(var16.intValue(), var16.intValue() + 1, (String)DocumentXmlUtils.entitiesMap.get(var5.get(var16)));
                                    var5.remove(var16);
                                    if(!var5.isEmpty()) {
                                        var16 = (Long)var5.lastKey();
                                    }
                                }
                            }
                            continue label115;
                        }
                    }
                }
            }

            while(!var5.isEmpty()) {
                var15 = (Long)var5.lastKey();
                var4.replace(var15.intValue(), var15.intValue() + 1, (String)DocumentXmlUtils.entitiesMap.get(var5.get(var15)));
                var5.remove(var15);
            }

            return var4.toString();
        }
    }

    private String saveAnnotationSetAsXml(List<Annotation> var1, boolean var2) {
        String var3;
        if(this.getContent() == null) {
            var3 = "";
        } else {
            var3 = this.getContent().toString();
        }

        StringBuffer var4 = DocumentXmlUtils.filterNonXmlChars(new StringBuffer(var3));
        if(var1 == null) {
            return var4.toString();
        } else {
            StringBuffer var5 = new StringBuffer(3 * this.getContent().size().intValue());
            Long var6 = Long.valueOf(0L);
            TreeMap var7 = new TreeMap();
            HashMap var8 = new HashMap(100);
            if(this.getContent().size().longValue() != 0L) {
                this.buildEntityMapFromString(var3, var7);
            }

            TreeSet var9 = new TreeSet();
            Iterator var10 = var1.iterator();

            while(var10.hasNext()) {
                Annotation var11 = (Annotation)var10.next();
                Long var12 = var11.getStartNode().getOffset();
                Long var13 = var11.getEndNode().getOffset();
                var9.add(var12);
                var9.add(var13);
                ArrayList var14;
                if(var8.containsKey(var12)) {
                    ((List)var8.get(var12)).add(var11);
                } else {
                    var14 = new ArrayList(10);
                    var14.add(var11);
                    var8.put(var12, var14);
                }

                if(var8.containsKey(var13)) {
                    ((List)var8.get(var13)).add(var11);
                } else {
                    var14 = new ArrayList(10);
                    var14.add(var11);
                    var8.put(var13, var14);
                }
            }

            Iterator var27 = var9.iterator();
            StringBuffer var17 = new StringBuffer(255);

            Long var15;
            label117:
            for(Stack var18 = new Stack(); var27.hasNext(); var6 = var15) {
                var15 = (Long)var27.next();
                List var16 = (List)var8.get(var15);
                var16 = this.getAnnotationsForOffset(var16, var15);
                var17.setLength(0);
                var18.clear();
                Iterator var19 = var16.iterator();

                while(true) {
                    while(true) {
                        while(true) {
                            Annotation var21;
                            while(var19.hasNext()) {
                                Annotation var20 = (Annotation)var19.next();
                                if(var15.equals(var20.getEndNode().getOffset())) {
                                    if(var15.equals(var20.getStartNode().getOffset())) {
                                        if(null != var20.getFeatures().get("isEmptyAndSpan") && "true".equals(var20.getFeatures().get("isEmptyAndSpan"))) {
                                            var17.append(this.writeStartTag(var20, var2));
                                            var18.push(var20);
                                        } else {
                                            var17.append(this.writeEmptyTag(var20));
                                            var1.remove(var20);
                                        }
                                    } else {
                                        if(!var18.isEmpty()) {
                                            while(!var18.isEmpty()) {
                                                var21 = (Annotation)var18.pop();
                                                var17.append(this.writeEndTag(var21));
                                            }
                                        }

                                        var17.append(this.writeEndTag(var20));
                                    }
                                } else if(var15.equals(var20.getStartNode().getOffset())) {
                                    if(!var18.isEmpty()) {
                                        while(!var18.isEmpty()) {
                                            var21 = (Annotation)var18.pop();
                                            var17.append(this.writeEndTag(var21));
                                        }
                                    }

                                    var17.append(this.writeStartTag(var20, var2));
                                }
                            }

                            if(!var18.isEmpty()) {
                                while(!var18.isEmpty()) {
                                    var21 = (Annotation)var18.pop();
                                    var17.append(this.writeEndTag(var21));
                                }
                            }

                            StringBuffer var22 = new StringBuffer();
                            SortedMap var23 = var7.subMap(var6, var15);
                            Long var25 = var6;

                            while(!var23.isEmpty()) {
                                Long var24 = (Long)var23.firstKey();
                                String var26 = (String)DocumentXmlUtils.entitiesMap.get(var7.get(var24));
                                var22.append(var4.substring(var25.intValue(), var24.intValue()));
                                var22.append(var26);
                                var25 = Long.valueOf(var24.longValue() + 1L);
                                var23.remove(var24);
                            }

                            var22.append(var4.substring(var25.intValue(), var15.intValue()));
                            var5.append(var22);
                            var5.append(var17.toString());
                            continue label117;
                        }
                    }
                }
            }

            StringBuffer var28 = new StringBuffer();
            SortedMap var29 = var7.subMap(var6, Long.valueOf((long)var4.length()));
            Long var31 = var6;

            while(!var29.isEmpty()) {
                Long var30 = (Long)var29.firstKey();
                String var32 = (String)DocumentXmlUtils.entitiesMap.get(var7.get(var30));
                var28.append(var4.substring(var31.intValue(), var30.intValue()));
                var28.append(var32);
                var31 = Long.valueOf(var30.longValue() + 1L);
                var29.remove(var30);
            }

            var28.append(var4.substring(var31.intValue(), var4.length()));
            var5.append(var28);
            return var5.toString();
        }
    }

    private boolean hasOriginalContentFeatures() {
        FeatureMap var1 = this.getFeatures();
        boolean var2 = false;
        var2 = var1.get("Original_document_content_on_load") != null && var1.get("Document_repositioning_info") != null;
        return var2;
    }

    private String saveAnnotationSetAsXmlInOrig(Set<Annotation> var1, boolean var2) {
        String var4 = (String)this.features.get("Original_document_content_on_load");
        if(var4 == null) {
            var4 = "";
        }

        long var5 = (long)var4.length();
        RepositioningInfo var7 = (RepositioningInfo)this.getFeatures().get("Document_repositioning_info");
        StringBuffer var3 = new StringBuffer(var4);
        if(var1 == null) {
            return var3.toString();
        } else {
            StatusListener var8 = (StatusListener)Gate.getListeners().get("gate.event.StatusListener");
            AnnotationSet var9 = this.getAnnotations("Original markups");
            AnnotationSetImpl var10 = new AnnotationSetImpl(this);
            if(var8 != null) {
                var8.statusChanged("Constructing the dumping annotation set.");
            }

            if(var1 != null) {
                Iterator var11 = var1.iterator();

                label136:
                while(true) {
                    while(true) {
                        if(!var11.hasNext()) {
                            break label136;
                        }

                        Annotation var12 = (Annotation)var11.next();
                        if(this.insertsSafety(var9, var12) && this.insertsSafety((AnnotationSet)var10, var12)) {
                            var10.add(var12);
                        } else {
                            Out.prln("Warning: Annotation with ID=" + var12.getId() + ", startOffset=" + var12.getStartNode().getOffset() + ", endOffset=" + var12.getEndNode().getOffset() + ", type=" + var12.getType() + " was found to violate the" + " crossed over condition. It will be discarded");
                        }
                    }
                }
            }

            if(var8 != null) {
                var8.statusChanged("Dumping annotations as XML");
            }

            TreeSet var22 = new TreeSet();
            Iterator var23 = var1.iterator();

            while(var23.hasNext()) {
                Annotation var13 = (Annotation)var23.next();
                var22.add(var13.getStartNode().getOffset());
                var22.add(var13.getEndNode().getOffset());
            }

            while(true) {
                label118:
                while(!var22.isEmpty()) {
                    Long var24 = (Long)var22.last();
                    var22.remove(var24);
                    List var14 = this.getAnnotationsForOffset(var1, var24);
                    StringBuffer var15 = new StringBuffer("");
                    Stack var16 = new Stack();
                    Iterator var17 = var14.iterator();
                    Annotation var18 = null;

                    while(true) {
                        while(true) {
                            while(true) {
                                Annotation var19;
                                while(var17.hasNext()) {
                                    var18 = (Annotation)var17.next();
                                    var17.remove();
                                    if(var24.equals(var18.getEndNode().getOffset())) {
                                        if(var24.equals(var18.getStartNode().getOffset())) {
                                            if(null != var18.getFeatures().get("isEmptyAndSpan") && "true".equals(var18.getFeatures().get("isEmptyAndSpan"))) {
                                                var15.append(this.writeStartTag(var18, var2, false));
                                                var16.push(var18);
                                            } else {
                                                var15.append(this.writeEmptyTag(var18, false));
                                                var1.remove(var18);
                                            }
                                        } else {
                                            while(!var16.isEmpty()) {
                                                var19 = (Annotation)var16.pop();
                                                var15.append(this.writeEndTag(var19));
                                            }

                                            var15.append(this.writeEndTag(var18));
                                        }
                                    } else if(var24.equals(var18.getStartNode().getOffset())) {
                                        while(!var16.isEmpty()) {
                                            var19 = (Annotation)var16.pop();
                                            var15.append(this.writeEndTag(var19));
                                        }

                                        var15.append(this.writeStartTag(var18, var2, false));
                                        var1.remove(var18);
                                    }
                                }

                                while(!var16.isEmpty()) {
                                    var19 = (Annotation)var16.pop();
                                    var15.append(this.writeEndTag(var19));
                                }

                                long var25 = -1L;
                                boolean var21 = var18 != null && var24.equals(var18.getEndNode().getOffset());
                                if(var21) {
                                    var25 = var7.getOriginalPos((long)var24.intValue(), true);
                                }

                                if(var25 == -1L) {
                                    var25 = var7.getOriginalPos((long)var24.intValue());
                                }

                                if(var25 != -1L && var25 <= var5) {
                                    var3.insert((int)var25, var15.toString());
                                    continue label118;
                                }

                                Out.prln("Error in the repositioning. The offset (" + var24.intValue() + ") could not be positioned in the original document. \n" + "Calculated position is: " + var25 + " placed back: " + var21);
                                continue label118;
                            }
                        }
                    }
                }

                if(this.theRootAnnotation != null) {
                    var3.append(this.writeEndTag(this.theRootAnnotation));
                }

                return var3.toString();
            }
        }
    }

    private List<Annotation> getAnnotationsForOffset(Set<Annotation> var1, Long var2) {
        LinkedList var3 = new LinkedList();
        if(var1 != null && var2 != null) {
            TreeSet var4 = new TreeSet(new MapDocument.AnnotationComparator(1, -3));
            TreeSet var5 = new TreeSet(new MapDocument.AnnotationComparator(0, -3));
            TreeSet var6 = new TreeSet(new MapDocument.AnnotationComparator(2, 3));
            Iterator var7 = var1.iterator();

            Annotation var8;
            while(var7.hasNext()) {
                var8 = (Annotation)var7.next();
                if(var2.equals(var8.getStartNode().getOffset())) {
                    if(var2.equals(var8.getEndNode().getOffset())) {
                        var6.add(var8);
                    } else {
                        var4.add(var8);
                    }
                } else if(var2.equals(var8.getEndNode().getOffset())) {
                    var5.add(var8);
                }
            }

            var3.addAll(var5);
            var5 = null;
            var3.addAll(var4);
            var4 = null;

            for(var7 = var6.iterator(); var7.hasNext(); var7.remove()) {
                var8 = (Annotation)var7.next();
                Iterator var9 = var3.iterator();
                boolean var10 = false;

                while(var9.hasNext()) {
                    Annotation var11 = (Annotation)var9.next();
                    if(var11.getId().intValue() > var8.getId().intValue()) {
                        var3.add(var3.indexOf(var11), var8);
                        var10 = true;
                        break;
                    }
                }

                if(!var10) {
                    var3.add(var8);
                }
            }

            return var3;
        } else {
            return var3;
        }
    }

    private List<Annotation> getAnnotationsForOffset(List<Annotation> var1, Long var2) {
        ArrayList var3 = new ArrayList();
        if(var1 != null && var2 != null) {
            TreeSet var4 = new TreeSet(new MapDocument.AnnotationComparator(1, -3));
            TreeSet var5 = new TreeSet(new MapDocument.AnnotationComparator(0, -3));
            TreeSet var6 = new TreeSet(new MapDocument.AnnotationComparator(2, 3));
            Iterator var7 = var1.iterator();

            Annotation var8;
            while(var7.hasNext()) {
                var8 = (Annotation)var7.next();
                if(var2.equals(var8.getStartNode().getOffset())) {
                    if(var2.equals(var8.getEndNode().getOffset())) {
                        var6.add(var8);
                    } else {
                        var4.add(var8);
                    }
                } else if(var2.equals(var8.getEndNode().getOffset())) {
                    var5.add(var8);
                }
            }

            var3.addAll(var5);
            var3.addAll(var4);
            var5 = null;
            var4 = null;

            for(var7 = var6.iterator(); var7.hasNext(); var7.remove()) {
                var8 = (Annotation)var7.next();
                Iterator var9 = var3.iterator();
                boolean var10 = false;

                while(var9.hasNext()) {
                    Annotation var11 = (Annotation)var9.next();
                    if(var11.getId().intValue() > var8.getId().intValue()) {
                        var3.add(var3.indexOf(var11), var8);
                        var10 = true;
                        break;
                    }
                }

                if(!var10) {
                    var3.add(var8);
                }
            }

            return var3;
        } else {
            return var3;
        }
    }

    private String writeStartTag(Annotation var1, boolean var2) {
        return this.writeStartTag(var1, var2, true);
    }

    private String writeStartTag(Annotation var1, boolean var2, boolean var3) {
        String var4 = null;
        if(this.serializeNamespaceInfo) {
            var4 = (String)var1.getFeatures().get(this.namespacePrefixFeature);
        }

        AnnotationSet var5 = this.getAnnotations("Original markups");
        StringBuffer var6 = new StringBuffer("");
        if(var1 == null) {
            return var6.toString();
        } else {
            if(this.theRootAnnotation != null && var1.getId().equals(this.theRootAnnotation.getId())) {
                if(var2) {
                    var6.append("<");
                    if(var4 != null && !var4.isEmpty()) {
                        var6.append(var4 + ":");
                    }

                    var6.append(var1.getType());
                    var6.append(" ");
                    if(var3) {
                        if(var1.getFeatures().get("xmlns:gate") == null) {
                            var6.append("xmlns:gate=\"http://www.gate.ac.uk\"");
                        }

                        var6.append(" gate:");
                    }

                    var6.append("gateId=\"");
                    var6.append(var1.getId());
                    var6.append("\"");
                    var6.append(" ");
                    if(var3) {
                        var6.append("gate:");
                    }

                    var6.append("annotMaxId=\"");
                    var6.append(this.nextAnnotationId);
                    var6.append("\"");
                    var6.append(this.writeFeatures(var1.getFeatures(), var3));
                    var6.append(">");
                } else if(var5.contains(var1)) {
                    var6.append("<");
                    if(var4 != null && !var4.isEmpty()) {
                        var6.append(var4 + ":");
                    }

                    var6.append(var1.getType());
                    var6.append(this.writeFeatures(var1.getFeatures(), var3));
                    var6.append(">");
                } else {
                    var6.append("<");
                    if(var4 != null && !var4.isEmpty()) {
                        var6.append(var4 + ":");
                    }

                    var6.append(var1.getType());
                    var6.append(">");
                }
            } else if(var2) {
                var6.append("<");
                if(var4 != null && !var4.isEmpty()) {
                    var6.append(var4 + ":");
                }

                var6.append(var1.getType());
                var6.append(" ");
                if(var3) {
                    var6.append("gate:");
                }

                var6.append("gateId=\"");
                var6.append(var1.getId());
                var6.append("\"");
                var6.append(this.writeFeatures(var1.getFeatures(), var3));
                var6.append(">");
            } else if(var5.contains(var1)) {
                var6.append("<");
                if(var4 != null && !var4.isEmpty()) {
                    var6.append(var4 + ":");
                }

                var6.append(var1.getType());
                var6.append(this.writeFeatures(var1.getFeatures(), var3));
                var6.append(">");
            } else {
                var6.append("<");
                if(var4 != null && !var4.isEmpty()) {
                    var6.append(var4 + ":");
                }

                var6.append(var1.getType());
                var6.append(">");
            }

            return var6.toString();
        }
    }

    private Annotation identifyTheRootAnnotation(AnnotationSet var1) {
        if(var1 == null) {
            return null;
        } else {
            Node var2 = var1.firstNode();
            Node var3 = var1.lastNode();
            if(var2.getOffset().longValue() != 0L) {
                return null;
            } else {
                Annotation var4 = null;
                long var5 = var2.getOffset().longValue();
                long var7 = var3.getOffset().longValue();
                Iterator var9 = var1.iterator();

                while(var9.hasNext()) {
                    Annotation var10 = (Annotation)var9.next();
                    if(var5 == var10.getStartNode().getOffset().longValue() && var7 == var10.getEndNode().getOffset().longValue()) {
                        if(var4 == null) {
                            var4 = var10;
                        } else if(var4.getId().intValue() > var10.getId().intValue()) {
                            var4 = var10;
                        }
                    }
                }

                return var4;
            }
        }
    }

    private Annotation identifyTheRootAnnotation(List<Annotation> var1) {
        if(var1 != null && !var1.isEmpty()) {
            if(((Annotation)var1.get(0)).getStartNode().getOffset().longValue() > 0L) {
                return null;
            } else if(var1.size() == 1) {
                Annotation var13 = (Annotation)var1.get(0);
                return var13.getEndNode().getOffset().equals(this.content.size())?var13:null;
            } else {
                long var2 = 0L;
                long var4 = 0L;

                for(int var6 = 0; var6 < var1.size(); ++var6) {
                    Annotation var7 = (Annotation)var1.get(var6);
                    long var8 = var7.getEndNode().getOffset().longValue();
                    if(var8 > var4) {
                        var4 = var8;
                    }
                }

                Annotation var14 = null;

                for(int var15 = 0; var15 < var1.size(); ++var15) {
                    Annotation var16 = (Annotation)var1.get(var15);
                    long var9 = var16.getStartNode().getOffset().longValue();
                    long var11 = var16.getEndNode().getOffset().longValue();
                    if(var2 == var9 && var4 == var11) {
                        if(var14 == null) {
                            var14 = var16;
                        } else if(var14.getId().intValue() > var16.getId().intValue()) {
                            var14 = var16;
                        }
                    }
                }

                return var14;
            }
        } else {
            return null;
        }
    }

    private void buildEntityMapFromString(String var1, TreeMap<Long, Character> var2) {
        if(var1 != null && var2 != null) {
            if(DocumentXmlUtils.entitiesMap != null && !DocumentXmlUtils.entitiesMap.isEmpty()) {
                Iterator var3 = DocumentXmlUtils.entitiesMap.keySet().iterator();

                while(var3.hasNext()) {
                    Character var4 = (Character)var3.next();
                    int var5 = 0;

                    while(-1 != var5) {
                        var5 = var1.indexOf(var4.charValue(), var5);
                        if(-1 != var5) {
                            var2.put(new Long((long)var5), var4);
                            ++var5;
                        }
                    }
                }

            } else {
                Err.prln("WARNING: Entities map was not initialised !");
            }
        }
    }

    private String writeEmptyTag(Annotation var1) {
        return this.writeEmptyTag(var1, true);
    }

    private String writeEmptyTag(Annotation var1, boolean var2) {
        String var3 = null;
        if(this.serializeNamespaceInfo) {
            var3 = (String)var1.getFeatures().get(this.namespacePrefixFeature);
        }

        StringBuffer var4 = new StringBuffer("");
        if(var1 == null) {
            return var4.toString();
        } else {
            var4.append("<");
            if(var3 != null && !var3.isEmpty()) {
                var4.append(var3 + ":");
            }

            var4.append(var1.getType());
            AnnotationSet var5 = this.getAnnotations("Original markups");
            if(!var5.contains(var1)) {
                var4.append(" gateId=\"");
                var4.append(var1.getId());
                var4.append("\"");
            }

            var4.append(this.writeFeatures(var1.getFeatures(), var2));
            var4.append("/>");
            return var4.toString();
        }
    }

    private String writeEndTag(Annotation var1) {
        String var2 = null;
        if(this.serializeNamespaceInfo) {
            var2 = (String)var1.getFeatures().get(this.namespacePrefixFeature);
        }

        StringBuffer var3 = new StringBuffer("");
        if(var1 == null) {
            return var3.toString();
        } else {
            var3.append("</");
            if(var2 != null && !var2.isEmpty()) {
                var3.append(var2 + ":");
            }

            var3.append(var1.getType() + ">");
            return var3.toString();
        }
    }

    private String writeFeatures(FeatureMap var1, boolean var2) {
        StringBuffer var3 = new StringBuffer("");
        if(var1 == null) {
            return var3.toString();
        } else {
            Iterator var4 = var1.keySet().iterator();

            while(true) {
                while(true) {
                    Object var5;
                    Object var6;
                    label73:
                    do {
                        String var7;
                        do {
                            do {
                                do {
                                    if(!var4.hasNext()) {
                                        return var3.toString();
                                    }

                                    var5 = var4.next();
                                    var6 = var1.get(var5);
                                } while(var5 == null);
                            } while(var6 == null);

                            if(!this.serializeNamespaceInfo) {
                                continue label73;
                            }

                            var7 = "xmlns:" + (String)var1.get(this.namespacePrefixFeature);
                        } while(var7.equals(var5.toString()) || this.namespacePrefixFeature.equals(var5.toString()));

                        if(this.namespaceURIFeature.equals(var5.toString())) {
                            var3.append(" ");
                            var3.append(var7 + "=\"" + var6.toString() + "\"");
                            return var3.toString();
                        }
                    } while("isEmptyAndSpan".equals(var5.toString()));

                    if(!String.class.isAssignableFrom(var5.getClass())) {
                        Out.prln("Warning:Found a feature NAME(" + var5 + ") that isn\'t a String.(feature discarded)");
                    } else if(!String.class.isAssignableFrom(var6.getClass()) && !Number.class.isAssignableFrom(var6.getClass()) && !Collection.class.isAssignableFrom(var6.getClass()) && !Boolean.class.isAssignableFrom(var6.getClass())) {
                        Out.prln("Warning:Found a feature VALUE(" + var6 + ") that doesn\'t came" + " from String, Number, Boolean, or Collection.(feature discarded)");
                    } else {
                        if("matches".equals(var5)) {
                            var3.append(" ");
                            if(var2) {
                                var3.append("gate:");
                            }

                            var3.append(DocumentXmlUtils.combinedNormalisation(var5.toString()));
                            var3.append("=\"");
                        } else {
                            var3.append(" ");
                            var3.append(DocumentXmlUtils.combinedNormalisation(var5.toString()));
                            var3.append("=\"");
                        }

                        if(Collection.class.isAssignableFrom(var6.getClass())) {
                            Iterator var9 = ((Collection)var6).iterator();

                            label89:
                            while(true) {
                                Object var8;
                                do {
                                    if(!var9.hasNext()) {
                                        if(var3.charAt(var3.length() - 1) == 59) {
                                            var3.deleteCharAt(var3.length() - 1);
                                        }
                                        break label89;
                                    }

                                    var8 = var9.next();
                                } while(!String.class.isAssignableFrom(var8.getClass()) && !Number.class.isAssignableFrom(var8.getClass()));

                                var3.append(DocumentXmlUtils.combinedNormalisation(var8.toString()));
                                var3.append(";");
                            }
                        } else {
                            var3.append(DocumentXmlUtils.combinedNormalisation(var6.toString()));
                        }

                        var3.append("\"");
                    }
                }
            }
        }
    }

    public String toXml() {
        return DocumentStaxUtils.toXml(this);
    }

    public Map<String, AnnotationSet> getNamedAnnotationSets() {
        if(this.namedAnnotSets == null) {
            this.namedAnnotSets = new HashMap();
        }

        return this.namedAnnotSets;
    }

    public Set<String> getAnnotationSetNames() {
        if(this.namedAnnotSets == null) {
            this.namedAnnotSets = new HashMap();
        }

        return this.namedAnnotSets.keySet();
    }

    public void removeAnnotationSet(String var1) {
        if(this.namedAnnotSets != null) {
            AnnotationSet var2 = (AnnotationSet)this.namedAnnotSets.remove(var1);
            if(var2 != null) {
                this.fireAnnotationSetRemoved(new DocumentEvent(this, 102, var1));
            }
        }

    }

    public void edit(Long var1, Long var2, DocumentContent var3) throws InvalidOffsetException {
        if(!this.isValidOffsetRange(var1, var2)) {
            throw new InvalidOffsetException("Offsets: " + var1 + "/" + var2);
        } else {
            if(this.content != null) {
                //((DocumentContentImpl)this.content).edit(var1, var2, var3);
                int var4 = var1.intValue();
                int var5 = var2.intValue();
                String text = this.content.getContent(var1,var2).toString();
                String var6 = this.content == null?"":text;
                StringBuffer var7 = new StringBuffer(text);
                var7.replace(var4, var5, var6);
                this.content = new DocumentContentImpl(var7.toString()) ;

            }

            if(this.defaultAnnots != null) {
                ((AnnotationSetImpl)this.defaultAnnots).edit(var1, var2, var3);
            }

            if(this.namedAnnotSets != null) {
                Iterator var4 = this.namedAnnotSets.values().iterator();

                while(var4.hasNext()) {
                    ((AnnotationSetImpl)var4.next()).edit(var1, var2, var3);
                }
            }

            this.fireContentEdited(new DocumentEvent(this, 103, var1, var2));
        }
    }

    public boolean isValidOffset(Long var1) {
        if(var1 == null) {
            return false;
        } else {
            long var2 = var1.longValue();
            return var2 <= this.getContent().size().longValue() && var2 >= 0L;
        }
    }

    public boolean isValidOffsetRange(Long var1, Long var2) {
        return this.isValidOffset(var1) && this.isValidOffset(var2) && var1.longValue() <= var2.longValue();
    }

    public void setNextAnnotationId(int var1) {
        this.nextAnnotationId = var1;
    }

    public Integer getNextAnnotationId() {
        return new Integer(this.nextAnnotationId++);
    }

    public Integer peakAtNextAnnotationId() {
        return Integer.valueOf(this.nextAnnotationId);
    }

    public Integer getNextNodeId() {
        return new Integer(this.nextNodeId++);
    }

    public int compareTo(Object var1) throws ClassCastException {
        MapDocument var2 = (MapDocument)var1;
        return this.getOrderingString().compareTo(var2.getOrderingString());
    }

    protected String getOrderingString() {
        if(this.sourceUrl == null) {
            return this.toString();
        } else {
            StringBuffer var1 = new StringBuffer(this.sourceUrl.toString());
            if(this.sourceUrlStartOffset != null && this.sourceUrlEndOffset != null) {
                var1.append(this.sourceUrlStartOffset.toString());
                var1.append(this.sourceUrlEndOffset.toString());
            }

            return var1.toString();
        }
    }

    public String getStringContent() {
        return this.stringContent;
    }

    @CreoleParameter(
            disjunction = "source",
            priority = 2,
            comment = "The content of the document"
    )
    public void setStringContent(String var1) {
        this.stringContent = var1;
    }

    public String toString() {
        String var1 = Strings.getNl();
        StringBuffer var2 = new StringBuffer("MapDocument: " + var1);
        var2.append("  content:" + this.content + var1);
        var2.append("  defaultAnnots:" + this.defaultAnnots + var1);
        var2.append("  encoding:" + this.encoding + var1);
        var2.append("  features:" + this.features + var1);
        var2.append("  markupAware:" + this.markupAware + var1);
        var2.append("  namedAnnotSets:" + this.namedAnnotSets + var1);
        var2.append("  nextAnnotationId:" + this.nextAnnotationId + var1);
        var2.append("  nextNodeId:" + this.nextNodeId + var1);
        var2.append("  sourceUrl:" + this.sourceUrl + var1);
        var2.append("  sourceUrlStartOffset:" + this.sourceUrlStartOffset + var1);
        var2.append("  sourceUrlEndOffset:" + this.sourceUrlEndOffset + var1);
        var2.append("  mapDocs:" + mapDocs);
        var2.append(var1);
        return var2.toString();
    }

    public synchronized void removeDocumentListener(DocumentListener var1) {
        if(this.documentListeners != null && this.documentListeners.contains(var1)) {
            Vector var2 = (Vector)this.documentListeners.clone();
            var2.removeElement(var1);
            this.documentListeners = var2;
        }

    }

    public synchronized void addDocumentListener(DocumentListener var1) {
        Vector var2 = this.documentListeners == null?new Vector(2):(Vector)this.documentListeners.clone();
        if(!var2.contains(var1)) {
            var2.addElement(var1);
            this.documentListeners = var2;
        }

    }

    protected void fireAnnotationSetAdded(DocumentEvent var1) {
        if(this.documentListeners != null) {
            Vector var2 = this.documentListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((DocumentListener)var2.elementAt(var4)).annotationSetAdded(var1);
            }
        }

    }

    protected void fireAnnotationSetRemoved(DocumentEvent var1) {
        if(this.documentListeners != null) {
            Vector var2 = this.documentListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((DocumentListener)var2.elementAt(var4)).annotationSetRemoved(var1);
            }
        }

    }

    protected void fireContentEdited(DocumentEvent var1) {
        if(this.documentListeners != null) {
            Vector var2 = this.documentListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((DocumentListener)var2.elementAt(var4)).contentEdited(var1);
            }
        }

    }

    public void resourceLoaded(CreoleEvent var1) {
    }

    public void resourceUnloaded(CreoleEvent var1) {
    }

    public void datastoreOpened(CreoleEvent var1) {
    }

    public void datastoreCreated(CreoleEvent var1) {
    }

    public void resourceRenamed(Resource var1, String var2, String var3) {
    }

    public void datastoreClosed(CreoleEvent var1) {
        if(var1.getDatastore().equals(this.getDataStore())) {
            Factory.deleteResource(this);
        }
    }

    public void setLRPersistenceId(Object var1) {
        super.setLRPersistenceId(var1);
        Gate.getCreoleRegister().addCreoleListener(this);
    }

    public void resourceAdopted(DatastoreEvent var1) {
    }

    public void resourceDeleted(DatastoreEvent var1) {
        if(var1.getSource().equals(this.getDataStore())) {
            if(var1.getResourceID().equals(this.getLRPersistenceId())) {
                Factory.deleteResource(this);
            }

        }
    }

    public void resourceWritten(DatastoreEvent var1) {
    }

    public void setDataStore(DataStore var1) throws PersistenceException {
        super.setDataStore(var1);
        if(this.dataStore != null) {
            this.dataStore.addDatastoreListener(this);
        }

    }

    public void setDefaultAnnotations(AnnotationSet var1) {
        this.defaultAnnots = var1;
    }

    class AnnotationComparator implements Comparator<Annotation> {
        int orderOn = -1;
        int orderType = 3;

        public AnnotationComparator(int var2, int var3) {
            this.orderOn = var2;
            this.orderType = var3;
        }

        public int compare(Annotation var1, Annotation var2) {
            int var3;
            if(this.orderOn == 0) {
                var3 = var1.getStartNode().getOffset().compareTo(var2.getStartNode().getOffset());
                return this.orderType == 3?(var3 == 0?var1.getId().compareTo(var2.getId()):var3):(var3 == 0?-var1.getId().compareTo(var2.getId()):-var3);
            } else if(this.orderOn == 1) {
                var3 = var1.getEndNode().getOffset().compareTo(var2.getEndNode().getOffset());
                return this.orderType == 3?(var3 == 0?-var1.getId().compareTo(var2.getId()):var3):(var3 == 0?var1.getId().compareTo(var2.getId()):-var3);
            } else {
                return this.orderOn == 2?(this.orderType == 3?var1.getId().compareTo(var2.getId()):-var1.getId().compareTo(var2.getId())):0;
            }
        }
    }*/
}
