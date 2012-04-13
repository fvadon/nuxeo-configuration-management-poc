# Nuxeo Configuration Management POC

This project is a Nuxeo Automation operation to populate the baseline of a configuration and is part of a example on how to use Nuxeo for configuration Management.

The operation works with Studio Application template : configuration-management
The operation  is launched from a baseline doc type (sub sub level of a Configuration doc  type).
It recreates (recursively) the tree of the Configuration and create a proxy for the last major version of all documents.
So the baseline is finally a snapshot of all last major version of documents.

The complete documentation on the configuration management template is available here:
http://doc.nuxeo.com/x/b4Ce

## How to build

You can build the BaselineConfiguraiton operation with:

	$ mvn clean install
  
# How to deploy

Copy/Paste your jar in a nuxeo distribution plugins folder (all distributions)

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>