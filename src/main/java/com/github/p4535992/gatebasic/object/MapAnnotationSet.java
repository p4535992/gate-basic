package com.github.p4535992.gatebasic.object;

import gate.*;
import gate.annotation.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
@SuppressWarnings("unused")
public class MapAnnotationSet extends AnnotationSetImpl implements AnnotationSet{
    static final long serialVersionUID = 4449426765310434166L;
    private String name;

    private List<MapAnnotation> listAnnotations;
    private MultiValueMap<String, MapAnnotation> mapAnnotationSets;
    private MultiValueMap<String, Map<String,List<String>>> mapStringAnnotationSets;

    private int index;

    public MapAnnotationSet(Document mapDocument,String nameAnnotationSet){
        super(mapDocument,nameAnnotationSet);
        this.name = super.getName();
        //new variables
        this.index = 0;
        this.listAnnotations = new ArrayList<>();
        //name = Objects.equals(nameAnnotationSet, "") ?setUpDefaultName():nameAnnotationSet;
        this.mapAnnotationSets = new LinkedMultiValueMap<>();
        this.mapStringAnnotationSets = new LinkedMultiValueMap<>();
    }

    public MapAnnotationSet(Document mapDocument){
        super(mapDocument);
        this.name = super.getName();
        //new variables
        this.index = 0;
        this.listAnnotations = new ArrayList<>();
        //name = Objects.equals(nameAnnotationSet, "") ?setUpDefaultName():nameAnnotationSet;
        this.mapAnnotationSets = new LinkedMultiValueMap<>();
        this.mapStringAnnotationSets = new LinkedMultiValueMap<>();
    }

    public MapAnnotationSet(AnnotationSet annotationSet) throws ClassCastException {
        super(annotationSet);
        this.name = super.getName();
        //new variables
        this.index = 0;
        this.listAnnotations = new ArrayList<>();
        //name = Objects.equals(nameAnnotationSet, "") ?setUpDefaultName():nameAnnotationSet;
        this.mapAnnotationSets = new LinkedMultiValueMap<>();
        this.mapStringAnnotationSets = new LinkedMultiValueMap<>();
    }

    //Suppplement constructor for old version

    public MapAnnotationSet(Document mapDocument,Annotation mapAnnotation){
        this(mapDocument,"",Collections.singletonList((MapAnnotation) mapAnnotation));
    }

    public MapAnnotationSet(Document mapDocument,List<Annotation> listAnnotations){
        this(mapDocument,"",listAnnotations);
    }

    public MapAnnotationSet(Document mapDocument,String nameAnnotationSet, MapAnnotation mapAnnotation){
        this(mapDocument,nameAnnotationSet,Collections.singletonList(mapAnnotation));
    }

    public MapAnnotationSet(Document mapDocument,String nameAnnotationSet, List<Annotation> listAnnotations){
        super(mapDocument);
        this.name = super.getName();
        //new variables
        this.index = 0;
        for(Annotation ann :listAnnotations){
            this.listAnnotations.add((MapAnnotation) ann);
        }
        this.mapAnnotationSets = setUpMapAnnotationSets();
        this.mapStringAnnotationSets = setUpMapStringAnnotationSets();
    }

    //SETTER AND GETTER

    public List<MapAnnotation> getListAnnotations() {
        return listAnnotations;
    }

    public MultiValueMap<String, MapAnnotation> getMapAnnotationSets() {
        return mapAnnotationSets;
    }

    public MultiValueMap<String, Map<String, List<String>>> getMapStringAnnotationSets() {
        return mapStringAnnotationSets;
    }

    public Map<String,Map<String,List<String>>> getMap(){
        if(mapAnnotationSets== null)return new LinkedHashMap<>();
        Map<String, Map<String,List<String>>> result = new LinkedHashMap<>(mapAnnotationSets.size());
        for (Map.Entry<String, List<MapAnnotation>> entry : mapAnnotationSets.entrySet()) {
            for(MapAnnotation mapAnnotation : entry.getValue()) {
                result.put(entry.getKey(),mapAnnotation.getMap());
            }
        }
        return result;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "AnnotationSet#"+index;
    }

    private MultiValueMap<String,MapAnnotation> setUpMapAnnotationSets(){
        MultiValueMap<String,MapAnnotation> map = new LinkedMultiValueMap<>();
        for(MapAnnotation mapAnnotation :  listAnnotations){
            map.add(name,mapAnnotation);
        }
        return map;
    }

    private MultiValueMap<String,Map<String,List<String>>> setUpMapStringAnnotationSets(){
        MultiValueMap<String,Map<String,List<String>>> map = new LinkedMultiValueMap<>();
        for(MapAnnotation mapAnnotation :  listAnnotations){
            map.add(name,mapAnnotation.getMap());
        }
        return map;
    }

