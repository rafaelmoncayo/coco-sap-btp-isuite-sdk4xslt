# COCO - Cloud Integration SDK extension for XSLT
This is a package for SAP Cloud Integration that enables the call of some **SDK functionalities of Cloud Integration from XSLT**. And also provides you a sample of how to call **custom Java code** from XSLT.

## Brief description of the challenge
Java classes/mehtods can be called from Saxon XSLT processor EE. You can look at Saxonica documentation [Writing reflexive extension functions in Java](https://www.saxonica.com/html/documentation10/extensibility/functions/index.html).

While it is possible to make calls to Java methods from Saxon XSLT, doing so from the SAP Cloud Integration environment offers an additional challenge. When Saxon compiles an XSLT program, it does reflection but it only has visibility on Java SDK classes, no to custom code neither Cloud Integration SDK API like com.sap.it.api.mapping.ValueMappingApi. If you import jars into your Integration Flow or at a Script Collection, Saxon will not recognice your custom code nor external libraries when it compiles your XSLT.

## What if...?
What if we use a custom class that implements an interface of Java SDK and put it as a property in the integration flow? For example java.util.Map. Map has two generic methods: get(value) and put(key,value) that look generic enough for many scenarios. So if we wrap our java logic in a class that implements Map... Eureka! our custom code could be called from an XSLT program.

## What to find in this package
Import package [COCO Java Extensions for XSLT.zip](.//COCO%20Java%20Extensions%20for%20XSLT.zip) into your Cloud Integration subaccount and you will be availabe to see:

### 1. COCO Call custom java from XSLT sample 
Iflow that shows how call custom Java code from XSLT. 

### 2. COCO - SDK extension for XSLT
_(in process)_ A Groovy Script Collection that will generate java wrappers that can be used from XSLT to do:
   - Value Mapping
   - Retrieve Partner Directory parameters
   - Write custom headers
