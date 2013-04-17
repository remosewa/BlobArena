

/*
 * BlobSolver.js the main function
 * Author: Matt
 */

function Pos(r, c){
	this.row = r;
	this.col = c;
}

function ObjQueue(m, t){
	this.move = new Move();
	this.move = m;
	this.team = t;
}


function MoveExplorer(b, ms){
	this.processQueue = new Array();
	this.moves = new Array();
	this.shutdown = false;
	this.maxPrune = 2;
	this.totalEvaluations = 0;
	////("time = " + new Date().getTime());
	this.stopTime = new Date().getTime() + 500;
	this.startBoard = b;
	this.moves = ms;
	////("ms = " + ms.to);
	
	this.start = function(){
		////("startBoard = " + this.startBoard);
		this.moves = this.getMoves(this.startBoard, 0, null);
		////("moves3 = " + moves);
		this.totalEvaluations = 1;
		while(this.processQueue.length > 0 && !this.shutdown){
			
			var o = this.processQueue.shift();
		//	//("o = " + o);
			o.move.otherMoves = this.getMoves(o.move.board, o.team, null);
		//	//("o.move.othermoves = " + o.move.otherMoves);
			this.totalEvaluations += 1;
		}
	}
	
	this.getMoves = function(b, team, gmoves){
		if(gmoves != null && gmoves.length() > 0){
		//	var m = new Move();
			//for each(var m in gmoves){
			//gmoves.forEach(function(m){	
			for(var i = 0; i < gmoves.length; i++){
				var m = gmoves[i];	
				//("m = " + m);
				this.getMoves(m.board, nextTeam(m.team), m.otherMoves);
			}
			return gmoves;
		} else {
			var moves = [];
			var duplicateMoves = [];
			////("Else statement moves = " + moves);
			////("getMyblobs = " + b.getMyBlobs(team)[1]);
			//var blob = new Blob();
		//	for each(var blob in b.getMyBlobs(team)){
			//b.getMyBlobs(team).forEach(function(blob{
			//var blobArray = b.getMyBlobs(team);
			for(var index = 0; index < b.getMyBlobs(team).length; index++){
				var blob = b.getMyBlobs(team)[index];	
				////("for statement blob = " + blob);
				////("blob's get moves = "+blob.getMoves());
		//		//("the stop time = " + this.stopTime);
			//	//("this.stopTime = " + new Date().getTime());
				if(this.stopTime < new Date().getTime()){
					 this.shutdown = true;
					 //("moves1 = " + moves[0]);
					 return moves;
				 }
				//for each(var m in blob.getMoves()){
				for(var i = 0; i < blob.getMoves().length; i++){
					var m = blob.getMoves()[i];
		//			//(" The M = " + m.to.col);
					var isThere = false;
					for(var j=0; j < duplicateMoves.length; j++){
						if(m.to==duplicateMoves[j]){
							isThere = true;
							break;
						}
					}
					if(b.getDistance(m.from, m.to) != 1 || !isThere/*duplicateMoves.indexOf(m.to) === -1*/){
						if(b.getDistance(m.from, m.to) === 1){
							duplicateMoves.push(m.to);
						}
						m.score = b.getScore(m, team);
						var newb = b.makeMove(m.from, m.to, team, false);
						m.board = newb;
						m.team = team;
						var o = new ObjQueue(m, this.nextTeam(team));
						this.processQueue.push(o);
						moves.push(m);
					}
				}
			}
		}
		////("moves2 = " + moves);
		return moves;
	}
	
	this.nextTeam = function(team){
		if(team === 0){
			return 1;
		} else{
			return 0;
		}
	}
} 


function Move(from, to){
	this.from = new Pos();
	this.to = new Pos();
	this.score = 0;
	this.tempscore = 0;
	this.board = new Board();
	this.otherMoves = new Array();
	this.team = 0;
	
	this.from = from;
	this.to = to;
	
	this.getScore = function(){
		var checkedYet = false;
		var otherScore = 0;
		var m = new Move();
		//for(m in this.otherMoves){
		for(var i = 0; i < this.otherMoves.length; i++){
			m = this.otherMoves[i];
			var tscore = m.getScore();
			if(!checkedYet){
				otherScore = tscore;
				checkedYet = true;
			} else if(this.team == 0 && tscore < otherScore){
				otherScore = tscore;
				checkedYet = true;
			} else if(this.team != 0 && tscore > otherScore){
				otherScore = tscore;
				checkedYet = true;
			}
		}
		this.tempscore = this.score + otherScore;
		return (this.score + otherScore);
	}
}



