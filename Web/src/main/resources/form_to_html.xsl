<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" 
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
                        <button class="btn btn-lg btn-primary btn-block" type="submit" onclick="createNewAnswer()">Odeslat</button>
                        
                        <script src="createAnswerXML.js"></script>
                        
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
                <h2 class="card-title" id="{@sid}"><xsl:value-of select="name"/></h2>               
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
                <div class="card-block" id="{@qid}">

                    <!--ALL POSSIBLE QUESTION TYPES-->

                    <xsl:if test="@type='checkbox'">
                        <xsl:for-each select="options/option">
                            <div class="form-group" type="checkbox">                    
                            <label>
                                <input type="checkbox"/>
                                <p>
                                    <xsl:value-of select="."/>
                                </p>
                            </label><br/>
                            </div>
                        </xsl:for-each>
                    </xsl:if>

                    <xsl:if test="@type='radiobutton'">
                        <xsl:variable name="rID" select="generate-id(options)"/> <!--Variable used as ID for multiple radiogroups-->                   
                        <xsl:for-each select="options/option">
                            <div class="form-group" type="radio">
                            <label>
                                <input type="radio" name="{$rID}"/>
                                <p>                                     
                                    <xsl:value-of select="."/>
                                </p>
                            </label><br/>
                            </div>
                        </xsl:for-each>
                    </xsl:if>

                    <xsl:if test="@type='range'">
                        <div class="form-group" type="range">                            
                            <input type="range" min="{min_val}" max="{max_val}" step="{step}" onchange="this.nextElementSibling.value = this.value"/>                          
                            <input type="text" value="{(min_val + max_val) div 2}" oninput="this.previousElementSibling.value = this.value"/>                            
                         
                        </div>
                    </xsl:if>

                    <xsl:if test="@type='textbox'">                
                        <fieldset class="form-group" type="textbox">                            
                            <textarea class="form-control" rows="3"></textarea>
                        </fieldset>
                    </xsl:if>

                    <xsl:if test="@type='date'">
                        <div class="form-group" type="date">
                        <input type="date"/>
                        </div>
                    </xsl:if>                
                </div>
        </div>
    </xsl:template>

</xsl:stylesheet>
