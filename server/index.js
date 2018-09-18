var express = require('express');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(8989, function() {
	console.log('Servidor corriendo en http://localhost:8080');
});

io.on('connection', function(socket) {
    console.log('Un cliente se ha conectado');
    socket.on('message', function(data) {
	console.log("Message",data);
	socket.emit("temperature",{"temperature":123});
    });
});
