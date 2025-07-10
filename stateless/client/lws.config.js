module.exports = {
  rewrite: [
    { from: "/login", "to": "http://localhost:8080/login" },
    { from: "/logout", "to": "http://localhost:8080/logout" },
    { from: "/authenticate", "to": "http://localhost:8080/authenticate" },
	  { from: "/admin", "to": "http://localhost:8080/admin" },
    { from: "/message", "to": "http://localhost:8080/message" },
    { from: "/disable", "to": "http://localhost:8080/disable" },
    { from: "/enable", "to": "http://localhost:8080/enable" },
    { from: "/isEnabled", "to": "http://localhost:8080/isEnabled" },
  ],
  logFormat: 'stats',
  hostname: 'localhost',
  port: 1234,
  directory: 'dist/app/browser',
  open: true
};
