ConLL to NIF
=================

Goal of this project is to convert sufficiently arbitrary, depnedency annotated ConLL corpora into NIF (nlp2rdf.org).

#Initial version

This repository contains a maven project with some java code. At the moment, it is able to parse the ConLL format and output a turtle string in NIF. An example resource is provided in /resources/sentence.conll. Start via 

````
ConLLToNIFCLI $prefixUri $inputfile

````
