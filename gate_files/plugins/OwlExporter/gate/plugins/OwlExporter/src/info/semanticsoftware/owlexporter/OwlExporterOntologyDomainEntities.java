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

public class OwlExporterOntologyDomainEntities extends OwlExporterOntologyPopulator{
	protected static final OntologyType oT = OntologyType.domain;
	protected static final String PREFIX = "domain";
	protected static final String FILEENDING = "_Domain";
		
	protected OwlExporterOntologyDomainEntities(OwlExporterOntology owlExporterOntology) {
		this.setOwlExporterOntology(owlExporterOntology);
	}
	
	protected OntologyType getOntologyType() {
    	return oT;
    }
	
	protected String getPrefix() {
    	return OwlExporterOntologyDomainEntities.PREFIX;
    }
	
	protected String getFileEnding() {
    	return OwlExporterOntologyDomainEntities.FILEENDING;
    }	
}
