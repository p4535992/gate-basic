/*
OwlExporter --  http://www.semanticsoftware.info/owlexporter

This file is part of the OwlExporter architecture.

Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
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

package info.semanticsoftware.owlexporter.exception;

public class ApplicationException extends Exception {
	private static final long serialVersionUID = 3242689860362986983L;

	public ApplicationException(){
		super();
	}

	public ApplicationException(String strMessage){
		super(strMessage);
	}

	public ApplicationException(Throwable thrCause){
		super(thrCause);
	}
	
	public ApplicationException(String strMessage, Throwable thrCause){
		super(strMessage, thrCause);
	}
}
