/*
OwlExporter --  http://www.semanticsoftware.info/owlexporter

This file is part of the OwlExporter architecture.

Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info
        René Witte
        Ninus Khamis

The OwlExporter  architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package info.semanticsoftware.owlexporter;

import java.io.File;
import java.net.URL;
import java.util.*;
import gate.*;
import gate.creole.*;
import gate.creole.metadata.*;

@CreoleResource(name = "OwlExporter",
        comment = "The OwLExporter Processing Resource")
public class OwlExporter extends AbstractLanguageAnalyser 
    implements ProcessingResource {
    private static final long serialVersionUID = 42L;
    
    @RunTime
    @Optional
    @CreoleParameter(comment="The Input Annotation Set")
    private String inputASName;

    @RunTime
    @CreoleParameter(comment="List of Names of Coreference Chains")
    private ArrayList<?> corefChainList;
    
    @RunTime
    @CreoleParameter(comment="File Location of the Domain Ontology to Import", suffixes="owl")
    private URL importDomainOntology;

    @RunTime
    @CreoleParameter(comment="File Location of the Domain Ontology to Export", suffixes="owl")
    private URL exportDomainOntology;

    @RunTime
    @CreoleParameter(comment="File Location of the NLP Ontology to Import", suffixes="owl")
    private URL importNLPOntology;

    @RunTime
    @CreoleParameter(comment="File Location of the NLP Ontology to Export", suffixes="owl")
    private URL exportNLPOntology;

    @RunTime
    @CreoleParameter(comment="Generate an NLP Ontology", defaultValue="false")
    private Boolean exportNLP;

    @RunTime
    @CreoleParameter(comment="Generate Multiple Ontologies (Generic, Domain, Coreference)", defaultValue="false")
    private Boolean multiOwlExport;

    @RunTime
    @CreoleParameter(comment="Show debug information", defaultValue="false")
    private Boolean debugFlag;

    @CreoleParameter(comment="Export format", defaultValue="OWL")
    private String exportFormat;

    private String documentFileName;
    private String exportDomainFilePathStr;
    private String exportNLPFilePathStr;
    private Boolean domainExists, nlpExists;	
	
    private static final String EXPORT_EXT = "owl";		
   
    
    public void setInputASName(String inputASName) {
	this.inputASName = inputASName;
    }
   
    public String getInputASName() {    	    	
    	return this.inputASName;
    }    
    
    
    public void setDocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
    }
   
    public String getDocumentFileName() {    	    	
    	return this.documentFileName;
    }
    
    public void setCorefChainList(ArrayList corefChainList) throws gate.creole.ExecutionException {
		this.corefChainList = corefChainList;
	}
	
	public ArrayList getCorefChainList() {
		return this.corefChainList;
	}
    
    public void setImportDomainOntology(URL importDomainOntology) {
		this.importDomainOntology = importDomainOntology;
    }
   
    public URL getImportDomainOntology() {    	    	
    	return this.importDomainOntology;
    }
    
    public void setExportDomainOntology(URL exportDomainOntology) {
		this.exportDomainOntology = exportDomainOntology;
    }
   
    public URL getExportDomainOntology() {    	    	
    	return this.exportDomainOntology;
    }
    
    public void setDomainExportFilepathStr(String exportDomainFilePathStr) {
		this.exportDomainFilePathStr = exportDomainFilePathStr;
    }
   
    public String getDomainExportFilepathStr() {    	    	
    	return this.exportDomainFilePathStr;
    }    
    
    public void setImportNLPOntology(URL importNLPOntology) {
		this.importNLPOntology = importNLPOntology;
    }
   
    public URL getImportNLPOntology() {    	    	
    	return this.importNLPOntology;
    }
    
    public void setExportNLPOntology(URL exportNLPOntology) {
		this.exportNLPOntology = exportNLPOntology;
    }
   
    public URL getExportNLPOntology() {    	    	
    	return this.exportNLPOntology;
    }
    
    public void setNLPExportFilepathStr(String exportNLPFilePathStr) {
		this.exportNLPFilePathStr = exportNLPFilePathStr;
    }
   
    public String getNLPExportFilepathStr() {    	    	
    	return this.exportNLPFilePathStr;
    }   
	
	public void setDebugFlag(Boolean debugFlag) {
		this.debugFlag = debugFlag;
	}
	
	public Boolean getDebugFlag() {
		return this.debugFlag;
	}	
	
	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
    }
   
    public String getExportFormat() {    	    	
    	return this.exportFormat;
    }
    
    public void setExportNLP(Boolean exportNLP) {
		this.exportNLP = exportNLP;
    }
   
    public Boolean getExportNLP() {    	    	
    	return this.exportNLP;
    }
    
    public void setMultiOwlExport(Boolean multiOwlExport) {
		this.multiOwlExport = multiOwlExport;
    }
   
    public Boolean getMultiOwlExport() {    	    	
    	return this.multiOwlExport;
    }
    
    public void setDomainExists(Boolean exists) {
		this.domainExists = exists;
	}
	
	public Boolean getDomainExists() {
		return this.domainExists;
	}
	
	public void setNLPExists(Boolean exists) {
		this.nlpExists = exists;
	}
	
	public Boolean getNLPExists() {
		return this.nlpExists;
	}
	
	public Resource init() throws ResourceInstantiationException {
		return super.init();
	}
	
	public void reInit() throws ResourceInstantiationException {
		init();
	}	 
	 
	public void execute() throws ExecutionException {		
		CommonOwlExporter cOE = new CommonOwlExporter();
		System.out.println("OWLExporter Message: OWLExporter Started.............");
		long time;
	        
		time = System.currentTimeMillis();		
		
		if (this.getDocument() == null) {
			throw new ExecutionException(
					"OWLExporter Error: No document found to export in APF format!");
		}

		try {			
			File testDomainFile = new File(this.getExportDomainOntology().toURI());
			File testNLPFile = new File(this.getExportNLPOntology().toURI());
			
			this.setDomainExists(testDomainFile.exists());
			this.setNLPExists(testNLPFile.exists());

			if (this.getExportDomainOntology() == null || this.getExportNLPOntology() == null) {
				this.setDomainExportFilepathStr(this.getDocument().getSourceUrl().getFile() + 
						"." + EXPORT_EXT);
				this.setNLPExportFilepathStr(this.getDocument().getSourceUrl().getFile() + 
						"." + EXPORT_EXT);
			} 
			else {
				this.setDomainExportFilepathStr(this.getExportDomainOntology().getPath());
				this.setNLPExportFilepathStr(this.getExportNLPOntology().getPath());
			}			
			
			this.setDocumentFileName(new File(document.getSourceUrl().getPath()).
					getName().replace(".", "_"));

			if (this.getDebugFlag()) System.out.println("exportFilePathStr: " + 
					this.getDomainExportFilepathStr());			
			
			OwlExporterOntology dOntology = new OwlExporterOntology(this);			
			OwlExporterOntology nOntology = null;			
			OwlExporterOntology baseOntology = new OwlExporterOntology(this);			
			
			List<OwlExporterOntologyPopulator> owlExporterOntologyPopulatorList = 
				new ArrayList<OwlExporterOntologyPopulator>();
			
			//Note to self we also maybe able to handle multi vs single using the saveOntology method in the Factory Class
			if(this.getMultiOwlExport()) {
				if (this.getDebugFlag()) System.out.println("Running Multi OWL File Export Mode.");
				owlExporterOntologyPopulatorList.add(new OwlExporterOntologyDomainEntities(dOntology));								
			}
			else {
				if (this.getDebugFlag()) System.out.println("Running Single OWL File Export Mode.");
				owlExporterOntologyPopulatorList.add(new OwlExporterOntologyDomainEntities(baseOntology));							
			}
			
			if(this.getExportNLP()) {
				nOntology = new OwlExporterOntology(this);	
				owlExporterOntologyPopulatorList.add(new OwlExporterOntologyNLPEntities(nOntology));				
			}
							
			
			baseOntology.createOntology("", OntologyType.domain);
			if(this.getExportNLP())
				nOntology.createOntology("", OntologyType.nlp);

			if(this.getDomainExists() || this.getNLPExists()) {
			   for(OwlExporterOntologyPopulator o : owlExporterOntologyPopulatorList) {
					if(o.hasProcessed(o.getOwlExporterOntology().getOwlModel(), this.getDocument().getSourceUrl())) {
						System.out.println("The OwlExporter has already processed entites from corpus: " +
								this.getDocument().getName() +
								". Please trySelectWithRowMap another corpus to process or remove the existing output ontology.");
						
						return;						
					}									
            }
			}
									
			for(OwlExporterOntologyPopulator o : owlExporterOntologyPopulatorList) {			
				//if (this.getMultiOwlExport() || o.getOntologyType() == OntologyType.nlp)
				if (this.getMultiOwlExport())
					o.getOwlExporterOntology().createOntology(o.getFileEnding(), o.getOntologyType());
				
				o.populateOntology(cOE, o.getOntologyType());
				o.addObjectProperty(o.getOwlExporterOntology().getOwlModel(), o.getOntologyType());
				
				if(o.getOntologyType() == OntologyType.domain) {
					o.addCoreferences(cOE);
					o.addCoreferenceProperty(o.getOwlExporterOntology().getOwlModel(), o.getOntologyType());					
				}						
			}		
			
			if(this.getExportNLP())
				for(OwlExporterOntologyPopulator d : owlExporterOntologyPopulatorList) {
					if(d.getOntologyType() == OntologyType.domain) {
						for(OwlExporterOntologyPopulator n : owlExporterOntologyPopulatorList) {
							if(n.getOntologyType() == OntologyType.nlp) {
								n.addDomainNLPProperty(
										d.getOwlExporterOntology().getOwlModel(), d.getOntologyType(),
										n.getOwlExporterOntology().getOwlModel(), n.getOntologyType(),
										OntologyType.domainnlp);							
								
							}								
						}						
					}						
				}

         if (this.getMultiOwlExport()) {
			   for(OwlExporterOntologyPopulator o : owlExporterOntologyPopulatorList) {				
				   if (o.getOntologyType() != OntologyType.nlp) {
					   o.getOwlExporterOntology().addNamespace(o.getPrefix());					
					   o.getOwlExporterOntology().saveOntology(o.getFileEnding(),o.getOntologyType());					
					   baseOntology.importOntology(o.getOwlExporterOntology(), o.getPrefix());
					
					   //really old functionality
					   //if(o.getOntologyType()!= OntologyType.nlp)
								
				   }												
				}												
			}
			
			baseOntology.saveOntology("", OntologyType.domain);
			
			if(this.exportNLP)
				nOntology.saveOntology("", OntologyType.nlp);
			
			//really old functionality
			//baseOntology.addNamespace();
			
			time = System.currentTimeMillis() - time;
						
			if(this.getDebugFlag())
				System.out.println("Exporting Annotations took " + time + " milliseconds");
			
		}
		catch (Exception e) {			
			e.printStackTrace();
			System.out.println("OWLExporter " + e);			
			return;
		}
	}
}
