package apiserver.services.pdf.gateways.jobs;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.core.connectors.coldfusion.jobs.CFPDFFormJob;
import apiserver.core.connectors.coldfusion.services.ObjectResult;
import apiserver.services.cache.model.Document;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
public class ExtractPdfFormResult extends CFPDFFormJob implements ObjectResult
{

    private String documentId;
    private Document file;
    private Object result;


    public String getDocumentId()
    {
        return documentId;
    }


    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }


    public Document getFile()
    {
        return file;
    }


    public void setFile(Document file)
    {
        this.file = file;
    }


    public Object getResult() {
        return result;
    }


    public void setResult(Object result) {
        this.result = result;
    }
}
