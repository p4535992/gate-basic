##############################################################
### A MODULE MAVEN WITH MANY UTILITY CLASS FOR THE API GATE VERSION 8###
##############################################################
#########################
###Last Update: 2016-01-28
#########################
NOTE: I'm not a expert programmer so any suggestion or advise is welcome.

[![Release](https://img.shields.io/github/release/p4535992/gate-basic.svg?label=maven)](https://jitpack.io/p4535992/gate-basic)

Example code 1, extract information with gate:
```java

    //Init Gate Embedded
    Gate8Kit gate8 = Gate8Kit.getInstance();
    controller = gate8.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
            "application_test1.xgapp");
    //Class for process a document with GATE and get a result with only the st ring value
    //name Document -> name AnnotationSet -> name Annotation -> string content.
    ExtractorInfoGate8 eig8 = ExtractorInfoGate8.getInstance();
    //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
    List<String> listAnn = new ArrayList<>();
    listAnn.add("MyIndirizzo");
    //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
    List<String> listAnnSet = new ArrayList<>();
    listAnnSet.add("MyAnnSet");
    //Store the result on of the extraction on a GateSupport Object
    GateSupport support = GateSupport.getInstance(
            eig8.extractorGATE(
                    new URL("http://www.unifi.it"),(CorpusController)controller,"corpus_test_1",listAnn,listAnnSet,true)
    );
    //Now you can get the content from a specific document, specific AnnotationSet, specific Annotation.
    String contnet0 = support.getContent("doc1", "MyAnnSet", "MyIndirizzo");
    String content1 = support.getContent(0,"MyAnnSet", "MyIndirizzo");
    String content2 = support.getContent(0,0,"MyIndirizzo");
    String content3 = support.getContent(0,0,0); 
    
```
Example code 2, set up Gate embedded to the project with API GATE.
Note: By default the base directory is given from "System.getProperty("user.dir")" the root folder of the project, if
you want change ou must invoke setBaseDirectory(String pathToTheDirectory) before invoke setUpGateEmbedded.

```java

    private static Controller controller;
    Gate8Kit gate8 = Gate8Kit.getInstance();
            controller = gate8.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                    "application_test1.xgapp");
    ExtractorInfoGate8 eig8 = ExtractorInfoGate8.getInstance();
    eig8 =...
    
```
Example code 3, set up Gate embedded to the project with Spring Framework (the parameter class just help us to find the
resources file).

```java

    private static DocumentProcessor procDoc;
    procDoc = gate8.setUpGateEmbeddedWithSpring("gate/gate-beans.xml",this.getClass(),"documentProcessor");
    ExtractorInfoGate8 eig8 = ExtractorInfoGate8.getInstance();
    eig8 =...
```


You can the dependency to this github repository With jitpack (https://jitpack.io/):

<!-- Put the Maven coordinates in your HTML: -->
 <pre class="prettyprint">&lt;dependency&gt;
  &lt;groupId&gt;com.github.p4535992&lt;/groupId&gt;
  &lt;artifactId&gt;gate-basic&lt;/artifactId&gt;
  &lt;version&gt;<span id="latest_release">1.6.7</span>&lt;/version&gt;
&lt;/dependency&gt;  </pre>

<!-- Add this script to update "latest_release" span to latest version -->
<script>
      var user = 'p4535992'; // Replace with your user/repo
      var repo = 'gate-basic'

      var xmlhttp = new XMLHttpRequest();
      xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
              var myArr = JSON.parse(xmlhttp.responseText);
              populateRelease(myArr);
          }
      }
      xmlhttp.open("GET", "https://api.github.com/repos/" user + "/" + repo + "/releases", true);
      xmlhttp.send();

      function populateRelease(arr) {
          var release = arr[0].tag_name;
          document.getElementById("latest_release").innerHTML = release;
      }
</script>
