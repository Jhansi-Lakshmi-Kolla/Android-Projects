var http = require('http');
var io = require('socket.io');
var server = http.createServer();

/*var h = new Object(); // or just {}
h['one'] = 1;
h['two'] = 2;
h['three'] = 3;
// show the values stored
for (var k in h) {
    // use hasOwnProperty to filter out keys from the Object.prototype
    if (h.hasOwnProperty(k)) {
        alert('key is: ' + k + ', value is: ' + h[k]);
    }
}
 
alert('size of hash table ' + h.length);*/
var clients = {};
var clientLocations = {};

var server = http.createServer(function(req,res){
	res.writeHead(200);
	res.end('Hello Http');
});

console.log( 'simply printing');

server.listen(8800,'0.0.0.0');
var listener = io.listen(server);
listener.sockets.on('connection', function(socket){
	
	socket.emit('message', 'only to sender');
	listener.emit('message', 'all including sender');
	
	socket.on('register', function(data){
        console.log( 'registering the socket with id ', socket.id, ' username ; ', data);
		clients[data] = socket.id;
		socket.broadcast.to(socket.id).emit('message', 'for your eyes only');
		console.log(clients);
		
    });
	
	socket.on('disconnect', function(){
        console.log( socket.name + ' has disconnected from the chat.' + socket.id);
		delete clients[clients.getKeyByValue(socket.id)];
		console.log(clients);
    });
	
	socket.on('location_send', function(data) {
		console.log('someone sent location', data.latitude, ',', data.longitude, ' to ', data.receiver);
		//clientLocations[clients.getKeyByValue(socket.id)] = data.latitude.concat(',').concat(data.longitude);
		socket.broadcast.to(clients[data.receiver]).emit('location_receive', {'latitude':data.latitude, 'longitude':data.longitude,'type':data.type});
	});
	
	socket.on('confirm_direct', function(data) {
		console.log('direct confirmation request by ', data.from, ' to ', data.to);
		//clientLocations[clients.getKeyByValue(socket.id)] = data.latitude.concat(',').concat(data.longitude);
		socket.broadcast.to(clients[data.to]).emit('confirm_direct', {'from':data.from});
	});
	
	socket.on('stop_direct', function(data) {
		console.log('direct stop request by ', data.from, ' to ', data.to);
		//clientLocations[clients.getKeyByValue(socket.id)] = data.latitude.concat(',').concat(data.longitude);
		socket.broadcast.to(clients[data.to]).emit('stop_direct', {'from':data.from});
	});
	
	socket.on('send_direction', function(data) {
		console.log('directions', data.directions, ' sent by ', data.from, ' to ', data.to);
		//clientLocations[clients.getKeyByValue(socket.id)] = data.latitude.concat(',').concat(data.longitude);
		socket.broadcast.to(clients[data.to]).emit('receive_direction', {'from':data.from, 'directions':data.directions});
	});
	
	
});

Object.prototype.getKeyByValue = function( value ) {
    for( var prop in this ) {
        if( this.hasOwnProperty( prop ) ) {
             if( this[ prop ] === value )
                 return prop;
        }
    }
}

var test = {
   key1: 42,
   key2: 'foo'
};


//listener.sockets.on('location_send', function(socket){
//	console.log('someone sent location');
//	var socket = clients[sId];
//	socket.emit('message', 'I received the location from you');
//});

//setInterval(function(){
 //   console.log('hello world');
//}, 1000);