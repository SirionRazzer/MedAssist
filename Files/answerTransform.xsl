<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : newstylesheet.xsl
    Created on : 11 June 2016, 18:12
    Author     : Julia
    Description:
        Transformation on the entire Forms document along with the answers file -
        - taking question text from the forms document based on fid and qid.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" 
              encoding="UTF-8" 
              indent="yes"/>

<xsl:variable name="answerDoc" select="/" />
<xsl:variable name="formsDoc" select="document('forms.xml')" />

<xsl:variable name="relQuestions" select="$formsDoc//question[../../../../@fid = $answerDoc//@fid]" />


  
<xsl:template match="/">
    <html>
        <head>
            <title>MedAssist - Odpovědi pacienta</title> 
        </head>
            <body role="document">
                <div class="container">
                    <div class="bg-white">
                        <div class="page-header">
                            <h1>Formulář č. <xsl:value-of select="answers/@fid"/> pacienta <xsl:value-of select="answers/@pid"/></h1>                        
                        </div> 
                        <xsl:for-each select="answers/slide">
                                <xsl:for-each select="answer">
                                <xsl:variable name="answerQid" select="@qid" />
                                    <div>
                                        <xsl:value-of select="$answerQid"/>
                                        <xsl:text>. </xsl:text>
                                        <xsl:value-of select="$relQuestions[@qid=current()/@qid]/text"/>
                                        <ul>
                                            <xsl:apply-templates select="value"/>
                                        </ul>
                                    </div>
                                </xsl:for-each>
                        </xsl:for-each>
                    </div>               
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.2/js/bootstrap.min.js" integrity="sha384-vZ2WRJMwsjRMW/8U7i6PWi6AlO1L79snBrmgiDpgIWJ82z8eA5lenwvxbMV1PAh7" crossorigin="anonymous"></script>            
            </body>
        </html>
    </xsl:template>


    <xsl:template match="value">
        <li>    
                <!--ANSWER TEXT-->
                <xsl:value-of select="."/>
        </li>
    </xsl:template>

</xsl:stylesheet>
