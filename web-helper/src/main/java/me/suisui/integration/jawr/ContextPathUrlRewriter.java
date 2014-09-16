package me.suisui.integration.jawr;

import java.io.IOException;

import net.jawr.web.resource.bundle.css.CssImageUrlRewriter;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;

public class ContextPathUrlRewriter extends CssImageUrlRewriter {
    private BundleProcessingStatus status;

    public ContextPathUrlRewriter(BundleProcessingStatus status) {
        super(status.getJawrConfig());
        this.status = status;
    }

    @Override
    protected String getRewrittenImagePath(String originalCssPath, String newCssPath, String url) throws IOException {
        if(!url.startsWith("/") && url.endsWith(".htc")){
            String fullImgPath = PathNormalizer.concatWebPath(originalCssPath, url);
            String contextPath = status.getJawrConfig().getContext().getContextPath();
            return PathNormalizer.joinDomainToPath(contextPath, fullImgPath);
        }else{
            return url;
        }
            
    }
}
