var express = require("express");
var mysql = require('mysql');
var app = express();
app.use(express.static(__dirname + '/'));
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs = require("fs");
server.listen(process.env.PORT || 3000);

var con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "orderfood"
});

io.sockets.on('connection', function (socket) {

  console.log("NOTICE: NEW USER CONNECTED! " + socket.id);

  socket.on('CLIENT_SEND_REQUEST_LOGIN', function(data){
  	var userAndPass = data;
    //socket.un = data;
    console.log(socket.un);
  	checkUserAndPass(userAndPass,socket);
  });

   socket.on('REQUEST_BOOK', function(data){
	 getListTable(data,socket);
  });

    socket.on('LOG_OUT', function(data){
  	logOut(data,socket);
  });

    socket.on('CLIENT_SEND_REQUEST_DRINNK', function(data){
  	  getMenuDrink(socket);
  });

    socket.on('CLIENT_SEND_REQUEST_FOOD', function(data){
      getMenuFood(socket);
  });

    socket.on('CLIENT_SEND_TEMP_BILL', function(data){
      //console.log(data);
      insertTempBill(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_BILL', function(data){
      //console.log(data);
      getBillExists(socket,data);
  });

    socket.on('DELETE_BILL', function(data){
      //console.log(data);
      deleteBill(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_LIST_BILL', function(data){
      //console.log(data);
      getListBill(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_LIST_ITEM_BILL', function(data){
      //console.log(data);
      getListItemBill(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_LIST_STAFF', function(data){
      //console.log(data);
      getListStaff(socket);
  });

    socket.on('CLIENT_SEND_IMAGE_STAFF', function(data){
      //console.log(data);
      serverSendResultUpImage(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_INSERT_STAFF', function(data){
      //console.log(data);
      insertStaff(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_DELETE_STAFF', function(data){
      //console.log(data);
      deleteStaff(socket,data);
  });

    socket.on('CLIENT_SEND_REQUEST_EDIT_STAFF', function(data){
      //console.log(data);
      editStaff(socket,data);
      console.log(data);
  });

});


function checkUserAndPass(userPass,socket)
{
  con.query("SELECT * FROM nhanvien WHERE userPass = '"+userPass+"'", function (err, result, fields) {
    if (err) throw err;
    socket.emit('SERVER_SEND_RESULT',result);
    if (result.length == 1) {
    	con.query("UPDATE nhanvien SET online = 1 WHERE userPass = '"+userPass+"'", function (err, result, fields) {
    	if (err) throw err;});
    }
  });
	//console.log(userPass);
}

function getListTable(num,socket)
{

  if (num == -1) {
      con.query("SELECT * FROM danhsachban", function (err, result, fields) {
    if (err) throw err;
    io.sockets.emit('SERVER_SEND_LIST_TABLE',result);
    //console.log(result);
  });
  }
  else
  {
      con.query("UPDATE danhsachban SET tinhTrang = 1 WHERE tenBan = '"+num+"'", function (err, result, fields) {
      if (err) throw err;
      //console.log(result);
      });

      con.query("SELECT * FROM danhsachban", function (err, result, fields) {
      if (err) throw err;
      io.sockets.emit('SERVER_SEND_LIST_TABLE',result);
      //console.log(result);
      });
  }
}

function logOut(idStaff,socket)
{
	con.query("UPDATE nhanvien SET online = 0 WHERE idNhanVien = '"+idStaff+"'", function (err, result, fields) {
    if (err) throw err;
    //console.log(result);
  });

  con.query("SELECT * FROM `nhanvien` WHERE chucVu = 'BB'", function (err, result, fields) {
    if (err) throw err;
      io.sockets.emit('SERVER_SEND_LIST_STAFF',result);
      //console.log(result);
  });
}

function getMenuDrink(socket)
{
	con.query("SELECT nhommonan.tenNhom, danhsachmonan.tenMonAn, danhsachmonan.gia, danhsachmonan.tenDVTinh"+
		", danhsachmonan.tinhTrang, danhsachmonan.anhMonAn "+
		"FROM nhommonan INNER JOIN danhsachmonan ON nhommonan.tenNhom = danhsachmonan.tenNhom WHERE nhommonan.loai = 'drink'", function (err, result, fields) {
    if (err) throw err;
    socket.emit('SERVER_SEND_MENU_DRINK',result);
    //console.log(result);
  });
}

function getMenuFood(socket)
{
  con.query("SELECT nhommonan.tenNhom, danhsachmonan.tenMonAn, danhsachmonan.gia, danhsachmonan.tenDVTinh"+
    ", danhsachmonan.tinhTrang, danhsachmonan.anhMonAn "+
    "FROM nhommonan INNER JOIN danhsachmonan ON nhommonan.tenNhom = danhsachmonan.tenNhom WHERE nhommonan.loai = 'food'", function (err, result, fields) {
    if (err) throw err;
    socket.emit('SERVER_SEND_MENU_FOOD',result);
    //console.log(result);
  });
}

function insertTempBill(socket,que)
{
    var arrQue = [];
    arrQue = que.split(";");
    for(var i=0; i<arrQue.length; i++)
    {
      con.query(arrQue[i], function (err, result, fields) {
      if (err) throw err;
      });
    }

}

function getBillExists(socket,table)
{
  con.query("SELECT * FROM `hoadonchothanhtoan` INNER JOIN `danhsachmonan` ON hoadonchothanhtoan.tenMonAn = danhsachmonan.tenMonAn WHERE `tenBan` = "+table, function (err, result, fields) {
  if (err) throw err;
  socket.emit("SERVER_SEND_BILL",result);
  //console.log(result);
  });
  
}

function deleteBill(socket,que)
{
    var arr = [];
    arr = que.split(";");
    for(var i=0; i<arr.length; i++)
    {
      con.query(arr[i], function (err, result, fields) {
      if (err) throw err;
      //console.log(result);
      });
    }

    con.query("SELECT * FROM danhsachban", function (err, result, fields) {
    if (err) throw err;
      io.sockets.emit('SERVER_SEND_LIST_TABLE',result);
      //console.log(result);
    });
}

function getListBill(socket,idStaff)
{
    con.query("SELECT * FROM `thongkehoadon` WHERE idNhanVien = '"+idStaff+"'", function (err, result, fields) {
    if (err) throw err;
      socket.emit('SERVER_SEND_LIST_BILL',result);
      //console.log(result);
    });
}

function getListItemBill(socket,idBill)
{
    con.query("SELECT * FROM `dsmonantheohd` WHERE idHoaDon = '"+idBill+"'", function (err, result, fields) {
    if (err) throw err;
      socket.emit('SERVER_SEND_LIST_ITEM_BILL',result);
      //console.log(result);
    });
}

function getListStaff(socket)
{
    con.query("SELECT * FROM `nhanvien` WHERE chucVu = 'BB'", function (err, result, fields) {
    if (err) throw err;
      io.sockets.emit('SERVER_SEND_LIST_STAFF',result);
      //console.log(result);
    });
}

function getFileNameImage(id)
{
  return "image/staff/" + id.substring(2) + getMilis() + ".png";
}

function getMilis()
{
  var date = new Date();
  var milis = date.getTime();
  return milis;
}

function serverSendResultUpImage(socket,data)
{
  var url = getFileNameImage(socket.id);
  fs.writeFile(url, data, function(err) {
    if(err) {
        return console.log(err);
    }
    socket.emit('SERVER_SEND_RESULT_UP_IMAGE',"/"+url);
  });
}

function insertStaff(socket,que)
{
    con.query(que, function (err, result, fields) {
    if (err) {
      console.log(err);
    }
    else{
      io.sockets.emit('SERVER_SEND_LIST_STAFF',result);
      console.log(result);
    }

    });
}

function deleteStaff(socket,que)
{
    con.query(que, function (err, result, fields) {
    if (err) {
      socket.emit('SERVER_SEND_RESULT_DELETE_STAFF',"false");
    }
    else{
        con.query("SELECT * FROM `nhanvien` WHERE chucVu = 'BB'", function (err, result, fields) {
        if (err) throw err;
          io.sockets.emit('SERVER_SEND_LIST_STAFF',result);
          //console.log(result);
        });
    }

    });
}

function editStaff(socket,que)
{
    con.query(que, function (err, result, fields) {
    if (err) {
      socket.emit('SERVER_SEND_RESULT_EDIT_STAFF',"false");
      console.log(err);
    }
    else{
        con.query("SELECT * FROM `nhanvien` WHERE chucVu = 'BB'", function (err, result, fields) {
        if (err) throw err;
          io.sockets.emit('SERVER_SEND_LIST_STAFF',result);
          //console.log(result);
        });
    }
    });
}