function Board(){
	this.matrix = 
	[[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,],
	[,,,,,,,]]

	this.addBlob = function(p, team){
		this.matrix[p.row][p.col] = new Blob(this, p, team);
	}
	
	this.moveBlob = function(from, to){

		var b = this.getSpot(from);
		b.pos = to;
		this.matrix[from.row][from.col] = undefined;
		this.matrix[to.row][to.col]=b;
	}
	
	this.getSpot = function(p){
		////(" p = " +p.row + " " + p.col);
		////(" matrix = " + this.matrix[0][0]);

		return this.matrix[p.row][p.col];
	}
	
	this.makeMove = function(from, to, team, print){
		var newb = this.copyBoard();
		////("makemove from = "+ from.col + " to = " + to.col)
		if(this.getDistance(from, to) === 1){
			newb.addBlob(to, team);
		} else{

			newb.moveBlob(from,to);
		}
		for(var r = to.row - 1; r<=to.row+1; r+=1){
			for(var c = to.col -1; c<= to.col + 1; c+=1){
				if(r>=0 && c >= 0 && r<8 && c<8){
					var testb = newb.matrix[r][c];
					if(testb != null && testb.team !=team){
						testb.team = team;
					}
				}
			}
		}
		return newb;
	}
	
	this.canMoveTo = function(p){
		////("GET SPOT IS : " + this.getSpot(p));
		if(this.getSpot(p) === undefined){
			return true;
		} else{
			return false;
		}
	}
	
	this.getMyBlobs = function(team){
		var blist = new Array();
		for(var r =0; r<8; r+=1){
			for(var c = 0; c<8; c+=1){
				var testb = new Blob();
				testb = this.matrix[r][c];
				if(testb != null && testb.team == team){
					blist.push(testb);
				}
			}
		}
		return blist;
	}
	
	this.getDistance = function(p1, p2){
		var dist = Math.floor(Math.sqrt(Math.pow((p1.row-p2.row), 2) + Math.pow((p1.col - p2.col), 2)));
		return dist;
	}
	
	this.getScore = function(m, team){
		var f = m.from;
		var p = m.to;
		var score = 0;
		if(this.getDistance(f, p) === 1){
			score += 1;
		}
		for(var r = p.row - 1; r <= p.row + 1; r += 1){
			for(var c = p.col - 1; c <= p.col + 1; c+=1){
				if(r >= 0 && c >= 0 && r<8 && c<8){
					var testb = this.getSpot(new Pos(r, c));
					if(testb != null && testb.team != team){
						score != 1;
					}
				}
			}
		}
		if(team != 0){
			score = -score;
		}
		return score;
	}
	
	//this.addBlob = function(p, b){
	//	this.matrix[p.row][p.col] = b;
	//}
	
	this.copyBoard = function(){
		var newboard = new Board();
		for(var r = 0; r < 8; r+=1){
			for(var c = 0; c< 8; c+=1){
				if(this.matrix[r][c] != null){
					newboard.matrix[r][c] = this.matrix[r][c].copyBlob(newboard);
				}
			}
		}
		return newboard;
	}
	
	this.printBoard = function(){
		////("bye");
		for(var r = 0; r < 8; r+=1){
			var row = "";
			for(var c = 0; c<8; c += 1){
				var testb = this.matrix[r][c];
				if(testb === undefined){
					row += "0 ";
				} else if (testb.team === 0){
					row += "+ ";
				} else {
					row += "- ";
				}
			}
			//WScript.StdOut.WriteLine("row = " + row);
		}
	}
}

//var theboard = new Board();
////(theboard[0][0]);
////("hell235o");
//theboard.printBoard();



function Blob(b, p, t){
	this.board = b;
	this.pos = p;
	this.team = t;
	
	// returns an array of possible moves
	this.getMoves = function(){
		////("welcome to getmoves");
		var rlist = new Array();
		for(var r = this.pos.row - 2; r <= this.pos.row + 2; r+=1){
			for(var c = this.pos.col - 2; c <= this.pos.col + 2; c+=1){
				var testp = new Pos(r, c);
				if(r >= 0){
					////("CAN IT MOVE TO!?" + testp.row);
				}
				if(r >= 0 && c >= 0 && r < 8 && c < 8 && this.board.canMoveTo(testp)){
					var m = new Move(this.pos,testp);
				//	//("THE GETMOVES M = " +m);
					rlist.push(m);
				}
			}
		}
		////("rlist = " + rlist);
		return rlist;
	}
	
	this.copyBlob = function(b){
	//	var blobCopy = new Blob(b, )
		return(new Blob(b,new Pos(this.pos.row,this.pos.col),this.team));
	} 
}



