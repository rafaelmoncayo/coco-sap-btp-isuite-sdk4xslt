<xsl:stylesheet version="3.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:jt="http://saxon.sf.net/java-type"
	xmlns:map="java:java.util.Map"
	exclude-result-prefixes="#all" >
	
	<!-- 
	=======================
	 TestValueMapping.xslt
	=======================
	 Purpose: Do value mapping testing
	
	 Prerequisites:
	 a) Deploy the script collection "COCO Cloud Integration SDK extensions 
	    for XSLT"
	 b) Deploy the value mapping "COCO Value Mapping - UOM"
	 c) Instantiate the property COCO_ValueMapping with an instance of 
	    ValueMappingWrapper calling the script COCO_AddJavaSDKExtension.groovy
	-->
	
	<xsl:output 
		method="xml" 
		omit-xml-declaration="yes" 
		indent="yes" />
	
	<!-- Expected instance of ValueMappingWrapper java class -->
	<xsl:param name="COCO_ValueMapping" />
	<xsl:variable name="vmUOM" select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', 'COCO', 'UOM_TEXT'))" />

	<xsl:template match="/">
		<items>
			<xsl:text>&#xa;  </xsl:text>
			<xsl:comment> COCO_ValueMapping variable is a <xsl:value-of select="$COCO_ValueMapping" /> </xsl:comment>
			<xsl:text>&#xa;  </xsl:text>
			<xsl:comment> vmUOM variable is a <xsl:value-of select="$vmUOM" /> </xsl:comment>
			<xsl:for-each select="/Items/Item" >
				<item>
					<xsl:attribute name="id">
						<xsl:value-of select="MaterialNumber" />
					</xsl:attribute>
					<xsl:attribute name="uom_code">
						<xsl:value-of select="UnitOfMeasure" />
					</xsl:attribute>
					<xsl:attribute name="uom_mapped1">
						<xsl:value-of select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', UnitOfMeasure, 'COCO', 'UOM_TEXT'))" />
					</xsl:attribute>
					<xsl:attribute name="uom_mapped2">
						<xsl:value-of select="map:get($vmUOM, UnitOfMeasure)" />
					</xsl:attribute>
				</item>
			</xsl:for-each>

			<xsl:text>&#xa; </xsl:text>		
			<xsl:comment> Another way to do value mapping: send all source values as a sequence in a binded instance of ValueMapperWrapper </xsl:comment>
			<SequenceCall>
					<xsl:attribute name="source_values">
						<xsl:value-of select="/Items/Item/UnitOfMeasure" separator="," />
					</xsl:attribute>
					<xsl:attribute name="target_values">
						<xsl:value-of select="map:get($vmUOM, /Items/Item/UnitOfMeasure)" separator="," />
					</xsl:attribute>
			</SequenceCall>
		</items>

	</xsl:template>

</xsl:stylesheet>
