package com.harrison.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {

  private String webRootName;
  private File webRoot;

  public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
    webRootName = webRootPath;
    webRoot = new File(webRootPath);
    if (!webRoot.exists() || !webRoot.isDirectory()) {
      throw new WebRootNotFoundException("The webRoot path provided didn't lead to a directory.");
    }
  }

  private boolean requestedPathEndsWithSlash(String path) {
    return path.endsWith("/");
  }

  private boolean requestedPathDeclaresFiletype(String path) {
    return path.contains(".");
  }

  private boolean requestedPathExistsInsideWebroot(String path) {
    File file = new File(webRoot, path);

    if (!file.exists()) {
      return false;
    }

    try {
      if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())) {
        return true;
      }
      return false;
    } catch (IOException e) {
      return false;
    }
  }

  public String getFileMimeType(String path) throws FileNotFoundException { // Research mimeTypes..., URLConnection
                                                                            // mimeType tables..?
    if (requestedPathEndsWithSlash(path)) {
      path = path + "index.html";
    } else if (!requestedPathDeclaresFiletype(path)) {
      path = path + ".html";
    }

    if (!requestedPathExistsInsideWebroot(path))
      throw new FileNotFoundException();

    File file = new File(webRoot, path);

    String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

    if (mimeType == null) {
      return "application/octet-stream"; // default catch-all for other types
    }

    return mimeType;
  }

  public byte[] getFileAsByteArray(String path) throws FileNotFoundException, ReadFileException {
    if (requestedPathEndsWithSlash(path)) {
      path = path + "index.html";
    } else if (!requestedPathDeclaresFiletype(path)) {
      path = path + ".html";
    }

    if (!requestedPathExistsInsideWebroot(path))
      throw new FileNotFoundException();

    File file = new File(webRoot, path);
    FileInputStream fileInputStream = new FileInputStream(file);
    byte[] pageByteArray = new byte[(int) file.length()];

    try {
      fileInputStream.read(pageByteArray);
      fileInputStream.close();
    } catch (IOException e) {
      throw new ReadFileException(e);
    }

    return pageByteArray;
  }

  public String getWebrootName() {
    return webRootName;
  }

}
