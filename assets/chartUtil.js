	function getColumnChartView(data, vaxisIdx){
		var dt = new google.visualization.DataTable();
		var time = data.getValue(0, 5);  // 5 = time index
		
		if(time.charAt(5) == 'W'){
			dt.addColumn('string', 'Week');
		}else if(time.length <= 7){
			dt.addColumn('string', 'Month');
		}else if(time.length <= 10){
			dt.addColumn('string', 'Day');
		}else{
			dt.addColumn('string', 'Hour');
		}			 
		var names = data.getDistinctValues(1);  // 1 = name index
		var nameColIdxMap = {};
		for(var i=0; i<names.length; i++){
			var name = names[i];
			dt.addColumn('number', name);
			nameColIdxMap[name] = i+1; // the column idx would be i+1, since we've already added the first column (time).
		}
		
//		for (var i = 0, keys = Object.keys(nameColIdxMap), len = keys.length; i < len; i++) {
//    		console.log('key is: ' + keys[i] + ', value is: ' + nameColIdxMap[keys[i]]);
//		}
		 
		var prevTime = "";
		var row = -1;
		for(var r=0; r<data.getNumberOfRows(); r++){
			var time = data.getValue(r, 5);  // 5 = time index
			var name = data.getValue(r, 1);  // 1 = name index
			if(prevTime != time){
				row = row+1;
				dt.addRows(1);
				prevTime = time;
				dt.setValue(row, 0, time);
			}
			dt.setValue(row, nameColIdxMap[name], calcViewersOrDuration(data, r, vaxisIdx));
		}
		
		function calcViewersOrDuration(data, rownum, vaxisIdx){
			if(vaxisIdx == 3){  // 3 = viewers index
				return data.getValue(rownum, 3);  // 3 = viewers index
			}else{
				var duration = data.getValue(rownum, 4);  // 4 = duration index
				return Math.round(duration / 36000) / 100;
			}
		}
		
		return dt;		
	} 
	
	function printDataTable(data){
		console.log('Instantiated DataTable:: rows:'+data.getNumberOfRows()+' Cols:'+data.getNumberOfColumns());
		for (var i = 0; i<data.getNumberOfRows(); i++) {
			for(var j=0; j<data.getNumberOfColumns(); j++){
	    		console.log('row['+i+']='+data.getValue(i,j));
	    	}
		}
	}      
	