package com.harrison.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.harrison.httpserver.core.io.ReadFileException;
import com.harrison.httpserver.core.io.WebRootHandler;
import com.harrison.httpserver.core.io.WebRootNotFoundException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // ??
class WebRootHandlerTest {

  private WebRootHandler webRootHandler;
  private Method requestedPathEndsWithSlash;
  private Method requestedPathExistsInsideWebroot;

  @BeforeAll
  private void beforeClass() throws NoSuchMethodException, SecurityException, WebRootNotFoundException {
    webRootHandler = new WebRootHandler("examplePages");
    Class<WebRootHandler> cls = WebRootHandler.class;

    requestedPathEndsWithSlash = cls.getDeclaredMethod("requestedPathEndsWithSlash", String.class);
    requestedPathEndsWithSlash.setAccessible(true);

    requestedPathExistsInsideWebroot = cls.getDeclaredMethod("requestedPathExistsInsideWebroot", String.class);
    requestedPathExistsInsideWebroot.setAccessible(true);
  }

  @Test
  void testInvalidPageGetPageByteArray() {
    try {
      byte[] pageByteArray = webRootHandler.getFileAsByteArray("../LICENSE");
      fail();
    } catch (FileNotFoundException e) {
      // pass
    } catch (ReadFileException e) {
      fail(e);
    }
  }

  @Test
  void testGetPageByteArray() {
    try {
      byte[] pageByteArray = webRootHandler.getFileAsByteArray("/");
      assertTrue(pageByteArray.length > 0);
    } catch (FileNotFoundException e) {
      fail(e);
    } catch (ReadFileException e) {
      fail(e);
    }
  }

  @Test
  void checkProperMimeTypeForHTMLFile() {
    try {
      String mimeType = webRootHandler.getFileMimeType("/");
      assertEquals("text/html", mimeType);
    } catch (FileNotFoundException e) {
      fail(e);
    }
  }

  @Test
  void checkProperMimeTypeForOctetStream() {
    try {
      String mimeType = webRootHandler.getFileMimeType("favicon.ico");
      assertEquals("application/octet-stream", mimeType);
    } catch (FileNotFoundException e) {
      fail(e);
    }
  }

  @Test
  void recognizeInvalidPathEscapingWebroot() {
    boolean result;
    try {
      result = (Boolean) requestedPathExistsInsideWebroot.invoke(webRootHandler, "../LICENSE");
      assertFalse(result);
    } catch (IllegalAccessException e) {
      fail(e);
    } catch (IllegalArgumentException e) {
      fail(e);
    } catch (InvocationTargetException e) {
      fail(e);
    }
  }

  @Test
  void recognizeValidRelativePathInsideWebroot() {
    boolean result;
    try {
      result = (Boolean) requestedPathExistsInsideWebroot.invoke(webRootHandler, "./././index.html");
      assertTrue(result);
    } catch (IllegalAccessException e) {
      fail(e);
    } catch (IllegalArgumentException e) {
      fail(e);
    } catch (InvocationTargetException e) {
      fail(e);
    }
  }

  @Test
  void recognizePathEndsWithSlash() {
    try {
      boolean result = (Boolean) requestedPathEndsWithSlash.invoke(webRootHandler, "/");
      assertTrue(result);
    } catch (IllegalAccessException e) {
      fail(e);
    } catch (IllegalArgumentException e) {
      fail(e);
    } catch (InvocationTargetException e) {
      fail(e);
    }
  }

  @Test
  void recognizePathDoesntEndWithSlash() {
    try {
      boolean result = (Boolean) requestedPathEndsWithSlash.invoke(webRootHandler, "index.html");
      assertFalse(result);
    } catch (IllegalAccessException e) {
      fail(e);
    } catch (IllegalArgumentException e) {
      fail(e);
    } catch (InvocationTargetException e) {
      fail(e);
    }
  }

  @Test
  void tryValidRelativeWebrootPath() {
    try {
      WebRootHandler webRootHandler = new WebRootHandler("examplePages");
    } catch (WebRootNotFoundException e) {
      fail();
    }
  }

  @Test
  void tryInvalidWebroot() {
    try {
      WebRootHandler webRootHandler = new WebRootHandler("NonExistentWebRoot");
      fail();
    } catch (WebRootNotFoundException e) {
      // pass
    }
  }
}
