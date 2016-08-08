package com.github.p4535992.gatebasic.object;

import gate.*;
import gate.corpora.CorpusImpl;
import java.util.Comparator;
import gate.corpora.DocumentImpl;
import gate.creole.AbstractLanguageResource;
import gate.creole.CustomDuplication;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.*;
import gate.event.*;
import gate.util.BomStrippingInputStreamReader;
import gate.util.Err;
import gate.util.Files;
import gate.util.Strings;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
@CreoleResource(
        name = "GATE Corpus",
        comment = "GATE transient corpus.",
        interfaceName = "gate.Corpus",
        icon = "corpus-trans",
        helpURL = "http://gate.ac.uk/userguide/sec:developer:loadlr"
)
public class MapCorpus extends CorpusImpl implements Corpus{
    static final long serialVersionUID = -4443142759053898456L;
    protected List<Document> listDocument;

    private MultiValueMap<String,Document> mapCorpus;
    private String docName;
    private int index;

    public MapCorpus(){
        super();
        //new variables
        this.index = 0;
        //this.listDocument = new ArrayList<>();
        this.docName = setUpDefaultName();
        this.mapCorpus = new LinkedMultiValueMap<>();
        //this.mapStringDocs = new LinkedMultiValueMap<>();
    }

    public MapCorpus(Document mapDocument){
        this("",Collections.singletonList(mapDocument));
    }

    public MapCorpus(List<Document> listDocument){
        this("",listDocument);
    }

    public MapCorpus(String nameCorpus, Document mapDocument){
        this(nameCorpus,Collections.singletonList(mapDocument));
    }

    public MapCorpus(String nameCorpus, List<Document> listDocument){
        super();
        this.index = 0;
        this.listDocument = new ArrayList<>();
        for(Document doc : listDocument){
            this.listDocument.add((MapDocument)doc);
        }
        this.docName = Objects.equals(nameCorpus, "") ?setUpDefaultName():nameCorpus;
        this.mapCorpus = setUpMapCorpus();
        //this.mapStringDocs = setUpMapStringDocs();
    }

    public MapCorpus(MultiValueMap<String,Document> mapCorpus){
        this.mapCorpus = mapCorpus;
    }

    //Getter and setter

    public MultiValueMap<String,Document> getMapCorpus() {
        return mapCorpus;
    }

    public void setMapCorpus(MultiValueMap<String,Document> mapCorpus) {
        this.mapCorpus = mapCorpus;
    }

    public List<Document> getListDocument() {
        return listDocument;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "Corpus#"+index;
    }

