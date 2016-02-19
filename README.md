sap-hcp-deployer
================

This plugin is written for uploading applications to SAP Hana Cloud Platform.

Pre-requisites
==============

The SAP Neo SDK should be downloaded ( from [here](https://tools.hana.ondemand.com/#cloud)) & installed.

Installation guidelines
=======================

- Download & install Jenkins from [here](https://jenkins-ci.org/)

- Generate the `*.hpi` file by executing `mvn package`. The file will be found in `target/` directory.

- Put the file in `<jenkins-installation-directory>/plugins` directory.

- Restart jenkins with `jenkins-url/restrart` url.

Thats it! You are ready to go..

Jenkins compatibility
=====================

works with version >= 1.647

Issues
======

Please raise issues [here](https://github.com/jenkinsci/sap-hcp-deployer-plugin).