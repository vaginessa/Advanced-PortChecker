package com.codedead.advanced.portchecker.utils;

import com.codedead.advanced.portchecker.interfaces.IRunnableHelper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// Unfortunately, this class is needed to circumvent Linux errors involving the Desktop class and browsing websites
// See https://stackoverflow.com/a/27880944 for more information
public final class RunnableSiteOpener implements Runnable {

    private final String url;
    private final IRunnableHelper iRunnableHelper;

    /**
     * Initialize a new RunnableSiteOpener
     *
     * @param url             The URL that should be opened
     * @param iRunnableHelper The {@link IRunnableHelper} interface that can be used to delegate messages
     */
    public RunnableSiteOpener(final String url, final IRunnableHelper iRunnableHelper) {
        if (url == null)
            throw new NullPointerException("URL cannot be null!");
        if (url.isEmpty())
            throw new IllegalArgumentException("URL cannot be empty!");

        this.url = url;
        this.iRunnableHelper = iRunnableHelper;
    }

    /**
     * Method that is invoked to run the task
     */
    @Override
    public void run() {
        try {
            Desktop.getDesktop().browse(new URI(url));
            if (iRunnableHelper != null) {
                iRunnableHelper.executed();
            }
        } catch (final IOException | URISyntaxException | UnsupportedOperationException ex) {
            if (iRunnableHelper != null) {
                iRunnableHelper.exceptionOccurred(ex);
            }
        }
    }
}
