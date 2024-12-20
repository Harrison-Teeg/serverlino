package com.harrison.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {
  // This has to be in order from lowest to highest version
  HTTP_1_1("HTTP/1.1", 1, 1);

  public final String LITERAL;
  public final int MAJOR;
  public final int MINOR;
  public static final int LITERAL_MAX_LENGTH; // this should be fine since by convention for HTTP/x.x: each x should be
                                              // one digit

  static {
    int len = -1;
    for (HttpVersion ver : HttpVersion.values()) {
      int literalLen = ver.name().length();
      if (literalLen > len) {
        len = literalLen;
      }
    }
    LITERAL_MAX_LENGTH = len;
  }

  HttpVersion(String literal, int major, int minor) {
    this.LITERAL = literal;
    this.MAJOR = major;
    this.MINOR = minor;
  }

  private static final Pattern httpVersionRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)$");

  public static HttpVersion getCompatibleHttpVersion(String literal) throws HttpParsingException {
    Matcher regexMatcher = httpVersionRegexPattern.matcher(literal);
    if (!regexMatcher.find() || regexMatcher.groupCount() != 2) {
      throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
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
    return bestCompatible;
  }
}
