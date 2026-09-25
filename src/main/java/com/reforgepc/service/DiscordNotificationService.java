package com.reforgepc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DiscordNotificationService {

  private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final String webhookUrl;

  public DiscordNotificationService(
      @Value("${discord.webhook-url:}") String webhookUrl) {
    this.webhookUrl = webhookUrl;
  }

  public void sendRequestLog(
      String method,
      String path,
      String username,
      int status,
      long duration) {
    if (webhookUrl == null || webhookUrl.isBlank()) {
      return;
    }

    String title = getTitle(status);
    int color = getColor(status);
    String timestamp = getDisplayTimestamp();

    String json = """
        {
          "embeds": [
            {
              "title": "%s",
              "color": %d,
              "fields": [
                {
                  "name": "Method",
                  "value": "`%s`",
                  "inline": true
                },
                {
                  "name": "Status",
                  "value": "`%d %s`",
                  "inline": true
                },
                {
                  "name": "User",
                  "value": "`%s`",
                  "inline": true
                },
                {
                  "name": "Endpoint",
                  "value": "`%s`",
                  "inline": false
                },
                {
                  "name": "Duration",
                  "value": "`%d ms`",
                  "inline": true
                },
                {
                  "name": "Time",
                  "value": "`%s`",
                  "inline": true
                }
              ]
            }
          ]
        }
        """.formatted(
        escapeJson(title),
        color,
        escapeJson(method),
        status,
        getStatusText(status),
        escapeJson(username),
        escapeJson(path),
        duration,
        escapeJson(timestamp));

    send(json);
  }

  public void sendErrorLog(
      String method,
      String path,
      String username,
      Throwable exception,
      long duration) {
    if (webhookUrl == null || webhookUrl.isBlank()) {
      return;
    }

    String exceptionName = exception.getClass().getSimpleName();

    String message = exception.getMessage();

    if (message == null || message.isBlank()) {
      message = "No error message available.";
    }

    String timestamp = getDisplayTimestamp();

    String json = """
        {
          "embeds": [
            {
              "title": "🔴 Unhandled Application Error",
              "color": 15158332,
              "fields": [
                {
                  "name": "Method",
                  "value": "`%s`",
                  "inline": true
                },
                {
                  "name": "User",
                  "value": "`%s`",
                  "inline": true
                },
                {
                  "name": "Duration",
                  "value": "`%d ms`",
                  "inline": true
                },
                {
                  "name": "Endpoint",
                  "value": "`%s`",
                  "inline": false
                },
                {
                  "name": "Exception",
                  "value": "`%s`",
                  "inline": false
                },
                {
                  "name": "Message",
                  "value": "```%s```",
                  "inline": false
                },
                {
                  "name": "Time",
                  "value": "`%s`",
                  "inline": true
                }
              ]
            }
          ]
        }
        """.formatted(
        escapeJson(method),
        escapeJson(username),
        duration,
        escapeJson(path),
        escapeJson(exceptionName),
        escapeJson(message),
        escapeJson(timestamp));

    send(json);
  }

  private String getDisplayTimestamp() {
    return LocalDateTime.now().format(DISPLAY_TIME_FORMAT);
  }

  private void send(String json) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(webhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();

    try {
      httpClient.sendAsync(
          request,
          HttpResponse.BodyHandlers.ofString());
    } catch (Exception ignored) {
      // Discord logging must never affect the application.
    }
  }

  private String getTitle(int status) {
    if (status >= 500) {
      return "🔴 Server Error";
    }

    if (status >= 400) {
      return "🟠 Client Error";
    }

    if (status >= 300) {
      return "🟡 Request Redirect";
    }

    return "🟢 Request";
  }

  private int getColor(int status) {
    if (status >= 500) {
      return 15158332;
    }

    if (status >= 400) {
      return 16753920;
    }

    if (status >= 300) {
      return 16776960;
    }

    return 5763719;
  }

  private String getStatusText(int status) {
    return switch (status) {
      case 200 -> "OK";
      case 201 -> "Created";
      case 204 -> "No Content";
      case 301 -> "Moved Permanently";
      case 302 -> "Found";
      case 303 -> "See Other";
      case 304 -> "Not Modified";
      case 400 -> "Bad Request";
      case 401 -> "Unauthorized";
      case 403 -> "Forbidden";
      case 404 -> "Not Found";
      case 405 -> "Method Not Allowed";
      case 409 -> "Conflict";
      case 422 -> "Unprocessable Entity";
      case 429 -> "Too Many Requests";
      case 500 -> "Internal Server Error";
      case 502 -> "Bad Gateway";
      case 503 -> "Service Unavailable";
      default -> "HTTP " + status;
    };
  }

  private String escapeJson(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r");
  }
}