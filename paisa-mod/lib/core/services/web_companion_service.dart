import 'dart:io';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final webCompanionServiceProvider = Provider<WebCompanionService>((ref) {
  return WebCompanionService();
});

class WebCompanionService {
  HttpServer? _server;
  bool _isRunning = false;
  int _port = 8080;

  bool get isRunning => _isRunning;
  int get port => _port;

  Future<bool> startServer({int port = 8080}) async {
    try {
      _port = port;
      _server = await HttpServer.bind(InternetAddress.anyIPv4, port);
      _isRunning = true;
      _server!.listen(_handleRequest);
      return true;
    } catch (e) {
      _isRunning = false;
      return false;
    }
  }

  Future<void> stopServer() async {
    await _server?.close(force: true);
    _isRunning = false;
  }

  Future<void> _handleRequest(HttpRequest request) async {
    // Add CORS headers
    request.response.headers.add('Access-Control-Allow-Origin', '*');
    request.response.headers.add('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    request.response.headers.add('Access-Control-Allow-Headers', 'Origin, Content-Type, Accept');

    if (request.method == 'OPTIONS') {
      request.response.statusCode = HttpStatus.ok;
      await request.response.close();
      return;
    }

    final path = request.uri.path;
    if (path == '/' || path == '/index.html') {
      try {
        final content = await rootBundle.loadString('assets/web_companion/index.html');
        request.response
          ..headers.contentType = ContentType.html
          ..write(content);
      } catch (e) {
        request.response
          ..statusCode = HttpStatus.internalServerError
          ..write('Error loading web companion interface: $e');
      }
    } else if (path == '/api/bootstrap') {
      request.response
        ..headers.contentType = ContentType.json
        ..write(jsonEncode({
          'status': 'ok',
          'app': 'Paisa Unrestricted',
          'version': '6.0.0',
          'features': {
            'pro_unlocked': true,
            'web_companion': true,
            'receipts': true,
            'unlimited_accounts': true,
            'unlimited_budgets': true,
            'unlimited_goals': true
          }
        }));
    } else if (path == '/api/ping') {
      request.response
        ..headers.contentType = ContentType.json
        ..write(jsonEncode({'status': 'online', 'timestamp': DateTime.now().toIso8601String()}));
    } else {
      request.response
        ..statusCode = HttpStatus.notFound
        ..write('Endpoint not found');
    }
    await request.response.close();
  }
}
