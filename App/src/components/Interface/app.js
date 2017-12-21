const express = require('express');
const mysql = require('mysql');

const db = mysql.createConnection({
    host:'35.190.165.119',
    user:'root',
    password:'root',
    database:'dustin'
});

//connect
db.connect((err) => {
    if(err) {
        throw err;
    }
    console.log('connected');
});

const app = express();

app.listen('3004', () => {
   console.log('server started on 3004');
});

app.get('/Mapdata', (req,res) => {
    let sql = 'select * from location3';
    let query = db.query(sql, (err, results) => {
        if(err) throw err;
        console.log(results);
        res.send(results);
    })
});

app.get('/tweetCount', (req,res) => {
    let sql = 'select * from tweetCount where time = CURDATE()';
    let query = db.query(sql, (err, results) => {
        if(err) throw err;
        console.log(results);
        res.send(results);
    })
});

app.get('/warning', (req,res) => {
    let sql = 'select * from NDWarning';
    let query = db.query(sql, (err, results) => {
        if(err) throw err;
        console.log(results);
        res.send(results);
    })
});
