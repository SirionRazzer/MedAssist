<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" 
              encoding="UTF-8" 
              indent="yes"/>

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
                             <div><xsl:value-of select="@qid"/>
                             <xsl:text>.</xsl:text>
                              <ul>
                                 <xsl:apply-templates select="value"/>
                              </ul></div>
                          </xsl:for-each>
                        </xsl:for-each>
                    </div>               
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.2/js/bootstrap.min.js" integrity="sha384-vZ2WRJMwsjRMW/8U7i6PWi6AlO1L79snBrmgiDpgIWJ82z8eA5lenwvxbMV1PAh7" crossorigin="anonymous"></script>            
            </body>
        </html>
    </xsl:template>

    <xsl:template match="slide">
        <xsl:for-each select="/">
    <tr>
      <td><xsl:value-of select="/answers/slide/answer/@qid"/></td>
      <td><xsl:value-of select="/answers/slide/answer/value"/></td>
    </tr>
    </xsl:for-each>
       <div>
            <xsl:text>bu</xsl:text>
            <xsl:apply-templates select="answer"/>
        </div>
    </xsl:template>

    <xsl:template match="value">
        <xsl:value-of select="value"/>
    </xsl:template>

    <xsl:template match="value">
        <li>    
                <!--ANSWER TEXT-->
                <xsl:value-of select="."/>
        </li>
    </xsl:template>

</xsl:stylesheet>