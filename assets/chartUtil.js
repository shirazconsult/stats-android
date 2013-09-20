function drawSimpleColumnChart(jsonData, options, eventtype, columnIdx){
		var data = createDataTable(jsonData);
//		var groupedData = groupDataOnViewersAndDuraion(data);
		
		// construct chartwrapper view
		var rowIdxs = data.getFilteredRows([{column: 0, value: eventtype}]);
		var row = new Array();
		var lut = new google.visualization.DataTable();
		lut.addColumn('string', '');
		row[0] = '';
		for(var i=0; i<rowIdxs.length; i++){
			var col = data.getValue(rowIdxs[i], 1);
			lut.addColumn('number', col);
			if(columnIdx == 4){
				// columnIdx == 4 for duration.
				row[i+1] = Math.round(data.getValue(rowIdxs[i], 4) / 36000) / 100;
			}else{  // columnIdx == 3 for viewers. 
				row[i+1] = data.getValue(rowIdxs[i], 3) / 1000;
			}
		}
		lut.addRow(row);
		
		var view = new google.visualization.DataView(lut);
		
		console.log('Processed Data.. Drawing ChartWrapper');
		var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));		
	    chart.draw(view, options);		
	}

function drawPieChart(jsonData, options, eventtype, columnIdx){
		var data = createDataTable(jsonData);
//		var groupedData = groupDataOnViewersAndDuraion(data);
		var view = new google.visualization.DataView(data);
		if(columnIdx == 4){
			view.setColumns([
				1,    
				{calc:toHoursAndMinutes, type:'number', title:'hours'}
			]);
		}else{
			view.setColumns([
				1,     
				{calc:toKilo, type:'number', title:'used'}
			]);
		}
		
		var rowIdxs = data.getFilteredRows([{column: 0, value: eventtype}]);
	    view.setRows(rowIdxs);	    
	
		console.log('Processed Data.. Drawing PieChart');
		var chart = new google.visualization.PieChart(document.getElementById('chart_div'));		
	    chart.draw(view, options);		
	}
	
      function drawColumnChart(jsonData, options, vaxisId){
		var data = createDataTable(jsonData);
		
		var processedData = getColumnChartView(data, vaxisId);  // 3 = viewers index, 4 = duration index
		console.log('Processed DataTable.. Drawing ColumnChart');
		var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
	    chart.draw(processedData, options);      
      }    

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
	
	// create a google chart datatable represented by the jsonData parameter.
	function createDataTable(jsonData){
		var data = new google.visualization.DataTable();
		
		var cols = jsonData.cols;
		var rows = jsonData.rows;
		for(var i=0; i<cols.length; i++){
			data.addColumn(cols[i]);
		}
		for(var i=0; i<rows.length; i++){
			data.addRow(rows[i]);
		}
		return data;
	}
	
	// Group data in a datatable by aggregating the duration (idx=4) and viewers (idx=3)
	function groupDataOnViewersAndDuraion(data){
		return google.visualization.data.group(
			data, 
			[0,  1],  
			[{
				'column': 4,   
				'aggregation': google.visualization.data.sum, 
				'type': 'number',
				'label': 'viewedMillis'
			},
			{
				'column': 3,   
				'aggregation': google.visualization.data.sum, 
				'type': 'number',
				'label': 'sum'
			}]
		);
	}
	
	
	// convert the aggregated duration column in groupped data (see groupDataOnViewersAndDuraion) to hours and minutes.
	function toHoursAndMinutesInGroupedData(dataTable, rowNum){
		return Math.round(dataTable.getValue(rowNum, 2) / 36000) / 100;
	}

	// convert the aggregated viewers column in groupped data (see groupDataOnViewersAndDuraion) to kilos
	function toKiloInGroupedData(dataTable, rowNum){
		return dataTable.getValue(rowNum, 3) / 1000;
	}

	// convert the aggregated duration column. 4 = column idx of duration
	function toHoursAndMinutes(dataTable, rowNum){
		return Math.round(dataTable.getValue(rowNum, 4) / 36000) / 100;
	}

	// convert the aggregated viewers column. 3 = column idx of viewers
	function toKilo(dataTable, rowNum){
		return dataTable.getValue(rowNum, 3) / 1000;
	}
	
	function printDataTable(data){
		console.log('Instantiated DataTable:: rows:'+data.getNumberOfRows()+' Cols:'+data.getNumberOfColumns());
		for (var i=0; i<data.getNumberOfRows(); i++) {
			for(var j=0; j<data.getNumberOfColumns(); j++){
	    		console.log('row['+i+']='+data.getValue(i,j));
	    	}
		}
	}      
	