    public void add(MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.add(setUpDefaultName(),mapAnnotation);
        this.mapStringAnnotationSets.add(setUpDefaultName(),mapAnnotation.getMap());
    }

    public void add(String annotationSetName,MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.add(annotationSetName,mapAnnotation);
        this.mapStringAnnotationSets.add(annotationSetName,mapAnnotation.getMap());
    }

    public void add(MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotations.addAll( mapAnnotationSet.getListAnnotations());
        addAll(mapAnnotationSet);
        addAllString(mapAnnotationSet);
    }

    public void add(List<MapAnnotationSet> mapAnnotationSet){
        for(MapAnnotationSet annSet: mapAnnotationSet){
            add(annSet);
        }
    }

    private void addAll(MapAnnotationSet mapAnnotationSet){
        for(Map.Entry<String,List<MapAnnotation>> entry: mapAnnotationSet.getMapAnnotationSets().entrySet()){
            for(MapAnnotation mapAnnotation: entry.getValue()) {
                this.mapAnnotationSets.add(entry.getKey(),mapAnnotation);
            }
        }
    }

    private void addAllString(MapAnnotationSet mapAnnotationSet){
        for(Map.Entry<String,Map<String,List<String>>> entry: mapAnnotationSet.getMap().entrySet()){
            for(Map.Entry<String,List<String>> entry2 : entry.getValue().entrySet()){
                this.mapStringAnnotationSets.add(entry.getKey(),entry.getValue());
            }
        }
    }


    public void put(MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.put(setUpDefaultName(),Collections.singletonList(mapAnnotation));
        this.mapStringAnnotationSets.put(setUpDefaultName(),Collections.singletonList(mapAnnotation.getMap()));
    }

    public void put(String annotationSetName,MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.put(annotationSetName,Collections.singletonList(mapAnnotation));
        this.mapStringAnnotationSets.put(annotationSetName,Collections.singletonList(mapAnnotation.getMap()));
    }

    private void setIfNull(){
        if(listAnnotations == null || listAnnotations.isEmpty()){
            listAnnotations = new ArrayList<>();
        }
        if(mapAnnotationSets == null || mapAnnotationSets.isEmpty()){
            mapAnnotationSets = new LinkedMultiValueMap<>();
        }
        if(mapStringAnnotationSets == null || mapStringAnnotationSets.isEmpty()){
            mapStringAnnotationSets = new LinkedMultiValueMap<>();
        }
    }

    public void clear(){
        super.clear();
        listAnnotations.clear();
        mapAnnotationSets.clear();
        mapStringAnnotationSets.clear();
    }

    //===============================================================================================================


    public boolean isEmpty(){
        return mapAnnotationSets.isEmpty();
        //return values().size();
    }

    public int size(){
        return mapAnnotationSets.size();
    }

