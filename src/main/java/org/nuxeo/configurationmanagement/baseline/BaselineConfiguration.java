/**
 * 
 */

package org.nuxeo.configurationmanagement.baseline;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.automation.core.collectors.BlobCollector;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.VersionModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelIteratorImpl;

/**
 * @author fvadon
 */

@Operation(id = BaselineConfiguration.ID, category = Constants.CAT_SERVICES, label = "Baseline configuration", description = "This operation sets the baseline from the actual and current configuration. Should only be run with configuration management")
public class BaselineConfiguration {

	public static final String ID = "baseline_configuration";
	private static final Log log = LogFactory
			.getLog(BaselineConfiguration.class);

	@Context
	protected CoreSession documentManager;

	@OperationMethod
	public DocumentModel run(DocumentModel input) {
		log.info("Creating Baseline");
		try {
			DocumentModel currentBaseline = input;
			DocumentModel baselineLibrary = documentManager.getDocument(input
					.getParentRef());
			DocumentModel configuration = documentManager
					.getDocument(baselineLibrary.getParentRef());

			this.baseLineFolder(configuration, currentBaseline);

		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return input;
	}

	@OperationMethod
	protected void baseLineFolder(DocumentModel configurationNode,
			DocumentModel baselineNode) {
		/*
		 * Copy the confNode into the baselineNode and recurse with the
		 * confNodeChild and the newly created baseLineNode
		 */

		if (configurationNode.isFolder()) {
			try {
				// Get the children
				DocumentModelList configurationNodeChildren = documentManager
						.getChildren(configurationNode.getRef());
				// Check that children exists, that they are folderich, and not
				// baseline library
				if (!configurationNodeChildren.isEmpty()) {
					Iterator<DocumentModel> iterator = configurationNodeChildren
							.iterator();
					while (iterator.hasNext()) {
						DocumentModel nextConfigurationChild = iterator.next();
						if (!(nextConfigurationChild.getType()
								.equals("Baseline_Library"))) {
							DocumentModel newBaselineNode;

							if (nextConfigurationChild.isFolder()) {

								// Create the new baseline node folderish doc
								// from the conf node and copy to its new path
								newBaselineNode = documentManager
										.createDocumentModel(baselineNode
												.getPathAsString(),
												nextConfigurationChild
														.getName(),
												nextConfigurationChild
														.getType());
								newBaselineNode
										.copyContent(nextConfigurationChild);
								newBaselineNode = documentManager
										.createDocument(newBaselineNode);
								// Run baseLineFolder on each newly created
								// children folder
								this.baseLineFolder(nextConfigurationChild,
										newBaselineNode);
							}
							//Not a folder, so we need the last major version of the document.
							// If not deleted and the last major version
							else if (!(nextConfigurationChild
									.getCurrentLifeCycleState()
									.equals("deleted"))) {
								if (!(nextConfigurationChild
										.isLatestMajorVersion())) {
									// Get all version of the doc and look for
									// the latest major
									List<DocumentModel> configurationChildVersionList = documentManager
											.getVersions(nextConfigurationChild
													.getRef());
									Iterator<DocumentModel> versionListIterator = configurationChildVersionList
											.iterator();
									DocumentModel latestMajor = null;
									boolean lastVersionFound = false;
									while (versionListIterator.hasNext()
											&& !lastVersionFound) {
										latestMajor = versionListIterator
												.next();
										lastVersionFound = (latestMajor
												.isLatestMajorVersion() && !(latestMajor.getCurrentLifeCycleState().equals("deleted")));
									}
									// if one of the version in the latest
									// major, create a proxy for in the node
									// baseline.
									if (lastVersionFound) {
										newBaselineNode = documentManager
												.createProxy(
														latestMajor.getRef(),
														baselineNode.getRef());
									}
								}
							} else { // The node version is already the latest
										// version, so proxy it to the
										// baseline folder.
								newBaselineNode = documentManager.createProxy(
										nextConfigurationChild.getRef(),
										baselineNode.getRef());
							}

						}

					}

				}

			} catch (ClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
