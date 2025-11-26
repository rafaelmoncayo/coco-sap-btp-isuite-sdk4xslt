# COCO - Cloud Integration SDK extension for XSLT
This is package for SAP Cloud Integration that enables the call of some **SDK functionalities of Cloud Integration from XSLT**. And also provides you a sample of how to call **custom Java code** from XSLT.

## Brief Description
Java classes/mehtods can be called from Saxon XSLT processor EE. You can look at Saxonica documentation [Writing reflexive extension functions in Java](https://www.saxonica.com/html/documentation10/extensibility/functions/index.html).

Besides you can call java classes from XSLT doing it at SAP Cloud Integration offers an additional challenge. When Saxon compiles an XSLT program, it does reflextion but it just have visibility to Java SDK classes no custom code neither Cloud Integration SDK API like com.sap.it.api.mapping.ValueMappingApi. If you import jars in the Integration Flow or at a Script Collection, Saxon will not be aware of your custom code neither used external libraries when it compiles your XSLT.

## What if...?
What if we use a class that inherits or implements a class or interface of Java SDK and put it as a property in the integration flow? For example java.util.Map. Map has two generic methods: get(value) and put(key,value) that look generic enough for many scenarios. So if we wrap our java logic in a class that implements Map... Eureka! our custom code could be called from an XSLT program.

## What to find in this package
### 1. COCO Call custom java from XSLT sample 
Iflow that shows how call custom Java code from XSLT. 

### 2. COCO - SDK extension for XSLT
_(in process)_ A groovy script that will generate java wrappers that can be used from XSLT to do:
   - Value Mapping
   - Retrieve Partner Directory parameters
   - Write custom headers
