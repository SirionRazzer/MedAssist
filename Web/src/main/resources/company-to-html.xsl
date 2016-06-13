<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml">
    
    <xsl:output method="xml"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                encoding="UTF-8"
                indent="yes"
    />
    <xsl:template match="company">
        <html>
            <head>
                <title>Company</title>
                <meta http-equiv="Content-Style-Type" content="text/css"/>
                <link rel="stylesheet" href="company.css" type="text/css" media="screen"/>
            </head>
            <body>
                <h1>Company </h1>
                <div class="link-box">
                    <ul>
                        <lh>Quick links to divisions</lh>
                        <xsl:apply-templates select="division" mode="quick_division_list"/>
                    </ul>
                    <ul>
                        <lh>Quick links to employee categories</lh>
                        <xsl:apply-templates select="category" mode="quick_employeeCategory_list"/>
                    </ul>
                </div>
                <h2>Divisions</h2>
                <xsl:apply-templates select="division"/>
                <h2>Employee Categories</h2>
                <xsl:apply-templates select="category"/>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="division" mode="quick_division_list">
        <li>
            <xsl:element name="a">
                <xsl:attribute name="href">#<xsl:value-of select="@did"/></xsl:attribute>
                <xsl:value-of select="name"/>
            </xsl:element>
        </li>
    </xsl:template>   
    
    <xsl:template match="category" mode="quick_employeeCategory_list">
        <li>
            <xsl:element name="a">
                <xsl:attribute name="href">#<xsl:value-of select="@cid"/></xsl:attribute> 
                <xsl:value-of select="name"/>
            </xsl:element>
        </li>
    </xsl:template>
    
    <xsl:template match="division">
        <div class="division">
            <h3>
                <xsl:element name="a">
                    <xsl:attribute name="name">
                        <xsl:value-of select="@did"/>
                    </xsl:attribute> 
                    <xsl:value-of select="name"/>
                </xsl:element>
            </h3>
            <div>
                <em>Head: </em>
                <strong>
                    <xsl:value-of select="head/person/name"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="head/person/surname"/>
                    <xsl:text> (PID=</xsl:text>
                    <xsl:value-of select="head/person/@pid"/>
                    <xsl:text>)</xsl:text>
                </strong>
            </div>
            <ul>
                <lh>Employees:</lh>
                <xsl:apply-templates select="employees/employee"/>
            </ul>
        </div>
    </xsl:template>
    
    <xsl:template match="employee">
       <li>
            <xsl:if test="@category = 'worker' or @category = 'developer'">
                <xsl:value-of select="name"/>
                <xsl:text>, salary </xsl:text>
                <xsl:value-of select="salary"/>
                <xsl:text> CZK</xsl:text>
            </xsl:if>
            <xsl:if test="@category = 'contractor'">
                <xsl:value-of select="company"/>
                <xsl:text>, contractor fee </xsl:text>
                <xsl:value-of select="fee"/>
                <xsl:text> CZK, is </xsl:text>
                <xsl:if test="vat = 'no'">
                    <xsl:text> not </xsl:text>
                </xsl:if>
                <xsl:text>a VAT payer</xsl:text>
            </xsl:if>
        </li>
    </xsl:template>
    
    <xsl:template match="category">
        <div class="category">
            <h3>
                <xsl:element name="a">
                    <xsl:attribute name="name">
                        <xsl:value-of select="@cid"/>
                    </xsl:attribute>
                    <xsl:value-of select="name"/>
                </xsl:element>
            </h3>
            <em>
                <xsl:value-of select="description"/>
            </em>
            <ul>
                <lh>Employees in this category:</lh>
                <xsl:if test="@cid = 'worker'">
                    <xsl:for-each select="/company/division">
                        <xsl:apply-templates select="employees/employee" mode="worker"/>                        
                    </xsl:for-each>
                </xsl:if>
                <xsl:if test="@cid = 'developer'">
                    <xsl:for-each select="/company/division">
                        <xsl:apply-templates select="employees/employee" mode="developer"/>                        
                    </xsl:for-each>
                </xsl:if>
                <xsl:if test="@cid = 'contractor'">
                    <xsl:for-each select="/company/division">
                        <xsl:apply-templates select="employees/employee" mode="contractor"/>                        
                    </xsl:for-each>
                </xsl:if>
            </ul>
        </div>
    </xsl:template>
    
    <xsl:template match="employee" mode="worker">
        <xsl:if test="@category = 'worker'">
            <li>
                <xsl:value-of select="name"/>
                <xsl:text>, salary </xsl:text>
                <xsl:value-of select="salary"/>
                <xsl:text> CZK</xsl:text>
            </li>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="employee" mode="developer">
        <xsl:if test="@category = 'developer'">
            <li>
                <xsl:value-of select="name"/>
                <xsl:text>, salary </xsl:text>
                <xsl:value-of select="salary"/>
                <xsl:text> CZK</xsl:text>
            </li>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="employee" mode="contractor">
        <xsl:if test="@category = 'contractor'">
            <li>
                <xsl:value-of select="company"/>
                <xsl:text>, contractor fee </xsl:text>
                <xsl:value-of select="fee"/>
                <xsl:text> CZK, is </xsl:text>
                <xsl:if test="vat = 'no'">
                    <xsl:text> not </xsl:text>
                </xsl:if>
                <xsl:text>a VAT payer</xsl:text>
            </li>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
