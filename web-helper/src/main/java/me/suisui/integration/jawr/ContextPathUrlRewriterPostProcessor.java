package me.suisui.integration.jawr;

import java.io.IOException;

import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;
import net.jawr.web.resource.bundle.postprocess.impl.CSSURLPathRewriterPostProcessor;

public class ContextPathUrlRewriterPostProcessor extends CSSURLPathRewriterPostProcessor {
    @Override
	protected StringBuffer doPostProcessBundle(BundleProcessingStatus status, StringBuffer bundleData)
            throws IOException {
        // Retrieve the full bundle path, so we will be able to define the
        // relative path for the css images
        String fullBundlePath = getFinalFullBundlePath(status);
        ContextPathUrlRewriter urlRewriter = new ContextPathUrlRewriter(status);
        return urlRewriter.rewriteUrl(status.getLastPathAdded(), fullBundlePath, bundleData.toString());
    }
}
