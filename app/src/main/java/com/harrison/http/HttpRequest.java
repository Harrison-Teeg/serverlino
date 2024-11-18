package com.harrison.http;

public class HttpRequest extends HttpMessage {

  private HttpMethod method;
  private String requestTarget;
  private String originalHttpVersion; // original request literal
  private HttpVersion bestSupportedHttpVersion;

  public String getRequestTarget() {
    return requestTarget;
  }

  public HttpVersion getHttpVersion() {
    return bestSupportedHttpVersion;
  }

  public HttpMethod getMethod() {
    return method;
  }

  void setMethod(String methodName) throws HttpParsingException {
    for (HttpMethod method : HttpMethod.values()) {
      if (methodName.equals(method.name())) {
        this.method = method;
        return;
      }
    }
    throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
  }

  void setRequestTarget(String target) throws HttpParsingException {
    if (target != null && target.length() > 0) {
      this.requestTarget = target;
    } else {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    }
  }

  void setHttpVersion(String literal) throws HttpParsingException {
    if (literal != null && literal.length() > 0) {
      this.bestSupportedHttpVersion = HttpVersion.getCompatibleHttpVersion(literal);
    } else {
      throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
    }
  }
}
