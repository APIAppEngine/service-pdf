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

/**
 * User: mikenimer
 * Date: 9/16/13
 */
public class Html2PdfJob extends CFDocumentJob implements BinaryJob
{

    private String html;
    private String headerHtml;
    private String footerHtml;
    private byte[] pdfBytes;



    public String getHtml()
    {
        return html;
    }


    /**
     * Html to convert to pdf
     * @param html
     */
    public void setHtml(String html)
    {
        this.html = html;
    }


    public String getHeaderHtml() {
        return headerHtml;
    }


    /**
     * HTML to use for the header of all pages in the PDF
     * @param headerHtml
     */
    public void setHeaderHtml(String headerHtml) {
        this.headerHtml = headerHtml;
    }


    public String getFooterHtml() {
        return footerHtml;
    }


    /**
     * HTML to use for the footer of all pages in the PDF
     * @param footerHtml
     */
    public void setFooterHtml(String footerHtml) {
        this.footerHtml = footerHtml;
    }


    /**
     * bytes of generated pdf to return
     * @return
     */
    public byte[] getPdfBytes() {
        return pdfBytes;
    }

    public void setPdfBytes(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
    }


}