    public List<MapAnnotation> values(){
        Collection<List<MapAnnotation>> coll = mapAnnotationSets.values();
        List<MapAnnotation> list = new ArrayList<>();
        for(List<MapAnnotation> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapAnnotation>>> entrySet(){
        return mapAnnotationSets.entrySet();
    }

    public List<MapAnnotation> getList(String annotationSetName){
        for(Map.Entry<String,List<MapAnnotation>> entry: mapAnnotationSets.entrySet()){
            if(entry.getKey().toLowerCase().contains(annotationSetName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<MapAnnotation> getList(Integer indexAnnotationSet){
        if(!mapAnnotationSets.isEmpty()) {
            return new ArrayList<>(mapAnnotationSets.values()).get(indexAnnotationSet);
        }else{
            return new ArrayList<>();
        }
    }

    public MapAnnotation find(String nameAnnotationSet,String nameAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(MapAnnotation ann : getList(nameAnnotationSet)){
            for(Map.Entry<String,List<String>> entry : ann.entrySet()){
                if(entry.getKey().equals(nameAnnotation)){
                    theMapAnnotation = ann;
                    break;
                }
            }
            if(theMapAnnotation!=null)break;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(String nameAnnotationSet,Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        int i = 0;
        for(MapAnnotation ann : getList(nameAnnotationSet)){
            if(i == indexAnnotation){
                theMapAnnotation = ann;
                break;
            }
            i++;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(Integer indexAnnotationSet,Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        int i = 0;
        for(MapAnnotation ann : getList(indexAnnotationSet)){
            if(i == indexAnnotation){
                theMapAnnotation = ann;
                break;
            }
            i++;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(Map.Entry<String,List<MapAnnotation>> mapAnnSet : mapAnnotationSets.entrySet()){
            int i = 0;
            for(MapAnnotation ann: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<String>> entry : ann.entrySet()) {
                    if (i == indexAnnotation) {
                        theMapAnnotation = ann;
                        break;
                    }
                    i++;
                }
                if (theMapAnnotation != null)break;
            }
            if (theMapAnnotation != null)break;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(String nameAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(Map.Entry<String,List<MapAnnotation>> mapAnnSet : mapAnnotationSets.entrySet()){
            for(MapAnnotation ann: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<String>> entry : ann.entrySet()) {
                    if (entry.getKey().equals(nameAnnotation)) {
                        theMapAnnotation = ann;
                        break;
                    }
                }
                if (theMapAnnotation != null)break;
            }
            if (theMapAnnotation != null)break;
        }
        return theMapAnnotation;
    }

    public String getName(Integer indexAnnotationSet) {
        int i = 0;
        for (Map.Entry<String, List<MapAnnotation>> entry : mapAnnotationSets.entrySet()) {
            if (i == indexAnnotationSet) {
                return entry.getKey();
            }
            i++;
        }
        return null;
    }

    public String getName(String nameAnnotationSet) {
        for (Map.Entry<String, List<MapAnnotation>> entry : mapAnnotationSets.entrySet()) {
            if (Objects.equals(entry.getKey(), nameAnnotationSet)) {
                return entry.getKey();
            }
        }
        return null;
    }

   /* public String getName() {
        return name;
    }*/

    @Override
    public String toString() {
        return "MapAnnotationSet{" +
                "name='" + name + '\'' +
                ", mapAnnotationSets=" + mapAnnotationSets +
                ", index=" + index +
                '}';
    }

    //=================================================================================================================
   /* public Iterator<Annotation> iterator() {
        return new MapAnnotationSet.AnnotationSetIterator();
    }

    public boolean remove(Object var1) throws ClassCastException {
        Annotation var2 = (Annotation)var1;
        boolean var3 = this.removeFromIdIndex(var2);
        if(var3) {
            this.removeFromTypeIndex(var2);
            this.removeFromOffsetIndex(var2);
        }

        this.fireAnnotationRemoved(new AnnotationSetEvent(this, 202, this.getDocument(), var2));
        return var3;
    }

    protected boolean removeFromIdIndex(Annotation var1) {
        return this.annotsById.remove(var1.getId()) != null;
    }

    protected void removeFromTypeIndex(Annotation var1) {
        if(this.annotsByType != null) {
            AnnotationSet var2 = (AnnotationSet)this.annotsByType.get(var1.getType());
            if(var2 != null) {
                var2.remove(var1);
            }

            if(var2 != null && var2.isEmpty()) {
                this.annotsByType.remove(var1.getType());
            }
        }

    }

    protected void removeFromOffsetIndex(Annotation var1) {
        if(this.nodesByOffset != null) {
            ;
        }

        if(this.annotsByStartNode != null) {
            Integer var2 = var1.getStartNode().getId();
            Object var3 = this.annotsByStartNode.get(var2);
            if(var3 instanceof Annotation) {
                this.annotsByStartNode.remove(var2);
                return;
            }

            Collection var4 = (Collection)var3;
            var4.remove(var1);
            if(var4.size() == 1) {
                this.annotsByStartNode.put(var2, var4.iterator().next());
            }
        }

    }

    public int size() {
        return this.annotsById.size();
    }

    public Annotation get(Integer var1) {
        return (Annotation)this.annotsById.get(var1);
    }

    public AnnotationSet get() {
        return (AnnotationSet)(this.annotsById.isEmpty()?this.emptyAS():new ImmutableAnnotationSetImpl(this.doc, this.annotsById.values()));
    }

    public AnnotationSet get(String var1) {
        if(this.annotsByType == null) {
            this.indexByType();
        }

        AnnotationSet var2 = (AnnotationSet)this.annotsByType.get(var1);
        return var2 == null?this.emptyAS():var2.get();
    }

    public AnnotationSet get(Set<String> var1) throws ClassCastException {
        if(this.annotsByType == null) {
            this.indexByType();
        }

        Iterator var2 = var1.iterator();
        ArrayList<Annotation> var3 = new ArrayList<>();

        while(true) {
            AnnotationSet var5;
            do {
                if(!var2.hasNext()) {
                    if(var3.isEmpty()) {
                        return this.emptyAS();
                    }

                    return new ImmutableAnnotationSetImpl(this.doc, var3);
                }

                String var4 = (String)var2.next();
                var5 = (AnnotationSet)this.annotsByType.get(var4);
            } while(var5 == null);

            for (Annotation aVar5 : var5) {
                var3.add(aVar5);
            }
        }
    }

    public AnnotationSet get(String var1, FeatureMap var2) {
        if(this.annotsByType == null) {
            this.indexByType();
        }

        AnnotationSet var3 = this.get(var1);
        if(var3 == null) {
            return null;
        } else {
            Iterator var4 = var3.iterator();
            ArrayList<Annotation> var5 = new ArrayList<>();

            while(var4.hasNext()) {
                Annotation var6 = (Annotation)var4.next();
                if(var6.getFeatures().subsumes(var2)) {
                    var5.add(var6);
                }
            }

            if(var5.isEmpty()) {
                return this.emptyAS();
            } else {
                return new ImmutableAnnotationSetImpl(this.doc, var5);
            }
        }
    }

    public AnnotationSet get(String var1, Set<?> var2) {
        if(this.annotsByType == null) {
            this.indexByType();
        }

        AnnotationSet var3 = null;
        if(var1 != null) {
            var3 = this.get(var1);
            if(var3 == null) {
                return null;
            }
        }

        ArrayList<Annotation> var4 = new ArrayList<>();
        Iterator var5 = null;
        if(var1 != null) {
            var5 = var3.iterator();
        } else {
            var5 = this.annotsById.values().iterator();
        }

        while(var5.hasNext()) {
            Annotation var6 = (Annotation)var5.next();
            if(var6.getFeatures().keySet().containsAll(var2)) {
                var4.add(var6);
            }
        }

        if(var4.isEmpty()) {
            return this.emptyAS();
        } else {
            return new ImmutableAnnotationSetImpl(this.doc, var4);
        }
    }

    public AnnotationSet get(Long var1) {
        if(this.annotsByStartNode == null) {
            this.indexByStartOffset();
        }

        Node var2 = (Node)this.nodesByOffset.getNextOf(var1);
        if(var2 == null) {
            return this.emptyAS();
        } else {
            Collection<Annotation> var3;
            for(var3 = this.getAnnotsByStartNode(var2.getId()); var3 == null; var3 = this.getAnnotsByStartNode(var2.getId())) {
                var2 = (Node)this.nodesByOffset.getNextOf(var2.getOffset() + 1L);
                if(var2 == null) {
                    return this.emptyAS();
                }
            }

            return new ImmutableAnnotationSetImpl(this.doc, var3);
        }
    }

    public AnnotationSet getStartingAt(long var1) {
        if(this.annotsByStartNode == null) {
            this.indexByStartOffset();
        }

        Node var3 = (Node)this.nodesByOffset.get(var1);
        return (AnnotationSet)(var3 == null?this.emptyAS():new ImmutableAnnotationSetImpl(this.doc, this.getAnnotsByStartNode(var3.getId())));
    }

    public List<Annotation> inDocumentOrder() {
        if(this.annotsByStartNode == null) {
            this.indexByStartOffset();
        }

        Collection<Node> var1 = this.nodesByOffset.values();
        ArrayList<Annotation> var2 = new ArrayList<>();

        for (Node var4 : var1) {
            Collection<Annotation> var5 = this.getAnnotsByStartNode(var4.getId());
            if (var5 != null) {
                var2.addAll(var5);
            }
        }
        return var2;
    }

    public AnnotationSet get(Long var1, Long var2) {
        return this.get((String)null, (Long)var1, var2);
    }

    public AnnotationSet getStrict(Long var1, Long var2) {
        if(this.annotsByStartNode == null) {
            this.indexByStartOffset();
        }

        ArrayList<Annotation> var3 = null;
        Node var5 = (Node)this.nodesByOffset.get(var1);
        if(var5 != null) {
            Collection<Annotation> var7 = this.getAnnotsByStartNode(var5.getId());
            if(var7 != null) {
                for (Annotation var6 : var7) {
                    if (var6.getEndNode().getOffset().compareTo(var2) == 0) {
                        if (var3 == null) {
                            var3 = new ArrayList<>();
                        }
                        var3.add(var6);
                    }
                }
            }
        }

        return new ImmutableAnnotationSetImpl(this.doc, var3);
    }

    public AnnotationSet get(String var1, Long var2, Long var3) {
        if(this.annotsByStartNode == null) {
            this.indexByStartOffset();
        }

        ArrayList<Annotation> var4 = new ArrayList<>();
        boolean var9 = StringUtils.isNotBlank(var1);
        Long var10 = var2 - this.longestAnnot;
        if(var10 < 0L) {
            var10 = 0L;
        }

        Iterator var5 = this.nodesByOffset.subMap(var10, var2).values().iterator();

        label72:
        while(true) {
            Iterator<Annotation> var6;
            Annotation var8;
            Collection<Annotation> var11;
            do {
                Node var7;
                if(!var5.hasNext()) {
                    var5 = this.nodesByOffset.subMap(var2, var3).values().iterator();
                    while(true) {
                        while(true) {
                            do {
                                if(!var5.hasNext()) {
                                    return new ImmutableAnnotationSetImpl(this.doc, var4);
                                }
                                var7 = (Node)var5.next();
                                var11 = this.getAnnotsByStartNode(var7.getId());
                            } while(var11 == null);

                            if(!var9) {
                                var4.addAll(var11);
                            } else {
                                var6 = var11.iterator();

                                while(var6.hasNext()) {
                                    var8 = (Annotation)var6.next();
                                    if(var8.getType().equals(var1)) {
                                        var4.add(var8);
                                    }
                                }
                            }
                        }
                    }
                }

                var7 = (Node)var5.next();
                var11 = this.getAnnotsByStartNode(var7.getId());
            } while(var11 == null);

            var6 = var11.iterator();

            while(true) {
                do {
                    if(!var6.hasNext()) {
                        continue label72;
                    }

                    var8 = (Annotation)var6.next();
                } while(var9 && !var8.getType().equals(var1));

                if(var8.getEndNode().getOffset().compareTo(var2) > 0) {
                    var4.add(var8);
                }
            }
        }
    }

    public AnnotationSet getCovering(String var1, Long var2, Long var3) {
        if(var3 < var2) {
            return this.emptyAS();
        } else {
            if(this.annotsByStartNode == null) {
                this.indexByStartOffset();
            }

            if(var3 - var2 > this.longestAnnot) {
                return this.emptyAS();
            } else {
                ArrayList<Annotation> var4 = new ArrayList<>();
                boolean var9 = StringUtils.isNotBlank(var1);
                Long var10 = var3 - 1L - this.longestAnnot;
                if(var10 < 0L) {
                    var10 = 0L;
                }

                Iterator var5 = this.nodesByOffset.subMap(var10, var2 + 1L).values().iterator();

                label51:
                while(true) {
                    Collection var11;
                    do {
                        if(!var5.hasNext()) {
                            return new ImmutableAnnotationSetImpl(this.doc, var4);
                        }

                        Node var7 = (Node)var5.next();
                        var11 = this.getAnnotsByStartNode(var7.getId());
                    } while(var11 == null);

                    Iterator var6 = var11.iterator();

                    while(true) {
                        Annotation var8;
                        do {
                            if(!var6.hasNext()) {
                                continue label51;
                            }

                            var8 = (Annotation)var6.next();
                        } while(var9 && !var8.getType().equals(var1));

                        if(var8.getEndNode().getOffset().compareTo(var3) >= 0) {
                            var4.add(var8);
                        }
                    }
                }
            }
        }
    }

    public AnnotationSet get(String var1, FeatureMap var2, Long var3) {
        AnnotationSet var4 = this.get(var3);
        return var4 == null?this.emptyAS():var4.get(var1, var2);
    }

    public AnnotationSet getContained(Long var1, Long var2) {
        if(var2 < var1) {
            return this.emptyAS();
        } else {
            if(this.annotsByStartNode == null) {
                this.indexByStartOffset();
            }

            ArrayList<Annotation> var3 = null;
            Iterator<Node> var4 = this.nodesByOffset.subMap(var1, var2).values().iterator();

            while(true) {
                Collection<Annotation> var7;
                do {
                    if(!var4.hasNext()) {
                        return new ImmutableAnnotationSetImpl(this.doc, var3);
                    }

                    Node var5 = (Node)var4.next();
                    var7 = this.getAnnotsByStartNode(var5.getId());
                } while(var7 == null);

                for (Annotation var8 : var7) {
                    if (var8.getEndNode().getOffset().compareTo(var2) <= 0) {
                        if (var3 == null) {
                            var3 = new ArrayList<>();
                        }

                        var3.add(var8);
                    }
                }
            }
        }
    }

    public Node firstNode() {
        this.indexByStartOffset();
        return this.nodesByOffset.isEmpty()?null:(Node)this.nodesByOffset.get(this.nodesByOffset.firstKey());
    }

    public Node lastNode() {
        this.indexByStartOffset();
        return this.nodesByOffset.isEmpty()?null:(Node)this.nodesByOffset.get(this.nodesByOffset.lastKey());
    }

    public Node nextNode(Node var1) {
        this.indexByStartOffset();
        return (Node)this.nodesByOffset.getNextOf(var1.getOffset() + 1L);
    }

    public static void setAnnotationFactory(AnnotationFactory var0) {
        annFactory = var0;
    }

    public Integer add(Node var1, Node var2, String var3, FeatureMap var4) {
        Integer var5 = this.doc.getNextAnnotationId();
        annFactory.createAnnotationInSet(this, var5, var1, var2, var3, var4);
        return var5;
    }

    public boolean add(Annotation var1) throws ClassCastException {
        Object var2 = this.annotsById.put(var1.getId(), var1);
        if(this.annotsByType != null) {
            this.addToTypeIndex(var1);
        }

        if(this.annotsByStartNode != null) {
            this.addToStartOffsetIndex(var1);
        }

        AnnotationSetEvent var3 = new AnnotationSetEvent(this, 201, this.doc, var1);
        this.fireAnnotationAdded(var3);
        this.fireGateEvent(var3);
        return var2 != var1;
    }

    public boolean addAll(Collection<? extends Annotation> var1) {
        Iterator var2 = var1.iterator();
        boolean var3 = false;

        while(var2.hasNext()) {
            Annotation var4 = (Annotation)var2.next();

            try {
                this.add(var4.getStartNode().getOffset(), var4.getEndNode().getOffset(), var4.getType(), var4.getFeatures());
                var3 = true;
            } catch (InvalidOffsetException var6) {
                throw new IllegalArgumentException(var6.toString());
            }
        }

        return var3;
    }

    protected boolean addAllKeepIDs(Collection<? extends Annotation> var1) {
        Iterator var2 = var1.iterator();

        boolean var3;
        Annotation var4;
        for(var3 = false; var2.hasNext(); var3 |= this.add(var4)) {
            var4 = (Annotation)var2.next();
        }

        return var3;
    }

    private Node[] getNodes(Long var1, Long var2) throws InvalidOffsetException {
        if(!this.doc.isValidOffsetRange(var1, var2)) {
            throw new InvalidOffsetException("Offsets [" + var1 + ":" + var2 + "] not valid for this document of size " + this.doc.getContent().size());
        } else {
            if(this.nodesByOffset == null) {
                this.indexByStartOffset();
            }

            Object var3 = (Node)this.nodesByOffset.get(var1);
            if(var3 == null) {
                var3 = new NodeImpl(this.doc.getNextNodeId(), var1);
            }

            Object var4 = null;
            if(var1.equals(var2)) {
                return new Node[]{(Node)var3, (Node)var3};
            } else {
                var4 = (Node)this.nodesByOffset.get(var2);
                if(var4 == null) {
                    var4 = new NodeImpl(this.doc.getNextNodeId(), var2);
                }

                return new Node[]{(Node)var3, (Node)var4};
            }
        }
    }

    public Integer add(Long var1, Long var2, String var3, FeatureMap var4) throws InvalidOffsetException {
        Node[] var5 = this.getNodes(var1, var2);
        return this.add(var5[0], var5[1], var3, var4);
    }

    public void add(Integer var1, Long var2, Long var3, String var4, FeatureMap var5) throws InvalidOffsetException {
        Node[] var6 = this.getNodes(var2, var3);
        annFactory.createAnnotationInSet(this, var1, var6[0], var6[1], var4, var5);
        if(var1 >= this.doc.peakAtNextAnnotationId()) {
            this.doc.setNextAnnotationId(var1 + 1);
        }

    }

    protected void indexByType() {
        if(this.annotsByType == null) {
            this.annotsByType = new HashMap<>(4);

            for (Annotation o : this.annotsById.values()) {
                this.addToTypeIndex(o);
            }

        }
    }

    protected void indexByStartOffset() {
        if(this.annotsByStartNode == null) {
            if(this.nodesByOffset == null) {
                this.nodesByOffset = new RBTreeMap<>();
            }

            this.annotsByStartNode = new HashMap<>(this.annotsById.size());

            for (Annotation o : this.annotsById.values()) {
                this.addToStartOffsetIndex(o);
            }

        }
    }

    void addToTypeIndex(Annotation var1) {
        if(this.annotsByType != null) {
            String var2 = var1.getType();
            AnnotationSet var3 = this.annotsByType.get(var2);
            if(var3 == null) {
                var3 = new MapAnnotationSet(this.doc);
                this.annotsByType.put(var2, var3);
            }

            ((AnnotationSet)var3).add(var1);
        }
    }

    void addToStartOffsetIndex(Annotation var1) {
        Node var2 = var1.getStartNode();
        Node var3 = var1.getEndNode();
        Long var4 = var2.getOffset();
        Long var5 = var3.getOffset();
        if(this.nodesByOffset != null) {
            this.nodesByOffset.put(var4, var2);
            this.nodesByOffset.put(var5, var3);
        }

        long var6 = var5 - var4;
        if(var6 > this.longestAnnot) {
            this.longestAnnot = var6;
        }

        if(this.annotsByStartNode != null) {
            Object var8 = this.annotsByStartNode.get(var2.getId());
            if(var8 == null) {
                this.annotsByStartNode.put(var2.getId(), var1);
            } else {
                Set<Annotation> var9 = null;
                if(var8 instanceof Annotation) {
                    if(var8.equals(var1)) {
                        return;
                    }

                    var9 = new HashSet<>(3);
                    var9.add((Annotation)var8);
                    this.annotsByStartNode.put(var2.getId(), var9);
                } else {
                    var9 = (Set)var8;
                }
                var9.add(var1);
            }

        }
    }

    public void edit(Long var1, Long var2, DocumentContent var3) {
        this.indexByStartOffset();
        Iterator var12;
        long var14;
        if(var2.compareTo(var1) > 0) {
            ArrayList var4 = new ArrayList<>(this.nodesByOffset.subMap(var1, var2 + 1L).values());
            NodeImpl var5 = null;
            if(!var4.isEmpty()) {
                var5 = (NodeImpl)var4.get(0);
                ArrayList<Annotation> var6 = new ArrayList<>();
                ArrayList<Annotation> var7 = new ArrayList<>();
                ArrayList<Node> var8 = new ArrayList<>(this.nodesByOffset.subMap(0L, var2 + 1L).values());
                Iterator var9 = var8.iterator();

                label122:
                while(true) {
                    Collection var11;
                    do {
                        if(!var9.hasNext()) {
                            for(int var22 = 1; var22 < var4.size(); ++var22) {
                                Node var24 = (Node)var4.get(var22);
                                Collection<Annotation> var26 = this.getAnnotsByStartNode(var24.getId());
                                if(var26 != null) {
                                    var6.addAll(var26);
                                }
                            }

                            Iterator var23 = var6.iterator();

                            MapAnnotation var25;
                            while(var23.hasNext()) {
                                var25 = (MapAnnotation)var23.next();
                                var25.start = var5;
                                if(var25.start == var25.end) {
                                    this.remove(var25);
                                } else {
                                    this.addToStartOffsetIndex(var25);
                                }
                            }

                            var23 = var7.iterator();

                            while(var23.hasNext()) {
                                var25 = (MapAnnotation)var23.next();
                                var25.end = var5;
                                if(var25.start == var25.end) {
                                    this.remove(var25);
                                }
                            }

                            for(int var27 = 1; var27 < var4.size(); ++var27) {
                                Node var28 = (Node)var4.get(var27);
                                this.nodesByOffset.remove(var28.getOffset());
                                this.annotsByStartNode.remove(var28.getId());
                            }

                            this.nodesByOffset.remove(var5.getOffset());
                            var5 = new NodeImpl(((NodeImpl) var4.get(0)).getId(),var1);
                            this.nodesByOffset.put(var5.getOffset(), var5);
                            break label122;
                        }

                        Node var10 = (Node)var9.next();
                        var11 = this.getAnnotsByStartNode(var10.getId());
                    } while(var11 == null);

                    var12 = var11.iterator();

                    while(var12.hasNext()) {
                        Annotation var13 = (Annotation)var12.next();
                        var14 = var13.getEndNode().getOffset();
                        if(var14 >= var1 && var14 <= var2) {
                            var7.add(var13);
                        }
                    }
                }
            }
        }

        boolean var18 = Gate.getUserConfig().getBoolean("docedit_insert_prepend");
        long var19 = var1;
        long var20 = var2;
        long var21 = var3 == null?0L:var3.size();
        ArrayList<Node> var29 = new ArrayList<>(this.nodesByOffset.tailMap(var1).values());
        var12 = var29.iterator();

        NodeImpl var30 = null;
        while(var12.hasNext()) {
            var30 = (NodeImpl)var12.next();
            this.nodesByOffset.remove(var30.getOffset());
        }

        long var16;

        for(var12 = var29.iterator(); var12.hasNext(); var30 = new NodeImpl(var30.getId(),var16)) {
            var30 = (NodeImpl)var12.next();
            var14 = var30.getOffset();
            var16 = var14 - (var20 - var19) + var21;
            if(var14 == var19) {
                if(var16 < var19) {
                    var16 = var19;
                }

                if(var18) {
                    var16 = var19;
                }
            }
        }

        var12 = var29.iterator();

        while(var12.hasNext()) {
            var30 = (NodeImpl)var12.next();
            this.nodesByOffset.put(var30.getOffset(), var30);
        }

    }

    public String getName() {
        return this.name;
    }

    public Document getDocument() {
        return this.doc;
    }

    public Set<String> getAllTypes() {
        this.indexByType();
        return Collections.unmodifiableSet(this.annotsByType.keySet());
    }

    private Collection<Annotation> getAnnotsByStartNode(Integer var1) {
        Object var2 = this.annotsByStartNode.get(var1);
        if(var2 == null) {
            return null;
        } else if(var2 instanceof Annotation) {
            ArrayList<Annotation> var3 = new ArrayList<>(2);
            var3.add((Annotation)var2);
            return var3;
        } else {
            return (Collection)var2;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public synchronized void removeAnnotationSetListener(AnnotationSetListener var1) {
        if(this.annotationSetListeners != null && this.annotationSetListeners.contains(var1)) {
            Vector<AnnotationSetListener> var2 = (Vector)this.annotationSetListeners.clone();
            var2.removeElement(var1);
            this.annotationSetListeners = var2;
        }

    }

    public synchronized void addAnnotationSetListener(AnnotationSetListener var1) {
        Vector<AnnotationSetListener> var2 = this.annotationSetListeners == null?new Vector<>(2):(Vector)this.annotationSetListeners.clone();
        if(!var2.contains(var1)) {
            var2.addElement(var1);
            this.annotationSetListeners = var2;
        }

    }

    protected void fireAnnotationAdded(AnnotationSetEvent var1) {
        if(this.annotationSetListeners != null) {
            Vector var2 = this.annotationSetListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((AnnotationSetListener)var2.elementAt(var4)).annotationAdded(var1);
            }
        }

    }

    protected void fireAnnotationRemoved(AnnotationSetEvent var1) {
        if(this.annotationSetListeners != null) {
            Vector var2 = this.annotationSetListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((AnnotationSetListener)var2.elementAt(var4)).annotationRemoved(var1);
            }
        }

    }

    public synchronized void removeGateListener(GateListener var1) {
        if(this.gateListeners != null && this.gateListeners.contains(var1)) {
            Vector<GateListener> var2 = (Vector)this.gateListeners.clone();
            var2.removeElement(var1);
            this.gateListeners = var2;
        }

    }

    public synchronized void addGateListener(GateListener var1) {
        Vector<GateListener> var2 = this.gateListeners == null?new Vector<>(2):(Vector)this.gateListeners.clone();
        if(!var2.contains(var1)) {
            var2.addElement(var1);
            this.gateListeners = var2;
        }

    }

    protected void fireGateEvent(GateEvent var1) {
        if(this.gateListeners != null) {
            Vector var2 = this.gateListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((GateListener)var2.elementAt(var4)).processGateEvent(var1);
            }
        }

    }

    private void writeObject(ObjectOutputStream var1) throws IOException {
        ObjectOutputStream.PutField var2 = var1.putFields();
        var2.put("name", this.name);
        var2.put("doc", this.doc);
        this.annotations = new Annotation[this.annotsById.size()];
        this.annotations = (Annotation[])this.annotsById.values().toArray(this.annotations);
        var2.put("annotations", this.annotations);
        var2.put("relations", this.relations);
        var1.writeFields();
        this.annotations = null;
        boolean var3 = this.annotsByType != null;
        boolean var4 = this.annotsByStartNode != null;
        var1.writeBoolean(var3);
        var1.writeBoolean(var4);
    }

    private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
        this.longestAnnot = 0L;
        ObjectInputStream.GetField var2 = var1.readFields();
        this.name = (String)var2.get("name", (Object)null);
        this.doc = (MapDocument) var2.get("doc", (Object)null);
        boolean var3 = false;
        boolean var4 = false;
        this.annotations = (Annotation[])((Annotation[])var2.get("annotations", (Object)null));
        if(this.annotations == null) {
            Map var5 = (Map)var2.get("annotsById", (Object)null);
            if(var5 == null) {
                throw new IOException("Invalid serialised data: neither annotations array or map by id are present.");
            }

            this.annotations = (Annotation[])var5.values().toArray(new Annotation[0]);
        } else {
            var3 = var1.readBoolean();
            var4 = var1.readBoolean();
        }

        this.annotsById = new HashMap<>(this.annotations.length);
        if(var3) {
            this.annotsByType = new HashMap<>(4);
        }

        if(var4) {
            this.nodesByOffset = new RBTreeMap<>();
            this.annotsByStartNode = new HashMap<>(this.annotations.length);
        }

        Collections.addAll(this, this.annotations);

        this.relations = (RelationSet)var2.get("relations", null);
        this.annotations = null;
    }

    public RelationSet getRelations() {
        if(this.relations == null) {
            this.relations = new RelationSet(this);
        }

        return this.relations;
    }

    protected AnnotationSet emptyAS() {
        return new ImmutableAnnotationSetImpl(this.doc, null);
    }

    static {
        setAnnotationFactory(new DefaultAnnotationFactory());
    }

    class AnnotationSetIterator implements Iterator<Annotation> {
        private Iterator<Annotation> iter;
        protected Annotation lastNext = null;

        AnnotationSetIterator() {
            this.iter = MapAnnotationSet.this.annotsById.values().iterator();
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public Annotation next() {
            return this.lastNext = (Annotation)this.iter.next();
        }

        public void remove() {
            this.iter.remove();
            if(this.lastNext != null) {
                MapAnnotationSet.this.removeFromTypeIndex(this.lastNext);
                MapAnnotationSet.this.removeFromOffsetIndex(this.lastNext);
                MapAnnotationSet.this.fireAnnotationRemoved(new AnnotationSetEvent(MapAnnotationSet.this, 202, MapAnnotationSet.this.getDocument(), this.lastNext));
            }
        }
    }*/
}
