<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" 
              doctype-system="about:legacy-compat"
              encoding="UTF-8" 
              indent="yes" />

  <xsl:template match="/">
    <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
    <html>
        <head>
            <title>MedAssist - Pacient</title>
            <meta http-equiv="Content-Style-Type" content="text/css"/>
            <link rel="stylesheet" href="company.css" type="text/css" media="screen"/> <!--Zmenit stylesheet-->
        </head>
        <body>
            <div class="form-name"> <!--trida pro nazev formulare-->
                <h1>
                    <xsl:value-of select="form/name"/>                    
                </h1>                
            </div>
            <xsl:apply-templates select="node()/slides/slide"/>
        </body>
    </html>
  </xsl:template>
  
  <xsl:template match="slide">
      <div class="slide-wrapper"> <!--trida pro zabaleni jednoho slajdu-->
          <div class="slide-header"> <!--trida pro zahlavi slajdu-->
              <p><xsl:value-of select="@sid"/></p>
              <p><xsl:value-of select="name"/></p>              
          </div>
          <xsl:apply-templates select="questions"/>       
      </div>
  </xsl:template>

<xsl:template match="questions">
    <div class="questions-wrapper"> <!--trida pro zabaleni otazek-->
        <xsl:apply-templates select="question"/>
    </div>
</xsl:template>

<xsl:template match="question">
    <div class="question-wrapper"> <!--trida pro zabaleni jedne otazky-->
        <div class="question-info"> <!--trida pro zabaleni info o otazce-->
            <table>
                <tr>
                    <td><xsl:value-of select="@qid"/></td>
                    <td><xsl:value-of select="@type"/></td>
                </tr>
            </table>            
        </div>
        
        <form role="form">
            
            <!--QUESTION TEXT-->
            
            <div class="form-group"> <!--trida pro bootstrap podle prikladu z w3schools-->
                <h2><xsl:value-of select="text"/></h2>
            </div>
                        
            <!--ALL POSSIBLE QUESTION TYPES-->
            
            <xsl:if test="@type='checkbox'">
                <xsl:for-each select="options/option">
                    <div class="form-group">                    
                    <input type="checkbox" id="chb"/>
                    <label for="chb">
                        <xsl:value-of select="."/>                        
                    </label><br/>
                    </div>
                </xsl:for-each>
            </xsl:if>

            <xsl:if test="@type='radiobutton'">
                <xsl:for-each select="options/option">
                    <div class="form-group">
                    <input type="radio" name="rad" id="rd"/>
                    <label for="rd">
                        <xsl:value-of select="."/>
                    </label><br/>
                    </div>
                </xsl:for-each>
            </xsl:if>

            <xsl:if test="@type='range'">
                <div class="form-group">                
                <input type="range" min="{min_val}" max="{max_val}" step="{step}"/>                
                </div>
            </xsl:if>

            <xsl:if test="@type='textbox'">                
                <div class="form-group">
                <input type="text"/>
                </div>
            </xsl:if>

            <xsl:if test="@type='date'">
                <div class="form-group">
                <input type="date"/>
                </div>
            </xsl:if>
        </form>
    </div>
</xsl:template>

</xsl:stylesheet>
