Nuxeo Automation operation to populate the baseline of a configuration.
Works with Studio Application template : configuration-management
The operation  is launched from a baseline doc type (sub sub level of a Configuration doc  type).
It recreates (recursively) the tree of the Configuration and create a proxy for the last major version of all documents.
So the baseline is finally a snapshot of all last major version of documents.