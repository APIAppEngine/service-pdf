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

import apiserver.core.connectors.coldfusion.jobs.CFDocumentJob;
import apiserver.core.connectors.coldfusion.services.BinaryJob;

import java.util.Map;

/**
 * User: mikenimer
 * Date: 9/16/13
 */
public class Url2PdfJob extends CFDocumentJob implements BinaryJob
{


    // Url to load and save as pdf
    private String path;
    // generated PDF bytes
    private byte[] pdfBytes;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getPdfBytes() {
        return pdfBytes;
    }

    public void setPdfBytes(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }


    @Override
    public Map getOptions()
    {
        return super.getOptions();
    }


    @Override
    public void setOptions(Map _options)
    {
        super.setOptions(_options);
    }
}