    private MultiValueMap<String,Document> setUpMapCorpus(){
        MultiValueMap<String,Document> map = new LinkedMultiValueMap<>();
        for(Document mapDocument : listDocument){
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

    public boolean add(Document mapDocument){
        setIfNull();
        this.mapCorpus.add(setUpDefaultName(),mapDocument);
        return this.listDocument.add(mapDocument);
        //re-set values
        //this.mapStringDocs.add(setUpDefaultName(),mapDocument.getMap());
    }

    public void add(String annotationSetName,Document mapDocument){
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
        for(Map.Entry<String,List<Document>> entry: mapCorpus.getMapCorpus().entrySet()){
            for(Document mapDocument: entry.getValue()) {
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

    public void put(Document mapDocument){
        setIfNull();
        this.listDocument.add(mapDocument);
        //re-set values
        this.mapCorpus.put(setUpDefaultName(),Collections.singletonList(mapDocument));
        //this.mapStringDocs.put(setUpDefaultName(),Collections.singletonList(mapDocument.getMap()));
    }

    public void put(String annotationSetName,Document mapDocument){
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
        //return this.listDocument.size();
    }

    public boolean isEmpty(){
        return mapCorpus.isEmpty();
        //return values().size();
        //return this.listDocument.isEmpty();
    }

    public List<Document> values(){
        Collection<List<Document>> coll = mapCorpus.values();
        List<Document> list = new ArrayList<>();
        for(List<Document> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<Document>>> entrySet(){
        return mapCorpus.entrySet();
    }

    public List<Document> getList(String documentName){
        for(Map.Entry<String,List<Document>> entry: mapCorpus.entrySet()){
            if(entry.getKey().toLowerCase().contains(documentName.toLowerCase())){
                return entry.getValue();
            }
        }
        return null;
    }

    public List<Document> getList(Integer indexDocument){
        //List<List<MapDocument>> l = new ArrayList<>(mapCorpus.values());
        return new ArrayList<>(mapCorpus.values()).get(indexDocument);
    }

    public boolean hasValue(String key){
        List<Document> value = mapCorpus.get(key);
        return value != null && !value.isEmpty();
    }

    public boolean hasKey(Document value){
        for (Map.Entry<String,List<Document>> entry : mapCorpus.entrySet()) {
            for(Document annSet: entry.getValue()) {
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
    
    //==================================================================================================================
   /* public List<String> getDocumentNames() {
        ArrayList<String> var1 = new ArrayList<>(this.listDocument.size());

        for (Document var3 : this.listDocument) {
            var1.add((var3).getName());
        }

        return var1;
    }

    public String getDocumentName(int var1) {
        return ((Document)this.listDocument.get(var1)).getName();
    }

    public void unloadDocument(Document var1) {
    }

    public boolean isDocumentLoaded(int var1) {
        return true;
    }

    protected void clearDocList() {
        if(this.listDocument != null) {
            this.listDocument.clear();
        }
    }

    public boolean contains(Object var1) {
        return this.listDocument.contains(var1);
    }

    public Iterator<Document> iterator() {
        return this.listDocument.iterator();
    }

    @Override
    public void forEach(Consumer<? super Document> consumer) {

    }

    public Object[] toArray() {
        return this.listDocument.toArray();
    }

    public <T> T[] toArray(T[] var1) {
        return this.listDocument.toArray(var1);
    }

    @Override
    public boolean add(Document var1) {
        return this.listDocument.add(var1);
    }

    public boolean remove(Object var1) {
        return this.listDocument.remove(var1);
    }

    public boolean containsAll(Collection<?> var1) {
        return this.listDocument.containsAll(var1);
    }

    public boolean addAll(Collection<? extends Document> var1) {
        return this.listDocument.addAll(var1);
    }

    public boolean addAll(int var1, Collection<? extends Document> var2) {
        return this.listDocument.addAll(var1, var2);
    }

    public boolean removeAll(Collection<?> var1) {
        return this.listDocument.removeAll(var1);
    }

    @Override
    public boolean removeIf(Predicate<? super Document> predicate) {
        return false;
    }

    public boolean retainAll(Collection<?> var1) {
        return this.listDocument.retainAll(var1);
    }

    @Override
    public void replaceAll(UnaryOperator<Document> unaryOperator) {

    }

    @Override
    public void sort(Comparator<? super Document> comparator) {

    }

    *//*public void clear() {
        this.listDocument.clear();
    }*//*

    public boolean equals(Object var1) {
        return var1 instanceof MapCorpus && this.listDocument.equals(var1);
    }

    public int hashCode() {
        return this.listDocument.hashCode();
    }

    public Document get(int var1) {
        return (Document)this.listDocument.get(var1);
    }

    public Document set(int var1, Document var2) {
        return (Document)this.listDocument.set(var1, var2);
    }

    public void add(int var1, Document var2) {
        this.listDocument.add(var1, var2);
    }

    public Document remove(int var1) {
        return (Document)this.listDocument.remove(var1);
    }

    public int indexOf(Object var1) {
        return this.listDocument.indexOf(var1);
    }

    public int lastIndexOf(Object var1) {
        return this.listDocument.lastIndexOf(var1);
    }

    public ListIterator<Document> listIterator() {
        return this.listDocument.listIterator();
    }

    public ListIterator<Document> listIterator(int var1) {
        return this.listDocument.listIterator(var1);
    }

    public List<Document> subList(int var1, int var2) {
        return this.listDocument.subList(var1, var2);
    }

    @Override
    public Spliterator<Document> spliterator() {
        return null;
    }

    @Override
    public Stream<Document> stream() {
        return null;
    }

    @Override
    public Stream<Document> parallelStream() {
        return null;
    }

    public void cleanup() {
        Gate.getCreoleRegister().removeCreoleListener(this);
    }

    public Resource init() {
        if(this.documentsList != null && !this.documentsList.isEmpty()) {
            this.addAll(this.documentsList);
        }

        return this;
    }

    public static void populate(Corpus var0, URL var1, FileFilter var2, String var3, boolean var4) throws IOException {
        populate(var0, var1, var2, var3, (String)null, var4);
    }

    public static void populate(Corpus var0, URL var1, FileFilter var2, String var3, String var4, boolean var5) throws IOException {
        if(!var1.getProtocol().equalsIgnoreCase("file")) {
            throw new IllegalArgumentException("The URL provided is not of type \"file:\"!");
        } else {
            File var6 = Files.fileFromURL(var1);
            if(!var6.exists()) {
                throw new FileNotFoundException(var6.toString());
            } else if(!var6.isDirectory()) {
                throw new IllegalArgumentException(var6.getAbsolutePath() + " is not a directory!");
            } else {
                File[] var7;
                if(var5) {
                    var7 = Files.listFilesRecursively(var6, var2);
                } else {
                    var7 = var6.listFiles(var2);
                }

                if(var7 != null) {
                    Arrays.sort(var7, new Comparator<File>() {
                        public int compare(File var1, File var2) {
                            return var1.getName().compareTo(var2.getName());
                        }
                    });
                    int var9 = var7.length;

                    for (File var11 : var7) {
                        if (!var11.isDirectory()) {
                            StatusListener var12 = (StatusListener) Gate.getListeners().get("gate.event.StatusListener");
                            if (var12 != null) {
                                var12.statusChanged("Reading: " + var11.getName());
                            }

                            String var13 = var11.getName() + "_" + Gate.genSym();
                            FeatureMap var14 = Factory.newFeatureMap();
                            var14.put("sourceUrl", var11.toURI().toURL());
                            if (var3 != null) {
                                var14.put("encoding", var3);
                            }

                            if (var4 != null) {
                                var14.put("mimeType", var4);
                            }

                            try {
                                Document var15 = (Document) Factory.createResource(DocumentImpl.class.getName(), var14, (FeatureMap) null, var13);
                                var0.add(var15);
                                if (var0.getLRPersistenceId() != null) {
                                    var0.unloadDocument(var15);
                                    Factory.deleteResource(var15);
                                }
                            } catch (Throwable var17) {
                                String var16 = Strings.getNl();
                                Err.prln("WARNING: Corpus.populate could not instantiate document" + var16 + "  Document name was: " + var13 + var16 + "  Exception was: " + var17 + var16 + var16);
                                var17.printStackTrace();
                            }

                            if (var12 != null) {
                                var12.statusChanged(var11.getName() + " read");
                            }
                        }
                    }

                }
            }
        }
    }

    public void populate(URL var1, FileFilter var2, String var3, boolean var4) throws IOException, ResourceInstantiationException {
        populate(this, var1, var2, var3, (String)null, var4);
    }

    public void populate(URL var1, FileFilter var2, String var3, String var4, boolean var5) throws IOException, ResourceInstantiationException {
        populate(this, var1, var2, var3, var4, var5);
    }

    public static long populate(Corpus var0, URL var1, String var2, String var3, int var4, String var5, String var6, boolean var7) throws IOException {
        StatusListener var8 = (StatusListener)Gate.getListeners().get("gate.event.StatusListener");
        var2 = var2.toLowerCase();
        var5 = var5 == null?"":var5.trim() + "_";
        BomStrippingInputStreamReader var9 = null;

        long var28;
        try {
            if(var3 != null && var3.trim().length() != 0) {
                var9 = new BomStrippingInputStreamReader(var1.openStream(), var3, 10485760);
            } else {
                var9 = new BomStrippingInputStreamReader(var1.openStream(), 10485760);
            }

            String var10 = var9.readLine();
            StringBuilder var11 = new StringBuilder();
            boolean var12 = true;
            int var13 = 1;
            long var14 = 0L;

            while(var10 != null) {
                String var16 = var10.toLowerCase();
                int var17;
                if(var12) {
                    var17 = var16.indexOf("<" + var2 + " ");
                    if(var17 == -1) {
                        var17 = var16.indexOf("<" + var2 + ">");
                    }

                    if(var17 != -1) {
                        var10 = var10.substring(var17);
                        var12 = false;
                    } else {
                        var10 = var9.readLine();
                    }
                } else {
                    var17 = var16.indexOf("</" + var2 + ">");
                    if(var17 == -1) {
                        var11.append(var10).append("\n");
                        var10 = var9.readLine();
                    } else {
                        var11.append(var10.substring(0, var17 + var2.length() + 3));
                        var12 = true;
                        if(var8 != null) {
                            var8.statusChanged("Creating Document Number :" + var13);
                        }

                        String var18 = var5 + var13 + "_" + Gate.genSym();
                        String var19 = var11.toString();
                        if(!var7) {
                            var19 = var19.substring(var19.indexOf(">") + 1, var19.lastIndexOf("<"));
                        }

                        FeatureMap var20 = Factory.newFeatureMap();
                        if(var6 != null) {
                            var20.put("mimeType", var6);
                        }

                        var20.put("stringContent", var19);
                        if(var3 != null && var3.trim().length() > 0) {
                            var20.put("encoding", var3);
                        }

                        var14 += (long)var19.getBytes().length;

                        try {
                            Document var21 = (Document)Factory.createResource(DocumentImpl.class.getName(), var20, (FeatureMap)null, var18);
                            ++var13;
                            var0.add(var21);
                            if(var0.getLRPersistenceId() != null) {
                                var0.unloadDocument(var21);
                                Factory.deleteResource(var21);
                            }

                            if(var13 - 1 == var4) {
                                break;
                            }
                        } catch (Throwable var26) {
                            String var22 = Strings.getNl();
                            Err.prln("WARNING: Corpus.populate could not instantiate document" + var22 + "  Document name was: " + var18 + var22 + "  Exception was: " + var26 + var22 + var22);
                            var26.printStackTrace();
                        }

                        var11 = new StringBuilder();
                        if(var8 != null) {
                            var8.statusChanged(var18 + " created!");
                        }

                        var10 = var10.substring(var17 + var2.length() + 3);
                        if(var10.trim().equals("")) {
                            var10 = var9.readLine();
                        }
                    }
                }
            }

            var28 = var14;
        } finally {
            if(var9 != null) {
                var9.close();
            }

        }

        return var28;
    }

    public long populate(URL var1, String var2, String var3, int var4, String var5, String var6, boolean var7) throws IOException, ResourceInstantiationException {
        return populate(this, var1, var2, var3, var4, var5, var6, var7);
    }

    public synchronized void removeCorpusListener(CorpusListener var1) {
        if(this.corpusListeners != null && this.corpusListeners.contains(var1)) {
            Vector<CorpusListener> var2 = (Vector)this.corpusListeners.clone();
            var2.removeElement(var1);
            this.corpusListeners = var2;
        }

    }

    public synchronized void addCorpusListener(CorpusListener var1) {
        Vector<CorpusListener> var2 = this.corpusListeners == null?new Vector<>(2):(Vector)this.corpusListeners.clone();
        if(!var2.contains(var1)) {
            var2.addElement(var1);
            this.corpusListeners = var2;
        }

    }

    public Resource duplicate(Factory.DuplicationContext var1) throws ResourceInstantiationException {
        Corpus var2 = (Corpus)Factory.defaultDuplicate(this, var1);

        for (Document var4 : this) {
            var2.add((Document) Factory.duplicate(var4, var1));
        }

        return var2;
    }

    protected void fireDocumentAdded(CorpusEvent var1) {
        if(this.corpusListeners != null) {
            Vector var2 = this.corpusListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((CorpusListener)var2.elementAt(var4)).documentAdded(var1);
            }
        }

    }

    protected void fireDocumentRemoved(CorpusEvent var1) {
        if(this.corpusListeners != null) {
            Vector var2 = this.corpusListeners;
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
                ((CorpusListener)var2.elementAt(var4)).documentRemoved(var1);
            }
        }

    }

    @gate.creole.metadata.Optional
    @CreoleParameter(
            collectionElementType = Document.class,
            comment = "A list of GATE documents"
    )
    public void setDocumentsList(List<Document> var1) {
        this.documentsList = var1;
    }

    public List<Document> getDocumentsList() {
        return this.documentsList;
    }

    public void resourceLoaded(CreoleEvent var1) {
    }

    public void resourceUnloaded(CreoleEvent var1) {
        Resource var2 = var1.getResource();
        if(var2 instanceof Document) {
            while(this.contains(var2)) {
                this.remove(var2);
            }
        }

    }

    public void resourceRenamed(Resource var1, String var2, String var3) {
    }

    public void datastoreOpened(CreoleEvent var1) {
    }

    public void datastoreCreated(CreoleEvent var1) {
    }

    public void datastoreClosed(CreoleEvent var1) {
    }

    protected class VerboseList extends AbstractList<Document> implements Serializable {
        private static final long serialVersionUID = 3483062654980468826L;
        List<Document> data = new ArrayList<>();

        VerboseList() {
        }

        public Document get(int var1) {
            return (Document)this.data.get(var1);
        }

        public int size() {
            return this.data.size();
        }

        public Document set(int var1, Document var2) {
            Document var3 = (Document)this.data.set(var1, var2);
            MapCorpus.this.fireDocumentRemoved(new CorpusEvent(MapCorpus.this, var3, var1, 402));
            MapCorpus.this.fireDocumentAdded(new CorpusEvent(MapCorpus.this, var2, var1, 401));
            return var3;
        }

        public void add(int var1, Document var2) {
            this.data.add(var1, var2);
            MapCorpus.this.fireDocumentAdded(new CorpusEvent(MapCorpus.this, var2, var1, 401));
        }

        public Document remove(int var1) {
            Document var2 = (Document)this.data.remove(var1);
            MapCorpus.this.fireDocumentRemoved(new CorpusEvent(MapCorpus.this, var2, var1, 402));
            return var2;
        }
    }*/
}
