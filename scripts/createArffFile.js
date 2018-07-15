var fs = require('fs');
var path = require('path');
var _ = require('lodash');
var fft = require('jsfft');
var Promise = require('bluebird');

var DEFAULT_SEPARATOR = ',';
var DEFAULT_DATA_SIZE = 64;
var DEFAULT_ACTIVITIES = ['walking', 'running', 'sitting', 'standing', 'jumping', 'falling', 'others']

var ARFF_FILE = path.join(__dirname, 'training_file.arff');

function filterByMedian(array){

    let final_array = [];

    while(array.length > 0){
        let aux_array = [];

        if(array.length >= 10){
            aux_array = array.splice(0, 10);
        }else{
            aux_array = array.splice(0, array.length);
        }

        aux_array.sort(function(a,b) {return a - b;})

        var half = Math.floor(aux_array.length/2);

        if(aux_array.length % 2)
            final_array.push(parseFloat(aux_array[half]));
        else
            final_array.push(parseFloat((aux_array[half-1] + aux_array[half]) / 2.0));
    }

    return final_array;
}

function filterByMean(array){
    let final_array = [];

    while(array.length > 0){
        let aux_array = [];

        if(array.length >= 10){
            aux_array = array.splice(0, 10);
        }else{
            aux_array = array.splice(0, array.length);
        }

        if(aux_array.length > 0){
            let sum = aux_array.reduce(function(sum, val){ return sum + val }, 0);
            let mean = sum / aux_array.length;

            final_array.push(parseFloat(mean));
        }
        

    }

    return final_array;
}

function checkArffFile(){

    return new Promise(function(resolve, reject){

        if(!fs.existsSync(ARFF_FILE)){

            console.log('CREATING ARFF FILE!');

            let dataToWrite = '@RELATION falldetection\n' + 
                                '\n';

            for(let i = 1; i <= DEFAULT_DATA_SIZE; i++){
                dataToWrite += '@ATTRIBUTE accelerometer' + i + ' real\n';
            }

            dataToWrite += '@ATTRIBUTE accelerometermax real\n'
                    
            for(let i = 1; i <= DEFAULT_DATA_SIZE; i++){
                dataToWrite += '@ATTRIBUTE gyroscope' + i + ' real\n';
            }

            dataToWrite += '@ATTRIBUTE gyroscopemax real\n'

            dataToWrite += '@ATTRIBUTE activity {walking, running, sitting, standing, jumping, falling, others}\n';
            dataToWrite += '\n';
            dataToWrite += '@DATA\n';

            fs.writeFileSync(ARFF_FILE, dataToWrite);

            resolve();

        }else{
            console.log('ARFF FILE EXISTS!');
            resolve();
        }

    });

}

function buildArffFileFromTxt(file_path, activity){
    console.log('File PAth: ', file_path)

    return checkArffFile()
    .then(function(){

        console.log('START READING!!!!');

        let accelerometer_data = [];
        let gyroscope_data = [];

        fs.readFileSync(file_path).toString().split('\n').forEach(function(line){
            let arrayData = line.trim().split(DEFAULT_SEPARATOR);

            let aux_data;
            let val;
            
            aux_data = arrayData.splice(0, 3);
            if(aux_data.length == 3){
                val = Math.sqrt(Math.pow(parseInt(aux_data[0]),2) + Math.pow(parseInt(aux_data[1]),2) + Math.pow(parseInt(aux_data[2]),2));
                accelerometer_data.push(val);
            }

            aux_data = arrayData.splice(0, 3);
            if(aux_data.length == 3){
                val = Math.sqrt(Math.pow(parseInt(aux_data[0]),2) + Math.pow(parseInt(aux_data[1]),2) + Math.pow(parseInt(aux_data[2]),2));
                gyroscope_data.push(val);
            }
        });

        //Mean
        // accelerometer_data = filterByMean(accelerometer_data);
        // gyroscope_data = filterByMean(gyroscope_data);

        // subdivide in arrays of length 64

        if(accelerometer_data.length == gyroscope_data.length){

            let accelerometer_sub = [];
            let gyroscope_sub = [];

            while(accelerometer_data.length >= DEFAULT_DATA_SIZE && gyroscope_data.length >= DEFAULT_DATA_SIZE){

                let accelerometer_fft;
                let gyroscope_fft;

                let dataToWrite = '';
                let max = 0;

                if(accelerometer_data.length >= DEFAULT_DATA_SIZE){
                    accelerometer_sub = accelerometer_data.splice(0, DEFAULT_DATA_SIZE);
                }
                // else{
                //     accelerometer_sub = accelerometer_data.splice(0, accelerometer_data.length);
                // }

                if(accelerometer_sub.length > 0){
                    accelerometer_fft = fft.FFT(accelerometer_sub).real;

                    for(let i = 0; i < accelerometer_fft.length; i++){
                        dataToWrite += accelerometer_fft[i] + ','
                        if(accelerometer_fft[i] > max){
                            max = accelerometer_fft[i];
                        }
                    }

                    dataToWrite += max + ','
                }

                max = 0;
                
                if(gyroscope_data.length >= DEFAULT_DATA_SIZE){
                    gyroscope_sub = gyroscope_data.splice(0, DEFAULT_DATA_SIZE);
                }
                // else{
                //     gyroscope_sub = gyroscope_data.splice(0, gyroscope_data.length);
                // }

                if(gyroscope_sub.length > 0){
                    gyroscope_fft = fft.FFT(gyroscope_sub).real;

                    for(let i = 0; i < gyroscope_fft.length; i++){
                        dataToWrite += gyroscope_fft[i] + ','
                        if(gyroscope_fft[i] > max){
                            max = gyroscope_fft[i];
                        }
                    }

                    dataToWrite += max + ','
                }

                dataToWrite += activity + '\n';


                fs.appendFileSync(ARFF_FILE, dataToWrite);

            }

        }else{
            console.log('Something went Wrong!!!!');
        }

    }).catch(function(err){
        console.log('ERROR: ', err);
    });
    
    

}

function buildArffFileFromCSV(file_path){

}


var filesToRead = require('./filesToRead.2.json');

Promise.each(filesToRead, function(file){
    if(file.file.substring('.txt')){
        return buildArffFileFromTxt(path.join(__dirname, file.file), file.activity)
        .then(function(){
            console.log('FINISHED FILE: ' + file.file);
        });
    }else{
        console.log("No function match filename extension!!");
    }
});

// if (process.argv.length < 4) {
//     console.log('Usage: node scriptFile.js FILENAME ACTIVITY');
//     process.exit(1);
// }

// if(_.indexOf(DEFAULT_ACTIVITIES, process.argv[3]) == -1){
//     console.log('Invalid activity!!');
//     console.log('Activities: ' + DEFAULT_ACTIVITIES)
//     process.exit(1);
// }

// if(process.argv[2].substring('.txt')){
//     buildArffFileFromTxt(path.join(__dirname, process.argv[2]), process.argv[3])
//     .then(function(){
//         console.log('Finished!!');
//     });
// }else{
//     console.log("No function match filename extension!!");
// }

