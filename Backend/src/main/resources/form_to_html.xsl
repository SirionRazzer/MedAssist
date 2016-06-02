<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" 
              encoding="UTF-8" 
              indent="yes"/>

    <xsl:template match="/">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>      
        <html>
            <head>
                <xsl:text disable-output-escaping='yes'>&lt;meta http-equiv="X-UA-Compatible" content="IE=edge"&gt;</xsl:text>
                <xsl:text disable-output-escaping='yes'>&lt;meta name="viewport" content="width=device-width, initial-scale=1"&gt;</xsl:text>            
              
                <title>MedAssist - Pacient</title> 
                          
                <!-- Bootstrap core CSS -->
                <!--<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet"/>-->
                <link href="bootstrap.min.css" rel="stylesheet"/>
                <link href="styles.css" rel="stylesheet"/>
                <!--TODO:
                Custom styles for this template
                -->                
            </head>
            <body role="document">
                <div class="container">
                    <div class="bg-white">
                        <div class="page-header">
                            <h1><xsl:value-of select="form/name"/></h1>                        
                        </div>


                        <xsl:apply-templates select="node()/slides/slide"/>
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Odeslat</button>

                        <div>
                            <p>form by MedAssist</p>
                        </div>     
                    </div>               
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.2/js/bootstrap.min.js" integrity="sha384-vZ2WRJMwsjRMW/8U7i6PWi6AlO1L79snBrmgiDpgIWJ82z8eA5lenwvxbMV1PAh7" crossorigin="anonymous"></script>            
            </body>
        </html>
    </xsl:template>

    <xsl:template match="slide">
        <div class="slide-wrapper"> <!--TODO-->
            <div class="card-block">
                <h2 class="card-title"><xsl:value-of select="name"/></h2>               
            </div>
            <xsl:apply-templates select="questions"/>       
        </div>
    </xsl:template>

    <xsl:template match="questions">
        <div class="form">
            <xsl:apply-templates select="question"/>
        </div>
    </xsl:template>

    <xsl:template match="question">
        <div class="card">           

                <!--QUESTION TEXT-->
                
                <h3 class="card-header"><xsl:value-of select="text"/></h3>
                <div class="card-block">

                <!--ALL POSSIBLE QUESTION TYPES-->

                <xsl:if test="@type='checkbox'">
                    <xsl:for-each select="options/option">
                        <div class="form-group">                    
                        <label>
                            <input type="checkbox"/>
                            <xsl:value-of select="."/>                        
                        </label><br/>
                        </div>
                    </xsl:for-each>
                </xsl:if>

                <xsl:if test="@type='radiobutton'">
                    <xsl:variable name="rID" select="generate-id(options)"/> <!--Variable used as ID for multiple radiogroups-->                   
                    <xsl:for-each select="options/option">
                        <div class="form-group">
                        <label>
                            <input type="radio" name="{$rID}"/>                                     
                            <xsl:value-of select="."/>
                        </label><br/>
                        </div>
                    </xsl:for-each>
                </xsl:if>

                <xsl:if test="@type='range'">
                    <div class="form-group">
                        <xsl:value-of select="min_val"/><input type="range" min="{min_val}" max="{max_val}" step="{step}"/>                
                        <xsl:value-of select="max_val"/>
                    </div>
                </xsl:if>

                <xsl:if test="@type='textbox'">                
                    <fieldset class="form-group">                            
                        <textarea class="form-control" rows="3"></textarea>
                    </fieldset>
                </xsl:if>

                <xsl:if test="@type='date'">
                    <div class="form-group">
                    <input type="date"/>
                    </div>
                </xsl:if>
                
                </div>
        </div>
    </xsl:template>

</xsl:stylesheet>
