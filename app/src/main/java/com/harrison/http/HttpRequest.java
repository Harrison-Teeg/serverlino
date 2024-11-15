package com.harrison.http;

public class HttpRequest extends HttpMessage {

  private HttpMethod method;
  private String requestTarget;
  private HttpVersion httpVersion;

  public String getRequestTarget() {
    return requestTarget;
  }

  public HttpVersion getHttpVersion() {
    return httpVersion;
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
      this.httpVersion = HttpVersion.getCompatibleHttpVersion(literal);
    } else {
      throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
    }
  }
}