function BlobSolver(){
	this.newBoard = function(movefirst){
		var board = new Board();
		if(!movefirst){
		board.addBlob(new Pos(0,0), 0);
		board.addBlob(new Pos(0,7), 0);
		board.addBlob(new Pos(7,0), 1);
		board.addBlob(new Pos(7,7), 1);
		}else{
		board.addBlob(new Pos(0,0), 1);
		board.addBlob(new Pos(0,7), 1);
		board.addBlob(new Pos(7,0), 0);
		board.addBlob(new Pos(7,7), 0);
		}
		
		return board;
	}
	
	this.getTreeDepth = function(m1){
		if(m1.otherMoves.length > 0){
			return 1 + this.getTreeDepth(m1.otherMoves[0]);
		} else {
			return 1;
		}
	}
	
/*	this.bestMove = function(moves){
		var bestscore = -50;
		var bestmove = new Move();
		var move = new Move();
		for( move in moves ){
			var score = move.getScore();
			if(score > bestscore){
				bestscore = score;
				bestmove = move;
			}
		}
		return bestmove;
	}
*/	
	this.subMoves = function(moves, b){
		var m = new Move();
		//for(m in moves){
		//("THE LENGTH OF MOVES IS = " + moves.length);
		for(var j = 0; j < moves.length; j++){
			//("doing subMOVES IAWHJEFUAWHEF");
			m = moves[j];
			if(b===m.board){
				return m.otherMoves;
			}
		}
		return null;
	}
	return this;
}

	BlobSolver.prototype.bestMove = function(moves){
		//("othermoves of best move " + moves.otherMoves);
		var bestscore = -50;
		var bestmove = new Move();
		var move = new Move();
		////("moves in bestmove = " + moves[0]);
	//	for each( move in moves ){
		for(var i = 0; i < moves.length; i++){
			move = moves[i];
		//	//("move = " + move.from.col);
			var score = move.getScore();
	//		//("score = " + score);
			////("score = " + score);
			if(score > bestscore){
				bestscore = score;
				bestmove = move;
			}
		}
		////("bestmove = " + bestmove.from.col);

		return bestmove;
	}
var BlobSolver = new BlobSolver();
var moveFirst = true; //true == this engine moves second
var board = BlobSolver.newBoard(moveFirst);
var moves = new Array()
var input = '';  //prompt("Make your move", "Type your number here");
if(moveFirst){
	input = WScript.StdIn.ReadLine();
}
//("prompt answer = " + input);

while(moveFirst || input!='exit'){
	if(moveFirst){
		var elems = input.split(",");
		var movf = new Pos(parseInt(elems[0]), parseInt(elems[1]));
		var movt = new Pos(parseInt(elems[2]), parseInt(elems[3]));
		//("movf = " + movf.col + " movt = " + movt.col);

		board = board.makeMove(movf, movt, 1, true);
		//("MOVES BEFORE SUBMOVE = " + moves);
		moves = BlobSolver.subMoves(moves, board);
		//("moves = " + moves);
		//("Board after Moving and if Statement: " + board.matrix);
	} else{
		moveFirst = true;
	}
	////("board2 = " + board.matrix[0]);
	//("the moves = " + moves);
	var me = new MoveExplorer(board, moves);
	me.start();

	//("move = " + me.moves[0]);

	
	moves = me.moves;
	////("moves = " + me.moves);
	for(var i = 0; i < moves.length; i++){
		//("BlobSolver.getTreeDepth(moves[0]) = "+BlobSolver.getTreeDepth(moves[i]));
	}
	var fm = BlobSolver.bestMove(moves);
	////("fm = " + fm.otherMoves);
	moves = fm.otherMoves;
	////("fm.other moves = " + moves);
	board = board.makeMove(fm.from, fm.to, 0, true);
	WScript.StdOut.WriteLine (fm.from.row + "," + fm.from.col + "," + fm.to.row + "," + fm.to.col);
	board.printBoard();
	////("Board after Moving: " + board.matrix);
	//input = prompt("Make your MOVE", "Type your number here");
	input = WScript.StdIn.ReadLine();
}

//n = parseInt(n);

