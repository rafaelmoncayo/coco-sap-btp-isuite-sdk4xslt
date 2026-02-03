<xsl:stylesheet version="3.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:jt="http://saxon.sf.net/java-type"
	xmlns:map="java:java.util.Map"
	exclude-result-prefixes="#all" >

	<!-- 
	===========================
	 TestPartnerDirectory.xslt
	===========================
	 Purpose: Test different methods of the PartnerDirectoryWrapper
	
	 Prerequisites:
	 a) Deploy the script collection "COCO Cloud Integration SDK extensions 
	    for XSLT"
	 b) Create the Partner Id 'S4H400' in Manage Partner Directory tile.  
	    at String Parameters add the following parameters
	      http_port: 443
	      hostname: s4h.somedomain.corp
	    at Binary Parameters add a SSL certificate with name SSL_Cert 
	    at Alternative Partners add the following entry
	      Agency: Landscape
	      Scheme: S4HANA
	      ID: DEV
	    at Authorized Users add: 'user1', 'user2'
	 c) Instantiate the property "COCO_PartnerDirectory" with an instance of 
	    PartnerDirectoryWrapper calling the script 
	    COCO_AddJavaSDKExtension.groovy
	-->
	
	
	<xsl:output 
		method="xml" 
		omit-xml-declaration="yes" 
		indent="yes" />
	
	<!-- Expected instance of PartnerDirectoryWrapper java class -->
	<xsl:param name="COCO_PartnerDirectory" />
	
	<!-- Create a binded instance -->
	<xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, 'S4H400')" />
	<!-- 
	Other ways to get binded instance: 
	<xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstance', 'Landscape', 'S4HANA', 'DEV'))" />
	<xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('Instance', 'S4H400'))" />
	<xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstanceOfAuthorizedUser', 'user1'))" />
    -->

	<xsl:template match="/">
		<xsl:variable name="err1" select="map:get($COCO_PartnerDirectory, ('WrongMethod', 'whatever'))" />
	
		<results>
			<xsl:text>&#xa;  </xsl:text>
			<xsl:comment> Expected errors </xsl:comment>
			<test_error 
				result="{map:get($COCO_PartnerDirectory, ())}" 
				expect="ERROR: Please, pass arguments a XSLT sequence. Received null value" />
			<test_error 
				result="{map:get($COCO_PartnerDirectory, ('WrongMethod', 'whatever'))}" 
				expect="ERROR: No method 'WrongMethod' exists. Use one of the followings: 'PartnerId', 'PartnerIdInstance', 'AlternativePartnerId', 'AuthorizedUsers', 'Parameter', 'PartnerIdOfAuthorizedUser' or 'PartnerIdInstanceOfAuthorizedUser'" />
			<test_error 
				result="{map:get($COCO_PartnerDirectory, ('PartnerId', 'Landscape'))}" 
				expect="ERROR: Wrong number of parameters for method 'PartnerId'. Add the following values after 'PartnerId': partnerId, agency, scheme, alternativePartnerId" />
			<xsl:text>&#xa;  </xsl:text>
			<xsl:comment> Expected success responses </xsl:comment>
			<test_PartnerId 
				result="{map:get($COCO_PartnerDirectory, ('PartnerId', 'Landscape', 'S4HANA', 'DEV'))}" 
				expect="S4H400" />
			<test_toString 
				result="{$pdS4}" 
				expect="PartnerDirectoryWrapper (S4H400)" />
			<test_AlternativePartnerId 
				result="{map:get($COCO_PartnerDirectory, ('AlternativePartnerId', 'S4H400', 'Landscape', 'S4HANA'))}" 
				expect="DEV" />
			<test_AlternativePartnerId
				result="{map:get($pdS4, ('AlternativePartnerId', 'Landscape', 'S4HANA'))}" 
				expect="DEV" />
			<test_AuthorizedUsers
				result="{map:get($COCO_PartnerDirectory, ('AuthorizedUsers', 'S4H400'))}" 
				expect="payrollextension user1 user2" />
			<test_AuthorizedUsers 
				result="{map:get($pdS4, ('AuthorizedUsers'))}" 
				expect="payrollextension user1 user2" />
			<test_Parameter
				result="{map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'hostname'))}" 
				expect="s4h.somedomain.corp" />
			<test_Parameter
				result="{map:get($pdS4, ('Parameter', 'hostname'))}" 
				expect="s4h.somedomain.corp" />
			<test_Parameter
				result="{map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'http_port', 'STRING'))}" 
				expect="443" />
			<test_Parameter
				result="{map:get($pdS4, ('Parameter', 'http_port', 'STRING'))}" 
				expect="443" />
			
			<xsl:variable name="octects1" select="map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'SSL_Cert', 'BINARYDATA'))" />
			<xsl:variable name="octects2" select="map:get($pdS4, ('Parameter', 'SSL_Cert', 'BINARYDATA'))" />
			<test_Parameter_BinaryData
				result="{substring(codepoints-to-string($octects1),1,40)}" 
				expect="-----BEGIN CERTIFICATE-----&#xD;&#xA;MIIGODCCBb6" />
			<test_Parameter_BinaryData
				result="{substring(codepoints-to-string($octects1),1,40)}" 
				expect="-----BEGIN CERTIFICATE-----&#xD;&#xA;MIIGODCCBb6" />

			<xsl:variable name="b64_1" select="map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'SSL_Cert', 'BINARYDATAB64'))" />
			<xsl:variable name="b64_2" select="map:get($pdS4, ('Parameter', 'SSL_Cert', 'BINARYDATAB64'))" />
			<test_Parameter_BinaryDataB64
				result="{substring($b64_1,1,40)}" 
				expect="LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpN" />
			<test_Parameter_BinaryDataB64
				result="{substring($b64_2,1,40)}" 
				expect="LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpN" />
				
			<test_PartnerIdOfAuthorizedUser
				result="{map:get($COCO_PartnerDirectory, ('PartnerIdOfAuthorizedUser', 'user1'))}" 
				expect="S4H400" />
			
		</results>
	</xsl:template>

</xsl:stylesheet>
