package com.harrison.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {
  // This has to be in order from lowest to highest version
  HTTP_1_1("HTTP/1.1", 1, 1);

  public final String LITERAL;
  public final int MAJOR;
  public final int MINOR;

  HttpVersion(String literal, int major, int minor) {
    this.LITERAL = literal;
    this.MAJOR = major;
    this.MINOR = minor;
  }

  private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)$");

  public static HttpVersion getCompatibleHttpVersion(String literal) throws HttpParsingException {
    Matcher regexMatcher = httpVersionRegexPattern.matcher(literal);
    if (!regexMatcher.find() || regexMatcher.groupCount() != 2) {
      throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
    }

    int major = Integer.parseInt(regexMatcher.group("major"));
    int minor = Integer.parseInt(regexMatcher.group("minor"));

    HttpVersion bestCompatible = null;
    for (HttpVersion ver : HttpVersion.values()) {
      if (ver.LITERAL.equals(literal)) {
        return ver;
      } else {
        if (ver.MAJOR == major) {
          if (ver.MINOR < minor) {
            bestCompatible = ver;
          }
        }
      }
    }
    if (bestCompatible == null) {
      throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
    }
    return bestCompatible;
  }
}
